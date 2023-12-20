package org.edec.efficiency.service;

import org.edec.efficiency.model.ConfigurationEfficiency;
import org.edec.efficiency.model.ProblemGroup;
import org.edec.efficiency.model.ProblemStudent;
import org.edec.efficiency.model.ProblemSubjectGroup;
import org.edec.model.EmployeeModel;

import java.util.List;


public interface EfficiencyService {
    ConfigurationEfficiency getConfiguration (Long idSem);
    List<EmployeeModel> getEmployeeByDepartment (Long idDepartment);
    List<ProblemGroup> getProblemGroupsConfiguration (Long idSem, String qualification);
    List<ProblemGroup> getProblemGroups (Long idSem, Long idEmp, Integer course, String groupname, String fio, String statusEfficiency,
                                         boolean assigned);
    List<ProblemSubjectGroup> getProblemSubjectGroups (Long idLGS);
    List<ProblemStudent> getProblemStudents (Long idLGS, Long idEmp);
    Long getCurrentSem (Long idInst, int formOfStudy);
    Long getEmployee (Long idHum);
    EmployeeModel getEmpByEmployees (Long idEmp, List<EmployeeModel> employees);
    boolean updateConfiguration (ConfigurationEfficiency configuration);
    boolean updateEfficiencyGroup (Long idLGS, Boolean efficiency);
    boolean updateEfficiencySubject (Long idLGSS, Boolean attendance, Boolean eok, Boolean performance);
    boolean updateEmployeeForGroup (Long idEmp, Long idLGS);
    boolean updateEmployeeForStudent (Long idEmp, Long idEfficiencyStudent);
    boolean updateStatusConfirmForGroup (Long idEmp, Long idLGS);
    boolean updateStatusConfirmForStudent (Long idEmp, Long idSSS);
    boolean updateStatusCompletedForStudent (Long idEmp, Long idSSS);
    boolean updateEfficiencyStudent (ProblemStudent student);
}
