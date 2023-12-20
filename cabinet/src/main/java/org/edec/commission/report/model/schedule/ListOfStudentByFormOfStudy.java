package org.edec.commission.report.model.schedule;

public class ListOfStudentByFormOfStudy {
    private Integer course;

    private String fio;
    private String formOfStudy;
    private String groupname;
    private String recordbook;
    private String semester;

    public ListOfStudentByFormOfStudy () {
    }

    public Integer getCourse () {
        return course;
    }

    public void setCourse (Integer course) {
        this.course = course;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getFormOfStudy () {
        return formOfStudy;
    }

    public void setFormOfStudy (String formOfStudy) {
        this.formOfStudy = formOfStudy;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public String getRecordbook () {
        return recordbook;
    }

    public void setRecordbook (String recordbook) {
        this.recordbook = recordbook;
    }

    public String getSemester () {
        return semester;
    }

    public void setSemester (String semester) {
        this.semester = semester;
    }
}
