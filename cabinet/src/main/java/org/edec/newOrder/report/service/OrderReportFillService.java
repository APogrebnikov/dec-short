package org.edec.newOrder.report.service;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.newOrder.model.dao.EmployeeOrderEsoModel;
import org.edec.newOrder.model.report.OrderReportEmployeeModel;
import org.edec.newOrder.model.report.OrderReportMainModel;
import org.edec.newOrder.model.report.OrderReportModel;
import org.edec.newOrder.report.OrderLineService;
import org.edec.newOrder.report.OrderReportDAO;
import org.edec.newOrder.report.OrderReportMineDAO;
import org.edec.newOrder.report.constant.OrderLineType;
import org.edec.newOrder.report.model.OrderLineModel;
import org.edec.newOrder.report.model.OrderPageModel;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.constants.OrderStudentJSONConst;
import org.edec.utility.constants.ScholarshipSizeConst;
import org.edec.utility.converter.DateConverter;
import org.edec.workflow.model.WorkflowModel;
import org.edec.workflow.service.WorkflowService;
import org.edec.workflow.service.impl.WorkflowServiceEnsembleImpl;
import org.json.JSONObject;
import org.zkoss.zk.ui.Executions;

import javax.servlet.ServletContext;
import java.util.*;
import java.util.stream.Collectors;

import static org.edec.utility.constants.OrderRuleConst.ACADEMIC_FIRST_COURSE;
import static org.edec.utility.constants.OrderRuleConst.ACADEMIC_IN_SESSION;
import static org.edec.utility.constants.OrderRuleConst.ACADEMIC_NOT_IN_SESSION;

public class OrderReportFillService {
    public static final String MAIN_ORDER = "main_order";
    public static final String PREDICATE_FIO = "predicate_fio";
    public static final String PREDICATE_POST = "predicate_post";
    public static final String EMPLOYEES = "employee";

    public static final String FORM_OF_STUDY = "$FOS$";
    public static final String FIRST_DATE = "$DATE1$";

    private OrderReportMainModel main;

    private OrderReportDAO orderReportDAO = new OrderReportDAO();

    private OrderLineService orderLineService = new OrderLineService();

    private ServletContext servletContext;

    private Boolean original = false;

    public OrderReportFillService() {
    }

    public OrderReportFillService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public Map<String, Object> getMainOrderMap(Long idOrder) {
        Map<String, Object> map = new HashMap<>();
        main = orderReportDAO.getMainOrderInfoByID(idOrder);

        //TODO: Добавить проверку наличия в системе сертификатов emplo
        List<WorkflowModel> wfs = null;

        if (main.getOrdernumber() != null && !original) {
            WorkflowService workflowService;
            if (Executions.getCurrent() != null) {
                workflowService = new WorkflowServiceEnsembleImpl();
            } else {
                workflowService = new WorkflowServiceEnsembleImpl(servletContext);
            }
            wfs = workflowService.getArchiveTasksConfirmingByOrderId(idOrder);
        }

        List<EmployeeOrderEsoModel> employeesOrderModels = orderReportDAO.getEmployeesOrder(idOrder);
        List<OrderReportEmployeeModel> employees = new ArrayList<>();
        String predicatingfio = null, predicatingpost = null;
        for (EmployeeOrderEsoModel employee : employeesOrderModels) {
            if (employee.getSubquery() != null && !employee.getSubquery().equals("")) {
                if (!orderReportDAO.existsStudentsInOrderBySubquery(idOrder, employee.getSubquery())) {
                    continue;
                }
            }
            if (employee.getActionrule() == 0) {
                if (main.getFormOfStudyId().equals(employee.getFormofstudy())) {
                    main.setExecutorfio(employee.getFio());
                    main.setExecutortel(employee.getPost());
                }
            } else if (employee.getActionrule() == 1) {
                predicatingfio = employee.getShortFio();
                predicatingpost = employee.getPost();
            } else if (employee.getActionrule() == 2) {
                OrderReportEmployeeModel emp = new OrderReportEmployeeModel(employee.getShortFio(), employee.getPost());
                if (wfs != null) {
                    for (WorkflowModel wf : wfs) {
                        if (employee.getFio().equals(wf.getFio()) && wf.getCertNumber() != null) {
                            emp.setCertnum(wf.getCertNumber());
                            emp.setCertfio(wf.getFio());
                        }
                    }
                }
                employees.add(emp);
            }
        }

        main.setPredicatingfio(predicatingfio);
        main.setPredicatingpost(predicatingpost);
        main.setEmployees(employees);

        map.put(MAIN_ORDER, main);
        map.put(PREDICATE_FIO, predicatingfio);
        map.put(PREDICATE_POST, predicatingpost);
        map.put(EMPLOYEES, employees);

        return map;
    }

    /* Общий метод для формирования отчета Jasper*/
    public JRBeanCollectionDataSource getBeanData(Long idOrder, Long idOrderRule, List<OrderLineModel> orderLines) {
        getMainOrderMap(idOrder);
        List<OrderLineModel> lines;

        List<OrderPageModel> pages = new ArrayList<>();

        if (orderLines == null) {
            lines = orderLineService.getOrderLines(idOrder);
        } else {
            lines = orderLines;
        }

        if (lines.isEmpty()) {
            lines = getLines(idOrder);

            orderLineService.saveOrderLines(idOrder, lines);
        }

        if (OrderRuleConst.getById(idOrderRule).equals(ACADEMIC_IN_SESSION) ||
            OrderRuleConst.getById(idOrderRule).equals(ACADEMIC_NOT_IN_SESSION) ||
            OrderRuleConst.getById(idOrderRule).equals(ACADEMIC_FIRST_COURSE)) {
            pages = orderLineService.getOrderPagesForAcademicScholarship(lines, main);
        }

        main.setOrderLines(lines);
        main.setOrderPages(pages);

        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    /* Общий метод для формирования строк по приказу */
    public List<OrderLineModel> getLines(Long idOrder) {

        List<OrderLineModel> lines = new ArrayList<>();

        Long idOrderRule = orderReportDAO.getOrderType(idOrder);

        //формируем список строк
        switch (OrderRuleConst.getById(idOrderRule)) {
            case SET_ELIMINATION_AFTER_TRANSFER_RESPECTFUL:
            case SET_ELIMINATION_AFTER_TRANSFER_NOT_RESPECTFUL:
                lines = getLinesForTransferProlongation(idOrder);
                break;
            case SET_ELIMINATION_AFTER_PRACTICE:
                lines = getLinesForSetFirstEliminationAfterPractice(idOrder);
                break;
            case CANCEL_SCHOLARSHIP_AFTER_PRACTICE:
                lines = getLinesForCancelScholarshipAfterPracticeOrder(idOrder);
                break;
            case CANCEL_ACADEMICAL_SCHOLARSHIP_IN_SESSION:
                lines = getLinesForCancelScholarshipAfterSessionOrder(idOrder);
                break;
            case SOCIAL_IN_SESSION:
            case SOCIAL_NEW_REFERENCE:
                lines = getLinesForSocialOrder(idOrder);
                break;
            case SOCIAL_INCREASED_IN_SESSION:
            case SOCIAL_INCREASED_NEW_REFERENCE:
                lines = getLinesForSocialIncreasedOrder(idOrder);
                break;
            case TRANSFER:
                lines = getLinesForTransfer(idOrder);
                break;
            case TRANSFER_CONDITIONALLY_RESPECTFUL:
                lines = getLinesForTransferOrder(idOrder);
                break;
            case TRANSFER_CONDITIONALLY:
                lines = getLinesForTransferConditionally(idOrder);
                break;
            case TRANSFER_AFTER_TRANSFER_CONDITIONALLY_RESPECTFUL:
            case TRANSFER_AFTER_TRANSFER_CONDITIONALLY_NOT_RESPECTFUL:
                lines = getLinesForTransferAfterTransferOrder(idOrder);
                break;
            case DEDUCTION_INITIATIVE:
                lines = getLinesForDeductionOrder(idOrder);
                break;
            case DEDUCTION_BY_COMMISSION_RESULT:
                lines = getLinesForDeductionByComissionResultOrder(idOrder);
                break;
            case SET_ELIMINATION_RESPECTFUL:
            case SET_ELIMINATION_NOT_RESPECTFUL:
            case PROLONGATION_ELIMINATION_WINTER:
                lines = getLinesForSetElimination(idOrder);
                break;
            case MATERIAL_SUPPORT:
                lines = getLinesForMaterialSupport(idOrder);
                break;
            case SET_FIRST_ELIMINATION:
                lines = getLinesForSetFirstElimination(idOrder);
                break;
            case SET_SECOND_ELIMINATION:
                lines = getLinesForSetEliminationCertification(idOrder);
                break;
            case ACADEMIC_IN_SESSION:
            case ACADEMIC_NOT_IN_SESSION:
                List<OrderPageModel> pages = getPagesForAcademicOrder(idOrder);
                lines = orderLineService.getOrderLinesForAcademicScholarship(pages);
                break;
            case ACADEMIC_FIRST_COURSE:
                List<OrderPageModel> pages2 = getPagesForAcademicFirstCourseOrder(idOrder);
                lines = orderLineService.getOrderLinesForAcademicScholarship(pages2);
                break;
            case CANCEL_SOCIAL_INCREASED_BY_PRACTICE:
            case CANCEL_SOCIAL_INCREASED_IN_SESSION:
                lines = getLinesForCancelSocialIncreasedOrder(idOrder);
                break;
            case ACADEMIC_INDIVIDUAL:
                lines = getLinesForAcademicIndividualOrder(idOrder);
                break;
            case ACADEMIC_INCREASED:
                lines = getLinesForAcademicIncreasedOrder(idOrder);
                break;
        }

        List<OrderLineModel> orderHeader = new ArrayList<>();
        String description = orderReportDAO.getOrderDescription(idOrder);

        orderHeader.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        orderHeader.add(new OrderLineModel(description, OrderLineType.RED_LINE_INDENT.getValue()));
        orderHeader.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        orderHeader.add(new OrderLineModel("ПРИКАЗЫВАЮ:", getPosition(idOrderRule)));
        orderHeader.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

        lines.addAll(0, orderHeader);

        //проставляем номера для всех приказов кроме академических (у них проставляется на этапе формирования строк)
        if (!OrderRuleConst.getById(idOrderRule).equals(ACADEMIC_IN_SESSION) &&
            !OrderRuleConst.getById(idOrderRule).equals(ACADEMIC_NOT_IN_SESSION) &&
            !OrderRuleConst.getById(idOrderRule).equals(ACADEMIC_FIRST_COURSE)) {

            int lineNumber = 1;

            for (OrderLineModel orderLine : lines) {
                orderLine.setLineNumber(lineNumber);
                lineNumber++;
            }
        } else {
            //так как добавляем заголовок для всех шаблонов,
            //необходимо пересчитать номера линий для первой страницы
            lines = recountLineNumbersForFirstPage(lines);
        }

        return lines;
    }

    private int getPosition(Long idOrderRule) {
        switch (OrderRuleConst.getById(idOrderRule)) {
            case SOCIAL_IN_SESSION:
            case SOCIAL_NEW_REFERENCE:
            case SOCIAL_INCREASED_IN_SESSION:
            case SOCIAL_INCREASED_NEW_REFERENCE:
            case SET_FIRST_ELIMINATION:
            case TRANSFER_CONDITIONALLY:
            case TRANSFER_AFTER_TRANSFER_CONDITIONALLY_NOT_RESPECTFUL:
                return OrderLineType.NO_INDENT.getValue();
            default:
                return OrderLineType.CENTERED.getValue();
        }
    }

    private List<OrderLineModel> recountLineNumbersForFirstPage(List<OrderLineModel> lines) {
        int lineNumber = 1;

        for (OrderLineModel line : lines) {
            if (line.getLinePage() != null && line.getLinePage() == 2) {
                break;
            }
            line.setLineNumber(lineNumber);
            line.setLinePage(1);

            lineNumber++;
        }

        return lines;
    }

    /* Получение строк по приказам */
    public List<OrderLineModel> getLinesForTransferProlongation(Long idOrder) {

        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> models = orderReportDAO.getTransferEsoModel(idOrder);

        String foundation = models.size() == 0 ? "" : models.get(0).getFoundation();

        String prevDescription = "";
        String prevGroup = "";
        int prevCourse = 0;
        int sectionNumber = 1;
        int studentNumber = 1;

        for (OrderReportModel transfer : models) {
            if (!prevDescription.equals(transfer.getDescription())) {
                if (!prevDescription.equals("")) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                prevDescription = transfer.getDescription();
                lines.add(new OrderLineModel(sectionNumber + ". " + prevDescription, OrderLineType.RED_LINE_INDENT.getValue()));
                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                sectionNumber++;
                studentNumber = 1;
                prevGroup = "";
                prevCourse = 0;
            }

            if (prevCourse != transfer.getCourse()) {
                prevCourse = transfer.getCourse();
                lines.add(new OrderLineModel(prevCourse + " курс", OrderLineType.NO_INDENT.getValue()));
                studentNumber = 1;
                prevGroup = "";
            }

            if (!prevGroup.equals(transfer.getGroupname())) {
                prevGroup = transfer.getGroupname();
                lines.add(new OrderLineModel("Группа " + prevGroup, OrderLineType.NO_INDENT.getValue()));
                studentNumber = 1;
            }

            lines.add(new OrderLineModel(studentNumber++ + ". (# " + transfer.getRecordbook() + ") " + transfer.getFio(),
                                         OrderLineType.RED_LINE_INDENT.getValue()
            ));
        }

        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("Основание: " + foundation, OrderLineType.NO_INDENT.getValue()));

        return lines;
    }

    public List<OrderLineModel> getLinesForSetFirstEliminationAfterPractice(Long idOrder){
        //Получение и преобразование данных
        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> models = orderReportDAO.getTransferEsoModel(idOrder);

        JSONObject attributes = new JSONObject(orderReportDAO.getOrderAttributes(idOrder));
        String season = attributes.has("attr2") ? attributes.getString("attr2") : "";
        String year = attributes.has("attr3") ? attributes.getString("attr3") : "";

        models.forEach(el -> {
            JSONObject json = new JSONObject(el.getAdditional());
            el.setPracticType(json.has(OrderStudentJSONConst.PRACTIC_TYPE) ? json.getString(OrderStudentJSONConst.PRACTIC_TYPE) : "");
            el.setPracticName(json.has(OrderStudentJSONConst.PRACTIC_NAME) ? json.getString(OrderStudentJSONConst.PRACTIC_NAME) : "");
            el.setDatePracticFrom(DateConverter.convertStringToDate(
                    json.has(OrderStudentJSONConst.PRACTIC_DATE_FROM) ? json.getString(OrderStudentJSONConst.PRACTIC_DATE_FROM) : "",
                    "dd.MM.yyyy"
            ));
            el.setDatePracticTo(DateConverter.convertStringToDate(
                    json.has(OrderStudentJSONConst.PRACTIC_DATE_TO) ? json.getString(OrderStudentJSONConst.PRACTIC_DATE_TO) : "",
                    "dd.MM.yyyy"
            ));
            el.setDescription(
                    el.getDescription().replace("$practicType$", el.getPracticType()).replace("$practicName$", el.getPracticName())
                      .replace("$datePracticFrom$", DateConverter.convertDateToString(el.getDatePracticFrom()))
                      .replace("$datePracticTo$", DateConverter.convertDateToString(el.getDatePracticTo()))
                      .replace("$dateTo$", DateConverter.convertDateToString(el.getDate1()))
                      .replace("$season$", season)
                      .replace("$year$", year)) ;
        });

        models = models.stream()
                       .sorted(Comparator.comparing(OrderReportModel::getDescription)
                                         .thenComparing(OrderReportModel::getGovernmentFinanced)
                                         .thenComparing(OrderReportModel::getGroupname)
                                         .thenComparing(OrderReportModel::getFio))
                       .collect(Collectors.toList());

        //инициализируем начальные значения
        int sectionNumber = 1;
        int studentNumber = 1;
        String prevDescription = "";
        String prevDateTo = "";
        String prevGovernmentFinanced = "";
        Integer prevCourse = 0;
        String prevGroup = "";

        for(OrderReportModel model : models){
            //Описание
            if (!prevDescription.equals(model.getDescription())) {
                if (!prevDescription.equals("")) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                prevDescription = model.getDescription();
                lines.add(new OrderLineModel(sectionNumber + ". " + prevDescription, OrderLineType.RED_LINE_INDENT.getValue()));
                sectionNumber++;

                prevDateTo = "";
            }
            //Дата
            String dateTo = DateConverter.convertDateToString(model.getDate1());
            if(!prevDateTo.equals(dateTo)){
                prevDateTo = dateTo;
                /*
                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                lines.add(new OrderLineModel("до " + prevDateTo, OrderLineType.CENTERED.getValue()));
                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                */
                prevGovernmentFinanced = "";
            }
            //Форма обучения
            String governmentFinanced = Boolean.toString(model.getGovernmentFinanced());
            if(!prevGovernmentFinanced.equals(governmentFinanced)){

                if(!prevGovernmentFinanced.isEmpty()){
                    //Основание
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                    lines.add(new OrderLineModel(sectionNumber + ". " + prevDescription, OrderLineType.RED_LINE_INDENT.getValue()));
                    sectionNumber++;
                    //Дата
                    /*
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                    lines.add(new OrderLineModel("до " + prevDateTo, OrderLineType.CENTERED.getValue()));
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                     */
                }

                prevGovernmentFinanced = model.getGovernmentFinanced().toString();

                lines.add(new OrderLineModel(
                        model.getGovernmentFinanced() ? "За счет бюджетных ассигнований федерального бюджета"
                        : "На условиях договора об оказании платных образовательных услуг", OrderLineType.NO_INDENT.getValue()));

                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

                prevCourse = 0;
            }
            //Курс
            if(!prevCourse.equals(model.getCourse())){
                prevCourse = model.getCourse();

                lines.add(new OrderLineModel(model.getCourse() + 1 + " курс", OrderLineType.NO_INDENT.getValue()));
                prevGroup = "";
            }
            //Группа
            if(!prevGroup.equals(model.getGroupname())){
                prevGroup = model.getGroupname();

                lines.add(new OrderLineModel("Группа " + prevGroup, OrderLineType.NO_INDENT.getValue()));
                studentNumber = 1;
            }
            //Студент
            lines.add(new OrderLineModel(studentNumber++ + ". (# " + model.getRecordbook() + ") " + model.getFio(),
                                         OrderLineType.RED_LINE_INDENT.getValue()
            ));
        }

        //Низ документа
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("Прилагаемые документы: служебная записка и.о. директора института с указанием устанавливаемых сроков.",
                                     OrderLineType.NO_INDENT.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

        return lines;
    }

    public List<OrderLineModel> getLinesForCancelScholarshipAfterPracticeOrder(Long idOrder) {

        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> models = orderReportDAO.getTransferEsoModel(idOrder);

        String foundation = models.size() == 0 ? "" : models.get(0).getFoundation();

        String prevDescription = "";
        String prevGroup = "";
        int prevCourse = 0;
        int sectionNumber = 1;
        int studentNumber = 1;

        for (OrderReportModel transfer : models) {
            if (!prevDescription.equals(transfer.getDescription())) {

                prevDescription = transfer.getDescription();
                lines.add(new OrderLineModel(sectionNumber + ". " + prevDescription, OrderLineType.NO_INDENT.getValue()));
                sectionNumber++;
                studentNumber = 1;
                prevGroup = "";
                prevCourse = 0;
            }

            if (prevCourse != transfer.getCourse()) {
                //Убираем отступ сразу после названия секции
                if (prevCourse != 0) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                prevCourse = transfer.getCourse();
                lines.add(new OrderLineModel(prevCourse + " курс", OrderLineType.NO_INDENT.getValue()));
                studentNumber = 1;
                prevGroup = "";
            }

            if (!prevGroup.equals(transfer.getGroupname())) {
                prevGroup = transfer.getGroupname();
                lines.add(new OrderLineModel("Группа " + prevGroup, OrderLineType.NO_INDENT.getValue()));
                studentNumber = 1;
            }

            lines.add(new OrderLineModel(studentNumber++ + ". (# " + transfer.getRecordbook() + ") " + transfer.getFio(),
                                         OrderLineType.RED_LINE_INDENT.getValue()
            ));
        }

        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("Основание: " + foundation, OrderLineType.NO_INDENT.getValue()));

        return lines;
    }

    public List<OrderLineModel> getLinesForCancelScholarshipAfterSessionOrder(Long idOrder) {

        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> models = orderReportDAO.getStudentsForCancelInSession(idOrder);

        String foundation = models.size() == 0 ? "" : models.get(0).getFoundation();

        String prevDescription = "";
        String prevGroup = "";
        int prevCourse = 0;
        int sectionNumber = 1;
        int studentNumber = 1;

        for (OrderReportModel transfer : models) {
            if (!prevDescription.equals(transfer.getDescription())) {
                if (!prevDescription.equals("")) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                prevDescription = transfer.getDescription();
                lines.add(new OrderLineModel(sectionNumber + ") " + prevDescription, OrderLineType.RED_LINE_INDENT.getValue()));
                sectionNumber++;
                studentNumber = 1;
                prevGroup = "";
                prevCourse = 0;
            }

            if (prevCourse != transfer.getCourse()) {

                prevCourse = transfer.getCourse();
                lines.add(new OrderLineModel(prevCourse + " курс", OrderLineType.NO_INDENT.getValue()));
                studentNumber = 1;
                prevGroup = "";
            }

            if (!prevGroup.equals(transfer.getGroupname())) {
                prevGroup = transfer.getGroupname();
                lines.add(new OrderLineModel("Группа " + prevGroup, OrderLineType.NO_INDENT.getValue()));
                studentNumber = 1;
            }

            lines.add(new OrderLineModel(studentNumber++ + ". (# " + transfer.getRecordbook() + ") " + transfer.getFio(),
                                         OrderLineType.RED_LINE_INDENT.getValue()
            ));
        }

        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("Основание: " + foundation, OrderLineType.NO_INDENT.getValue()));

        return lines;
    }

    public List<OrderLineModel> getLinesForSocialOrder(Long idOrder) {

        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> models = orderReportDAO.getListSocial(idOrder);

        String foundation = models.size() == 0 ? "" : models.get(0).getFoundation();

        String prevDescription = "";
        String prevGroup = "";
        int prevCourse = 0;
        int sectionNumber = 1;
        int studentNumber = 1;

        for (OrderReportModel transfer : models) {
            if (!prevDescription.equals(transfer.getDescription())) {
                if (!prevDescription.equals("")) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                prevDescription = transfer.getDescription();
                lines.add(new OrderLineModel(sectionNumber + ". " + prevDescription, OrderLineType.RED_LINE_INDENT.getValue()));
                sectionNumber++;
                studentNumber = 1;
                prevGroup = "";
                prevCourse = 0;
            }

            if (prevCourse != transfer.getCourse()) {
                if (prevCourse != 0) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                prevCourse = transfer.getCourse();
                lines.add(new OrderLineModel("Курс " + prevCourse, OrderLineType.NO_INDENT.getValue()));
                studentNumber = 1;
                prevGroup = "";
            }

            if (!prevGroup.equals(transfer.getGroupname())) {
                prevGroup = transfer.getGroupname();
                lines.add(new OrderLineModel("Группа " + prevGroup, OrderLineType.NO_INDENT.getValue()));
                studentNumber = 1;
            }

            if (transfer.getSirota() != null && transfer.getSirota()) {
                transfer.setScholarship(ScholarshipSizeConst.SOCIAL_FOR_ORPHAN.getSize());
            } else if ((transfer.getIndigent() != null && transfer.getIndigent())
                    || (transfer.getInvalid() != null && transfer.getInvalid())
                    || (transfer.getVeteran() != null && transfer.getVeteran())
            ) {
                transfer.setScholarship(ScholarshipSizeConst.SOCIAL.getSize());
            }

            lines.add(new OrderLineModel(
                    studentNumber++ + ". (# " + transfer.getRecordbook() + ") " + transfer.getFio() + " - " + transfer.getScholarship() +
                    "(руб.)", OrderLineType.RED_LINE_INDENT.getValue()));
        }

        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("Основание: " + foundation, OrderLineType.NO_INDENT.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

        return lines;
    }

    public List<OrderLineModel> getLinesForSocialIncreasedOrder(Long idOrder) {

        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> models = orderReportDAO.getListSocialIncrease(idOrder);

        String foundation = models.size() == 0 ? "" : models.get(0).getFoundation();

        String prevDescription = "";
        String prevDescriptionDate = "";
        String prevGroup = "";
        int prevCourse = 0;
        int sectionNumber = 1;
        int studentNumber = 1;

        for (OrderReportModel transfer : models) {
            if (!prevDescription.equals(transfer.getDescription())) {

                if (!prevDescription.equals("")) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                prevDescription = transfer.getDescription();
                lines.add(new OrderLineModel(sectionNumber + ". " + prevDescription, OrderLineType.RED_LINE_INDENT.getValue()));
                sectionNumber++;
                studentNumber = 1;
                prevGroup = "";
                prevCourse = 0;
                prevDescriptionDate = "";
            }

            if ((transfer.getIdOrderSection() == 59 || transfer.getIdOrderSection() == 61) &&
                !prevDescriptionDate.equals(transfer.getDescriptionDate())) {

                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

                prevDescriptionDate = transfer.getDescriptionDate();
                lines.add(new OrderLineModel(prevDescriptionDate, OrderLineType.NO_INDENT.getValue()));
                studentNumber = 1;
                prevGroup = "";
                prevCourse = 0;
            }

            if (prevCourse != transfer.getCourse()) {
                if (prevCourse == 0 && (transfer.getIdOrderSection() != 59 && transfer.getIdOrderSection() != 61)) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                prevCourse = transfer.getCourse();
                lines.add(new OrderLineModel("Курс " + prevCourse, OrderLineType.NO_INDENT.getValue()));
                studentNumber = 1;
                prevGroup = "";
            }

            if (!prevGroup.equals(transfer.getGroupname())) {
                prevGroup = transfer.getGroupname();
                lines.add(new OrderLineModel("Группа " + prevGroup, OrderLineType.NO_INDENT.getValue()));
                studentNumber = 1;
            }

            if (transfer.getSirota() != null && transfer.getSirota() && transfer.getIdOrderSection() != null &&
                (transfer.getIdOrderSection() == 59 || transfer.getIdOrderSection() == 61)) {
                transfer.setScholarship(ScholarshipSizeConst.SOCIAL_INCREASED_FOR_ORPHAN.getSize());
            } else if (((transfer.getIndigent() != null && transfer.getIndigent())
                    || (transfer.getVeteran() != null && transfer.getVeteran())
                    || (transfer.getInvalid() != null && transfer.getInvalid()) && transfer.getIdOrderSection() != null)
                    && (transfer.getIdOrderSection() == 59 || transfer.getIdOrderSection() == 61)
            ) {
                transfer.setScholarship(ScholarshipSizeConst.SOCIAL_INCREASED.getSize());
            }

            lines.add(new OrderLineModel(studentNumber++ + ". (# " + transfer.getRecordbook() + ") " + transfer.getFio() +
                                         (transfer.getScholarship() == null ? "" : " - " + transfer.getScholarship() + "(руб.)"),
                                         OrderLineType.RED_LINE_INDENT.getValue()
            ));
        }

        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("Основание: " + foundation, OrderLineType.NO_INDENT.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

        return lines;
    }

    public List<OrderLineModel> getLinesForTransferOrder(Long idOrder) {

        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> models = orderReportDAO.getTransferEsoModel(idOrder);

        String foundation = models.size() == 0 ? "" : models.get(0).getFoundation();

        String prevDescription = "";
        String prevGroup = "";
        int prevCourse = 0;
        int sectionNumber = 1;
        int studentNumber = 1;

        for (OrderReportModel transfer : models) {
            if (!prevDescription.equals(transfer.getDescription())) {

                if (!prevDescription.equals("")) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                prevDescription = transfer.getDescription();
                lines.add(new OrderLineModel(sectionNumber + ") " + prevDescription, OrderLineType.RED_LINE_INDENT.getValue()));
                sectionNumber++;
                prevCourse = 0;
            }

            if (prevCourse != transfer.getCourse()) {

                prevCourse = transfer.getCourse();
                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                lines.add(new OrderLineModel("На " + prevCourse + " курс", OrderLineType.RED_LINE_INDENT.getValue()));
                prevGroup = "";
            }

            if (!prevGroup.equals(transfer.getGroupname())) {
                prevGroup = transfer.getGroupname();
                lines.add(new OrderLineModel("Группа " + prevGroup, OrderLineType.RED_LINE_INDENT.getValue()));
                studentNumber = 1;
            }

            lines.add(new OrderLineModel(studentNumber++ + ". (#" + transfer.getRecordbook() + ") " + transfer.getFio(),
                                         OrderLineType.RED_LINE_INDENT.getValue()
            ));
        }

        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("Основание: " + foundation, OrderLineType.NO_INDENT.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

        return lines;
    }

    public List<OrderLineModel> getLinesForTransferAfterTransferOrder(Long idOrder) {

        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> models = orderReportDAO.getTransferEsoModel(idOrder);

        String prevDescription = "";
        String prevGroup = "";
        int prevCourse = 0;
        int sectionNumber = 1;
        int studentNumber = 1;
        long prevIdOrderSection = 0;

        for (OrderReportModel transfer : models) {

            if (transfer.getAdditional() != null && !transfer.getAdditional().equals("")) {
                JSONObject json = new JSONObject(transfer.getAdditional());
                if (json.has(OrderStudentJSONConst.ORDER_NUMBER) &&
                    json.has(OrderStudentJSONConst.ORDER_DATE) &&
                        json.has(OrderStudentJSONConst.SCHOOL_YEAR)) {
                    Date datePrev = DateConverter
                            .convertStringToDate(json.getString(OrderStudentJSONConst.ORDER_DATE), "yyyy-MM-dd");

                    transfer.setPrevOrderDate(DateConverter.convertDateToString(datePrev));
                    transfer.setPrevOrderNum(json.getString(OrderStudentJSONConst.ORDER_NUMBER));
                    transfer.setSchoolYear(json.getString(OrderStudentJSONConst.SCHOOL_YEAR));

                    transfer.setDescription(transfer.getDescription().replace("$prevNum$", transfer.getPrevOrderNum()));
                    transfer.setDescription(transfer.getDescription().replace("$prevDate$", transfer.getPrevOrderDate()));
                    transfer.setDescription(transfer.getDescription().replace("$year$", transfer.getSchoolYear()));
                }
            }

            if (!prevDescription.equals(transfer.getDescription())) {

                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

                prevDescription = transfer.getDescription();

                lines.add(new OrderLineModel(sectionNumber + ". " + transfer.getDescription(), OrderLineType.RED_LINE_INDENT.getValue()));

                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

                if (transfer.getIdOrderSection() == 85L) {
                    lines.add(new OrderLineModel("за счет бюджетный ассигнований федерального бюджета",
                                                 OrderLineType.NO_INDENT.getValue()
                    ));
                } else {
                    lines.add(new OrderLineModel("на условиях договора об оказании платных образовательных услуг",
                                                 OrderLineType.NO_INDENT.getValue()
                    ));
                }

                prevIdOrderSection = transfer.getIdOrderSection();
                sectionNumber++;
                prevCourse = 0;
            }

            if (prevIdOrderSection != 0 && prevIdOrderSection != transfer.getIdOrderSection()) {
                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

                prevDescription = transfer.getDescription();

                lines.add(new OrderLineModel(sectionNumber + ". " + transfer.getDescription(), OrderLineType.RED_LINE_INDENT.getValue()));

                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

                if (transfer.getIdOrderSection() == 85L) {
                    lines.add(new OrderLineModel("за счет бюджетный ассигнований федерального бюджета",
                                                 OrderLineType.NO_INDENT.getValue()
                    ));
                } else {
                    lines.add(new OrderLineModel("на условиях договора об оказании платных образовательных услуг",
                                                 OrderLineType.NO_INDENT.getValue()
                    ));
                }

                prevIdOrderSection = transfer.getIdOrderSection();
                sectionNumber++;
                prevCourse = 0;
            }

            if (prevCourse != transfer.getCourse()) {

                prevCourse = transfer.getCourse();
                lines.add(new OrderLineModel("На " + (prevCourse + 1) + " курсе", OrderLineType.NO_INDENT.getValue()));
                studentNumber = 1;
                prevGroup = "";
            }

            if (!prevGroup.equals(transfer.getGroupname())) {
                prevGroup = transfer.getGroupname();
                lines.add(new OrderLineModel("Группа " + prevGroup, OrderLineType.NO_INDENT.getValue()));
                studentNumber = 1;
            }

            lines.add(new OrderLineModel(studentNumber++ + ". (# " + transfer.getRecordbook() + ") " + transfer.getFio(),
                                         OrderLineType.RED_LINE_INDENT.getValue()
            ));
        }

        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

        return lines;
    }

    public List<OrderLineModel> getLinesForDeductionOrder(Long idOrder) {

        List<OrderReportModel> models = orderReportDAO.getListIndividuals(idOrder);
        List<OrderLineModel> lines = new ArrayList<>();

        int studentNumber = 1;

        for (OrderReportModel student : models) {

            lines.add(new OrderLineModel(
                    (models.size() != 1 ? studentNumber++ + ". " : "") + "(# " + student.getRecordbook() + ") " + student.getFio() + ", " +
                    student.getCourse() + " курс, " + "гр. " + student.getGroupname() + ", " + student.getSpeciality() + ", " +
                    student.getFormofstudy() + ", " + (student.getSex() == 1 ? "обучающегося " : "обучающуюся ") +
                    student.getEconomyformofstudy() + ", " + "отчислить по собственному желанию с " +
                    DateConverter.convertDateToString(student.getDate1()) + ".", OrderLineType.RED_LINE_INDENT.getValue()));

            if (student.getIs_government_financed() == 1) {
                lines.add(
                        new OrderLineModel("Ранее назначенные выплаты отменить с " + DateConverter.convertDateToString(student.getDate2()),
                                           OrderLineType.RED_LINE_INDENT.getValue()
                        ));
            } else {
                student.setFoundation(student.getFoundation().replace(".", "; расчет фактически израсходованных средств."));
            }

            if (student.getAdditional() != null && !student.getAdditional().equals("")) {
                JSONObject additional = new JSONObject(student.getAdditional());

                if (additional.has(OrderStudentJSONConst.FOUNDATION)) {
                    student.setFoundation(additional.getString(OrderStudentJSONConst.FOUNDATION));
                }
            }

            lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
            lines.add(new OrderLineModel("Прилагаемые документы: " + student.getFoundation(), OrderLineType.RED_LINE_INDENT.getValue()));

            lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        }

        return lines;
    }

    public List<OrderLineModel> getLinesForSetElimination(Long idOrder) {

        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> students = orderReportDAO.getSetEliminationModel(idOrder);

        String prevDescription = "";
        String prevGroup = "";
        String prevFoundation = "";
        int prevCourse = 0;
        int sectionNumber = 1;
        int studentNumber = 1;

        //необходимо подсчитать количество секций если их больше 1 то добавляем нумерацию в секциях
        boolean hasMultipleSections = false;
        for (OrderReportModel student : students) {
            if (prevDescription.equals("")) {
                prevDescription = student.getDescription();
            }

            if (!prevDescription.equals(student.getDescription())) {
                hasMultipleSections = true;
                break;
            }
        }
        prevDescription = "";

        for (OrderReportModel student : students) {
            if (!prevDescription.equals(student.getDescription())) {

                if (!prevFoundation.equals("") && !prevFoundation.equals(student.getFoundation())) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                    lines.add(new OrderLineModel("Прилагаемые документы: " + prevFoundation, OrderLineType.NO_INDENT.getValue()));
                }

                if (!prevDescription.equals("")) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                prevDescription = student.getDescription();
                prevFoundation = student.getFoundation();
                prevCourse = 0;

                lines.add(new OrderLineModel(
                        ((hasMultipleSections || main.getIdOrderRule() == OrderRuleConst.SET_ELIMINATION_RESPECTFUL.getId()) ?
                         sectionNumber++ + ") " : "") + prevDescription, OrderLineType.RED_LINE_INDENT.getValue()));
            }

            if (prevCourse != student.getCourse()) {

                prevCourse = student.getCourse();
                prevGroup = "";

                lines.add(new OrderLineModel("Курс " + student.getCourse(), OrderLineType.NO_INDENT.getValue()));
            }

            if (!prevGroup.equals(student.getGroupname())) {
                prevGroup = student.getGroupname();
                studentNumber = 1;

                if (prevCourse == 0) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                lines.add(new OrderLineModel("Группа " + prevGroup, OrderLineType.NO_INDENT.getValue()));
            }

            lines.add(new OrderLineModel(studentNumber++ + ". (# " + student.getRecordbook() + ") " + student.getFio(),
                                         OrderLineType.RED_LINE_INDENT.getValue()
            ));
        }

        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("Прилагаемые документы: " + prevFoundation, OrderLineType.NO_INDENT.getValue()));

        return lines;
    }

    public void updateINNs(Long idOrder) {
        List<OrderReportModel> listMaterialSupportModels = orderReportDAO.getMaterialSupportModel(idOrder);

        String ids = listMaterialSupportModels.stream().filter(el -> el.getIdStudentMine() != null)
                .map(el -> el.getIdStudentMine().toString()).collect(Collectors.joining(","));

        List<OrderReportModel> listModelOnlyINN = new OrderReportMineDAO().getINN(ids);
        for (OrderReportModel student : listMaterialSupportModels) {
            Iterator itr = listModelOnlyINN.iterator();
            while (itr.hasNext()) {
                Object[] obj = (Object[]) itr.next();
                if (obj[1].equals(student.getIdStudentMine().intValue())) {
                    student.setINN(obj[0].toString().replaceAll("[^0-9]", ""));
                }
            }
        }
    }

    public List<OrderLineModel> getLinesForMaterialSupport(Long idOrder) {

        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> listMaterialSupportModels = orderReportDAO.getMaterialSupportModel(idOrder);

        String ids = listMaterialSupportModels.stream().filter(el -> el.getIdStudentMine() != null)
                                              .map(el -> el.getIdStudentMine().toString()).collect(Collectors.joining(","));

        //инициализация DAO непосредственно при использовании обусловлена присутствием
        //большого таймаута подключении к шахтам со своей машины (используем только тогда, когда это нужно)
        List<OrderReportModel> listModelOnlyINN = new OrderReportMineDAO().getINN(ids);

        String prevDescription = "";
        String prevGroup = "";
        int studentNumber = 1;
        int totalSum = 0;

        String foundation = listMaterialSupportModels.size() == 0 ? "" : listMaterialSupportModels.get(0).getFoundation();

        for (OrderReportModel student : listMaterialSupportModels) {
            Iterator itr = listModelOnlyINN.iterator();
            while (itr.hasNext()) {
                Object[] obj = (Object[]) itr.next();
                if (obj[1].equals(student.getIdStudentMine().intValue())) {
                    student.setINN(obj[0].toString().replaceAll("[^0-9]", ""));
                }
            }

            if (!prevDescription.equals(student.getDescription())) {
                prevDescription = student.getDescription();
                prevGroup = "";

                lines.add(new OrderLineModel(student.getDescription(), OrderLineType.NO_INDENT.getValue()));
            }

            if (!prevGroup.equals(student.getGroupname())) {
                prevGroup = student.getGroupname();
                studentNumber = 1;

                if (!prevGroup.equals("")) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                lines.add(new OrderLineModel("Группа № " + student.getGroupname(), OrderLineType.NO_INDENT.getValue()));
            }

            totalSum += Integer.parseInt(student.getScholarship());

            lines.add(new OrderLineModel(
                    studentNumber++ + ".     " + student.getRecordbook() + "     " + student.getFio() + " " + student.getScholarship() +
                    " (руб.)                    ", OrderLineType.CENTERED_LIST.getValue()));

            lines.add(new OrderLineModel("( № " + student.getINN() + "  )", OrderLineType.CENTERED.getValue()));
        }

        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("_________________________________________", OrderLineType.NO_INDENT.getValue()));
        lines.add(new OrderLineModel("Сумма по приказу: " + totalSum + " руб.", OrderLineType.NO_INDENT.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("Основание: " + foundation, OrderLineType.RED_LINE_INDENT.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

        return lines;
    }

    public List<OrderLineModel> getLinesForDeductionByComissionResultOrder(Long idOrder) {

        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> models = orderReportDAO.getListIndividuals(idOrder);

        int studentNumber = 1;

        for (OrderReportModel student : models) {

            lines.add(new OrderLineModel(
                    studentNumber++ + ". (# " + student.getRecordbook() + ") " + student.getFio() + ", " + student.getCourse() + " курс, " +
                    "гр. " + student.getGroupname() + ", " + student.getSpeciality() + ", " + student.getFormofstudy() + ", " +
                    (student.getSex() == 1 ? "обучающегося " : "обучающуюся ") + student.getEconomyformofstudy() + ", " +
                    "отчислить за невыполнение обязанностей по добросовестному освоению образоватеьной программы и выполнению учебного плана (как не " +
                    (student.getSex() == 1 ? "прошедшего " : "прошедшую ") + "промежуточную аттестацию) с " +
                    DateConverter.convertDateToString(student.getDate1()), OrderLineType.RED_LINE_INDENT.getValue()));

            if (student.getIs_government_financed() == 1) {
                lines.add(
                        new OrderLineModel(" Ранее назначенные выплаты отменить с " + DateConverter.convertDateToString(student.getDate2()),
                                           OrderLineType.RED_LINE_INDENT.getValue()
                        ));
            }

            if (student.getAdditional() != null && !student.getAdditional().equals("")) {
                JSONObject additional = new JSONObject(student.getAdditional());

                if (additional.has(OrderStudentJSONConst.FOUNDATION)) {
                    student.setFoundation(additional.getString(OrderStudentJSONConst.FOUNDATION));
                }
            }

            lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
            lines.add(new OrderLineModel("Прилагаемые документы: " + student.getFoundation(), OrderLineType.RED_LINE_INDENT.getValue()));
            lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        }

        return lines;
    }

    public List<OrderLineModel> getLinesForSetEliminationCertification(Long idOrder) {

        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> students = orderReportDAO.getSetCertificationModel(idOrder);

        String prevGroup = "";
        String prevLineCourse = "";
        String foundation = students.size() != 0 ? students.get(0).getFoundation() : null;
        int prevCourse = 0;
        int sectionNumber = 1;
        int studentNumber = 1;
        long prevIdOrderSection = 0;
        String description = "";

        for (OrderReportModel student : students) {

            if (!description.equals(student.getDescription())) {

                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

                description = student.getDescription();

                lines.add(new OrderLineModel(sectionNumber + ". " + student.getDescription(), OrderLineType.RED_LINE_INDENT.getValue()));

                if (student.getIdOrderSection() == 107L) {
                    lines.add(new OrderLineModel("за счет бюджетный ассигнований федерального бюджета",
                                                 OrderLineType.RED_LINE_INDENT.getValue()
                    ));
                } else {
                    lines.add(new OrderLineModel("на условиях договора об оказании платных образовательных услуг",
                                                 OrderLineType.RED_LINE_INDENT.getValue()
                    ));
                }

                prevIdOrderSection = student.getIdOrderSection();
                sectionNumber++;
                prevCourse = 0;
            }

            if (prevIdOrderSection != 0 && prevIdOrderSection != student.getIdOrderSection()) {
                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

                description = student.getDescription();

                lines.add(new OrderLineModel(sectionNumber + ". " + student.getDescription(), OrderLineType.RED_LINE_INDENT.getValue()));

                if (student.getIdOrderSection() == 107L) {
                    lines.add(new OrderLineModel("за счет бюджетный ассигнований федерального бюджета",
                                                 OrderLineType.RED_LINE_INDENT.getValue()
                    ));
                } else {
                    lines.add(new OrderLineModel("на условиях договора об оказании платных образовательных услуг",
                                                 OrderLineType.RED_LINE_INDENT.getValue()
                    ));
                }

                prevIdOrderSection = student.getIdOrderSection();
                sectionNumber++;
                prevCourse = 0;
            }

            if (prevCourse != student.getCourse()) {

                prevCourse = student.getCourse();

                lines.add(new OrderLineModel("Курс " + prevCourse, OrderLineType.RED_LINE_INDENT.getValue()));

                prevGroup = "";
            }

            if (!prevGroup.equals(student.getGroupname())) {

                prevGroup = student.getGroupname();

                lines.add(new OrderLineModel(prevLineCourse + "Группа " + prevGroup, OrderLineType.RED_LINE_INDENT.getValue()));

                studentNumber = 1;
            }

            lines.add(new OrderLineModel(studentNumber++ + ". (# " + student.getRecordbook() + ") " + student.getFio(),
                                         OrderLineType.RED_LINE_INDENT.getValue()
            ));
        }

        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("Прилагаемые документы: " + foundation, OrderLineType.RED_LINE_INDENT.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

        return lines;
    }

    public List<OrderLineModel> getLinesForAcademicIncreasedOrder(Long idOrder) {
        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> models = orderReportDAO.getListForAcademicIncreased(idOrder);

        String prevDescription = "";
        String prevGroup = "";
        String prevSessionRating = "";
        String prevSubDescription = "";
        int studentNumber = 1;

        for (OrderReportModel transfer : models) {

            if (!prevDescription.equals(transfer.getDescription())) {
                if (!prevDescription.equals("")) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                prevDescription = transfer.getDescription();

                lines.add(new OrderLineModel(prevDescription, OrderLineType.NO_INDENT.getValue()));

                prevGroup = "";
            }

            if (!prevGroup.equals(transfer.getGroupname())) {

                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

                prevGroup = transfer.getGroupname();

                lines.add(new OrderLineModel("Группа " + prevGroup, OrderLineType.NO_INDENT.getValue()));

                prevSessionRating = "";
            }

            transfer.setAcademicalPerformance(setSessionResultForAcademicIncreased(transfer.getSessionresult()));

            if (!prevSessionRating.equals(transfer.getAcademicalPerformance())) {
                if (!prevSessionRating.isEmpty()) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                prevSessionRating = transfer.getAcademicalPerformance();

                lines.add(new OrderLineModel(prevSessionRating, OrderLineType.NO_INDENT.getValue()));

                prevSubDescription = "";
            }

            transfer.setSubDescription("за достижения в " + transfer.getNomination() + ":");

            if (!prevSubDescription.equals(transfer.getSubDescription())) {
                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                lines.add(new OrderLineModel(transfer.getSubDescription(), OrderLineType.CENTERED.getValue()));

                prevSubDescription = transfer.getSubDescription();

                studentNumber = 1;
            }

            if (studentNumber == 1) {
                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
            }

            lines.add(new OrderLineModel(studentNumber++ + ".\t" + transfer.getRecordbook() + "\t" + transfer.getFio(),
                                         OrderLineType.RED_LINE_INDENT.getValue()
            ));
        }

        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

        return lines;
    }

    private String setSessionResultForAcademicIncreased(String sessionresult) {
        if (sessionresult.equals("отлично")) {
            return "обучающимся на отлично:";
        } else if (sessionresult.equals("хорошо и отлично")) {
            return "обучающимся на хорошо и отлично:";
        } else if (sessionresult.equals("хорошо")) {
            return "обучающимся на хорошо:";
        }

        return "";
    }

    public List<OrderPageModel> getPagesForAcademicOrder(Long idOrder) {
        getMainOrderMap(idOrder);

        List<OrderReportModel> models = orderReportDAO.getAcademicModel(idOrder);

        //Костыль для постраничного разделения
        List<OrderPageModel> pages = new ArrayList<>();
        List<OrderLineModel> lines = new ArrayList<>();

        String prevDescription = "";
        String prevGroup = "";
        String prevSubDescription = "";
        int studentNumber = 1;

        OrderPageModel page = new OrderPageModel();

        for (OrderReportModel transfer : models) {

            if (!prevGroup.equals(transfer.getGroupname())) {

                if (!prevGroup.equals("")) {
                    page.setPredicatingfio(main.getPredicatingfio());
                    page.setPredicatingpost(main.getPredicatingpost());
                    page.setEmployees(main.getEmployees());

                    //Костыль для постраничного разделения
                    List<OrderLineModel> orderLines = new ArrayList<>();
                    OrderLineModel orderLine = new OrderLineModel();
                    orderLine.setSubOrderLines(lines);
                    orderLines.add(orderLine);

                    page.setOrderLines(orderLines);

                    pages.add(page);

                    page = new OrderPageModel();
                    lines = new ArrayList<>();
                }

                prevGroup = transfer.getGroupname();
                lines.add(new OrderLineModel("Группа " + prevGroup, OrderLineType.CENTERED.getValue()));
                studentNumber = 1;
                prevDescription = "";
                prevSubDescription = "";
            }

            if (!prevDescription.equals(transfer.getDescription())) {

                if (!prevDescription.equals("")) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

                prevDescription = transfer.getDescription();
                lines.add(new OrderLineModel(prevDescription, OrderLineType.NO_INDENT.getValue()));
                studentNumber = 1;
                prevSubDescription = "";
            }

            if (!prevSubDescription.equals(transfer.getSubDescription())) {
                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

                prevSubDescription = transfer.getSubDescription();
                lines.add(new OrderLineModel(prevSubDescription, OrderLineType.CENTERED.getValue()));
                studentNumber = 1;
            }

            if (studentNumber == 1) {
                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
            }

            lines.add(new OrderLineModel(studentNumber++ + ". (" + transfer.getRecordbook() + ") " + transfer.getFio(),
                                         OrderLineType.RED_LINE_INDENT.getValue()
            ));
        }

        page.setPredicatingfio(main.getPredicatingfio());
        page.setPredicatingpost(main.getPredicatingpost());
        page.setEmployees(main.getEmployees());

        //Костыль для постраничного разделения

        List<OrderLineModel> orderLines = new ArrayList<>();
        OrderLineModel orderLine = new OrderLineModel();
        orderLine.setSubOrderLines(lines);
        orderLines.add(orderLine);
        page.setOrderLines(orderLines);
        pages.add(page);

        return pages;
    }

    public List<OrderPageModel> getPagesForAcademicFirstCourseOrder(Long idOrder) {
        getMainOrderMap(idOrder);

        List<OrderReportModel> models = orderReportDAO.getAcademicFirstCourseModel(idOrder);

        //Костыль для постраничного разделения
        List<OrderPageModel> pages = new ArrayList<>();
        List<OrderLineModel> lines = new ArrayList<>();

        String prevDescription;
        String prevGroup = "";
        int studentNumber = 1;

        OrderPageModel page = new OrderPageModel();

        for (OrderReportModel transfer : models) {

            if (!prevGroup.equals(transfer.getGroupname())) {

                if (!prevGroup.equals("")) {
                    page.setPredicatingfio(main.getPredicatingfio());
                    page.setPredicatingpost(main.getPredicatingpost());
                    page.setEmployees(main.getEmployees());

                    //Костыль для постраничного разделения
                    List<OrderLineModel> orderLines = new ArrayList<>();
                    OrderLineModel orderLine = new OrderLineModel();
                    orderLine.setSubOrderLines(lines);
                    orderLines.add(orderLine);

                    page.setOrderLines(orderLines);

                    pages.add(page);

                    page = new OrderPageModel();
                    lines = new ArrayList<>();
                }

                prevGroup = transfer.getGroupname();
                lines.add(new OrderLineModel("Группа " + prevGroup, OrderLineType.CENTERED.getValue()));
                studentNumber = 1;

                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                prevDescription = transfer.getDescription();
                lines.add(new OrderLineModel(prevDescription, OrderLineType.NO_INDENT.getValue()));

                if (models.get(0).getPageHeader() != null && !models.get(0).getPageHeader().equals("")) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                    lines.add(new OrderLineModel(models.get(0).getPageHeader(), OrderLineType.CENTERED.getValue()));
                }
            }

            if (studentNumber == 1) {
                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
            }

            lines.add(new OrderLineModel(studentNumber++ + ". (" + transfer.getRecordbook() + ") " + transfer.getFio(),
                                         OrderLineType.RED_LINE_INDENT.getValue()
            ));
        }

        page.setPredicatingfio(main.getPredicatingfio());
        page.setPredicatingpost(main.getPredicatingpost());
        page.setEmployees(main.getEmployees());

        //Костыль для постраничного разделения

        List<OrderLineModel> orderLines = new ArrayList<>();
        OrderLineModel orderLine = new OrderLineModel();
        orderLine.setSubOrderLines(lines);
        orderLines.add(orderLine);
        page.setOrderLines(orderLines);
        pages.add(page);

        return pages;
    }

    public List<OrderLineModel> getLinesForCancelSocialIncreasedOrder(Long idOrder) {
        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> models = orderReportDAO.getTransferEsoModel(idOrder);

        String foundation = models.size() == 0 ? "" : models.get(0).getFoundation();

        String prevDescription = "";
        String prevGroup = "";
        int prevCourse = 0;
        int sectionNumber = 1;
        int studentNumber = 1;

        for (OrderReportModel transfer : models) {
            if (!prevDescription.equals(transfer.getDescription())) {
                if (!prevDescription.isEmpty()) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                prevDescription = transfer.getDescription();
                lines.add(new OrderLineModel(sectionNumber + ". " + prevDescription, OrderLineType.RED_LINE_INDENT.getValue()));
                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                sectionNumber++;
                studentNumber = 1;
                prevGroup = "";
                prevCourse = 0;
            }

            if (prevCourse != transfer.getCourse()) {

                prevCourse = transfer.getCourse();
                lines.add(new OrderLineModel(prevCourse + " курс", OrderLineType.NO_INDENT.getValue()));
                studentNumber = 1;
                prevGroup = "";
            }

            if (!prevGroup.equals(transfer.getGroupname())) {
                prevGroup = transfer.getGroupname();
                lines.add(new OrderLineModel("Группа " + prevGroup, OrderLineType.NO_INDENT.getValue()));
                studentNumber = 1;
            }

            if (transfer.getSirota() != null && transfer.getSirota()) {
                transfer.setScholarship(ScholarshipSizeConst.SOCIAL_FOR_ORPHAN.getSize());
            } else if ((transfer.getIndigent() != null && transfer.getIndigent())
                    || (transfer.getInvalid() != null && transfer.getInvalid())
                    || (transfer.getVeteran() != null && transfer.getVeteran())
            ) {
                transfer.setScholarship(ScholarshipSizeConst.SOCIAL.getSize());
            }

            lines.add(new OrderLineModel(studentNumber++ + ". (# " + transfer.getRecordbook() + ") " + transfer.getFio() +
                                         (sectionNumber > 2 ? " - " + ScholarshipSizeConst.SOCIAL.getSize() + "(руб.)" : ""),
                                         OrderLineType.RED_LINE_INDENT.getValue()
            ));
        }

        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("Основание: " + foundation + ".", OrderLineType.NO_INDENT.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

        return lines;
    }

    public void setOriginal(Boolean original) {
        this.original = original;
    }

    private List<OrderLineModel> getLinesForAcademicIndividualOrder(Long idOrder) {
        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> models = orderReportDAO.getTransferEsoModel(idOrder);

        String foundation = models.size() == 0 ? "" : models.get(0).getFoundation();
        int studentNumber = 0;

        for (OrderReportModel student : models) {
            lines.add(new OrderLineModel(
                    "(# " + student.getRecordbook() + ") " + student.getFio() + ", " + student.getCourse() + " курс, группа " +
                    student.getGroupname() + ",  успешно прошедшему промежуточную аттестацию, обучающемуся на \"" +
                    student.getAcademicalPerformance() + "\"" +
                    " - назначить государственную академическую стипендию в размере, установленном приказом ректора СФУ," + "  с " +
                    DateConverter.convertDateToString(student.getDate1()) + " по " + DateConverter.convertDateToString(student.getDate2()),
                    OrderLineType.NO_INDENT.getValue()
            ));

            if (studentNumber != models.size() - 1) {
                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
            }

            studentNumber++;
        }

        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("Основание: " + foundation + ".", OrderLineType.RED_LINE_INDENT.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

        return lines;
    }

    public List<OrderLineModel> getLinesForSetFirstElimination(Long idOrder) {
        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> students = orderReportDAO.getTransferEsoModel(idOrder);

        String foundation = students.size() == 0 ? "" : students.get(0).getFoundation();

        int studentNumber = 1;
        int descriptionNumber = 1;
        int prevCourse = 0;
        String prevDescription = "";
        String prevGroup = "";
        boolean governmentFinanced = true;
        String semesterText = "";

        for (OrderReportModel student : students) {

            if (student.getAdditional() != null && !student.getAdditional().equals("")) {
                JSONObject json = new JSONObject(student.getAdditional());
                if (json.has(OrderStudentJSONConst.SEMESTER) && json.has(OrderStudentJSONConst.SEMESTER)) {

                    Long idSemester = json.getLong(OrderStudentJSONConst.SEMESTER);

                    semesterText = orderReportDAO.getSemester(idSemester);
                }
            }

            if (governmentFinanced != student.getGovernmentFinanced()) {
                governmentFinanced = !governmentFinanced;
                descriptionNumber++;
                prevDescription = "";

                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
            }

            if (!prevDescription.equals(student.getDescription())) {
                prevDescription = student.getDescription();
                prevCourse = 0;
                prevGroup = "";
                studentNumber = 1;

                lines.add(new OrderLineModel(descriptionNumber + ". " + student.getDescription().replace("$sem$", semesterText),
                                             OrderLineType.RED_LINE_INDENT.getValue()
                ));
                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                lines.add(new OrderLineModel(governmentFinanced
                                             ? "за счет бюджетных ассигнований федерального бюджета"
                                             : "на условиях договора об оказании платных услуг", OrderLineType.NO_INDENT.getValue()));
            }

            if (prevCourse != student.getCourse()) {
                prevCourse = student.getCourse();
                prevGroup = "";
                studentNumber = 1;

                lines.add(new OrderLineModel(student.getCourse() + " курс", OrderLineType.NO_INDENT.getValue()));
            }
            if (!prevGroup.equals(student.getGroupname())) {
                if (!prevGroup.isEmpty()) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                prevGroup = student.getGroupname();
                studentNumber = 1;

                lines.add(new OrderLineModel("Группа " + student.getGroupname(), OrderLineType.NO_INDENT.getValue()));
            }

            lines.add(new OrderLineModel(studentNumber++ + ". (#" + student.getRecordbook() + ") " + student.getFio(),
                                         OrderLineType.RED_LINE_INDENT.getValue()
            ));
        }

        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("Прилагаемые документы: " + foundation, OrderLineType.RED_LINE_INDENT.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

        return lines;
    }

    public List<OrderLineModel> getLinesForTransferConditionally(Long idOrder) {
        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> students = orderReportDAO.getTransferEsoModel(idOrder);

        JSONObject attributes = new JSONObject(orderReportDAO.getOrderAttributes(idOrder));
        String year = attributes.has("attr1") ? attributes.getString("attr1") : "";

        students.forEach(el -> {
            el.setDescription(
                    el.getDescription().replace("$year$", year));
        });

        String prevDescription = "";

        int studentNumber = 1;
        int descriptionNumber = 1;
        int prevCourse = 0;
        String prevGroup = "";
        boolean governmentFinanced = true;

        for (OrderReportModel student : students) {

            if (governmentFinanced != student.getGovernmentFinanced()) {
                governmentFinanced = !governmentFinanced;
                descriptionNumber++;
                prevDescription = "";

                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
            }

            if (!prevDescription.equals(student.getDescription())) {
                prevDescription = student.getDescription();
                prevCourse = 0;
                prevGroup = "";
                studentNumber = 1;

                lines.add(
                        new OrderLineModel(descriptionNumber + ". " + student.getDescription(), OrderLineType.RED_LINE_INDENT.getValue()));
                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                lines.add(new OrderLineModel(governmentFinanced
                                             ? "За счет бюджетных ассигнований федерального бюджета"
                                             : "На условиях договора об оказании платных услуг", OrderLineType.NO_INDENT.getValue()));
            }

            if (prevCourse != student.getCourse()) {
                prevCourse = student.getCourse();
                prevGroup = "";
                studentNumber = 1;

                lines.add(new OrderLineModel("На " + (student.getCourse() + 1) + " курс", OrderLineType.NO_INDENT.getValue()));
            }

            if (!prevGroup.equals(student.getGroupname())) {
                if (!prevGroup.isEmpty()) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                prevGroup = student.getGroupname();
                studentNumber = 1;

                lines.add(new OrderLineModel("Группа " + student.getGroupname(), OrderLineType.NO_INDENT.getValue()));
            }

            lines.add(new OrderLineModel(studentNumber++ + ". (#" + student.getRecordbook() + ") " + student.getFio(),
                                         OrderLineType.RED_LINE_INDENT.getValue()
            ));
        }

        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

        return lines;
    }

    public List<OrderLineModel> getLinesForTransfer(Long idOrder) {
        List<OrderLineModel> lines = new ArrayList<>();

        List<OrderReportModel> students = orderReportDAO.getTransferEsoModel(idOrder);

        String prevDescription = "";

        int studentNumber = 1;
        int descriptionNumber = 1;
        int prevCourse = 0;
        String prevGroup = "";
        String semesterText = "";
        boolean governmentFinanced = true;

        for (OrderReportModel student : students) {

            if (student.getAdditional() != null && !student.getAdditional().equals("")) {
                JSONObject json = new JSONObject(student.getAdditional());
                if (json.has(OrderStudentJSONConst.SEMESTER) && json.has(OrderStudentJSONConst.SEMESTER)) {

                    Long idSemester = json.getLong(OrderStudentJSONConst.SEMESTER);

                    semesterText = orderReportDAO.getYear(idSemester);
                }
            }

            if (governmentFinanced != student.getGovernmentFinanced()) {
                governmentFinanced = !governmentFinanced;
                descriptionNumber++;
                prevDescription = "";

                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
            }

            if (!prevDescription.equals(student.getDescription())) {
                prevDescription = student.getDescription();
                prevCourse = 0;
                prevGroup = "";
                studentNumber = 1;

                lines.add(new OrderLineModel(descriptionNumber + ". " + student.getDescription().replace("$sem$", semesterText),
                                             OrderLineType.RED_LINE_INDENT.getValue()
                ));
                lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                lines.add(new OrderLineModel(governmentFinanced
                                             ? "За счет бюджетных ассигнований федерального бюджета"
                                             : "На условиях договора об оказании платных услуг", OrderLineType.NO_INDENT.getValue()));
            }

            if (prevCourse != student.getCourse()) {
                prevCourse = student.getCourse();
                prevGroup = "";
                studentNumber = 1;

                lines.add(new OrderLineModel("На " + (student.getCourse() + 1) + " курс", OrderLineType.NO_INDENT.getValue()));
            }

            if (!prevGroup.equals(student.getGroupname())) {
                if (!prevGroup.isEmpty()) {
                    lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
                }

                prevGroup = student.getGroupname();
                studentNumber = 1;

                lines.add(new OrderLineModel("Группа " + student.getGroupname(), OrderLineType.NO_INDENT.getValue()));
            }

            lines.add(new OrderLineModel(studentNumber++ + ". (#" + student.getRecordbook() + ") " + student.getFio(),
                                         OrderLineType.RED_LINE_INDENT.getValue()
            ));
        }

        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        lines.add(new OrderLineModel("", OrderLineType.EMPTY.getValue()));

        return lines;
    }
}
