package org.edec.admin.manager;

import org.edec.admin.model.DepartmentModel;
import org.edec.admin.model.EmployeeModel;
import org.edec.admin.model.ModuleModel;
import org.edec.admin.model.RoleModel;
import org.edec.dao.DAO;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.Collections;
import java.util.List;


public class AdminManager extends DAO {
    public List<EmployeeModel> getEmployeeByFilter (String fio) {
        String query = "SELECT\n" + "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio,\n" + "\tEMP.id_employee  AS idEmp\n" + "FROM\n" +
                       "\thumanface HF\n" + "\tINNER JOIN employee EMP USING (id_humanface)\n" + "WHERE\n" +
                       "\tHF.family||' '||HF.name||' '||HF.patronymic ILIKE :fio\n" + "ORDER BY\n" + "\tfio";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("idEmp", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(EmployeeModel.class));
        q.setString("fio", "%" + fio + "%");
        return (List<EmployeeModel>) getList(q);
    }

    public List<ModuleModel> getModulesByRole (Long idRole) {
        String query = "SELECT\n" + "\tM.name, MRD.readonly, MRD.formofstudy, \n" +
                       "\tDEP.fulltitle AS departmentTitle, INST.fulltitle AS instituteTitle,\n" +
                       "\tMRD.id_module_role_department AS idModuleRoleDep\n" + "FROM\n" + "\tmodule_role_department MRD\n" +
                       "\tINNER JOIN dic_module M USING (id_dic_module)\n" + "\tINNER JOIN department DEP USING (id_department)\n" +
                       "\tLEFT JOIN institute INST USING (id_institute)\n" + "WHERE\n" + "\tMRD.id_dic_role = :idRole\n" + "ORDER BY\n" +
                       "\tname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("name")
                              .addScalar("readonly")
                              .addScalar("departmentTitle")
                              .addScalar("instituteTitle")
                              .addScalar("formofstudy")
                              .addScalar("idModuleRoleDep", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(ModuleModel.class));
        q.setParameter("idRole", idRole);
        return (List<ModuleModel>) getList(q);
    }

    public List<RoleModel> getRolesByEmp (Long idEmp) {
        String query = "SELECT\n" + "\tR.name, R.id_dic_role AS idRole\n" + "FROM\n" + "\tlink_employee_role LER\n" +
                       "\tINNER JOIN dic_role R USING (id_dic_role)\n" + "WHERE\n" + "\tLER.id_employee = :idEmp\n" + "ORDER BY\n" +
                       "\tname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("name")
                              .addScalar("idRole", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(RoleModel.class));
        q.setParameter("idEmp", idEmp);
        return (List<RoleModel>) getList(q);
    }

    // TODO REFACTOR переименовать метод, название звучит очень странно
    public List<RoleModel> getRolesNotEqualEmp (Long idEmp) {
        String query = "SELECT\n" + "\tname, id_dic_role AS idRole\n" + "FROM\n" + "\tdic_role\n" + "WHERE\n" +
                       "\tid_dic_role NOT IN (SELECT id_dic_role FROM link_employee_role WHERE id_employee = :idEmp) \n" +
                       "\tAND visible = TRUE\n" + "ORDER BY\n" + "\tname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("name")
                              .addScalar("idRole", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(RoleModel.class));
        q.setParameter("idEmp", idEmp);
        return (List<RoleModel>) getList(q);
    }

    public boolean addRoleForEmployee (Long idEmp, Long idRole) {
        String query = "INSERT INTO link_employee_role (id_employee, id_dic_role) VALUES (:idEmp, :idRole)";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idEmp", idEmp).setParameter("idRole", idRole);
        return executeUpdate(q);
    }

    public boolean deleteRoleEmp (Long idEmp, Long idRole) {
        String query = "DELETE FROM link_employee_role WHERE id_employee = :idEmp AND id_dic_role = :idRole";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idEmp", idEmp).setParameter("idRole", idRole);
        return executeUpdate(q);
    }

    public List<RoleModel> getAllRoles () {
        String query = "SELECT name, id_dic_role AS idRole FROM dic_role WHERE visible = TRUE ORDER BY name ";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idRole", LongType.INSTANCE)
                              .addScalar("name")
                              .setResultTransformer(Transformers.aliasToBean(RoleModel.class));
        return (List<RoleModel>) getList(q);
    }

    public List<ModuleModel> getAllModules () {
        String query = "SELECT name, id_dic_module AS idModule FROM dic_module WHERE visible = TRUE";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("name")
                              .addScalar("idModule", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(ModuleModel.class));
        return (List<ModuleModel>) getList(q);
    }

    public List<DepartmentModel> getAllDepartments () {
        String query =
                "SELECT\n" + "\tDEP.fulltitle AS departmentTitle,INST.fulltitle AS instituteTitle,DEP.id_department AS idDepartment\n" +
                "FROM\n" + "\tdepartment DEP\n" + "\tLEFT JOIN institute INST USING (id_institute)\n" + "ORDER BY\n" + "\tDEP.fulltitle";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("departmentTitle")
                              .addScalar("instituteTitle")
                              .addScalar("idDepartment", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(DepartmentModel.class));
        return (List<DepartmentModel>) getList(q);
    }

    public boolean addModuleForRole (Long idDepartment, Long idModule, Long idRole, Integer formofstudy, Boolean readonly) {
        String query = "INSERT INTO module_role_department " + "(id_department, id_dic_module, id_dic_role, formofstudy, readonly)\n" +
                       "VALUES (:idDepartment, :idModule, :idRole, :formofstudy, :readonly)";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idDepartment", idDepartment)
         .setParameter("idModule", idModule)
         .setParameter("idRole", idRole)
         .setParameter("formofstudy", formofstudy)
         .setParameter("readonly", readonly);
        return executeUpdate(q);
    }

    public boolean deleteModuleFromRole (Long idModuleRoleDep) {
        String query = "DELETE FROM module_role_department WHERE id_module_role_department =  :idModuleRoleDep";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idModuleRoleDep", idModuleRoleDep);
        return executeUpdate(q);
    }

    public boolean addRole (String value) {
        String query = "INSERT INTO dic_role (name) VALUES ('" + value + "')";
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean addModule (String name, String url, String image) {
        return executeUpdate(getSession().createSQLQuery("INSERT INTO dic_module (name, url, path_image) VALUES (:name, :url, :image)")
                                         .setString("name", name)
                                         .setString("url", url)
                                         .setString("image", image));
    }

    public List<String> getRolesByModule (Long idModule) {
        String query =
                "SELECT \tDISTINCT DR.name\n" + "FROM \tdic_role DR\n" + "\tINNER JOIN module_role_department MRD USING (id_dic_role)\n" +
                "WHERE\tMRD.id_dic_module = :idModule\n" + "ORDER BY name\n" + "\t\n";
        Query q = getSession().createSQLQuery(query).setLong("idModule", idModule);
        return (List<String>) getList(q);
    }
}
