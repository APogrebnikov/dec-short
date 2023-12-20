package org.edec.efficiency.service.impl;

import org.edec.efficiency.manager.EntityManagerEfficiency;
import org.edec.efficiency.model.*;
import org.edec.efficiency.model.dao.GroupStudentModel;
import org.edec.efficiency.service.EfficiencyService;
import org.edec.model.EmployeeModel;

import java.util.ArrayList;
import java.util.List;


public class EfficiencyImpl implements EfficiencyService {
    private EntityManagerEfficiency emEfficiency = new EntityManagerEfficiency();

    @Override
    public ConfigurationEfficiency getConfiguration (Long idSem) {
        ConfigurationEfficiency configurationEfficiency = emEfficiency.getConfiguration(idSem);
        if (configurationEfficiency != null) {
            return configurationEfficiency;
        }
        Long idConfiguration = emEfficiency.createNewConfiguration(idSem);
        configurationEfficiency = new ConfigurationEfficiency();
        configurationEfficiency.setIdSem(idSem);
        configurationEfficiency.setIdConfigurationEfficiency(idConfiguration);
        return configurationEfficiency;
    }

    @Override
    public List<EmployeeModel> getEmployeeByDepartment (Long idDepartment) {
        return emEfficiency.getEmployeeByDep(idDepartment);
    }

    @Override
    public List<ProblemGroup> getProblemGroupsConfiguration (Long idSem, String qualification) {
        return emEfficiency.getProblemGroupsConfiguration(idSem, qualification);
    }

    @Override
    public List<ProblemGroup> getProblemGroups (Long idSem, Long idEmp, Integer course, String groupname, String student,
                                                String statusEfficiency, boolean assigned) {
        return getProblemGroupByModel(
                emEfficiency.getGroupStudentModel(idSem, idEmp, course, groupname, student, statusEfficiency, assigned));
    }

    @Override
    public List<ProblemSubjectGroup> getProblemSubjectGroups (Long idLGS) {
        return emEfficiency.getProblemSubjectGroups(idLGS);
    }

    @Override
    public List<ProblemStudent> getProblemStudents (Long idLGS, Long idEmp) {
        return emEfficiency.getProblemStudent(idLGS, idEmp);
    }

    @Override
    public Long getCurrentSem (Long idInst, int formOfStudy) {
        return emEfficiency.getSemByInsyAndFormOfStudy(idInst, formOfStudy);
    }

    @Override
    public Long getEmployee (Long idHum) {
        return emEfficiency.getEmployeeByHum(idHum);
    }

    @Override
    public EmployeeModel getEmpByEmployees (Long idEmp, List<EmployeeModel> employees) {
        for (EmployeeModel employee : employees) {
            if (employee.getIdEmp().equals(idEmp)) {
                return employee;
            }
        }
        return null;
    }

    @Override
    public boolean updateConfiguration (ConfigurationEfficiency configuration) {
        if (!emEfficiency.updateConfigurationEfficiency(configuration) || !emEfficiency.deleteAllEfficiencyStudentWithStatusCreate() ||
            !emEfficiency.createEfficiencyStudentBySem(configuration.getIdSem())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean updateEfficiencyGroup (Long idLGS, Boolean efficiency) {
        return emEfficiency.updateEfficiencyGroup(idLGS, efficiency);
    }

    @Override
    public boolean updateEfficiencySubject (Long idLGSS, Boolean attendance, Boolean eok, Boolean performance) {
        return emEfficiency.updateEfficiencySubject(idLGSS, attendance, eok, performance);
    }

    @Override
    public boolean updateEmployeeForGroup (Long idEmp, Long idLGS) {
        return emEfficiency.updateEmployeeForGroup(idEmp, idLGS);
    }

    @Override
    public boolean updateEmployeeForStudent (Long idEmp, Long idEfficiencyStudent) {
        return emEfficiency.updateEmployeeForStudent(idEmp, idEfficiencyStudent);
    }

    @Override
    public boolean updateStatusConfirmForGroup (Long idEmp, Long idLGS) {
        return emEfficiency.confirmGroupForManage(idEmp, idLGS);
    }

    @Override
    public boolean updateStatusConfirmForStudent (Long idEmp, Long idSSS) {
        return emEfficiency.confirmStudentForManage(idEmp, idSSS);
    }

    @Override
    public boolean updateStatusCompletedForStudent (Long idEmp, Long idSSS) {
        return emEfficiency.completeStudentForManage(idEmp, idSSS);
    }

    @Override
    public boolean updateEfficiencyStudent (ProblemStudent student) {
        return emEfficiency.updateEfficiencyStudent(student);
    }

    private List<ProblemGroup> getProblemGroupByModel (List<GroupStudentModel> models) {
        List<ProblemGroup> result = new ArrayList<>();

        for (GroupStudentModel model : models) {
            boolean addGroup = true;
            for (ProblemGroup group : result) {
                if (group.getIdLGS().equals(model.getIdLGS())) {
                    setStudentForGroup(group, model);
                    addGroup = false;
                    break;
                }
            }
            if (addGroup) {
                ProblemGroup group = new ProblemGroup();
                group.setGroupname(model.getGroupname());
                group.setIdLGS(model.getIdLGS());
                setStudentForGroup(group, model);
                result.add(group);
            }
        }
        return result;
    }

    private void setStudentForGroup (ProblemGroup group, GroupStudentModel model) {
        ProblemStudent student = new ProblemStudent();
        student.setGroupLeader(model.getGroupLeader());
        student.setComment(model.getComment());
        student.setFamily(model.getFamily());
        student.setName(model.getName());
        student.setPatronymic(model.getPatronymic());
        student.setIdSSS(model.getIdSSS());
        student.setIdEfficiencyStudent(model.getIdEfficiencyStudent());
        student.setIdEmp(model.getIdEmp());
        student.setAttend(model.getAttend());
        student.setEokActivity(model.getEokActivity());
        student.setPerformance(model.getPerformance());
        student.setStatus(StatusEfficiency.getStatusEfficiencyByInt(model.getStatusEfficiency()));
        student.setDateCreated(model.getDateCreated());
        student.setDateConfirm(model.getDateConfirm());
        student.setDateCompleted(model.getDateCompleted());
        group.getStudents().add(student);
    }
}
