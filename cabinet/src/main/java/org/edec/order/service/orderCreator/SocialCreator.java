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

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.Date;


public class SocialCreator {
    CreateOrderManagerESO managerCreate = new CreateOrderManagerESO();
    CreateOrderService service = new CreateOrderServiceESO();

    //TODO исправлена работа с датами, нужно тестить
    public Long createSocialOrderNewReference (OrderRuleModel rule, Date firstDate, Date secondDate, String description, Long idSemester,
                                               boolean inSession) {
        List<OrderSectionModel> listSections = managerCreate.getListOrderSection(rule.getId());

        List<StudentToCreateModel> listStudents = managerCreate.getStudentsForSocialOrderNewReference(
                firstDate, secondDate, inSession
                                       ? idSemester
                                       : managerCreate.getPrevSemester(idSemester));

        listStudents.addAll(managerCreate.getListFirstCourseForSocial(idSemester));

        List<StudentToCreateModel> listInvalids = new ArrayList<>();
        List<StudentToCreateModel> listNeededs = new ArrayList<>();
        //TODO My inglish is very biutiful, no vaeriants - bazara zero
        List<StudentToCreateModel> listSiroti = new ArrayList<>();

        if (new Date().getMonth() == 8 && !inSession) {
            listSiroti = managerCreate.getListSirotsForSocial(idSemester);
        }

        for (StudentToCreateModel item : listStudents) {
            if (item.getSessionResult() != null && item.getSessionResult() > 1 && item.getSemesternumber() <= 3 && item.getQualification() != 3) {
                continue;
            }

            if (item.getSemesternumber() == 1 || !(item.getSessionResultPrev() != null && item.getSessionResultPrev() > 1 && item.getSemesternumber() <= 4) ||
                item.getQualification() == 3) {
                if (item.getInvalid()) {
                    listInvalids.add(item);
                } else {
                    listNeededs.add(item);
                }
            }
        }

        //findAndRemoveDubleStudentsInCollection(listInvalids);
        //findAndRemoveDubleStudentsInCollection(listNeededs);

        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        Long idOrder = managerCreate.createEmptyOrder(rule.getId(), new Date(), idSemester, idHumanface, description);

        for (OrderSectionModel item : listSections) {
            Long idLOS = managerCreate.createLinkOrderSection(idOrder, item.getId(), null, new Date(), new Date());

            if (item.getName().equals("Инвалиды")) {
                for (StudentToCreateModel student : listInvalids) {
                    List<ReferenceModelESO> listReference = managerCreate.getAllReferenceByStudentcard(student.getIdStudentcard());

                    if (student.getSecondDate() == null) {
                        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(), student.getFirstDate(),
                                                                   DateConverter.getEndOfStudyForStudentByPeriodOfSudy(
                                                                           student.getDateOfBeginSchoolYear(), student.getPeriodOfStudy(),
                                                                           student.getSemesternumber()
                                                                   ), student.getGroupname(), ""
                        );
                    } else {
                        ReferenceModelESO ref = managerCreate.getPrevReferenceForStudent(
                                student.getId(), student.getFirstDate(), student.getSecondDate());

                        Date firstDateStudent;

                        if (ref != null) {
                            if (ref.getDateFinish().after(student.getFirstDate())) {
                                firstDateStudent = DateConverter.getNextDay(ref.getDateFinish());
                            } else {
                                firstDateStudent = student.getFirstDate();
                            }
                        } else {
                            firstDateStudent = student.getFirstDate();
                        }

                        if (listReference.size() > 1) {
                            //берем предыдущую справку
                            ReferenceModelESO reference = listReference.get(1);

                            Date dateFinish = reference.getDateGet().before(new Date(2017 - 1900, 0, 1))
                                              ? DateConverter.getFirstDayOfNextMonthByCalendar(reference.getDateFinish())
                                              : DateConverter.getNextDay(reference.getDateFinish());

                            if (firstDate.before(dateFinish)) {
                                firstDateStudent = dateFinish;
                            }
                        }

                        Date dateOfEnd = DateConverter.getEndOfStudyForStudentByPeriodOfSudy(
                                student.getDateOfBeginSchoolYear(), student.getPeriodOfStudy(), student.getSemesternumber());

                        Date secondDateStudent = student.getFirstDate().before(new Date(2017 - 1900, 0, 1))
                                                 ? DateConverter.getLastDayOfMonth(student.getSecondDate())
                                                 : student.getSecondDate();

                        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(), firstDateStudent,
                                                                   dateOfEnd.before(secondDateStudent) ? dateOfEnd : secondDateStudent,
                                                                   student.getGroupname(), ""
                        );
                    }
                }
            }

            if (item.getName().equals("Социальная защита")) {
                for (StudentToCreateModel student : listNeededs) {
                    List<ReferenceModelESO> listReference = managerCreate.getAllReferenceByStudentcard(student.getIdStudentcard());
                    ReferenceModelESO ref = managerCreate.getPrevReferenceForStudent(
                            student.getId(), student.getFirstDate(), student.getSecondDate());

                    Date firstDateStudent = null;

                    if (ref != null) {
                        if (ref.getDateStart().after(student.getFirstDate())) {
                            firstDateStudent = DateConverter.getNextDay(ref.getDateFinish());
                        } else {
                            firstDateStudent = student.getFirstDate();
                        }
                    } else {
                        firstDateStudent = student.getFirstDate();
                    }

                    if (listReference.size() > 1) {
                        //берем предыдущую справку
                        ReferenceModelESO reference = listReference.get(1);

                        Date dateFinish = null;
                        if (reference.getDateStart().before(new Date(2017 - 1900, 0, 1))) {
                            if (reference.getDateFinish().getMonth() != 11) {
                                dateFinish = DateConverter.getFirstDayOfNextMonthByCalendar(reference.getDateFinish());
                            } else {
                                dateFinish = DateConverter.getFirstDayOfNextYear(reference.getDateFinish());
                            }
                        } else {
                            if (reference.getDateFinish().getMonth() == 11 && reference.getDateFinish().getDay() == 31) {
                                dateFinish = DateConverter.getFirstDayOfNextYear(reference.getDateFinish());
                            } else {
                                dateFinish = DateConverter.getNextDay(reference.getDateFinish());
                            }
                        }

                        if (firstDateStudent.before(dateFinish)) {
                            firstDateStudent = dateFinish;
                        }
                    }

                    Date dateOfEnd = DateConverter.getEndOfStudyForStudentByPeriodOfSudy(
                            student.getDateOfBeginSchoolYear(), student.getPeriodOfStudy(), student.getSemesternumber());

                    Date secondDateStudent = student.getFirstDate().before(new Date(2017 - 1900, 0, 1)) ? DateConverter.getLastDayOfMonth(
                            student.getSecondDate()) : student.getSecondDate();

                    managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(), firstDateStudent,
                                                               dateOfEnd.before(secondDateStudent) ? dateOfEnd : secondDateStudent,
                                                               student.getGroupname(), ""
                    );
                }
            }

            if (item.getName().equals("Сироты")) {
                for (StudentToCreateModel student : listSiroti) {
                    GregorianCalendar cal = new GregorianCalendar();
                    cal.setTime(student.getBirthDate());
                    Period period = Period.between(
                            LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)),
                            LocalDate.now()
                    );
                    if (period.getYears() < 18) {

                        Date dateOfEighteenYears = DateConverter.addYearsToDate(student.getBirthDate(), 18);
                        Date dateOfEighteenYearsRounded = DateConverter.getFirstDayOfNextMonthByCalendar(dateOfEighteenYears);

                        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(), new Date(new Date().getYear(), 8, 1),
                                                                   dateOfEighteenYearsRounded, student.getGroupname(), ""
                        );
                    }
                }
            }
        }

        return idOrder;
    }

    public Long createSocialOrderInSession (OrderRuleModel rule, Date firstDate, Date secondDate, String description, Long idSemester) {
        List<OrderSectionModel> listSections = managerCreate.getListOrderSection(rule.getId());
        List<StudentToCreateModel> listStudents = managerCreate.getStudentsForSocialOrderInSession(firstDate, secondDate, idSemester);

        for (int i = 0; i < listStudents.size(); i++) {
            if (listStudents.get(i).getSirota() || listStudents.get(i).getFirstDate() == null) {
                continue;
            }

            if (i < listStudents.size() - 1 && listStudents.get(i).equals(listStudents.get(i + 1))) {
                listStudents.remove(i);
                i--;
            }
        }

        List<StudentToCreateModel> listSirots = new ArrayList<>();
        List<StudentToCreateModel> listInvalids = new ArrayList<>();
        List<StudentToCreateModel> listNeededs = new ArrayList<>();

        for (StudentToCreateModel item : listStudents) {
            if (item.getInvalid()) {
                listInvalids.add(item);
            } else if (item.getSirota()) {
                listSirots.add(item);
            } else {
                listNeededs.add(item);
            }
        }

        findAndRemoveDubleStudentsInCollection(listInvalids);
        findAndRemoveDubleStudentsInCollection(listNeededs);
        findAndRemoveDubleStudentsInCollection(listSirots);

        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        Long idOrder = managerCreate.createEmptyOrder(rule.getId(), new Date(), idSemester, idHumanface, description);

        for (OrderSectionModel item : listSections) {
            Long idLOS = managerCreate.createLinkOrderSection(idOrder, item.getId(), null, new Date(), new Date());

            if (item.getName().equals("Сироты")) {
                for (StudentToCreateModel student : listSirots) {
                    if (student.getSessionResultPrev() != null && student.getSessionResultPrev() > 1 && student.getSemesternumber() > 1 &&
                        student.getSemesternumber() < 5) {
                        Date dateOfEnd = DateConverter.getEndOfStudyForStudentByPeriodOfSudy(
                                student.getDateOfBeginSchoolYear(), student.getPeriodOfStudy(), student.getSemesternumber());

                        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(),
                                                                   DateConverter.getFirstDayOfNextMonthByCalendar(firstDate), dateOfEnd,
                                                                   student.getGroupname(), ""
                        );
                    }
                }
            }

            if (item.getName().equals("Инвалиды")) {
                for (StudentToCreateModel student : listInvalids) {
                    Date dateOfEnd = DateConverter.getEndOfStudyForStudentByPeriodOfSudy(
                            student.getDateOfBeginSchoolYear(), student.getPeriodOfStudy(), student.getSemesternumber());

                    if (student.getSessionResultPrev() != null && student.getSessionResultPrev() > 1 && student.getSecondDate() == null &&
                        student.getSemesternumber() < 5 && student.getSemesternumber() > 1) {
                        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(),
                                                                   DateConverter.getFirstDayOfNextMonthByCalendar(firstDate), dateOfEnd,
                                                                   student.getGroupname(), ""
                        );
                    } else if (student.getSecondDate() != null && student.getSessionResultPrev() != null &&
                               student.getSessionResultPrev() > 1 && student.getSemesternumber() < 5 && student.getSemesternumber() > 1) {
                        Date secondDateStudent = student.getFirstDate().before(new Date(2017 - 1900, 0, 1))
                                                 ? DateConverter.getLastDayOfMonth(student.getSecondDate())
                                                 : student.getSecondDate();

                        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(),
                                                                   DateConverter.getFirstDayOfNextMonthByCalendar(firstDate),
                                                                   dateOfEnd.before(secondDateStudent) ? dateOfEnd : secondDateStudent,
                                                                   student.getGroupname(), ""
                        );
                    }
                }
            }

            if (item.getName().equals("Социальная защита")) {
                for (StudentToCreateModel student : listNeededs) {
                    Date secondDateStudent = student.getFirstDate().before(new Date(2017 - 1900, 0, 1)) ? DateConverter.getLastDayOfMonth(
                            student.getSecondDate()) : student.getSecondDate();

                    Date dateOfEnd = DateConverter.getEndOfStudyForStudentByPeriodOfSudy(
                            student.getDateOfBeginSchoolYear(), student.getPeriodOfStudy(), student.getSemesternumber());

                    if (student.getSessionResultPrev() != null && student.getSessionResultPrev() > 1 && student.getSemesternumber() < 5 &&
                        student.getSemesternumber() > 1) {
                        managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(),
                                                                   DateConverter.getFirstDayOfNextMonthByCalendar(firstDate),
                                                                   dateOfEnd.before(secondDateStudent) ? dateOfEnd : secondDateStudent,
                                                                   student.getGroupname(), ""
                        );
                    }
                }
            }
        }

        return idOrder;
    }

    //удаление лишних студентов(задвоение тех, кто перевелся)
    public void findAndRemoveDubleStudentsInCollection (List<StudentToCreateModel> students) {
        List<StudentToCreateModel> studentsToRemove = new ArrayList<>();
        for (StudentToCreateModel student : students) {
            for (StudentToCreateModel student2 : students) {
                if (student.getIdStudentcard() != null && student2.getIdStudentcard() != null) {
                    if (student.getIdStudentcard().longValue() == student2.getIdStudentcard().longValue()) {
                        if(student.getTransfer() == null) {
                            continue;
                        }

                        if (!student.getTransfer() && !student2.getTransfer()) {
                            continue;
                        }

                        if (student.getTransfer() && !student2.getTransfer()) {
                            studentsToRemove.add(student2);
                        }

                        if (!student.getTransfer() && student2.getTransfer()) {
                            studentsToRemove.add(student);
                        }

                        if (student.getTransfer() && student2.getTransfer()) {
                            continue;
                        }
                    }
                }
            }
        }

        students.removeAll(studentsToRemove);
        studentsToRemove.clear();
    }
}
