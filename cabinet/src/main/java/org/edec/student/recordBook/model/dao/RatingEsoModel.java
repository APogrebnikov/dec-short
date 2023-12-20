package org.edec.student.recordBook.model.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class RatingEsoModel {
    private Boolean exam;
    private Boolean pass;
    private Boolean cp;
    private Boolean cw;
    private Boolean practic;

    private Date consultationDate;
    private Date dateOfPass;
    private Date statusBeginDate;
    private Date statusFinishDate;
    private Date cpDate;

    private Double esogradecurrent;
    private Double esogrademax;
    private Double hoursCount;

    private Integer examrating;
    private Integer passrating;
    private Integer cprating;
    private Integer cwrating;
    private Integer practicrating;

    private Integer esoexamrating;
    private Integer esopassrating;
    private Integer esocprating;
    private Integer esocwrating;

    private Integer retakeCount;

    private Long idEsoCourse;
    private Long lessonCount;
    private Long visitCount;

    private String status;
    private String subjectName;

    private String examTutor;
    private String passTutor;
    private String cpTutor;
    private String cwTutor;
    private String practicTutor;
    private String teacher;

    public RatingEsoModel () {
    }
}
