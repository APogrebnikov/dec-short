package org.edec.notification.service.impl;

import org.edec.main.model.DepartmentModel;
import org.edec.manager.EntityManagerSemesterESO;
import org.edec.model.HumanfaceModel;
import org.edec.model.SemesterModel;
import org.edec.notification.manager.NotificationEsoManager;
import org.edec.notification.model.Department;
import org.edec.notification.model.eso.DepartmentHumanESO;
import org.edec.notification.service.NotificationEsoService;

import java.util.ArrayList;
import java.util.List;

public class NotificationEsoImpl implements NotificationEsoService {

    private EntityManagerSemesterESO emSemesterESO = new EntityManagerSemesterESO();
    private NotificationEsoManager notificationManager = new NotificationEsoManager();

    @Override
    public SemesterModel getCurrentSem (Long idInst, Integer formOfStudy) {
        List<SemesterModel> semesters = emSemesterESO.getSemesters(idInst, formOfStudy, 1);
        return semesters.size() == 0 ? null : semesters.get(0);
    }

    @Override
    public List<DepartmentModel> getDepartmentModels () {
        return null;
    }

    @Override
    public List<Department> getStudentDepartments (Long idInst, Integer formOfStudy, Integer course, String groupname, String qualification,
                                                   Boolean isGroupLeader, String fio) {
        return getDepartments(
                notificationManager.getStudentDepartments(idInst, formOfStudy, course, groupname, qualification, isGroupLeader, fio));
    }

    @Override
    public List<Department> getEmployeeDepartment (String department, Boolean isSecretaryChair, String fio) {
        return getDepartments(notificationManager.getEmployeeDepartments(fio, department, isSecretaryChair));
    }

    private List<Department> getDepartments (List<DepartmentHumanESO> models) {
        List<Department> result = new ArrayList<>();
        for (DepartmentHumanESO model : models) {
            boolean addDepartment = true;
            for (Department department : result) {
                if (department.getDepartment().equals(model.getDepartment())) {
                    setHumanForDepartment(model, department);
                    addDepartment = false;
                    break;
                }
            }
            if (addDepartment) {
                Department department = new Department();
                department.setDepartment(model.getDepartment());
                setHumanForDepartment(model, department);
                result.add(department);
            }
        }
        return result;
    }

    private void setHumanForDepartment (DepartmentHumanESO model, Department department) {
        HumanfaceModel humanface = new HumanfaceModel();
        humanface.setFamily(model.getFamily());
        humanface.setName(model.getName());
        humanface.setPatronymic(model.getPatronymic());
        humanface.setIdHum(model.getIdHum());

        department.getHumans().add(humanface);
    }
}
