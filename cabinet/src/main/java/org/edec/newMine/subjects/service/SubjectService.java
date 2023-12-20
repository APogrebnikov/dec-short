package org.edec.newMine.subjects.service;

import org.edec.main.model.DepartmentModel;
import org.edec.newMine.subjects.model.GroupsEsoModel;
import org.edec.newMine.subjects.model.SubjectMineModel;
import org.edec.newMine.subjects.model.SubjectWorkloadModel;
import org.edec.newMine.subjects.model.SubjectsModel;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface SubjectService {
    List<SubjectsModel> getSubjectsByIdLGS (Long idLgs);
    List<SubjectsModel> getSubjectGroupMine(String groupname, Long idInstMine, Integer semester, Integer course, Date dateBeginSchoolYear, Integer fos, Boolean shortForm);
    List<String> getRegisterNumberByLGSandSubj(Long idLGS, Long idSubj);
    boolean updateSubject(SubjectsModel subjectGroupDec, SubjectsModel subjectGroupMine);
    boolean deleteSubjectByLGSS(Long idLGSS);
    boolean createSubjectSRforLGS(Long idLGS, SubjectsModel subjectModelMine);
    List<GroupsEsoModel> getESOGroupsBySemester(Long idSem);

    void fillOtherSubjectsModelAndChair(List<SubjectsModel> subjectsESO, List<SubjectsModel> subjectsMine, List<DepartmentModel> departments);
    List<DepartmentModel> getDepartments();
    boolean updateSubjectCode(Long idSubject, String blocks);
    boolean updateAudSubject(SubjectsModel subjectGroupDec, SubjectsModel subjectGroupMine);

    void createSubjects(List<SubjectsModel> subjectsMine, Long idLGS);
    void linkTeachersForSubjects(List<SubjectsModel> subjectsMine, Long idLGS);
}
