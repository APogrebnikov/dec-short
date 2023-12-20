package org.edec.contingentMovement.model;

/**
 * Created by dmmax
 */
public class GroupModel {
    private int semester;

    private Long idDG;
    private Long idLgs;
    private Long otherdbid;

    private String speciality;
    private String groupname;
    private String shortDepartment;
    private String department;
    private String specname;

    private Integer nullcount;

    public Integer getNullcount () {
        return nullcount;
    }

    public void setNullcount (Integer nullcount) {
        this.nullcount = nullcount;
    }

    public GroupModel () {
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public Long getIdDG () {
        return idDG;
    }

    public void setIdDG (Long idDG) {
        this.idDG = idDG;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public Long getIdLgs () {
        return idLgs;
    }

    public void setIdLgs (Long idLgs) {
        this.idLgs = idLgs;
    }

    public Long getOtherdbid () {
        return otherdbid;
    }

    public void setOtherdbid (Long otherdbid) {
        this.otherdbid = otherdbid;
    }

    public int getSemester () {
        return semester;
    }

    public void setSemester (int semester) {
        this.semester = semester;
    }

    public String getShortDepartment() {
        return shortDepartment;
    }

    public void setShortDepartment(String shortDepartment) {
        this.shortDepartment = shortDepartment;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSpecname() {
        return specname;
    }

    public void setSpecname(String specname) {
        this.specname = specname;
    }
}
