package org.edec.schedule.model.dao;

import lombok.Data;

import java.util.Date;


@Data
public class DicTimeLesson {
    private Long idDicTimeLesson;

    private Date startTime;
    private Date endTime;

    private String timeName;
    private String nameLesson;
}
