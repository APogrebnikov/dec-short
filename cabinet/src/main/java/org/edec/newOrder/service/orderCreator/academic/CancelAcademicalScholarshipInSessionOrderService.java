package org.edec.newOrder.service.orderCreator.academic;

import org.edec.newOrder.model.*;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.converter.DateConverter;
import org.hibernate.type.IntegerType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CancelAcademicalScholarshipInSessionOrderService extends OrderService {

    @Override
    protected void generateParamModel() {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION;

        orderVisualGeneralParams = new ArrayList<>();
        orderVisualGeneralParams.add(new OrderVisualParamModel("Отменить с", ComponentEnum.DATEBOX));

        orderParams = new ArrayList<>();

        orderParams.add(new OrderCreateParamModel("Дата сдачи с", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Дата сдачи по", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Время сдачи", ComponentEnum.COMBOBOX_SEMESTER, true));

        isFilesNeeded = false;
    }

    @Override
    protected void generateDocumentModel() { }

    // Для того что приказ работал корректно - раз в пол года меняем семестр
    // за зимнюю сессию - предыдущий семестр
    // за летнюю сессию - текущий семестр
    @Override
    public Long createOrderInDatabase(List<Object> orderParams, List<OrderCreateStudentModel> students) {
        Long idOrder = getOrderCreateService()
                .createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams), getParamsGetter().getDescFromParams(orderParams));

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = getOrderCreateService().createEmptySection(idOrder, section.getId(), section.getFoundation(), null, null);

            List<OrderCreateStudentModel> listStudents = getStudentsForOrder(studentsOrderManager
                    .getStudentForCancelAcademicalScholarshipInSession(getParamsGetter().getIdSemesterFromParams(orderParams),
                                                                       (Date) orderParams.get(0),
                                                                       (Date) orderParams.get(1),
                                                                       (String) orderParams.get(2)
                    ));

            listStudents = listStudents.stream().filter(student -> student.getFirstDate().after((Date) orderParams.get(1)))
                                                           .collect(Collectors.toList());

            for(OrderCreateStudentModel student: listStudents){
                getOrderCreateService().createStudentInSection(idLOS, student.getId(), DateConverter.getFirstDayOfCurrentMonth(), null, student.getGroupname(), "");
            }
        }

        return idOrder;
    }

    @Override
    public boolean createAndAttachOrderDocuments(List<Object> documentParams, List<Object> orderParams, OrderEditModel order) {
        return false;
    }

    @Override
    public void setParamForStudent(Integer i, Object value, StudentModel studentModel, Long idOS) {
        switch (i) {
            case 1:
                getEditCreatedOrderService().updateDateForStudent(studentModel, (Date) value, 1);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Object getParamForStudent(Integer i, StudentModel studentModel, Long idOS) {
        switch (i) {
            case 1:
                return studentModel.getFirstDate();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public String getStringParamForStudent(Integer i, StudentModel studentModel, Long idOS) {
        switch (i) {
            case 1:
                return DateConverter.convertDateToString(studentModel.getFirstDate());
            default:
                throw new IllegalArgumentException();
        }
    }

    private List<OrderCreateStudentModel> getStudentsForOrder(List<OrderCreateStudentModel> studentList){
        //Сгруппировать по студентам, и собрать все их srh
        List<OrderCreateStudentModel> groupedStudentList = new ArrayList<>();

        if(studentList.size() == 0) {
            return groupedStudentList;
        }

        OrderCreateStudentModel student = null;

        for(OrderCreateStudentModel selectedStudent : studentList){
            if(student != null){
                if(student.getId().equals(selectedStudent.getId())){
                    student.getRatingsList().add(selectedStudent.getRating());
                }else{
                    groupedStudentList.add(student);

                    student = selectedStudent;
                    student.getRatingsList().add(student.getRating());
                }
            }else{
                student = selectedStudent;
                student.getRatingsList().add(student.getRating());

            }
        }

        groupedStudentList.add(student);

        //Подсчитать количество долгов если 0, то исключаем из списка
        groupedStudentList = groupedStudentList.stream().filter(selectedStudent->{
            for(Integer rating : selectedStudent.getRatingsList()){
                if(isBadMark(rating,selectedStudent.getIsSessionProlongation()))
                    return true;
            }

            return false;
        }).collect(Collectors.toList());

        return groupedStudentList;
    }

    private boolean isBadMark(int rating, boolean isSessionProlongation){
        return (rating==3 || rating == 2 || rating == -2) || (!isSessionProlongation && rating == -3);
    }

}
