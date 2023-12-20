package org.edec.student.schedule.service.impl;

import org.edec.student.schedule.manager.ScheduleStudentManager;
import org.edec.student.schedule.model.ScheduleDateModel;
import org.edec.student.schedule.model.ScheduleModel;
import org.edec.student.schedule.model.ScheduleSubjectModel;
import org.edec.student.schedule.service.ScheduleStudentsService;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ScheduleStudentsServiceImpl implements ScheduleStudentsService {

    private ScheduleStudentManager manager = new ScheduleStudentManager();

    @Override
    public List<ScheduleSubjectModel> getDailySchedule(String groupname, String dayname, int week, String timeName) {
        return manager.getSchedule(groupname, dayname, week, timeName);
    }

    @Override
    public ScheduleDateModel getDatesOfSemester(String groupname) {
        return manager.getDateOfSemester(groupname);
    }

    @Override
    public int getCurrentWeek(Date selectDay, Date dateOfBeginSemester) {
        return  manager.getCurrentWeek(selectDay, dateOfBeginSemester);
    }

    @Override
    public List<String> getListTimeName() {
        return manager.getListTimeName();
    }

    @Override
    public ScheduleModel getScheduleForWeek(String groupname, Date date, String timeName) {
        ScheduleModel schedule = new ScheduleModel();
        ScheduleDateModel datesOfSemesters = getDatesOfSemester(groupname);
        schedule.setMonday(getDailySchedule(groupname, "Понедельник", getCurrentWeek(date, datesOfSemesters.getDateOfBeginSemester()), timeName));
        schedule.setTuesday(getDailySchedule(groupname, "Вторник", getCurrentWeek(date, datesOfSemesters.getDateOfBeginSemester()), timeName));
        schedule.setWednesday(getDailySchedule(groupname, "Среда", getCurrentWeek(date, datesOfSemesters.getDateOfBeginSemester()), timeName));
        schedule.setThursday(getDailySchedule(groupname, "Четверг", getCurrentWeek(date, datesOfSemesters.getDateOfBeginSemester()), timeName));
        schedule.setFriday(getDailySchedule(groupname, "Пятница", getCurrentWeek(date, datesOfSemesters.getDateOfBeginSemester()), timeName));
        schedule.setSaturday(getDailySchedule(groupname, "Суббота", getCurrentWeek(date, datesOfSemesters.getDateOfBeginSemester()), timeName));
        schedule.setTimeName(timeName);
        return schedule;
    }
}
