package org.edec.teacher.service;

import org.edec.teacher.model.SemesterModel;
import org.edec.teacher.model.commission.CommissionModel;
import org.edec.teacher.model.commission.EmployeeModel;
import org.edec.teacher.model.commission.StudentModel;

import java.util.List;


public interface CompletionCommissionService {

    List<SemesterModel> getSemesterCommByHum(Long idHum, Long idRC, boolean allCommissions, boolean onlySigned);

    CommissionModel getCommissionByRegister(Long idRegister);
    List<CommissionModel> getListCommByHum(Long idHumanface, Integer signedReg);
}
