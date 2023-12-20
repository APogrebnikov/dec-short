package org.edec.teacher.model.attendanceProgress.dao;

import java.util.Date;


public class AttendanceEsoModel {
    private Boolean attend;
    private Date visitdate;
    private Long idSSS;

    public AttendanceEsoModel () {
    }

    public Boolean getAttend () {
        return attend;
    }

    public void setAttend (Boolean attend) {
        this.attend = attend;
    }

    public Date getVisitdate () {
        return visitdate;
    }

    public void setVisitdate (Date visitdate) {
        this.visitdate = visitdate;
    }

    public Long getIdSSS () {
        return idSSS;
    }

    public void setIdSSS (Long idSSS) {
        this.idSSS = idSSS;
    }
}
