package org.edec.attendance.model;

import org.edec.model.MonthAttendModel;
import org.edec.schedule.model.dao.GroupSubjectLesson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmmax work on 21.09.2017.
 */
public class MonthAttendSubjectsModel extends MonthAttendModel {
    private List<GroupSubjectLesson> lessons = new ArrayList<>();

    public MonthAttendSubjectsModel() {
    }

    public List<GroupSubjectLesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<GroupSubjectLesson> lessons) {
        this.lessons = lessons;
    }
}
