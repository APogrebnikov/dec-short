package org.edec.passportGroup.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by apple on 28.11.2017.
 */
@Getter
@Setter
public class ScholarshipInfo {
    private Long idCurDicGroup, idDicGroup, idSSS, idInstitute, idStudentcard, idSemester, idDicAction;
    private Boolean deductedCurSem, academicLeaveCurSem, governmentFinanced, nextGovernmentFinanced, deducted, academicLeave;
    private Date dateScholarshipBegin, dateScholarshipEnd, dateCompleteSession;
    private Integer sessionResult;
    private String sectionName;

    private String reason;
}
