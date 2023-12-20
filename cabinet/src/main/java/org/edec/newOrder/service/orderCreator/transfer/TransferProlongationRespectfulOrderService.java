package org.edec.newOrder.service.orderCreator.transfer;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateDocumentModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.DocumentEnum;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.model.enums.ParamEnum;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.converter.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class TransferProlongationRespectfulOrderService extends OrderService {
    @Override
    protected void generateParamModel () {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION;

        orderParams = new ArrayList<>();
        orderParams.add(new OrderCreateParamModel("Продлить до", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Группы", ComponentEnum.GROUP_CHOOSER, true));

        orderVisualGeneralParams = new ArrayList<>();
        orderVisualGeneralParams.add(new OrderVisualParamModel("Продлить до", ComponentEnum.DATEBOX));
    }

    @Override
    protected void generateDocumentModel () {
        orderDocuments = new ArrayList<>();

        OrderCreateDocumentModel documentModel = new OrderCreateDocumentModel("Служебная ЛАЗ", DocumentEnum.SET_ELIMINATION_NOTION);
        documentModel.getListDocumentParam().add(new OrderCreateParamModel("Служебная с", ComponentEnum.DATEBOX, true));

        orderDocuments.add(documentModel);
    }

    @Override
    public boolean createAndAttachOrderDocuments(List<Object> documentParams, List<Object> orderParams, OrderEditModel order) {
        if (documentParams.get(0) != null) {
            HashMap<ParamEnum, Object> params = new HashMap<>();
            params.put(ParamEnum.DATE_FROM, documentParams.get(0));
            params.put(ParamEnum.DOCUMENT_NAME, "Служебная продление ЛАЗ.pdf");
            params.put(ParamEnum.SEMESTER, "весеннего семестра 2017/2018");

            documentService.generateDocument(DocumentEnum.SET_ELIMINATION_NOTION, order, params);
        }

        return true;
    }

    @Override
    public Long createOrderInDatabase (List<Object> orderParams, List<OrderCreateStudentModel> students) {
        Long idOrder = getOrderCreateService().createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams), getParamsGetter().getDescFromParams(orderParams));

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());
        List<String> groups = new ArrayList<>((HashSet<String>)orderParams.get(1));
        String strGroups = groups.stream().map(el -> "'" + el + "'").collect(Collectors.joining(","));

        List<OrderCreateStudentModel> listStudents = studentsOrderManager
                .getStudentsForSetEliminationAfterTransferProlongation(getParamsGetter().getIdSemesterFromParams(orderParams), strGroups, true);

        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = getOrderCreateService().createEmptySection(idOrder, section.getId(), section.getFoundation(), null, null);

            for (OrderCreateStudentModel student : listStudents) {
                if (student.getPrevOrderTransferTo() != null && student.getPrevOrderTransferTo().after(new Date())) {
                    continue;
                }
                if (student.getPrevOrderTransferTo() == null && student.getPrevOrderTransferToProl() != null &&
                    student.getPrevOrderTransferToProl().after(new Date())) {
                    continue;
                }

                if ((section.getName().endsWith("Бюджет") && student.getGovernmentFinanced()) ||
                    (section.getName().endsWith("Договор") && !student.getGovernmentFinanced())) {
                    getOrderCreateService().createStudentInSection(idLOS, student.getId(), (Date) orderParams.get(0), null, student.getGroupname(), student.getCurSemester(), "");
                }
            }
        }

        return idOrder;
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