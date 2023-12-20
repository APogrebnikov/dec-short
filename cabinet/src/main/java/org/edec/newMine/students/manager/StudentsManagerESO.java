package org.edec.newMine.students.manager;

import org.edec.dao.DAO;
import org.edec.newMine.students.model.StudentsModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import java.util.Date;
import java.util.List;

public class StudentsManagerESO extends DAO {
    public List<StudentsModel> getStudentsCurrent(Long idLGS) {
        String query = "WITH student AS (SELECT\n" +
                "\t\tHF.family, HF.name, HF.patronymic, HF.id_humanface AS idHum, SC.other_dbuid AS idStudCardMine,\n" +
                "\t\tSC.id_studentcard AS idStudCard, SC.recordbook, MAX(SSS.id_student_semester_status) AS idSSS,\n" +
                "\t\tDG.groupname, " +
                "\tCASE\n" +
                "\t\tWHEN SSS.is_government_financed = 1 THEN 1\n" +
                "\t\tWHEN SSS.is_trustagreement = 1 THEN 2\n" +
                "\t\tWHEN SSS.is_government_financed = 0 THEN 3\n" + "\tELSE 0 END AS condOfEducation,\n" +
                "\tSC.hash\n" +
                "\tFROM\n" + "\t\tdic_group DG\n" +
                "\t\tINNER JOIN link_group_semester LGS USING (id_dic_group)\n" +
                "\t\tINNER JOIN student_semester_status SSS USING (id_link_group_semester)\n" +
                "\t\tINNER JOIN studentcard SC USING (id_studentcard)\n" + "\t\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "\tWHERE\n" + "\t\tLGS.id_link_group_semester = :idLGS\n" +
                "\tGROUP BY\n" +
                "\t\tHF.id_humanface, SC.id_studentcard, DG.id_dic_group, SSS.is_government_financed, SSS.is_trustagreement, SSS.is_government_financed\n" +
                "\tORDER BY\n" +
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
                .addScalar("condOfEducation")
                .addScalar("hash")
                .setResultTransformer(Transformers.aliasToBean(StudentsModel.class));
        q.setLong("idLGS", idLGS);
        return (List<StudentsModel>) getList(q);
    }

    public boolean updateStudentFromMine(Long idStudentcard, Long idMineStudentcard, String recordbook) {
        Query q = getSession().createSQLQuery(
                "UPDATE studentcard \n" + "SET other_dbuid = " + idMineStudentcard + ",\n" + "recordbook = " + recordbook + "\n" +
                        "WHERE id_studentcard = " + idStudentcard);
        return executeUpdate(q);
    }

    public List<StudentsModel> getStudentsByFioOrStudCard(String fio, String recordbook, Long idStudCardMine) {
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
                .setResultTransformer(Transformers.aliasToBean(StudentsModel.class));
        q.setString("fio", fio).setString("recordbook", recordbook).setString("idStudCardMine", String.valueOf(idStudCardMine));
        return (List<StudentsModel>) getList(q);
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

    public void createSSSforStudentByGroup(Long idStudent, int trustagreement, int governmentFinanced, Integer academicLeave, Long idGroup,
                                           String groupname) {
        try {
            String query = "SELECT * FROM create_sss_by_student_group("
                    + idStudent +
                    ", " + trustagreement +
                    ", " + governmentFinanced +
                    ", 0" +
                    ", " + academicLeave +
                    ", " + idGroup +
                    ", '" + groupname + "')";
            System.out.println(">> "+query);
            callFunction(query);
        } catch (Exception e) {
            System.out.println("Не удалось создать студента с параметрами: " +
                    "(" + idStudent + "," + trustagreement + "," + governmentFinanced + "," + academicLeave + "," + idGroup + "," + groupname + ",");
        }
    }


    public void createSRforStudentByGroup(Long idStudent, Long idGroup, String groupname) {
        String query = "SELECT * FROM create_sr_by_student_group(" + idStudent + ", " + idGroup + ", '" + groupname + "')";
        callFunction(query);
    }

    public boolean deleteStudent(Long idSSS) {
        Query deleteSrh = getSession().createSQLQuery("delete from sessionratinghistory " +
                "where id_sessionrating in (select id_sessionrating from sessionrating where id_student_semester_status = " + idSSS + ")");
        Query deleteSr = getSession().createSQLQuery("delete from sessionrating where id_student_semester_status = " + idSSS);
        Query deleteAttendance = getSession().createSQLQuery("delete from attendance where id_student_semester_status = " + idSSS);
        Query deleteAttendanceGate = getSession().createSQLQuery("delete from attendance_gate where id_student_semester_status = " + idSSS);
        Query deleteLinkOrderStudentStatus = getSession().createSQLQuery("delete from link_order_student_status where id_student_semester_status = " + idSSS);
        Query deleteSSS = getSession().createSQLQuery("delete from student_semester_status where id_student_semester_status = " + idSSS);
        try {
            begin();
            deleteSrh.executeUpdate();
            deleteSr.executeUpdate();
            deleteAttendance.executeUpdate();
            deleteAttendanceGate.executeUpdate();
            deleteLinkOrderStudentStatus.executeUpdate();
            deleteSSS.executeUpdate();
            commit();
            return true;
        } catch (Exception ex) {
            rollback();
            ex.printStackTrace();
            return false;
        } finally {
            close();
        }
    }

    public boolean updateStudentStatus(Long idSSS, int isDeducted, int isAcadem, int isEducationComplete) {
        String query = "update student_semester_status set is_deducted = " + isDeducted
                + ",  is_academicleave =" + isAcadem + ", is_educationcomplete = " + isEducationComplete + " \n" +
                "where student_semester_status =  " + idSSS;
        Query q = getSession().createSQLQuery(query);
        return executeUpdate(q);
    }

    public boolean updateHash(Long idStudent, String hash) {
        String query = "UPDATE studentcard SET hash = :hash WHERE id_studentcard = :idStudent";
        Query q = getSession().createSQLQuery(query)
                .setString("hash", hash)
                .setLong("idStudent",idStudent);
        return executeUpdate(q);
    }
}
