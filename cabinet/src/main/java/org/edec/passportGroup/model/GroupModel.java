package org.edec.passportGroup.model;

/**
 * Created by antonskripacev on 09.04.17.
 */
public class GroupModel {
    private Long idLgs;
    private Long idCurriculum;
    private Long idDicGroup;
    private String groupName;
    private Integer dateBegin;
    private Integer dateEnd;
    private Integer course;
    private Integer season;
    private Integer semesterNumber;
    private int qualification;

    public Long getIdLgs () {
        return idLgs;
    }

    public void setIdLgs (Long idLgs) {
        this.idLgs = idLgs;
    }

    public Integer getCourse () {
        return course;
    }

    public void setCourse (Integer course) {
        this.course = course;
    }

    public String getGroupName () {
        return groupName;
    }

    public void setGroupName (String groupName) {
        this.groupName = groupName;
    }

    public int getQualification () {
        return qualification;
    }

    public void setQualification (int qualification) {
        this.qualification = qualification;
    }

    public Integer getDateBegin () {
        return dateBegin;
    }

    public void setDateBegin (Integer dateBegin) {
        this.dateBegin = dateBegin;
    }

    public Integer getDateEnd () {
        return dateEnd;
    }

    public void setDateEnd (Integer dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Integer getSeason () {
        return season;
    }

    public void setSeason (Integer season) {
        this.season = season;
    }

    public Integer getSemesterNumber () {
        return semesterNumber;
    }

    public void setSemesterNumber (Integer semesterNumber) {
        this.semesterNumber = semesterNumber;
    }

    public Long getIdCurriculum () {
        return idCurriculum;
    }

    public void setIdCurriculum (Long idCurriculum) {
        this.idCurriculum = idCurriculum;
    }

    public Long getIdDicGroup () {
        return idDicGroup;
    }

    public void setIdDicGroup (Long idDicGroup) {
        this.idDicGroup = idDicGroup;
    }
}
