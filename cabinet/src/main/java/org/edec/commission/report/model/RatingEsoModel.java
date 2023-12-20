package org.edec.commission.report.model;


public class RatingEsoModel {
    private Boolean exam;
    private Boolean pass;
    private Boolean cp;
    private Boolean cw;
    private Boolean practic;

    private Integer examrating;
    private Integer passrating;
    private Integer cprating;
    private Integer cwrating;
    private Integer practicrating;

    private String semester;
    private String subjectname;

    private Integer type;

    public RatingEsoModel () {
    }

    public Boolean getExam () {
        return exam;
    }

    public void setExam (Boolean exam) {
        this.exam = exam;
    }

    public Boolean getPass () {
        return pass;
    }

    public void setPass (Boolean pass) {
        this.pass = pass;
    }

    public Boolean getCp () {
        return cp;
    }

    public void setCp (Boolean cp) {
        this.cp = cp;
    }

    public Boolean getCw () {
        return cw;
    }

    public void setCw (Boolean cw) {
        this.cw = cw;
    }

    public Boolean getPractic () {
        return practic;
    }

    public void setPractic (Boolean practic) {
        this.practic = practic;
    }

    public Integer getExamrating () {
        return examrating;
    }

    public void setExamrating (Integer examrating) {
        this.examrating = examrating;
    }

    public Integer getPassrating () {
        return passrating;
    }

    public void setPassrating (Integer passrating) {
        this.passrating = passrating;
    }

    public Integer getCprating () {
        return cprating;
    }

    public void setCprating (Integer cprating) {
        this.cprating = cprating;
    }

    public Integer getCwrating () {
        return cwrating;
    }

    public void setCwrating (Integer cwrating) {
        this.cwrating = cwrating;
    }

    public Integer getPracticrating () {
        return practicrating;
    }

    public void setPracticrating (Integer practicrating) {
        this.practicrating = practicrating;
    }

    public String getSemester () {
        return semester;
    }

    public void setSemester (String semester) {
        this.semester = semester;
    }

    public String getSubjectname () {
        return subjectname;
    }

    public void setSubjectname (String subjectname) {
        this.subjectname = subjectname;
    }

    public Integer getType () {
        return type;
    }

    public void setType (Integer type) {
        this.type = type;
    }
}
