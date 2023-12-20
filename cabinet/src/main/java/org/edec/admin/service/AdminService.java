package org.edec.admin.service;

import org.edec.admin.model.DepartmentModel;
import org.edec.admin.model.EmployeeModel;
import org.edec.admin.model.ModuleModel;
import org.edec.admin.model.RoleModel;

import java.util.List;


public interface AdminService {
    /**
     * Для прикрепления сотруднику ролей
     **/
    List<EmployeeModel> getEmployeesByFilter (String fio);

    List<ModuleModel> getModulesByRole (Long idRole);

    List<RoleModel> getRolesByEmp (Long idEmp);

    List<RoleModel> getRolesNotEqualEmp (Long idEmp);

    boolean addRoleForEmployee (Long idEmployee, Long idRole);

    boolean deleteRoleEmp (Long idEmp, Long idRole);
    /** /Для прикрепления сотруднику ролей**/

    /**
     * Для создание ролей
     **/
    List<DepartmentModel> getAllDepartments ();

    List<DepartmentModel> getDepartmentsByFilter (String departmentTitle, List<DepartmentModel> departments);

    List<ModuleModel> getAllModules ();

    List<RoleModel> getAllRoles ();

    List<String> getRolesByModule (Long idModule);

    boolean addRole (String value);

    boolean addModule (String name, String url, String image);

    boolean addModuleForRole (Long idDepartment, Long idModule, Long idRole, Integer formofstudy, Boolean readonly);

    boolean deleteModuleFromRole (Long idModuleRoleDep);
    /** /Для создание ролей**/
}
