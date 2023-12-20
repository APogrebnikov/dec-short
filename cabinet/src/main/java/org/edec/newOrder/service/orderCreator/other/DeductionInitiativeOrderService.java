package org.edec.newOrder.service.orderCreator.other;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.DocumentEnum;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.model.enums.ParamEnum;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.converter.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DeductionInitiativeOrderService extends OrderService {
    public DeductionInitiativeOrderService(FormOfStudy fos, Long idInstitute) {
        super(fos, idInstitute);
    }

    @Override
    protected void generateParamModel () {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION_WITH_STUDENT_FOUNDATION;

        if (formOfStudy != null && formOfStudy.equals(FormOfStudy.EXTRAMURAL)) {
            orderVisualGeneralParams.add(new OrderVisualParamModel("Отчислить с", ComponentEnum.DATEBOX));
        } else if (formOfStudy == null || formOfStudy.equals(FormOfStudy.FULL_TIME)) {
            orderVisualGeneralParams.add(new OrderVisualParamModel("Отчислить с", ComponentEnum.DATEBOX));
            orderVisualGeneralParams.add(new OrderVisualParamModel("Отмена выплат с", ComponentEnum.DATEBOX));
        }
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
                if (formOfStudy != null && formOfStudy.equals(FormOfStudy.EXTRAMURAL)) {
                    getOrderCreateService().createStudentInSection(idLOS, student.getId(), (Date)student.getParamsFromCreate().get(0), null, student.getGroupname(), "");
                } else {
                    getOrderCreateService().createStudentInSection(idLOS, student.getId(), (Date)student.getParamsFromCreate().get(0), (Date)student.getParamsFromCreate().get(1), student.getGroupname(), "");
                }
            }
        }

        return idOrder;
    }

    @Override
    public boolean createAndAttachOrderDocuments(List<Object> documentParams, List<Object> orderParams, OrderEditModel order) {
        getEditOrderService().fillOrderModel(order);
        List<StudentModel> listStudents = order.getAllStudents();

        for (StudentModel student : listStudents){
            HashMap<ParamEnum, Object> params = new HashMap<>();
            params.put(ParamEnum.DOCUMENT_NAME, "Учетная карточка " + student.getFio() + ".pdf" );
            params.put(ParamEnum.GROUP_NAME, student.getGroupname());
            params.put(ParamEnum.ID_STUDENTCARD_MINE, student.getIdMine());

            documentService.generateDocument(DocumentEnum.STUDENT_INDEX_CARD, order, params);
        }
        return false;
    }

    @Override
    public void setParamForStudent (Integer i, Object value, StudentModel studentModel, Long idOS) {
        assert i <= getEditCreatedOrderService().getVisualParamsByIdSection(idOS).size();

        if (formOfStudy != null && formOfStudy.equals(FormOfStudy.EXTRAMURAL)) {
            switch (i) {
                case 1:
                    getEditCreatedOrderService().updateDateForStudent(studentModel, (Date) value, 1);
                    break;
            }
        } else if (formOfStudy == null || formOfStudy.equals(FormOfStudy.FULL_TIME)) {
            switch (i) {
                case 1:
                    getEditCreatedOrderService().updateDateForStudent(studentModel, (Date) value, 1);
                    break;
                case 2:
                    getEditCreatedOrderService().updateDateForStudent(studentModel, (Date) value, 2);
                    break;
            }
        }
    }

    @Override
    public Object getParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        assert i <= getEditCreatedOrderService().getVisualParamsByIdSection(idOS).size();

        if (formOfStudy != null && formOfStudy.equals(FormOfStudy.EXTRAMURAL)) {
            switch (i) {
                case 1:
                    return studentModel.getFirstDate();
            }
        } else if (formOfStudy == null || formOfStudy.equals(FormOfStudy.FULL_TIME)) {
            switch (i) {
                case 1:
                    return studentModel.getFirstDate();
                case 2:
                    return studentModel.getSecondDate();
            }
        }

        return null;
    }

    @Override
    public String getStringParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        assert i <= getEditCreatedOrderService().getVisualParamsByIdSection(idOS).size();

        if (formOfStudy != null && formOfStudy.equals(FormOfStudy.EXTRAMURAL)) {
            switch (i) {
                case 1:
                    return DateConverter.convertDateToString(studentModel.getFirstDate());
            }
        } else if (formOfStudy == null || formOfStudy.equals(FormOfStudy.FULL_TIME)) {
            switch (i) {
                case 1:
                    return DateConverter.convertDateToString(studentModel.getFirstDate());
                case 2:
                    return DateConverter.convertDateToString(studentModel.getSecondDate());
            }
        }

        return null;
    }
}