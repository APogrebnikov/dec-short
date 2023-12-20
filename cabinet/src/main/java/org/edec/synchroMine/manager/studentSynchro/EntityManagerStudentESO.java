package org.edec.synchroMine.manager.studentSynchro;

import org.edec.dao.DAO;
import org.edec.synchroMine.model.StudentStatusModel;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.edec.utility.component.model.StudentModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import java.util.Date;
import java.util.List;


public class EntityManagerStudentESO extends DAO {
    public List<org.edec.synchroMine.model.eso.StudentModel> getStudentsCurrent(Long idInst, Long idLGS) {
        String query = "SELECT HF.family||' '||HF.name||' '||HF.patronymic AS fio, SC.other_dbuid AS idMineStudentcard, \n" +
                "\tSC.id_studentcard AS idStudentcard, SC.recordbook, SSS.id_student_semester_status AS idSSS,\n" + "\tCASE\n" +
                "\t\tWHEN SSS.is_educationcomplete = 1 THEN 4\n" + "\t\tWHEN SSS.is_deducted = 1 THEN 3\n" +
                "\t\tWHEN SSS.is_academicleave = 1 THEN -1\n" + "\tELSE 1 END AS idStatus,\n" + "\tCASE\n" +
                "\t\tWHEN SSS.is_government_financed = 1 AND SSS.is_trustagreement = 1 THEN 'ЦН'\n" +
                "\t\tWHEN SSS.is_government_financed = 1 THEN 'ОО'\n" + "\tELSE 'СН' END AS reasonStudy\n" + "FROM\n" +
                "\thumanface HF\n" + "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SEM USING (id_semester)\n" + "WHERE\n" +
                "\tSEM.is_current_sem = 1 AND SSS.is_deducted = 0 AND SSS.is_academicleave = 0\n" +
                "\tAND SEM.id_institute = :idInst\n" + (idLGS != null ? "\tAND LGS.id_link_group_semester = " + idLGS + "\n" : "") +
                "ORDER BY\n" + "\tFIO";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("idStudentcard", LongType.INSTANCE)
                .addScalar("recordbook")
                .addScalar("idMineStudentcard", LongType.INSTANCE)
                .addScalar("idStatus")
                .addScalar("reasonStudy")
                .addScalar("idSSS", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(org.edec.synchroMine.model.eso.StudentModel.class));
        q.setLong("idInst", idInst);
        return (List<org.edec.synchroMine.model.eso.StudentModel>) getList(q);
    }

    public List<GroupMineModel> getCurrentGroups(Long idInst) {
        String query = "SELECT DG.groupname, DG.id_dic_group AS idDG, LGS.id_link_group_semester AS idLGS, \n" +
                "\tLGS.course, LGS.otherdbid AS idGroupMine\n" +
                "FROM dic_group DG\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_dic_group)\n" +
                "\tINNER JOIN semester SEM USING (id_semester)\n" +
                "WHERE SEM.is_current_sem = 1 AND SEM.id_institute = :idInst\n" +
                "ORDER BY DG.groupname";
        Query q = getSession().createSQLQuery(query)
                .addScalar("groupname").addScalar("idDG", LongType.INSTANCE)
                .addScalar("idLGS", LongType.INSTANCE).addScalar("course")
                .addScalar("idGroupMine", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(GroupMineModel.class));
        q.setLong("idInst", idInst);
        return (List<GroupMineModel>) getList(q);
    }

    public List<StudentModel> getStudentsByGroupName(Long idLGS) {
        String query = "WITH student AS (SELECT\n" +
                "\t\tHF.family, HF.name, HF.patronymic, HF.id_humanface AS idHum, SC.other_dbuid AS idStudCardMine,\n" +
                "\t\tSC.id_studentcard AS idStudCard, SC.recordbook, MAX(SSS.id_student_semester_status) AS idSSS,\n" +
                "\t\tDG.groupname\n" + "\tFROM\n" + "\t\tdic_group DG\n" +
                "\t\tINNER JOIN link_group_semester LGS USING (id_dic_group)\n" +
                "\t\tINNER JOIN student_semester_status SSS USING (id_link_group_semester)\n" +
                "\t\tINNER JOIN studentcard SC USING (id_studentcard)\n" + "\t\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "\tWHERE\n" + "\t\tLGS.id_link_group_semester = :idLGS\n" + "\tGROUP BY\n" +
                "\t\tHF.id_humanface, SC.id_studentcard, DG.id_dic_group\n" + "\tORDER BY\n" +
                "\t\tHF.family, HF.name, HF.patronymic)\n" + "SELECT \n" + "\tstudent.*,\n" + "\tCASE\n" +
                "\t\tWHEN SSS.is_educationcomplete = 1 THEN 4\n" + "\t\tWHEN SSS.is_deducted = 1 THEN 3\n" +
                "\t\tWHEN SSS.is_academicleave = 1 THEN -1\n" + "\tELSE 1 END AS status\n" + "FROM\n" +
                "\tstudent_semester_status SSS\n" + "\tINNER JOIN student ON SSS.id_student_semester_status = student.idSSS";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family")
                .addScalar("name")
                .addScalar("patronymic")
                .addScalar("idHum", LongType.INSTANCE)
                .addScalar("idStudCardMine", LongType.INSTANCE)
                .addScalar("idStudCard", LongType.INSTANCE)
                .addScalar("recordbook")
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("status")
                .addScalar("groupname")
                .setResultTransformer(Transformers.aliasToBean(StudentModel.class));
        q.setLong("idLGS", idLGS);
        return (List<StudentModel>) getList(q);
    }

    public List<StudentModel> getStudentsByFioOrStudCard(String fio, String recordbook, Long idStudCardMine) {
        String query = "WITH searchStudent AS (\n" + "\tSELECT\n" + "\t\tHF.family, HF.name, HF.patronymic, HF.id_humanface AS idHum, \n" +
                "\t\tSC.id_studentcard AS idStudCard, SC.other_dbuid AS idStudCardMine, SC.recordbook,\n" +
                "\t\tMAX(SSS.id_student_semester_status) AS idSSS, MAX(LGS.semesternumber) AS semester,\n" + "\t\tDG.groupname\n" +
                "\tFROM\n" + "\t\thumanface HF\n" + "\t\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                "\t\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\t\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\t\tINNER JOIN dic_group DG USING (id_dic_group)\n" + "\tWHERE\n" +
                "\t\tHF.family||' '||HF.name||' '||HF.patronymic LIKE :fio\n" + "\t\tOR SC.recordbook LIKE :recordbook\n" +
                "\t\tOR CAST(SC.other_dbuid AS TEXT) ILIKE :idStudCardMine\n" + "\tGROUP BY\n" +
                "\t\tHF.id_humanface, SC.id_studentcard, DG.id_dic_group)\n" + "SELECT\n" + "\tST.*,\n" + "\tCASE\n" +
                "\t\tWHEN SSS.is_deducted = 1 THEN 3\n" + "\t\tWHEN SSS.is_educationcomplete = 1 THEN 4\n" +
                "\t\tWHEN SSS.is_academicleave = 1 THEN -1\n" + "\tELSE 1 END AS status,\n" +
                "\tCASE\n" +
                "\t\tWHEN SSS.is_government_financed = 1 THEN 1\n" +
                "\t\tWHEN SSS.is_trustagreement = 1 THEN 2\n" +
                "\t\tWHEN SSS.is_government_financed = 0 THEN 3\n" + "\tELSE 0 END AS condOfEducation\n" + "FROM\n" +
                "\tstudent_semester_status SSS\n" + "\tINNER JOIN searchStudent ST ON SSS.id_student_semester_status = ST.idSSS\n" +
                "ORDER BY\n" + "\tfamily, name, patronymic, groupname";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family")
                .addScalar("name")
                .addScalar("patronymic")
                .addScalar("idHum", LongType.INSTANCE)
                .addScalar("idStudCardMine", LongType.INSTANCE)
                .addScalar("idStudCard", LongType.INSTANCE)
                .addScalar("recordbook")
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("status")
                .addScalar("groupname")
                .addScalar("condOfEducation")
                .setResultTransformer(Transformers.aliasToBean(StudentModel.class));
        q.setString("fio", fio).setString("recordbook", recordbook).setString("idStudCardMine", String.valueOf(idStudCardMine));
        return (List<StudentModel>) getList(q);
    }

    public List<StudentModel> getHumansByFio(String fio) {

        String query = "SELECT HF.family, HF.name, HF.patronymic, HF.id_humanface AS idHum\n" +
                "FROM humanface HF\n" +
                "WHERE HF.family||' '||HF.name||' '||HF.patronymic LIKE :fio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family").addScalar("name").addScalar("patronymic")
                .addScalar("idHum", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(StudentModel.class));
        q.setString("fio", fio);
        return (List<StudentModel>) getList(q);

    }

    public Long createStudent(String groupname, String family, String name, String patronymic, Date birthday, String recordbook, Integer sex,
                              Long idStudentMine, Long idHum) {
        try {
            String query = "SELECT * FROM create_student_in_group(:groupname, :family, :name, :patronymic, :birthday, :recordbook, :sex, :idstudmine, :idHum)";
            Query q = getSession().createSQLQuery(query)
                    .addScalar("create_student_in_group", LongType.INSTANCE)
                    .setParameter("groupname", groupname, StringType.INSTANCE)
                    .setParameter("family", family, StringType.INSTANCE)
                    .setParameter("name", name, StringType.INSTANCE)
                    .setParameter("patronymic", patronymic, StringType.INSTANCE)
                    .setParameter("birthday", birthday, DateType.INSTANCE)
                    .setParameter("recordbook", recordbook, StringType.INSTANCE)
                    .setParameter("sex", sex, IntegerType.INSTANCE)
                    .setParameter("idstudmine", idStudentMine, LongType.INSTANCE)
                    .setParameter("idHum", idHum, LongType.INSTANCE);
            List<?> resList = getList(q);
            if (resList != null && resList.size() > 0) {
                Long id = (Long) resList.get(0);
                return id;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Long createStudentByEmployeeHum(String recordbook, Long idStudentMine, Long idHum) {
        String query = "SELECT * FROM create_student(null, null, null, null, :recordbook, null, :idstudmine, :idHum)";
        Query q = getSession().createSQLQuery(query)
                .addScalar("create_student", LongType.INSTANCE)
                .setParameter("recordbook", recordbook, StringType.INSTANCE)
                .setParameter("idstudmine", idStudentMine, LongType.INSTANCE)
                .setParameter("idHum", idHum, LongType.INSTANCE);
        Long id = (Long) getList(q).get(0);
        return id;
    }

    public void createSSSforStudentByGroup(Long idStudent, int trustagreement, int governmentFinanced, Integer academicLeave, Long idGroup,
                                           String groupname) {
        String query = "SELECT * FROM create_sss_by_student_group(" + idStudent + ", " + trustagreement + ", " + governmentFinanced + ", " +
                "0,"+academicLeave + ", " + idGroup + ", '" + groupname + "')";
        callFunction(query);
    }

    public void createSRforStudentByGroup(Long idStudent, Long idGroup, String groupname) {
        String query = "SELECT * FROM create_sr_by_student_group(" + idStudent + ", " + idGroup + ", '" + groupname + "')";
        callFunction(query);
    }

    public boolean updateStudentFromMine(Long idStudentcard, Long idMineStudentcard, String recordbook) {
        Query q = getSession().createSQLQuery(
                "UPDATE studentcard \n" + "SET other_dbuid = " + idMineStudentcard + ",\n" + "recordbook = " + recordbook + "\n" +
                        "WHERE id_studentcard = " + idStudentcard);
        return executeUpdate(q);
    }

    public boolean updateSSSstudyReason(Long idSSS, Integer government, Integer trustagreement) {
        Query q = getSession().createSQLQuery(
                "UPDATE student_semester_status \n" + "SET is_trustagreement = " + trustagreement + ",\n" + "is_government_financed = " +
                        government + "\n" + "WHERE id_student_semester_status = " + idSSS);
        return executeUpdate(q);
    }

    public boolean deleteSSS(Long idSSS) {
        Query q = getSession().createSQLQuery("DELETE FROM student_semester_status WHERE id_student_semester_status = :idSSS");
        q.setLong("idSSS", idSSS);
        return executeUpdate(q);
    }

    public List<StudentStatusModel> getStudentStatusESO (Long idInst, Long idSemester){

        String query = "WITH searchStudent AS (SELECT HF.family, HF.name, HF.patronymic, HF.id_humanface AS idHum, \n" +
                "SC.id_studentcard AS idStudCard, SC.other_dbuid AS idStudCardMine, SC.recordbook,\n" +
                "MAX(SSS.id_student_semester_status) AS idSSS, MAX(LGS.semesternumber) AS semester, DG.groupname, sem.formofstudy as formOfStudy\n" +
                "\n" +
                "FROM humanface HF\n" +
                "INNER JOIN studentcard SC USING (id_humanface)\n" +
                "INNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "inner join semester sem using (id_semester)\n" +
                "INNER JOIN dic_group DG USING (id_dic_group)\n" +
                "WHERE lgs.id_semester = "+idSemester+" and sem.id_institute = "+idInst+" \n" +
                "GROUP BY  HF.id_humanface, SC.id_studentcard, DG.id_dic_group, sem.formofstudy)\n" +
                "SELECT ST.*, \n" +
                "CASE WHEN SSS.is_deducted = 1 THEN 3\n" +
                "WHEN SSS.is_educationcomplete = 1 THEN 4\n" +
                "WHEN SSS.is_academicleave = 1 THEN -1 ELSE 1 END AS status, \n" +
                "CASE\n" +
                "WHEN SSS.is_trustagreement = 1 and SSS.is_government_financed = 1 THEN 2 \n" +
                "WHEN SSS.is_government_financed = 0 THEN 3\n" +
                "WHEN SSS.is_government_financed = 1 THEN 1 end as condOfEducation, "+
                "SSS.is_government_financed as isBudget, SSS.is_trustagreement as isTrustAgreement, sss.is_transfer_student as isTransferStudent, sss.is_transfered as isTransfered\n" +
                "FROM student_semester_status SSS\n" +
                "INNER JOIN searchStudent ST ON SSS.id_student_semester_status = ST.idSSS\n" +
                "ORDER BY groupname, family, name, patronymic ";

        Query q = getSession().createSQLQuery(query)
                .addScalar("family")
                .addScalar("name")
                .addScalar("patronymic")
                .addScalar("idHum", LongType.INSTANCE)
                .addScalar("idStudCardMine", LongType.INSTANCE)
                .addScalar("idStudCard", LongType.INSTANCE)
                .addScalar("recordbook")
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("status")
                .addScalar("groupname")
                .addScalar("formOfStudy")
                .addScalar("isBudget")
                .addScalar("isTrustAgreement")
                .addScalar("condOfEducation")
                .addScalar("isTransferStudent")
                .addScalar("isTransfered")
                .setResultTransformer(Transformers.aliasToBean(StudentStatusModel.class));

        return q.list();
    }
}