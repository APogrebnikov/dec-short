package org.edec.rest.ctrl;

import lombok.extern.log4j.Log4j;
import org.edec.newOrder.manager.OrderMainManagerESO;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.report.ReportService;
import org.edec.rest.manager.OrderRestDAO;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.constants.OrderStatusConst;
import org.edec.utility.constants.OrderTypeConst;
import org.edec.utility.email.Sender;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/order")
@Log4j
public class OrderRestCtrl {

    @Context
    private ServletContext servletContext;

    private OrderRestDAO orderRestDAO = new OrderRestDAO();
    private OrderMainManagerESO orderMainManagerESO = new OrderMainManagerESO();

    @PUT
    @Path("/update/document")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=utf-8")
    public String getPath(@FormParam("idOrder") Long id_order,
                          @FormParam("ordernumber") String ordernumber,
                          @FormParam("lotusid") String lotusid) {
        log.info("Обновление приказа(" + id_order + ") с номером(" + ordernumber + ") и лотусид(" + lotusid + ")");
        if (!orderRestDAO.updateOrderAfterLouts(id_order, ordernumber, lotusid)) {
            return new JSONObject().put("path", "").toString();
        }

        OrderEditModel order = orderMainManagerESO.getOrderById(id_order);
        ReportService jasperReportService = new ReportService(servletContext);
        JasperReport jasperReport = jasperReportService.getJasperForOrder(id_order);
        byte[] bytes = jasperReport.getFile();
        if (bytes == null) {
            return new JSONObject().put("path", "").toString();
        }

        FileManager fileManager = new FileManager(servletContext);

        fileManager.signedFile(order.getUrl());
        String path = fileManager
                .createFileByRelativePath(order.getUrl(), "Приказ " + order.getIdOrder() + " " + order.getDescription() + ".pdf", bytes);

        if (path != null && !path.equals("")) {
            log.info("Обновление приказа(" + id_order + ") с номером(" + ordernumber + ") и лотусид(" + lotusid + ") прошло успешно");
            return new JSONObject().put("path", path).toString();
        }

        return new JSONObject().put("path", "").toString();
    }

    @PUT
    @Path("/update/status")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=utf-8")
    public String getStatus(@FormParam("idOrder") Long id_order,
                            @FormParam("status") String status,
                            @FormParam("idHum") Long idHum,
                            @FormParam("signFio") String fio,
                            @FormParam("certNumber") String certNumber,
                            @FormParam("operation") String operation) {
        log.info("Обновление статуса приказа(" + id_order + ") со статусом(" + status + ")");
        Long statusId = OrderStatusConst.getOrderStatusConstByName(status).getId();
        if (orderRestDAO.updateOrderStatus(id_order, statusId, idHum, fio, certNumber, operation)) {
            //Если приказ подписан, то ставим статусы
            if (OrderStatusConst.AGREED == OrderStatusConst.getOrderStatusConstByName(status)) {
                return getStatus(id_order);
            }
        }

        return null;
    }

    @GET
    @Path("/update/statusManual")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=utf-8")
    public String getStatus(@QueryParam("idOrder") Long id_order) {
        //Если приказ подписан, то ставим статусы
        OrderEditModel order = orderMainManagerESO.getOrderById(id_order);
        switch (OrderTypeConst.getByType(order.getOrderType())) {
            case DEDUCTION:
                orderRestDAO.updateSSSdeduction(id_order);
                break;
            case ACADEMIC:
                if (OrderRuleConst.CANCEL_ACADEMICAL_SCHOLARSHIP_IN_SESSION == OrderRuleConst.getById(order.getIdOrderRule()) ||
                    OrderRuleConst.CANCEL_SCHOLARSHIP_AFTER_PRACTICE == OrderRuleConst.getById(order.getIdOrderRule())) {
                    orderRestDAO.updateSSScancelAcademic(id_order);
                } else {
                    orderRestDAO.updateSSSacademic(id_order);
                }
                break;
            case ACADEMIC_INCREASED:
                orderRestDAO.updateSSSacademicIncreased(id_order);
                orderRestDAO.deleteOrderInfo("academicIncreasedOrder", id_order);
                break;
            case SOCIAL:
                orderRestDAO.updateSSSsocial(id_order);
                break;
            case SOCIAL_INCREASED:
                orderRestDAO.updateSSSsocialIncreased(id_order);
                break;
            case TRANSFER:
                if (OrderRuleConst.TRANSFER_CONDITIONALLY == OrderRuleConst.getById(order.getIdOrderRule())){
                    orderRestDAO.updateTransferConditionally(id_order);
                }
                break;
            case SET_ELIMINATION_DEBTS:
                if(OrderRuleConst.SET_FIRST_ELIMINATION == OrderRuleConst.getById(order.getIdOrderRule())){
                    orderRestDAO.updateEliminationDate(id_order);
                }
                break;
        }
        return null;
    }

    @POST
    @Path("/send/email")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=utf-8")
    public String sendEmail(@FormParam("destination") String destination,
                            @FormParam("subject") String subject,
                            @FormParam("message") String message) {
        try {
            // ЗАГЛУШКА ИЗ-ЗА ОТКАЗА ОТ ЛОТУСА
            if (destination.toLowerCase().equals("spangof@sfu-kras.ru")) {
                return new JSONObject().put("STATUS", "SUCCESS").toString();
            }

            Sender sender = new Sender(servletContext);
            sender.sendSimpleMessage(destination, subject, message);

            log.info("Было отправлено email уведомление на почту " + destination +
                     " c темой " + subject + " и текстом '" + message + "'");

            //костыль для оповещения секретаря
            if(destination.equals("dguts@sfu-kras.ru")) {
                sender.sendSimpleMessage("edu@sfu-kras.ru", subject, message);
                sender.sendSimpleMessage("julia_ww@mail.ru", subject, message);
                sender.sendSimpleMessage("yweinstein@sfu-kras.ru", subject, message);
            }
        } catch (Exception e) {
            log.error("Не удалось отправить email уведомление на почту " + destination +
                     " c темой " + subject + " и текстом '" + message + "'");
            e.printStackTrace();
        }

        return new JSONObject().put("STATUS", "SUCCESS").toString();
    }

    @GET
    @Path("/status")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=utf-8")
    public String checkStatsu(@QueryParam("id_order") Long id_order) {
        log.info("Проверка статуса приказа(" + id_order + ") с отмененного:");
        OrderEditModel order = orderMainManagerESO.getOrderById(id_order);
        if (order == null) {
            return new JSONObject().put("Status", 4).toString();
        }
        Long statusId = OrderStatusConst.getOrderStatusConstByName(order.getStatus()).getId();
        return new JSONObject().put("Status", statusId).toString();
    }

    @GET
    @Path("/ordersign")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=utf-8")
    public String createOrderFile(@QueryParam("id_order") Long idOrder) {
        try {
            OrderEditModel order = orderMainManagerESO.getOrderById(idOrder);
            ReportService jasperReportService = new ReportService(servletContext);

            FileManager fm = new FileManager(servletContext);

            if (!fm.signedFile(order.getUrl())) {
                return new JSONObject().put("result", 0).toString();
            }

            String path = fm
                    .createFileByRelativePath(order.getUrl(), "Приказ " + order.getIdOrder() + " " + order.getDescription() + ".pdf",
                                              jasperReportService.getJasperForOrder(idOrder).getFile()
                    );

            if (path == null) {
                return new JSONObject().put("result", 0).toString();
            }

            return new JSONObject().put("result", 1).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().put("result", 0).toString();
        }
    }
}