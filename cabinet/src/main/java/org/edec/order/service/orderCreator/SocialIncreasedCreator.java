package org.edec.order.service.orderCreator;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.order.manager.CreateOrderManagerESO;
import org.edec.order.model.OrderRuleModel;
import org.edec.order.model.OrderSectionModel;
import org.edec.order.model.StudentToCreateModel;
import org.edec.order.model.dao.ReferenceModelESO;
import org.edec.order.service.CreateOrderService;
import org.edec.order.service.impl.CreateOrderServiceESO;
import org.edec.utility.converter.DateConverter;
import org.zkoss.zk.ui.Executions;

import java.util.Date;
import java.util.List;


public class SocialIncreasedCreator {
    private final String CANCEL_SCHOLARSHIP = "Отменить";
    private final String ASSIGN_SCHOLARSHIP = "Назначить";

    CreateOrderManagerESO managerCreate = new CreateOrderManagerESO();
    CreateOrderService service = new CreateOrderServiceESO();

    public Long createSocialIncreasedOrderNewReference (OrderRuleModel rule, Date firstDate, Date secondDate, String description,
                                                        Long idSemester, Boolean inSession) {
        List<OrderSectionModel> listSections = managerCreate.getListOrderSection(rule.getId());

        List<StudentToCreateModel> listStudents = managerCreate.getStudentsForSocialIncreasedOrderNewReference(
                firstDate, secondDate, service.getNeededSemester(
                        idSemester, inSession));

        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        Long idOrder = managerCreate.createEmptyOrder(rule.getId(), new Date(), idSemester, idHumanface, description);

        for (OrderSectionModel item : listSections) {
            Long idLOS = managerCreate.createLinkOrderSection(idOrder, item.getId(), null, new Date(),
                                                              item.getName().equals(ASSIGN_SCHOLARSHIP) ? new Date() : null
            );

            for (StudentToCreateModel student : listStudents) {
                if (item.getName().equals(ASSIGN_SCHOLARSHIP)) {
                    if (student.getSessionResult() > 1) {
                        if (student.getSecondDate() != null && student.getSecondDate().before(student.getDateOfEndSession())) {
                            List<ReferenceModelESO> listReference = managerCreate.getAllReferenceByStudentcard(student.getIdStudentcard());

                            Date firstDateStudent = student.getFirstDate();

                            if (listReference.size() > 1) {
                                //берем предыдущую справку
                                ReferenceModelESO reference = listReference.get(1);

                                Date dateFinish = DateConverter.getFirstDayOfNextMonthByCalendar(reference.getDateFinish());

                                if (firstDateStudent.before(dateFinish)) {
                                    firstDateStudent = dateFinish;
                                }
                            }

                            managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(), firstDateStudent,
                                                                       DateConverter.getLastDayOfMonth(student.getSecondDate()),
                                                                       student.getGroupname(), ""
                            );
                        } else {
                            List<ReferenceModelESO> listReference = managerCreate.getAllReferenceByStudentcard(student.getIdStudentcard());

                            Date firstDateStudent = student.getFirstDate();

                            if (listReference.size() > 1) {
                                //берем предыдущую справку
                                ReferenceModelESO reference = listReference.get(1);

                                Date dateFinish = DateConverter.getFirstDayOfNextMonthByCalendar(reference.getDateFinish());

                                if (firstDateStudent.before(dateFinish)) {
                                    firstDateStudent = dateFinish;
                                }
                            }

                            managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(), firstDateStudent,
                                                                       student.getDateOfEndSession() == null
                                                                       ? new Date()
                                                                       : DateConverter.getLastDayOfMonth(student.getDateNextEndOfSession()),
                                                                       student.getGroupname(), ""
                            );
                        }
                    }
                }
            }
        }

        return idOrder;
    }

    public Long createSocialIncreasedOrderNotInSession (OrderRuleModel rule, Date firstDate, Date secondDate, String description,
                                                        Long idSemester) {
        List<OrderSectionModel> listSections = managerCreate.getListOrderSection(rule.getId());

        List<StudentToCreateModel> listStudents = managerCreate.getStudentsForSocialIncreasedOrderNotInSession(
                firstDate, secondDate, managerCreate.getPrevSemester(idSemester));

        for (int i = 0; i < listStudents.size(); i++) {
            StudentToCreateModel student = listStudents.get(i);

            for (int j = i + 1; j < listStudents.size(); j++) {
                if (student.getId().equals(listStudents.get(j).getId())) {
                    if (student.getFirstDate().after(listStudents.get(j).getFirstDate())) {
                        listStudents.remove(j);
                        j--;
                    } else {
                        listStudents.remove(student);
                        i--;
                        break;
                    }
                }
            }
        }

        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        Long idOrder = managerCreate.createEmptyOrder(rule.getId(), new Date(), idSemester, idHumanface, description);

        for (OrderSectionModel item : listSections) {
            Long idLOS = managerCreate.createLinkOrderSection(idOrder, item.getId(), null, new Date(),
                                                              item.getName().equals(ASSIGN_SCHOLARSHIP) ? new Date() : null
            );

            for (StudentToCreateModel student : listStudents) {
                if (item.getName().equals(ASSIGN_SCHOLARSHIP)) {
                    if (student.getSecondDate() != null && student.getSecondDate().before(student.getDateNextEndOfSession())) {
                        Date secondDateStudent = student.getFirstDate().before(new Date(2017 - 1900, 0, 1))
                                                 ? DateConverter.getLastDayOfMonth(student.getSecondDate())
                                                 : student.getSecondDate();

                        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(),
                                                                   DateConverter.getFirstDayOfNextMonthByCalendar(firstDate),
                                                                   secondDateStudent, student.getGroupname(), ""
                        );
                    } else {
                        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(),
                                                                   DateConverter.getFirstDayOfNextMonthByCalendar(firstDate),
                                                                   student.getDateNextEndOfSession() == null
                                                                   ? DateConverter.getEndOfStudyForStudentByPeriodOfSudy(
                                                                           student.getDateOfBeginSchoolYear(), student.getPeriodOfStudy(),
                                                                           student.getSemesternumber()
                                                                   )
                                                                   : DateConverter.getLastDayOfMonth(student.getDateNextEndOfSession()), student.getGroupname(), ""
                        );
                    }
                } else {
                    Date firstDateStudent;

                    firstDateStudent = DateConverter.getFirstDayOfNextMonthByCalendar(firstDate);

                    if (student.getInvalid() && item.getName().equals("Отменить выплаты(Инвалиды)")) {
                        managerCreate.createLinkOrderStudentStatus(
                                idLOS, student.getId(), firstDateStudent, null, student.getGroupname(), "");
                    } else if (student.getSirota() && item.getName().equals("Отменить выплаты(Сироты)")) {
                        managerCreate.createLinkOrderStudentStatus(
                                idLOS, student.getId(), firstDateStudent, null, student.getGroupname(), "");
                    } else if (!student.getInvalid() && !student.getSirota() &&
                               item.getName().equals("Отменить выплаты(Социальная защита)")) {
                        managerCreate.createLinkOrderStudentStatus(
                                idLOS, student.getId(), firstDateStudent, null, student.getGroupname(), "");
                    }
                }
            }
        }

        return idOrder;

    }

    public Long createSocialIncreasedOrderInSession (OrderRuleModel rule, Date firstDate, Date secondDate, String description,
                                                     Long idSemester) {
        List<OrderSectionModel> listSections = managerCreate.getListOrderSection(rule.getId());

        List<StudentToCreateModel> listStudents = managerCreate.getStudentsForSocialIncreasedOrderInSession(
                firstDate, secondDate, idSemester);

        for (int i = 0; i < listStudents.size(); i++) {
            if (i < listStudents.size() - 1 && listStudents.get(i).getIdStudentcard().equals(listStudents.get(i + 1).getIdStudentcard())) {
                listStudents.remove(i);
                i--;
            }
        }

        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        Long idOrder = managerCreate.createEmptyOrder(rule.getId(), new Date(), idSemester, idHumanface, description);

        for (OrderSectionModel item : listSections) {
            Long idLOS = managerCreate.createLinkOrderSection(idOrder, item.getId(), null, new Date(),
                                                              item.getName().equals(ASSIGN_SCHOLARSHIP) ? new Date() : null
            );

            for (StudentToCreateModel student : listStudents) {
                if (item.getName().equals(ASSIGN_SCHOLARSHIP)) {
                    Date lastDayOfMonthEndSession = DateConverter.getLastDayOfMonth(student.getDateNextEndOfSession());

                    if (student.getSecondDate() != null && student.getSecondDate().before(lastDayOfMonthEndSession)) {
                        Date secondDateStudent = student.getFirstDate().before(new Date(2017 - 1900, 0, 1))
                                                 ? DateConverter.getLastDayOfMonth(student.getSecondDate())
                                                 : student.getSecondDate();

                        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(),
                                                                   DateConverter.getFirstDayOfNextMonthByCalendar(firstDate),
                                                                   secondDateStudent, student.getGroupname(), ""
                        );
                    } else {
                        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(),
                                                                   DateConverter.getFirstDayOfNextMonthByCalendar(firstDate),
                                                                   lastDayOfMonthEndSession, student.getGroupname(), ""
                        );
                    }
                } else {
                    if (student.getGetSocialPrev() != null && student.getGetSocialPrev()) {
                        Date firstDateStudent = DateConverter.getFirstDayOfNextMonthByCalendar(firstDate);

                        if (student.getInvalid() && item.getName().equals("Отменить выплаты(Инвалиды)")) {
                            managerCreate.createLinkOrderStudentStatus(
                                    idLOS, student.getId(), firstDateStudent, null, student.getGroupname(), "");
                        } else if (student.getSirota() && item.getName().equals("Отменить выплаты(Сироты)")) {
                            managerCreate.createLinkOrderStudentStatus(
                                    idLOS, student.getId(), firstDateStudent, null, student.getGroupname(), "");
                        } else if (item.getName().equals("Отменить выплаты(Социальная защита)")) {
                            managerCreate.createLinkOrderStudentStatus(
                                    idLOS, student.getId(), firstDateStudent, null, student.getGroupname(), "");
                        }
                    }
                }
            }
        }

        return idOrder;
    }
}
