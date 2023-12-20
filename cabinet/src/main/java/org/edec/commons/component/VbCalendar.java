package org.edec.commons.component;


import org.edec.attendance.model.MonthAttendSubjectsModel;
import org.edec.attendance.service.JournalOfAttendanceService;
import org.edec.attendance.service.impl.JournalOfAttendanceServiceImpl;
import org.edec.model.GroupSemesterModel;
import org.edec.schedule.model.dao.GroupSubjectLesson;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.converter.DateConverter;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import org.zkoss.composite.Composite;
import org.zkoss.composite.CompositeCtrls;
import org.zkoss.composite.Composites;

import java.util.*;

@Composite(name = "vbcalendar", macroURI = "/WEB-INF/component/vbcalendar.zul")
public class VbCalendar extends Div implements IdSpace {

    @Wire
    private Button btnPrevMonth, btnNextMonth;
    @Wire
    private Label lCurrentMonth;
    @Wire
    private Vbox vbCalendar;

    private Listbox lbSubject;
    private Date today = new Date();
    private Date chooseMonth;
    private List<MonthAttendSubjectsModel> monthAttendance;
    private GroupSemesterModel currentGroup;
    private List<GroupSubjectLesson> lessons;
    private MonthAttendSubjectsModel todayAttendance;
    private MonthAttendSubjectsModel selectedDay;

    private ComponentService componentService = new ComponentServiceESOimpl();
    private JournalOfAttendanceService journalOfAttendanceService = new JournalOfAttendanceServiceImpl();

    public VbCalendar() {
        try {
            CompositeCtrls.register(VbCalendar.class, Executions.getCurrent().getDesktop().getWebApp());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        Composites.doCompose(this, null);

    }

    public MonthAttendSubjectsModel onFirstRunFillSubject() {
        if (todayAttendance != null) {
            selectedDay = todayAttendance;
        } else {
            if (monthAttendance.size() > 0) {
                selectedDay = monthAttendance.get(monthAttendance.size() - 1);
            }
        }
        return selectedDay;
    }


    public void onLaterRefreshCalendar() {
        refreshCalendar();
        Clients.clearBusy();
    }

    @Listen("onClick = #btnPrevMonth")
    public void choosePrevMonth() {
        chooseMonth = DateConverter.changeDateByMonth(chooseMonth, java.util.Calendar.MONTH, -1);
        Clients.showBusy("Загрузка данных");
        onLaterRefreshCalendar();
    }

    @Listen("onClick = #btnNextMonth")
    public void chooseNextMonth() {
        chooseMonth = DateConverter.changeDateByMonth(chooseMonth, java.util.Calendar.MONTH, 1);
        Clients.showBusy("Загрузка данных");
        onLaterRefreshCalendar();
    }


    public void refreshCalendar() {
        int firstWeekSem = 1;
        vbCalendar.getChildren().clear();
        chooseMonth = currentGroup.getDateOfBeginSemester();
        Date dateBegin = DateConverter.getFirstDateOfMonthByCalendar(chooseMonth);
        btnPrevMonth.setDisabled(dateBegin.before(currentGroup.getDateOfBeginSemester()));
        Date dateEnd = DateConverter.getLastDateOfMonthByCalendar(chooseMonth);
        btnNextMonth.setDisabled(dateEnd.after(currentGroup.getDateOfEndSemester()));

        lCurrentMonth.setValue(DateConverter.getMonthByDate(chooseMonth));
        Hbox hboxForFillWeekDay = componentService.createHboxForFillWeekDay();
        hboxForFillWeekDay.setParent(vbCalendar);

        monthAttendance = journalOfAttendanceService.getMonthAttendModel(currentGroup.getIdLGS(), dateBegin, dateEnd);
        journalOfAttendanceService.fillMonthAttendance(monthAttendance, firstWeekSem, lessons);
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

    private void fillHboxDay(Hbox hbox, MonthAttendSubjectsModel monthAttend) {
        Hbox hboxDay;
        if (today.getYear() == monthAttend.getDay().getYear() && today.getMonth() == monthAttend.getDay().getMonth() && today.getDate() == monthAttend.getDay().getDate()) {
            todayAttendance = monthAttend;
            hboxDay = componentService.createHboxWithLabel(DateConverter.convertDateToDayString(monthAttend.getDay()), "day today");
        } else
            hboxDay = componentService.createHboxWithLabel(DateConverter.convertDateToDayString(monthAttend.getDay()), "day");
        if (monthAttend.getLessons().size() == 0 || monthAttend.getDay().after(today)
                || monthAttend.getDay().before(currentGroup.getDateOfBeginSemester()) || monthAttend.getDay().after(currentGroup.getDateOfEndSemester())) {
            hboxDay.setStyle("background: #ccc;");
        } else if (monthAttend.getDay().before(today) && monthAttend.getCount() == 0) {
            hboxDay.setStyle("background: #ff9494;");
        }
        hboxDay.setParent(hbox);
        hboxDay.addEventListener(Events.ON_CLICK, event -> {
            Clients.showBusy(lbSubject, "Загрузка данных");
            selectedDay = monthAttend;
            Events.echoEvent("onLater", lbSubject, null);
        });
    }

    public void setCurrentGroup(GroupSemesterModel currentGroup) {
        this.currentGroup = currentGroup;
    }

    public void setLessons(List<GroupSubjectLesson> lessons) {
        this.lessons = lessons;
    }

    public void setlbSubject(Listbox lbSubject) {
        this.lbSubject = lbSubject;
    }

    public void setMonthAttendance(List<MonthAttendSubjectsModel> monthAttendance) {
        this.monthAttendance = monthAttendance;
    }

}
