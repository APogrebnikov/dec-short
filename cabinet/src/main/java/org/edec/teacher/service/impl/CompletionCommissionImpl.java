package org.edec.teacher.service.impl;

import org.edec.teacher.manager.EntityManagerCompletion;
import org.edec.teacher.model.SemesterModel;
import org.edec.teacher.model.commission.CommissionModel;
import org.edec.teacher.model.commission.EmployeeModel;
import org.edec.teacher.model.commission.StudentModel;
import org.edec.teacher.model.dao.CompletionCommESOmodel;
import org.edec.teacher.service.CompletionCommissionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class CompletionCommissionImpl implements CompletionCommissionService {
    private EntityManagerCompletion emCompletion = new EntityManagerCompletion();

    @Override
    public List<SemesterModel> getSemesterCommByHum(Long idHum, Long idRegister, boolean allCommissions, boolean signed) {

        List<CompletionCommESOmodel> list = emCompletion.getCompletionCommESOmodel(idHum, idRegister, allCommissions, signed);
        list.sort((o1, o2) -> {
            if (o1.getDateOfBeginSY().compareTo(o2.getDateOfBeginSY()) == 0) {
                if (o1.getSeason().compareTo(o2.getSeason()) == 0) {
                    if (o1.getFormofstudy().compareTo(o2.getFormofstudy()) == 0) {
                        return o1.getSubjectName().compareTo(o2.getSubjectName());
                    } else {
                        return o1.getFormofstudy().compareTo(o2.getFormofstudy());
                    }
                } else {
                    return o2.getSeason().compareTo(o1.getSeason());
                }
            } else {
                return o2.getDateOfBeginSY().compareTo(o1.getDateOfBeginSY());
            }
        });
        return devideSemesterByCommission(list);
    }

    @Override
    public List<CommissionModel> getListCommByHum(Long idHumanface, Integer signedReg) {
        List<CommissionModel> listCommission = emCompletion.getListCommissionByHum(idHumanface);
        if (signedReg == 1) {
            return listCommission.stream().filter(com -> com.isSigned() && com.getCertnumber() != null).collect(Collectors.toList());
        } else if (signedReg == 2) {
            return listCommission.stream().filter(com -> !com.isSigned()).collect(Collectors.toList());
        } else if (signedReg  == 3) {
            return listCommission.stream().filter(com -> com.getCertnumber() != null && com.getCertnumber().equals("  ")).collect(Collectors.toList());
        } else {
            return listCommission;
        }
    }

    @Override
    public CommissionModel getCommissionByRegister(Long idRegister) {

        List<CompletionCommESOmodel> model = emCompletion.getCompletionCommESOmodel(null, idRegister, true,false);
        CommissionModel commission = getCommissionModelByModel(model.get(0));
        commission.setStudents(emCompletion.getStudentsByCommission(idRegister));
        List<EmployeeModel> employees = emCompletion.getEmployeeByCommission(idRegister);
        commission.setEmployees(employees);
        for (EmployeeModel employee : employees) {
            if (employee.getChairman()) {
                commission.setChairman(employee.getFio());
            } else {
                commission.getCommissionStaff().add(employee);
            }
        }
        return commission;
    }

    private List<SemesterModel> devideSemesterByCommission(List<CompletionCommESOmodel> models) {
        List<SemesterModel> result = new ArrayList<>();

        for (CompletionCommESOmodel model : models) {
            boolean createSem = true;
            for (SemesterModel semester : result) {
                if (semester.getSemesterStr().equals(model.getSemesterStr()) && semester.getFormofstudy().equals(model.getFormofstudy()) &&
                        semester.getInstitute().equals(model.getInstitute())) {
                    setCommissionForSemester(model, semester);
                    createSem = false;
                    break;
                }
            }
            if (createSem) {
                SemesterModel semester = new SemesterModel();
                semester.setCurSem(model.getCurSem());
                semester.setFormofstudy(model.getFormofstudy());
                semester.setInstitute(model.getInstitute());
                semester.setSemesterStr(model.getSemesterStr());
                setCommissionForSemester(model, semester);
                result.add(semester);
            }
        }
        return result;
    }

    private void setCommissionForSemester(CompletionCommESOmodel model, SemesterModel semester) {
        CommissionModel commission = getCommissionModelByModel(model);
        commission.setSemester(semester);
        semester.getCommissions().add(commission);
    }

    private CommissionModel getCommissionModelByModel(CompletionCommESOmodel model) {
        CommissionModel commission = new CommissionModel();
        commission.setCertnumber(model.getCertnumber());
        commission.setCourse(model.getCourse());
        commission.setDateOfCommission(model.getDateOfCommission());
        commission.setFormOfControl(model.getFormOfControl());
        commission.setIdRC(model.getIdRC());
        commission.setIdReg(model.getIdReg());
        commission.setIdSem(model.getIdSem());
        commission.setInstitute(model.getInstitute());
        commission.setRegNumber(model.getRegNumber());
        commission.setSemesternumber(model.getSemesternumber());
        commission.setSemesterStr(model.getSemesterStr());
        commission.setSignatorytutor(model.getSignatorytutor());
        commission.setSubjectName(model.getSubjectName());
        commission.setType(model.getType());
        commission.setClassroom(model.getClassroom());
        commission.setTimeCom(model.getTimeCom());
        commission.setHoursCount(model.getHoursCount().intValue());
        commission.setCreditUnits(new BigDecimal(commission.getHoursCount() / 36).setScale(2, RoundingMode.UP).doubleValue());

        return commission;
    }
}
