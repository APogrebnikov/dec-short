package org.edec.student.curriculum.service;

import org.edec.student.curriculum.model.StudentsCurriculumModel;
import org.edec.student.curriculum.model.SubjectCurriculumModel;

import java.util.List;

public interface StudentsCurriculumService {
    Long getOtherIdStudentcard (Long idHumanface);
    Integer getCurSem (int fos, List<SubjectCurriculumModel> subjectsFromISIT);
    StudentsCurriculumModel getCurriculum(Long idOtherStudentcard);
    List<SubjectCurriculumModel> getSubjectsFromISIT(Long idOtherStudentcard);
    List<SubjectCurriculumModel> compareSubjectsList(List<SubjectCurriculumModel> subjecsFromMine, List<SubjectCurriculumModel> subjecsFromISIT);
}
