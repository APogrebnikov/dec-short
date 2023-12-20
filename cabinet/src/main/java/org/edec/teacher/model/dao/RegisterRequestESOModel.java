package org.edec.teacher.model.dao;

import org.edec.teacher.model.registerRequest.StudentModel;

import java.util.Date;

public class RegisterRequestESOModel {
    private String family;
    private String name;
    private String patronymic;
    private String additionalInfo;

    private long idSss;
    private Integer status;
    private Date applyingDate;

    public String getFamily () {
        return family;
    }

    public void setFamily (String family) {
        this.family = family;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getPatronymic () {
        return patronymic;
    }

    public void setPatronymic (String patronymic) {
        this.patronymic = patronymic;
    }

    public Integer getStatus () {
        return status;
    }

    public void setStatus (Integer status) {
        this.status = status;
    }

    public String getAdditionalInfo () {
        return additionalInfo;
    }

    public void setAdditionalInfo (String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Date getApplyingDate () {
        return applyingDate;
    }

    public void setApplyingDate (Date applyingDate) {
        this.applyingDate = applyingDate;
    }

    public long getIdSss () {
        return idSss;
    }

    public void setIdSss (long idSss) {
        this.idSss = idSss;
    }
}
