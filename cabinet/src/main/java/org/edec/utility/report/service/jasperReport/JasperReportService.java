package org.edec.utility.report.service.jasperReport;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.edec.commission.report.model.NotionMainModel;
import org.edec.commission.report.model.notion.NotionStudentModel;
import org.edec.commission.report.model.schedule.ScheduleChairModel;
import org.edec.contingentMovement.model.ResitRatingModel;
import org.edec.contingentMovement.report.sevice.ResitMarkService;
import org.edec.contingentMovement.report.sevice.impl.ResitMarkImpl;
import org.edec.newOrder.model.report.ProtocolComissionerModel;
import org.edec.newOrder.model.report.ProtocolModel;
import org.edec.passportGroup.manager.PassportGroupManager;
import org.edec.passportGroup.model.RegisterCurProgressGroupModel;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.passportGroup.service.impl.PassportGroupServiceESO;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.contingentMovement.report.model.ProtocolCommissionModel;
import org.edec.factSheet.model.FactSheetTableModel;
import org.edec.register.model.report.RegisterDateModel;
import org.edec.register.service.dao.RegisterReportServiceImpl;
import org.edec.teacher.model.GroupModel;
import org.edec.teacher.model.correctRequest.CorrectRequestModel;
import org.edec.teacher.model.register.RegisterModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.constants.RegisterType;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.report.model.order.MainOrderModel;
import org.edec.order.model.OrderModel;
import org.edec.teacher.ctrl.WinCommissionCtrl;
import org.edec.teacher.model.commission.CommissionModel;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.constants.OrderTypeConst;
import org.edec.utility.fileManager.FileModel;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.model.jasperReport.ReportSrc;
import org.edec.utility.report.service.commission.CommissionReportService;
import org.edec.utility.report.service.order.OrderReportService;
import org.edec.utility.report.service.order.impl.*;
import org.edec.utility.report.service.register.CorrectRatingService;
import org.edec.utility.report.service.register.RegisterReportService;
import org.edec.utility.sign.service.SignService;
import org.edec.utility.sign.service.impl.SignCorrectRatingImpl;
import org.edec.utility.sign.service.impl.SignRegisterCommissionImpl;
import org.edec.utility.sign.service.impl.SignRegisterImpl;
import org.zkoss.zk.ui.Executions;

import javax.servlet.ServletContext;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class JasperReportService {
    private ServletContext servletContext;

    public JasperReportService() {
    }

    public JasperReportService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public JasperReport getJasperForOrder(OrderModel order) {
        OrderReportService orderReportService = null;
        ReportSrc reportSrc = null;
        Map<String, Object> arg = new HashMap<>();
        FileModel fileModel = null;
        if (OrderTypeConst.getByType(order.getOrderType()) == OrderTypeConst.ACADEMIC) {
            orderReportService = new OrderReportAcademicImpl();
            reportSrc = ReportSrc.ORDER_ACADEMIC;
            String path = getRealPath("orders/") + File.separator;
            arg.put("type", ReportSrc.ORDER_ACADEMIC.getType());
            arg.put("realPath", path);
        } else if (OrderTypeConst.getByType(order.getOrderType()) == OrderTypeConst.DEDUCTION) {
            orderReportService = new OrderReportDeductionImpl();
            reportSrc = ReportSrc.ORDER_INDIVIDUAL_DEDUCTION;
            arg.put("type", ReportSrc.ORDER_INDIVIDUAL_DEDUCTION.getType());
        } else if (OrderTypeConst.getByType(order.getOrderType()) == OrderTypeConst.SOCIAL) {
            orderReportService = new OrderReportSocialImpl();
            reportSrc = ReportSrc.ORDER_SOCIAL;
            arg.put("type", ReportSrc.ORDER_SOCIAL.getType());
        } else if (OrderTypeConst.getByType(order.getOrderType()) == OrderTypeConst.SOCIAL_INCREASED) {
            orderReportService = new OrderReportSocialIncreaseImpl();
            reportSrc = ReportSrc.ORDER_SOCIAL_INCREASE;
            arg.put("type", ReportSrc.ORDER_SOCIAL_INCREASE.getType());
        } else if (OrderTypeConst.getByType(order.getOrderType()) == OrderTypeConst.TRANSFER) {
            orderReportService = new OrderReportTansferImpl();
            if (order.getIdOrderRule().equals(OrderRuleConst.TRANSFER_AFTER_TRANSFER_CONDITIONALLY_RESPECTFUL.getId()) ||
                order.getIdOrderRule().equals(OrderRuleConst.TRANSFER_AFTER_TRANSFER_CONDITIONALLY_NOT_RESPECTFUL.getId()) ||
                order.getIdOrderRule().equals(OrderRuleConst.TRANSFER_PROLONGATION.getId())) {
                reportSrc = ReportSrc.ORDER_TRANSFER_AFTER_TRANSFER;
            } else {
                reportSrc = ReportSrc.ORDER_TRANSFER;
            }
            arg.put("type", reportSrc.getType());
        } else if (OrderTypeConst.getByType(order.getOrderType()) == OrderTypeConst.SET_ELIMINATION_DEBTS) {
            orderReportService = new OrderReportSetEliminationImpl(OrderRuleConst.getById(order.getIdOrderRule()));
            if (order.getIdOrderRule().equals(OrderRuleConst.SET_ELIMINATION_RESPECTFUL.getId())) {
                reportSrc = ReportSrc.ORDER_SET_ELIMINATION_RESPECTFUL;
            } else {
                reportSrc = ReportSrc.ORDER_SET_ELIMINATION;
            }
            arg.put("type", reportSrc.getType());
        }

        return new JasperReport("Приказ", getRealPath("") + reportSrc.getLocalPath(), arg,
                                orderReportService.getBeanData(order.getIdOrder()), fileModel, null, servletContext
        );
    }

    public JasperReport getJasperReportForServiceNote(OrderModel order, Date dateNotion, String sem) {
        OrderReportTansferImpl orderReportService = new OrderReportTansferImpl();
        ReportSrc reportSrc = ReportSrc.SERVICE_NOTE;
        Map arg = new HashMap();
        FileModel fileModel = null;
        JRBeanCollectionDataSource data = orderReportService.getBeanDataForServiceNote(order.getIdOrder());

        List<MainOrderModel> listDate = (List<MainOrderModel>) data.getData();
        listDate.get(0).setDateNote(new SimpleDateFormat("dd.MM.yyyy").format(dateNotion));
        listDate.get(0).setSemesters("по итогам промежуточной аттестации (весеннего семестра " + sem + " уч. г.)");

        data = new JRBeanCollectionDataSource(listDate);
        return new JasperReport("", getRealPath("") + reportSrc.getLocalPath(), arg, data, fileModel, null, servletContext);
    }

    public JasperReport getJasperReportForSetEliminationNotion(OrderModel order, Date dateNotion) {
        OrderReportSetEliminationImpl orderReportService = new OrderReportSetEliminationImpl(
                OrderRuleConst.getById(order.getIdOrderRule()));
        ReportSrc reportSrc = ReportSrc.SERVICE_NOTE;
        Map arg = new HashMap();
        FileModel fileModel = null;
        JRBeanCollectionDataSource data = orderReportService.getBeanDataForEliminationNote(order.getIdOrder());

        List<MainOrderModel> listDate = (List<MainOrderModel>) data.getData();
        listDate.get(0).setDateNote(new SimpleDateFormat("dd.MM.yyyy").format(dateNotion));

        data = new JRBeanCollectionDataSource(listDate);
        return new JasperReport("", getRealPath("") + reportSrc.getLocalPath(), arg, data, fileModel, null, servletContext);
    }

    public JasperReport getJasperReportProtocolCommission(Long idRegister) {
        String path = getRealPath("/commission/protocol/") + File.separator;
        Map<String, Object> arg = new HashMap<>();
        arg.put("realPath", path);
        return new JasperReport("Протокол", getRealPath("") + ReportSrc.COMM_PROTOCOL.getLocalPath(), arg,
                                new CommissionReportService().getListProtocol(idRegister), null
        );
    }

    public JasperReport getJasperReportCommRegister(WinCommissionCtrl winCommissionCtrl, CommissionModel commission) {
        FileModel fileModel = new FileModel(FileModel.Inst.getInstByShortName(commission.getInstitute()), FileModel.TypeDocument.REGISTER,
                                            FileModel.SubTypeDocument.COMMISSION_RETAKE, commission.getIdSem(),
                                            commission.getIdReg().toString()
        );
        fileModel.setFormat("pdf");
        SignService signService = null;
        if (winCommissionCtrl != null) {
            signService = new SignRegisterCommissionImpl(fileModel, winCommissionCtrl, commission);
        }
        return new JasperReport("Ведомость", getRealPath("") + ReportSrc.COMM_REGISTER.getLocalPath(), new HashMap(),
                                new CommissionReportService().getRegister(commission), signService
        );
    }

    public JasperReport getContingentIndCurr(String fio, String shortFio, String code, String group, Date datePass,
                                             List<ResitRatingModel> subjects) {
        Map<String, Object> arg = new HashMap<>();
        arg.put("studentFio", fio);
        arg.put("studentShortFio", shortFio);
        arg.put("code", code);
        arg.put("group", group);
        arg.put("datePass", datePass);
        return new JasperReport("Индивидуальный учебный план " + shortFio,
                                getRealPath("") + ReportSrc.CONTINGENT_INDIVIDUAL_CURR.getLocalPath(), arg,
                                new JRBeanCollectionDataSource(subjects), null
        );
    }

    public JasperReport getContingentIndCurr2019(String fio, String shortFio, String code, String group, Date datePass, int course, int semester,
                                             List<ResitRatingModel> subjects) {
        Map<String, Object> arg = new HashMap<>();
        arg.put("studentFio", fio);
        arg.put("studentShortFio", shortFio);
        arg.put("code", code);
        arg.put("group", group);
        arg.put("semester", semester);
        arg.put("course", course);
        arg.put("datePass", datePass);
        return new JasperReport("Индивидуальный учебный план " + shortFio,
                getRealPath("") + ReportSrc.CONTINGENT_INDIVIDUAL_CURR_2019.getLocalPath(), arg,
                new JRBeanCollectionDataSource(subjects), null
        );
    }

    public JasperReport getContingentIndCurrNoBudget(String fio, String shortFio, String code, String group, Date datePass,
                                                     List<ResitRatingModel> subjects) {
        Map<String, Object> arg = new HashMap<>();
        arg.put("studentFio", fio);
        arg.put("studentShortFio", shortFio);
        arg.put("code", code);
        arg.put("group", group);
        arg.put("datePass", datePass);
        return new JasperReport("Индивидуальный учебный план " + shortFio,
                                getRealPath("") + ReportSrc.CONTINGENT_INDIVIDUAL_CURR_NO_BUDGET.getLocalPath(), arg,
                                new JRBeanCollectionDataSource(subjects), null
        );
    }

    public JasperReport getContingentIndCurrNoBudget2019(String fio, String shortFio, String code, String group, Date datePass, int course, int semester,
                                                     List<ResitRatingModel> subjects) {
        Map<String, Object> arg = new HashMap<>();
        arg.put("studentFio", fio);
        arg.put("studentShortFio", shortFio);
        arg.put("code", code);
        arg.put("group", group);
        arg.put("semester", semester);
        arg.put("course", course);
        arg.put("datePass", datePass);
        return new JasperReport("Индивидуальный учебный план " + shortFio,
                getRealPath("") + ReportSrc.CONTINGENT_INDIVIDUAL_CURR_NO_BUDGET_2019.getLocalPath(), arg,
                new JRBeanCollectionDataSource(subjects), null
        );
    }


    /*public JasperReport getJasperReportRegister(WinRegisterCtrl win, Long idRegister, Long idInstitute, Long idHumanface, Long idSemester, Integer typeRegister)
    {

        JasperReport jasperReport = new JasperReport("Ведомость", getRealPath("")+ReportSrc.REGISTER.getLocalPath(), new HashMap(),
                new RegisterReportService().getBeanData(idRegister, idHumanface),
                typeRegister < 0 ? new SignRegisterImpl(win, idRegister, idInstitute,  idSemester, typeRegister) : null);
        return jasperReport;
    }*/

    /**
     * @param formOfStudy - форма обучения
     * @param idChair - может быть null, если нужно показать все кафедры
     */
    public JasperReport getJasperRepotCommissionSchedule(Integer formOfStudy, Long idChair, Boolean printGroup, Boolean printMembersComm) {
        return new JasperReport("Расписание комиссий на '" + DateConverter.convertDateToString(new Date()) + "'",
                                getRealPath("") + ReportSrc.COMM_SCHEDULE.getLocalPath(), new HashMap(),
                                new CommissionReportService().getSchedule(formOfStudy, idChair, printGroup, printMembersComm), null
        );
    }

    public JasperReport getJasperReportRegister(Runnable updateRegisterUI, Long idRegister, Long idInstitute, Long idHumanface,
                                                Long idSemester, Integer foc, Integer typeRegister, RegisterType registerType,
                                                boolean canBeSigned) {

        return new JasperReport("Ведомость", getRealPath("") + ReportSrc.REGISTER.getLocalPath(), new HashMap(),
                                new RegisterReportService().getBeanData(idRegister, idHumanface, idInstitute, idSemester),
                                canBeSigned && typeRegister < 0 ? new SignRegisterImpl(updateRegisterUI, idRegister, idInstitute,
                                                                                       idSemester, registerType, foc
                                ) : null
        );
    }

    public JasperReport getJasperReportCorrectRating(CorrectRequestModel correctRequestModel) {

        return new JasperReport("Служебная записка", getRealPath("") + ReportSrc.CORRECT_RATING.getLocalPath(), new HashMap(),
                                new CorrectRatingService().getBeanData(correctRequestModel),
                                correctRequestModel.getCertnumber() == null ? new SignCorrectRatingImpl(correctRequestModel, 1L, 56L) : null
        );
    }

    public JasperReport getJasperReportRegister(Long idRegister, Long idSem, Long idInst, int formOfStudy) {

        return new JasperReport("Ведомость", getRealPath("") + ReportSrc.REGISTER.getLocalPath(), new HashMap(),
                                new RegisterReportService().getBeanData(idRegister, null, idInst, idSem), null
        );
    }

    public JasperReport getJasperReportRegister(Long idRegister, Long idSem, Long idInst, int formOfStudy, Boolean isDeducted) {

        return new JasperReport("Ведомость", getRealPath("") + ReportSrc.REGISTER.getLocalPath(), new HashMap(),
                new RegisterReportService().getBeanData(idRegister, null, idInst, idSem, isDeducted), null
        );
    }


    public JasperReport getJasperReportRegisterWithoutMarks(int formOfControl, GroupModel curGroup, RegisterModel register) {
        RegisterReportService registerReportService = new RegisterReportService();
        ReportSrc reportSrc = ReportSrc.REGISTER_WITHOUT_MARKS;
        Map arg = new HashMap();
        JRBeanCollectionDataSource data = registerReportService.getBeanDataWithoutMarks(formOfControl, curGroup, register);

        return new JasperReport("", getRealPath("") + reportSrc.getLocalPath(), arg, data, null, null, servletContext);
    }

    public JasperReport getFactSheetByModel(Date dateFrom, Date dateTo, String shortFio, List<FactSheetTableModel> models) {
        Map<String, Object> arg = new HashMap<>();
        arg.put("shortFio", shortFio);
        arg.put("dateTo", dateTo);
        arg.put("dateFrom", dateFrom);
        return new JasperReport(
                "Список справок", //Название файла
                getRealPath("") + ReportSrc.FACT_SHEET.getLocalPath(), //Путь до файла
                arg,//передаваемые параметры
                new JRBeanCollectionDataSource(models), //Сервис
                null
        );
    }

    public JasperReport getIndCurrProtocol(ProtocolCommissionModel protocol) {
        Map<String, Object> arg = new HashMap<>();
        arg.put("typeProtocol", protocol.getTypeProtocol());
        return new JasperReport("Протокол заседания аттестационной комиссии (" + protocol.getFioStudent() + ")",
                                getRealPath("") + ReportSrc.CONTINGENT_PROTOCOL_COMMISSION.getLocalPath(), arg,
                                new JRBeanCollectionDataSource(Collections.singletonList(protocol)), null
        );
    }

    public JasperReport getScheduleJasper(JRBeanCollectionDataSource jrBeanCollectionDataSource) {
        return new JasperReport("Расписание комиссий", getRealPath("") + ReportSrc.COMMISSION_SCHEDULE.getLocalPath(), new HashMap(),
                                jrBeanCollectionDataSource, null
        );
    }

    public JasperReport getListOfStudentByChair(List<ScheduleChairModel> filteredStudentsByChar) {
        return new JasperReport("Список студентов (по кафедре)", getRealPath("") + ReportSrc.LIST_STUDENT_BY_CHAIR.getLocalPath(),
                                new HashMap(), new JRBeanCollectionDataSource(filteredStudentsByChar), null
        );
    }

    public JasperReport getScheduleByFormOfStudyJasper(Date dateOfBegin, Date dateOfEnd,
                                                       JRBeanCollectionDataSource jrBeanCollectionDataSource) {
        Map<String, Object> arg = new HashMap<>();
        arg.put("dateOfBegin", dateOfBegin);
        arg.put("dateOfEnd", dateOfEnd);

        return new JasperReport("Список студентов (по форме обучения)", getRealPath("") + ReportSrc.LIST_STUDENT_BY_FOS.getLocalPath(), arg,
                                jrBeanCollectionDataSource, null
        );
    }

    public JasperReport printReportNewNotionDirector(Date dateNotion, Date dateExamination, String executorFio, String executorTel,
                                                     List<NotionStudentModel> notionStudentModels) {

        Map<String, Object> params = new HashMap<>();
        params.put("dateNotion", dateNotion);
        params.put("dateExamination", dateExamination);
        params.put("executorFio", executorFio);
        params.put("executorTel", executorTel);
        params.put("realPath", getRealPath("orders/notionDirector/") + File.separator);

        Map<String, Object> map = new HashMap<>();
        map.put("notionStudents", notionStudentModels);
        List<Map<String, ?>> collections = Collections.singletonList(map);

        return new JasperReport("Представление директора", getRealPath("") + ReportSrc.NEW_NOTION.getLocalPath(), params,
                                new JRMapCollectionDataSource(collections), null
        );
    }

    public JasperReport printReportNotionDirector(List<NotionMainModel> mainModels) {
        return new JasperReport("Представление директора", getRealPath("") + ReportSrc.NOTION.getLocalPath(), new HashMap(),
                                new JRBeanCollectionDataSource(mainModels), null
        );
    }

    public JasperReport printReportBranchOfContractNotion(List<NotionMainModel> mainModels) {
        return new JasperReport("Представление директора на отчисление за невыполнение условий договора",
                                getRealPath("") + ReportSrc.NOTION_BY_BREACH_OF_CONTRACT.getLocalPath(), new HashMap(),
                                new JRBeanCollectionDataSource(mainModels), null
        );
    }

    public JasperReport getReportForRegisters(Date dateOfBegin, Date dateOfEnd, SemesterModel sem) {
        List<RegisterDateModel> registerDateModels = new RegisterReportServiceImpl()
                .getRegistersByPeriod(dateOfBegin, dateOfEnd, sem.getIdSem());

        if (registerDateModels.isEmpty()) {
            return null;
        }

        Date CurrentDate = new Date();
        Map<String, Object> arg = new HashMap<>();
        arg.put("dateOfBegin", DateConverter.convertDateToStringByFormat(sem.getDateOfBegin(), "yyyy"));
        arg.put("dateOfEnd", DateConverter.convertDateToStringByFormat(sem.getDateOfEnd(), "yyyy"));
        arg.put("season", sem.getSeason() == 0 ? "осенний" : "весенний");
        arg.put("CurrentDate", DateConverter.convertDateToString(CurrentDate));

        return new JasperReport("Отчет по ведомостям", getRealPath("") + ReportSrc.REGISTER_REPORT.getLocalPath(), arg,
                                new JRBeanCollectionDataSource(registerDateModels), null
        );
    }

    public JasperReport getPassportGroupReport(Collection<Map<String, ?>> params) {
        HashMap<String, Object> args = new HashMap<>();
        args.put("realPath", getRealPath("") + "/passportGroup/");

        return new JasperReport("Паспорт групп", getRealPath("") + ReportSrc.PASSPORT_GROUP_REPORT.getLocalPath(), args,
                                new JRMapCollectionDataSource(params), null
        );
    }

    private String getRealPath(String relativePath) {
        if (Executions.getCurrent() != null) {
            return Executions.getCurrent().getDesktop().getWebApp().getRealPath("WEB-INF/reports/" + relativePath);
        } else if (servletContext != null) {
            return servletContext.getRealPath("WEB-INF/reports/" + relativePath);
        }
        return "";
    }

    public JasperReport getResitMark(StudentStatusModel student) {
        ResitMarkService resitMarkService = new ResitMarkImpl();
        return new JasperReport("Перезачет оценок", getRealPath("") + ReportSrc.RESIT_MARK_REPORT.getLocalPath(), new HashMap(),
                                resitMarkService.getResitReport(student), null
        );
    }

    public JasperReport getMatHelpProtocol(ProtocolModel protocolModel) {

        return new JasperReport("Протокол на материальную помощь", getRealPath("") + ReportSrc.PROTOCOL_MAT_HELP.getLocalPath(), null,
                                new JRBeanCollectionDataSource(Collections.singletonList(protocolModel)), null
        );
    }

    public JasperReport getRegisterCurrentProgress(List<Long> listIDlgs) {
        List<RegisterCurProgressGroupModel> result = new ArrayList<>();
        PassportGroupService passportGroupService = new PassportGroupServiceESO();

        for (Long idLGS : listIDlgs) {
            RegisterCurProgressGroupModel group = passportGroupService.getGroupname(idLGS);
            group.setStudents(passportGroupService.getRegisterCurProgressByLgs(idLGS));
            result.add(group);
        }
        return new JasperReport("Ведомость текущей успеваемости", getRealPath("") + ReportSrc.REGISTER_CURRENT_PROGRESS.getLocalPath(),
                                null, new JRBeanCollectionDataSource(result), null
        );
    }
}