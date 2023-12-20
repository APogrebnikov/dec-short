package org.edec.commission.service;

import org.edec.commission.model.*;
import org.edec.model.SemesterModel;
import org.edec.secretaryChair.model.StudentCommissionModel;

import java.util.Date;
import java.util.List;

/**
 * Created by dmmax
 */
public interface CommissionService {
    List<StudentCountDebtModel> getListStudentCountDebt (String qualification, Integer course, Integer debtCount, String typeDebt,
                                                         Integer government, String listIdSem, boolean prolongation, Date dateProlongation,
                                                         Integer formofstudy, Long idInst);

    /**
     * Получение долгов студента
     *
     * @param idSc      - ид студентческой карты
     * @param idDg      - ид группы
     * @param listIdSem - список семестров, по которым ищем
     * @return - список долгов
     */
    List<StudentDebtModel> getDividedByFocStudentsDebt(Long idSc, Long idDg, String listIdSem, Integer formOfStudy);
    List<StudentDebtModel> getStudentByRegisterCommission (Long idRegComm);
    List<StudentCountDebtModel> getStudentsCountDebt(String fio, String group, List<StudentCountDebtModel> list);
    List<SubjectDebtModel> getSubjectCommissionByFilterAndSem (String fioGroupSubject, Long idSem, PeriodCommissionModel period,
                                                               Integer formOfStudy);
    List<SubjectDebtModel> getSubjectCommissionGraduateByPeriod (String fioGroupSubject, Long idSem, PeriodCommissionModel period,
                                                               Integer formOfStudy);
    List<SubjectDebtModel> getSubjectCommissionByFilterAndSem (String fioGroupSubject, Long idSem, PeriodCommissionModel period,
                                                               Integer formOfStudy, String formOfControl, Boolean isIndividual, Long idChair, String teacherStr);
    List<SubjectDebtModel> getSubjForCreateCommission (String listIdSSS, Date dateBegin, Date dateEnd);
    List<SubjectDebtModel> getSubjectsByFilter (String filter, List<SubjectDebtModel> allSubjects);
    List<SemesterModel> getAllSemesterWithCommission (Integer formOfStudy);
    List<SemesterModel> getSemesterByInstAndFOS (Long idInst, Integer fos);
    List<CommissionStructureModel> getCommissionStructure (Long idCommission);
    List<PeriodCommissionModel> getPeriodCommission (Integer formOfStudy);
    PeriodCommissionModel getPeriodCommissionForGradute (Integer formOfStudy, Long idSem);
    List<PeriodCommissionModel> getCommissionForGraduteBySem (Integer formOfStudy, Long idSem);
    List<Long> getListSSSForCommission (Long idRegCom);
    boolean updateStatusNotificationCom (Long idRegCom);

    boolean createIndividualCommission (StudentDebtModel studentDebtModel, Date dateBegin, Date dateEnd, Long idCurrentUser);
    boolean createCommonCommission (SubjectDebtModel subjectDebtModel, List<StudentDebtModel> students, Long idCurrentUser);
    boolean addStudentToCommission(StudentDebtModel student, SubjectDebtModel subjectDebtModel, Long idCurrentUser);
    boolean deleteCommission (Long idRegComm);
    boolean deleteSRHfromCommRegister (String listIDsrh);
    boolean updateCommissionRegister (Long idRegComm, Date dateBegin, Date dateEnd);
    boolean setCheckKutsSrh (Long idSrh, boolean status);
    void setStatusSignedForSubjAndStudent (SubjectDebtModel subject);
    boolean checkStudentAvailableForCommission(String fio, Date commissionDate);
    void setStatusSignedForSubjAndStudents (SubjectDebtModel subject, List<StudentDebtModel> students);
}
