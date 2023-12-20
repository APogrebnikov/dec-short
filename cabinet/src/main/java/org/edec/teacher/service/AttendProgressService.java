package org.edec.teacher.service;

import org.edec.teacher.model.attendanceProgress.StudentModel;

import java.util.List;


public interface AttendProgressService {
    List<StudentModel> getStudentsForGroup (Long idLGSS);
}
