package org.edec.chairEmployee.manager;

import org.edec.chairEmployee.model.PostModel;
import org.edec.dao.DAO;
import org.edec.main.model.DepartmentModel;
import org.edec.utility.component.model.EmployeeModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;

import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class EntityManagerChairEmployee extends DAO {
    public List<EmployeeModel> getEmployeesByFilter (String fio, Long idEmp) {
        String query = "SELECT HF.family, HF.name, HF.patronymic, HF.sex, HF.id_humanface AS idHum, \n" +
                       "\tEMP.id_employee AS idEmp, EMP.other_ad AS loginLdap, HF.email, hassign as hasSign\n" + "FROM humanface HF\n" +
                       "\tLEFT JOIN employee EMP USING (id_humanface)\n" + "WHERE HF.family||' '||HF.name||' '||HF.patronymic ILIKE :fio\n" +
                       (idEmp != null ? "\tAND CAST(EMP.id_employee AS TEXT) ILIKE :idEmp\n" : "") +
                       "ORDER BY HF.family, HF.name, HF.patronymic";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("family")
                              .addScalar("name")
                              .addScalar("patronymic")
                              .addScalar("sex")
                              .addScalar("loginLdap")
                              .addScalar("idHum", LongType.INSTANCE)
                              .addScalar("idEmp", LongType.INSTANCE)
                              .addScalar("email")
                .addScalar("hasSign", BooleanType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(EmployeeModel.class));
        q.setString("fio", "%" + fio + "%");
        if (idEmp != null) {
            q.setString("idEmp", String.valueOf(idEmp));
        }
        return (List<EmployeeModel>) getList(q);
    }

    public List<EmployeeModel> getEmployeeByDepartment (Long idDep) {
        String query = "SELECT HF.family, HF.name, HF.patronymic, LED.id_link_employee_department AS idLED,\n" +
                       "ER.rolename AS post, LED.is_hide AS isHide, HF.email\n" + "FROM humanface HF\n" +
                       "\tINNER JOIN employee USING (id_humanface)\n" + "\tINNER JOIN link_employee_department LED USING (id_employee)\n" +
                       "\tINNER JOIN employee_role ER USING (id_employee_role)\n" + "WHERE id_department = :idDep\n" +
                       "ORDER BY family, name, patronymic";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("family")
                              .addScalar("name")
                              .addScalar("patronymic")
                              .addScalar("post")
                              .addScalar("idLED", LongType.INSTANCE)
                              .addScalar("isHide", BooleanType.INSTANCE)
                              .addScalar("email")
                              .setResultTransformer(Transformers.aliasToBean(EmployeeModel.class));
        q.setLong("idDep", idDep);
        return (List<EmployeeModel>) getList(q);
    }

    public List<PostModel> getEmployeePostByIdEmp (Long idEmp) {
        String query = "SELECT\n" + "\tDEP.fulltitle AS department, ER.rolename AS post, LED.id_department AS idDepartment,\n" +
                       "\tER.id_employee_role AS idPost, LED.id_link_employee_department AS idLED\n" + "FROM\n" +
                       "\tlink_employee_department LED\n" + "\tINNER JOIN employee_role ER USING (id_employee_role)\n" +
                       "\tINNER JOIN department DEP USING (id_department)\n" + "WHERE\n" + "\tid_employee = :idEmp\n" + "ORDER BY\n" +
                       "\tfulltitle, rolename";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("department")
                              .addScalar("post")
                              .addScalar("idDepartment", LongType.INSTANCE)
                              .addScalar("idPost", LongType.INSTANCE)
                              .addScalar("idLED", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(PostModel.class));
        q.setLong("idEmp", idEmp);
        return (List<PostModel>) getList(q);
    }

    public List<DepartmentModel> getAllDepartments () {
        String query = "SELECT \n" + "\tid_department AS idDepartment, fulltitle, shorttitle, otherdbid AS idDepartmentMine,\n" +
                       "\tid_chair AS idChair\n" + "FROM department ORDER BY fulltitle";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idDepartment", LongType.INSTANCE)
                              .addScalar("fulltitle")
                              .addScalar("shorttitle")
                              .addScalar("idDepartmentMine", LongType.INSTANCE)
                              .addScalar("idChair", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(DepartmentModel.class));
        return (List<DepartmentModel>) getList(q);
    }

    public List<PostModel> getPosts () {
        String query = "SELECT rolename AS post, id_employee_role AS idPost FROM employee_role ORDER BY rolename";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("post")
                              .addScalar("idPost", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(PostModel.class));
        return (List<PostModel>) getList(q);
    }

    public Long createHumanEmployee (String family, String name, String patronymic, Integer sex, String ldapLogin, Boolean hasSign) {
        String query =
                "SELECT * FROM insert_emp_data('" + family + "', '" + name + "', '" + patronymic + "', " + sex + ", '" + ldapLogin + "', "+hasSign+ ")";
        Query q = getSession().createSQLQuery(query).addScalar("insert_emp_data", LongType.INSTANCE);
        return (Long) getList(q).get(0);
    }

    public Long createEmployee (Long idHum, String ldapLogin) {
        String query = "INSERT INTO employee (id_humanface, is_active, other_ad, hassign) VALUES (:idHum, 1, :ldapLogin, true) RETURNING id_employee";
        Query q = getSession().createSQLQuery(query).addScalar("id_employee", LongType.INSTANCE);
        q.setLong("idHum", idHum).setString("ldapLogin", ldapLogin);
        return (Long) getList(q).get(0);
    }

    public boolean updateHumParameters (EmployeeModel employee) {
        //Обновление humanface
        String query = "UPDATE humanface\n" + "SET family = :family, name = :name, patronymic = :patronymic, sex = :sex, email = :email \n" +
                       "WHERE id_humanface = :idHum";
        Query q = getSession().createSQLQuery(query)
                              .setString("family", employee.getFamily())
                              .setString("name", employee.getName())
                              .setString("patronymic", employee.getPatronymic())
                              .setString("email", employee.getEmail())
                              .setInteger("sex", employee.getSex())
                              .setLong("idHum", employee.getIdHum());
        if (!executeUpdate(q)) {
            return false;
        }
        //Обновление studentcard
        query = "UPDATE employee SET other_ad = :ldapLogin, hassign = :hasSign  WHERE id_employee = :idEmp";
        q = getSession().createSQLQuery(query).setString("ldapLogin", employee.getLoginLdap()).setLong("idEmp", employee.getIdEmp())
                .setBoolean("hasSign", employee.getHasSign());
        return executeUpdate(q);
    }

    public boolean updateHideEmpDep (EmployeeModel employee) {
        String queryHum = "UPDATE link_employee_department SET is_hide = :isHide WHERE id_link_employee_department = :idLED";
        Query qHum = getSession().createSQLQuery(queryHum).setBoolean("isHide", employee.getIsHide()).setLong("idLED", employee.getIdLED());
        return executeUpdate(qHum);
    }

    public boolean createLED (Long idEmp, Long idPost, Long idDepartment) {
        String query = "INSERT INTO link_employee_department (id_employee, id_employee_role, id_department) VALUES (:idEmp, :idPost, :idDepartment)";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idEmp", idEmp).setLong("idPost", idPost).setLong("idDepartment", idDepartment);
        return executeUpdate(q);
    }

    public boolean deleteLED (Long idLED) {
        String query = "DELETE FROM link_employee_department WHERE id_link_employee_department = :idLED";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idLED", idLED);
        return executeUpdate(q);
    }

    public boolean updateLED(Long idLED, Long idPost) {
        String query = "UPDATE link_employee_department SET id_employee_role = :idPost WHERE id_link_employee_department = :idLED";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idPost", idPost);
        q.setLong("idLED", idLED);
        return executeUpdate(q);
    }
}