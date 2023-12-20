package org.edec.teacher.model.attendanceProgress;

import java.util.ArrayList;
import java.util.List;


public class StudentModel {
    private Integer attendancecount = 0;
    private Integer progress;

    private Long idSSS;

    private String fio;

    private List<AttendanceModel> attendances = new ArrayList<>();

    public StudentModel () {
    }

    public Integer getProgress () {
        return progress;
    }

    public void setProgress (Integer progress) {
        this.progress = progress;
    }

    public Integer getAttendancecount () {
        return attendancecount;
    }

    public void setAttendancecount (Integer attendancecount) {
        this.attendancecount = attendancecount;
    }

    public Long getIdSSS () {
        return idSSS;
    }

    public void setIdSSS (Long idSSS) {
        this.idSSS = idSSS;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public List<AttendanceModel> getAttendances () {
        return attendances;
    }

    public void setAttendances (List<AttendanceModel> attendances) {
        this.attendances = attendances;
    }
}
