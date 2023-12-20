package org.edec.newOrder.service.orderCreator.academic;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.converter.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AcademicInSessionOrderService extends OrderService {
    @Override
    protected void generateParamModel() {
        groupingInEditEnum = GroupingInEditEnum.BY_GROUP;

        orderParams = new ArrayList<>();

        orderParams.add(new OrderCreateParamModel("Дата с", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Дата по", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Период сдачи" , ComponentEnum.COMBOBOX_SEMESTER, true));
        orderParams.add(new OrderCreateParamModel("Выпускники", ComponentEnum.CHECKBOX, true));

        orderVisualGeneralParams = new ArrayList<>();
        orderVisualGeneralParams.add(new OrderVisualParamModel("Дата с", ComponentEnum.DATEBOX));
        orderVisualGeneralParams.add(new OrderVisualParamModel("Дата по", ComponentEnum.DATEBOX));
    }

    @Override
    protected void generateDocumentModel() {
        // TODO
    }

    @Override
    public Long createOrderInDatabase(List<Object> orderParams, List<OrderCreateStudentModel> students) {
        Long idOrder = getOrderCreateService()
                .createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams), getParamsGetter().getDescFromParams(orderParams));

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        //Получаем студентов всех студентов (и с продлением)
        List<OrderCreateStudentModel> studentList = studentsOrderManager
                .getStudentsForAcademicalScholarshipInSession((Date) orderParams.get(0),
                                                              (Date) orderParams.get(1),
                                                              (String) orderParams.get(2),
                                                              null,
                                                              (Boolean) orderParams.get(3));
        if(!(Boolean)orderParams.get(3)){
            studentList = studentList.stream().filter(OrderCreateStudentModel::getNextGovernmentFinanced).collect(Collectors.toList());
        }

        //Получаем студентов для секций продления
        //TODO: Могут возникнуть ошибки с получением выпускников с продлением -
        // исправить получение семестров по необходимости
        /*List<OrderCreateStudentModel> studentListWithProlongation = getStudentsForOrder(studentsOrderManager
                                                                                                .getStudentsForAcademicalScholarshipInSessionWithProlongation(
                                                                                                        (Date) orderParams.get(0),
                                                                                                        (Date) orderParams.get(1),
                                                                                                        (String) orderParams.get(2)));*/

        //studentList.addAll(studentListWithProlongation);
        // получаем студентов с продлением
        List<OrderCreateStudentModel> studentListWithProlongation = studentList.stream().filter(OrderCreateStudentModel::getIsSessionProlongation).collect(Collectors.toList());
                /*studentListWithProlongation.stream().filter(OrderCreateStudentModel::getNextGovernmentFinanced)
                                                                 .collect(Collectors.toList());*/
        // оставляем без продления
        studentList = studentList.stream().filter(orderTmp -> orderTmp.getIsSessionProlongation() == false).collect(Collectors.toList());

        //Смотрим, чтобы на текущий момент студент обучался на бюджете
        studentList = studentList.stream().filter(OrderCreateStudentModel::getNextGovernmentFinanced).collect(Collectors.toList());

        for (OrderCreateOrderSectionModel sectionModel : listSections) {
            Long idLOS = getOrderCreateService()
                    .createEmptySection(idOrder, sectionModel.getId(), sectionModel.getFoundation(), null, null);

            for (OrderCreateStudentModel studentModel : studentList) {

                if ((sectionModel.getName().equals("Отлично") && studentModel.getSessionResult() == 4 ||
                     sectionModel.getName().equals("\"Отлично\" и \"хорошо\"") && studentModel.getSessionResult() == 3 ||
                     sectionModel.getName().equals("Хорошо") && studentModel.getSessionResult() == 2)) {
                    getOrderCreateService()
                            .createStudentInSection(idLOS,
                                                    studentModel.getId(),
                                                    DateConverter.getFirstDayOfNextMonth(studentModel.getDateOfEndSession()),
                                                    (Boolean) orderParams.get(3) ? studentModel.getDateOfCertificationTo() : DateConverter.getLastDayOfMonth(studentModel.getDateOfEndCurSession()),
                                                    studentModel.getGroupname(),
                                                    studentModel.getSemesternumber(),
                                                    "{}"
                            );
                }
            }

            for (OrderCreateStudentModel studentModel : studentListWithProlongation) {
                if (studentModel.getIsSessionProlongation() &&
                    (sectionModel.getName().equals("Продление(Отлично)") && studentModel.getSessionResult() == 4 ||
                     sectionModel.getName().equals("Продление(Хорошо и Отлично)") && studentModel.getSessionResult() == 3 ||
                     sectionModel.getName().equals("Продление(Хорошо)") && studentModel.getSessionResult() == 2)) {
                    getOrderCreateService()
                            .createStudentInSection(idLOS, studentModel.getId(), DateConverter.getFirstDayOfNextMonth(studentModel.getDateOfEndSession()),
                                                    DateConverter.getLastDayOfMonth(studentModel.getProlongationEndDate()),
                                                    studentModel.getGroupname(), studentModel.getSemesternumber(), "{}"
                            );
                }
            }
        }

        return idOrder;
    }

    @Override
    public boolean createAndAttachOrderDocuments(List<Object> documentParams, List<Object> orderParams, OrderEditModel order) {
        // TODO

        return true;
    }

    @Override
    public void removeStudentFromOrder(Long idLoss, OrderEditModel order) {

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
                return DateConverter.convertDateToString(studentModel.getFirstDate());
            case 2:
                return DateConverter.convertDateToString(studentModel.getSecondDate());
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

    private List<OrderCreateStudentModel> getStudentsForOrder(List<OrderCreateStudentModel> studentList) {

        //Сгруппировать по студентам, и собрать все их srh
        List<OrderCreateStudentModel> groupedStudentList = new ArrayList<>();

        if (studentList.size() == 0) {
            return groupedStudentList;
        }

        OrderCreateStudentModel student = null;

        //Данный кусок кода необходим, чтобы выявить студентов, которые успели пересдать предметы,
        //по которым у них стояла неявка до конца сессии, и поставить им последнуюю оценку
        //VERY VERY BAD CODE
        for (int i = 0; i < studentList.size(); i++) {
            for (int j = 0; j < studentList.size(); j++) {
                if (studentList.get(i).getId().equals(studentList.get(j).getId())) {
                    if (studentList.get(i).getIdSessionRating().equals(studentList.get(j).getIdSessionRating())) {
                        if (studentList.get(i).getRating() > studentList.get(j).getRating()) {
                            studentList.get(j).setRating(studentList.get(i).getRating());
                        } else {
                            studentList.get(i).setRating(studentList.get(j).getRating());
                        }
                    }
                }
            }
        }

        for (OrderCreateStudentModel selectedStudent : studentList) {
            if (student != null) {
                if (student.getId().equals(selectedStudent.getId())) {
                    student.getRatingsList().add(selectedStudent.getRating());
                } else {
                    groupedStudentList.add(student);

                    student = selectedStudent;
                    student.getRatingsList().add(student.getRating());
                }
            } else {
                student = selectedStudent;
                student.getRatingsList().add(student.getRating());
            }
        }

        groupedStudentList.add(student);

        //Оставляем в списке только тех студентов, кому нужно продление,
        //то есть среди оценок неявки, либо 4 или 5
        groupedStudentList = groupedStudentList.stream().filter(selectedStudent -> {

            //проверяем, есть ли продление
            if (!selectedStudent.getIsSessionProlongation()) {
                return false;
            }

            //проверяем, действует ли продление на текущий момент
            if (selectedStudent.getProlongationEndDate() != null && selectedStudent.getProlongationEndDate().before(new Date())) {
                return false;
            }

            //проверяем, чтобы студент не был перваком, такие нам не нужны в приказе
            //и закрыл предпредыдущий семестр на 4 или 5 (чтобы он получал стипендию, которую собираемся продлевать)
            if (selectedStudent.getSessionResult() == null || selectedStudent.getSessionResult() <= 1) {
                return false;
            }

            //проверяем, чтобы у студента за предыдущий семестр, была хотя одна или больше неявок,
            //а оставшиеся оценки были 4 либо 5
            int notAppearCounter = 0;

            for (Integer rating : selectedStudent.getRatingsList()) {
                if (rating == -3) {
                    notAppearCounter++;
                }

                if (!fitForProlongation(rating)) {
                    return false;
                }
            }

            return notAppearCounter != 0;
        }).collect(Collectors.toList());

        return groupedStudentList;
    }

    private boolean fitForProlongation(int rating) {
        return (rating == -3 || rating == 1 || rating == 4 || rating == 5);
    }
}