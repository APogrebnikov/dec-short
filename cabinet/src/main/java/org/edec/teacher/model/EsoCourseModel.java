package org.edec.teacher.model;


public class EsoCourseModel {
    private Boolean completed = false;

    private Integer idCategory;

    private Long idEsoCourse;

    private String fullname;
    private String shortname;

    private Boolean isSelected = false;

    public EsoCourseModel () {
    }

    public Boolean getCompleted () {
        return completed;
    }

    public void setCompleted (Boolean completed) {
        this.completed = completed;
    }

    public Integer getIdCategory () {
        return idCategory;
    }

    public void setIdCategory (Integer idCategory) {
        this.idCategory = idCategory;
    }

    public Long getIdEsoCourse () {
        return idEsoCourse;
    }

    public void setIdEsoCourse (Long idEsoCourse) {
        this.idEsoCourse = idEsoCourse;
    }

    public String getFullname () {
        return fullname;
    }

    public void setFullname (String fullname) {
        this.fullname = fullname;
    }

    public String getShortname () {
        return shortname;
    }

    public void setShortname (String shortname) {
        this.shortname = shortname;
    }

    public Boolean getChecked () {
        return isSelected;
    }

    public void setChecked (Boolean checked) {
        isSelected = checked;
    }
}
