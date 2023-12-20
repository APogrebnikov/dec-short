package org.edec.efficiency.model.dao;

import java.util.Date;

/**
 * @author Max Dimukhametov
 */
public class GroupStudentModel {
    private Boolean groupLeader;

    private Date dateCreated;
    private Date dateConfirm;
    private Date dateCompleted;

    private Float attend;
    private Float eokActivity;
    private Float performance;

    private Integer statusEfficiency;

    private Long idEfficiencyStudent;
    private Long idEmp;
    private Long idLGS;
    private Long idSSS;

    private String comment;
    private String family;
    private String groupname;
    private String name;
    private String patronymic;

    public GroupStudentModel () {
    }

    public Boolean getGroupLeader () {
        return groupLeader;
    }

    public void setGroupLeader (Boolean groupLeader) {
        this.groupLeader = groupLeader;
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

    public Integer getStatusEfficiency () {
        return statusEfficiency;
    }

    public void setStatusEfficiency (Integer statusEfficiency) {
        this.statusEfficiency = statusEfficiency;
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

    public Long getIdLGS () {
        return idLGS;
    }

    public void setIdLGS (Long idLGS) {
        this.idLGS = idLGS;
    }

    public Long getIdSSS () {
        return idSSS;
    }

    public void setIdSSS (Long idSSS) {
        this.idSSS = idSSS;
    }

    public String getFamily () {
        return family;
    }

    public void setFamily (String family) {
        this.family = family;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
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

    public String getComment () {
        return comment;
    }

    public void setComment (String comment) {
        this.comment = comment;
    }
}
