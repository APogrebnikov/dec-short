package org.edec.reference.manager;

import org.edec.dao.DAO;
import org.edec.reference.model.ExcelReportModel;
import org.edec.reference.model.ReferenceModel;
import org.edec.reference.model.StudentModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ReferenceManager extends DAO {

    public List<StudentModel> getStudents(String tbValue, Long idInst) {
        String query = "SELECT\n" +
                "\tSC.id_studentcard as idStudentcard,\n" +
                "\tHF.id_humanface as idHumanface,\n" +
                "\tHF.family || ' ' || HF.name || ' ' || HF.patronymic AS fio,\n" +
                "\tHF.birthday as dateOfBirth,\n" +
                "\tDG.groupname as groupName,\n" +
                "\tSC.is_invalid as isInvalid,\n" +
                "\tSC.is_sirota as isOrphan,\n" +
                "\tSC.is_indigent as isIndigent,\n" +
                "\tSC.is_veteran as isVeteran,\n" +
                "\tSC.type_invalid as invalidType,\n" +
                "\tmax(SEM.id_semester) as idSemester\n" +
                "\tFROM\n" +
                "\tstudentcard SC\n" +
                "\tINNER JOIN humanface HF ON SC.id_humanface = HF.id_humanface\n" +
                "\tINNER JOIN student_semester_status SSS ON SC.id_studentcard = SSS.id_studentcard\n" +
                "\tINNER JOIN link_group_semester LGS ON SSS.id_link_group_semester = LGS.id_link_group_semester\n" +
                "\tINNER JOIN dic_group DG ON LGS.id_dic_group = DG.id_dic_group\n" +
                "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "\tWHERE SEM.id_institute = :idInstitute\n" +
                "\tAND (SSS.is_deducted = 0 and SSS.formofstudy = 1 and SSS.is_government_financed = 1) AND HF.family ilike '%" + tbValue + "%' \n" +
                "\tAND (SSS.id_student_semester_status = (SELECT max(id_student_semester_status) FROM student_semester_status SSS2 \n" +
                "\tWHERE SSS.id_studentcard = SSS2.id_studentcard) \n" +
                "\tor SEM.is_current_sem = 1)\n" +
                "\tGROUP BY sc.id_studentcard, hf.id_humanface, family, name, patronymic, birthday, groupname, sc.is_invalid, sc.is_sirota, sc.is_indigent, sc.type_invalid\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idStudentcard", LongType.INSTANCE)
                .addScalar("idHumanface", LongType.INSTANCE)
                .addScalar("fio")
                .addScalar("dateOfBirth")
                .addScalar("groupName")
                .addScalar("isInvalid", BooleanType.INSTANCE)
                .addScalar("isOrphan", BooleanType.INSTANCE)
                .addScalar("isIndigent", BooleanType.INSTANCE)
                .addScalar("isVeteran", BooleanType.INSTANCE)
                .addScalar("invalidType")
                .addScalar("idSemester", LongType.INSTANCE)
                .setParameter("idInstitute", idInst)
                .setResultTransformer(Transformers.aliasToBean(StudentModel.class));
        return (List<StudentModel>) getList(q);
    }

    public List<ReferenceModel> getReferences(long idStudentcard) {
        String query = "SELECT\n" + "\tRF.id_reference AS idRef,\n" + "\tRF.id_reference_subtype AS refType,\n" +
                "\tRF.book_number AS booknumber,\n" + "\tRF.date_start AS dateStart,\n" + "\tRF.date_finish AS dateFinish,\n" +
                "\tRF.date_get AS dateGet,\n" + "\tRF.url AS url\n" + "\tFROM\n" + "\treference RF\n" +
                "\tWHERE RF.id_studentcard = :idStudentcard\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idRef", LongType.INSTANCE)
                .addScalar("refType", LongType.INSTANCE)
                .addScalar("booknumber")
                .addScalar("url")
                .addScalar("dateStart")
                .addScalar("dateFinish")
                .addScalar("dateGet")
                .setParameter("idStudentcard", idStudentcard)
                .setResultTransformer(Transformers.aliasToBean(ReferenceModel.class));
        return (List<ReferenceModel>) getList(q);
    }

    public boolean updateStudentStatus(long idStudentcard, boolean isOrphan, boolean isInvalid, boolean isVeteran, int typeInvalid, boolean isIndigent) {
        String query =
                "UPDATE studentcard \n"
                        + "SET is_sirota = :isOrphan,\n"
                        + "is_invalid = :isInvalid,\n"
                        + "is_indigent = :isIndigent,\n"
                        + "is_veteran = :isVeteran,\n"
                        + "type_invalid = :typeInvalid\n"
                        + "WHERE id_studentcard = :idStudentcard\n" + "\n";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("isOrphan", isOrphan ? 1 : 0)
                .setParameter("isInvalid", isInvalid ? 1 : 0)
                .setParameter("isIndigent", isIndigent ? 1 : 0)
                .setParameter("isVeteran", isVeteran ? 1 : 0)
                .setParameter("typeInvalid", typeInvalid)
                .setParameter("idStudentcard", idStudentcard);
        return executeUpdate(q);
    }

    public boolean updateStudentDateOfBirth(long idHumanface, Date dateOfBirth) {
        String query = "UPDATE humanface \n" + "SET birthday = :dateOfBirth\n" + "WHERE id_humanface = :idHumanface\n" + "\n";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idHumanface", idHumanface).setParameter("dateOfBirth", dateOfBirth);
        return executeUpdate(q);
    }

    public Long createReference(ReferenceModel reference) {
        String query = "INSERT INTO reference (id_reference_subtype, id_studentcard, book_number, date_start, date_finish, date_get, url)" +
                "VALUES (:idRefType, :idSc, :bookNumber, :dateOfStart, :dateOfFinish, :dateOfGet, :url)\n" +
                "RETURNING id_reference";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idRefType", reference.getRefType())
                .setParameter("idSc", reference.getIdStudentcard())
                .setParameter("bookNumber", reference.getBooknumber())
                .setParameter("bookNumber", reference.getBooknumber())
                .setDate("dateOfStart", reference.getDateStart())
                .setDate("dateOfFinish", reference.getDateFinish())
                .setDate("dateOfGet", reference.getDateGet())
                .setParameter("url", reference.getUrl());
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public boolean updateReference(ReferenceModel reference) {
        String query =
                "UPDATE reference \n" + "SET book_number = :bookNumber," + " date_start = :dateStart," + " date_finish = :dateFinish," +
                        " url = :url\n" + "WHERE id_reference = :idRef\n" + "\n";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idRef", reference.getIdRef())
                .setParameter("bookNumber", reference.getBooknumber())
                .setDate("dateStart", reference.getDateStart())
                .setDate("dateFinish", reference.getDateFinish())
                .setParameter("url", reference.getUrl());
        return executeUpdate(q);
    }

    public boolean deleteReference(long idRef) {
        String query = "DELETE FROM reference WHERE id_reference = :idRef";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idRef", idRef);
        return executeUpdate(q);
    }

    public List<ExcelReportModel> getExcelReport() {
        String query = "SELECT DISTINCT \n" +
                "\t  HF.family || ' ' || HF.name || ' ' || HF.patronymic AS fio, \n" +
                "\t  CASE \n" +
                "\t\t  WHEN SC.is_invalid = 1 THEN 'Инвалиды' \n" +
                "\t\t  WHEN SC.is_sirota = 1 THEN 'Сироты' \n" +
                "\t\t  WHEN SC.is_indigent = 1 THEN 'Малоимущие' \n" +
                "\t\t  WHEN SC.is_veteran = 1 THEN 'Ветеран' \n" +
                "\t  END AS typeOfReference, \n" +
                "\t  DG.groupname AS groupname, \n" +
                "\t  REF.book_number AS regNumber, \n" +
                "\t  REF.date_start AS dateStart, \n" +
                "\t  REF.date_finish AS dateFinish, \n" +
                "\t  CASE \n" +
                "\t\t  WHEN SC.order_info->'socialScholarship' IS NOT NULL THEN 'socialScholarship' \n" +
                "\t\t  WHEN SC.order_info->'socialIncreasedScholarship' IS NOT NULL THEN 'socialIncreasedScholarship' \n" +
                "\t  END AS orderType, \n" +
                "\t  CASE \n" +
                "\t\t  WHEN SC.order_info->'socialScholarship'->>'dateFrom' IS NOT NULL THEN CAST(SC.order_info->'socialScholarship'->>'dateFrom' AS date) \n" +
                "\t\t  WHEN SC.order_info->'socialIncreasedScholarship'->>'dateFrom' IS NOT NULL THEN CAST(SC.order_info->'socialIncreasedScholarship'->>'dateFrom' AS date) \n" +
                "\t\t  END AS firstDate, \n" +
                "\t  CASE \n" +
                "\t\t  WHEN SC.order_info->'socialScholarship'->>'dateTo' IS NOT NULL THEN CAST(SC.order_info->'socialScholarship'->>'dateTo' AS date) \n" +
                "\t\t  WHEN SC.order_info->'socialIncreasedScholarship'->>'dateTo' IS NOT NULL THEN CAST(SC.order_info->'socialIncreasedScholarship'->>'dateTo' AS date) \n" +
                "\t  END AS secondDate \n" +
                "FROM humanface HF \n" +
                "\t  INNER JOIN studentcard SC ON SC.id_humanface = HF.id_humanface \n" +
                "\t  INNER JOIN student_semester_status SSS ON SC.id_studentcard = SSS.id_studentcard \n" +
                "\t  INNER JOIN dic_group DG ON SC.id_current_dic_group = DG.id_dic_group \n" +
                "\t  INNER JOIN link_group_semester LGS ON DG.id_dic_group = LGS.id_dic_group \n" +
                "\t  INNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester \n" +
                "\t  LEFT JOIN reference REF ON REF.id_reference = ( \n" +
                "\t\t  SELECT MAX(id_reference) \n" +
                "\t\t  FROM reference REF2\n" +
                "\t\t  WHERE REF2.id_studentcard = SC.id_studentcard) \n" +
                "\t  INNER JOIN curriculum CUR ON DG.id_curriculum = CUR.id_curriculum \n" +
                "\t  LEFT JOIN reference_subtype RS ON REF.id_reference_subtype = RS.id_reference_subtype \n" +
                "WHERE \n" +
                "\t  (SC.is_sirota = 1 \n" +
                "\t  OR ((SC.is_invalid = 1 OR SC.is_indigent = 1) AND (REF.date_finish IS NULL OR REF.date_finish > now()))) \n" +
                "\t  AND ((SSS.is_academicleave = 1 AND SSS.id_student_semester_status = ( \n" +
                "\t\t  SELECT MAX(id_student_semester_status) \n" +
                "\t\t  FROM student_semester_status SSS2 \n" +
                "\t\t  WHERE SSS.id_studentcard = SSS2.id_studentcard)) \n" +
                "\t  OR (SSS.is_deducted = 0 AND SEM.is_current_sem = 1)) \n" +
                "\t  AND CUR.formofstudy = 1 \n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("regNumber")
                .addScalar("groupname")
                .addScalar("dateStart")
                .addScalar("dateFinish")
                .addScalar("typeOfReference")
                .addScalar("firstDate")
                .addScalar("secondDate")
                .addScalar("orderType")
                .setResultTransformer(Transformers.aliasToBean(ExcelReportModel.class));
        return (List<ExcelReportModel>) getList(q);
    }
}

