package org.edec.chairEmployee.service.impl;

import org.edec.chairEmployee.manager.EntityManagerChairEmployee;
import org.edec.chairEmployee.model.PostModel;
import org.edec.chairEmployee.service.ChairEmployeeService;
import org.edec.main.model.DepartmentModel;
import org.edec.utility.component.model.EmployeeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class ChairEmployeeImpl implements ChairEmployeeService {
    private EntityManagerChairEmployee emChairEmployee = new EntityManagerChairEmployee();

    @Override
    public List<EmployeeModel> getEmployeeByFilter (String fio, Long idEmp) {
        return emChairEmployee.getEmployeesByFilter(fio, idEmp);
    }

    @Override
    public List<EmployeeModel> getEmployeeByDepartment (Long idDepartment) {
        return emChairEmployee.getEmployeeByDepartment(idDepartment);
    }

    @Override
    public List<PostModel> getPostsByIdEmp (Long idEmp) {
        return emChairEmployee.getEmployeePostByIdEmp(idEmp);
    }

    @Override
    public List<DepartmentModel> getDepartments () {
        return emChairEmployee.getAllDepartments();
    }

    @Override
    public List<DepartmentModel> getDepartmentsByName (String fulltitle, List<DepartmentModel> departments) {
        if (fulltitle.equals("")) {
            return departments;
        }
        List<DepartmentModel> filteredDepartments = new ArrayList<>();
        for (DepartmentModel department : departments) {
            if (department.getFulltitle().toLowerCase().contains(fulltitle.toLowerCase())) {
                filteredDepartments.add(department);
            }
        }
        return filteredDepartments;
    }

    @Override
    public List<PostModel> getPosts () {
        return emChairEmployee.getPosts();
    }

    @Override
    public PostModel getPostByName (String post, List<PostModel> posts) {
        for (PostModel postModel : posts) {
            if (postModel.getPost().equals(post)) {
                return postModel;
            }
        }
        return null;
    }

    @Override
    public Long createEmployee (String family, String name, String patronymic, Integer sex, String ldapLogin, Boolean hasSign) {
        return emChairEmployee.createHumanEmployee(family, name, patronymic, sex, ldapLogin, hasSign);
    }

    @Override
    public Long createEmployeeByHum (String ldapLogin, Long idHum) {
        return emChairEmployee.createEmployee(idHum, ldapLogin);
    }

    @Override
    public boolean createLED (Long idEmp, Long idPost, Long idDepartment) {
        return emChairEmployee.createLED(idEmp, idPost, idDepartment);
    }

    @Override
    public boolean updateLED(Long idLED, Long idPost) {
        return emChairEmployee.updateLED(idLED, idPost);
    }

    @Override
    public boolean deleteLED (Long idLED) {
        return emChairEmployee.deleteLED(idLED);
    }

    @Override
    public boolean updateParamsHum (EmployeeModel employee) {
        return emChairEmployee.updateHumParameters(employee);
    }

    @Override
    public boolean updateParamsHum (Long idLED, Long idPost) {
        return false;
    }

    @Override
    public boolean updateHideEmpDep (EmployeeModel employee) {
        return emChairEmployee.updateHideEmpDep(employee);
    }
}
