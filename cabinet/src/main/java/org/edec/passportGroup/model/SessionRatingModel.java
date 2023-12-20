package org.edec.passportGroup.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionRatingModel {
    private Long idSubject;
    private Integer type;
    private Boolean exam;
    private Boolean pass;
    private Boolean cp;
    private Boolean cw;
    private Boolean practic;
    private Long idSSS;
    private Double esoGradeCurrent;
    private Integer examRating;
    private Integer passRating;
    private Integer cpRating;
    private Integer cwRating;
    private Integer practicRating;
    private Double esoGradeMax;
    private Integer skipCount;
    private Integer visitCount;
    private Integer esoExamRating;
    private Integer esoPassRating;
    private Integer esoCPRating;
    private Integer esoCWRating;
    private Integer retakeCount;
    private Integer idDopEso2;
    private Integer attendenceCount;
    private Boolean esoStudy;
    private String status;
    private Long idSR;
}
