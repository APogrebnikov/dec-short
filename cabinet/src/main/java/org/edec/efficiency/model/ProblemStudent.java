package org.edec.efficiency.model;

import org.edec.model.StudentModel;

import java.util.Date;


public class ProblemStudent extends StudentModel {
    private Boolean isGroupLeader;

    private Date dateCreated;
    private Date dateConfirm;
    private Date dateCompleted;

    private Float attend;
    private Float eokActivity;
    private Float performance;

    private Integer statusEfficiency;

    private Long idEfficiencyStudent;
    private Long idEmp;

    private String comment;

    private StatusEfficiency status;

    public ProblemStudent () {
    }

    public Boolean getGroupLeader () {
        return isGroupLeader;
    }

    public void setGroupLeader (Boolean groupLeader) {
        isGroupLeader = groupLeader;
    }

    public Date getDateCreated () {
        return dateCreated;
    }

    public void setDateCreated (Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateConfirm () {
        return dateConfirm;
    }

    public void setDateConfirm (Date dateConfirm) {
        this.dateConfirm = dateConfirm;
    }

    public Date getDateCompleted () {
        return dateCompleted;
    }

    public void setDateCompleted (Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public Float getAttend () {
        return attend;
    }

    public void setAttend (Float attend) {
        this.attend = attend;
    }

    public Float getEokActivity () {
        return eokActivity;
    }

    public void setEokActivity (Float eokActivity) {
        this.eokActivity = eokActivity;
    }

    public Float getPerformance () {
        return performance;
    }

    public void setPerformance (Float performance) {
        this.performance = performance;
    }

    public StatusEfficiency getStatus () {
        return status != null ? status : StatusEfficiency.getStatusEfficiencyByInt(statusEfficiency);
    }

    public void setStatus (StatusEfficiency status) {
        this.status = status;
    }

    public Long getIdEfficiencyStudent () {
        return idEfficiencyStudent;
    }

    public void setIdEfficiencyStudent (Long idEfficiencyStudent) {
        this.idEfficiencyStudent = idEfficiencyStudent;
    }

    public Long getIdEmp () {
        return idEmp;
    }

    public void setIdEmp (Long idEmp) {
        this.idEmp = idEmp;
    }

    public Integer getStatusEfficiency () {
        return statusEfficiency;
    }

    public void setStatusEfficiency (Integer statusEfficiency) {
        this.statusEfficiency = statusEfficiency;
    }

    public String getComment () {
        return comment;
    }

    public void setComment (String comment) {
        this.comment = comment;
    }
}
