package org.edec.chairEmployee.service;

import org.edec.chairEmployee.model.PostModel;
import org.edec.main.model.DepartmentModel;
import org.edec.utility.component.model.EmployeeModel;

import java.util.List;

/**
 * @author Max Dimukhametov
 */
public interface ChairEmployeeService {
    List<EmployeeModel> getEmployeeByFilter (String fio, Long idEmp);

    List<EmployeeModel> getEmployeeByDepartment (Long idDepartment);

    List<PostModel> getPostsByIdEmp (Long idEmp);

    List<DepartmentModel> getDepartments ();

    List<DepartmentModel> getDepartmentsByName (String fulltitle, List<DepartmentModel> departments);

    List<PostModel> getPosts ();

    PostModel getPostByName (String post, List<PostModel> posts);

    Long createEmployee (String family, String name, String patronymic, Integer sex, String ldapLogin, Boolean hasSign);

    Long createEmployeeByHum (String ldapLogin, Long idHum);

    boolean createLED (Long idEmp, Long idPost, Long idDepartment);

    boolean updateLED (Long idLED, Long idPost);

    boolean deleteLED (Long idLED);

    boolean updateParamsHum (EmployeeModel employee);

    boolean updateHideEmpDep (EmployeeModel employee);

    boolean updateParamsHum (Long idLED, Long idPost);
}