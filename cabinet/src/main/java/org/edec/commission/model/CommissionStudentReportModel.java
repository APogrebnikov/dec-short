package org.edec.commission.model;

import java.util.Date;

public class CommissionStudentReportModel {
    private Boolean isGovernmentFinanced;

    private Date commissionDate;

    private String chair;
    private String classroom;
    private String fio;
    private String focStr;
    private String groupname;
    private String rating;
    private String semesterStr;
    private String subjectname;

    public CommissionStudentReportModel () {
    }

    public Boolean getGovernmentFinanced () {
        return isGovernmentFinanced;
    }

    public void setGovernmentFinanced (Boolean governmentFinanced) {
        isGovernmentFinanced = governmentFinanced;
    }

    public Date getCommissionDate () {
        return commissionDate;
    }

    public void setCommissionDate (Date commissionDate) {
        this.commissionDate = commissionDate;
    }

    public String getChair () {
        return chair;
    }

    public void setChair (String chair) {
        this.chair = chair;
    }

    public String getClassroom () {
        return classroom;
    }

    public void setClassroom (String classroom) {
        this.classroom = classroom;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getFocStr () {
        return focStr;
    }

    public void setFocStr (String focStr) {
        this.focStr = focStr;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public String getSemesterStr () {
        return semesterStr;
    }

    public void setSemesterStr (String semesterStr) {
        this.semesterStr = semesterStr;
    }

    public String getSubjectname () {
        return subjectname;
    }

    public void setSubjectname (String subjectname) {
        this.subjectname = subjectname;
    }

    public String getRating () {
        return rating;
    }

    public void setRating (String rating) {
        this.rating = rating;
    }
}
