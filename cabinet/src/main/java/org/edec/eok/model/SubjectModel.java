package org.edec.eok.model;

import java.util.ArrayList;
import java.util.List;

public class SubjectModel {
    private Double hourscount;

    private Long idEsoCourse;
    private Long idLGSS;

    private String department;
    private String formOfControl;
    private String groupname;
    private String subjectname;

    private List<TeacherModel> teachers = new ArrayList<>();

    public SubjectModel () {
    }

    public Double getHourscount () {
        return hourscount;
    }

    public void setHourscount (Double hourscount) {
        this.hourscount = hourscount;
    }

    public Long getIdEsoCourse () {
        return idEsoCourse;
    }

    public void setIdEsoCourse (Long idEsoCourse) {
        this.idEsoCourse = idEsoCourse;
    }

    public Long getIdLGSS () {
        return idLGSS;
    }

    public void setIdLGSS (Long idLGSS) {
        this.idLGSS = idLGSS;
    }

    public String getDepartment () {
        return department;
    }

    public void setDepartment (String department) {
        this.department = department;
    }

    public String getFormOfControl () {
        return formOfControl;
    }

    public void setFormOfControl (String formOfControl) {
        this.formOfControl = formOfControl;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public String getSubjectname () {
        return subjectname;
    }

    public void setSubjectname (String subjectname) {
        this.subjectname = subjectname;
    }

    public List<TeacherModel> getTeachers () {
        return teachers;
    }

    public void setTeachers (List<TeacherModel> teachers) {
        this.teachers = teachers;
    }
}
