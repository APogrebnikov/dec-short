package org.edec.order.service.orderCreator;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.order.manager.CreateOrderManagerESO;
import org.edec.order.model.OrderRuleModel;
import org.edec.order.model.OrderSectionModel;
import org.edec.order.model.StudentToCreateModel;
import org.edec.utility.converter.DateConverter;
import org.zkoss.zk.ui.Executions;

import java.util.Date;
import java.util.List;


public class AcademicalCreator {
    CreateOrderManagerESO managerCreate = new CreateOrderManagerESO();

    public Long createAcademicalOrderNotInSession (OrderRuleModel rule, Date firstDate, Date secondDate, Date dateStartScholarship,
                                                   String description, Long idSemester) {
        List<OrderSectionModel> listSections = managerCreate.getListOrderSection(rule.getId());

        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        Long idOrder = managerCreate.createEmptyOrder(rule.getId(), new Date(), idSemester, idHumanface, description);

        List<StudentToCreateModel> listMarkFour = managerCreate.getStudentsForAcademicalNotInSession(firstDate, secondDate, idSemester, 2);
        List<StudentToCreateModel> listMarkFourAndFive = managerCreate.getStudentsForAcademicalNotInSession(
                firstDate, secondDate, idSemester, 3);
        List<StudentToCreateModel> listMarkFive = managerCreate.getStudentsForAcademicalNotInSession(firstDate, secondDate, idSemester, 4);

        for (OrderSectionModel item : listSections) {
            Long idLOS = managerCreate.createLinkOrderSection(idOrder, item.getId(), null, new Date(), new Date());

            switch (item.getName()) {
                case "Отлично":
                    for (StudentToCreateModel student : listMarkFive) {
                        if (!student.getNextGovernmentFinanced()) {
                            continue;
                        }

                        Date endDate = student.getDateNextEndOfSession() == null ? new Date() : student.getDateNextEndOfSession();
                        endDate = DateConverter.getLastDayOfMonth(endDate);

                        if (endDate.before(new Date(firstDate.getYear(), firstDate.getMonth() + 1, 1))) {
                            continue;
                        }

                        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(), dateStartScholarship == null
                                                                                           ? new Date(
                                                                           new Date().getYear(), new Date().getMonth(), 1)
                                                                                           : dateStartScholarship, endDate,
                                                                   student.getGroupname(), ""
                        );
                    }
                    break;
                case "\"Отлично\" и \"хорошо\"":
                    for (StudentToCreateModel student : listMarkFourAndFive) {
                        if (!student.getNextGovernmentFinanced()) {
                            continue;
                        }

                        Date endDate = student.getDateNextEndOfSession() == null ? new Date() : student.getDateNextEndOfSession();
                        endDate = DateConverter.getLastDayOfMonth(endDate);

                        if (endDate.before(new Date(firstDate.getYear(), firstDate.getMonth() + 1, 1))) {
                            continue;
                        }

                        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(), dateStartScholarship == null
                                                                                           ? new Date(
                                                                           new Date().getYear(), new Date().getMonth(), 1)
                                                                                           : dateStartScholarship, endDate,
                                                                   student.getGroupname(), ""
                        );
                    }
                    break;
                case "Хорошо":

                    for (StudentToCreateModel student : listMarkFour) {
                        if (!student.getNextGovernmentFinanced()) {
                            continue;
                        }

                        Date endDate = student.getDateNextEndOfSession() == null ? new Date() : student.getDateNextEndOfSession();
                        endDate = DateConverter.getLastDayOfMonth(endDate);

                        if (endDate.before(new Date(firstDate.getYear(), firstDate.getMonth() + 1, 1))) {
                            continue;
                        }

                        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(), dateStartScholarship == null
                                                                                           ? new Date(
                                                                           new Date().getYear(), new Date().getMonth(), 1)
                                                                                           : dateStartScholarship, endDate,
                                                                   student.getGroupname(), ""
                        );
                    }
                    break;
            }
        }

        return idOrder;
    }

    public Long createAcademicalOrderInSession (OrderRuleModel rule, Date firstDate, Date secondDate, String description, Long idSemester) {
        List<OrderSectionModel> listSections = managerCreate.getListOrderSection(rule.getId());

        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        Long idOrder = managerCreate.createEmptyOrder(rule.getId(), new Date(), idSemester, idHumanface, description);

        List<StudentToCreateModel> listMarkFour = managerCreate.getStudentsForAcademicalInSession(firstDate, secondDate, idSemester, 2);
        List<StudentToCreateModel> listMarkFourAndFive = managerCreate.getStudentsForAcademicalInSession(
                firstDate, secondDate, idSemester, 3);
        List<StudentToCreateModel> listMarkFive = managerCreate.getStudentsForAcademicalInSession(firstDate, secondDate, idSemester, 4);
        List<StudentToCreateModel> listMarkProlongation = managerCreate.getStudentsForAcademicalInSessionWithProlongation(
                firstDate, secondDate, idSemester);

        for (OrderSectionModel item : listSections) {
            Long idLOS = managerCreate.createLinkOrderSection(idOrder, item.getId(), null, new Date(), new Date());

            switch (item.getName()) {
                case "Отлично":
                    for (StudentToCreateModel student : listMarkFive) {
                        createStudentInOrder(idLOS, student);
                    }
                    break;
                case "\"Отлично\" и \"хорошо\"":
                    for (StudentToCreateModel student : listMarkFourAndFive) {
                        createStudentInOrder(idLOS, student);
                    }
                    break;
                case "Хорошо":
                    for (StudentToCreateModel student : listMarkFour) {
                        createStudentInOrder(idLOS, student);
                    }
                    break;
                case "Продление(Отлично)":
                    for (StudentToCreateModel student : listMarkProlongation) {
                        if (!checkNextGovernmentAndNotDeducted(student)) {
                            continue;
                        }

                        if (student.getSessionResultPrev() == null || student.getSessionResultPrev() != 4) {
                            continue;
                        }

                        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(),
                                                                   new Date(new Date().getYear(), new Date().getMonth(), 1),
                                                                   DateConverter.getLastDayOfMonth(student.getProlongationEndDate()),
                                                                   student.getGroupname(), ""
                        );
                    }
                    break;
                case "Продление(Хорошо и Отлично)":
                    for (StudentToCreateModel student : listMarkProlongation) {
                        if (!checkNextGovernmentAndNotDeducted(student)) {
                            continue;
                        }

                        if (student.getSessionResultPrev() == null || student.getSessionResultPrev() != 3) {
                            continue;
                        }

                        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(),
                                                                   new Date(new Date().getYear(), new Date().getMonth(), 1),
                                                                   DateConverter.getLastDayOfMonth(student.getProlongationEndDate()),
                                                                   student.getGroupname(), ""
                        );
                    }
                    break;
                case "Продление(Хорошо)":
                    for (StudentToCreateModel student : listMarkProlongation) {
                        if (!checkNextGovernmentAndNotDeducted(student)) {
                            continue;
                        }

                        if (student.getSessionResultPrev() == null || student.getSessionResultPrev() != 2) {
                            continue;
                        }

                        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(),
                                                                   new Date(new Date().getYear(), new Date().getMonth(), 1),
                                                                   DateConverter.getLastDayOfMonth(student.getProlongationEndDate()),
                                                                   student.getGroupname(), ""
                        );
                    }
                    break;
            }
        }

        return idOrder;
    }

    private boolean checkNextGovernmentAndNotDeducted (StudentToCreateModel student) {
        if (student.getNextGovernmentFinanced() != null && student.getNextGovernmentFinanced().equals(false)) {
            return false;
        }

        if (student.getDeductedCurSem() != null && student.getDeductedCurSem().equals(true)) {
            return false;
        }

        return true;
    }

    private Date getSecondDate (Date date) {
        if (date == null) {
            return new Date(new Date().getYear(), 5, 30);
        } else {
            return DateConverter.getLastDayOfMonth(date);
        }
    }

    private void createStudentInOrder (Long idLOS, StudentToCreateModel student) {
        if (!checkNextGovernmentAndNotDeducted(student)) {
            return;
        }

        if (getSecondDate(student.getDateNextEndOfSession()).before(new Date(new Date().getYear(), new Date().getMonth(), 1))) {
            return;
        }

        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(), new Date(new Date().getYear(), new Date().getMonth(), 1),
                                                   getSecondDate(student.getDateNextEndOfSession()), student.getGroupname(), ""
        );
    }
}
