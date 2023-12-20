package org.edec.order.service.impl;

import lombok.extern.log4j.Log4j;
import org.edec.newOrder.manager.EditOrderManagerESO;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.order.manager.EntityManagerOrderESO;
import org.edec.order.model.*;
import org.edec.utility.component.model.RatingModel;
import org.edec.order.model.StudentModel;
import org.edec.order.model.dao.OrderModelESO;
import org.edec.order.service.EditOrderService;
import org.edec.studentPassport.model.dao.RatingEsoModel;
import org.edec.utility.constants.OrderStatusConst;
import org.edec.utility.constants.OrderStudentJSONConst;
import org.edec.utility.constants.OrderTypeConst;
import org.edec.utility.httpclient.manager.HttpClient;
import org.edec.utility.zk.PopupUtil;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Log4j
public class EditOrderServiceESO implements EditOrderService {
    OrderModel orderModel;

    private SimpleDateFormat formatYYYY = new SimpleDateFormat("YYYY");

    @Override
    public void fillOrderModel (OrderModel orderModel) {
        EntityManagerOrderESO managerOrderESO = new EntityManagerOrderESO();
        List<OrderModelESO> listModelsESO = managerOrderESO.getListOrderModelESO(orderModel.getIdOrder().longValue());

        switch (OrderTypeConst.getByType(orderModel.getOrderType())) {
            case ACADEMIC:
                fillOrderModelForAcademical(orderModel, listModelsESO);
                break;
            case DEDUCTION:
                fillOrderModelForDeduction(orderModel, listModelsESO);
                break;
            case SOCIAL:
                fillOrderModelForSocial(orderModel, listModelsESO);
                break;
            case SOCIAL_INCREASED:
                fillOrderModelForSocialIncreased(orderModel, listModelsESO);
                break;
            case TRANSFER:
                fillOrderModelForTransfer(orderModel, listModelsESO);
                break;
            case SET_ELIMINATION_DEBTS:
                fillOrderModelForSetEliminationDebts(orderModel, listModelsESO);
                break;
        }
    }

    public void fillOrderModelForAcademical (OrderModel orderModel, List<OrderModelESO> listModelsESO) {
        orderModel.setGroups(new ArrayList<GroupModel>());
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
                groupToAdd.setName(item.getGroupname());
                groupToAdd.setSections(new ArrayList<SectionModel>());
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
                sectionToAdd.setName(item.getSectionname());
                sectionToAdd.setFoundation(item.getFoundation());
                sectionToAdd.setStudentModels(new ArrayList<StudentModel>());
                sectionToAdd.setId(item.getIdSection());
                sectionToAdd.setFirstDate(item.getFirstDateSection());
                sectionToAdd.setSecondDate(item.getSecondDateSection());
                groupToAdd.getSections().add(sectionToAdd);
            }

            StudentModel studentToAdd = new StudentModel();
            transformOrderModelESOToStudentModel(item, studentToAdd);

            sectionToAdd.getStudentModels().add(studentToAdd);
        }
    }

    public void fillOrderModelForDeduction (OrderModel orderModel, List<OrderModelESO> listModelsESO) {
        orderModel.setSections(new ArrayList<SectionModel>());
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
                sectionToAdd.setName(item.getSectionname());
                sectionToAdd.setFoundation(item.getFoundation());
                sectionToAdd.setStudentModels(new ArrayList<StudentModel>());
                sectionToAdd.setId(item.getIdSection());
                sectionToAdd.setFirstDate(item.getFirstDateSection());
                sectionToAdd.setSecondDate(item.getSecondDateSection());

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
                groupToAdd.setName(item.getGroupname());
                groupToAdd.setSections(new ArrayList<SectionModel>());
                sectionToAdd.getGroups().add(groupToAdd);
            }

            StudentModel studentToAdd = new StudentModel();
            transformOrderModelESOToStudentModel(item, studentToAdd);

            groupToAdd.getStudentModels().add(studentToAdd);
        }
    }

    public void fillOrderModelForTransfer (OrderModel orderModel, List<OrderModelESO> listModelsESO) {
        orderModel.setSections(new ArrayList<SectionModel>());
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
                sectionToAdd.setName(item.getSectionname());
                sectionToAdd.setFoundation(item.getFoundation());
                sectionToAdd.setFoundationLos(item.getFoundationLos());
                sectionToAdd.setStudentModels(new ArrayList<StudentModel>());
                sectionToAdd.setId(item.getIdSection());
                sectionToAdd.setFirstDate(item.getFirstDateSection());
                sectionToAdd.setSecondDate(item.getSecondDateSection());

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
                groupToAdd.setName(item.getGroupname());
                groupToAdd.setSections(new ArrayList<SectionModel>());
                sectionToAdd.getGroups().add(groupToAdd);
            }

            StudentModel studentToAdd = new StudentModel();
            transformOrderModelESOToStudentModel(item, studentToAdd);
            studentToAdd.setGroupname(item.getGroupname());

            groupToAdd.getStudentModels().add(studentToAdd);
        }
    }

    public void fillOrderModelForSetEliminationDebts (OrderModel orderModel, List<OrderModelESO> listModelsESO) {
        orderModel.setSections(new ArrayList<SectionModel>());
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
                sectionToAdd.setName(item.getSectionname());
                sectionToAdd.setFoundation(item.getFoundation());
                sectionToAdd.setFoundationLos(item.getFoundationLos());
                sectionToAdd.setStudentModels(new ArrayList<StudentModel>());
                sectionToAdd.setId(item.getIdSection());
                sectionToAdd.setFirstDate(item.getFirstDateSection());

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
                groupToAdd.setName(item.getGroupname());
                groupToAdd.setSections(new ArrayList<SectionModel>());
                sectionToAdd.getGroups().add(groupToAdd);
            }

            StudentModel studentToAdd = new StudentModel();
            transformOrderModelESOToStudentModel(item, studentToAdd);
            studentToAdd.setGroupname(item.getGroupname());

            groupToAdd.getStudentModels().add(studentToAdd);
        }
    }

    public void fillOrderModelForSocialIncreased (OrderModel orderModel, List<OrderModelESO> listModelsESO) {
        orderModel.setSections(new ArrayList<SectionModel>());
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
                sectionToAdd.setName(item.getSectionname());
                sectionToAdd.setFoundation(item.getFoundation());
                sectionToAdd.setStudentModels(new ArrayList<StudentModel>());
                sectionToAdd.setId(item.getIdSection());
                sectionToAdd.setFirstDate(item.getFirstDateSection());
                sectionToAdd.setSecondDate(item.getSecondDateSection());

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
                groupToAdd.setName(item.getGroupname());
                groupToAdd.setSections(new ArrayList<SectionModel>());
                sectionToAdd.getGroups().add(groupToAdd);
            }

            StudentModel studentToAdd = new StudentModel();
            transformOrderModelESOToStudentModel(item, studentToAdd);

            groupToAdd.getStudentModels().add(studentToAdd);
        }
    }

    public void fillOrderModelForSocial (OrderModel orderModel, List<OrderModelESO> listModelsESO) {
        orderModel.setSections(new ArrayList<SectionModel>());
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
                sectionToAdd.setName(item.getSectionname());
                sectionToAdd.setFoundation(item.getFoundation());
                sectionToAdd.setStudentModels(new ArrayList<StudentModel>());
                sectionToAdd.setId(item.getIdSection());
                sectionToAdd.setFirstDate(item.getFirstDateSection());
                sectionToAdd.setSecondDate(item.getSecondDateSection());

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
                groupToAdd.setName(item.getGroupname());
                groupToAdd.setSections(new ArrayList<SectionModel>());
                sectionToAdd.getGroups().add(groupToAdd);
            }

            StudentModel studentToAdd = new StudentModel();
            transformOrderModelESOToStudentModel(item, studentToAdd);

            groupToAdd.getStudentModels().add(studentToAdd);
        }
    }

    public void transformOrderModelESOToStudentModel (OrderModelESO orderModelESO, StudentModel studentModel) {
        studentModel.setId(orderModelESO.getIdStudent());
        studentModel.setFio(orderModelESO.getFio());
        studentModel.setFirstDate(orderModelESO.getFirstDate());
        studentModel.setSecondDate(orderModelESO.getSecondDate());
        studentModel.setThirdDate(orderModelESO.getThirdDate());
        studentModel.setRecordnumber(orderModelESO.getRecordbook());
        studentModel.setAdditional(orderModelESO.getAdditionalInfo());
        studentModel.setIsGovernmentFinanced(orderModelESO.getIsGovernmentFinanced());

        if (orderModelESO.getAdditionalInfo() != null && !orderModelESO.getAdditionalInfo().equals("")) {
            JSONObject additionalInfo = new JSONObject(orderModelESO.getAdditionalInfo());

            if (additionalInfo.has(OrderStudentJSONConst.ORDER_DATE)) {
                if (additionalInfo.getString(OrderStudentJSONConst.ORDER_DATE) != null) {
                    try {
                        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(
                                additionalInfo.getString(OrderStudentJSONConst.ORDER_DATE));
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
        }
    }

    @Override
    public List<RatingModel> getMarksStudent (Long idLoss, boolean isDebt) {
        EntityManagerOrderESO managerOrderESO = new EntityManagerOrderESO();

        return divideEsoModelForRatingModel(managerOrderESO.getMarksForStudentInOrder(idLoss), isDebt);
    }

    private List<RatingModel> divideEsoModelForRatingModel (List<RatingEsoModel> listEsoModel, boolean debt) {
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

    private RatingModel getRatingModelByEsoModel (RatingEsoModel esoModel) {
        RatingModel ratingModel = new RatingModel();
        ratingModel.setSemester(esoModel.getSemester());
        ratingModel.setSubjectname(esoModel.getSubjectname());
        return ratingModel;
    }

    public void updateOrderDesc (String desc, long idOrder) {
        EntityManagerOrderESO managerOrderESO = new EntityManagerOrderESO();
        managerOrderESO.updateOrderDesc(desc, idOrder);
    }

    @Override
    public void updateDateForLoss (Long idLoss, Date date, int numDate) throws Exception {
        new EntityManagerOrderESO().updateDateForLoss(idLoss, date, numDate);
    }

    @Override
    public void saveFoundation (Long id, String foundation) {
        new EntityManagerOrderESO().saveFoundation(id, foundation);
    }

    @Override
    public void saveFoundationStudent (StudentModel student, String foundation) {
        JSONObject object;
        if (student.getAdditional() == null || student.getAdditional().equals("")) {
            object = new JSONObject();
        } else {
            object = new JSONObject(student.getAdditional());
        }

        object.put(OrderStudentJSONConst.FOUNDATION, foundation);
        student.setAdditional(object.toString());

        new EntityManagerOrderESO().saveFoundationStudent(student.getId(), object.toString());
    }

    @Override
    public void deleteStudents (StudentModel student) {
        new EntityManagerOrderESO().removeStudentFromOrder(student.getId());
    }

    @Override
    public void updateOrderInfo(OrderModel orderEModel){
        new EditOrderManagerESO().updateOrderInfo(orderModel);
    }

    @Override
    public void syncOrder(Long idOrder){
        JSONObject jsonIdOrder = new JSONObject();
        jsonIdOrder.put("idOrder", idOrder);

        JSONObject jsonOrder = new JSONObject(HttpClient.makeHttpRequest("http://localhost:8081/sync/order",
                                                                         HttpClient.POST,
                                                                         new ArrayList<>(),
                                                                         jsonIdOrder.toString()));

        if(jsonOrder.has("status")){
            if(jsonOrder.get("status").equals("error")){
                log.error("Синхронизация приказа в Шахтах c id = " + idOrder + " не удалось!");
                PopupUtil.showError("Синхронизация приказа не удалась!");
                return;
            }
            else{
                PopupUtil.showInfo("Приказ был успешно синхронизирован!");
                new EditOrderManagerESO().updateOrderStatus(idOrder, OrderStatusConst.SYNCHED);
            }
        }
    }
}
