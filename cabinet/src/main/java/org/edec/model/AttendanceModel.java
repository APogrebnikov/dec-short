package org.edec.model;

import java.util.Date;


public class AttendanceModel {
    private Boolean lesson;

    private Date visitDate;

    private Integer attend;
    private Integer pos;

    private Long idAttendance;
    private Long idSSS;
    private Long idLGSS;

    private String subjectname;

    public AttendanceModel () {
    }

    public AttendanceModel (AttendanceModel attendance) {
        this.idLGSS = attendance.getIdLGSS();
        this.subjectname = attendance.getSubjectname();
        this.pos = attendance.getPos();
        this.visitDate = attendance.getVisitDate();
        this.lesson = attendance.getLesson();
    }

    public Boolean getLesson () {
        return lesson;
    }

    public void setLesson (Boolean lesson) {
        this.lesson = lesson;
    }

    public Date getVisitDate () {
        return visitDate;
    }

    public void setVisitDate (Date visitDate) {
        this.visitDate = visitDate;
    }

    public Integer getAttend () {
        return attend;
    }

    public void setAttend (Integer attend) {
        this.attend = attend;
    }

    public Integer getPos () {
        return pos;
    }

    public void setPos (Integer pos) {
        this.pos = pos;
    }

    public Long getIdAttendance () {
        return idAttendance;
    }

    public void setIdAttendance (Long idAttendance) {
        this.idAttendance = idAttendance;
    }

    public Long getIdSSS () {
        return idSSS;
    }

    public void setIdSSS (Long idSSS) {
        this.idSSS = idSSS;
    }

    public Long getIdLGSS () {
        return idLGSS;
    }

    public void setIdLGSS (Long idLGSS) {
        this.idLGSS = idLGSS;
    }

    public String getSubjectname () {
        return subjectname;
    }

    public void setSubjectname (String subjectname) {
        this.subjectname = subjectname;
    }
}
