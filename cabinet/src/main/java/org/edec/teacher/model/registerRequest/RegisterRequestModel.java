package org.edec.teacher.model.registerRequest;

import java.util.Date;

public class RegisterRequestModel {
    private Long idRegisterRequest;
    private Long idHumanface;
    private Long idLgss;

    private StudentModel student;

    private Integer status;
    private Integer foc;

    private String additionalInfo;
    private Date applyingDate;
    private Date answeringDate;

    public Long getIdRegisterRequest () {
        return idRegisterRequest;
    }

    public void setIdRegisterRequest (Long idRegisterRequest) {
        this.idRegisterRequest = idRegisterRequest;
    }

    public Long getIdHumanface () {
        return idHumanface;
    }

    public void setIdHumanface (Long idHumanface) {
        this.idHumanface = idHumanface;
    }

    public Long getIdLgss () {
        return idLgss;
    }

    public void setIdLgss (Long idLgss) {
        this.idLgss = idLgss;
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

    public Date getAnsweringDate () {
        return answeringDate;
    }

    public void setAnsweringDate (Date answeringDate) {
        this.answeringDate = answeringDate;
    }

    public StudentModel getStudent () {
        return student;
    }

    public void setStudent (StudentModel student) {
        this.student = student;
    }

    public Integer getFoc () {
        return foc;
    }

    public void setFoc (Integer foc) {
        this.foc = foc;
    }
}
