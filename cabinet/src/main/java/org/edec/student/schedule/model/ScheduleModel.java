package org.edec.student.schedule.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleModel {
    private String timeName;
    private Date selectDate;
    private List<ScheduleSubjectModel> monday, tuesday, wednesday, thursday, friday, saturday;
}
