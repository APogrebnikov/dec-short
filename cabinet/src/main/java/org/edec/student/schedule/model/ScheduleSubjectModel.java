package org.edec.student.schedule.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleSubjectModel {
    private String subjectname, dayName, room,  teacher, timeName;
    private Long idLGSS, idLSCH;
    private Boolean lesson;

    private Integer week;
}
