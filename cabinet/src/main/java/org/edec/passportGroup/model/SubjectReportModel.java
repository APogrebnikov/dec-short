package org.edec.passportGroup.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.utility.constants.FormOfControlConst;

import java.util.Date;
import java.util.List;


@Getter
@Setter
public class SubjectReportModel implements Cloneable {
    private Long idSubject;
    private Long idLgss;
    private Long idDicSubject;
    private Long otherDbId;
    private Long idChair;
    private FormOfControlConst foc;
    private Boolean exam, pass, cp, cw, practic;
    private String subjectName;
    private String status;
    private String groupName;
    private Date checkDate;
    private Date checkExamDate;
    private Date checkPassDate;
    private Date checkCPDate;
    private Date checkCWDate;
    private Date checkPracticDate;
    private Date consultDate;
    private List<TeacherModel> listEmployees;
    private String depTitle;
    private String subjectCode;
    private Double hoursCount;
    private Double hoursAudCount;
    private Double hoursLabor;
    private Double hoursLection;
    private Double hoursPractic;
    private Integer type;
    private Boolean synchMine;
    private Boolean practice;

    @Override
    public SubjectReportModel clone () {
        try {
            return (SubjectReportModel) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }
}