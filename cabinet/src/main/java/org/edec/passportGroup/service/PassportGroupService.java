package org.edec.passportGroup.service;

import org.edec.admin.model.EmployeeModel;
import org.edec.passportGroup.model.*;
import org.edec.register.model.RetakeModel;
import org.edec.utility.constants.FormOfControlConst;

import java.util.Date;
import java.util.List;

/**
 * Created by antonskripacev on 09.04.17.
 */
public interface PassportGroupService {

    /**
     * Возвращает модель списка семестров в соответствии с указанным параметрами.
     *
     * @param idInstitute    id института.
     * @param typeOfSemester тип семестра: 1 - осенний, 2 - весенний.
     * @param formOfStudy    форма обучения: 1 - очная, 2 - заочная.
     * @return semesterList - модель списка семетсров (тип: SemesterModel),
     */
    List<SemesterModel> getSemestersByParams (long idInstitute, int typeOfSemester, int formOfStudy);

    List<GroupModel> getGroupsByFilter (long idSemester, int course, String group, boolean isBachelor, boolean isMaster,
                                        boolean isEngineer, Long idChair);

    List<StudentModel> getStudentsByGroup (long idLgs);

    List<SubjectModel> getSubjectsByGroup (long idLgs);

    List<EmployeeModel> getEmployeesBySubject (long idLgss);

    String[] getRegistersBySr (Long idSr, FormOfControlConst foc);

    List<SubjectReportModel> getSubjectsReport (List<GroupModel> groupList, int formCtrl, int status, String searchStr);

    List<TeacherModel> getTeachers (String teacher, String inst, String dep);

    boolean addTeacherToSubject (Long idTeacher, Long idLgss);

    boolean removeTeacherFromSubject (Long idLesg);

    void setScholarshipInfoForListStudents (List<StudentModel> listStudents);

    boolean cancelScholarship (StudentModel studentModel, ScholarshipInfo scholarshipInfo, Date dateCancel, String orderNumber,
                               String userName, Long idHumanface);

    boolean setScholarship (StudentModel studentModel, ScholarshipInfo scholarshipInfo, Date dateSet, Date dateStart, Date dateFinish,
                            String orderNumber, String userName, Long idHumanface);

    boolean openRetake (SubjectModel subject, List<org.edec.register.model.StudentModel> studentsList, GroupModel group, Date dateOfBegin, Date dateOfEnd,
                        String userFio, int typeOfRetake);

    boolean changeRating (RetakeModel retakeModel, FormOfControlConst foc, int newRating, int oldRating, String userFio,
                          Long userHumanfaceId, String groupName, String subjectName, String studentName);

    void setStatisticForSubjects (List<StudentModel> students, List<SubjectModel> subjects);

    int getRetakeCount (long idSR);

    int countSR (long idLGSS);

    boolean deleteSubject (long idLGSS, boolean hasMarks, boolean hasMultipleFoc, FormOfControlConst foc);

    List<SubjectModel> getSubjectList ();

    List<DepartmentModel> getDepartmentList ();

    long createDicSubject (String subjectName);

    long createSubject (SubjectModel subject);

    boolean createLinkGroupSemesterSubject (LinkGroupSemesterSubjectModel lgss);

    boolean createSessionRating (SessionRatingModel sr);

    List<Long> getStudentSemesterStatusList (long idLGS);

    boolean updateDicSubject (long idDicSubject, String subjectName);

    boolean updateSubject (SubjectModel subject);

    boolean updateSessionRating (SessionRatingModel sr);

    List<SubjectReportModel> getSubjectListModel (List<SubjectReportModel> list);

    List<SubjectReportModel> filterSubjectReportList (List<SubjectReportModel> list, String subjectName, int foc, int attachmentStatus);

    boolean updateConsultDate (long idLGSS, Date consultDate);

    boolean updateCheckDate (long idLGSS, Date checkDate, FormOfControlConst foc);

    boolean updateGroup (long idLGSS, long idLGS);

    boolean checkIfOpenRetakeExist (long idSR, FormOfControlConst foc);

    boolean notExistEmployeeSubjectGroup (long idSubject);

    List<SessionRatingModel> getSRbySSS (long idSSS);

    List<RegisterCurProgressModel> getRegisterCurProgressByLgs(Long idLGS);

    RegisterCurProgressGroupModel getGroupname(Long idLGS);

    List<StudentModel> getStudentForOpenMainRetake (Long idLgss);
    Long getIdChairByHumanface(Long idHumanface);

    void createSRH(long idLGSS, FormOfControlConst foc, Integer type);

}
