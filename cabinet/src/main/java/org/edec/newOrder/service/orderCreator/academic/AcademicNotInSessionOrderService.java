package org.edec.newOrder.service.orderCreator.academic;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.addStudent.LinkOrderSectionEditModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.addStudent.SearchStudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.DateUtility;
import org.edec.utility.converter.DateConverter;
import org.hibernate.cfg.NotYetImplementedException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AcademicNotInSessionOrderService extends OrderService {
    @Override
    protected void generateParamModel () {
        groupingInEditEnum = GroupingInEditEnum.BY_GROUP;

        orderParams = new ArrayList<>();
        orderParams.add(new OrderCreateParamModel("Дата сдачи долгов с", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Дата сдачи долгов по", ComponentEnum.DATEBOX, true));

        orderVisualGeneralParams = new ArrayList<>();
        orderVisualGeneralParams.add(new OrderVisualParamModel("Назначена", ComponentEnum.DATEBOX));
        orderVisualGeneralParams.add(new OrderVisualParamModel("Заканчивается", ComponentEnum.DATEBOX));
    }

    @Override
    protected void generateDocumentModel () {
        // TODO
    }

    @Override
    public Long createOrderInDatabase (List<Object> orderParams, List<OrderCreateStudentModel> students) {
        Long idOrder = getOrderCreateService()
                .createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams), getParamsGetter().getDescFromParams(orderParams));

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        List<OrderCreateStudentModel> studentList = studentsOrderManager.getStudentsForAcademicalScholarshipNotInSession((Date)orderParams.get(0), (Date)orderParams.get(1), getParamsGetter().getIdSemesterFromParams(orderParams));

        for(OrderCreateOrderSectionModel sectionModel : listSections) {
            Long idLOS = getOrderCreateService().createEmptySection(idOrder, sectionModel.getId(), sectionModel.getFoundation(), null, null);

            for(OrderCreateStudentModel studentModel : studentList) {
                if(sectionModel.getName().equals("Отлично") && studentModel.getSessionResult() == 4
                        || sectionModel.getName().equals("\"Отлично\" и \"хорошо\"") && studentModel.getSessionResult() == 3
                        || sectionModel.getName().equals("Хорошо") && studentModel.getSessionResult() == 2) {
                    getOrderCreateService().createStudentInSection(idLOS, studentModel.getId(),
                            studentModel.getDateCompleteSession() != null
                                    ? DateConverter.getFirstDayOfNextMonth(studentModel.getDateCompleteSession())
                                    : DateConverter.getFirstDayOfNextMonth(new Date()),
                            DateConverter.getLastDayOfMonth(studentModel.getDateNextEndOfSession()), studentModel.getGroupname(),
                            studentModel.getSemesternumber(), "{}"
                    );
                }
            }
        }

        return idOrder;
    }

    //
    private List<OrderCreateStudentModel> groupedList(List<OrderCreateStudentModel> studentList){
        List<OrderCreateStudentModel> list = new ArrayList<>();

        OrderCreateStudentModel prevStudent = null;
        for(OrderCreateStudentModel student : studentList){
            if(prevStudent == null){
                prevStudent = student;
            }

            if(!student.getId().equals(prevStudent.getId())){
                list.add(prevStudent);
                prevStudent = student;
            }
        }

        if(prevStudent!=null) list.add(prevStudent);

        return list;
    }

    @Override
    public boolean createAndAttachOrderDocuments(List<Object> documentParams, List<Object> orderParams, OrderEditModel order) {
        // TODO

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
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void removeStudentFromOrder (Long idLoss, OrderEditModel order) {
        super.removeStudentFromOrder(idLoss, order);
    }

    @Override
    public Object getParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        switch (i) {
            case 1:
                return studentModel.getFirstDate();
            case 2:
                return studentModel.getSecondDate();
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
            default:
                throw new IllegalArgumentException();
        }
    }
}