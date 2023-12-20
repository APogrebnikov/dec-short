package org.edec.reference.model;

import org.edec.utility.constants.ReferenceType;

import java.util.Date;

public class StudentModel {

    private Long idStudentcard;
    private Long idHumanface;
    private Long idSemester;

    private String fio;
    private String groupName;

    private Boolean isInvalid;
    private Boolean isOrphan;
    private Boolean isIndigent;
    private Boolean isVeteran;

    private Integer invalidType;

    private Date dateOfBirth;

    public Long getIdStudentcard () {
        return idStudentcard;
    }

    public void setIdStudentcard (Long idStudentcard) {
        this.idStudentcard = idStudentcard;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getGroupName () {
        return groupName;
    }

    public void setGroupName (String groupName) {
        this.groupName = groupName;
    }

    public Boolean getInvalid () {
        return isInvalid;
    }

    public void setInvalid (Boolean invalid) {
        isInvalid = invalid;
    }

    public Boolean getOrphan () {
        return isOrphan;
    }

    public void setOrphan (Boolean orphan) {
        isOrphan = orphan;
    }

    public Boolean getIndigent () {
        return isIndigent;
    }

    public void setIndigent (Boolean indigent) {
        isIndigent = indigent;
    }

    public Integer getInvalidType () {
        return invalidType;
    }

    public void setInvalidType (Integer invalidType) {
        this.invalidType = invalidType;
    }

    public Boolean getVeteran() {
        return isVeteran;
    }

    public void setVeteran(Boolean veteran) {
        isVeteran = veteran;
    }

    public Date getDateOfBirth () {
        return dateOfBirth;
    }

    public void setDateOfBirth (Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Long getIdHumanface () {
        return idHumanface;
    }

    public void setIdHumanface (Long idHumanface) {
        this.idHumanface = idHumanface;
    }

    public Long getIdSemester () {
        return idSemester;
    }

    public void setIdSemester (Long idSemester) {
        this.idSemester = idSemester;
    }

}
