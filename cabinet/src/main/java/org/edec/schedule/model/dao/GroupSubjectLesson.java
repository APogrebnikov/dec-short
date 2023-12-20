package org.edec.schedule.model.dao;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
public class GroupSubjectLesson {
    private Boolean lesson;

    private Integer week;

    private Long idLGSS;
    private Long idLSCH;

    private String room;
    private String subjectName;
    private String teacher;

    private DicDayLesson dicDayLesson;
    private DicTimeLesson dicTimeLesson;
}
