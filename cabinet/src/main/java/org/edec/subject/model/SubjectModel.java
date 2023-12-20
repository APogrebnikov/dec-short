package org.edec.subject.model;

import org.edec.admin.model.EmployeeModel;
import org.edec.utility.constants.FormOfControlConst;

import java.util.ArrayList;
import java.util.List;

public class SubjectModel {
    private Long idSubject;
    private Long idLgss;
    private Long idDg;
    private Long idSem;
    private String subjectName;
    private String groupName;
    private String semesterStr;
    private List<TeacherModel> teachers = new ArrayList<>();
    private Integer season, formofstudy;

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

    public String getSemesterStr () {
        return semesterStr;
    }

    public void setSemesterStr (String semesterStr) {
        this.semesterStr = semesterStr;
    }

    public Integer getSeason () {
        return season;
    }

    public void setSeason (Integer season) {
        this.season = season;
    }

    public Integer getFormofstudy () {
        return formofstudy;
    }

    public void setFormofstudy (Integer formofstudy) {
        this.formofstudy = formofstudy;
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

    public List<TeacherModel> getTeachers () {
        return teachers;
    }

    public void setTeachers (List<TeacherModel> teachers) {
        this.teachers = teachers;
    }
}

