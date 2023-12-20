package org.edec.student.journalOfAttendance.service.impl;

import org.edec.model.AttendanceModel;
import org.edec.model.GroupSemesterModel;
import org.edec.schedule.model.dao.GroupSubjectLesson;
import org.edec.student.journalOfAttendance.manager.JournalOfAttendanceManager;
import org.edec.student.journalOfAttendance.model.MonthAttendSubjectsModel;
import org.edec.student.journalOfAttendance.model.StudentAttendanceModel;
import org.edec.student.journalOfAttendance.service.JournalOfAttendanceService;

import java.util.*;


public class JournalOfAttendanceServiceImpl implements JournalOfAttendanceService {
    private JournalOfAttendanceManager journalOfAttendanceManager = new JournalOfAttendanceManager();

    @Override
    public GroupSemesterModel getGroupSemesterByHum (Long idHum) {
        return journalOfAttendanceManager.getGroupSemester(idHum);
    }

    @Override
    public GroupSemesterModel getGroupSemesterByHum (Long idHum, Long idSem) {
        return journalOfAttendanceManager.getGroupSemester(idHum, idSem);
    }

    @Override
    public List<AttendanceModel> getSubjectAttendanceByLGSandDate (Long idLGS, Date visitDate) {
        List<AttendanceModel> subjectsAttendance = journalOfAttendanceManager.getSubjectAttendanceByLGSAndDate(idLGS, visitDate);
        return subjectsAttendance;
    }

    @Override
    public List<MonthAttendSubjectsModel> getMonthAttendModel (Long idLGSS, Date dateBegin, Date dateEnd) {
        return journalOfAttendanceManager.getMonthAttendModel(idLGSS, dateBegin, dateEnd);
    }

    @Override
    public List<StudentAttendanceModel> getStudentByGroup (Long idLGS) {
        return journalOfAttendanceManager.getStudentByLGS(idLGS);
    }

    @Override
    public void fillStudentByAttendance (List<StudentAttendanceModel> students, List<AttendanceModel> subjectsAttendance, Long idLGS,
                                         Date visitDate) {
        List<AttendanceModel> attendancesStudent = journalOfAttendanceManager.getAttendanceByLGSAndDate(idLGS, visitDate);
        for (StudentAttendanceModel student : students) {
            student.getAttendances().clear();
            List<AttendanceModel> tmpList = new ArrayList<>();
            for (AttendanceModel attendanceModel : subjectsAttendance) {
                AttendanceModel newAttendance = new AttendanceModel(attendanceModel);
                newAttendance.setIdSSS(student.getIdSSS());
                tmpList.add(newAttendance);
            }
            student.getAttendances().addAll(tmpList);
            for (AttendanceModel attendanceStudent : student.getAttendances()) {
                for (AttendanceModel attendance : attendancesStudent) {
                    if (attendance.getIdLGSS().equals(attendanceStudent.getIdLGSS()) &&
                        attendance.getPos().equals(attendanceStudent.getPos()) && student.getIdSSS().equals(attendance.getIdSSS())) {
                        attendanceStudent.setAttend(attendance.getAttend());
                        attendanceStudent.setIdAttendance(attendance.getIdAttendance());
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void fillMonthAttendance (List<MonthAttendSubjectsModel> monthAttendance, Integer firstWeekSem,
                                     List<GroupSubjectLesson> lessons) {
        Calendar calendar = Calendar.getInstance();
        for (MonthAttendSubjectsModel monthAttend : monthAttendance) {
            calendar.setTime(monthAttend.getDay());
            for (GroupSubjectLesson lesson : lessons) {
                if (calendar.get(Calendar.DAY_OF_WEEK) == (lesson.getDicDayLesson().getIdDicDayLesson().intValue() + 1)) {
                    if ((firstWeekSem == 1 && ((lesson.getWeek() == 1 && (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 1)) ||
                                               (lesson.getWeek() == 2 && (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 0)))) ||
                        (firstWeekSem == 2 && ((lesson.getWeek() == 1 && (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 0)) ||
                                               (lesson.getWeek() == 2 && (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 1))))) {
                        monthAttend.getLessons().add(lesson);
                    }
                }
            }
        }
    }

    @Override
    public void createAttendance (AttendanceModel attendance) {
        journalOfAttendanceManager.createAttendance(attendance);
    }

    @Override
    public void updateAttendance (AttendanceModel attendance) {
        journalOfAttendanceManager.updateAttendance(attendance);
    }

    @Override
    public void deleteAttendanceByDate (MonthAttendSubjectsModel selectedDay, Long idLGS) {
        journalOfAttendanceManager.deleteAttendanceByDate(selectedDay.getDay(), idLGS);
    }
}
