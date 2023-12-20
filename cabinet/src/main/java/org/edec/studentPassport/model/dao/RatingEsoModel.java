package org.edec.studentPassport.model.dao;

import lombok.Data;

@Data
public class RatingEsoModel {
    private Boolean exam;
    private Boolean pass;
    private Boolean cp;
    private Boolean cw;
    private Boolean practic;

    private Integer examrating;
    private Integer passrating;
    private Integer cprating;
    private Integer cwrating;
    private Integer practicrating;

    private Integer type;

    private String semester;
    private String subjectname;
}
