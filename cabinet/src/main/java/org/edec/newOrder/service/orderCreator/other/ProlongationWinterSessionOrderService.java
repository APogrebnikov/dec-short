package org.edec.newOrder.service.orderCreator.other;

import org.edec.newOrder.model.*;
import org.edec.newOrder.model.createOrder.OrderCreateDocumentModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.DocumentEnum;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.model.enums.ParamEnum;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.converter.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ProlongationWinterSessionOrderService extends OrderService {

    @Override
    protected void generateParamModel () {
        orderParams = new ArrayList<>();
        orderParams.add(new OrderCreateParamModel("Продлить до", ComponentEnum.DATEBOX, true));

        orderVisualGeneralParams = new ArrayList<>();
        orderVisualGeneralParams.add(new OrderVisualParamModel("Продлить до", ComponentEnum.DATEBOX));

        isFilesNeeded = true;
    }

    @Override
    protected void generateDocumentModel () {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION_WITH_ORDER_FOUNDATION;

        orderDocuments = new ArrayList<>();

        OrderCreateDocumentModel documentModel = new OrderCreateDocumentModel("Служебная ППА", DocumentEnum.SET_ELIMINATION_NOTION);
        documentModel.getListDocumentParam().add(new OrderCreateParamModel("Служебная с", ComponentEnum.DATEBOX, true));

        orderDocuments.add(documentModel);
    }

    @Override
    public Long createOrderInDatabase (List<Object> orderParams, List<OrderCreateStudentModel> students) {
        Long idOrder = getOrderCreateService().createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams), getParamsGetter().getDescFromParams(orderParams));

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = getOrderCreateService().createEmptySection(idOrder, section.getId(), section.getFoundation(), null, null);

            // TODO убрать хардкод константы id_order_section
            List<OrderCreateStudentModel> listStudents = studentsOrderManager.getStudentForProlongationAfterSetElimination(
                    managerESO.getPrevSemester(getParamsGetter().getIdSemesterFromParams(orderParams)), (section.getId().equals(93L)));

            for (OrderCreateStudentModel student : listStudents) {
                // TODO придумать универсализацию по обращению с параметрами приказа
                getOrderCreateService().createStudentInSection(idLOS, student.getId(), (Date) orderParams.get(0), null, student.getGroupname(), "");
            }
        }

        return idOrder;
    }

    @Override
    public boolean createAndAttachOrderDocuments(List<Object> documentParams, List<Object> orderParams, OrderEditModel order) {
        if (documentParams.get(0) != null) {
            HashMap<ParamEnum, Object> params = new HashMap<>();
            params.put(ParamEnum.DATE_FROM, documentParams.get(0));
            params.put(ParamEnum.DOCUMENT_NAME, "Служебная продление ЛАЗ.pdf");
            // TODO заполнять не статично
            params.put(ParamEnum.SEMESTER, "осеннего семестра 2017/2018");

            documentService.generateDocument(DocumentEnum.SET_ELIMINATION_NOTION, order, params);
        }

        return false;
    }

    @Override
    public void setParamForStudent (Integer i, Object value, StudentModel studentModel, Long idOS) {
        switch (i) {
            case 1:
                getEditCreatedOrderService().updateDateForStudent(studentModel, (Date) value, 1);
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
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public String getStringParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        switch (i) {
            case 1:
                return DateConverter.convertDateToString(studentModel.getFirstDate());
            default:
                throw new IllegalArgumentException();
        }
    }
}