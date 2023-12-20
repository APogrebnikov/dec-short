package org.edec.schedule.model.dao;

import lombok.Data;


@Data
public class GroupSubjectLessonEso {
    private Boolean lesson;

    private Integer week;

    private Long idLGSS;
    private Long idLSCH;

    private String dayName;
    private String room;
    private String subjectName;
    private String teacher;
    private String timeName;
}
