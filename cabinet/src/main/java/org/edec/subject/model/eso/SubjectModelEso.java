package org.edec.subject.model.eso;

import java.util.Date;

public class SubjectModelEso {
    private Long idSubject;
    private Long idLgss;
    private Long idDg;
    private Long idLesg;
    private String depTitle;
    private String subjectName;
    private String groupName;
    private Integer season;
    private Long idSem;
    private String fioTeacher;
    private Long idEmp;

    public Long getIdLgss () {
        return idLgss;
    }

    public void setIdLgss (Long idLgss) {
        this.idLgss = idLgss;
    }

    public Long getIdSubject () {
        return idSubject;
    }

    public void setIdSubject (Long idSubject) {
        this.idSubject = idSubject;
    }

    public String getSubjectName () {
        return subjectName;
    }

    public void setSubjectName (String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getSeason () {
        return season;
    }

    public void setSeason (Integer season) {
        this.season = season;
    }

    public Long getIdSem () {
        return idSem;
    }

    public void setIdSem (Long idSem) {
        this.idSem = idSem;
    }

    public Long getIdDg () {
        return idDg;
    }

    public void setIdDg (Long idDg) {
        this.idDg = idDg;
    }

    public String getGroupName () {
        return groupName;
    }

    public void setGroupName (String groupName) {
        this.groupName = groupName;
    }

    public String getFioTeacher () {
        return fioTeacher;
    }

    public void setFioTeacher (String fioTeacher) {
        this.fioTeacher = fioTeacher;
    }

    public Long getIdEmp () {
        return idEmp;
    }

    public void setIdEmp (Long idEmp) {
        this.idEmp = idEmp;
    }

    public String getDepTitle () {
        return depTitle;
    }

    public void setDepTitle (String depTitle) {
        this.depTitle = depTitle;
    }

    public Long getIdLesg () {
        return idLesg;
    }

    public void setIdLesg (Long idLesg) {
        this.idLesg = idLesg;
    }
}
