package org.edec.main.manager;

import org.edec.dao.DAO;
import org.edec.main.model.dao.UserRoleModuleESOmodel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;
import org.hibernate.type.NumericBooleanType;

import java.util.Collections;
import java.util.List;


public class UserManagerDAO extends DAO {
    public List<UserRoleModuleESOmodel> getUserRoleModuleModel (String ldapLogin) {

        String query = "SELECT\n" +
                       " HF.family || ' ' || HF.name ||\n" +
                       "       (CASE WHEN HF.patronymic IS NOT NULL THEN ' ' || HF.patronymic ELSE '' END) AS fio," +
                       " HF.id_humanface AS idHum,\n" +
                       " DR.name AS roleName, COALESCE(MRD.readonly, FALSE) AS readonly," +
                       " MRD.formofstudy, DM.name AS moduleName,\n" +
                       " DM.path_image AS imagePath," +
                       " DM.url, " +
                       " DM.id_dic_module AS idModule, " +
                       " COUNT(id_studentcard)>0 AS student," +
                       " COUNT(LESG.*)>0 AS teacher,\n" +
                       " DEP.fulltitle, " +
                       " DEP.shorttitle, " +
                       " DEP.id_department AS idDepartment, " +
                       " DEP.id_chair AS idChair,\n" +
                       " INST.id_institute AS idInstitute, " +
                       " INST.fulltitle AS institute, " +
                       " INST.otherdbid AS idInstituteMine,\n" +
                       "  MRD.type, HF.start_page AS startPage,\n" +
                       "                                 CASE WHEN COUNT(id_studentcard)>0 THEN (SELECT DISTINCT(groupname) FROM studentcard SC2\n" +
                       "                                   INNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                       "                                   INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "                                   INNER JOIN semester SEM ON LGS.id_semester =  SEM.id_semester\n" +
                       "                                   INNER JOIN dic_group DG USING (id_dic_group)\n" +
                       "                                 WHERE SC2.id_studentcard = MAX(SC.id_studentcard) AND LGS.id_dic_group = SC2.id_current_dic_group)\n" +
                       "                                 END AS groupname,\n" +
                       " COALESCE(CASE WHEN (COUNT(id_studentcard)>0 AND (SELECT COUNT(*) FROM student_semester_status SSS INNER JOIN link_group_semester LGS USING (id_link_group_semester) INNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester AND SEM.is_current_sem = 1 WHERE SSS.id_studentcard = MAX(SC.id_studentcard)) > 0)\n" +
                       "                                   THEN (SELECT CASE WHEN SSS.is_group_leader IS NOT NULL THEN SSS.is_group_leader ELSE 0 END FROM studentcard SC2\n" +
                       "                                     INNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                       "                                     INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "                                     INNER JOIN semester SEM ON LGS.id_semester =  SEM.id_semester AND SEM.is_current_sem = 1\n" +
                       "                                   WHERE SC2.id_studentcard = MAX(SC.id_studentcard) AND LGS.id_dic_group = SC2.id_current_dic_group)\n" +
                       " END, 0) AS groupLeader,\n" +
                       "                                 CASE WHEN COUNT(id_studentcard)>0 THEN (SELECT DISTINCT(SEM.formofstudy) FROM studentcard SC2\n" +
                       "                                   INNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                       "                                   INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "                                   INNER JOIN semester SEM ON LGS.id_semester =  SEM.id_semester\n" +
                       "                                 WHERE SC2.id_studentcard = MAX(SC.id_studentcard) AND LGS.id_dic_group = SC2.id_current_dic_group)\n" +
                       "                                 END AS formofstudystudent\n" + "FROM\n" + "  humanface HF\n" +
                       "  LEFT JOIN studentcard SC USING (id_humanface)\n" + "  LEFT JOIN employee EMP USING (id_humanface)\n" +
                       "  LEFT JOIN link_employee_subject_group LESG USING (id_employee)\n" +
                       "  LEFT JOIN link_employee_role LER USING (id_employee)\n" + "  LEFT JOIN dic_role DR USING (id_dic_role)\n" +
                       "  LEFT JOIN module_role_department MRD USING (id_dic_role)\n" +
                       "  LEFT JOIN dic_module DM USING (id_dic_module)\n" + "  LEFT JOIN department DEP USING (id_department)\n" +
                       "  LEFT JOIN institute INST USING (id_institute)\n" +
                       " WHERE EMP.other_ad ILIKE :ldapLogin OR SC.ldap_login ILIKE :ldapLogin\n" +
                       " GROUP BY idHum, DR.id_dic_role, LER.id_link_employee_role, id_module_role_department, DM.id_dic_module, DEP.id_department, INST.id_institute\n" +
                       " ORDER BY idHum desc, LER.id_link_employee_role, DM.name";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("idHum", LongType.INSTANCE)
                              .addScalar("student")
                              .addScalar("roleName")
                              .addScalar("readonly")
                              .addScalar("formofstudy")
                              .addScalar("imagePath")
                              .addScalar("type")
                              .addScalar("teacher")
                              .addScalar("moduleName")
                              .addScalar("url")
                              .addScalar("idModule", LongType.INSTANCE)
                              .addScalar("fulltitle")
                              .addScalar("shorttitle")
                              .addScalar("idDepartment", LongType.INSTANCE)
                              .addScalar("idInstitute", LongType.INSTANCE)
                              .addScalar("idChair", LongType.INSTANCE)
                              .addScalar("institute")
                              .addScalar("startPage")
                              .addScalar("idInstituteMine", LongType.INSTANCE)
                              .addScalar("groupname")
                              .addScalar("groupLeader", NumericBooleanType.INSTANCE)
                              .addScalar("formofstudystudent")
                              .setResultTransformer(Transformers.aliasToBean(UserRoleModuleESOmodel.class));
        q.setString("ldapLogin", ldapLogin);
        return (List<UserRoleModuleESOmodel>) getList(q);
    }

    public List<UserRoleModuleESOmodel> getUserRoleModuleModelForParent (String login, String password) {

        String query = "SELECT\n" + "\tPA.id_parent AS idParent, PA.family||' '||PA.name||' '||PA.patronymic AS fio,\n" +
                       "\tPA.start_page AS startPage, TRUE AS parent, SC.id_humanface AS idHum\n" + "FROM\n" + "\tparent PA \n" +
                       "\tLEFT JOIN studentcard SC USING (id_studentcard)\n" + "WHERE\n" +
                       "\tPA.username ILIKE :login AND PA.password ILIKE :password\n";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("startPage")
                              .addScalar("parent")
                              .addScalar("idHum", LongType.INSTANCE)
                              .addScalar("idParent", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(UserRoleModuleESOmodel.class));
        q.setString("login", login).setString("password", password);
        return (List<UserRoleModuleESOmodel>) getList(q);
    }

    public boolean setVisitedModuleByUser (Long idHum, Long idModule) {
        String query = "INSERT INTO link_humanface_module(id_humanface, id_dic_module) VALUES (:idHum, :idModule)";
        Query q = getSession().createSQLQuery(query).setLong("idModule", idModule).setLong("idHum", idHum);
        return executeUpdate(q);
    }

    public Integer getQualification (Long idHum) {
        String query = "SELECT CU.qualification FROM\n" + "humanface HF\n" + "INNER JOIN studentcard SC USING (id_humanface)\n" +
                       "INNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                       "INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "INNER JOIN semester SE USING (id_semester)\n" + "INNER JOIN dic_group DG USING (id_dic_group)\n" +
                       "INNER JOIN curriculum CU USING (id_curriculum)\n" + "WHERE HF.id_humanface=:id_hum AND SE.is_current_sem=1";
        Query q = getSession().createSQLQuery(query);
        q.setLong("id_hum", idHum);
        List<Integer> list = (List<Integer>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Integer getStudentSemesterStatus(String fio, String groupname) {
        String query = "SELECT id_student_semester_status AS id\n" +
                "\tFROM student_semester_status SSS\n" +
                "    INNER JOIN studentcard SC using(id_studentcard)\n" +
                "\tINNER JOIN humanface HF using(id_humanface)\n" +
                "    INNER JOIN link_group_semester LGS USING(id_link_group_semester)\n" +
                "\tINNER JOIN semester S using(id_semester)\n" +
                "    INNER JOIN dic_group DG using(id_dic_group) \n" +
                "\tWHERE family||' '||name||' '||patronymic ILIKE '%" + fio + "%'\n" +
                "\tAND groupname = '" + groupname + "' AND SSS.is_deducted = 0 AND S.is_current_sem = 1";
        Query q = getSession().createSQLQuery(query);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0).intValue();
    }

    public Integer checkStudentIsAcademicLeave(String fio, String groupname) {
        String query = "SELECT SSS.is_academicleave\n" +
                "\tFROM student_semester_status SSS\n" +
                "    INNER JOIN studentcard SC using(id_studentcard)\n" +
                "\tINNER JOIN humanface HF using(id_humanface)\n" +
                "    INNER JOIN link_group_semester LGS USING(id_link_group_semester)\n" +
                "\tINNER JOIN semester S using(id_semester)\n" +
                "    INNER JOIN dic_group DG using(id_dic_group) \n" +
                "\tWHERE family||' '||name||' '||patronymic ILIKE '%" + fio + "%'\n" +
                "\tAND groupname = '"+ groupname + "'\n" +
                "\tORDER BY  S.id_semester";
        Query q = getSession().createSQLQuery(query);
        List<Integer> list = (List<Integer>) getList(q);
        return list.size() == 0 ? 0 : list.get(list.size()-1);
    }
}