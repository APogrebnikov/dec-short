package org.edec.curriculumSchedule.model;

import lombok.Data;

import java.util.Date;

@Data
public class GroupModel {
    private Long idDg;
    private Long idLgs;
    private String groupName;
    private Date dateOfBeginStudy;
    private Date dateOfEndStudy;
    private Date dateOfBeginSemester;
    private Date dateOfEndSemester;
    private Date dateOfBeginPassWeek;
    private Date dateOfEndPassWeek;
    private Date dateOfBeginSession;
    private Date dateOfEndSession;
    private Date dateOfBeginVacation;
    private Date dateOfEndVacation;
    private Date dateOfCertificationFrom, dateOfCertificationTo;
    private Date dateOfBeginPractice, dateOfEndPractice;
}
