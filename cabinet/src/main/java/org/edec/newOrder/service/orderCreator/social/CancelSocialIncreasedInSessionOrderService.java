package org.edec.newOrder.service.orderCreator.social;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.DateUtility;
import org.edec.utility.converter.DateConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CancelSocialIncreasedInSessionOrderService extends OrderService {
    @Override
    protected void generateParamModel() {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION;

        orderParams = new ArrayList<>();

        orderParams.add(new OrderCreateParamModel("Отменить с", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Дата сдачи с", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Дата сдачи по", ComponentEnum.DATEBOX, true));

        orderSectionVisualParamsMap = new HashMap<>();

        List<OrderVisualParamModel> orderVisualGeneralParamsCancel = new ArrayList<>();
        orderVisualGeneralParamsCancel.add(new OrderVisualParamModel("Отменить с", ComponentEnum.DATEBOX));

        orderSectionVisualParamsMap.put(120L, orderVisualGeneralParamsCancel);

        List<OrderVisualParamModel> orderVisualGeneralParamsAssign = new ArrayList<>();
        orderVisualGeneralParamsAssign.add(new OrderVisualParamModel("Дата с", ComponentEnum.DATEBOX));
        orderVisualGeneralParamsAssign.add(new OrderVisualParamModel("Дата до", ComponentEnum.DATEBOX));

        orderSectionVisualParamsMap.put(121L, orderVisualGeneralParamsAssign);
    }

    @Override
    protected void generateDocumentModel() {

    }

    @Override
    public boolean createAndAttachOrderDocuments(List<Object> documentParams, List<Object> orderParams, OrderEditModel order) {
        return false;
    }

    @Override
    public Long createOrderInDatabase(List<Object> orderParams, List<OrderCreateStudentModel> students) {
        Long idOrder = getOrderCreateService()
                .createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams), getParamsGetter().getDescFromParams(orderParams));

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        Date previousMonthFirstDay = DateConverter.getFirstDayOfPreviousMonth();
        Date currentMonthLastDay = DateConverter.getLastDayOfCurrentMonth();

        //Получаем список студентов для отмены соц. повыш. стипендии
        List<OrderCreateStudentModel> listStudents = getStudentsForOrder(studentsOrderManager
                                                                                 .getStudentForCancelSocialIncreasedScholarshipInSession(
                                                                                         getParamsGetter()
                                                                                                 .getIdSemesterFromParams(orderParams),
                                                                                         (Date) orderParams.get(1),
                                                                                         (Date) orderParams.get(2)
                                                                                 ));

        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = getOrderCreateService().createEmptySection(idOrder, section.getId(), section.getFoundation(), null, null);

            // прекращение выплат
            if (section.getId().equals(120L)) {
                for (OrderCreateStudentModel student : listStudents) {
                    getOrderCreateService()
                            .createStudentInSection(idLOS, student.getId(), (Date) orderParams.get(0), null, student.getGroupname(), "");
                }
            } else { // переназначение
                for (OrderCreateStudentModel student : listStudents) {
                    getOrderCreateService()
                            .createStudentInSection(idLOS, student.getId(), (Date) orderParams.get(0), student.getDateSocialScholarshipTo(),
                                                    student.getGroupname(), "");
                }
            }
        }

        return idOrder;
    }

    @Override
    public void setParamForStudent(Integer i, Object value, StudentModel studentModel, Long idOS) {
        if(idOS == 120) {
            switch (i) {
                case 1:
                    getEditCreatedOrderService().updateDateForStudent(studentModel, (Date) value, 1);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        } else {
            switch (i) {
                case 1:
                    getEditCreatedOrderService().updateDateForStudent(studentModel, (Date) value, 1);
                    break;
                case 2:
                    getEditCreatedOrderService().updateDateForStudent(studentModel, (Date) value, 2);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public Object getParamForStudent(Integer i, StudentModel studentModel, Long idOS) {
        if(idOS == 120) {
            switch (i) {
                case 1:
                    return studentModel.getFirstDate();
                default:
                    throw new IllegalArgumentException();
            }
        } else {
            switch (i) {
                case 1:
                    return studentModel.getFirstDate();
                case 2:
                    return studentModel.getSecondDate();
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public String getStringParamForStudent(Integer i, StudentModel studentModel, Long idOS) {
        if(idOS == 120) {
            switch (i) {
                case 1:
                    return DateConverter.convertDateToString(studentModel.getFirstDate());
                default:
                    throw new IllegalArgumentException();
            }
        } else {
            switch (i) {
                case 1:
                    return DateConverter.convertDateToString(studentModel.getFirstDate());
                case 2:
                    return DateConverter.convertDateToString(studentModel.getSecondDate());
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private List<OrderCreateStudentModel> getStudentsForOrder(List<OrderCreateStudentModel> studentList) {
        //Сгруппировать по студентам, и собрать все их srh
        List<OrderCreateStudentModel> groupedStudentList = new ArrayList<>();

        if (studentList.size() == 0) {
            return groupedStudentList;
        }

        OrderCreateStudentModel student = null;

        for (OrderCreateStudentModel selectedStudent : studentList) {
            if (student != null) {
                if (student.getId().equals(selectedStudent.getId())) {
                    student.getRatingsList().add(selectedStudent.getRating());
                } else {
                    groupedStudentList.add(student);

                    student = selectedStudent;
                    student.getRatingsList().add(student.getRating());
                }
            } else {
                student = selectedStudent;
                student.getRatingsList().add(student.getRating());
            }
        }

        groupedStudentList.add(student);

        return groupedStudentList;
    }
}
