package org.edec.student.calendarOfEvents.ctrl;

import org.edec.model.AttendanceModel;
import org.edec.model.GroupSemesterModel;
import org.edec.schedule.model.dao.GroupSubjectLesson;
import org.edec.schedule.service.AttendanceService;
import org.edec.schedule.service.impl.AttendanceImpl;
import org.edec.student.calendarOfEvents.model.MonthAttendStudentModel;
import org.edec.student.calendarOfEvents.service.CalOfEventService;
import org.edec.student.calendarOfEvents.service.impl.CalOfEventImpl;
import org.edec.student.journalOfAttendance.service.JournalOfAttendanceService;
import org.edec.student.journalOfAttendance.service.impl.JournalOfAttendanceServiceImpl;
import org.edec.student.recordBook.model.GroupModel;
import org.edec.student.recordBook.model.StudentSemesterModel;
import org.edec.student.recordBook.service.RecordBookService;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Button btnPrevMonth, btnNextMonth;
    @Wire
    private Label lSelectedDay, lCurrentMonth, lBeginSem, lEndSem, lTagging;
    @Wire
    private Vbox vbCalendar, vbEvents;
    @Wire
    private Combobox cmbSemester, cmbGroup;

    private AttendanceService attendanceService = new AttendanceImpl();
    private CalOfEventService calOfEventService = new CalOfEventImpl();
    private ComponentService componentService = new ComponentServiceESOimpl();
    private JournalOfAttendanceService journalOfAttendanceService = new JournalOfAttendanceServiceImpl();

    private Date chooseMonth;
    private Date today = new Date();
    private GroupSemesterModel currentGroup;
    private GroupModel selectedGroup;
    private Integer firstWeekSem = 2;
    private Long idSSS;
    private MonthAttendStudentModel todayAttendance;
    private List<GroupSubjectLesson> lessons = new ArrayList<>();
    private List<MonthAttendStudentModel> monthAttendances;

    @Override
    protected void fill() {

        template.setVisitedModuleByHum(template.getCurrentUser().getIdHum(), currentModule.getIdModule());

        initCmbGroup();
        initCmbSemester();
        Clients.showBusy(vbCalendar, "Загрузка данных");
        Events.echoEvent(Events.ON_CHANGE, cmbSemester, null);
        Events.echoEvent("onFirstRun", vbCalendar, null);
        Clients.clearBusy(vbCalendar);
    }

    private void initCmbSemester() {
        cmbSemester.setItemRenderer((comboitem, studentSemesterModel, i) -> {
            comboitem.setValue(studentSemesterModel);
            comboitem.setLabel(((StudentSemesterModel) studentSemesterModel).getSemesterNumber());
        });
        GroupModel selectedGroup = (GroupModel)cmbGroup.getModel().getElementAt(0);
        cmbSemester.setModel(new ListModelList<>(selectedGroup.getSemesters()));
    }

    private void initCmbGroup() {
        RecordBookService recordBookService = new RecordBookService();
        List<GroupModel> groups = recordBookService.getGroupByHum(template.getCurrentUser().getIdHum());
        cmbGroup.setItemRenderer((comboitem, groupModel, i) -> {
            comboitem.setValue(groupModel);
            comboitem.setLabel(((GroupModel) groupModel).getGroupname());
        });
        cmbGroup.setVisible(groups.size() > 1);
        cmbGroup.setModel(new ListModelList<>(groups));
    }

    @Listen("onAfterRender = #cmbGroup")
    public void onAfterRenderCmbGroup () {
        if(cmbGroup.getItems().size() != 0) {
            cmbGroup.setSelectedIndex(0);
            selectedGroup = cmbGroup.getSelectedItem().getValue();
            cmbSemester.setModel(new ListModelList<>(selectedGroup.getSemesters()));
        }
    }

    @Listen("onChange = #cmbGroup")
    public void onChangeCmbGroup () {
        selectedGroup = cmbGroup.getSelectedItem().getValue();
        cmbSemester.setModel(new ListModelList<>(selectedGroup.getSemesters()));
        Events.echoEvent(Events.ON_CHANGE, cmbSemester, null);
    }

    @Listen("onAfterRender = #cmbSemester")
    public void onAfterRenderCmbSemester () {
        if(cmbSemester.getItems().size() != 0) {
            cmbSemester.setSelectedIndex(0);
        }
    }

    @Listen("onChange = #cmbSemester")
    public void onChangeSemester () {
        uploadingDataWhenChanging();
        refreshCalendar();
    }

    @Listen("onLater = #vbCalendar")
    public void onLaterRefreshCalendar() {
        refreshCalendar();
        Clients.clearBusy();
    }

    @Listen("onFirstRun = #vbCalendar")
    public void onFirstRun() {
        if (todayAttendance != null) {
            selectDay(todayAttendance);
        } else if(monthAttendances != null){
            selectDay(monthAttendances.get(monthAttendances.size() - 1));
        }
    }

    @Listen("onClick = #btnPrevMonth")
    public void changeOnPrevMonth() {
        chooseMonth = DateConverter.changeDateByMonth(chooseMonth, Calendar.MONTH, -1);
        Clients.showBusy("Загрузка данных");
        Events.echoEvent("onLater", vbCalendar, null);
    }

    @Listen("onClick = #btnNextMonth")
    public void changeOnNextMonth() {
        chooseMonth = DateConverter.changeDateByMonth(chooseMonth, Calendar.MONTH, 1);
        Clients.showBusy("Загрузка данных");
        Events.echoEvent("onLater", vbCalendar, null);
    }

    private void uploadingDataWhenChanging() {
        Long idSem = selectedGroup.getSemesters().get(cmbSemester.getSelectedIndex()).getIdSem();

        currentGroup = journalOfAttendanceService.getGroupSemesterByHum(template.getCurrentUser().getIdHum(), idSem);
        if (currentGroup == null){
            return;
        }
        idSSS = calOfEventService.getIdSSSbyHum(template.getCurrentUser().getIdHum(), idSem);
        lessons = attendanceService.getLessonsFromDb(currentGroup.getGroupname(), idSem);
        lBeginSem.setValue("Начало семестра: " + (currentGroup.getDateOfBeginSemester() != null ? DateConverter.convertDateToString(currentGroup.getDateOfBeginSemester()) : ""));
        lEndSem.setValue("Окончание семестра: " + (currentGroup.getDateOfEndSemester() != null ? DateConverter.convertDateToString(currentGroup.getDateOfEndSemester()) : ""));

        if (today.before(currentGroup.getDateOfBeginSemester())) chooseMonth = currentGroup.getDateOfEndSemester();
        else if (today.after(currentGroup.getDateOfEndSemester())) chooseMonth = currentGroup.getDateOfEndSemester();
        else chooseMonth = today;
    }

    public void refreshCalendar() {
        while (vbCalendar.getChildren().size() > 1)
            vbCalendar.getChildren().remove(1);
        Date dateBegin = DateConverter.getFirstDateOfMonthByCalendar(chooseMonth);
        btnPrevMonth.setDisabled(dateBegin.before(currentGroup.getDateOfBeginSemester()));
        Date dateEnd = DateConverter.getLastDateOfMonthByCalendar(chooseMonth);
        btnNextMonth.setDisabled(dateEnd.after(currentGroup.getDateOfEndSemester()));
        lCurrentMonth.setValue(DateConverter.getMonthByDate(chooseMonth));

        componentService.createHboxForFillWeekDay().setParent(vbCalendar);

        monthAttendances = calOfEventService.getAttendancesByMonth(idSSS, dateBegin, dateEnd);
        calOfEventService.fillAtendanceByLessons(monthAttendances, firstWeekSem, lessons);

        int count = 0;
        for (MonthAttendStudentModel monthAttendance : monthAttendances) {
            if (count == 0) componentService.createHboxWithFlex().setParent(vbCalendar);
            count++;
            fillHboxDay((Hbox) vbCalendar.getChildren().get(vbCalendar.getChildren().size() - 1), monthAttendance);
            if (count == 7)
                count = 0;
        }
    }

    private void fillHboxDay(Hbox hbox, MonthAttendStudentModel monthAttendance) {
        Hbox hboxDay;
        if (today.getYear() == monthAttendance.getDay().getYear() && today.getMonth() == monthAttendance.getDay().getMonth() && today.getDate() == monthAttendance.getDay().getDate()) {
            todayAttendance = monthAttendance;
            hboxDay = componentService.createHboxWithLabel(DateConverter.convertDateToDayString(monthAttendance.getDay()), "day today");
        } else
            hboxDay = componentService.createHboxWithLabel(DateConverter.convertDateToDayString(monthAttendance.getDay()), "day");

        if (monthAttendance.getDay().before(currentGroup.getDateOfBeginSemester()) || monthAttendance.getDay().after(currentGroup.getDateOfEndSemester())
                || (monthAttendance.getCount() == 0 && monthAttendance.getLessons().size() == 0))
            hboxDay.setStyle("background: #ccc;"); //занятий не было
        if (monthAttendance.getDay().before(today) && monthAttendance.getCount() == 0 && monthAttendance.getLessons().size() > 0)
            hboxDay.setStyle("background: #AFEEEE;"); //староста не заполнил журнал посещаемости
        if (monthAttendance.getCount() > 0 && monthAttendance.getAttendCount().equals(monthAttendance.getCount()))
            hboxDay.setStyle("background: #95FF82;"); //100%-ая посещаемость
        if (monthAttendance.getCount() > 0 && monthAttendance.getAttendCount() < monthAttendance.getCount())
            hboxDay.setStyle("background: #FFEF70"); //Посещена часть занятий
        if (monthAttendance.getCount() > 0 && monthAttendance.getAttendCount() == 0)
            hboxDay.setStyle("background: #ff9494;"); //Все занятия пропущены


        hboxDay.setParent(hbox);
        hboxDay.addEventListener(Events.ON_CLICK, event -> {
            selectDay(monthAttendance);
        });
    }

    private void selectDay(MonthAttendStudentModel selectedDay) {
        lTagging.setValue("*чтобы посмотреть расписание - нажмите на поле с числом месяца");
        lTagging.setParent(vbCalendar);
        while (vbEvents.getChildren().size() > 1)
            vbEvents.getChildren().remove(1);
        if (selectedDay.getAttendances().size() == 0)
            calOfEventService.fillAttendanceBySelectedDay(selectedDay, idSSS);
        if (selectedDay.getAttendances().size() != 0) {
            lSelectedDay.setValue("Посещаемость за " + (DateConverter.convertDateToString(selectedDay.getDay())));
            for (AttendanceModel attendance : selectedDay.getAttendances()) {
                String textAttend = attendance.getSubjectname();
                Label label = new Label();
                if (attendance.getAttend() == 1) {
                    textAttend += " (посетил занятие)";
                    label.setValue(textAttend);
                    label.setStyle("font-size: 16pt; background: #95FF82;");
                } else if (attendance.getAttend() == 2) {
                    textAttend += " (работа в электронных курсах)";
                    label.setValue(textAttend);
                    label.setStyle("font-size: 16pt; background: #95FF82;");
                } else if (attendance.getAttend() == 3) {
                    textAttend += " (преподаватель не пришел)";
                    label.setValue(textAttend);
                    label.setStyle("font-size: 16pt; background: #ccc;");
                } else {
                    textAttend += " (не посетил занятие)";
                    label.setValue(textAttend);
                    label.setStyle("font-size: 16pt; background: #ff9494;");
                }
                label.setParent(vbEvents);
            }
        } else {
            lSelectedDay.setValue("Расписание на " + (DateConverter.convertDateToString(selectedDay.getDay())));
            for (GroupSubjectLesson lesson : selectedDay.getLessons()) {
                Label label = new Label(lesson.getSubjectName() + " - " + lesson.getTeacher() + "(" + (lesson.getRoom()) + ")");
                label.setParent(vbEvents);
                label.setStyle("font-size: 16pt;");
            }
        }
    }
}
