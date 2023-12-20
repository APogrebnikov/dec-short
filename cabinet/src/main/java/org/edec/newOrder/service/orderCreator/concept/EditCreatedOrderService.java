package org.edec.newOrder.service.orderCreator.concept;

import org.edec.newOrder.manager.CreateOrderManagerESO;
import org.edec.newOrder.manager.EditOrderManagerESO;
import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.addStudent.LinkOrderSectionEditModel;
import org.edec.newOrder.model.addStudent.SearchStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.DocumentEnum;
import org.edec.newOrder.model.enums.ParamEnum;
import org.edec.newOrder.report.OrderLineService;
import org.edec.newOrder.report.service.OrderReportFillService;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.converter.DateConverter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EditCreatedOrderService {
    private OrderService orderService;
    private EditOrderManagerESO editOrderManagerESO = new EditOrderManagerESO();
    private CreateOrderManagerESO managerESO = new CreateOrderManagerESO();
    private OrderLineService orderLineService = new OrderLineService();
    private OrderReportFillService orderReportFillService = new OrderReportFillService();

    public EditCreatedOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void addStudentToOrder (SearchStudentModel studentModel, OrderEditModel order, LinkOrderSectionEditModel orderSection) {
        Long id = managerESO.createLinkOrderStudentStatus(orderSection.getIdLOS(), studentModel.getId(), null, null,
                                                          studentModel.getGroupname(), studentModel.getSemesterNumber(), "{}"
        );
        //TODO переделать архитектуру
        if (order.getIdOrderRule() == 13){

            HashMap<ParamEnum, Object> params = new HashMap<>();
            params.put(ParamEnum.DOCUMENT_NAME, "Учетная карточка " + studentModel.getFio() + ".pdf" );
            params.put(ParamEnum.GROUP_NAME, studentModel.getGroupname());
            params.put(ParamEnum.ID_STUDENTCARD_MINE, studentModel.getIdMine());

            orderService.getDocumentService().generateDocument(DocumentEnum.STUDENT_INDEX_CARD, order, params);
        }
        List<OrderVisualParamModel> listParams = getVisualParamsByIdSection(orderSection.getIdOS());

        StudentModel mockStudent = new StudentModel(id);
        for (int i = 0; i < listParams.size(); i++) {
            orderService.setParamForStudent(i + 1, studentModel.getStudentParams().get(i), mockStudent, orderSection.getIdOS());
        }

        orderLineService.updateOrderLines(order.getIdOrder(), orderReportFillService.getLines(order.getIdOrder()));
    }

    public List<OrderVisualParamModel> getVisualParamsByIdSection (Long idOrderSection) {
        return idOrderSection == null || orderService.getOrderSectionVisualParamsMap().get(idOrderSection) == null
               ? orderService.getOrderVisualGeneralParams()
               : orderService.getOrderSectionVisualParamsMap().get(idOrderSection);
    }

    public void updateAdditionalFieldForStudent(StudentModel studentModel) {
        long idOrder = editOrderManagerESO.getIdOrder(studentModel.getId());

        editOrderManagerESO.updateLossParam(studentModel.getId(),
                                             " additional = '" + studentModel.getAdditional() + "'"
        );

        orderLineService.updateOrderLines(idOrder, orderReportFillService.getLines(idOrder));
    }

    public void updateDateForStudent (StudentModel studentModel, Date date, int fieldNum) {
        if(date == null) {
            return;
        }

        String field = "";

        switch (fieldNum) {
            case 1:
                studentModel.setFirstDate(date);
                field = "first_date";
                break;
            case 2:
                studentModel.setSecondDate(date);
                field = "second_date";
                break;
        }

        editOrderManagerESO.updateLossParam(studentModel.getId(),
                                            field + " = '" + DateConverter.convertDateToStringByFormat(date, "yyyy-MM-dd") + "'"
        );

        long idOrder = editOrderManagerESO.getIdOrder(studentModel.getId());

        orderLineService.updateOrderLines(idOrder, orderReportFillService.getLines(idOrder));
    }

    public void removeStudentFromOrder (Long idLoss) {
        editOrderManagerESO.removeStudentFromOrder(idLoss);
    }

    public void updateOrderDesc (String desc, long id) {
        managerESO.updateOrderDesc(desc, id);
    }

    public String getFioByIdLoss(Long idLoss) {
        return editOrderManagerESO.getFioByIdLoss(idLoss);
    }
}
