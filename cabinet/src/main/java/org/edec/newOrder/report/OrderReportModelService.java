package org.edec.newOrder.report;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.synchroMine.model.mine.Student;
import org.edec.newOrder.model.dao.EmployeeOrderEsoModel;
import org.edec.newOrder.model.report.*;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.constants.OrderStudentJSONConst;
import org.edec.utility.constants.ScholarshipSizeConst;
import org.edec.utility.converter.DateConverter;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class OrderReportModelService {
    public static final String MAIN_ORDER = "main_order";
    public static final String PREDICATE_FIO = "predicate_fio";
    public static final String PREDICATE_POST = "predicate_post";
    public static final String EMPLOYEES = "employee";

    public static final String FORM_OF_STUDY = "$FOS$";
    public static final String FIRST_DATE = "$DATE1$";

    private OrderReportMainModel main;

    private OrderReportDAO orderReportDAO = new OrderReportDAO();

    private OrderReportMineDAO orderReportMineDAO = new OrderReportMineDAO();

    private Map<String, Object> getMainOrderMap(Long idOrder) {
        Map<String, Object> map = new HashMap<>();
        main = orderReportDAO.getMainOrderInfoByID(idOrder);

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
                employees.add(new OrderReportEmployeeModel(employee.getShortFio(), employee.getPost()));
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

    /* Получение данных по приказам */
    public JRBeanCollectionDataSource getBeanDataForAcademicIncreasedOrder(Long idOrder) {
        Map mainOrderMap = getMainOrderMap(idOrder);
        main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);

        List<OrderReportSectionModel> sections = new ArrayList<>();

        List<OrderReportModel> academicList = orderReportDAO.getAcademicIncreaseReportModel(idOrder);

        for (OrderReportModel academic : academicList) {
            boolean addSection = true;
            for (OrderReportSectionModel section : sections) {
                if (section.getDescription().equals(academic.getDescription())) {
                    setSubsectionForSectionForAcademicIncrease(section, academic);
                    addSection = false;
                    break;
                }
            }
            if (addSection) {
                OrderReportSectionModel section = createSectionForReport(academic.getDescription(), academic.getFoundation());
                setSubsectionForSectionForAcademicIncrease(section, academic);
                sections.add(section);
            }
        }
        // Сортировка подсекций
        for (OrderReportSectionModel section : sections) {
            if (section.getSubsections().size() == 3) {
                if (section.getSubsections().get(1).getDescription().equals("Обучающимся на \"отлично\".")) {
                    Collections.swap(section.getSubsections(), 0, 1);
                    if (section.getSubsections().get(1).getDescription().equals("Обучающимся на \"хорошо\".")) {
                        Collections.swap(section.getSubsections(), 1, 2);
                    }
                } else if (section.getSubsections().get(1).getDescription().equals("Обучающимся на \"хорошо\".")) {
                    Collections.swap(section.getSubsections(), 1, 2);
                    if (section.getSubsections().get(1).getDescription().equals("Обучающимся на \"отлично\".")) {
                        Collections.swap(section.getSubsections(), 0, 1);
                    }
                }
            } else if (section.getSubsections().size() == 2) {
                if (section.getSubsections().get(1).getDescription().equals("Обучающимся на \"отлично\".") ||
                    section.getSubsections().get(0).getDescription().equals("Обучающимся на \"хорошо и отлично\".")) {
                    Collections.swap(section.getSubsections(), 0, 1);
                }
            }
        }

        main.setSections(sections);

        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public JRBeanCollectionDataSource getBeanDataForMaterialSupport(Long idOrder) {
        Map mainOrderMap = getMainOrderMap(idOrder);
        main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);

        main.setPredicatingfio((String) mainOrderMap.get(PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(PREDICATE_POST));
        main.setEmployees((List<OrderReportEmployeeModel>) mainOrderMap.get(EMPLOYEES));

        List<OrderReportSectionModel> sections = new ArrayList<>();

        List<OrderReportModel> listMaterialSupportModels = orderReportDAO.getMaterialSupportModel(idOrder);

        String ids = listMaterialSupportModels.stream().filter(el -> el.getIdStudentMine() != null)
                                              .map(el -> el.getIdStudentMine().toString()).collect(Collectors.joining(","));

        List<OrderReportModel> listModelOnlyINN = orderReportMineDAO.getINN(ids);

        for (OrderReportModel model : listMaterialSupportModels) {
            Iterator itr = listModelOnlyINN.iterator();
            while (itr.hasNext()) {
                Object[] obj = (Object[]) itr.next();
                if (obj[1].equals(model.getIdStudentMine().intValue())) {
                    model.setINN(obj[0].toString().replaceAll("[^0-9]", ""));
                }
            }

            boolean addSection = true;
            for (OrderReportSectionModel section : sections) {
                if (section.getDescription().equals(model.getDescription())) {
                    setCourseForSectionForMaterialSupport(section, model);
                    addSection = false;
                    break;
                }
            }
            if (addSection) {
                OrderReportSectionModel section = createSectionForReport(model.getDescription(), model.getFoundation());
                section.setDescription(model.getDescription());
                setCourseForSectionForMaterialSupport(section, model);
                sections.add(section);
            }
        }

        for (OrderReportSectionModel section : sections) {
            Integer finalSum = 0;
            for (OrderReportCourseModel course : section.getCourses()) {
                for (OrderReportGroupModel group : course.getGroups()) {
                    for (OrderReportStudentModel student : group.getStudents()) {
                        finalSum += Integer.parseInt(student.getScholarship());
                    }
                }
            }
            section.setFoundation("Сумма по приказу: " + finalSum + " руб.");
        }
        main.setSections(sections);

        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public JRBeanCollectionDataSource getBeanDataForAcademicOrder(Long idOrder) {
        Map mainOrderMap = getMainOrderMap(idOrder);
        OrderReportMainModel main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);
        main.setPredicatingfio(null);

        List<OrderReportGroupModel> listGroup = new ArrayList<>();
        List<OrderReportModel> academicList = orderReportDAO.getAcademicModel(idOrder);
        for (OrderReportModel academic : academicList) {
            boolean addGroup = true;
            for (OrderReportGroupModel group : listGroup) {
                if (group.getGroupname().equals(academic.getGroupname())) {
                    setSection(academic, group);
                    addGroup = false;
                    break;
                }
            }
            if (addGroup) {
                OrderReportGroupModel newGroup = new OrderReportGroupModel();
                newGroup.setGroupname(academic.getGroupname());
                newGroup.setPredicatingfio((String) mainOrderMap.get(PREDICATE_FIO));
                newGroup.setPredicatingpost((String) mainOrderMap.get(PREDICATE_POST));
                newGroup.setEmployees((List<OrderReportEmployeeModel>) mainOrderMap.get(EMPLOYEES));
                setSection(academic, newGroup);
                listGroup.add(newGroup);
            }
        }
        main.setGroups(listGroup);

        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public JRBeanCollectionDataSource getBeanDataForDeductionOrder(Long idOrder) {
        Map mainOrderMap = getMainOrderMap(idOrder);
        OrderReportMainModel main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);
        List<OrderReportModel> listIndividual = orderReportDAO.getListIndividuals(idOrder);

        for (OrderReportModel student : listIndividual) {
            if (student.getAdditional() != null && !student.getAdditional().equals("")) {
                JSONObject additional = new JSONObject(student.getAdditional());

                if (additional.has(OrderStudentJSONConst.FOUNDATION)) {
                    student.setFoundation(additional.getString(OrderStudentJSONConst.FOUNDATION));
                }
            }
        }

        main.setIndividualsStudents(listIndividual);
        main.setPredicatingfio((String) mainOrderMap.get(PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(PREDICATE_POST));
        main.setEmployees((List<OrderReportEmployeeModel>) mainOrderMap.get(EMPLOYEES));

        main.setIdOrderRule(OrderRuleConst.DEDUCTION_INITIATIVE.getId());

        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public JRBeanCollectionDataSource getBeanDataForSocialOrder(Long idOrder) {
        Map mainOrderMap = getMainOrderMap(idOrder);
        OrderReportMainModel main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);

        main.setPredicatingfio((String) mainOrderMap.get(PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(PREDICATE_POST));
        main.setEmployees((List<OrderReportEmployeeModel>) mainOrderMap.get(EMPLOYEES));

        List<OrderReportModel> socialModels = orderReportDAO.getListSocial(idOrder);
        List<OrderReportSectionModel> sections = new ArrayList<>();

        for (OrderReportModel social : socialModels) {
            boolean addSection = true;
            for (OrderReportSectionModel section : sections) {
                if (section.getDescription().equals(social.getDescription())) {
                    setCourseForSectionForSocial(section, social);
                    addSection = false;
                    break;
                }
            }
            if (addSection) {
                OrderReportSectionModel section = createSectionForReport(social.getDescription(), social.getFoundation());
                section.setDescription(social.getDescription());
                section.setFoundation(social.getFoundation());
                setCourseForSectionForSocial(section, social);
                sections.add(section);
            }
        }
        main.setSections(sections);

        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public JRBeanCollectionDataSource getBeanDataForSocialIncreasedOrder(Long idOrder) {
        Map mainOrderMap = getMainOrderMap(idOrder);
        OrderReportMainModel main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);

        main.setPredicatingfio((String) mainOrderMap.get(PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(PREDICATE_POST));
        main.setEmployees((List<OrderReportEmployeeModel>) mainOrderMap.get(EMPLOYEES));

        List<OrderReportModel> socialIncreaseModels = orderReportDAO.getListSocialIncrease(idOrder);
        List<OrderReportSectionModel> sections = new ArrayList<>();

        for (OrderReportModel socialInc : socialIncreaseModels) {
            boolean addSection = true;
            for (OrderReportSectionModel section : sections) {
                if (section.getDescription().equals(socialInc.getDescription())) {
                    setSubsectionForSectionForSocialIncreased(section, socialInc);
                    addSection = false;
                    break;
                }
            }
            if (addSection) {
                OrderReportSectionModel section = createSectionForReport(socialInc.getDescription(), socialInc.getFoundation());
                section.setDescription(socialInc.getDescription());
                section.setFoundation(socialInc.getFoundation());
                setSubsectionForSectionForSocialIncreased(section, socialInc);
                sections.add(section);
            }
        }
        main.setSections(sections);
        for (OrderReportSectionModel section : main.getSections()) {
            if (section.getDescription().contains("Прекратить")) {
                main.setTypeorder(main.getTypeorder() + "<br>О прекращении выплаты государственной социальной стипендии");
            }
        }

        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public JRBeanCollectionDataSource getBeanDataForTransferOrder(Long idOrder) {
        Map mainOrderMap = getMainOrderMap(idOrder);
        main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);

        main.setPredicatingfio((String) mainOrderMap.get(PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(PREDICATE_POST));
        main.setEmployees((List<OrderReportEmployeeModel>) mainOrderMap.get(EMPLOYEES));

        List<OrderReportSectionModel> sections = new ArrayList<>();
        List<OrderReportModel> transferModels = orderReportDAO.getTransferEsoModel(idOrder);

        for (OrderReportModel transfer : transferModels) {
            if (transfer.getAdditional() != null && !transfer.getAdditional().equals("")) {
                JSONObject json = new JSONObject(transfer.getAdditional());
                if (json.has(OrderStudentJSONConst.ORDER_NUMBER) &&
                    json.has(OrderStudentJSONConst.ORDER_DATE)) {
                    Date datePrev = DateConverter
                            .convertStringToDate(json.getString(OrderStudentJSONConst.ORDER_DATE), "yyyy-MM-dd");
                    transfer.setPrevOrderDate(DateConverter.convertDateToString(datePrev));
                    transfer.setPrevOrderNum(json.getString(OrderStudentJSONConst.ORDER_NUMBER));
                    transfer.setDescription(transfer.getDescription().replace("$prevNum$", transfer.getPrevOrderNum()));
                    transfer.setDescription(transfer.getDescription().replace("$prevDate$", transfer.getPrevOrderDate()));
                }
            }

            boolean addSection = true;
            for (OrderReportSectionModel section : sections) {
                if (section.getDescription().equals(transfer.getDescription())) {
                    setCourseForSectionForTransfer(transfer, section);
                    addSection = false;
                    break;
                }
            }

            if (addSection) {
                OrderReportSectionModel section = createSectionForReport(transfer.getDescription(), transfer.getFoundation());
                section.setDescription(transfer.getDescription());
                section.setFoundation(transfer.getFoundation());
                setCourseForSectionForTransfer(transfer, section);
                sections.add(section);
            }
        }

        for (int i = 0; i < sections.size(); i++) {
            OrderReportSectionModel section = sections.get(i);
            OrderReportSectionModel nextSection = sections.size() != (i + 1) ? sections.get(i + 1) : null;

            if (nextSection == null) {
                continue;
            }
            if (section.getDescription().contains("уважител") && !nextSection.getDescription().contains("уважител")) {
                continue;
            }

            section.setFoundation(null);
        }

        main.setSections(sections);
        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public JRBeanCollectionDataSource getBeanDataForSetElimination(Long idOrder, OrderRuleConst orderRuleConst) {
        Map mainOrderMap = getMainOrderMap(idOrder);

        if (orderRuleConst.equals(OrderRuleConst.SET_ELIMINATION_RESPECTFUL)) {
            main = getBeanDataForReportWithPaymentDesc(idOrder, false);
        } else {
            main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);

            List<OrderReportSectionModel> sections = new ArrayList<>();
            List<OrderReportModel> setEliminationModels = orderReportDAO.getSetEliminationModel(idOrder);

            for (OrderReportModel model : setEliminationModels) {
                boolean addSection = true;

                for (OrderReportSectionModel section : sections) {
                    if (section.getDescription().equals(model.getDescription())) {
                        setCourseForSectionForSetElimination(model, section);
                        addSection = false;
                        break;
                    }
                }

                if (addSection) {
                    if (sections.size() > 0) {
                        if (sections.get(sections.size() - 1).getSubsections().get(1).getCourses().size() == 0) {
                            sections.get(sections.size() - 1).getSubsections().remove(1);
                        }

                        if (sections.get(sections.size() - 1).getSubsections().get(0).getCourses().size() == 0) {
                            sections.get(sections.size() - 1).getSubsections().remove(0);
                        }
                    }

                    OrderReportSectionModel section = new OrderReportSectionModel();
                    section.setDescription(model.getDescription());
                    section.setFoundation(model.getFoundation());
                    section.setSubsections(new ArrayList<>());

                    OrderReportSectionModel budgetSection = new OrderReportSectionModel();
                    budgetSection.setCourses(new ArrayList<>());
                    if (orderRuleConst.equals(OrderRuleConst.SET_ELIMINATION_RESPECTFUL)) {
                        budgetSection.setDescription(null);
                    } else {
                        budgetSection.setDescription("обучающимся за счет бюджетных ассигнований федерального бюджета:");
                    }
                    section.getSubsections().add(budgetSection);

                    OrderReportSectionModel paymentSection = new OrderReportSectionModel();
                    paymentSection.setCourses(new ArrayList<>());
                    paymentSection.setDescription("обучающимся на условиях договора об оказании платных образовательных услуг:");
                    section.getSubsections().add(paymentSection);

                    setCourseForSectionForSetElimination(model, section);

                    sections.add(section);
                }
            }

            if (sections.size() > 0) {
                if (sections.get(sections.size() - 1).getSubsections().get(1).getCourses().size() == 0) {
                    sections.get(sections.size() - 1).getSubsections().remove(1);
                }

                if (sections.get(sections.size() - 1).getSubsections().get(0).getCourses().size() == 0) {
                    sections.get(sections.size() - 1).getSubsections().remove(0);
                }
            }

            main.setSections(sections);
        }

        main.setPredicatingfio((String) mainOrderMap.get(PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(PREDICATE_POST));
        main.setEmployees((List<OrderReportEmployeeModel>) mainOrderMap.get(EMPLOYEES));

        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public OrderReportMainModel getBeanDataForReportWithPaymentDesc(Long idOrder, Boolean isForNotion) {
        Map mainOrderMap = getMainOrderMap(idOrder);
        main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);

        List<OrderReportSectionModel> sections = new ArrayList<>();
        List<OrderReportModel> setEliminationModels = orderReportDAO.getSetEliminationModel(idOrder);

        for (OrderReportModel model : setEliminationModels) {
            boolean addSection = true;

            String description = "Прошу установить срок ликвидации академических задолженностей по итогам осеннего семестра " +
                                 (model.getBeginYear().getYear() + 1900) + "-" + (model.getEndYear().getYear() + 1900) +
                                 " учебного года до " + DateConverter.convertDateToString(model.getFirstDateStudent()) +
                                 "г. следующим студентам очной формы обучения, " +
                                 " обучающимся за счет бюджетных ассигнований федерального бюджета:";

            for (OrderReportSectionModel section : sections) {
                if (section.getDescription().equals(isForNotion ? description : model.getDescription())) {
                    setCourseForSectionForSetElimination(model, section, model.getGovernmentFinanced() ? true : false);
                    addSection = false;
                    break;
                }
            }

            if (addSection) {
                OrderReportSectionModel section = new OrderReportSectionModel();
                section.setDescription(isForNotion ? description : model.getDescription());
                section.setFoundation(model.getFoundation());
                section.setSubsections(new ArrayList<>());
                setCourseForSectionForSetElimination(model, section, model.getGovernmentFinanced() ? true : false);
                sections.add(section);
            }
        }

        for (int i = 0; i < sections.size(); i++) {
            OrderReportSectionModel section = sections.get(i);
            OrderReportSectionModel nextSection = sections.size() != (i + 1) ? sections.get(i + 1) : null;

            if (nextSection == null) {
                continue;
            }
            if (section.getDescription().contains("уважител") && !nextSection.getDescription().contains("уважител")) {
                continue;
            }

            section.setFoundation(null);
        }

        main.setSections(sections);

        return main;
    }
    /* Получение данных по приказам */

    // TODO вынести в отдельный сервис
    public JRBeanCollectionDataSource getBeanDataForServiceNote(Long idOrder, String desc) {
        Map mainOrderMap = getMainOrderMap(idOrder);
        main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);

        main.setPredicatingfio((String) mainOrderMap.get(PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(PREDICATE_POST));
        main.setEmployees((List<OrderReportEmployeeModel>) mainOrderMap.get(EMPLOYEES));

        List<OrderReportSectionModel> sections = new ArrayList<>();
        List<OrderReportModel> transferModels = orderReportDAO.getTransferEsoModelForServiceNote(idOrder);

        for (OrderReportModel transfer : transferModels) {
            String formOfStudy = transfer.getDescription().toLowerCase().contains("договор")
                                 ? "на условиях договора об оказании платных образовательных услуг"
                                 : "за счет бюджетных ассигнований федерального бюджета";
            transfer.setDescription(desc.replace(FORM_OF_STUDY, formOfStudy)
                                        .replace(FIRST_DATE, new SimpleDateFormat("dd.MM.yyyy").format(transfer.getFirstDateStudent())));

            boolean addSection = true;
            for (OrderReportSectionModel section : sections) {
                if (section.getDescription().equals(transfer.getDescription())) {
                    setCourseForSectionForTransfer(transfer, section);
                    addSection = false;
                    break;
                }
            }

            if (addSection) {
                OrderReportSectionModel section = createSectionForReport(transfer.getDescription(), transfer.getFoundation());
                setCourseForSectionForTransfer(transfer, section);
                sections.add(section);
            }
        }

        for (int i = 0; i < sections.size(); i++) {
            OrderReportSectionModel section = sections.get(i);
            OrderReportSectionModel nextSection = sections.size() != (i + 1) ? sections.get(i + 1) : null;

            if (nextSection == null) {
                continue;
            }
            if (section.getDescription().contains("уважител") && !nextSection.getDescription().contains("уважител")) {
                continue;
            }

            section.setFoundation(null);
        }

        main.setSections(sections);
        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public JRBeanCollectionDataSource getBeanDataForEliminationNote(Long idOrder) {
        main = getBeanDataForReportWithPaymentDesc(idOrder, true);

        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public JRBeanCollectionDataSource getBeanDataForMaterialSupportProtocol(Long idOrder, String dateOfBegin, String dateOfEnd) {
        List<OrderReportModel> listMaterialSupportModels = orderReportDAO.getMaterialSupportModel(idOrder);
        List<ProtocolStudentModel> protocolStudentModelList = new ArrayList<>();

        int finalAmount = 0;
        for (OrderReportModel orderReportModel : listMaterialSupportModels) {
            ProtocolStudentModel protocolStudentModel = new ProtocolStudentModel();
            protocolStudentModel.setFio(orderReportModel.getFio());
            protocolStudentModel.setGroupname(orderReportModel.getGroupname());
            protocolStudentModel.setAmount(orderReportModel.getScholarship());
            protocolStudentModel.setDateOfBegin(dateOfBegin);
            protocolStudentModel.setDateOfEnd(dateOfEnd);
            finalAmount += Integer.valueOf(orderReportModel.getScholarship());
            if (orderReportModel.getTypeScholarship().equals(0)) {
                protocolStudentModel.setType("единовременная");
            } else if (orderReportModel.getTypeScholarship().equals(1)) {
                protocolStudentModel.setType("ежемесячная");
            }
            protocolStudentModelList.add(protocolStudentModel);
        }

        List<ProtocolModel> protocolModelList = new ArrayList<>();
        ProtocolModel protocolModel = new ProtocolModel();
        protocolModel.setFinalAmount(String.valueOf(finalAmount));
        protocolModel.setStudentList(protocolStudentModelList);
        protocolModelList.add(protocolModel);
        return new JRBeanCollectionDataSource(protocolModelList);
    }

    /* Создание сущностей для академической стипендии */
    private void setSection(OrderReportModel academic, OrderReportGroupModel group) {
        boolean addSection = true;
        for (OrderReportSectionModel section : group.getSections()) {
            if (section.getProlongation() == academic.getProlongation()) {
                setSubsection(academic, section);
                addSection = false;
                break;
            }
        }
        if (addSection) {
            OrderReportSectionModel newSection = new OrderReportSectionModel();
            newSection.setDescription(academic.getDescription());
            newSection.setProlongation(academic.getProlongation());
            setSubsection(academic, newSection);
            group.getSections().add(newSection);
        }
    }

    private void setSubsection(OrderReportModel academic, OrderReportSectionModel section) {
        boolean addSubSection = true;
        for (OrderReportSectionModel subSection : section.getSubsections()) {
            if (subSection.getDescription().equals(academic.getSubDescription())) {
                subSection.getStudents().add(new OrderReportStudentModel(academic.getFio(), academic.getRecordbook()));
                addSubSection = false;
                break;
            }
        }
        if (addSubSection) {
            OrderReportSectionModel newSubSection = new OrderReportSectionModel();
            newSubSection.setDescription(academic.getSubDescription());
            newSubSection.getStudents().add(new OrderReportStudentModel(academic.getFio(), academic.getRecordbook()));
            section.getSubsections().add(newSubSection);
        }
    }

    /* Создание Курса для подсекции */
    private void setCourseForSubSectionForAcademicIncreased(OrderReportSectionModel subSection, OrderReportModel academic) {
        boolean addCourse = true;
        for (OrderReportCourseModel course : subSection.getCourses()) {
            if (course.getCourse() - academic.getCourse() == 0) {
                setGroupForCourse(course, academic);
                addCourse = false;
                break;
            }
        }
        if (addCourse) {
            OrderReportCourseModel course = createCourseForReport(academic.getCourse());
            setGroupForCourse(course, academic);
            subSection.getCourses().add(course);
        }
    }

    private void setCourseForSubSectionForSocialIncreased(OrderReportSectionModel subSection, OrderReportModel socialInc) {
        boolean addCourse = true;
        for (OrderReportCourseModel course : subSection.getCourses()) {
            if (course.getCourse() - socialInc.getCourse() == 0) {
                setGroupForCourse(course, socialInc);
                addCourse = false;
                break;
            }
        }
        if (addCourse) {
            OrderReportCourseModel course = new OrderReportCourseModel();
            course.setCourse(socialInc.getCourse());
            setGroupForCourse(course, socialInc);
            subSection.getCourses().add(course);
        }
    }

    /* Создание секции для подсекции */
    private void setSubsectionForSectionForAcademicIncrease(OrderReportSectionModel section, OrderReportModel academicInc) {
        boolean addSubSection = true;
        for (OrderReportSectionModel subSection : section.getSubsections()) {
            if (subSection.getDescription().equals("Обучающимся на \"" + academicInc.getSessionresult() + "\".")) {
                setCourseForSubSectionForAcademicIncreased(subSection, academicInc);
                addSubSection = false;
                break;
            }
        }
        if (addSubSection) {
            OrderReportSectionModel subSection = new OrderReportSectionModel();
            subSection.setDescription("Обучающимся на \"" + academicInc.getSessionresult() + "\".");
            setCourseForSubSectionForAcademicIncreased(subSection, academicInc);
            section.getSubsections().add(subSection);
        }
    }

    private void setSubsectionForSectionForSocialIncreased(OrderReportSectionModel section, OrderReportModel socialInc) {
        boolean addSubSection = true;
        for (OrderReportSectionModel subSection : section.getSubsections()) {
            if (subSection.getDescription().equals(socialInc.getDescriptionDate())) {
                setCourseForSubSectionForSocialIncreased(subSection, socialInc);
                addSubSection = false;
                break;
            }
        }
        if (addSubSection) {
            OrderReportSectionModel subSection = new OrderReportSectionModel();
            subSection.setDescription(socialInc.getDescriptionDate());
            setCourseForSubSectionForSocialIncreased(subSection, socialInc);
            section.getSubsections().add(subSection);
        }
    }

    /* Создание курса для секции */
    private void setCourseForSectionForTransfer(OrderReportModel transfer, OrderReportSectionModel section) {
        boolean addCourse = true;
        for (OrderReportCourseModel course : section.getCourses()) {
            if (course.getCourse() - transfer.getCourse() == 0) {
                setGroupForCourse(course, transfer);
                addCourse = false;
                break;
            }
        }
        if (addCourse) {
            OrderReportCourseModel course = new OrderReportCourseModel();
            course.setCourse(transfer.getCourse());

            if (main.getIdOrderRule().equals(OrderRuleConst.TRANSFER_PROLONGATION.getId())) {
                course.setFullcourse(transfer.getCourse() + " курс");
            }

            if (main.getIdOrderRule().equals(OrderRuleConst.TRANSFER_AFTER_TRANSFER_CONDITIONALLY_RESPECTFUL.getId()) ||
                main.getIdOrderRule().equals(OrderRuleConst.TRANSFER_AFTER_TRANSFER_CONDITIONALLY_NOT_RESPECTFUL.getId())) {
                course.setFullcourse("на " + transfer.getCourse() + " курсе");
            }

            setGroupForCourse(course, transfer);
            section.getCourses().add(course);
        }
    }

    private void setCourseForSectionForSocial(OrderReportSectionModel section, OrderReportModel social) {
        boolean addCourse = true;
        for (OrderReportCourseModel course : section.getCourses()) {
            if (course.getCourse() - social.getCourse() == 0) {
                setGroupForCourse(course, social);
                addCourse = false;
                break;
            }
        }
        if (addCourse) {
            OrderReportCourseModel course = new OrderReportCourseModel();
            course.setCourse(social.getCourse());
            course.setFullcourse(social.getCourse() + " курс");
            setGroupForCourse(course, social);
            section.getCourses().add(course);
        }
    }

    private void setCourseForSectionForMaterialSupport(OrderReportSectionModel section, OrderReportModel materialSupport) {
        boolean addCourse = true;
        for (OrderReportCourseModel course : section.getCourses()) {
            if (course.getCourse() - materialSupport.getCourse() == 0) {
                setGroupForCourse(course, materialSupport);
                addCourse = false;
                break;
            }
        }
        if (addCourse) {
            OrderReportCourseModel course = new OrderReportCourseModel();
            course.setCourse(materialSupport.getCourse());
            setGroupForCourse(course, materialSupport);
            section.getCourses().add(course);
        }
    }

    private void setCourseForSectionForSetElimination(OrderReportModel model, OrderReportSectionModel section, boolean governmentFinanced) {
        boolean addCourse = true;
        for (OrderReportCourseModel course : section.getCourses()) {
            if (course.getCourse() - model.getCourse() == 0) {
                setGroupForCourse(course, model);
                addCourse = false;
                break;
            }
        }
        if (addCourse) {
            OrderReportCourseModel course = new OrderReportCourseModel();
            course.setCourse(model.getCourse());

            course.setFullcourse("Курс " + model.getCourse());

            setGroupForCourse(course, model);
            section.getCourses().add(course);
        }
    }

    private void setCourseForSectionForSetElimination(OrderReportModel model, OrderReportSectionModel section) {
        boolean addCourse = true;
        if (section.getSubsections().size() != 0) {
            if (model.getGovernmentFinanced()) {
                for (OrderReportCourseModel course : section.getSubsections().get(0).getCourses()) {
                    if (course.getCourse() - model.getCourse() == 0) {
                        setGroupForCourse(course, model);
                        addCourse = false;
                        break;
                    }
                }
            } else {
                for (OrderReportCourseModel course : section.getSubsections().get(1).getCourses()) {
                    if (course.getCourse() - model.getCourse() == 0) {
                        setGroupForCourse(course, model);
                        addCourse = false;
                        break;
                    }
                }
            }
        }

        if (addCourse) {
            OrderReportCourseModel course = new OrderReportCourseModel();
            course.setCourse(model.getCourse());

            course.setFullcourse("Курс " + model.getCourse());

            setGroupForCourse(course, model);

            if (model.getGovernmentFinanced()) {
                section.getSubsections().get(0).getCourses().add(course);
            } else {
                section.getSubsections().get(1).getCourses().add(course);
            }
        }
    }

    /* Создание группы для курса */
    private void setGroupForCourse(OrderReportCourseModel course, OrderReportModel orderReportModel) {
        boolean addGroup = true;
        for (OrderReportGroupModel group : course.getGroups()) {
            if (group.getGroupname().equals(orderReportModel.getGroupname())) {
                setStudentForGroup(group, orderReportModel);
                addGroup = false;
                break;
            }
        }
        if (addGroup) {
            OrderReportGroupModel group = createGroupForReport(orderReportModel.getGroupname());
            setStudentForGroup(group, orderReportModel);
            course.getGroups().add(group);
        }
    }

    /* Создание сущности студента для группы */
    private void setStudentForGroup(OrderReportGroupModel group, OrderReportModel orderReportModel) {
        OrderReportStudentModel student = new OrderReportStudentModel();
        student.setFio(orderReportModel.getFio());

        if (orderReportModel.getINN() != null) {
            student.setScholarship(orderReportModel.getScholarship());
            student.setINN(orderReportModel.getINN());
        }

        //Назначение стипендии если соц приказ
        if (orderReportModel.getSirota() != null && orderReportModel.getSirota()) {
            if (main.getIdOrderRule() == OrderRuleConst.SOCIAL_INCREASED_IN_SESSION.getId() ||
                main.getIdOrderRule() == OrderRuleConst.SOCIAL_INCREASED_NEW_REFERENCE.getId()) {
                if (orderReportModel.getIdOrderSection() != null &&
                    (orderReportModel.getIdOrderSection() == 59 || orderReportModel.getIdOrderSection() == 61)) {
                    student.setScholarship(ScholarshipSizeConst.SOCIAL_INCREASED_FOR_ORPHAN.getSize());
                }
            } else {
                student.setScholarship(ScholarshipSizeConst.SOCIAL_FOR_ORPHAN.getSize());
            }
        } else if ((orderReportModel.getIndigent() != null && orderReportModel.getIndigent()) ||
                   (orderReportModel.getInvalid() != null && orderReportModel.getInvalid())) {
            if ((main.getIdOrderRule() == OrderRuleConst.SOCIAL_INCREASED_IN_SESSION.getId() ||
                 main.getIdOrderRule() == OrderRuleConst.SOCIAL_INCREASED_NEW_REFERENCE.getId())) {
                if (orderReportModel.getIdOrderSection() != null &&
                    (orderReportModel.getIdOrderSection() == 59 || orderReportModel.getIdOrderSection() == 61)) {
                    student.setScholarship(ScholarshipSizeConst.SOCIAL_INCREASED.getSize());
                }
            } else {
                student.setScholarship(ScholarshipSizeConst.SOCIAL.getSize());
            }
        }

        student.setRecordbook(orderReportModel.getRecordbook());
        group.getStudents().add(student);
    }

    /* Создание сущностей для отчетов */
    private OrderReportSectionModel createSectionForReport(String description, String foundation) {
        OrderReportSectionModel section = new OrderReportSectionModel();
        section.setDescription(description);
        section.setFoundation(foundation);
        return section;
    }

    private OrderReportGroupModel createGroupForReport(String groupname) {
        OrderReportGroupModel group = new OrderReportGroupModel();
        group.setGroupname(groupname);
        return group;
    }

    private OrderReportCourseModel createCourseForReport(Integer course) {
        OrderReportCourseModel courseModel = new OrderReportCourseModel();
        courseModel.setCourse(course);
        return courseModel;
    }
}
