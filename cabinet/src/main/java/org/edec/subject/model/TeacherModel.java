package org.edec.subject.model;

import java.util.ArrayList;
import java.util.List;

public class TeacherModel {
    private Long idLesg;
    private Long idTeacher;
    private Long idLed;
    private String fullName;
    private List<String> depTitles = new ArrayList<>();
    private Boolean isHidden;

    public Long getIdLesg () {
        return idLesg;
    }

    public void setIdLesg (Long idLesg) {
        this.idLesg = idLesg;
    }

    public Long getIdTeacher () {
        return idTeacher;
    }

    public void setIdTeacher (Long idTeacher) {
        this.idTeacher = idTeacher;
    }

    public String getFullName () {
        return fullName;
    }

    public void setFullName (String fullName) {
        this.fullName = fullName;
    }

    public List<String> getDepTitles () {
        return depTitles;
    }

    public void setDepTitles (List<String> depTitles) {
        this.depTitles = depTitles;
    }

    public Boolean getHidden () {
        return isHidden;
    }

    public void setHidden (Boolean hidden) {
        isHidden = hidden;
    }

    public Long getIdLed () {
        return idLed;
    }

    public void setIdLed (Long idLed) {
        this.idLed = idLed;
    }
}
