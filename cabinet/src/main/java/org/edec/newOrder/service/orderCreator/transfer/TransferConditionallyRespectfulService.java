package org.edec.newOrder.service.orderCreator.transfer;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.converter.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransferConditionallyRespectfulService extends OrderService {

    @Override
    protected void generateParamModel () {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION_WITH_ORDER_FOUNDATION;

        orderParams = new ArrayList<>();

        orderVisualGeneralParams = new ArrayList<>();
        orderVisualGeneralParams.add(new OrderVisualParamModel("Перевести с", ComponentEnum.DATEBOX));
        orderVisualGeneralParams.add(new OrderVisualParamModel("Сроки ликвидации по", ComponentEnum.DATEBOX));

        isFilesNeeded = true;
    }

    @Override
    protected void generateDocumentModel () {
        orderDocuments = new ArrayList<>();
    }

    @Override
    public Long createOrderInDatabase (List<Object> orderParams, List<OrderCreateStudentModel> students) {
        Long idOrder = getOrderCreateService().createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams), getParamsGetter().getDescFromParams(orderParams));

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = getOrderCreateService().createEmptySection(idOrder, section.getId(), section.getFoundation(), null, null);

            for (OrderCreateStudentModel student : students) {
                if (student.getGovernmentFinanced() && section.getId().equals(96L) ||
                    !student.getGovernmentFinanced() && section.getId().equals(97L)) {
                    // TODO продумать механизм изменения параметров студентов во время формирования приказа(ручной режим)
                    getOrderCreateService().createStudentInSection(idLOS, student.getId(), new Date(), new Date(), student.getGroupname(), "");
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
    public void setParamForStudent (Integer i, Object value, StudentModel studentModel, Long idOS) {
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
    public Object getParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        switch (i) {
            case 1:
                return studentModel.getFirstDate();
            case 2:
                return studentModel.getSecondDate();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public String getStringParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
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
