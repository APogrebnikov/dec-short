package org.edec.newOrder.service.orderCreator.academic;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.converter.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CancelAcademicalScholarshipByPracticeOrderService extends OrderService {

    @Override
    protected void generateParamModel() {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION;

        orderParams = new ArrayList<>();
        orderParams.add(new OrderCreateParamModel("Дата отмены(переназначения)", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Дата сдачи с:", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Дата сдачи по:", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Семестр сдачи практики:", ComponentEnum.COMBOBOX_SEMESTER, true));


        orderSectionVisualParamsMap = new HashMap<>();

        List<OrderVisualParamModel> orderVisualGeneralParamsCancel = new ArrayList<>();
        orderVisualGeneralParamsCancel.add(new OrderVisualParamModel("Отменить с", ComponentEnum.DATEBOX));

        orderSectionVisualParamsMap.put(98L, orderVisualGeneralParamsCancel);

        List<OrderVisualParamModel> orderVisualGeneralParamsAssign = new ArrayList<>();
        orderVisualGeneralParamsAssign.add(new OrderVisualParamModel("Дата с", ComponentEnum.DATEBOX));
        orderVisualGeneralParamsAssign.add(new OrderVisualParamModel("Дата до", ComponentEnum.DATEBOX));

        orderSectionVisualParamsMap.put(99L, orderVisualGeneralParamsAssign);
    }

    @Override
    protected void generateDocumentModel() {

    }

    @Override
    public Long createOrderInDatabase(List<Object> orderParams, List<OrderCreateStudentModel> students) {
        Long idOrder = getOrderCreateService().createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams), getParamsGetter().getDescFromParams(orderParams));

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = getOrderCreateService().createEmptySection(idOrder, section.getId(), section.getFoundation(), null, null);

            // прекращение выплат
            if(section.getId().equals(98L)) {
                List<OrderCreateStudentModel> listStudents = getListStudentsScholarshipChanging((Date) orderParams.get(1),
                                                                                                (Date) orderParams.get(2),
                                                                                                (String) orderParams.get(3),
                                                                                                true);
                for (OrderCreateStudentModel student : listStudents) {
                    getOrderCreateService().createStudentInSection(idLOS, student.getId(), (Date) orderParams.get(0), null, student.getGroupname(), "");
                }
            } else { // переназначение
                List<OrderCreateStudentModel> listStudents = getListStudentsScholarshipChanging((Date) orderParams.get(1),
                                                                                                (Date) orderParams.get(2),
                                                                                                (String) orderParams.get(3),
                                                                                                false);
                for (OrderCreateStudentModel student : listStudents) {
                    getOrderCreateService().createStudentInSection(idLOS, student.getId(), (Date) orderParams.get(0), DateConverter.getLastDayOfMonth(student.getDateOfEndSession()), student.getGroupname(), "");
                }
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
        if(idOS == 98) {
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
        if(idOS == 98) {
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
        if(idOS == 98) {
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

    public List<OrderCreateStudentModel> getListStudentsScholarshipChanging(Date dateFrom,
                                                                            Date dateTo,
                                                                            String season,
                                                                            boolean isCanceled) {
        List<OrderCreateStudentModel> listStudents = studentsOrderManager.getStudentsWithAcademicScholarshipChanging(dateFrom,
                                                                                                                     dateTo,
                                                                                                                     season);

        listStudents = listStudents.stream().filter(s->s.getSessionResult() != null
                                                       && s.getSessionResultPrev()!=null &&
                                                       s.getSessionResultPrev() >= 2)
                                   .collect(Collectors.toList());

        if(isCanceled){
            listStudents = listStudents.stream().filter(s-> (s.getSessionResult() < 1 || s.getSessionResult() == 2 || s.getSessionResult() == 3)
                                                            || (s.getSessionResultPrev() == 2 && s.getSessionResult() == 5)
                                                            || (s.getSessionResultPrev() == 4 && s.getSessionResult() == 4))
                                       .collect(Collectors.toList());
        }else{
            listStudents = listStudents.stream().filter(s->
                                                                (s.getSessionResultPrev() == 2 && s.getSessionResult() == 5)
                                                                || (s.getSessionResultPrev() == 4 && s.getSessionResult() == 4))
                                       .collect(Collectors.toList());
        }

        return listStudents;
    }
}
