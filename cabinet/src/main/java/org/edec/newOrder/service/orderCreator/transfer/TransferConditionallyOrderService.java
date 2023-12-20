package org.edec.newOrder.service.orderCreator.transfer;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.addStudent.LinkOrderSectionEditModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.addStudent.SearchStudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.DocumentEnum;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.model.enums.ParamEnum;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.constants.SemesterConst;
import org.edec.utility.converter.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TransferConditionallyOrderService extends OrderService {
    @Override
    protected void generateParamModel () {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION;
        orderParams = new ArrayList<>();
        orderParams.add(new OrderCreateParamModel("Срок сдачи с", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Срок сдачи по", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Перевести с", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Курс", ComponentEnum.COMBOBOX_COURSE, true));
        orderVisualGeneralParams = new ArrayList<>();
        orderVisualGeneralParams.add(new OrderVisualParamModel("Перевести с", ComponentEnum.DATEBOX));
    }

    @Override
    protected void generateDocumentModel () {
        // TODO
    }

    @Override
    public Long createOrderInDatabase (List<Object> orderParams, List<OrderCreateStudentModel> students) {
        Long idOrder = getOrderCreateService()
                .createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams),
                                  getParamsGetter().getDescFromParams(orderParams));

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        List<String> attributes = new ArrayList<String>();
        attributes.add(managerESO.getSchoolYearBySemesterId(getParamsGetter().getIdSemesterFromParams(orderParams)));
        getOrderCreateService().updateOrderAttr(idOrder, attributes);

        List<OrderCreateStudentModel> listStudents = studentsOrderManager
                .getStudentsForTransferConditionally((Date) orderParams.get(0),
                                                           (Date) orderParams.get(1),
                                                           Integer.parseInt((String) orderParams.get(3))
                );

        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = getOrderCreateService().createEmptySection(idOrder, section.getId(), section.getFoundation(), null, null);

            for (OrderCreateStudentModel studentModel : listStudents) {

                if ((section.getName().contains("Бюджет") && studentModel.getGovernmentFinanced() ||
                     section.getName().contains("Договор") && !studentModel.getGovernmentFinanced())) {
                    getOrderCreateService().createStudentInSection(idLOS,
                                                                   studentModel.getId(),
                                                                   (Date) orderParams.get(2),
                                                                   null,
                                                                   studentModel.getGroupname(),
                                                                   ""
                    );
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
    public void removeStudentFromOrder (Long idLoss, OrderEditModel order) {

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
}