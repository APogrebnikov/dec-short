package org.edec.newOrder.service.orderCreator.other;

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

import java.util.*;

public class DeductionByComissionResultOrderService extends OrderService {
    @Override
    protected void generateParamModel() {

        groupingInEditEnum = GroupingInEditEnum.BY_SECTION;
        orderParams.add(new OrderCreateParamModel("Отчислить с", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Отмена выплат с", ComponentEnum.DATEBOX, true));

        orderVisualGeneralParams = new ArrayList<>();
        orderVisualGeneralParams.add(new OrderVisualParamModel("Отчислить с", ComponentEnum.DATEBOX));
        orderVisualGeneralParams.add(new OrderVisualParamModel("Отмена выплат с", ComponentEnum.DATEBOX));

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

        Long idOrder = getOrderCreateService().createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams), getParamsGetter().getDescFromParams(orderParams));
        Long idCurrentSem = getParamsGetter().getIdSemesterFromParams(orderParams);

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        List<OrderCreateStudentModel> studentList = studentsOrderManager.getStudentsForDeductionByComission(idCurrentSem);


        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = getOrderCreateService().createEmptySection(idOrder, section.getId(), section.getFoundation(), null, null);

            for (OrderCreateStudentModel student : studentList){
                getOrderCreateService()
                        .createStudentInSection(idLOS, student.getId(), (Date) orderParams.get(0),
                                (Date) orderParams.get(1), student.getGroupname(),
                                student.getSemesternumber(), "{}"
                        );
            }
        }

        return idOrder;
    }

    @Override
    public void setParamForStudent(Integer i, Object value, StudentModel studentModel, Long idOS) {
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

    @Override
    public Object getParamForStudent(Integer i, StudentModel studentModel, Long idOS) {
        switch (i) {
            case 1:
                return DateConverter.convertDateToString(studentModel.getFirstDate());
            case 2:
                return DateConverter.convertDateToString(studentModel.getSecondDate());
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public String getStringParamForStudent(Integer i, StudentModel studentModel, Long idOS) {
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
