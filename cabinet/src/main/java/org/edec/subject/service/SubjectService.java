package org.edec.subject.service;

import org.edec.subject.model.SubjectModel;
import org.edec.subject.model.TeacherModel;
import org.edec.utility.component.model.SemesterModel;

import java.util.List;

public interface SubjectService {

    List<SubjectModel> getSubjectsBySem (long idDepartment, long idSem);

    List<TeacherModel> getTeachers (String teacher, long idDep);

    boolean addTeacherToSubject (Long idTeacher, Long idLgss);

    boolean removeTeacherFromSubject (Long idLesg);

    List<SubjectModel> filterSubjects (List<SubjectModel> subjects, String subjectName, String groupName);

    List<SemesterModel> getSemester (Long idInst, Integer fos, Integer season);

    boolean changeTeacherVisibility (Long idLed, boolean isHide);

    List<TeacherModel> filterTeachers (List<TeacherModel> teachers, String teacherFio, Boolean showHiddenTeachers);
}
