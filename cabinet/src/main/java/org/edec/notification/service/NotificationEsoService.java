package org.edec.notification.service;

import org.edec.main.model.DepartmentModel;
import org.edec.model.SemesterModel;
import org.edec.notification.model.Department;

import java.util.List;

public interface NotificationEsoService {
    SemesterModel getCurrentSem (Long idInst, Integer formOfStudy);
    List<DepartmentModel> getDepartmentModels ();
    List<Department> getStudentDepartments (Long idInst, Integer formOfStudy, Integer course, String groupname, String qualification,
                                            Boolean isGroupLeader, String fio);
    List<Department> getEmployeeDepartment (String department, Boolean isSecretaryChair, String fio);
}
