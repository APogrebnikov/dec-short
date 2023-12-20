package org.edec.teacher.model;

import org.edec.teacher.model.commission.CommissionModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SemesterModel {
    private Boolean curSem;

    private Integer formofstudy;
    private Integer season;

    private String institute;
    private String semesterStr;

    private Long idInstitute;

    private Long idSemester;
    private Date dateOfBeginYear;
    private Date dateOfEndYear;

    private List<CommissionModel> commissions = new ArrayList<>();
    private List<SubjectModel> subjects = new ArrayList<>();

    public SemesterModel () {
    }

    public Long getIdInstitute () {
        return idInstitute;
    }

    public void setIdInstitute (Long idInstitute) {
        this.idInstitute = idInstitute;
    }

    public Boolean getCurSem () {
        return curSem;
    }

    public void setCurSem (Boolean curSem) {
        this.curSem = curSem;
    }

    public Integer getFormofstudy () {
        return formofstudy;
    }

    public void setFormofstudy (Integer formofstudy) {
        this.formofstudy = formofstudy;
    }

    public String getInstitute () {
        return institute;
    }

    public void setInstitute (String institute) {
        this.institute = institute;
    }

    public String getSemesterStr () {
        return semesterStr;
    }

    public void setSemesterStr (String semesterStr) {
        this.semesterStr = semesterStr;
    }

    public List<SubjectModel> getSubjects () {
        return subjects;
    }

    public void setSubjects (List<SubjectModel> subjects) {
        this.subjects = subjects;
    }

    public List<CommissionModel> getCommissions () {
        return commissions;
    }

    public void setCommissions (List<CommissionModel> commissions) {
        this.commissions = commissions;
    }

    public Integer getSeason () {
        return season;
    }

    public void setSeason (Integer season) {
        this.season = season;
    }

    public Date getDateOfBeginYear () {
        return dateOfBeginYear;
    }

    public void setDateOfBeginYear (Date dateOfBeginYear) {
        this.dateOfBeginYear = dateOfBeginYear;
    }

    public Date getDateOfEndYear () {
        return dateOfEndYear;
    }

    public void setDateOfEndYear (Date dateOfEndYear) {
        this.dateOfEndYear = dateOfEndYear;
    }

    public Long getIdSemester () {
        return idSemester;
    }

    public void setIdSemester (Long idSemester) {
        this.idSemester = idSemester;
    }
}
