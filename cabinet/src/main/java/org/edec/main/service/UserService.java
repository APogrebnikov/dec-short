package org.edec.main.service;

import org.edec.main.model.ModuleModel;
import org.edec.main.model.RoleModel;
import org.edec.main.model.UserModel;


public interface UserService {
    UserModel getUserByLdapLogin (String ldapLogin);
    ModuleModel getModuleByUserAndPath (String path, UserModel user);
    ModuleModel getModuleByRoleAndPath (String path, RoleModel role);
    UserModel getUserByInnerLogin (String login, String password);
    Integer getStudentSemesterStatus(String fio, String groupname);
    Integer checkStudentIsAcademicLeave(String fio, String groupname);
    void changeSelected (ModuleModel selectedModule, UserModel user);
    boolean setVisitedModuleByHum (Long idHum, Long idModule);
    boolean createTokenAndIdHumanface (Long idHumanface, String token);
}
