package org.edec.student.journalOfAttendance.model;

import org.edec.model.AttendanceModel;
import org.edec.model.StudentModel;

import java.util.ArrayList;
import java.util.List;


public class StudentAttendanceModel extends StudentModel {
    private List<AttendanceModel> attendances = new ArrayList<>();

    public StudentAttendanceModel () {
    }

    public List<AttendanceModel> getAttendances () {
        return attendances;
    }

    public void setAttendances (List<AttendanceModel> attendances) {
        this.attendances = attendances;
    }
}
