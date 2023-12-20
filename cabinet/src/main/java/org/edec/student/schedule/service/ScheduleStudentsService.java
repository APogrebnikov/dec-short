package org.edec.student.schedule.service;

import org.edec.student.schedule.model.ScheduleDateModel;
import org.edec.student.schedule.model.ScheduleModel;
import org.edec.student.schedule.model.ScheduleSubjectModel;

import java.util.Date;
import java.util.List;

public interface ScheduleStudentsService {

    List<ScheduleSubjectModel> getDailySchedule(String groupname, String dayname, int week, String timeName);
    ScheduleDateModel getDatesOfSemester(String groupname);
    int getCurrentWeek(Date selectDay, Date dateOfBeginSemester);
    List<String> getListTimeName();
    ScheduleModel getScheduleForWeek(String groupname, Date date, String timeName);
}
