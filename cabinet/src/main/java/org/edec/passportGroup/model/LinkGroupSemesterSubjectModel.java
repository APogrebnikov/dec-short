package org.edec.passportGroup.model;

public class LinkGroupSemesterSubjectModel {
    private Long idLGS;
    private Long idSubject;
    private Integer printStatus;
    private Double hoursCP;
    private Double hoursCW;
    private Double hoursConrolDistance;
    private Double hoursConsult;
    private Double hoursControlSelfStudy;
    private Boolean formExam;
    private Integer lessonCount;

    public Long getIdLGS () {
        return idLGS;
    }

    public void setIdLGS (Long idLGS) {
        this.idLGS = idLGS;
    }

    public Long getIdSubject () {
        return idSubject;
    }

    public void setIdSubject (Long idSubject) {
        this.idSubject = idSubject;
    }

    public Integer getPrintStatus () {
        return printStatus;
    }

    public void setPrintStatus (Integer printStatus) {
        this.printStatus = printStatus;
    }

    public Double getHoursCP () {
        return hoursCP;
    }

    public void setHoursCP (Double hoursCP) {
        this.hoursCP = hoursCP;
    }

    public Double getHoursCW () {
        return hoursCW;
    }

    public void setHoursCW (Double hoursCW) {
        this.hoursCW = hoursCW;
    }

    public Double getHoursConrolDistance () {
        return hoursConrolDistance;
    }

    public void setHoursConrolDistance (Double hoursConrolDistance) {
        this.hoursConrolDistance = hoursConrolDistance;
    }

    public Double getHoursConsult () {
        return hoursConsult;
    }

    public void setHoursConsult (Double hoursConsult) {
        this.hoursConsult = hoursConsult;
    }

    public Double getHoursControlSelfStudy () {
        return hoursControlSelfStudy;
    }

    public void setHoursControlSelfStudy (Double hoursControlSelfStudy) {
        this.hoursControlSelfStudy = hoursControlSelfStudy;
    }

    public Integer getLessonCount () {
        return lessonCount;
    }

    public void setLessonCount (Integer lessonCount) {
        this.lessonCount = lessonCount;
    }

    public Boolean getFormExam () {
        return formExam;
    }

    public void setFormExam (Boolean formExam) {
        this.formExam = formExam;
    }
}
