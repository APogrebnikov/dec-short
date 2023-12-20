package org.edec.order.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class StudentToCreateModel {
    private Long id;
    private Long idStudentcard;

    private String groupname;
    private String prevOrderNumber;
    private String fio;

    private Boolean sirota, invalid, transfer, nextGovernmentFinanced, governmentFinanced;
    private Boolean getSocialPrev, deductedCurSem;

    private Integer semesternumber;
    private Integer typeInvalid;
    private Integer sessionResult;
    private Integer sessionResultPrev;
    private Integer qualification;

    private Date dateCompleteSession;
    private Date dateOfEndSession;
    private Date dateNextEndOfSession;
    private Date dateOfEndEducation;
    private Date dateOfEndElimination;
    private Date prolongationEndDate;
    private Date firstDate;
    private Date secondDate;
    private Date birthDate;
    private Date prevOrderDateSign;
    private Date prevOrderTransferTo;
    private Date prevOrderTransferToProl;
    private Date dateOfBeginSchoolYear;

    private Double periodOfStudy;
}
