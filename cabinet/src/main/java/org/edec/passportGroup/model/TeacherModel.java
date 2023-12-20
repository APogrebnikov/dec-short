package org.edec.passportGroup.model;

import java.util.List;

/**
 * Created by ilyabaikalow on 20.11.17.
 */
public class TeacherModel {
    private Long idLesg;
    private Long idTeacher;
    private String fullName;
    private String InstTitle;
    private List<String> listDepTitles;

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

    public String getInstTitle () {
        return InstTitle;
    }

    public void setInstTitle (String instTitle) {
        InstTitle = instTitle;
    }

    public List<String> getListDepTitles () {
        return listDepTitles;
    }

    public void setListDepTitles (List<String> listDepTitles) {
        this.listDepTitles = listDepTitles;
    }
}
