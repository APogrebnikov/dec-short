package org.edec.newOrder.service.orderCreator.social;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.constants.QualificationConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.fileManager.FileModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SocialIncreasedInSessionOrderService extends OrderService {
    @Override
    protected void generateParamModel() {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION;

        orderParams = new ArrayList<>();
        orderParams.add(new OrderCreateParamModel("Дата сдачи с", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Дата сдачи по", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Период сдачи", ComponentEnum.COMBOBOX_SEMESTER, true));

        orderVisualGeneralParams = new ArrayList<>();
        orderVisualGeneralParams.add(new OrderVisualParamModel("Назначена", ComponentEnum.DATEBOX));
        orderVisualGeneralParams.add(new OrderVisualParamModel("Заканчивается", ComponentEnum.DATEBOX));
    }

    @Override
    protected void generateDocumentModel() {

    }

    @Override
    public boolean createAndAttachOrderDocuments(List<Object> documentParams, List<Object> orderParams, OrderEditModel order) {
        return true;
    }

    @Override
    public Long createOrderInDatabase(List<Object> orderParams, List<OrderCreateStudentModel> students) {
        Long idOrder = getOrderCreateService()
                .createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams), getParamsGetter().getDescFromParams(orderParams));

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        //Находим студентов у кого есть справки за выбранный отрезок времени
        List<OrderCreateStudentModel> studentList = studentsOrderManager
                .getStudentsForSocialIncreasedAfterSession((Date) orderParams.get(0),
                                                           (Date) orderParams.get(1),
                                                           (String) orderParams.get(2));

        studentList = studentList.stream()
                                 .filter(student ->
                                                 (student.getQualification().equals(QualificationConst.SPECIALIST.getValue())
                                                  || student.getQualification().equals(QualificationConst.BACHELOR.getValue()))
                                                 && ((student.getSemesternumber() >= 2) && student.getSemesternumber() <= 4
                                                     && student.getSessionResult() > 1))
                                 .collect(Collectors.toList());

        //Выставляем даты получения стипендии
        //ДАТА1 - с 1 числа следующего месяца от ликвидации долгов(dateCompleteSession)
        //ДАТА2 - последнее число месяца окончания следующей сессии

        studentList = studentList.stream().peek(student -> {
            Date secondDate;

            if (student.getDateReferenceTo() == null) {
                secondDate = DateConverter.getLastDayOfMonth(student.getDateOfEndSession());
            } else {
                secondDate = student.getDateReferenceTo().before(DateConverter.getLastDayOfMonth(student.getDateOfEndSession())) ? student.getDateReferenceTo() : DateConverter.getLastDayOfMonth(student.getDateOfEndSession());
                /*
                if(student.getSemesternumber() == 4) { // Для второго курса даты окончания - начало 3го курса
                    secondDate = student.getDateReferenceTo().before(DateConverter.getLastDayOfMonth(student.getDateOfEndSemester())) ? student.getDateReferenceTo() : DateConverter.getLastDayOfMonth(student.getDateOfEndSemester());
                } else {
                    secondDate = student.getDateReferenceTo().before(DateConverter.getLastDayOfMonth(student.getDateOfEndSession())) ? student.getDateReferenceTo() : DateConverter.getLastDayOfMonth(student.getDateOfEndSession());
                }
                */
            }

            student.setFirstDate(DateConverter.getFirstDayOfNextMonth(student.getDateCompleteSession()));
            student.setSecondDate(secondDate);
        }).collect(Collectors.toList());

        //Заполняем секции приказа
        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = getOrderCreateService().createEmptySection(idOrder, section.getId(), section.getFoundation(), null, null);

            for (OrderCreateStudentModel student : studentList) {
                if(section.getName().equals("Назначить")) {
                    getOrderCreateService().createStudentInSection(idLOS, student.getId(), student.getFirstDate(),
                                                                   student.getSecondDate(), student.getGroupname(),
                                                                   student.getSemesternumber(), ""
                    );
                } else if((section.getName().contains("Отменить выплаты(Сироты)") && student.getSirota()
                        || section.getName().contains("Отменить выплаты(Инвалиды)") && student.getInvalid()
                        || section.getName().contains("Отменить выплаты(Ветераны)") && student.getVeteran()
                        || section.getName().contains("Отменить выплаты(Социальная защита)") && student.getIndigent()) && student.getDateSocialScholarshipTo() !=null && student.getDateSocialScholarshipTo().after(new Date())){

                    getOrderCreateService().createStudentInSection(idLOS, student.getId(), student.getFirstDate(),
                                                                   null, student.getGroupname(),
                                                                   student.getSemesternumber(), "{}"
                    );
                }


            }
        }

        return idOrder;
    }

    @Override
    public void removeStudentFromOrder(Long idLoss, OrderEditModel order) {
        super.removeStudentFromOrder(idLoss, order);

        FileModel pathOrder = new FileModel(FileModel.Inst.getInstById(1L), FileModel.TypeDocument.ORDER,
                FileModel.SubTypeDocument.SOCIAL_INCREASE, order.getIdSemester(),
                Long.toString(order.getIdOrder())
        );

        new FileManager().removeReferenceFromOrder(pathOrder, getEditCreatedOrderService().getFioByIdLoss(idLoss));
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
            default:
                throw new IllegalArgumentException();
        }

    }

    @Override
    public Object getParamForStudent(Integer i, StudentModel studentModel, Long idOS) {
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
    public String getStringParamForStudent(Integer i, StudentModel studentModel, Long idOS) {
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
