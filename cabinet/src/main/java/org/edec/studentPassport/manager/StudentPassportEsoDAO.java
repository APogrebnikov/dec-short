package org.edec.studentPassport.manager;

import org.edec.dao.DAO;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.studentPassport.model.dao.RatingEsoModel;
import org.edec.studentPassport.model.filter.StudentPassportFilter;
import org.edec.utility.constants.FormOfStudy;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;


public class StudentPassportEsoDAO extends DAO {

    public StudentStatusModel getStudentByScId(Long studentCardId) {
        String scId = "%%";
        if (studentCardId != null) {
            scId = studentCardId.toString();
        }
        String query = "SELECT\n" +
                "\tHF.family, HF.name, HF.patronymic, SC.recordBook, max(LGS.course) AS course, \n" +
                "\tDG.groupname, max(SSS.is_academicleave) = 1 AS academicLeave, max(SSS.is_deducted) = 1 AS deducted,\n" +
                "\tMAX(SSS.is_trustagreement)= 1 AS trustAgreement, MAX(SSS.is_government_financed) = 1 AS governmentFinanced,\n" +
                "\tMAX(SSS.id_student_semester_status) AS idSSS, HF.email, HF.birthday, HF.id_humanface AS idHum, \tDG.id_dic_group AS idDG, SC.id_studentcard AS idStudentCard,\n" +
                "\tHF.foreigner, SE.id_institute AS idInstitute, SC.other_dbuid AS mineId\n" +
                "FROM\n" +
                "\thumanface HF\n" +
                "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                "\tLEFT JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tLEFT JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tLEFT JOIN semester SE USING (id_semester)\n" +
                "\tLEFT JOIN dic_group DG USING (id_dic_group) \n" +
                "WHERE\n" +
                "\tCAST(SC.id_studentcard AS TEXT) ILIKE :studentcard\n" +
                "GROUP BY\n" +
                "\tHF.id_humanface, SC.id_studentcard, DG.id_dic_group, SE.id_institute, LGS.id_semester \n" +
                "ORDER BY\n" +
                "\tHF.family, HF.name, LGS.id_semester";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family").addScalar("name").addScalar("patronymic")
                .addScalar("recordBook").addScalar("groupname")
                .addScalar("course")
                .addScalar("academicLeave").addScalar("deducted")
                .addScalar("birthday")
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("email").addScalar("mineId", LongType.INSTANCE)
                .addScalar("trustAgreement")
                .addScalar("governmentFinanced")
                .addScalar("idHum", LongType.INSTANCE)
                .addScalar("idDG", LongType.INSTANCE)
                .addScalar("idStudentCard", LongType.INSTANCE)
                .addScalar("foreigner")
                .addScalar("idInstitute", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(StudentStatusModel.class));
        q.setParameter("studentcard", scId);
        List<?> list = getList(q);
        return list.size() == 0 ? null : (StudentStatusModel) list.get(0);
    }

    public List<StudentStatusModel> getStudentByFilter(StudentPassportFilter filter) {

        String instCondition = "";

        if (filter.getInst() != null && filter.getInst().getIdInst() != null) {
            instCondition = "  and sem.id_institute = " + filter.getInst().getIdInst() + "\n";
        }

        String formOfStudyCondition = "";

        if (filter.getFormOfStudy() != null) {
            formOfStudyCondition = "  and sem.formofstudy in " +
                    "" + filter.getFormOfStudy().getMineTypes().toString()
                    .replace("[", "(")
                    .replace("]", ")");
        }

        String debtorsCondition = "";

        if (filter.isOnlyDebtors()) {
            debtorsCondition = "  and sc.is_debtor = true\n";
        }

        String query = "with searchMaxSss as (select max(sss.id_student_semester_status) as idSss, lgs.id_dic_group as idDg\n" +
                "    from student_semester_status sss\n" +
                "      inner join link_group_semester lgs using (id_link_group_semester)\n" +
                "    group by sss.id_studentcard, lgs.id_dic_group\n" +
                ")\n" +
                "select hf.family, hf.name, hf.patronymic, hf.sex, hf.email, hf.birthday, hf.id_humanface as idHum, hf.foreigner,\n" +
                "  dg.id_dic_group as idDG, dg.groupname, lgs.course,\n" +
                "  sc.id_current_dic_group as currentGroupId, sc.id_studentcard as idStudentCard, sc.other_dbuid as mineId,\n" +
                "  sc.recordBook, sc.is_debtor as isDebtor,\n" +
                "  sss.is_academicleave = 1 as academicLeave, sss.is_deducted = 1 as deducted,\n" +
                "  sss.is_educationcomplete = 1 as educationcomplite, sss.is_trustagreement = 1 as trustAgreement,\n" +
                "  sss.is_government_financed = 1 as governmentFinanced, sss.id_student_semester_status as idSSS,\n" +
                "  sem.id_semester as idSemester, sem.id_institute as idInstitute\n" +
                "from humanface hf\n" +
                "  inner join studentcard sc using (id_humanface)\n" +
                "  inner join student_semester_status sss using (id_studentcard)\n" +
                "  inner join link_group_semester lgs using (id_link_group_semester)\n" +
                "  inner join semester sem using (id_semester)\n" +
                "  inner join dic_group dg using (id_dic_group)\n" +
                "  inner join searchMaxSss maxSss on dg.id_dic_group = maxSss.idDg and sss.id_student_semester_status = maxSss.idSss\n" +
                "where 1 = 1\n" +
                "  and hf.family||' '||hf.name||' '||hf.patronymic ilike :fio\n" +
                "  and sc.recordbook ilike :recordBook\n" +
                "  and dg.groupname ilike :groupname\n" +
                debtorsCondition + instCondition + formOfStudyCondition +
                " order by HF.family, HF.name";

        Query q = getSession().createSQLQuery(query)
                .addScalar("family").addScalar("name").addScalar("patronymic").addScalar("sex")
                .addScalar("email").addScalar("birthday").addScalar("idHum").addScalar("foreigner")
                .addScalar("idDG").addScalar("groupname").addScalar("course")
                .addScalar("currentGroupId").addScalar("idStudentCard").addScalar("mineId").addScalar("recordBook").addScalar("isDebtor")
                .addScalar("academicLeave").addScalar("deducted").addScalar("idSSS").addScalar("educationcomplite")
                .addScalar("trustAgreement").addScalar("governmentFinanced")
                .addScalar("idInstitute").addScalar("idSemester")
                .setResultTransformer(Transformers.aliasToBean(StudentStatusModel.class));
        q.setParameter("fio", "%" + filter.getFio() + "%")
                .setParameter("recordBook", "%" + filter.getRecordBook() + "%")
                .setParameter("groupname", "%" + filter.getGroupName() + "%");
        return (List<StudentStatusModel>) getList(q);
    }

    public List<StudentStatusModel> getStudentByFilterWithNull(String fio, String recordBook, String groupname) {
        String query = "SELECT INQUERY.*, \n" +
                "\tSSSO.is_academicleave = 1 AS academicLeave, \n" +
                "\tSSSO.is_deducted = 1 AS deducted,\n" +
                "\tSSSO.is_educationcomplete=1 AS educationcomplite,\n" +
                "\tSSSO.is_trustagreement= 1 AS trustAgreement, \n" +
                "\tSSSO.is_government_financed = 1 AS governmentFinanced \n" +
                "\tFROM \n" +
                "(SELECT\n" +
                "\tHF.family, HF.name, HF.patronymic, HF.sex, SC.id_current_dic_group AS currentGroupId, DG.id_dic_group AS groupId, SC.recordBook, \n" +
                "\tDG.groupname, \n" +
                "\tmax(LGS.course) AS course, \n" +
                "\tMAX(SSS.id_student_semester_status) AS idSSS, \n" +
                "\tHF.email, HF.birthday, HF.id_humanface AS idHum, \t\n" +
                "\tDG.id_dic_group AS idDG, SC.id_studentcard AS idStudentCard, SC.other_dbuid AS mineId,\n" +
                "\tHF.foreigner, SE.id_institute AS idInstitute, MAX(SE.id_semester) AS idSemester\n" +
                "\tFROM humanface HF\n" +
                "\tLEFT JOIN studentcard SC USING (id_humanface)\n" +
                "\tLEFT JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tLEFT JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tLEFT JOIN semester SE USING (id_semester)\n" +
                "\tLEFT JOIN dic_group DG USING (id_dic_group) \n" +
                "WHERE HF.family||' '||HF.name||' '||HF.patronymic ILIKE :fio\n" +
                "\tAND SC.recordBook ILIKE :recordBook\n" +
                "\tAND (DG.groupname ILIKE :groupname OR DG.groupname IS NULL)\n" +
                "GROUP BY HF.id_humanface, SC.id_studentcard, DG.id_dic_group, SE.id_institute\n" +
                "ORDER BY HF.family, HF.name) AS INQUERY\n" +
                "INNER JOIN student_semester_status SSSO ON SSSO.id_student_semester_status = INQUERY.idSSS\n";
        /*
        String query = "SELECT\n" + "\tHF.family, HF.name, HF.patronymic, SC.id_current_dic_group AS currentGroupId, DG.id_dic_group AS groupId, SC.recordBook, max(LGS.course) AS course, \n" +
                "\tDG.groupname, max(SSS.is_academicleave) = 1 AS academicLeave, max(SSS.is_deducted) = 1 AS deducted,\n" +
                "\tMAX(SSS.is_educationcomplete)=1 AS educationcomplite,\n" +
                "\tMAX(SSS.is_trustagreement)= 1 AS trustAgreement, MAX(SSS.is_government_financed) = 1 AS governmentFinanced,\n" +
                "\tMAX(SSS.id_student_semester_status) AS idSSS, HF.email, HF.birthday, HF.id_humanface AS idHum, " +
                "\tDG.id_dic_group AS idDG, SC.id_studentcard AS idStudentCard, SC.other_dbuid AS mineId,\n" +
                "\tHF.foreigner, SE.id_institute AS idInstitute, MAX(SE.id_semester) AS idSemester\n" +
                "\tFROM\n" + "\thumanface HF\n" +
                "\tLEFT JOIN studentcard SC USING (id_humanface)\n" +
                "\tLEFT JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tLEFT JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tLEFT JOIN semester SE USING (id_semester)\n" +
                "\tLEFT JOIN dic_group DG USING (id_dic_group) \n" +
                "WHERE\n" +
                "\tHF.family||' '||HF.name||' '||HF.patronymic ILIKE :fio\n" +
                "\tAND SC.recordBook ILIKE :recordBook\n" +
                "\tAND (DG.groupname ILIKE :groupname OR DG.groupname IS NULL)\n" +
                "GROUP BY\n" +
                "\tHF.id_humanface, SC.id_studentcard, DG.id_dic_group, SE.id_institute\n" +
                "ORDER BY\n" +
                "\tHF.family, HF.name";
                */
        Query q = getSession().createSQLQuery(query)
                .addScalar("family").addScalar("name").addScalar("sex")
                .addScalar("currentGroupId", LongType.INSTANCE)
                .addScalar("patronymic")
                .addScalar("recordBook")
                .addScalar("groupname")
                .addScalar("course")
                .addScalar("academicLeave")
                .addScalar("deducted")
                .addScalar("birthday")
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("email")
                .addScalar("educationcomplite")
                .addScalar("trustAgreement")
                .addScalar("governmentFinanced")
                .addScalar("idHum", LongType.INSTANCE)
                .addScalar("idDG", LongType.INSTANCE)
                .addScalar("idStudentCard", LongType.INSTANCE)
                .addScalar("foreigner").addScalar("mineId", LongType.INSTANCE)
                .addScalar("idInstitute", LongType.INSTANCE)
                .addScalar("idSemester", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(StudentStatusModel.class));
        q.setParameter("fio", "%" + fio + "%")
                .setParameter("recordBook", "%" + recordBook + "%")
                .setParameter("groupname", "%" + groupname + "%");
        return (List<StudentStatusModel>) getList(q);
    }

    public List<StudentStatusModel> getStudentsByFilterDetail(String fio, String recordBook, String groupname) {
        String query = "SELECT HF.family, HF.name, HF.patronymic, HF.sex, SC.id_current_dic_group AS currentGroupId,\n" +
                "\tSC.recordBook, LGS.course AS course, \n" +
                "\tDG.groupname, SSS.is_academicleave = 1 AS academicLeave, SSS.is_deducted = 1 AS deducted,\n" +
                "\tSSS.is_educationcomplete = 1 AS educationcomplite,\n" +
                "\tSSS.is_trustagreement = 1 AS trustAgreement, SSS.is_government_financed = 1 AS governmentFinanced,\n" +
                "\tSSS.id_student_semester_status AS idSSS, HF.email, HF.birthday, HF.id_humanface AS idHum, " +
                "\tDG.id_dic_group AS idDG, SC.id_studentcard AS idStudentCard, SC.other_dbuid AS mineId,\n" +
                "\tHF.foreigner, SE.id_institute AS idInstitute, SE.id_semester AS idSemester\n" +
                "\tFROM humanface HF\n" +
                "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SE USING (id_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group) \n" +
                "WHERE HF.family||' '||HF.name||' '||HF.patronymic ILIKE :fio\n" + "\tAND SC.recordBook ILIKE :recordBook\n" +
                "\tAND DG.groupname ILIKE :groupname\n" +
                "ORDER BY HF.family, HF.name, SC.id_studentcard DESC, LGS.id_semester DESC";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family").addScalar("name").addScalar("sex")
                .addScalar("currentGroupId", LongType.INSTANCE)
                .addScalar("patronymic").addScalar("mineId", LongType.INSTANCE)
                .addScalar("recordBook")
                .addScalar("groupname")
                .addScalar("course")
                .addScalar("academicLeave")
                .addScalar("deducted")
                .addScalar("birthday")
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("email")
                .addScalar("educationcomplite")
                .addScalar("trustAgreement")
                .addScalar("governmentFinanced")
                .addScalar("idHum", LongType.INSTANCE)
                .addScalar("idDG", LongType.INSTANCE)
                .addScalar("idStudentCard", LongType.INSTANCE)
                .addScalar("foreigner")
                .addScalar("idInstitute", LongType.INSTANCE)
                .addScalar("idSemester", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(StudentStatusModel.class));
        q.setParameter("fio", "%" + fio + "%")
                .setParameter("recordBook", "%" + recordBook + "%")
                .setParameter("groupname", "%" + groupname + "%");
        return (List<StudentStatusModel>) getList(q);
    }

    public boolean saveStudent(StudentStatusModel studentStatusModel) {
        try {
            begin();
            String query = "UPDATE humanface SET family = :family, name = :name, patronymic = :patronymic, " +
                    "email = :email, birthday = :birthday, foreigner = :foreigner \n" + "WHERE id_humanface = :idHum";
            Query q = getSession().createSQLQuery(query);
            q.setParameter("family", studentStatusModel.getFamily())
                    .setParameter("name", studentStatusModel.getName())
                    .setParameter("patronymic", studentStatusModel.getPatronymic())
                    .setParameter("email", studentStatusModel.getEmail())
                    .setDate("birthday", studentStatusModel.getBirthday())
                    .setParameter("idHum", studentStatusModel.getIdHum())
                    .setBoolean("foreigner", studentStatusModel.getForeigner());
            q.executeUpdate();
            String queryStudentcard = "UPDATE studentcard SET recordBook = :recordBook, is_debtor = :isDebtor WHERE id_studentcard = :idStudentcard";
            q = getSession().createSQLQuery(queryStudentcard);
            q.setParameter("recordBook", studentStatusModel.getRecordBook())
                    .setParameter("idStudentcard", studentStatusModel.getIdStudentCard())
                    .setBoolean("isDebtor", studentStatusModel.getIsDebtor());
            q.executeUpdate();
            commit();
            return true;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return false;
        } finally {
            close();
        }
    }

    public List<RatingEsoModel> getRatingByIdHumAndDigGroup(Long idHum, Long idDG) {
        String query = "SELECT \n" +
                "\tSR.is_exam = 1 AS exam, " +
                       "SR.is_pass = 1 AS pass, " +
                       "SR.is_courseproject = 1 AS cp, " +
                       "SR.is_coursework = 1 AS cw, " +
                       "SR.is_practic = 1 AS practic,\n" +
                "\tSR.examrating, " +
                       "SR.passrating, " +
                       "SR.courseprojectrating AS cprating, " +
                       "SR.courseworkrating AS cwrating, SR.practicrating,\n" +
                "\tDS.subjectname,\n" +
                       "subject.type,\n" +
                       "\tCASE\n" +
                "\t\tWHEN SEM.season = 0 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend)||' (осень)'\n" +
                "\t\tWHEN SEM.season = 1 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend)||' (весна)'\n" +
                "\tEND AS semester\n" +
                       "FROM\n" + "\tstudentcard SC\n" +
                "\tINNER JOIN student_semester_status USING (id_studentcard)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SEM USING (id_semester)\n" + "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" +
                       "\tINNER JOIN subject USING (id_subject)\n" +
                "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "WHERE\n" +
                "\tSC.id_humanface = " + idHum +
                " AND LGS.id_dic_group = " + idDG;
        Query q = getSession().createSQLQuery(query)
                .addScalar("exam")
                .addScalar("pass")
                .addScalar("cp")
                .addScalar("cw")
                .addScalar("practic")
                .addScalar("examrating")
                .addScalar("passrating")
                .addScalar("cprating")
                .addScalar("cwrating")
                .addScalar("practicrating")
                .addScalar("subjectname")
                .addScalar("semester")
                .addScalar("type")
                .setResultTransformer(Transformers.aliasToBean(RatingEsoModel.class));

        return (List<RatingEsoModel>) getList(q);
    }
}
