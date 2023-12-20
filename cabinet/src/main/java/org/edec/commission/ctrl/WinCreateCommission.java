package org.edec.commission.ctrl;

import lombok.extern.log4j.Log4j;
import org.edec.commission.ctrl.renderer.SubjectCreateCommissionRenderer;
import org.edec.commission.model.StudentDebtModel;
import org.edec.commission.model.SubjectDebtModel;
import org.edec.commission.model.comporator.SubjectDebtComporator;
import org.edec.commission.service.CommissionService;
import org.edec.commission.service.impl.CommissionServiceESOimpl;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.secretaryChair.model.StudentCommissionModel;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.*;

@Log4j
public class WinCreateCommission extends SelectorComposer<Component> {
    public static final String LIST_ID_SSS = "list_id_sss";
    public static final String FORM_OF_STUDY = "form_of_study";
    public static final String DATE_BEGIN_COMMISSION = "date_begin_commission";
    public static final String DATE_END_COMMISSION = "date_end_commission";

    @Wire
    private Listbox lbCreateCommission;

    @Wire
    private Listheader lhSubject, lhCountDebts, lhFoc, lhChair, lhSem;

    @Wire
    private Textbox tbCreateCommissionSearch;

    @Wire
    private Window winCreateCommission;

    private CommissionService commissionService = new CommissionServiceESOimpl();
    private TemplatePageCtrl template = new TemplatePageCtrl();

    String listIdSSS;
    Integer formOfStudy;
    private Date dateBegin, dateEnd;
    private List<SubjectDebtModel> subjects;

    private SubjectDebtComporator subjectDebtComp;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        listIdSSS = (String) Executions.getCurrent().getArg().get(LIST_ID_SSS);
        formOfStudy = (Integer) Executions.getCurrent().getArg().get(FORM_OF_STUDY);
        dateBegin = (Date) Executions.getCurrent().getArg().get(DATE_BEGIN_COMMISSION);
        dateEnd = (Date) Executions.getCurrent().getArg().get(DATE_END_COMMISSION);

        lhSubject.setSortAscending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_SUBJECT));
        lhSubject.setSortDescending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_SUBJECT_REV));
        lhCountDebts.setSortAscending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_COUNT_DEBTS));
        lhCountDebts.setSortDescending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_COUNT_DEBTS_REV));
        lhFoc.setSortAscending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_FOC));
        lhFoc.setSortDescending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_FOC_REV));
        lhChair.setSortAscending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_CHAIR));
        lhChair.setSortDescending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_CHAIR_REV));
        lhSem.setSortAscending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_SEM));
        lhSem.setSortDescending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_SEM_REV));

        lbCreateCommission.setItemRenderer(new SubjectCreateCommissionRenderer(this));

        callShowBusyForListbox("Загрузка данных из БД", null);
    }

    @Listen("onOK=#tbCreateCommissionSearch;onClick=#btnCreateCommissionSearch")
    public void searchByFilter() {
        callShowBusyForListbox("Загрузка данных из БД", null);
    }

    @Listen("onLater = #lbCreateCommission")
    public void laterOnCreateCommission(Event event) {
        List<SubjectDebtModel> tempList = initListboxSubject(tbCreateCommissionSearch.getValue());
        if (event.getData() != null) {
            tempList.sort((Comparator<? super SubjectDebtModel>) event.getData());
        }
        ListModelList lmSubject = new ListModelList(tempList);
        lmSubject.setMultiple(true);
        lbCreateCommission.setModel(lmSubject);
        lbCreateCommission.renderAll();
        Clients.clearBusy(lbCreateCommission);
    }

    @Listen("onClick = #btnCreateCommission")
    public void createCommission() {
        if (lbCreateCommission.getSelectedCount() == 0) {
            PopupUtil.showWarning("Выберите один или несколько предметов");
            return;
        }

        StringBuilder errorMessage = new StringBuilder();
        StringBuilder messageAboutStudentsAdded = new StringBuilder();

        for (Listitem li : lbCreateCommission.getSelectedItems()) {
            SubjectDebtModel subject = li.getValue();
            if (subject.isSigned()) {
                continue;
            }
            StringBuilder listStudentsAdded = new StringBuilder();
            StringBuilder listStudentsError = new StringBuilder();
            List<StudentDebtModel> studentsForCreating = new ArrayList<>(subject.getStudents());

            // Если уже существует подходящая комиссия
            List<SubjectDebtModel> existingCommissionList = commissionService
                    .getSubjectCommissionByFilterAndSem(subject.getSubjectname(), subject.getIdSemester(), null, formOfStudy,
                                                        subject.getFocStr(), false, subject.getIdChair(), subject.getTeachers()
                    );
            if (existingCommissionList.size() != 0) {
                for (SubjectDebtModel existingCommission : existingCommissionList) {
                    if (!existingCommission.isSigned() && existingCommission.getDateofendcomission().after(new Date())) {
                        existingCommission
                                .setStudents(commissionService.getStudentByRegisterCommission(existingCommission.getIdRegComission()));

                        boolean allStudentsArePaid = existingCommission.getStudents().stream()
                                                                       .noneMatch(StudentDebtModel::getIsGovernmentFinanced);

                        List<StudentDebtModel> studentsAdded = new ArrayList<>();
                        for (StudentDebtModel student : studentsForCreating) {
                            boolean conditionForCreation;
                            if (allStudentsArePaid) {
                                conditionForCreation = !student.getIsGovernmentFinanced();
                            } else {
                                conditionForCreation = student.getIsGovernmentFinanced();
                            }

                            if (conditionForCreation && existingCommission.getDateComission() != null && !commissionService
                                    .checkStudentAvailableForCommission(student.getFio(), existingCommission.getDateComission())) {

                                DialogUtil.error("Нельзя добавить в существующую комиссию по предмету " + student.getSubjectname() +
                                                 " студента " + student.getFio() +
                                                 " за семестр " + student.getSemesterStr() +
                                                 ". У него уже есть комиссии, назначенные на этот день");

                                return;
                            }

                            if (conditionForCreation) {
                                if (commissionService
                                        .addStudentToCommission(student, existingCommission, template.getCurrentUser().getIdHum())) {
                                    listStudentsAdded.append(student.getFio()).append("\n");
                                    studentsAdded.add(student);
                                    subject.getStudents().get(studentsForCreating.indexOf(student)).setOpenComm(true);
                                } else {
                                    listStudentsError.append(student.getFio()).append("\n");
                                }
                            }
                        }

                        for (StudentDebtModel student : studentsAdded) {
                            studentsForCreating.remove(student);
                        }

                        if (!listStudentsError.toString().equals("")) {
                            errorMessage.append("Не удалось добавить студентов:").append(listStudentsAdded)
                                        .append(" в комиссию по предмету: ").append(existingCommission.getSubjectname()).append(" (")
                                        .append(existingCommission.getFocStr()).append("), ").append(existingCommission.getSemesterStr())
                                        .append("\n");
                        }

                        if (!listStudentsAdded.toString().equals("")) {

                            messageAboutStudentsAdded.append("Студент:\n").append(listStudentsAdded).append("был добавлен в комиссию ")
                                                     .append(existingCommission.getSubjectname()).append("\n");
                        }
                    }
                }
                if (studentsForCreating.size() != 0) {
                    errorMessage.append(createCommonCommission(subject, studentsForCreating));
                } else {
                    subject.setSigned(true);
                }
            } else {
                List<StudentDebtModel> studentsAreGovernmentFinanced = new ArrayList<>();
                List<StudentDebtModel> studentsAreNotGovernmentFinanced = new ArrayList<>();

                for (StudentDebtModel student : studentsForCreating) {
                    if (!student.getIsGovernmentFinanced()) {
                        studentsAreGovernmentFinanced.add(student);
                    } else {
                        studentsAreNotGovernmentFinanced.add(student);
                    }
                }

                if (studentsAreGovernmentFinanced.size() != 0) {
                    errorMessage.append(createCommonCommission(subject, studentsAreGovernmentFinanced));
                }

                if (studentsAreNotGovernmentFinanced.size() != 0) {
                    errorMessage.append(createCommonCommission(subject, studentsAreNotGovernmentFinanced));
                }

                subject.setSigned(true);
            }
        }

        callShowBusyForListbox("Загрузка данных из БД", null);

        if (!errorMessage.toString().equals("")) {
            Messagebox.show(errorMessage.toString());
            log.error(errorMessage);
        }

        if (!messageAboutStudentsAdded.toString().equals("")) {
            Messagebox.show(messageAboutStudentsAdded.toString());
        }
    }

    public StringBuilder createCommonCommission(SubjectDebtModel subject, List<StudentDebtModel> students) {
        StringBuilder errorMessage = new StringBuilder();
        if (commissionService.createCommonCommission(subject, students, template.getCurrentUser().getIdHum())) {
            commissionService.setStatusSignedForSubjAndStudents(subject, students);

            students.forEach(student -> logCommissionCreation(subject, student));
        } else {
            errorMessage.append("Не удалось создать комиссию по предмету: ").append(subject.getSubjectname()).append(" (")
                        .append(subject.getFocStr()).append("), ").append(subject.getSemesterStr()).append("\n");
        }
        return errorMessage;
    }

    public void createIndividualCommission(SubjectDebtModel subject) {
        if (subject.isSigned()) {
            return;
        }
        boolean check = true;
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Не удалось создать индивидуальные комиссии по предмету: ").append(subject.getSubjectname()).append(" (")
                    .append(subject.getFocStr()).append("), ").append(subject.getSemesterStr()).append(":\n");

        for (StudentDebtModel student : subject.getStudents()) {
            if (commissionService.createIndividualCommission(student, subject.getDateofbegincomission(), subject.getDateofendcomission(),
                                                             template.getCurrentUser().getIdHum()
            )) {
                student.setOpenComm(true);

                logCommissionCreation(subject, student);
            } else {
                check = false;
                errorMessage.append(student.getFio()).append("\n");
            }
        }
        if (!check) {
            PopupUtil.showError(errorMessage.toString());
        } else {
            subjects.get(subjects.indexOf(subject)).setSigned(true);
        }
        callShowBusyForListbox("Загрузка данных из БД", null);
    }

    @Listen("onSort = #lhSubject")
    public void sortSubject() {
        callShowBusyForListbox("Сортировка", getComporatorByListhead(lhSubject));
    }

    @Listen("onSort = #lhChair")
    public void sortChair() {
        callShowBusyForListbox("Сортировка", getComporatorByListhead(lhChair));
    }

    @Listen("onSort = #lhCountDebts")
    public void sortCountDebts() {
        callShowBusyForListbox("Сортировка", getComporatorByListhead(lhCountDebts));
    }

    @Listen("onSort = #lhFoc")
    public void sortFoc() {
        callShowBusyForListbox("Сортировка", getComporatorByListhead(lhFoc));
    }

    @Listen("onSort = #lhSem")
    public void sortSem() {
        callShowBusyForListbox("Сортировка", getComporatorByListhead(lhSem));
    }

    public void changeStatusForSubject(SubjectDebtModel subject) {
        subjects.get(subjects.indexOf(subject)).setSigned(true);
        callShowBusyForListbox("Загрузка данных", null);
    }

    private SubjectDebtComporator getComporatorByListhead(Listheader lh) {
        if (lh.getSortDirection().equals("natural") || lh.getSortDirection().equals("descending")) {
            return (SubjectDebtComporator) lh.getSortAscending();
        } else {
            return (SubjectDebtComporator) lh.getSortDescending();
        }
    }

    public void callShowBusyForListbox(String msg, Object data) {
        Clients.showBusy(lbCreateCommission, msg);
        Events.echoEvent("onLater", lbCreateCommission, data);
    }

    private List<SubjectDebtModel> initListboxSubject(String filter) {
        if (subjects == null) {
            subjects = commissionService.getSubjForCreateCommission(listIdSSS, dateBegin, dateEnd);
        }
        if (filter.equals("")) {
            return subjects;
        } else {
            return commissionService.getSubjectsByFilter(filter, subjects);
        }
    }

    private void logCommissionCreation(SubjectDebtModel subject, StudentDebtModel student) {
        log.info("Была создана комиссия " + DateConverter.convertDateToString(new Date()) + "):" + subject.getSubjectname() + "(" +
                 subject.getFocStr() + ")" + " для студента " + student.getFio() + " сотрудником " + template.getCurrentUser().getFio());
    }
}
