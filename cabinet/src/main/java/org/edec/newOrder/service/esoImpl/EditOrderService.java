package org.edec.newOrder.service.esoImpl;

import lombok.extern.log4j.Log4j;
import org.edec.newOrder.manager.EditOrderManagerESO;
import org.edec.newOrder.model.addStudent.LinkOrderSectionEditModel;
import org.edec.newOrder.model.addStudent.SearchGroupModel;
import org.edec.newOrder.model.addStudent.SearchStudentModel;
import org.edec.newOrder.model.editOrder.GroupModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.SectionModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.dao.OrderModelESO;
import org.edec.newOrder.report.OrderLineService;
import org.edec.newOrder.report.service.OrderReportFillService;
import org.edec.rest.ctrl.OrderRestCtrl;
import org.edec.studentPassport.model.dao.RatingEsoModel;
import org.edec.utility.component.model.RatingModel;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.constants.OrderStatusConst;
import org.edec.utility.constants.OrderStudentJSONConst;
import org.edec.utility.constants.OrderTypeConst;
import org.edec.utility.httpclient.manager.HttpClient;
import org.edec.utility.zk.PopupUtil;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Log4j
public class EditOrderService {
    private EditOrderManagerESO managerOrderESO = new EditOrderManagerESO();
    OrderLineService orderLineService = new OrderLineService();
    OrderReportFillService orderReportFillService = new OrderReportFillService();
    private SimpleDateFormat formatYYYY = new SimpleDateFormat("YYYY");

    public void fillOrderModel(OrderEditModel orderModel) {
        List<OrderModelESO> listModelsESO = managerOrderESO.getListOrderModelESO(orderModel.getIdOrder().longValue());

        switch (OrderTypeConst.getByType(orderModel.getOrderType())) {
            case ACADEMIC:
                OrderRuleConst orderRuleConst = OrderRuleConst.getById(orderModel.getIdOrderRule());
                if (orderRuleConst.equals(OrderRuleConst.CANCEL_ACADEMICAL_SCHOLARSHIP_IN_SESSION) ||
                    orderRuleConst.equals(OrderRuleConst.CANCEL_SCHOLARSHIP_AFTER_PRACTICE) ||
                    orderRuleConst.equals(OrderRuleConst.ACADEMIC_INDIVIDUAL) ||
                    orderRuleConst.equals(OrderRuleConst.ACADEMIC_FIRST_COURSE)) {
                    groupOrderBySections(orderModel, listModelsESO);
                } else {
                    groupOrderByGroups(orderModel, listModelsESO);
                }
                break;
            case ACADEMIC_INCREASED:
            case MATERIAL_SUPPORT:
            case DEDUCTION:
            case SOCIAL:
            case SOCIAL_INCREASED:
            case TRANSFER:
            case SET_ELIMINATION_DEBTS:
                groupOrderBySections(orderModel, listModelsESO);
                break;
        }
    }

    private void groupOrderByGroups(OrderEditModel orderModel, List<OrderModelESO> listModelsESO) {
        orderModel.setGroups(new ArrayList<>());
        for (OrderModelESO item : listModelsESO) {
            GroupModel groupToAdd = new GroupModel();
            SectionModel sectionToAdd = new SectionModel();

            boolean isExistGroupInModel = false;
            for (GroupModel group : orderModel.getGroups()) {
                if (group.getName().equals(item.getGroupname())) {
                    isExistGroupInModel = true;
                    groupToAdd = group;
                }
            }

            if (!isExistGroupInModel) {
                groupToAdd = new GroupModel();
                fillGroupInformation(groupToAdd, item);
                orderModel.getGroups().add(groupToAdd);
            }

            boolean isExistSectionInModel = false;
            for (SectionModel section : groupToAdd.getSections()) {
                if (section.getName().equals(item.getSectionname())) {
                    isExistSectionInModel = true;
                    sectionToAdd = section;
                }
            }

            if (!isExistSectionInModel) {
                sectionToAdd = new SectionModel();
                fillSectionInformation(sectionToAdd, item);
                groupToAdd.getSections().add(sectionToAdd);
            }

            StudentModel studentToAdd = new StudentModel();
            transformOrderModelESOToStudentModel(item, studentToAdd);

            sectionToAdd.getStudentModels().add(studentToAdd);
        }
    }

    private void groupOrderBySections(OrderEditModel orderModel, List<OrderModelESO> listModelsESO) {
        orderModel.setSections(new ArrayList<>());
        for (OrderModelESO item : listModelsESO) {
            GroupModel groupToAdd = new GroupModel();
            SectionModel sectionToAdd = new SectionModel();

            boolean isExistSectionInModel = false;
            for (SectionModel section : orderModel.getSections()) {
                if (section.getName().equals(item.getSectionname())) {
                    isExistSectionInModel = true;
                    sectionToAdd = section;
                }
            }

            if (!isExistSectionInModel) {
                sectionToAdd = new SectionModel();
                fillSectionInformation(sectionToAdd, item);
                orderModel.getSections().add(sectionToAdd);
            }

            boolean isExistGroupInModel = false;
            for (GroupModel group : sectionToAdd.getGroups()) {
                if (group.getName().equals(item.getGroupname())) {
                    isExistGroupInModel = true;
                    groupToAdd = group;
                }
            }

            if (!isExistGroupInModel) {
                groupToAdd = new GroupModel();
                fillGroupInformation(groupToAdd, item);
                sectionToAdd.getGroups().add(groupToAdd);
            }

            StudentModel studentToAdd = new StudentModel();
            transformOrderModelESOToStudentModel(item, studentToAdd);

            groupToAdd.getStudentModels().add(studentToAdd);
        }
    }

    private void fillSectionInformation(SectionModel section, OrderModelESO item) {
        section.setName(item.getSectionname());
        section.setFoundation(item.getFoundation());
        section.setFoundationLos(item.getFoundationLos());
        section.setStudentModels(new ArrayList<>());
        section.setId(item.getIdSection());
        section.setIdOS(item.getIdOS());
        section.setFirstDate(item.getFirstDateSection());
        section.setSecondDate(item.getSecondDateSection());
    }

    private void fillGroupInformation(GroupModel group, OrderModelESO item) {
        group.setName(item.getGroupname());
        group.setSections(new ArrayList<>());
    }

    public void transformOrderModelESOToStudentModel(OrderModelESO orderModelESO, StudentModel studentModel) {
        studentModel.setId(orderModelESO.getIdStudent());
        studentModel.setFio(orderModelESO.getFio());
        studentModel.setFirstDate(orderModelESO.getFirstDate());
        studentModel.setSecondDate(orderModelESO.getSecondDate());
        studentModel.setThirdDate(orderModelESO.getThirdDate());
        studentModel.setRecordnumber(orderModelESO.getRecordbook());
        studentModel.setGroupname(orderModelESO.getGroupname());
        studentModel.setAdditional(orderModelESO.getAdditionalInfo());
        studentModel.setIdMine(orderModelESO.getIdMine());

        if (orderModelESO.getAdditionalInfo() != null && !orderModelESO.getAdditionalInfo().equals("")) {
            JSONObject additionalInfo = new JSONObject(orderModelESO.getAdditionalInfo());

            if (additionalInfo.has(OrderStudentJSONConst.ORDER_NUMBER)) {
                if (additionalInfo.getString(OrderStudentJSONConst.ORDER_DATE) != null) {
                    try {
                        Date date = new SimpleDateFormat("yyyy-MM-dd")
                                .parse(additionalInfo.getString(OrderStudentJSONConst.ORDER_DATE));
                        studentModel.setDatePrevOrder(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (additionalInfo.has(OrderStudentJSONConst.ORDER_NUMBER)) {
                if (additionalInfo.getString(OrderStudentJSONConst.ORDER_NUMBER) != null) {
                    studentModel.setNumberPrevOrder(additionalInfo.getString(OrderStudentJSONConst.ORDER_NUMBER));
                }
            }

            if (additionalInfo.has(OrderStudentJSONConst.FOUNDATION)) {
                if (additionalInfo.getString(OrderStudentJSONConst.FOUNDATION) != null) {
                    studentModel.setFoundation(additionalInfo.getString(OrderStudentJSONConst.FOUNDATION));
                }
            }

            if (additionalInfo.has(OrderStudentJSONConst.NOMINATION)) {
                if (additionalInfo.getString(OrderStudentJSONConst.NOMINATION) != null) {
                    studentModel.setNomination(additionalInfo.getString(OrderStudentJSONConst.NOMINATION));
                }
            }
        }
    }

    public void saveFoundation(Long id, String foundation) {
        new EditOrderManagerESO().saveFoundation(id, foundation);

        orderLineService.updateOrderLines(id, orderReportFillService.getLines(id));
    }

    public void saveFoundationStudent(StudentModel student, String foundation) {
        JSONObject object;
        if (student.getAdditional() == null || student.getAdditional().equals("")) {
            object = new JSONObject();
        } else {
            object = new JSONObject(student.getAdditional());
        }

        object.put(OrderStudentJSONConst.FOUNDATION, foundation);
        student.setAdditional(object.toString());

        new EditOrderManagerESO().saveFoundationStudent(student.getId(), object.toString());

        long idOrder = new EditOrderManagerESO().getIdOrder(student.getId());

        orderLineService.updateOrderLines(idOrder, orderReportFillService.getLines(idOrder));
    }

    public List<RatingModel> getMarksStudent(Long idLoss, boolean isDebt) {
        managerOrderESO = new EditOrderManagerESO();
        return divideEsoModelForRatingModel(managerOrderESO.getMarksForStudentInOrder(idLoss), isDebt);
    }

    private List<RatingModel> divideEsoModelForRatingModel(List<RatingEsoModel> listEsoModel, boolean debt) {
        final List<RatingModel> result = new ArrayList<>();
        List<RatingModel> examList = new ArrayList<>();
        List<RatingModel> passList = new ArrayList<>();
        List<RatingModel> cpList = new ArrayList<>();
        List<RatingModel> cwList = new ArrayList<>();
        List<RatingModel> practicList = new ArrayList<>();

        for (RatingEsoModel esoModel : listEsoModel) {
            if (esoModel.getExam() && (!debt || (debt && esoModel.getExamrating() < 3))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getExamrating());
                ratingModel.setFoc("Экзамен");
                examList.add(ratingModel);
            }
            if (esoModel.getPass() && (!debt || (debt && (esoModel.getPassrating() == 2 || esoModel.getPassrating() < 1)))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getPassrating());
                ratingModel.setFoc("Зачет");
                passList.add(ratingModel);
            }
            if (esoModel.getCp() && (!debt || (debt && esoModel.getCprating() < 3))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getCprating());
                ratingModel.setFoc("КП");
                cpList.add(ratingModel);
            }
            if (esoModel.getCw() && (!debt || (debt && esoModel.getCwrating() < 3))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getCwrating());
                ratingModel.setFoc("КР");
                cwList.add(ratingModel);
            }
            if (esoModel.getPractic() && (!debt || (debt && esoModel.getPracticrating() < 3))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getPracticrating());
                ratingModel.setFoc("Практика");
                practicList.add(ratingModel);
            }
        }

        result.addAll(examList);
        result.addAll(passList);
        result.addAll(cpList);
        result.addAll(cwList);
        result.addAll(practicList);

        Collections.sort(result, (o1, o2) -> {
            try {
                if (formatYYYY.parse(o1.getSemester()).compareTo(formatYYYY.parse(o2.getSemester())) == 0) {
                    return o1.getSemester().compareTo(o2.getSemester());
                }
                return formatYYYY.parse(o2.getSemester()).compareTo(formatYYYY.parse(o1.getSemester()));
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });

        return result;
    }

    private RatingModel getRatingModelByEsoModel(RatingEsoModel esoModel) {
        RatingModel ratingModel = new RatingModel();
        ratingModel.setSemester(esoModel.getSemester());
        ratingModel.setSubjectname(esoModel.getSubjectname());
        return ratingModel;
    }

    public List<LinkOrderSectionEditModel> getSectionsFromOrder(long idOrder) {
        return managerOrderESO.getLinkOrderSections(idOrder).stream()
                              .map(los -> new LinkOrderSectionEditModel(los.getIdLOS(), los.getIdOS(), los.getName()))
                              .collect(Collectors.toList());
    }

    public List<SearchGroupModel> getGroupsForSearch(long idOrder) {
        return managerOrderESO.getGroupsForOrderSearch(idOrder).stream().map(group -> new SearchGroupModel(group.getId(), group.getName()))
                              .collect(Collectors.toList());
    }

    public List<SearchStudentModel> getStudentsForSearch(String surname, String name, String patronymic, String groupname, long idOrder) {
        return managerOrderESO.getStudents(surname, name, patronymic, groupname, idOrder).stream()
                              .map(student -> new SearchStudentModel(student.getSurname(), student.getName(), student.getPatronymic(),
                                                                     student.getNameGroup(), student.getIdSSS(),
                                                                     student.getNumberSemester(), student.getIdMine()
                              )).collect(Collectors.toList());
    }

    public void deleteStudents(StudentModel student) {
        long idOrder = new EditOrderManagerESO().getIdOrder(student.getId());

        managerOrderESO.removeStudentFromOrder(student.getId());

        orderLineService.updateOrderLines(idOrder, orderReportFillService.getLines(idOrder));
    }

    public void updateOrderInfo(OrderEditModel orderEditModel) {
        managerOrderESO.updateOrderInfo(orderEditModel);
    }

    public void updateStudentsForAcademicIncreased(Long idOrder) {
        managerOrderESO.updateStudentOrderInfo("academicIncreasedOrder", idOrder);
    }

    public void syncOrder(Long idOrder) {
        /*
        JSONObject jsonIdOrder = new JSONObject();
        jsonIdOrder.put("idOrderRequest", idOrder);

        JSONObject jsonOrder = new JSONObject(
                HttpClient.makeHttpRequest("http://localhost:8081/sync/order", HttpClient.POST, new ArrayList<>(), jsonIdOrder.toString()));

        if (jsonOrder.has("status")) {
            if (jsonOrder.get("status").equals("error")) {
                log.error("Синхронизация приказа в Шахтах c id = " + idOrder + " не удалось!");
                PopupUtil.showError("Синхронизация приказа не удалась!");
                return;
            } else {
                PopupUtil.showInfo("Приказ был успешно синхронизирован!");
                managerOrderESO.updateOrderStatus(idOrder, OrderStatusConst.SYNCHED);
            }
        }
        */

        // ВАЖНО - заглушка просто меняющая статус
        // Процесс разноса перенесен в 1С
        PopupUtil.showInfo("Приказ был успешно синхронизирован!");
        managerOrderESO.updateOrderStatus(idOrder, OrderStatusConst.SYNCHED);
    }

    public void exportProject(Long idOrder) {
        JSONObject jsonIdOrder = new JSONObject();
        jsonIdOrder.put("idOrderRequest", idOrder.toString());
        try {
            JSONObject jsonOrder = new JSONObject(HttpClient.makeHttpRequest("http://localhost:8081/sync/project", HttpClient.POST,
                    new ArrayList<>(), jsonIdOrder.toString()
            ));

            if (jsonOrder.has("status") && jsonOrder.get("status").equals("success")) {
                new OrderRestCtrl().getStatus(idOrder);
                PopupUtil.showInfo("Проект приказа был успешно экспортирован в Шахты!");
                managerOrderESO.updateOrderStatus(idOrder, OrderStatusConst.EXPORTED);
            } else {
                PopupUtil.showError("Экспорт проекта приказа не удался, обратитесь к администраторам!");
                if (jsonOrder.has("message") ) {
                    System.out.println(">>>" + jsonOrder.get("message"));
                }
            }
        } catch (Exception e) {
            PopupUtil.showError("Экспорт проекта приказа не удался, обратитесь к администраторам!");
            e.printStackTrace();
        }

    }
}
