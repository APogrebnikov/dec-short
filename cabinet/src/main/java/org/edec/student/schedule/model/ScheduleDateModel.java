package org.edec.student.schedule.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class ScheduleDateModel {
    private Date day;
    private int numberOfDays, season;
    private Date dateOfBeginSemester, dateOfEndSemester;

}
