package org.edec.newOrder.service.orderCreator.elimination;

import org.edec.newOrder.model.addStudent.LinkOrderSectionEditModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.addStudent.SearchStudentModel;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.service.orderCreator.OrderService;

import java.util.List;

public class SetEliminationOrderService extends OrderService {
    private boolean isRespectful;

    public SetEliminationOrderService (boolean isRespectful) {
        this.isRespectful = isRespectful;
    }

    @Override
    protected void generateParamModel () {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION_WITH_ORDER_FOUNDATION;

        // TODO
    }

    @Override
    protected void generateDocumentModel () {
        // TODO
    }

    @Override
    public Long createOrderInDatabase (List<Object> orderParams, List<OrderCreateStudentModel> students) {
        return null;
    }

    @Override
    public boolean createAndAttachOrderDocuments(List<Object> documentParams, List<Object> orderParams, OrderEditModel order) {
        // TODO

        return true;
    }

    @Override
    public void setParamForStudent (Integer i, Object value, StudentModel studentModel, Long idOS) {

    }

    @Override
    public Object getParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        return null;
    }

    @Override
    public String getStringParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        return null;
    }

    public void addStudentToOrder (SearchStudentModel studentModel, OrderEditModel order, LinkOrderSectionEditModel orderSection) {

    }
}