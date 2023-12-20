package org.edec.successful.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.utility.constants.FormOfControlConst;

import java.util.Date;


@Getter
@Setter
public class RatingModel {
    private Long idGroup;
    private Long idStudent;
    private Long idHumanface;
    private Long idStudentcard;
    private Long idSemester;
    private Long idDepartment;
    private Long idSRH;
    private Long idSubject;

    private String groupname;
    private String fio;
    private String subjectName;
    private String recordbook;
    private String regNumber;
    private String departmentName;
    private String specialityCode;

    private Integer course;
    private Integer formOfStudy;
    private Integer lvl;
    private Boolean govFinanced;

    private Boolean pass, exam, cp, cw, practic;
    private Integer passRating, examRating, CPRating, CWRating, practicRating;
    private Integer rating;
    private Date signdate;
    private Integer type;
    private FormOfControlConst foc;
    private String status;

    //Выпускающая кафедра T
    private String tChairFulltitle;
    //Выпускающая кафедра E
    private String eChairFulltitle;
    private String eChairShorttitle;
    private Long tChairId;
    private Long eChairId;

    public FormOfControlConst getFOC () {
        if (pass) {
            return FormOfControlConst.PASS;
        }
        if (exam) {
            return FormOfControlConst.EXAM;
        }
        if (cp) {
            return FormOfControlConst.CP;
        }
        if (cw) {
            return FormOfControlConst.CW;
        }
        if (practic) {
            return FormOfControlConst.PRACTIC;
        }
        return null;
    }
}
