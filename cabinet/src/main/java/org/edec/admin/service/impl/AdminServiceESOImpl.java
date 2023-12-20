package org.edec.admin.service.impl;

import org.edec.admin.manager.AdminManager;
import org.edec.admin.model.DepartmentModel;
import org.edec.admin.model.EmployeeModel;
import org.edec.admin.model.ModuleModel;
import org.edec.admin.model.RoleModel;
import org.edec.admin.service.AdminService;

import java.util.ArrayList;
import java.util.List;


public class AdminServiceESOImpl implements AdminService {
    private AdminManager adminManager = new AdminManager();

    @Override
    public List<EmployeeModel> getEmployeesByFilter (String fio) {
        return adminManager.getEmployeeByFilter(fio);
    }

    @Override
    public List<ModuleModel> getModulesByRole (Long idRole) {
        return adminManager.getModulesByRole(idRole);
    }

    @Override
    public List<RoleModel> getRolesByEmp (Long idEmp) {
        return adminManager.getRolesByEmp(idEmp);
    }

    @Override
    public List<RoleModel> getRolesNotEqualEmp (Long idEmp) {
        return adminManager.getRolesNotEqualEmp(idEmp);
    }

    public boolean addRoleForEmployee (Long idEmp, Long idRole) {
        return adminManager.addRoleForEmployee(idEmp, idRole);
    }

    @Override
    public boolean deleteRoleEmp (Long idEmp, Long idRole) {
        return adminManager.deleteRoleEmp(idEmp, idRole);
    }

    @Override
    public List<DepartmentModel> getAllDepartments () {
        return adminManager.getAllDepartments();
    }

    @Override
    public List<DepartmentModel> getDepartmentsByFilter (String departmentTitle, List<DepartmentModel> departments) {
        List<DepartmentModel> filteredDepartments = new ArrayList<>();
        for (DepartmentModel department : departments) {
            if (department.getDepartmentTitle().toLowerCase().contains(departmentTitle.toLowerCase())) {
                filteredDepartments.add(department);
            }
        }
        return filteredDepartments;
    }

    @Override
    public List<ModuleModel> getAllModules () {
        return adminManager.getAllModules();
    }

    @Override
    public List<RoleModel> getAllRoles () {
        return adminManager.getAllRoles();
    }

    @Override
    public List<String> getRolesByModule (Long idModule) {
        return adminManager.getRolesByModule(idModule);
    }

    @Override
    public boolean addRole (String value) {
        return adminManager.addRole(value);
    }

    @Override
    public boolean addModule (String name, String url, String image) {
        return adminManager.addModule(name, url, image);
    }

    @Override
    public boolean addModuleForRole (Long idDepartment, Long idModule, Long idRole, Integer formofstudy, Boolean readonly) {
        return adminManager.addModuleForRole(idDepartment, idModule, idRole, formofstudy, readonly);
    }

    @Override
    public boolean deleteModuleFromRole (Long idModuleRoleDep) {
        return adminManager.deleteModuleFromRole(idModuleRoleDep);
    }
}
