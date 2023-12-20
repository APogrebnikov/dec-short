package org.edec.teacher.model.attendanceProgress;

import java.util.Date;


public class AttendanceModel {
    private Boolean attend;
    private Date visitdate;

    private StudentModel student;

    public AttendanceModel (Date visitdate, Boolean attend) {
        this.visitdate = visitdate;
        this.attend = attend;
    }

    public AttendanceModel () {

    }

    public Boolean getAttend () {
        return attend;
    }

    public void setAttend (Boolean attend) {
        this.attend = attend;
    }

    public StudentModel getStudent () {
        return student;
    }

    public void setStudent (StudentModel student) {
        this.student = student;
    }

    public Date getVisitdate () {
        return visitdate;
    }

    public void setVisitdate (Date visitdate) {
        this.visitdate = visitdate;
    }
}
