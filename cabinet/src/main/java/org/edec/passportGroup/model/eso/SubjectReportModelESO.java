package org.edec.passportGroup.model.eso;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by ilyabaikalow on 15.11.17.
 */
@Getter
@Setter
public class SubjectReportModelESO {
    private Long idLgss;
    private Long idLesg;
    private Long idSubject;
    private Long idEmployee;
    private Long idDicSubject;
    private Long otherDbId;
    private Long idChair;
    private Boolean pass;
    private Boolean exam;
    private Boolean cp;
    private Boolean cw;
    private Boolean practic;
    private String subjectName;
    private String groupName;
    private String fullName;
    private String instTitle;
    private String depTitle;
    private Date examDate;
    private Date passDate;
    private Date courseProjectDate;
    private Date courseWorkDate;
    private Date practicDate;
    private Date consultDate;
    private String subjDepTitle;
    private String subjectCode;
    private Double hoursCount;
    private Double hoursAudCount;
    private Double hoursLabor;
    private Double hoursLection;
    private Double hoursPractic;
    private Integer type;
    private Boolean synchMine;
    private Boolean practice;
}
