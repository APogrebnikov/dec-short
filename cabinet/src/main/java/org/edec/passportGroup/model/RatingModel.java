package org.edec.passportGroup.model;

import org.edec.utility.constants.FormOfControlConst;

/**
 * Created by antonskripacev on 09.04.17.
 */
public class RatingModel {
    private Long idSR, idSubject;
    private Integer rating, type;
    private FormOfControlConst foc;
    private String status;
    private StudentModel student;

    public Integer getType () {
        return type;
    }

    public void setType (Integer type) {
        this.type = type;
    }

    public StudentModel getStudent () {
        return student;
    }

    public void setStudent (StudentModel student) {
        this.student = student;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    public Long getIdSR () {
        return idSR;
    }

    public void setIdSR (Long idSR) {
        this.idSR = idSR;
    }

    public Long getIdSubject () {
        return idSubject;
    }

    public void setIdSubject (Long idSubject) {
        this.idSubject = idSubject;
    }

    public FormOfControlConst getFoc () {
        return foc;
    }

    public void setFoc (FormOfControlConst foc) {
        this.foc = foc;
    }

    public Integer getRating () {
        return rating;
    }

    public void setRating (Integer rating) {
        this.rating = rating;
    }
}
