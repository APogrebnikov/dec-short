package org.edec.newOrder.service.orderCreator.academic;


import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.service.esoImpl.CreateOrderServiceESO;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.order.manager.CreateOrderManagerESO;
import org.edec.utility.constants.OrderStudentJSONConst;
import org.edec.utility.constants.QualificationConst;
import org.edec.utility.converter.DateConverter;
import org.json.JSONObject;
import org.zkoss.util.media.Media;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AcademicIncreasedOrderService extends OrderService {

    private final int PARAM_PERIOD = 0;
    private final int PARAM_FILE = 1;

    @Override
    protected void generateParamModel () {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION;

        orderParams = new ArrayList<>();
        orderParams.add(new OrderCreateParamModel("Семестр", ComponentEnum.COMBOBOX_SEMESTER, true));
        orderParams.add(new OrderCreateParamModel("Файл", ComponentEnum.FILE, true));

        orderVisualGeneralParams = new ArrayList<>();
        orderVisualGeneralParams.add(new OrderVisualParamModel("Назначить с", ComponentEnum.DATEBOX));
        orderVisualGeneralParams.add(new OrderVisualParamModel("Назначить по", ComponentEnum.DATEBOX));
        orderVisualGeneralParams.add(new OrderVisualParamModel("Оценка", ComponentEnum.TEXTBOX));

        isFilesNeeded = false;
    }

    @Override
    protected void generateDocumentModel () {}

    @Override
    public Long createOrderInDatabase (List<Object> orderParams, List<OrderCreateStudentModel> students) {

        CreateOrderManagerESO managerCreate = new CreateOrderManagerESO();
        Long idCurrentSem = getParamsGetter().getIdSemesterFromParams(orderParams);
        Long idSemCheck = idCurrentSem;
        Long idSemForAcademicIncreased = managerCreate.getNextSemester(idCurrentSem);
        Long idOrder = getOrderCreateService().createEmptyOrder(idSemCheck, getParamsGetter().getDescFromParams(orderParams));

        if (orderParams.get(PARAM_PERIOD).equals("Весна")){
            idSemForAcademicIncreased = idCurrentSem;
            idSemCheck = managerCreate.getPrevSemester(idCurrentSem);
        }

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        CreateOrderServiceESO orderServiceESO = new CreateOrderServiceESO();

        try {
            students = orderServiceESO.parseForAcademicIncreasedOrder(((Media)orderParams.get(PARAM_FILE)), idSemCheck, idSemForAcademicIncreased);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        StringBuilder warningAboutDebtors = new StringBuilder("Следующие студенты имеет задолжности или оценки удовлетворительно:\n");
        boolean checkWarningAboutDebtors = false;

        for (int i = 0; i < students.size(); i++) {
            OrderCreateStudentModel student = students.get(i);
            if(student.getId().equals(-1L)) {
                checkWarningAboutDebtors = true;
                warningAboutDebtors.append(student.getFio()).append(", группа ").append(student.getGroupname()).append("\n");
            }
        }

        if (checkWarningAboutDebtors) {
          Messagebox.show(warningAboutDebtors.toString());
        }


        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = getOrderCreateService().createEmptySection(idOrder, section.getId(), section.getFoundation(), null, null);

            for (OrderCreateStudentModel student : students) {
                if (student.getNomination().equals(section.getName()) && !identifyStringSessionResult(student.getSessionResult()).equals("")){
                    getOrderCreateService().createStudentInSection(idLOS, student.getId(),
                                                                   DateConverter.getFirstDayOfNextMonth(student.getDateOfEndSession()),
                                                                   student.getQualification().equals(QualificationConst.MASTER.getValue()) && student.getCourse() == 2 && orderParams.get(PARAM_PERIOD).equals("Весна") ? student.getDateNextEndOfSession() :DateConverter.getLastDayOfMonth(student.getDateNextEndOfSession()),
                                                                   student.getGroupname(),
                            "{\"sessionResult\":\""+ identifyStringSessionResult(student.getSessionResult())+"\"," +
                            "\"nomination\":\"" + student.getNomination() +"\"}");
                }
            }
        }

        return idOrder;
    }

    public String identifyStringSessionResult(Integer sessionResult)
    {
        switch (sessionResult) {
            case 4:
                return "отлично";
            case 3:
                return "хорошо и отлично";
            case 2:
                return "хорошо";
            default:
                return "";
        }
    }

    @Override
    public boolean createAndAttachOrderDocuments (List<Object> documentParams, List<Object> orderParams, OrderEditModel order) {
        return true;
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
            case 3:
                JSONObject additional = new JSONObject(studentModel.getAdditional());
                additional.put(OrderStudentJSONConst.SESSION_RESULT, value);
                studentModel.setAdditional(additional.toString());
                getEditCreatedOrderService().updateAdditionalFieldForStudent(studentModel);
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
            case 3:
                return getResultSession(studentModel.getAdditional());
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
            case 3:
                return getResultSession(studentModel.getAdditional());
            default:
                throw new IllegalArgumentException();
        }
    }

    private String getResultSession(String json){ ;
        String resultSession = "";
        if (json != null && !json.equals("")) {
            JSONObject additionalJSON = new JSONObject(json);

            if (additionalJSON.has(OrderStudentJSONConst.SESSION_RESULT)) {
                resultSession = additionalJSON.getString(OrderStudentJSONConst.SESSION_RESULT);
            }
        }
        return resultSession;
    }


}
