package org.edec.newOrder.service.orderCreator.elimination;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.addStudent.LinkOrderSectionEditModel;
import org.edec.newOrder.model.addStudent.SearchStudentModel;
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
import org.edec.utility.constants.SemesterConst;
import org.edec.utility.converter.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SetEliminationSecondTimeOrderService extends OrderService {
    private final String attr1 = "второго"; // куда константу затолкать то?

    @Override
    protected void generateParamModel () {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION;
        orderParams = new ArrayList<>();
        orderParams.add(new OrderCreateParamModel("Срок сдачи с", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Срок сдачи по", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Установить срок ППА до", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Период сдачи", ComponentEnum.COMBOBOX_SEMESTER, true));
        orderParams.add(new OrderCreateParamModel("Курс", ComponentEnum.COMBOBOX_COURSE, true));
        orderVisualGeneralParams = new ArrayList<>();
        orderVisualGeneralParams.add(new OrderVisualParamModel("Установить срок ППА до", ComponentEnum.DATEBOX));
    }

    @Override
    protected void generateDocumentModel () {
    }

    @Override
    public boolean createAndAttachOrderDocuments(List<Object> documentParams, List<Object> orderParams, OrderEditModel order) {
        getEditOrderService().fillOrderModel(order);

        HashMap<ParamEnum, Object> params = new HashMap<>();
        params.put(ParamEnum.DOCUMENT_NAME, "Служебная записка.docx");

        documentService.generateDocument(DocumentEnum.NOTION_RECTOR, order, params);

        return true;
    }

    @Override
    public Long createOrderInDatabase (List<Object> orderParams, List<OrderCreateStudentModel> students) {
        Long idOrder = getOrderCreateService()
                .createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams), getParamsGetter().getDescFromParams(orderParams));

        String season = orderParams.get(3).toString(); // 3 - Осень/Весна
        Long idSemester = getParamsGetter().getIdSemesterFromParams(orderParams);
        String attr2 = "весеннего";
        if(season.equals(SemesterConst.AUTUMN)){
            idSemester = managerESO.getPrevSemester(idSemester);
            attr2 = "осеннего";
        }

        List<String> attributes = new ArrayList<String>();
        attributes.add(attr1);
        attributes.add(attr2);
        attributes.add(managerESO.getSchoolYearBySemesterId(idSemester));
        getOrderCreateService().updateOrderAttr(idOrder, attributes);

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        List<OrderCreateStudentModel> listStudents = studentsOrderManager
                .getStudentForSecondEliminationAfterSession((Date) orderParams.get(0),
                        (Date) orderParams.get(1),
                        (String) orderParams.get(3),
                        Integer.parseInt((String) orderParams.get(4)));

        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = getOrderCreateService().createEmptySection(idOrder, section.getId(), section.getFoundation(), null, null);

            for (OrderCreateStudentModel studentModel : listStudents) {

                if ((section.getName().contains("Бюджет") && studentModel.getGovernmentFinanced() ||
                        section.getName().contains("Договор") && !studentModel.getGovernmentFinanced())) {
                    getOrderCreateService().createStudentInSection(idLOS, studentModel.getId(),
                            (Date) orderParams.get(2),
                            null,
                            studentModel.getGroupname(),
                            "{\"semester\":\"" + idSemester +
                                    "\"}"
                    );
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
