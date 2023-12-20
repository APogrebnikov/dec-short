package org.edec.newOrder.service.orderCreator.academic;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.constants.OrderStudentJSONConst;
import org.edec.utility.converter.DateConverter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AcademicIndividualOrderService extends OrderService {

    @Override
    protected void generateParamModel() {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION_WITH_STUDENT_FOUNDATION;

        orderVisualGeneralParams.add(new OrderVisualParamModel("Назначить с", ComponentEnum.DATEBOX));
        orderVisualGeneralParams.add(new OrderVisualParamModel("Назначить до", ComponentEnum.DATEBOX));
        orderVisualGeneralParams.add(new OrderVisualParamModel("Успеваемость", ComponentEnum.COMBOBOX_PERFORMANCE));

    }

    @Override
    protected void generateDocumentModel() {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION_WITH_ORDER_FOUNDATION;

        orderDocuments = new ArrayList<>();
    }

    @Override
    public boolean createAndAttachOrderDocuments(List<Object> documentParams, List<Object> orderParams, OrderEditModel order) {
        return false;
    }

    @Override
    public Long createOrderInDatabase(List<Object> orderParams, List<OrderCreateStudentModel> students) {
        Long idOrder = getOrderCreateService().createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams), getParamsGetter().getDescFromParams(orderParams));

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = getOrderCreateService().createEmptySection(idOrder, section.getId(), section.getFoundation(), null, null);

            for (OrderCreateStudentModel student : students) {
                JSONObject additionalJSON = new JSONObject();

                additionalJSON.put(OrderStudentJSONConst.ACADEMIC_PERFORMANCE, student.getParamsFromCreate().get(2).toString());

                getOrderCreateService().createStudentInSection(idLOS,
                                                               student.getId(),
                                                               (Date)student.getParamsFromCreate().get(0),
                                                               (Date)student.getParamsFromCreate().get(1),
                                                               student.getGroupname(),
                                                               additionalJSON.toString());
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
            case 3:
                JSONObject additional = new JSONObject(studentModel.getAdditional());
                additional.put(OrderStudentJSONConst.ACADEMIC_PERFORMANCE, value);
                studentModel.setAdditional(additional.toString());
                getEditCreatedOrderService().updateAdditionalFieldForStudent(studentModel);
        }
    }

    @Override
    public Object getParamForStudent(Integer i, StudentModel studentModel, Long idOS) {
        switch (i) {
            case 1:
                return studentModel.getFirstDate();
            case 2:
                return studentModel.getSecondDate();
            case 3:
                JSONObject additional = new JSONObject(studentModel.getAdditional());

                if (additional.has(OrderStudentJSONConst.ACADEMIC_PERFORMANCE)) {
                    return additional.getString(OrderStudentJSONConst.ACADEMIC_PERFORMANCE);
                }
        }

        return null;
    }

    @Override
    public String getStringParamForStudent(Integer i, StudentModel studentModel, Long idOS) {
        switch (i) {
            case 1:
                return DateConverter.convertDateToString(studentModel.getFirstDate());
            case 2:
                return DateConverter.convertDateToString(studentModel.getSecondDate());
            case 3:
                JSONObject additional = new JSONObject(studentModel.getAdditional());

                if (additional.has(OrderStudentJSONConst.ACADEMIC_PERFORMANCE)) {
                    return additional.getString(OrderStudentJSONConst.ACADEMIC_PERFORMANCE);
                }
        }

        return null;
    }
}
