package org.edec.synchroMine.service;

import org.edec.main.model.DepartmentModel;
import org.edec.model.SemesterModel;
import org.edec.synchroMine.model.dao.SubjectGroupMineModel;
import org.edec.synchroMine.model.dao.SubjectGroupModel;
import org.edec.synchroMine.model.dao.WorkloadModel;
import org.edec.synchroMine.model.eso.GroupMineModel;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface GroupSubjectService {
    List<DepartmentModel> getDepartments();

    List<SemesterModel> getSemesters(Long idInst, Integer formOfStudy);

    List<SubjectGroupMineModel> getSubjectMineModel(Long idInstMine, Integer semester, Integer course, String groupname,
                                                    Set<Long> listSubject);

    List<SubjectGroupMineModel> getRegisterSubjectByGroupInCourse(Long idInstMine, Integer course, Integer semester, String groupname);

    List<SubjectGroupModel> getSubjectGroupESO(Long idLGS);

    List<SubjectGroupModel> getSubjectGroupMine(String groupname, Long idInstMine, Integer semester, Integer course, Date dateBeginSchoolYear);

    List<WorkloadModel> getWorkloadByGroup(Long idInstMine, Integer course, String groupname, Set<Long> workloads);

    List<String> getRegisterNumberByLGSandSubj(Long idLGS, Long idSubj);

    SubjectGroupModel getSubjectGroupByMineModel(SubjectGroupMineModel mineModel, Date dateBeginSchoolYear);

    Long searchIdEmpByFio(String family, String name, String patronymic);

    void fillGroupsSubjects(List<SubjectGroupModel> subjectsESO, List<SubjectGroupModel> subjectsMine, List<DepartmentModel> departments);

    void compareCurriculumSubjectsAndRegisterSubjects(List<SubjectGroupMineModel> curriculumSubjects, List<SubjectGroupMineModel> registerSubjects);

    boolean updateGroup(Long idLGS, Integer idGroupMine);

    boolean updateChair(Long idCurriculum, Integer idChairMine);

    boolean updateSubject(SubjectGroupModel subjectGroupDec, SubjectGroupModel subjectGroupMine);

    boolean updateAudSubject(SubjectGroupModel subjectGroupDec, SubjectGroupModel subjectGroupMine);

    boolean updateSubjectFacultative(Long idSubject, Boolean facultative);

    boolean updateSubjectCode(Long idSubject, String blocks);

    boolean updateSubjectPracticeType(Long idSubject, String practiceType, String practiceTypeDative);

    boolean updateLGSPracticeDate(Long idLGS, Date dateOfBeginPractice, Date dateOfEndPractice);

    boolean createSubjectSRforLGS(Long idLGS, SubjectGroupModel subjectModelMine);

    void createSubjects(List<SubjectGroupModel> subjectsMine, Long idLGS);

    void createGroup(GroupMineModel data, Long idInst, SemesterModel semester);

    boolean deleteSubjectByLGSS(Long idLGSS);
}
