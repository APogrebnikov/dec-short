package org.edec.order.service.impl;

import lombok.extern.log4j.Log4j;
import org.edec.newOrder.report.ReportService;
import org.edec.order.manager.EntityManagerOrderESO;
import org.edec.order.model.EmployeeOrderModel;
import org.edec.order.model.OrderModel;
import org.edec.order.service.OrderEnsembleService;
import org.edec.rest.manager.OrderRestDAO;
import org.edec.synchroMine.model.eso.entity.Order;
import org.edec.synchroMine.model.eso.entity.OrderStatusType;
import org.edec.utility.constants.OrderStatusConst;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.httpclient.manager.HttpClient;
import org.edec.utility.report.manager.OrderReportDAO;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.zkoss.zk.ui.Executions;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


@Log4j
public class OrderEnsembleServiceImpl implements OrderEnsembleService {

    private static final String POST_START_ENSEMBLE = "order.startBP";

    private EntityManagerOrderESO emManagerEso = new EntityManagerOrderESO();
    private ReportService jasperReportService = new ReportService();
    private FileManager fileManager = new FileManager();
    private OrderRestDAO orderRestDAO = new OrderRestDAO();
    private OrderReportDAO orderReportDAO = new OrderReportDAO();

    private Properties properties;

    public OrderEnsembleServiceImpl() {
        properties = new Properties();

        String propertyString = Executions.getCurrent().getDesktop().getWebApp().getRealPath("WEB-INF/properties/dataservice.properties");
        try {
            FileInputStream inputStream = new FileInputStream(propertyString);
            properties.load(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Order Ensemble service", e);
        }
    }

    @Override
    public boolean sendOrderToEnsemble(OrderModel order) {
        try {
            //Обновляем order
            order = emManagerEso.getOrderById(order.getIdOrder());
            OrderStatusConst orderStatus = OrderStatusConst.getOrderStatusConstByName(order.getStatus());

            switch (orderStatus) {
                case CREATED:
                case REVISION:
                    break;
                default:
                    return false;
            }

            Order orderEntity = orderRestDAO.getOrderById(order.getIdOrder());
            OrderStatusType orderStatusTypeEntity = orderRestDAO.getOrderStatusById(OrderStatusConst.APPROVAL.getId());
            orderEntity.setOrderStatusType(orderStatusTypeEntity);

            JasperReport jasperReport = jasperReportService.getJasperForOrder(order.getIdOrder());
            byte[] bytes = jasperReport.getFile();
            if (bytes == null) {
                log.warn("Order " + order.getIdOrder() + " jasper error");
                return false;
            }

            /*if (order.getUrl() != null && !order.getUrl().trim().equals("")) {
                File file = new File(orderEntity.getUrl());
                for (File tmpFile : file.listFiles()) {
                    if (tmpFile.isFile()) {
                        tmpFile.delete();
                    }
                }
            }*/

            String path = fileManager.createFileByRelativePath(order.getUrl(),
                    (order.getNumber() != null ? "Приказ " : "Проект ") + order.getIdOrder() +
                            " " + order.getDescription() + ".pdf", bytes
            );
            if (path == null) {
                log.warn("Order " + order.getIdOrder() + " file error");
                return false;
            }
            if (orderStatus == OrderStatusConst.REVISION) {
                orderRestDAO.updateOrder(orderEntity);
                return true;
            }
            JSONObject jsonData = new JSONObject();
            jsonData.put("subject", order.getDescription());
            jsonData.put("path", path);
            jsonData.put("orderid", order.getIdOrder());
            jsonData.put("type", "по личному составу студентов");
            jsonData.put("ordernumber", order.getNumber());
            jsonData.put("lotusid", order.getIdLotus());

            //Согласущие лица
            JSONArray jsonArrAgreement = new JSONArray();
            //Рассылка
            JSONArray jsonArrDistribution = new JSONArray();
            //Исполнители
            JSONArray jsonArrExecutor = new JSONArray();
            List<EmployeeOrderModel> employees = emManagerEso.getEmployeeForEnsemble(order.getIdOrder());
            if (employees.size() == 0) {
                log.warn("У приказа " + order.getIdOrder() + " нет лиц для рассылки/согласования/исполнения");
                return false;
            }
            int count = 0;
            for (EmployeeOrderModel employee : employees) {
                if (employee.getSubquery() != null && !employee.getSubquery().equals("") &&
                        !orderReportDAO.existsStudentsInOrderBySubquery(order.getIdOrder(), employee.getSubquery())) {
                    continue;
                }

                if (employee.getActionrule() == 3) {
                    jsonArrDistribution.put(new JSONObject()
                            .put("fio", employee.getFio())
                            .put("idhum", employee.getIdHum())
                            .put("email", employee.getEmail()));
                }

                if (employee.getActionrule() == 0) {
                    jsonArrDistribution.put(new JSONObject()
                            .put("fio", employee.getFio())
                            .put("idhum", employee.getIdHum())
                            .put("email", employee.getEmail()));
                    jsonData.put("senderEmail", employee.getEmail());
                    jsonData.put("senderFio", employee.getFio());
                }

                if (employee.getActionrule() == 1 || employee.getActionrule() == 2) {
                    jsonArrAgreement.put(new JSONObject()
                            .put("fio", employee.getFio())
                            .put("iter", ++count)
                            .put("idhum", employee.getIdHum())
                            .put("email", employee.getEmail())
                            .put("sign", (employee.getActionrule() == 1) ? 2 : employee.getSign() ? 1 : 0));
                }
            }

            jsonData.put("listHum", jsonArrAgreement);
            jsonData.put("listDistribution", jsonArrDistribution);
            jsonData.put("listExecutor", jsonArrExecutor);

            JSONObject jsonObject = new JSONObject(
                    HttpClient.makeHttpRequest(properties.getProperty(POST_START_ENSEMBLE), HttpClient.POST, new ArrayList<>(),
                            jsonData.toString()
                    ));
            return jsonObject.has("Status");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Order " + order.getIdOrder() + ", error: ", e);
            return false;
        }
    }
}
