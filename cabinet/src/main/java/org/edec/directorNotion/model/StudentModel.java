package org.edec.directorNotion.model;

public class StudentModel {

    private String family;
    private String groupname;
    private String name;
    private String patronymic;
    private String recordbook;
    private String directionNumber;
    private String directionName;

    private Integer formOfStudy;
    private Integer course;

    public StudentModel () {
    }

    public Integer getCourse () {
        return course;
    }

    public void setCourse (Integer course) {
        this.course = course;
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

    public String getRecordbook () {
        return recordbook;
    }

    public void setRecordbook (String recordbook) {
        this.recordbook = recordbook;
    }

    public String getFio () {
        return this.family + " " + this.name + " " + this.patronymic;
    }

    public String getDirectionNumber () {
        return directionNumber;
    }

    public void setDirectionNumber (String directionNumber) {
        this.directionNumber = directionNumber;
    }

    public String getDirectionName () {
        return directionName;
    }

    public void setDirectionName (String directionName) {
        this.directionName = directionName;
    }

    public Integer getFormOfStudy () {
        return formOfStudy;
    }

    public void setFormOfStudy (Integer formOfStudy) {
        this.formOfStudy = formOfStudy;
    }
}
