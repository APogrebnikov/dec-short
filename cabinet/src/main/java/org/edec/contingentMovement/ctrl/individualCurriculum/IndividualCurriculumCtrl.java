package org.edec.contingentMovement.ctrl.individualCurriculum;

import javafx.util.Pair;
import lombok.NonNull;
import org.edec.commons.component.LbSearchStudent;
import org.edec.commons.model.GroupDirectionModel;
import org.edec.commons.model.StudentGroupModel;
import org.edec.commons.renderer.GroupRenderer;
import org.edec.contingentMovement.ctrl.renderer.RatingRenderer;
import org.edec.contingentMovement.model.ResitRatingModel;
import org.edec.contingentMovement.model.comparator.ResitRatingComparator;
import org.edec.contingentMovement.service.IndividualCurrService;
import org.edec.contingentMovement.service.ResitService;
import org.edec.contingentMovement.service.impl.IndividualCurrImpl;
import org.edec.contingentMovement.service.impl.ResitImpl;
import org.edec.model.GroupModel;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.IncludeSelector;
import org.edec.utility.zk.PopupUtil;
import org.springframework.util.CollectionUtils;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.edec.contingentMovement.ctrl.individualCurriculum.WinAddSubjectsCtrl.EVENT_ON_FINISH;

public class IndividualCurriculumCtrl extends IncludeSelector {

    private static final String CURRENT_SEMESTER = "current_semester";

    /*
     *   Компоненты
     */
    @Wire
    private Button btnAddSubjects;
    @Wire
    private Checkbox chIndCurrExternalStudent, chNoBudget;
    @Wire
    private Combobox cmbIndCurrSelectGroup;
    @Wire
    private Datebox dbIndCurrDatePass;
    @Wire
    private Textbox tbIndCurrFioStudent;
    @Wire
    private LbSearchStudent lbIndCurrSearchStudent;
    @Wire
    private Listbox lbIndCurrStudentSubject, lbIndCurrGroupSubject;

    /*
     *   Сервисы
     */
    private IndividualCurrService individualCurrService = new IndividualCurrImpl();
    private JasperReportService jasperReportService = new JasperReportService();
    private ResitService resitService = new ResitImpl();

    /*
     *   Переменные
     */
    private Long idSem;
    private List<ResitRatingModel> addedSubjects = new ArrayList<>(), resitSubjects, resitSubjectsSecondTable = new ArrayList<>(), studentRatings;

    public static void setPropertiesToInclude(@NonNull Include include, @NonNull InstituteModel selectedInstitute,
                                              @NonNull FormOfStudy selectedFormOfStudy, @NonNull Long idSem) {
        IncludeSelector.setPropertiesToInclude(include, selectedInstitute, selectedFormOfStudy);
        include.setDynamicProperty(CURRENT_SEMESTER, idSem);
    }

    @Override
    protected void fillAttributeFromSession() {
        super.fillAttributeFromSession();
        idSem = (Long) Executions.getCurrent().getArg().get(CURRENT_SEMESTER);
    }

    @Override
    protected void fill() {
        lbIndCurrSearchStudent.setCheckmark(true);
        cmbIndCurrSelectGroup.setItemRenderer(new GroupRenderer());
        lbIndCurrStudentSubject.setItemRenderer(new RatingRenderer(false));
        cmbIndCurrSelectGroup.setModel(new ListModelList<>(individualCurrService.getGroupDirectionBySemester(idSem)));
    }

    @Listen("onSelect = #cmbIndCurrSelectGroup; onCheck = #chIndCurrExternalStudent;")
    public void selectGroupInIndCurr() {
        if (!checkOnChooseInstAndFos()) {
            return;
        }
        if (cmbIndCurrSelectGroup.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите группу!");
            return;
        }
        addedSubjects.clear();
        Clients.showBusy(lbIndCurrGroupSubject, "Загрузка данных");
        Events.echoEvent("onLater", lbIndCurrGroupSubject, null);
    }

    @Listen("onLater = #lbIndCurrGroupSubject")
    public void laterOnSearchGroupSubject() {
        GroupModel selectedGroup = cmbIndCurrSelectGroup.getSelectedItem().getValue();
        lbIndCurrGroupSubject.setItemRenderer(new RatingRenderer(true));

        resitSubjects = individualCurrService.getSubjectByGroup(selectedGroup.getIdDG());
        //Если не был выбран студент из института, то перезачет делать не нужно
        if (studentRatings != null) {
            resitService.autoResit(studentRatings, resitSubjects);
        }
        if (!CollectionUtils.isEmpty(addedSubjects)) {
            resitSubjects.addAll(addedSubjects);
            resitSubjects.sort(ResitRatingComparator::compareRatingsBySemesterSubject);
        }
        lbIndCurrGroupSubject.setModel(new ListModelList<>(resitSubjects));
        lbIndCurrGroupSubject.renderAll();
        Clients.clearBusy(lbIndCurrGroupSubject);
    }

    @Listen("onClick = #lbIndCurrSearchStudent")
    public void selectedIndCurrSearchStudent() {
        if (lbIndCurrSearchStudent.getSelectedItem() == null) {
            return;
        }
        Clients.showBusy(lbIndCurrStudentSubject, "Загрузка данных");
        Events.echoEvent("onLater", lbIndCurrStudentSubject, null);
    }

    @Listen("onLater = #lbIndCurrStudentSubject")
    public void laterOnSearchStudentSubject() {

        StudentGroupModel selectedStudent = lbIndCurrSearchStudent.getSelectedItem().getValue();
        studentRatings = individualCurrService.getRatingByStudentAndGroup(selectedStudent.getIdStudentCard(), selectedStudent.getIdDG());
        lbIndCurrStudentSubject.setModel(new ListModelList<>(studentRatings));
        lbIndCurrStudentSubject.renderAll();
        Clients.clearBusy(lbIndCurrStudentSubject);
        if (cmbIndCurrSelectGroup.getSelectedItem() != null) {
            Clients.showBusy(lbIndCurrGroupSubject, "Загрузка данных");
            Events.echoEvent("onLater", lbIndCurrGroupSubject, null);
        }
    }

    private boolean checkOnChooseInstAndFos() {
        if (idSem == null) {
            DialogUtil.exclamation("Пожалуйста, выберите институт и форму обучения. " +
                    "Модуль может работать не корректно с невыбранной формой обучения.");
            return false;
        }
        return true;
    }

    @Listen("onClick = #btnIndCurrPrintReport")
    public void printReportIndCurriculum() {
        giveIndCurrReport(true);
    }

    @Listen("onClick = #btnIndCurrDownloadReport")
    public void downloadDocxOfReportIndCurriculum() {
        giveIndCurrReport(false);
    }

    private void giveIndCurrReport(boolean isPdf){
        if (cmbIndCurrSelectGroup.getSelectedItem() == null || dbIndCurrDatePass.getValue() == null) {
            DialogUtil.exclamation("Выберите группу и заполните дату сдачи");
            return;
        }
        GroupDirectionModel selectedGroup = cmbIndCurrSelectGroup.getSelectedItem().getValue();
        String fio, shortFio;
        List<ResitRatingModel> subjects;
        if (chIndCurrExternalStudent.isChecked()) {
            String[] splitFio = tbIndCurrFioStudent.getValue().replaceAll("  "," ").split(" ");
            if (splitFio.length < 2) {
                DialogUtil.exclamation("Указано неверное ФИО! Введите ФИО через пробелы.");
                return;
            }
            fio = tbIndCurrFioStudent.getValue().replaceAll("  "," ");

            if (splitFio.length > 2) {
                shortFio = splitFio[0] + " " + splitFio[1].substring(0, 1) + ". " + splitFio[2].substring(0, 1) + ".";
            } else {
                shortFio = splitFio[0] + " " + splitFio[1].substring(0, 1) + ".";
            }

            subjects = resitSubjects;
            if (chNoBudget.isChecked()) {
                JasperReport report = jasperReportService
                        .getContingentIndCurrNoBudget2019(fio, shortFio, "Направление " + selectedGroup.getCode(), selectedGroup.getGroupname(),
                                dbIndCurrDatePass.getValue(), selectedGroup.getCourse(), selectedGroup.getSemester(), subjects);
                if (isPdf) {
                    report.showPdf();
                } else {
                    report.downloadDocx();
                }
            } else {
                JasperReport report = jasperReportService
                        .getContingentIndCurr2019(fio, shortFio, "Направление " + selectedGroup.getCode(), selectedGroup.getGroupname(),
                                dbIndCurrDatePass.getValue(), selectedGroup.getCourse(), selectedGroup.getSemester(), subjects);
                if (isPdf) {
                    report.showPdf();
                } else {
                    report.downloadDocx();
                }
            }
        } else {
            if (lbIndCurrSearchStudent.getSelectedItem() == null) {
                DialogUtil.exclamation("Выберите студента");
                return;
            }
            StudentGroupModel selectedStudent = lbIndCurrSearchStudent.getSelectedItem().getValue();
            fio = selectedStudent.getFio();
            shortFio = selectedStudent.getShortFioInverse();
            subjects = new ArrayList<>();
            for (Listitem li : lbIndCurrGroupSubject.getItems()) {
                ResitRatingModel subject = li.getValue();
                if ((subject.getRating() == null || (subject.getRating() != 1 && subject.getRating() < 3)) && !subject.getSelectedAsSecondTable()) {
                    subjects.add(subject);
                }
            }

            List<ResitRatingModel> filteredSubjects = subjects;
            // Если отчет формируется в сессию, то попадают предметы и по текущему семестру
            Pair<Date, Date> beginEndSession = individualCurrService.getLastSessionDates(fio);
            if(!(dbIndCurrDatePass.getValue().after(beginEndSession.getKey()) && dbIndCurrDatePass.getValue().before(beginEndSession.getValue()))) {
                //В отчет не должны попадать предметы по текущему семестру
                filteredSubjects = subjects.stream().filter(s -> !s.getCurSem()).collect(Collectors.toList());
            }

            ResitRatingModel itog = new ResitRatingModel();
            itog.setSubjectname("Итого");

            Double sumHours = (double) 0;
            Double sumAudHours = (double) 0;
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            int i = 1;
            for (ResitRatingModel filteredSubject : filteredSubjects) {
                filteredSubject.setPosition(String.valueOf(i));
                filteredSubject.setDatepass(format.format(dbIndCurrDatePass.getValue()));
                if(!filteredSubject.getFoc().equals("КП") && !filteredSubject.getFoc().equals("КР")) {
                    sumHours += filteredSubject.getHoursCount();
                    sumAudHours += filteredSubject.getHoursaudcount();
                }
                i++;
            }

            itog.setPosition("");
            itog.setHoursCount(sumHours);
            itog.setHoursaudcount(sumAudHours);
            itog.setDatepass("");
            itog.setFoc("");
            itog.setType(0);
            filteredSubjects.add(itog);

            if (selectedStudent.getIsGovernmentFinanced() == 1) {
                if (isPdf) {
                    jasperReportService
                            .getContingentIndCurr2019(fio, shortFio, "Направление " + selectedGroup.getCode(), selectedGroup.getGroupname(),
                                    dbIndCurrDatePass.getValue(), selectedGroup.getCourse(), selectedGroup.getSemester(), filteredSubjects
                            ).showPdf();
                } else {
                    jasperReportService
                            .getContingentIndCurr2019(fio, shortFio, "Направление " + selectedGroup.getCode(), selectedGroup.getGroupname(),
                                    dbIndCurrDatePass.getValue(), selectedGroup.getCourse(), selectedGroup.getSemester(), filteredSubjects
                            ).downloadDocx();
                }
            } else {
                if (isPdf) {
                    jasperReportService
                            .getContingentIndCurrNoBudget2019(fio, shortFio, "Направление " + selectedGroup.getCode(), selectedGroup.getGroupname(),
                                    dbIndCurrDatePass.getValue(), selectedGroup.getCourse(), selectedGroup.getSemester(), filteredSubjects
                            ).showPdf();
                } else {
                    jasperReportService
                            .getContingentIndCurrNoBudget2019(fio, shortFio, "Направление " + selectedGroup.getCode(), selectedGroup.getGroupname(),
                                    dbIndCurrDatePass.getValue(), selectedGroup.getCourse(), selectedGroup.getSemester(), filteredSubjects
                            ).downloadDocx();
                }
            }
        }
    }

    @Listen("onClick = #btnIndCurrOpenProtocol")
    public void openDialogWindowWithProtocol() {

        if (cmbIndCurrSelectGroup.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите сначала группу!");
            cmbIndCurrSelectGroup.focus();
            return;
        }

        GroupDirectionModel selectedGroup = cmbIndCurrSelectGroup.getSelectedItem().getValue();

        List<ResitRatingModel> resitSubjectsSecondTable = lbIndCurrGroupSubject.getItems().stream()
                                                                               .map(li -> ((ResitRatingModel) li.getValue()))
                                                                               .filter(ResitRatingModel::getSelectedAsSecondTable)
                                                                               .collect(Collectors.toList());;

        List<ResitRatingModel> resitSubjects = lbIndCurrGroupSubject.getItems().stream()
                .map(li -> ((ResitRatingModel) li.getValue()))
                .filter(rating -> rating.getRating() != null && (rating.getRating() == 1 || rating.getRating() > 2))
                .collect(Collectors.toList());

        if (resitSubjects.size() == 0) {
            PopupUtil.showWarning("Нет перезачтеных дисциплин. Отчет невозможно построить");
            return;
        }

        Map<String, Object> arg = new HashMap<>();

        arg.put(WinIndCurrProtocolCtrl.ARG_SELECTED_GROUP, selectedGroup);
        arg.put(WinIndCurrProtocolCtrl.ARG_SELECTED_RESIST_SUBJECTS, resitSubjects);
        arg.put(WinIndCurrProtocolCtrl.ARG_SELECTED_RESIT_SUBJECT_SECOND_TABLE, resitSubjectsSecondTable);

        if (chIndCurrExternalStudent.isChecked()) {
            arg.put(WinIndCurrProtocolCtrl.ARG_SELECTED_FIO_STUDENT, tbIndCurrFioStudent.getValue());
        } else {
            if (lbIndCurrSearchStudent.getSelectedItem() == null) {
                PopupUtil.showWarning("Необходимо выбрать студента");
                return;
            }
            StudentGroupModel selectedStudent = lbIndCurrSearchStudent.getSelectedItem().getValue();
            arg.put(WinIndCurrProtocolCtrl.ARG_SELECTED_STUDENT, selectedStudent);
        }

        ComponentHelper.createWindow("/contingentMovement/individualCurriculum/winIndCurrProtocol.zul", "winIndCurrProtocol", arg).doModal();
    }

    @Listen("onClick = #btnAddSubjects")
    public void addSubjects() {

        Map<String, Object> arg = new HashMap<>();
        arg.put(WinAddSubjectsCtrl.ADDED_SUBJECTS, addedSubjects);

        Window winAddSubjects = ComponentHelper.createWindow("/contingentMovement/individualCurriculum/winAddSubjects.zul", "winAddSubjects", arg);
        winAddSubjects.addEventListener(EVENT_ON_FINISH, event -> updateResitSubjectsAfterAdding());

        winAddSubjects.doModal();
    }

    private void updateResitSubjectsAfterAdding() {

        if (studentRatings != null) {
            studentRatings.forEach(rating -> rating.setResitRating(null));
        }
        Clients.showBusy(lbIndCurrGroupSubject, "Загрузка данных");
        Events.echoEvent("onLater", lbIndCurrGroupSubject, null);
    }
}
