package org.edec.student.calendarOfEvents.service.impl;

import org.edec.schedule.model.dao.GroupSubjectLesson;
import org.edec.student.calendarOfEvents.manager.CalOfEventManager;
import org.edec.student.calendarOfEvents.model.MonthAttendStudentModel;
import org.edec.student.calendarOfEvents.service.CalOfEventService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CalOfEventImpl implements CalOfEventService {
    private CalOfEventManager calOfEventManager = new CalOfEventManager();

    @Override
    public List<MonthAttendStudentModel> getAttendancesByMonth (Long idSSS, Date dateOfBegin, Date dateOfEnd) {
        return calOfEventManager.getAttendancesByDate(idSSS, dateOfBegin, dateOfEnd);
    }

    @Override
    public Long getIdSSSbyHum (Long idHum, Long idSem) {
        return calOfEventManager.getIdSSSbyHum(idHum, idSem);
    }

    @Override
    public void fillAtendanceByLessons (List<MonthAttendStudentModel> monthAttendances, Integer firstWeekSem,
                                        List<GroupSubjectLesson> lessons) {
        Calendar calendar = Calendar.getInstance();
        for (MonthAttendStudentModel monthAttend : monthAttendances) {
            calendar.setTime(monthAttend.getDay());
            for (GroupSubjectLesson lesson : lessons) {
                if (calendar.get(Calendar.DAY_OF_WEEK) == (lesson.getDicDayLesson().getIdDicDayLesson().intValue() + 1)) {
                    if ((firstWeekSem == 1 && ((lesson.getWeek() == 1 && (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 1)) ||
                                               (lesson.getWeek() == 2 && (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 0)))) ||
                        (firstWeekSem == 2 && ((lesson.getWeek() == 1 && (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 0)) ||
                                               (lesson.getWeek() == 2 && (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 1))))) {
                        monthAttend.getLessons().add(lesson);
                    }
                }
            }
        }
    }

    @Override
    public void fillAttendanceBySelectedDay (MonthAttendStudentModel selectedDay, Long idSSS) {
        selectedDay.getAttendances().addAll(calOfEventManager.getAttendanceBySSS(idSSS, selectedDay.getDay()));
    }
}
