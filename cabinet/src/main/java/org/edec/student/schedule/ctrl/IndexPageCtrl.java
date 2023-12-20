package org.edec.student.schedule.ctrl;

import org.edec.main.model.UserModel;
import org.edec.student.schedule.ctrl.renderer.ScheduleStudentsRenderer;
import org.edec.student.schedule.model.ScheduleDateModel;
import org.edec.student.schedule.model.ScheduleModel;
import org.edec.student.schedule.service.ScheduleStudentsService;
import org.edec.student.schedule.service.impl.ScheduleStudentsServiceImpl;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.*;
import java.util.Calendar;

public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Listbox lbSchedule;
    @Wire
    private Label lGroupname, lGroupnameVal, lSemester, lSemesterVal, lWeek, lWeekVal, lDate, lCurrentMonth;
    @Wire
    private Datebox dbSelectDate;
    @Wire
    private Button btnBackToToday;

    private UserModel currentUser = template.getCurrentUser();
    private Date today = new Date();

    private ScheduleStudentsService service = new ScheduleStudentsServiceImpl();


    private String groupname = currentUser.getGroupname();
    private ScheduleDateModel datesOfSemester = service.getDatesOfSemester(groupname);

    @Override
    protected void fill() throws InterruptedException {
        if (groupname != null) {
            refreshSchedule(today);
        } else {
            lGroupname.setValue("Группа:");
            lSemester.setValue("Семестр:");
            lWeek.setValue("Неделя: ");
            lDate.setValue("Дата: ");
            dbSelectDate.setValue(today);
            btnBackToToday.setDisabled(false);
        }
    }

    public Date getFirstDateOfWeek(Date selectDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectDay);
        cal.add(Calendar.DAY_OF_WEEK, - (cal.get(Calendar.DAY_OF_WEEK) - 2));
        Date date = cal.getTime();

        return date;
    }

    public Date getLastDateOfWeek(Date selectDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectDay);
        Date date = cal.getTime();
        cal.add(Calendar.DAY_OF_WEEK, 7 - (date.getDay()));
        date = cal.getTime();

        return date;
    }

    public void refreshSchedule(Date selectDate) {
        lGroupnameVal.setValue(groupname);
        if (datesOfSemester.getDateOfBeginSemester() != null && datesOfSemester.getDateOfEndSemester() != null){
            lSemesterVal.setValue(DateConverter.convert2dateToString(datesOfSemester.getDateOfBeginSemester(), datesOfSemester.getDateOfEndSemester()) + " " +
                    (datesOfSemester.getSeason() == 0 ? "осень" : "весна"));
            lWeekVal.setValue(
                    (service.getCurrentWeek(selectDate, getFirstDateOfWeek(datesOfSemester.getDateOfBeginSemester())) == 1 ? "нечетная" : " четная")
                            + " (" + DateConverter.convertDateToString(getFirstDateOfWeek(selectDate))
                            + " - "+ DateConverter.convertDateToString(getLastDateOfWeek(selectDate)) + ")");
            dbSelectDate.setValue(selectDate);

            List<ScheduleModel> schedule = new ArrayList<>();
            List<String> timeNameList = service.getListTimeName();
            for (String time : timeNameList){
                ScheduleModel scheduleModel = service.getScheduleForWeek(groupname, selectDate, time);
                scheduleModel.setSelectDate(selectDate);
                schedule.add(scheduleModel);
            }

            ListModelList<ScheduleModel> lmSchedule = new ListModelList<>(schedule);
            lbSchedule.setModel(lmSchedule);
            lbSchedule.setItemRenderer(new ScheduleStudentsRenderer());
            lbSchedule.renderAll();
        } else {
            lbSchedule.setEmptyMessage("Не заполнены даты начала и конца семестра!");
        }


    }

    @Listen("onChange = #dbSelectDate")
    public void refreshScheduleByDate(){
        if (groupname != null){
            refreshSchedule(dbSelectDate.getValue());
        }

    }
    @Listen("onClick = #btnBackToToday")
    public void backToToday(){
        if (groupname != null){
            dbSelectDate.setValue(today);
            refreshSchedule(dbSelectDate.getValue());
        }
    }
}

