package org.edec.newOrder.service.orderCreator.transfer;

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
import org.edec.utility.constants.SemesterConst;
import org.edec.utility.converter.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class TransferOrderService extends OrderService {

    @Override
    protected void generateParamModel() {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION;
        orderParams = new ArrayList<>();
        orderParams.add(new OrderCreateParamModel("Срок сдачи с", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Срок сдачи по", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Перевести с", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Курс", ComponentEnum.COMBOBOX_COURSE, true));
        orderVisualGeneralParams = new ArrayList<>();
        orderVisualGeneralParams.add(new OrderVisualParamModel("Перевести с", ComponentEnum.DATEBOX));
    }

    @Override
    protected void generateDocumentModel() {
    }

    @Override
    public Long createOrderInDatabase(List<Object> orderParams, List<OrderCreateStudentModel> students) {
        Long idOrder = getOrderCreateService()
                .createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams),
                                  getParamsGetter().getDescFromParams(orderParams));

        List<String> attributes = new ArrayList<String>();
        Long idSemester = getParamsGetter().getIdSemesterFromParams(orderParams);
        attributes.add(managerESO.getSchoolYearBySemesterId(idSemester));
        getOrderCreateService().updateOrderAttr(idOrder, attributes);

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        /*List<OrderCreateStudentModel> studentList = studentsOrderManager
                .getStudentsForAcademicalScholarshipInSession((Date) orderParams.get(0),
                                                              (Date) orderParams.get(1),
                                                              SemesterConst.SPRING,
                                                              Integer.parseInt((String) orderParams.get(3)),
                                                              false); // не бывает выпускников в переводном?
         */
        List<OrderCreateStudentModel> studentList = studentsOrderManager.getStudentForTransfer((Date) orderParams.get(0),
                                                                                               (Date) orderParams.get(1),
                                                                                               Integer.parseInt((String) orderParams.get(3)));

        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = getOrderCreateService().createEmptySection(idOrder, section.getId(), section.getFoundation(), null, null);

            for (OrderCreateStudentModel studentModel : studentList) {

                if ((section.getName().contains("Бюджет") && studentModel.getNextGovernmentFinanced() ||
                     section.getName().contains("Договор") && !studentModel.getNextGovernmentFinanced())) {
                    getOrderCreateService().createStudentInSection(idLOS,
                                                                   studentModel.getId(),
                                                                   (Date) orderParams.get(2),
                                                                   null,
                                                                   studentModel.getGroupname(),
                                                                   "{\"semester\": \"" + getParamsGetter().getIdSemesterFromParams(orderParams) + "\"}"
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
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Object getParamForStudent(Integer i, StudentModel studentModel, Long idOS) {
        switch (i) {
            case 1:
                return studentModel.getFirstDate();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public String getStringParamForStudent(Integer i, StudentModel studentModel, Long idOS) {
        switch (i) {
            case 1:
                return DateConverter.convertDateToString(studentModel.getFirstDate());
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
            if (selectedStudent.getProlongationEndDate().before(new Date())) {
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