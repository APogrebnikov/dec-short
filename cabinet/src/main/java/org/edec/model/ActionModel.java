package org.edec.model;

import java.util.Date;


public class ActionModel {
    private Long idActionModel;
    private Long idStudentcard;
    private Long idLinkOrderStudentStatus;
    private Long idDicAction;
    private Long idDicGroupFrom;
    private Long idDicGroupTo;
    private Long idInstituteFrom;
    private Long idInstituteTo;
    private Long idSemester;
    private Long idUser;
    private Date dateAction;
    private Date dateStart;
    private Date dateFinish;
    private String orderNumber;
    private Integer seq;
    private Long idDicScholarship;

    public Long getIdStudentcard () {
        return idStudentcard;
    }

    public void setIdStudentcard (Long idStudentcard) {
        this.idStudentcard = idStudentcard;
    }

    public Long getIdLinkOrderStudentStatus () {
        return idLinkOrderStudentStatus;
    }

    public void setIdLinkOrderStudentStatus (Long idLinkOrderStudentStatus) {
        this.idLinkOrderStudentStatus = idLinkOrderStudentStatus;
    }

    public Long getIdDicAction () {
        return idDicAction;
    }

    public void setIdDicAction (Long idDicAction) {
        this.idDicAction = idDicAction;
    }

    public Long getIdDicGroupFrom () {
        return idDicGroupFrom;
    }

    public void setIdDicGroupFrom (Long idDicGroupFrom) {
        this.idDicGroupFrom = idDicGroupFrom;
    }

    public Long getIdDicGroupTo () {
        return idDicGroupTo;
    }

    public void setIdDicGroupTo (Long idDicGroupTo) {
        this.idDicGroupTo = idDicGroupTo;
    }

    public Long getIdInstituteFrom () {
        return idInstituteFrom;
    }

    public void setIdInstituteFrom (Long idInstituteFrom) {
        this.idInstituteFrom = idInstituteFrom;
    }

    public Long getIdInstituteTo () {
        return idInstituteTo;
    }

    public void setIdInstituteTo (Long idInstituteTo) {
        this.idInstituteTo = idInstituteTo;
    }

    public Long getIdSemester () {
        return idSemester;
    }

    public void setIdSemester (Long idSemester) {
        this.idSemester = idSemester;
    }

    public Long getIdUser () {
        return idUser;
    }

    public void setIdUser (Long idUser) {
        this.idUser = idUser;
    }

    public Date getDateAction () {
        return dateAction;
    }

    public void setDateAction (Date dateAction) {
        this.dateAction = dateAction;
    }

    public Date getDateStart () {
        return dateStart;
    }

    public void setDateStart (Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateFinish () {
        return dateFinish;
    }

    public void setDateFinish (Date dateFinish) {
        this.dateFinish = dateFinish;
    }

    public String getOrderNumber () {
        return orderNumber;
    }

    public void setOrderNumber (String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getSeq () {
        return seq;
    }

    public void setSeq (Integer seq) {
        this.seq = seq;
    }

    public Long getIdDicScholarship () {
        return idDicScholarship;
    }

    public void setIdDicScholarship (Long idDicScholarship) {
        this.idDicScholarship = idDicScholarship;
    }
}
