package org.edec.teacher.service.impl;

import org.edec.teacher.manager.EntityManagerAttendProgress;
import org.edec.teacher.model.attendanceProgress.AttendanceModel;
import org.edec.teacher.model.attendanceProgress.StudentModel;
import org.edec.teacher.model.attendanceProgress.dao.AttendanceEsoModel;
import org.edec.teacher.service.AttendProgressService;

import java.util.*;


public class AttendProgressServiceImpl implements AttendProgressService {
    private EntityManagerAttendProgress emAttendProgress = new EntityManagerAttendProgress();

    @Override
    public List<StudentModel> getStudentsForGroup (Long idLGSS) {
        List<StudentModel> students = emAttendProgress.getStudentsByIdLGSS(idLGSS);
        List<AttendanceEsoModel> attendanceEsoModels = emAttendProgress.getModelStudentAttend(idLGSS);
        Set<Date> tmpDates = new HashSet<>();
        for (AttendanceEsoModel attendanceEsoModel : attendanceEsoModels) {
            tmpDates.add(attendanceEsoModel.getVisitdate());
        }

        List<Date> dates = new ArrayList<>(tmpDates);

        Collections.sort(dates, Date::compareTo);

        for (StudentModel student : students) {
            for (Date date : dates) {
                boolean addDate = true;
                for (AttendanceEsoModel attendanceEsoModel : attendanceEsoModels) {
                    if (attendanceEsoModel.getVisitdate().equals(date) && attendanceEsoModel.getIdSSS().equals(student.getIdSSS())) {
                        student.getAttendances().add(new AttendanceModel(date, attendanceEsoModel.getAttend()));
                        if (attendanceEsoModel.getAttend()) {
                            student.setAttendancecount(student.getAttendancecount() + 1);
                        }
                        addDate = false;
                        break;
                    }
                }
                if (addDate) {
                    student.getAttendances().add(new AttendanceModel(date, null));
                }
            }
        }

        return students;
    }
}
