package org.edec.student.calendarOfEvents.model;

import org.edec.model.AttendanceModel;
import org.edec.model.MonthAttendModel;
import org.edec.schedule.model.dao.GroupSubjectLesson;

import java.util.ArrayList;
import java.util.List;


public class MonthAttendStudentModel extends MonthAttendModel {
    private Long attendCount;

    private List<GroupSubjectLesson> lessons = new ArrayList<>();
    private List<AttendanceModel> attendances = new ArrayList<>();

    public Long getAttendCount () {
        return attendCount;
    }

    public void setAttendCount (Long attendCount) {
        this.attendCount = attendCount;
    }

    public List<GroupSubjectLesson> getLessons () {
        return lessons;
    }

    public void setLessons (List<GroupSubjectLesson> lessons) {
        this.lessons = lessons;
    }

    public List<AttendanceModel> getAttendances () {
        return attendances;
    }

    public void setAttendances (List<AttendanceModel> attendances) {
        this.attendances = attendances;
    }
}
