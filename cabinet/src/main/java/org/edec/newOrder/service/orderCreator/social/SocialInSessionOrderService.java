package org.edec.newOrder.service.orderCreator.social;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.addStudent.LinkOrderSectionEditModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.addStudent.SearchStudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.newOrder.service.orderCreator.concept.SocialDateGetter;
import org.edec.order.model.StudentToCreateModel;
import org.edec.utility.constants.QualificationConst;
import org.edec.utility.constants.ReferenceType;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.fileManager.FileModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SocialInSessionOrderService extends OrderService {
    @Override
    protected void generateParamModel() {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION;

        orderParams = new ArrayList<>();
        orderParams.add(new OrderCreateParamModel("Дата сдачи сессии с", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Дата сдачи сессии по", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Период сдачи", ComponentEnum.COMBOBOX_SEMESTER, true));

        orderVisualGeneralParams = new ArrayList<>();
        orderVisualGeneralParams.add(new OrderVisualParamModel("Назначена", ComponentEnum.DATEBOX));
        orderVisualGeneralParams.add(new OrderVisualParamModel("Заканчивается", ComponentEnum.DATEBOX));
    }

    @Override
    protected void generateDocumentModel() {
    }

    @Override
    public Long createOrderInDatabase(List<Object> orderParams, List<OrderCreateStudentModel> students) {

        Long idOrder = getOrderCreateService()
                .createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams), getParamsGetter().getDescFromParams(orderParams));

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        //Находим студентов у кого есть справки за выбранный отрезок времени
        List<OrderCreateStudentModel> listStudents = studentsOrderManager
                .getStudentsForSocialInSession((Date) orderParams.get(0), (Date) orderParams.get(1),
                        getParamsGetter().getIdSemesterFromParams(orderParams),
                        (String) orderParams.get(2)
                );

        //Оставляем только тех студентов у которых либо:
        //- студенты на четвертом семестре
        //- студенты на семестре который меньше 4 но закрыли сессию на тройки либо не закрыли вообще
        listStudents = listStudents.stream().filter(student -> ((student.getSemesternumber() - 1) == 4 ||
                        ((student.getSemesternumber() - 1) < 4 &&
                                (student.getSessionResult() < 0 || student.getSessionResult() == 1))))
                .collect(Collectors.toList());

        //Выставляем даты получения стипендии
        //ДАТА 1 - как в старых приказах
        //ДАТА 2 - как в соц приказах по новым справкам
        listStudents = listStudents.stream().map(student -> {

            student.setDateSocialScholarshipFrom(DateConverter.getFirstDayOfNextMonthByCalendar((Date) orderParams.get(0)));

            student.setDateSocialScholarshipTo(SocialDateGetter.getSocialScholarshipDateTo(student));

            return student;
        }).collect(Collectors.toList());

        //Заполняем секции приказа
        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = getOrderCreateService().createEmptySection(idOrder, section.getId(), section.getFoundation(), null, null);

            for (OrderCreateStudentModel student : listStudents) {

                if (section.getName().equals("Инвалиды") && student.getInvalid() ||
                        (section.getName().equals("Сироты") && student.getSirota()) ||
                        (section.getName().equals("Социальная защита") && student.getIndigent()) ||
                        (section.getName().equals("Ветераны") && student.getVeteran())) {

                    getOrderCreateService().createStudentInSection(idLOS, student.getId(), student.getDateSocialScholarshipFrom(),
                            student.getDateSocialScholarshipTo(), student.getGroupname(),
                            student.getSemesternumber(), ""
                    );
                }
            }
        }

        return idOrder;
    }

    @Override
    public boolean createAndAttachOrderDocuments(List<Object> documentParams, List<Object> orderParams, OrderEditModel order) {
        return true;
    }

    @Override
    public void removeStudentFromOrder(Long idLoss, OrderEditModel order) {
        super.removeStudentFromOrder(idLoss, order);

        FileModel pathOrder = new FileModel(
                //FIXME убрать id inst
                FileModel.Inst.getInstById(1L), FileModel.TypeDocument.ORDER, FileModel.SubTypeDocument.SOCIAL, order.getIdSemester(),
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