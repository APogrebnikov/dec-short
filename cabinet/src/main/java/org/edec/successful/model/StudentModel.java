package org.edec.successful.model;

import java.util.ArrayList;
import java.util.List;


public class StudentModel {
    private List<SubjectModel> subjects = new ArrayList<>();
    private String fio;
    private Long idStudent;
    private Long idHumanface;
    private Long idStudentcard;

    public List<SubjectModel> getSubjects () {
        return subjects;
    }

    public void setSubjects (List<SubjectModel> subjects) {
        this.subjects = subjects;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public Long getIdStudent () {
        return idStudent;
    }

    public void setIdStudent (Long idStudent) {
        this.idStudent = idStudent;
    }

    public Long getIdHumanface () {
        return idHumanface;
    }

    public void setIdHumanface (Long idHumanface) {
        this.idHumanface = idHumanface;
    }

    public Long getIdStudentcard () {
        return idStudentcard;
    }

    public void setIdStudentcard (Long idStudentcard) {
        this.idStudentcard = idStudentcard;
    }
}
