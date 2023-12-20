package org.edec.attendance.ctrl;

import org.edec.attendance.model.MonthAttendSubjectsModel;
import org.edec.attendance.model.StudentAttendanceModel;
import org.edec.attendance.service.JournalOfAttendanceService;
import org.edec.attendance.service.impl.ComponentGroupServiceImpl;
import org.edec.attendance.service.impl.JournalOfAttendanceServiceImpl;
import org.edec.model.AttendanceModel;
import org.edec.model.GroupSemesterModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.schedule.model.dao.GroupSubjectLesson;
import org.edec.schedule.service.AttendanceService;
import org.edec.schedule.service.impl.AttendanceImpl;
import org.edec.attendance.service.ComponentGroupService;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

/**
 * @author luninok
 */
public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Button btnPrevMonth, btnNextMonth, btnSearchForTabDay, btnSearchForTabPeriod;
    @Wire
    private Label lCurrentMonth, lCurrentDate, lBeginSem, lEndSem;
    @Wire
    private Listbox lbSubjectForTabDay;
    @Wire
    private Grid gridForPeriodTab;
    @Wire
    private Vbox vbCalendar, vbListbox;
    @Wire
    private Hbox hbCalendar;
    @Wire
    private Combobox cmbSemesterForTabDay, cmbFormOfStudyForTabDay, cmbQualificationForTabDay, cmbCourseForTabDay, cmbGroupForTabDay;
    @Wire
    private Combobox cmbSemesterForTabPeriod, cmbFormOfStudyForTabPeriod, cmbQualificationForTabPeriod, cmbCourseForTabPeriod, cmbGroupForTabPeriod;
    @Wire
    private Datebox dbBeginPeriod, dbEndPeriod;

    private ComponentService componentService = new ComponentServiceESOimpl();
    private ComponentGroupService componentGroupService = new ComponentGroupServiceImpl();
    private JournalOfAttendanceService journalOfAttendanceService = new JournalOfAttendanceServiceImpl();

    private Date today = new Date();
    private Date chooseMonth;
    private Integer firstWeekSem = 1;
    private GroupSemesterModel currentGroupTabDay;
    private MonthAttendSubjectsModel todayAttendance;
    private MonthAttendSubjectsModel selectedDay;
    private List<GroupSubjectLesson> lessonsTabDay;
    private List<MonthAttendSubjectsModel> monthAttendance;
    private List<MonthAttendSubjectsModel> periodAttendance;
    private List<StudentAttendanceModel> studentsTabDay;
    private AttendanceService attendanceService = new AttendanceImpl();

    private GroupSemesterModel currentGroupTabPeriod;
    private List<GroupSubjectLesson> lessonsTabPeriod;

    protected void fill() {

        initCmbFormOfStudyForTabDay();
        initCmbSemesterForTabDay();
        cmbQualificationForTabDay.setSelectedIndex(0);
        cmbCourseForTabDay.setSelectedIndex(0);
        initCmbGroupForTabDay();

        initCmbFormOfStudyForTabPeriod();
        initCmbSemesterForTabPeriod();
        cmbQualificationForTabPeriod.setSelectedIndex(0);
        cmbCourseForTabPeriod.setSelectedIndex(0);
        initCmbGroupForTabPeriod();
        initDateBox();

        Clients.showBusy("Загрузка данных");
        Events.echoEvent("onLater", vbCalendar, null);
        Events.echoEvent("onFirstRun", vbCalendar, null);
        Events.echoEvent("onLater", cmbSemesterForTabDay, null);
        Events.echoEvent("onLater", cmbGroupForTabDay, null);

        Events.echoEvent("onLater", cmbSemesterForTabPeriod, null);
        Events.echoEvent("onLater", cmbGroupForTabPeriod, null);
    }

    private void initCmbFormOfStudyForTabDay() {
        componentService.fillCmbFormOfStudy(cmbFormOfStudyForTabDay, null, FormOfStudy.ALL.getType(), false);
    }

    @Listen("onChange = #cmbFormOfStudyForTabDay")
    public void onChangeCmbFormOfStudyForTabDay() {
        initCmbSemesterForTabDay();
        Events.echoEvent("onLater", cmbSemesterForTabDay, null);
        if (cmbFormOfStudyForTabDay.getSelectedIndex() == 1) {
            cmbQualificationForTabPeriod.setSelectedIndex(0);
            cmbQualificationForTabPeriod.setVisible(true);
        }
        initCmbGroupForTabDay();
        Events.echoEvent("onLater", cmbGroupForTabDay, null);
    }

    private void initCmbSemesterForTabDay() {
        int formOfStudy = ((FormOfStudy) cmbFormOfStudyForTabDay.getSelectedItem().getValue()).getType();
        componentService.fillCmbSem(cmbSemesterForTabDay, 1L, formOfStudy, null);
    }

    @Listen("onChange = #cmbSemesterForTabDay")
    public void onChangeCmbSemesterForTabDay() {
        initCmbGroupForTabDay();
        Events.echoEvent("onLater", cmbGroupForTabDay, null);
    }

    @Listen("onLater = #cmbGroupForTabDay")
    public void onLaterCmbGroupForTabDay() {
        cmbGroupForTabDay.setSelectedIndex(0);
        Clients.clearBusy();
    }

    @Listen("onChange = #cmbQualificationForTabDay")
    public void onChangeCmbQualificationForTabDay() {
        try {
            checkCompatibilityQualificationAndCourse(cmbCourseForTabDay, cmbQualificationForTabDay);
            initCmbGroupForTabDay();
            Events.echoEvent("onLater", cmbGroupForTabDay, null);
        } catch (Exception e) {
            Messagebox.show("Выберите другую квалификацию");
        }
    }

    @Listen("onChange = #cmbCourseForTabDay")
    public void onChangeCmbCourseForTabDay() {
        initCmbGroupForTabDay();
        Events.echoEvent("onLater", cmbGroupForTabDay, null);
    }

    private void initCmbGroupForTabDay() {
        initCmbGroup(cmbSemesterForTabDay, cmbQualificationForTabDay, cmbCourseForTabDay, cmbGroupForTabDay);
        uploadingDataWhenChangingTabDay();
    }

    @Listen("onLater = #cmbSemesterForTabDay")
    public void onLaterCmbSemesterForTabDay() {
        //cmbSemesterForTabDay.setSelectedIndex(0);
        Clients.clearBusy();
    }

    @Listen("onClick = #btnSearchForTabDay")
    public void onClickBtnSearchForTabDay() {
        uploadingDataWhenChangingTabDay();
        refreshCalendar();
        selectedDay = monthAttendance.get(monthAttendance.size() - 1);
        refreshJournal(selectedDay);
    }

    private void uploadingDataWhenChangingTabDay() {

        Long idSem;
        if (cmbSemesterForTabDay.getSelectedIndex() == -1) {
            idSem = ((SemesterModel) cmbSemesterForTabDay.getModel().getElementAt(0)).getIdSem();
        } else {
            idSem = ((SemesterModel) cmbSemesterForTabDay.getSelectedItem().getValue()).getIdSem();
        }
        if (cmbGroupForTabDay.getSelectedIndex() == -1) {
            currentGroupTabDay = ((GroupSemesterModel) cmbGroupForTabDay.getModel().getElementAt(0));
        } else {
            currentGroupTabDay = cmbGroupForTabDay.getSelectedItem().getValue();
        }
        if (currentGroupTabDay.getDateOfBeginSemester() == null || currentGroupTabDay.getDateOfEndSemester() == null){
            PopupUtil.showError("Не указаны даты начала и конца семестра.");
            return;
        }
        chooseMonth = currentGroupTabDay.getDateOfBeginSemester();
        studentsTabDay = journalOfAttendanceService.getStudentByGroup(currentGroupTabDay.getIdLGS());
        lessonsTabDay = attendanceService.getLessonsFromDb(currentGroupTabDay.getGroupname(), idSem);
        lBeginSem.setValue("Начало семестра: " + (currentGroupTabDay.getDateOfBeginSemester() != null ? DateConverter.convertDateToString(currentGroupTabDay.getDateOfBeginSemester()) : ""));
        lEndSem.setValue("Окончание семестра: " + (currentGroupTabDay.getDateOfEndSemester() != null ? DateConverter.convertDateToString(currentGroupTabDay.getDateOfEndSemester()) : ""));

    }


    private void initCmbFormOfStudyForTabPeriod() {
        componentService.fillCmbFormOfStudy(cmbFormOfStudyForTabPeriod, null, FormOfStudy.ALL.getType(), false);
        cmbFormOfStudyForTabPeriod.setSelectedIndex(0);
    }

    @Listen("onChange = #cmbFormOfStudyForTabPeriod")
    public void onChangeCmbFormOfStudyForTabPeriod() {
        initCmbSemesterForTabPeriod();
        Events.echoEvent("onLater", cmbSemesterForTabPeriod, null);
        initCmbGroupForTabPeriod();
        Events.echoEvent("onLater", cmbGroupForTabPeriod, null);
    }

    private void initCmbSemesterForTabPeriod() {
        int formOfStudy = ((FormOfStudy) cmbFormOfStudyForTabPeriod.getSelectedItem().getValue()).getType();
        componentService.fillCmbSem(cmbSemesterForTabPeriod, 1L, formOfStudy, null);
    }

    @Listen("onChange = #cmbSemesterForTabPeriod")
    public void onChangeCmbSemesterForTabPeriod() {
        initCmbGroupForTabPeriod();
        Events.echoEvent("onLater", cmbGroupForTabPeriod, null);
        initDateBox();
        Clients.clearBusy();
    }

    @Listen("onLater = #cmbSemesterForTabPeriod")
    public void onLaterCmbSemesterForTabPeriod() {
        //cmbSemesterForTabPeriod.setSelectedIndex(0);
    }

    @Listen("onChange = #cmbQualificationForTabPeriod")
    public void onChangeCmbQualificationForTabPeriod() {
        try {
            checkCompatibilityQualificationAndCourse(cmbCourseForTabPeriod, cmbQualificationForTabPeriod);
            initCmbGroupForTabPeriod();
            Events.echoEvent("onLater", cmbGroupForTabPeriod, null);
        } catch (Exception e) {
            Messagebox.show("Выберите другую квалификацию");
        }
    }

    @Listen("onChange = #cmbCourseForTabPeriod")
    public void onChangeCmbCourseForTabPeriod() {
        initCmbGroupForTabPeriod();
        Events.echoEvent("onLater", cmbGroupForTabPeriod, null);
    }

    private void initCmbGroupForTabPeriod() {
        initCmbGroup(cmbSemesterForTabPeriod, cmbQualificationForTabPeriod, cmbCourseForTabPeriod, cmbGroupForTabPeriod);
        uploadingDataWhenChangingTabPeriod();
    }

    @Listen("onLater = #cmbGroupForTabPeriod")
    public void onLaterCmbGroupForTabPeriod() {
        cmbGroupForTabPeriod.setSelectedIndex(0);
        Clients.clearBusy();
    }

    private void initDateBox() {
        dbBeginPeriod.setValue(currentGroupTabPeriod.getDateOfBeginSemester());
        dbEndPeriod.setValue(currentGroupTabPeriod.getDateOfEndSemester());
    }

    @Listen("onClick = #btnSearchForTabPeriod")
    public void onClickBtnSearchForTabPeriod() {
        uploadingDataWhenChangingTabPeriod();
        refreshJournal();
    }

    private void uploadingDataWhenChangingTabPeriod() {
        if (cmbGroupForTabPeriod.getSelectedIndex() == -1) {
            currentGroupTabPeriod = ((GroupSemesterModel) cmbGroupForTabPeriod.getModel().getElementAt(0));
        } else {
            currentGroupTabPeriod = cmbGroupForTabPeriod.getSelectedItem().getValue();
        }
        lessonsTabPeriod = attendanceService.getLessonsFromDb(currentGroupTabPeriod.getGroupname());

    }

    private void initCmbGroup(Combobox cmbSemester, Combobox cmbQualification, Combobox cmbCourse, Combobox cmbGroup) {
        Long idSem = 0L;
        if (cmbSemester.getSelectedIndex() == -1) {
            int i = 0;
            for (SemesterModel semesterModel : ((List<SemesterModel>) cmbSemester.getModel())) {
                if (semesterModel.isCurSem()) {
                    //cmbSemester.setSelectedIndex(i);
                    idSem = semesterModel.getIdSem();
                }
                i++;
            }
        } else {
            idSem = ((SemesterModel) cmbSemester.getSelectedItem().getValue()).getIdSem();
        }

        String qualification = cmbQualification.getSelectedItem().getValue();
        String courses = cmbCourse.getSelectedItem().getValue();
        componentGroupService.fillCmbGroupsWithSemester(cmbGroup, idSem, courses, qualification);
    }


    private void checkCompatibilityQualificationAndCourse(Combobox cmbCourse, Combobox cmbQualification) {
        int selectedIndex = cmbCourse.getSelectedIndex();
        if (cmbQualification.getSelectedItem().getLabel().equals("Бакалавр")) {
            cmbCourse.getItems().clear();
            for (int i = 1; i < 5; i++) {
                Comboitem comboitem = new Comboitem(String.valueOf(i));
                comboitem.setValue(String.valueOf(i));
                cmbCourse.getItems().add(comboitem);
            }
            Comboitem comboitem = new Comboitem("Все");
            comboitem.setValue("1,2,3,4");
            cmbCourse.getItems().add(comboitem);
            if (selectedIndex == -1 || selectedIndex > 3) {
                cmbCourse.setSelectedIndex(0);
            } else {
                cmbCourse.setSelectedIndex(selectedIndex);
            }
        } else if (cmbQualificationForTabDay.getSelectedItem().getLabel().equals("Магистр")) {
            cmbCourse.getItems().clear();
            for (int i = 1; i < 3; i++) {
                Comboitem comboitem = new Comboitem(String.valueOf(i));
                comboitem.setValue(String.valueOf(i));
                cmbCourse.getItems().add(comboitem);
            }
            Comboitem comboitem = new Comboitem("Все");
            comboitem.setValue("1,2");
            cmbCourse.getItems().add(comboitem);
            if (selectedIndex == -1 || selectedIndex > 1) {
                cmbCourse.setSelectedIndex(0);
            } else {
                cmbCourse.setSelectedIndex(selectedIndex);
            }
        } else {
            cmbCourse.getItems().clear();
            for (int i = 1; i < 7; i++) {
                Comboitem comboitem = new Comboitem(String.valueOf(i));
                comboitem.setValue(String.valueOf(i));
                cmbCourse.getItems().add(comboitem);
            }
            Comboitem comboitem = new Comboitem("Все");
            comboitem.setValue("1,2,3,4,5,6");
            cmbCourse.getItems().add(comboitem);
            if (selectedIndex == -1) {
                cmbCourse.setSelectedIndex(0);
            } else {
                cmbCourse.setSelectedIndex(selectedIndex);
            }
        }
    }

    @Listen("onFirstRun = #vbCalendar")
    public void onFirstRunFillSubject() {
        if (todayAttendance != null) {
            selectedDay = todayAttendance;
            refreshJournal(todayAttendance);
            Clients.clearBusy();
        } else {
            if (monthAttendance != null && monthAttendance.size() > 0) {
                selectedDay = monthAttendance.get(monthAttendance.size() - 1);
                refreshJournal(monthAttendance.get(monthAttendance.size() - 1));
                Clients.clearBusy();
            }
        }
        Clients.clearBusy();
    }

    @Listen("onLater = #vbCalendar")
    public void onLaterRefreshCalendar() {
        if (chooseMonth == null){
            PopupUtil.showError("Не заполнены даты начала и конца семестра.");
            Clients.clearBusy();
            return;
        }
        refreshCalendar();
        Clients.clearBusy();

    }

    @Listen("onLater = #lbSubjectForTabDay")
    public void laterOnLbSubject() {
        refreshJournal(selectedDay);
        Clients.clearBusy(lbSubjectForTabDay);
    }

    @Listen("onClick = #btnPrevMonth")
    public void choosePrevMonth() {
        chooseMonth = DateConverter.changeDateByMonth(chooseMonth, Calendar.MONTH, -1);
        Clients.showBusy("Загрузка данных");
        Events.echoEvent("onLater", vbCalendar, null);
    }

    @Listen("onClick = #btnNextMonth")
    public void chooseNextMonth() {
        chooseMonth = DateConverter.changeDateByMonth(chooseMonth, Calendar.MONTH, 1);
        Clients.showBusy("Загрузка данных");
        Events.echoEvent("onLater", vbCalendar, null);
    }

    private void refreshCalendar() {
        vbCalendar.getChildren().clear();
        Date dateBegin = DateConverter.getFirstDateOfMonthByCalendar(chooseMonth);
        btnPrevMonth.setDisabled(dateBegin.before(currentGroupTabDay.getDateOfBeginSemester()));
        Date dateEnd = DateConverter.getLastDateOfMonthByCalendar(chooseMonth);
        btnNextMonth.setDisabled(dateEnd.after(currentGroupTabDay.getDateOfEndSemester()));

        lCurrentMonth.setValue(DateConverter.getMonthByDate(chooseMonth));
        Hbox hboxForFillWeekDay = componentService.createHboxForFillWeekDay();
        hboxForFillWeekDay.setParent(vbCalendar);

        monthAttendance = journalOfAttendanceService.getMonthAttendModel(currentGroupTabDay.getIdLGS(), dateBegin, dateEnd);
        if (monthAttendance == null){
            PopupUtil.showError("Не заполнены даты начала и конца семестра у групп");
            return;
        }
        journalOfAttendanceService.fillMonthAttendance(monthAttendance, firstWeekSem, lessonsTabDay);
        int count = 0;
        for (MonthAttendSubjectsModel monthAttend : monthAttendance) {
            if (count == 0) {
                Hbox hboxWithFlex = componentService.createHboxWithFlex();
                hboxWithFlex.setParent(vbCalendar);
            }
            count++;
            fillHboxDay((Hbox) vbCalendar.getChildren().get(vbCalendar.getChildren().size() - 1), monthAttend);
            if (count == 7) count = 0;
        }
    }

    private void refreshJournal(MonthAttendSubjectsModel attendDay) {
        lCurrentDate.setValue("Посещаемость за '" + DateConverter.convertDateToString(attendDay.getDay()) + "'");
        List<AttendanceModel> subjectsAttendance = journalOfAttendanceService.getSubjectAttendanceByLGSandDate(currentGroupTabDay.getIdLGS(), attendDay.getDay());
        journalOfAttendanceService.fillStudentByAttendance(studentsTabDay, subjectsAttendance, currentGroupTabDay.getIdLGS(), attendDay.getDay());
        if (subjectsAttendance.size() == 0 && attendDay.getLessons().size() != 0) {
            for (int i = 0; i < attendDay.getLessons().size(); i++) {
                AttendanceModel attendance = new AttendanceModel();
                GroupSubjectLesson lesson = attendDay.getLessons().get(i);
                attendance.setLesson(lesson.getLesson());
                attendance.setIdLGSS(lesson.getIdLGSS());
                attendance.setPos(i + 1);
                attendance.setSubjectname(lesson.getSubjectName());
                attendance.setVisitDate(attendDay.getDay());
                subjectsAttendance.add(attendance);
            }
        }
        fillStudents(subjectsAttendance, attendDay);
    }

    private void refreshJournal() {
        if (dbBeginPeriod.getValue().getTime() > dbEndPeriod.getValue().getTime()) {
            Messagebox.show("Неверно выбран период");
            return;
        }
        periodAttendance = journalOfAttendanceService.getMonthAttendModel(currentGroupTabPeriod.getIdLGS(), dbBeginPeriod.getValue(), dbEndPeriod.getValue());
        journalOfAttendanceService.fillMonthAttendance(periodAttendance, firstWeekSem, lessonsTabPeriod);
        fillStudentsForPeriod();

    }

    private void fillStudentsForPeriod() {

        List<Auxheader> auxheaders = gridForPeriodTab.getChildren().get(1).getChildren();
        List<Column> columns = gridForPeriodTab.getChildren().get(2).getChildren();
        List<Row> rows = gridForPeriodTab.getChildren().get(3).getChildren();

        while (auxheaders.size() > 2)
            auxheaders.remove(2);

        while (columns.size() > 2)
            columns.remove(2);

        while (gridForPeriodTab.getRows().getVisibleItemCount() != 1)
            rows.remove(1);

        List<List<AttendanceModel>> subjectsAttendanceList = new ArrayList<List<AttendanceModel>>();
        List<List<StudentAttendanceModel>> studentsByAttendanceList = new ArrayList<List<StudentAttendanceModel>>();

        for (int i = 0; i < periodAttendance.size(); i++) {
            MonthAttendSubjectsModel selectedDayInPeriod = periodAttendance.get(i);
            List<AttendanceModel> subjectsAttendance = journalOfAttendanceService.getSubjectAttendanceByLGSandDate(currentGroupTabPeriod.getIdLGS(), selectedDayInPeriod.getDay());
            List<StudentAttendanceModel> studentsTabPeriod = journalOfAttendanceService.getStudentByGroup(currentGroupTabPeriod.getIdLGS());
            journalOfAttendanceService.fillStudentByAttendance(studentsTabPeriod, subjectsAttendance, currentGroupTabPeriod.getIdLGS(), selectedDayInPeriod.getDay());

            if (subjectsAttendance.size() == 0 && selectedDayInPeriod.getLessons().size() != 0) {
                for (int j = 0; j < selectedDayInPeriod.getLessons().size(); j++) {
                    AttendanceModel attendance = new AttendanceModel();
                    GroupSubjectLesson lesson = selectedDayInPeriod.getLessons().get(j);
                    attendance.setLesson(lesson.getLesson());
                    attendance.setIdLGSS(lesson.getIdLGSS());
                    attendance.setPos(j + 1);
                    attendance.setSubjectname(lesson.getSubjectName());
                    attendance.setVisitDate(selectedDayInPeriod.getDay());
                    subjectsAttendance.add(attendance);
                }
            }

            subjectsAttendanceList.add(subjectsAttendance);
            studentsByAttendanceList.add(studentsTabPeriod);
        }

        Auxheader auxheaderPass = new Auxheader();
        auxheaderPass.setParent(gridForPeriodTab.getChildren().get(1));
        auxheaderPass.setLabel("Пропущено");

        Column columnPass = new Column();
        columnPass.setParent(gridForPeriodTab.getColumns());
        columnPass.setWidth("100px");

        for (int i = 0; i < periodAttendance.size(); i++) {
            if (subjectsAttendanceList.get(i).size() != 0) {
                Auxheader auxheader = new Auxheader();
                auxheader.setParent(gridForPeriodTab.getChildren().get(1));
                Date dateForLabel = periodAttendance.get(i).getDay();
                auxheader.setLabel(new SimpleDateFormat("dd.MM.yyyy").format(dateForLabel));
                auxheader.setClass("category-center");
                auxheader.setAlign("center");
                auxheader.setTooltiptext("\tПосещаемость за " + periodAttendance.get(i).getDay().toString());

                for (AttendanceModel attendance : subjectsAttendanceList.get(i)) {
                    Column columnAttendance = new Column();
                    gridForPeriodTab.getColumns().appendChild(columnAttendance);
                    columnAttendance.setLabel(attendance.getSubjectname());
                    columnAttendance.setTooltiptext(attendance.getSubjectname());
                    columnAttendance.setWidth("60px");
                }
                auxheader.setColspan(subjectsAttendanceList.get(i).size());
            }
        }


        int count = 0;
        double percentPass = 0;
        for (int j = 0; j < studentsByAttendanceList.get(0).size(); j++) {
            ++count;
            Row row = new Row();
            gridForPeriodTab.getRows().appendChild(row);
            new Label(String.valueOf(count)).setParent(row);
            Label labelFio = new Label(studentsByAttendanceList.get(0).get(j).getFio());
            labelFio.setParent(row);
            Label labelNumberAttendance = new Label();
            labelNumberAttendance.setParent(row);
            double numberPass = 0;
            double numberLessons = 0;

            for (int i = 0; i < periodAttendance.size(); i++) {
                int numberPassDay = fillRowByAttendance(row, studentsByAttendanceList.get(i).get(j), periodAttendance.get(i));
                numberLessons += periodAttendance.get(i).getLessons().size();
                numberPass += numberPassDay;
            }
            labelNumberAttendance.setValue(Math.round(numberPass) + "/" + Math.round(numberLessons));
            percentPass += numberPass / numberLessons;
        }
        columnPass.setLabel(Math.round(percentPass) + "%");
    }


    private void fillStudents(List<AttendanceModel> subjectsAttendance, MonthAttendSubjectsModel attendDay) {
        lbSubjectForTabDay.getItems().clear();
        while (lbSubjectForTabDay.getListhead().getChildren().size() > 2)
            lbSubjectForTabDay.getListhead().getChildren().remove(2);
        if (subjectsAttendance.size() == 0) {
            Listheader lhr = new Listheader();
            lhr.setParent(lbSubjectForTabDay.getListhead());
            Label lHeader = new Label("\tНет занятий в этот день \\ Дата занятий не наступила");
            lHeader.setParent(lhr);
            lHeader.setSclass("cwf-listheader-label");
            lhr.setHflex("1");
            lhr.setTooltiptext("\tНет занятий в этот день \\ Дата занятий не наступила");
            lhr.setAlign("center");
            lhr.setStyle("cursor: pointer;");
        }
        for (AttendanceModel attendance : subjectsAttendance) {
            Listheader lhr = new Listheader();
            lhr.setParent(lbSubjectForTabDay.getListhead());
            Label lHeader = new Label(attendance.getSubjectname());
            lHeader.setParent(lhr);
            lHeader.setSclass("cwf-listheader-label");
            lhr.setHflex("1");
            lhr.setTooltiptext(attendance.getSubjectname());
            lhr.setAlign("center");
            lhr.setStyle("cursor: pointer;");
        }
        int count = 0;
        for (StudentAttendanceModel student : studentsTabDay) {
            for (int i = 0; i < attendDay.getLessons().size(); i++) {
                AttendanceModel attendance = new AttendanceModel();
                GroupSubjectLesson lesson = attendDay.getLessons().get(i);
                attendance.setLesson(lesson.getLesson());
                attendance.setIdLGSS(lesson.getIdLGSS());
                attendance.setIdSSS(student.getIdSSS());
                attendance.setPos(i + 1);
                attendance.setSubjectname(lesson.getSubjectName());
                attendance.setVisitDate(attendDay.getDay());
                student.getAttendances().add(attendance);
            }
            ++count;
            Listitem li = new Listitem();
            li.setParent(lbSubjectForTabDay);
            new Listcell(String.valueOf(count)).setParent(li);
            Listcell lcFio = new Listcell(student.getFio());
            lcFio.setParent(li);
            fillListitemByAttendance(li, student);
        }
    }

    private void fillListitemByAttendance(Listitem li, StudentAttendanceModel student) {
        if (student.getAttendances().size() == 0) {
            Listcell lcAttend = new Listcell();
            lcAttend.setHeight("20px");
            lcAttend.setStyle("background: #ccc;");
            lcAttend.setParent(li);
        }
        for (AttendanceModel attendance : student.getAttendances()) {
            Listcell lcAttend = new Listcell();
            lcAttend.setHeight("20px");
            if (attendance.getAttend() == null || attendance.getAttend() == -1)
                lcAttend.setStyle("background: #ccc;");
            else if (attendance.getAttend() == 0) {
                lcAttend.setLabel("Н");
                lcAttend.setStyle("background: #ff9494;");
            } else if (attendance.getAttend() == 1) {
                lcAttend.setLabel("+");
                lcAttend.setStyle("background: #94db70;");
            } else if (attendance.getAttend() == 2) {
                lcAttend.setLabel("Э");
                lcAttend.setStyle("background: #4DE2F7;");
            } else if (attendance.getAttend() == 3) {
                lcAttend.setLabel("П");
                lcAttend.setStyle("background: #FFFE7E;");
            }
            lcAttend.setParent(li);
        }
    }

    private int fillRowByAttendance(Row row, StudentAttendanceModel student, MonthAttendSubjectsModel dayAttendance) {
        int numberPassDay = 0;
        if (student.getAttendances().size() == 0) {
            for (int i = 0; i < dayAttendance.getLessons().size(); i++) {
                Cell cellAttend = new Cell();
                cellAttend.setHeight("20px");
                cellAttend.setStyle("background-color: #ccc; border-color: #eeeeee; border-width: 2px; border-top: 2px solid #eeeeee;");
                cellAttend.setParent(row);
            }
        }
        for (AttendanceModel attendance : student.getAttendances()) {
            Cell cellAttend = new Cell();
            cellAttend.setHeight("20px");
            cellAttend.setAlign("center");
            if (attendance.getAttend() == null || attendance.getAttend() == -1)
                cellAttend.setStyle("background-color: #ccc; border-color: #eeeeee; border-width: 2px; border-top: 2px solid #eeeeee;");
            else if (attendance.getAttend() == 0) {
                Label test = new Label("Н");
                numberPassDay++;
                test.setParent(cellAttend);
                cellAttend.setStyle("background-color: #ff9494; border-color: #eeeeee; border-width: 2px; border-top: 2px solid #eeeeee;");
            } else if (attendance.getAttend() == 1) {
                Label test = new Label("+");
                test.setParent(cellAttend);
                cellAttend.setStyle("background-color: #94db70; border-color: #eeeeee; border-width: 2px; border-top: 2px solid #eeeeee;");
            } else if (attendance.getAttend() == 2) {
                Label test = new Label("Э");
                test.setParent(cellAttend);
                cellAttend.setStyle("background-color: #4DE2F7; border-color: #eeeeee; border-width: 2px; border-top: 2px solid #eeeeee;");
            } else if (attendance.getAttend() == 3) {
                Label test = new Label("П");
                test.setParent(cellAttend);
                cellAttend.setStyle("background-color: #FFFE7E; border-color: #eeeeee; border-width: 2px; border-top: 2px solid #eeeeee;");
            }
            cellAttend.setParent(row);
        }
        return numberPassDay;
    }

    private void fillHboxDay(Hbox hbox, MonthAttendSubjectsModel monthAttend) {
        Hbox hboxDay;
        if (today.getYear() == monthAttend.getDay().getYear() && today.getMonth() == monthAttend.getDay().getMonth() && today.getDate() == monthAttend.getDay().getDate()) {
            todayAttendance = monthAttend;
            hboxDay = componentService.createHboxWithLabel(DateConverter.convertDateToDayString(monthAttend.getDay()), "day today");
        } else
            hboxDay = componentService.createHboxWithLabel(DateConverter.convertDateToDayString(monthAttend.getDay()), "day");
        if (monthAttend.getLessons().size() == 0 || monthAttend.getDay().after(today)
                || monthAttend.getDay().before(currentGroupTabDay.getDateOfBeginSemester()) || monthAttend.getDay().after(currentGroupTabDay.getDateOfEndSemester())) {
            hboxDay.setStyle("background: #ccc;");
        } else if (monthAttend.getDay().before(today) && monthAttend.getCount() != 0 && monthAttend.getCount() < (studentsTabDay.size()*monthAttend.getLessons().size()) ) {
            hboxDay.setStyle("background: yellow;");
        }
            else if (monthAttend.getDay().before(today) && monthAttend.getCount() == 0) {
            hboxDay.setStyle("background: #ff9494;");
        }
        hboxDay.setParent(hbox);
        hboxDay.addEventListener(Events.ON_CLICK, event -> {
            Clients.showBusy(lbSubjectForTabDay, "Загрузка данных");
            selectedDay = monthAttend;
            Events.echoEvent("onLater", lbSubjectForTabDay, null);
        });
    }

    @Listen("onAfterRender = #cmbSemesterForTabDay; ")
    public void setSemesterIndex(){
        cmbFormOfStudyForTabDay.setSelectedIndex(0);
        int i = 0;
        for (SemesterModel semesterModel : ((List<SemesterModel>) cmbSemesterForTabDay.getModel())) {
            if (semesterModel.isCurSem()) {
                cmbSemesterForTabDay.setSelectedIndex(i);
                cmbSemesterForTabPeriod.setSelectedIndex(i);
            }
            i++;
        }
    }
}