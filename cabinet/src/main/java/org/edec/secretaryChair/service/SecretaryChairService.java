package org.edec.secretaryChair.service;

import org.edec.secretaryChair.model.CommissionDayModel;
import org.edec.secretaryChair.model.CommissionModel;
import org.edec.secretaryChair.model.EmployeeModel;
import org.edec.secretaryChair.model.StudentCommissionModel;
import org.edec.teacher.model.commission.StudentModel;
import org.edec.utility.component.model.SemesterModel;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface SecretaryChairService {
    List<CommissionModel> getCommission(Long idSem, Long idChair, Integer formOfStudy, boolean signed);

    List<CommissionDayModel> getInfoCommissionDays(CommissionModel commission);

    List<SemesterModel> getSemesterByChair(Long idChair, Integer formOfStudy);

    List<StudentModel> getStudentByCommission(Long idCommission);

    /**
     * Проверка даты и времени, чтобы не было других комиссий на заданную дату
     *
     * @return
     */
    Map<String, List<StudentCommissionModel>> getStudentsForCheckFreeDate(CommissionModel commission, Date dateCommission);

    List<StudentCommissionModel> getStudentsForCheckFreeTime(CommissionModel commission, Date dateCommission);

    List<EmployeeModel> getEmployeeByChair(Long idChair);

    List<EmployeeModel> getEmployeeByCommission(Long idCommission);

    Map<Integer, List<Date>> getFreeIntervalByCommissionDay(CommissionDayModel commissionDay);

    boolean updateCommissionInfo(Date dateCommission, String classroom, Long idCommission);

    boolean deleteCommissionStaff(Long idCommission);

    boolean updateComissionStatusNotification(Long idComission, Integer status);

    boolean addCommissionStaff(EmployeeModel employee, Long idCommission);
}
