package org.edec.teacher.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
public class CompletionESOModel {
    private Boolean curSem;

    private Boolean exam, pass, cp, cw, practic;

    private Date dateofbegin,  dateofend; // даты начала и конца учебного года
    private Date completionDate;
    private Date examdate, passdate, cpdate, cwdate, practicdate;
    private Date begindate, enddate, secondDateBegin, secondDateEnd; //даты пересдач

    private Integer formofcontrol;
    private Integer formofstudy;
    private Integer season;
    private Integer course;
    private Integer semesterNumber;
    private Integer type;

    private Double hoursCount;

    private Long idESOcourse;
    private Long idLGSS;
    private Long idInstitute;
    private Long idSemester;

    private String groupname;
    private String institute;
    private String semesterStr;
    private String subjectname;
    private String certnumber;

    private Long idRegister = null;
   // private Date begindate = null;
    private String registerNumber = null;
    private Integer retakeCount = null;
    private Date signdate = null;
}
