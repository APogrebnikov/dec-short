package org.edec.subject.model.eso;

import java.util.List;

public class TeacherModelEso {
    private Long idLesg;
    private Long idTeacher;
    private Long idLed;
    private String fullName;
    private String depTitle;
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

    public String getDepTitle () {
        return depTitle;
    }

    public void setDepTitle (String depTitle) {
        this.depTitle = depTitle;
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
