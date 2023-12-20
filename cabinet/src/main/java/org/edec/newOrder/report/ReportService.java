package org.edec.newOrder.report;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.edec.newOrder.manager.OrderMainManagerESO;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.report.OrderReportMainModel;
import org.edec.newOrder.model.report.ProtocolComissionerModel;
import org.edec.newOrder.model.report.ProtocolModel;
import org.edec.newOrder.report.constant.OrderLineType;
import org.edec.newOrder.report.constant.OrderReportType;
import org.edec.newOrder.report.model.OrderLineModel;
import org.edec.newOrder.report.service.OrderReportFillService;
import org.edec.utility.DateUtility;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.doc.model.ParagraphData;
import org.edec.utility.doc.model.RunData;
import org.edec.utility.doc.service.DocService;
import org.edec.utility.fileManager.FileModel;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.model.jasperReport.ReportSrc;
import org.zkoss.zk.ui.Executions;

import javax.servlet.ServletContext;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {
    private ServletContext servletContext;
    private OrderMainManagerESO mainManagerESO = new OrderMainManagerESO();
    private OrderReportFillService orderReportFillService;

    public ReportService() {
        this.orderReportFillService = new OrderReportFillService();
    }

    public ReportService(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.orderReportFillService = new OrderReportFillService(servletContext);
        this.orderReportFillService.setOriginal(true);
    }

    public JasperReport getJasperForOrder(Long idOrder) {
        OrderEditModel order = mainManagerESO.getOrderById(idOrder);
        if (order != null) {
            return getJasperForOrder(order.getIdOrderRule(), order.getIdOrder());
        } else {
            return null;
        }
    }

    public JasperReport getJasperForOrder(Long idOrderRule, Long idOrder) {
        JRBeanCollectionDataSource data;
        Map<String, Object> arg = new HashMap<>();
        OrderReportType type;

        switch (OrderRuleConst.getById(idOrderRule)) {
            case ACADEMIC_IN_SESSION:
            case ACADEMIC_NOT_IN_SESSION:
            case ACADEMIC_FIRST_COURSE:
                type = OrderReportType.SEPARATE_BY_PAGES;
                String path = getRealPath("orders/") + File.separator;
                arg.put("realPath", path);
                break;
            default:
                type = OrderReportType.CONTINUOUS;
                break;
        }

        data = orderReportFillService.getBeanData(idOrder, idOrderRule, null);

        arg.put("type", type.getValue());
        arg.put("idOrder", idOrder);
        arg.put("idOrderRule", idOrderRule);

        return new JasperReport("Приказ", getRealPath("") + "/orders/OrderNew.jasper", arg, data, null, null, servletContext);
    }

    public JasperReport getJasperReportForMaterialSupportProtocol(OrderEditModel order, Date dateOfBegin, Date dateOfEnd, Date dateProtocol,
                                                                  String protocolNumber,
                                                                  List<ProtocolComissionerModel> protocolComissionerModels,
                                                                  List<OrderCreateStudentModel> refusalStudents) {
        if (refusalStudents == null) {
            refusalStudents = new ArrayList<>();
        }
        OrderReportModelService orderReportService = new OrderReportModelService();
        ReportSrc reportSrc = ReportSrc.PROTOCOL_MATERIAL_SUPPORT;
        Map arg = new HashMap();
        FileModel fileModel = null;
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        JRBeanCollectionDataSource data = orderReportService.getBeanDataForMaterialSupportProtocol(order.getIdOrder(), dateFormat.format(dateOfBegin), dateFormat.format(dateOfEnd));
        List<ProtocolModel> listDate = (List<ProtocolModel>) data.getData();


        listDate.get(0).setDateOfBegin(dateFormat.format(dateOfBegin));
        listDate.get(0).setDateOfEnd(dateFormat.format(dateOfEnd));
        listDate.get(0).setDateProtocol(dateFormat.format(dateProtocol));
        listDate.get(0).setProtocolNumber(protocolNumber);

        listDate.get(0).setRefusalStudentList(refusalStudents);
        // Доп. распределение комиссии Ищем особенных и удаляем их из общего списка - возвращая только ФИО
        listDate.get(0).setPredFIO(ProtocolComissionerModel.getFIOByRole(protocolComissionerModels, "Председатель комиссии"));
        listDate.get(0).setProfFIO(ProtocolComissionerModel.getFIOByRole(protocolComissionerModels, "Профсоюзная организация"));
        listDate.get(0).setSovetFIO(ProtocolComissionerModel.getFIOByRole(protocolComissionerModels, "Совет обучающихся"));

        listDate.get(0).setComissionerList(protocolComissionerModels);

        listDate.get(0).setTotalStudentList(listDate.get(0).genTotalStudentList());

        data = new JRBeanCollectionDataSource(listDate);

        JasperReport jasperReport = new JasperReport("", getRealPath("") + reportSrc.getLocalPath(), arg, data, fileModel, null,
                servletContext
        );

        return jasperReport;
    }

    public JasperReport getJasperReportForServiceNote(OrderEditModel order, Date dateNotion, String sem) {
        OrderReportModelService orderReportService = new OrderReportModelService();
        ReportSrc reportSrc = ReportSrc.SERVICE_NOTE;
        Map arg = new HashMap();
        FileModel fileModel = null;
        JRBeanCollectionDataSource data = orderReportService
                .getBeanDataForServiceNote(order.getIdOrder(), getDescriptionForServiceNote(order.getIdOrderRule()));

        List<OrderReportMainModel> listDate = (List<OrderReportMainModel>) data.getData();
        listDate.get(0).setDateNote(new SimpleDateFormat("dd.MM.yyyy").format(dateNotion));
        listDate.get(0).setSemesters("по итогам промежуточной аттестации (" + sem + " уч. г.)");
        listDate.get(0).setDescriptiontitle(getNameForServiceNote(order.getIdOrderRule()));

        data = new JRBeanCollectionDataSource(listDate);
        JasperReport jasperReport = new JasperReport("", getRealPath("") + reportSrc.getLocalPath(), arg, data, fileModel, null,
                servletContext
        );

        return jasperReport;
    }

    private String getDescriptionForServiceNote(Long idRule) {
        if (OrderRuleConst.getById(idRule).equals(OrderRuleConst.TRANSFER_PROLONGATION)) {
            return "Прошу продлить срок ликвидации академических задолженностей следующим студентам," + " обучающимся " +
                    OrderReportModelService.FORM_OF_STUDY + ", условно переведенным на следующий курс и не прошедшим" +
                    " промежуточную аттестацию, до " + OrderReportModelService.FIRST_DATE + ":";
        }

        if (OrderRuleConst.getById(idRule).equals(OrderRuleConst.PROLONGATION_ELIMINATION_WINTER)) {
            return "Прошу продлить срок ликвидации академических задолженностей следующим студентам," + " обучающимся " +
                    OrderReportModelService.FORM_OF_STUDY + ", не прошедшим" + " промежуточную аттестацию, до " +
                    OrderReportModelService.FIRST_DATE + ":";
        }

        if (OrderRuleConst.getById(idRule).equals(OrderRuleConst.SET_ELIMINATION_AFTER_TRANSFER_RESPECTFUL)) {
            return "Прошу установить срок повторной промежуточной аттестации до " + OrderReportModelService.FIRST_DATE +
                    " г. для ликвидации" + " академической задолженности обучающимися " + OrderReportModelService.FORM_OF_STUDY +
                    ", очной формы обучения, которые ранее были условно переведены на" +
                    " следующий курс с установлением индивидуальных сроков промежуточной аттестации по" +
                    " уважительной причине и не прошли ее:";
        }

        if (OrderRuleConst.getById(idRule).equals(OrderRuleConst.SET_ELIMINATION_AFTER_TRANSFER_NOT_RESPECTFUL)) {
            return "Прошу установить срок повторной промежуточной аттестации для ликвидации академических " + "задолженностей до " +
                    OrderReportModelService.FIRST_DATE + " г. обучающимся " + OrderReportModelService.FORM_OF_STUDY +
                    " очной формы обучения, не прошедшим промежуточную аттестацию " +
                    "по неуважительной причине или получившим неудовлетворительные результаты:";
        }

        if (OrderRuleConst.getById(idRule) == OrderRuleConst.SET_SECOND_ELIMINATION) {
            return "Прошу установить срок второй повторной промежуточной аттестации для ликвидации " + "академических задолженностей до " +
                    OrderReportModelService.FIRST_DATE + " г. обучающимся, не прошедшим промежуточную " +
                    "аттестацию по неуважительной причине и получившим неудовлетворительные результаты";
        }

        return "";
    }

    //TODO same as header of order rule, refactor
    private String getNameForServiceNote(Long idRule) {
        if (OrderRuleConst.getById(idRule).equals(OrderRuleConst.SET_ELIMINATION_AFTER_TRANSFER_RESPECTFUL)) {
            return "Об установлении сроков прохождения повторной <br>промежуточной аттестации, ранее условно переведенным<br>" +
                    "(с установлением индивидуальных сроков прохождения промежуточной аттестации по уважительной причине)";
        }

        if (OrderRuleConst.getById(idRule).equals(OrderRuleConst.SET_ELIMINATION_AFTER_TRANSFER_NOT_RESPECTFUL)) {
            return "Об установлении сроков повторной промежуточной<br>" + "аттестации";
        }

        return "";
    }

    private String getRealPath(String relativePath) {
        if (Executions.getCurrent() != null) {
            return Executions.getCurrent().getDesktop().getWebApp().getRealPath("WEB-INF/reports/" + relativePath);
        } else if (servletContext != null) {
            return servletContext.getRealPath("WEB-INF/reports/" + relativePath);
        }

        return "";
    }

    public Map<String, Object> getParamsFromNotionRector(OrderEditModel order) {
        Map<String, Object> data = new HashMap<>();

        data.put("sem", "весеннего семестра " + DateUtility.getPreviousYear() + " - " +
                DateUtility.getCurrentYear());
        data.put("date", DateConverter.convertDateToString(new Date(), ""));


        List<StudentModel> students = order.getAllStudents();

        if (!students.isEmpty())
            data.put("first_date", DateConverter.convertDateToString(students.get(0).getFirstDate()));

        List<ParagraphData> studentsParagraphList = new ArrayList<>();
        int number = 1;
        for (StudentModel student : students) {
            ParagraphData paragraphData = new ParagraphData();

            paragraphData.setIndentLevel(OrderLineType.RED_LINE_INDENT.getValue());

            paragraphData.setRunDataList(
                    Collections.singletonList(RunData.builder()
                            .text(number++ + ".   (# "
                                    + student.getRecordnumber() + ") " + student.getFio()).build()));

            studentsParagraphList.add(paragraphData);
        }

        data.put("content", studentsParagraphList);

        return data;
    }
}
