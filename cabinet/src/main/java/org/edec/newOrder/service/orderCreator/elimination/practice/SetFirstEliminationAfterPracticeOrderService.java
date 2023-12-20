package org.edec.newOrder.service.orderCreator.elimination.practice;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.constants.OrderStudentJSONConst;
import org.edec.utility.constants.SemesterConst;
import org.edec.utility.converter.DateConverter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SetFirstEliminationAfterPracticeOrderService extends OrderService {
    @Override
    protected void generateParamModel () {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION;

        orderParams = new ArrayList<>();
        orderParams.add(new OrderCreateParamModel("Срок сдачи с", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Срок сдачи по", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Курс", ComponentEnum.COMBOBOX_COURSE, true));
        orderParams.add(new OrderCreateParamModel("Установить срок ППА до", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Магистры", ComponentEnum.CHECKBOX, true));
        orderParams.add(new OrderCreateParamModel("Период сдачи", ComponentEnum.COMBOBOX_SEMESTER, true));

        orderVisualGeneralParams = new ArrayList<>();
        orderVisualGeneralParams.add(new OrderVisualParamModel("Срок до", ComponentEnum.DATEBOX, "120px"));
        orderVisualGeneralParams.add(new OrderVisualParamModel("Название практики", ComponentEnum.TEXTBOX_MULTILINE, "200px"));
        orderVisualGeneralParams.add(new OrderVisualParamModel("Тип практики", ComponentEnum.TEXTBOX, "150px"));
        orderVisualGeneralParams.add(new OrderVisualParamModel("Дата с", ComponentEnum.DATEBOX, "120px"));
        orderVisualGeneralParams.add(new OrderVisualParamModel("Дата по", ComponentEnum.DATEBOX, "120px"));
    }

    @Override
    protected void generateDocumentModel () {
        // TODO
    }

    @Override
    public boolean createAndAttachOrderDocuments(List<Object> documentParams, List<Object> orderParams, OrderEditModel order) {
        // TODO

        return true;
    }

    @Override
    public Long createOrderInDatabase (List<Object> orderParams, List<OrderCreateStudentModel> students) {
        Long idOrder = getOrderCreateService().createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams), getParamsGetter().getDescFromParams(orderParams));

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        String season = orderParams.get(5).toString(); // 5 - Осень/Весна
        Long idSemester = getParamsGetter().getIdSemesterFromParams(orderParams);
        String attr2 = "весеннего";
        if(season.equals(SemesterConst.AUTUMN)){
            idSemester = managerESO.getPrevSemester(idSemester);
            attr2 = "осеннего";
        }

        List<String> attributes = new ArrayList<String>();
        attributes.add("первой");
        attributes.add(attr2);
        attributes.add(managerESO.getSchoolYearBySemesterId(idSemester));
        getOrderCreateService().updateOrderAttr(idOrder, attributes);

        List<OrderCreateStudentModel> listStudents = studentsOrderManager
                .getStudentsForSetEliminationAfterPractice((Date) orderParams.get(0),
                                                           (Date) orderParams.get(1),
                                                           Integer.parseInt((String)orderParams.get(2)),
                                                           (Boolean) orderParams.get(4));

        // Надеюсь временный хардкод костыль
        for(OrderCreateStudentModel student : listStudents){
            switch(student.getPracticName()){
                case "Ознакомительная":
                    student.setPracticName("ознакомительной");
                    break;
                case "Ознакомительная практика":
                    student.setPracticName("ознакомительной практике");
                    break;
                case "Практика по получению первичных профессиональных умений и навыков":
                    student.setPracticName("практике по получению первичных профессиональных умений и навыков");
                    break;
                case "Практика по получению первичных профессиональных умений и навыков, в том числе первичных  умений и навыков научно-исследовательской деятельности":
                case "Практика по получению первичных профессиональных умений и навыков, в том числе первичных умений и навыков научно-исследовательской деятельности":
                    student.setPracticName("практике по получению первичных профессиональных умений и навыков, в том числе первичных умений и навыков научно-исследовательской деятельности");
                    break;
                case "Научно-исследовательская работа (получение первичных навыков научно-исследовательской работы)":
                    student.setPracticName("практике в виде научно-исследовательской работы");
                    break;
                case "Практика по получению профессиональных умений и опыта профессиональной деятельности":
                    student.setPracticName("практике получению профессиональных умений и опыта профессиональной деятельности");
                    break;
            }
        }

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

                    JSONObject additional = new JSONObject();
                    additional.put(OrderStudentJSONConst.PRACTIC_NAME, student.getPracticName());
                    additional.put(OrderStudentJSONConst.PRACTIC_TYPE, student.getPracticType());
                    additional.put(OrderStudentJSONConst.PRACTIC_DATE_FROM, DateConverter.convertDateToString(student.getDatePracticFrom()));
                    additional.put(OrderStudentJSONConst.PRACTIC_DATE_TO, DateConverter.convertDateToString(student.getDatePracticTo()));

                    getOrderCreateService().createStudentInSection(idLOS,
                                                                   student.getId(),
                                                                   (Date) orderParams.get(3),
                                                                   null, student.getGroupname(),
                                                                   Integer.parseInt(Long.toString(getParamsGetter().getIdSemesterFromParams(orderParams))),
                                                                   additional.toString());
                }
            }
        }

        return idOrder;
    }

    @Override
    public void setParamForStudent (Integer i, Object value, StudentModel studentModel, Long idOS) {
        JSONObject additional = new JSONObject(studentModel.getAdditional());

        switch (i) {
            case 1:
                getEditCreatedOrderService().updateDateForStudent(studentModel, (Date) value, 1);
            case 2:
                additional.put(OrderStudentJSONConst.PRACTIC_NAME, value);
                studentModel.setAdditional(additional.toString());
                getEditCreatedOrderService().updateAdditionalFieldForStudent(studentModel);
                break;
            case 3:
                additional.put(OrderStudentJSONConst.PRACTIC_TYPE, value);
                studentModel.setAdditional(additional.toString());
                getEditCreatedOrderService().updateAdditionalFieldForStudent(studentModel);
                break;
            case 4:
                additional.put(OrderStudentJSONConst.PRACTIC_DATE_FROM, DateConverter.convertDateToString((Date) value));
                studentModel.setAdditional(additional.toString());
                getEditCreatedOrderService().updateAdditionalFieldForStudent(studentModel);
                break;
            case 5:
                additional.put(OrderStudentJSONConst.PRACTIC_DATE_TO, DateConverter.convertDateToString((Date) value));
                studentModel.setAdditional(additional.toString());
                getEditCreatedOrderService().updateAdditionalFieldForStudent(studentModel);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Object getParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        return null;
    }

    @Override
    public String getStringParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        JSONObject additional = new JSONObject(studentModel.getAdditional());

        switch (i) {
            case 1:
                return DateConverter.convertDateToString(studentModel.getFirstDate());
            case 2:
                if (additional.has(OrderStudentJSONConst.PRACTIC_NAME)) {
                    return additional.getString(OrderStudentJSONConst.PRACTIC_NAME);
                }

                return "";
            case 3:
                if (additional.has(OrderStudentJSONConst.PRACTIC_TYPE)) {
                    return additional.getString(OrderStudentJSONConst.PRACTIC_TYPE);
                }

                return "";
            case 4:
                if (additional.has(OrderStudentJSONConst.PRACTIC_DATE_FROM)) {
                    return additional.getString(OrderStudentJSONConst.PRACTIC_DATE_FROM);
                }

                return "";
            case 5:
                if (additional.has(OrderStudentJSONConst.PRACTIC_DATE_TO)) {
                    return additional.getString(OrderStudentJSONConst.PRACTIC_DATE_TO);
                }

                return "";
            default:
                throw new IllegalArgumentException();
        }
    }
}
