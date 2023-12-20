package org.edec.contingentMovement.manager;

import javafx.util.Pair;
import org.edec.contingentMovement.model.ResitRatingModel;
import org.edec.dao.DAO;
import org.edec.model.ActionModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;

import java.util.Date;
import java.util.List;

public class ContingentMovementManager extends DAO {

    public static final String FUNCTION_CREATE_SSS = "create_sss_by_student_group";
    public static final String FUNCTION_CREATE_SR = "create_sr_by_student_group";
    public static final String FUNCTION_DELETE_SSS = "deleteNextSSSafterRecovery";

    public List<ResitRatingModel> getSubjectByGroup (Long idDG) {
        String query =
                "SELECT S.hoursaudcount, S.is_exam = 1 AS exam," +
                " S.is_pass = 1 AS pass, " +
                " S.is_courseproject = 1 AS cp," +
                " S.is_coursework = 1 AS cw," +
                " S.is_practic = 1 AS practic,\n" +
                " LGS.semesternumber," +
                " DS.subjectname," +
                " S.type," +
                " S.id_subject AS idSubj," +
                " DS.id_dic_subject AS idDicSubj,\n" +
                " S.hourscount, S.subjectcode,\n" +
                " (CASE WHEN LGS.semesternumber = (select LGS.semesternumber\n" +
                "    from link_group_semester LGS\n" +
                "    INNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "    INNER JOIN dic_group DG ON DG.id_dic_group = LGS.id_dic_group\n" +
                "    where DG.id_dic_group = " + idDG + "\n" +
                "    order by LGS.semesternumber DESC\n" + "    limit 1) " +
                "   THEN TRUE " +
                "   ELSE FALSE " +
                "   END) as curSem\n" +
                "FROM link_group_semester LGS\n" +
                "    INNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester)\n" +
                "    INNER JOIN subject S USING (id_subject)\n" +
                "    INNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "WHERE LGS.id_dic_group = :idDG\n"  +
                "and LGS.semesternumber <= (select LGS.semesternumber \n" +
                        "from link_group_semester LGS\n" +
                        "INNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                        "INNER JOIN dic_group DG ON DG.id_dic_group = LGS.id_dic_group\n" +
                        "where DG.id_dic_group = "+idDG+"\n" +
                        "order by LGS.semesternumber DESC\n" +
                        "limit 1) \n"+
                        "ORDER BY LGS.semesternumber, subjectname, exam, pass, cp, cw, practic";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("exam")
                              .addScalar("pass")
                              .addScalar("cp")
                              .addScalar("cw")
                              .addScalar("practic")
                              .addScalar("semesternumber")
                              .addScalar("subjectname").addScalar("subjectcode")
                              .addScalar("idSubj", StandardBasicTypes.LONG)
                              .addScalar("idDicSubj", StandardBasicTypes.LONG)
                              .addScalar("type")
                              .addScalar("hoursCount")
                              .addScalar("hoursaudcount", DoubleType.INSTANCE)
                              .addScalar("curSem", BooleanType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(ResitRatingModel.class));
        q.setLong("idDG", idDG);

        return (List<ResitRatingModel>) getList(q);
    }

    public List<ResitRatingModel> getRatingByStudentAndGroup (Long idSC, Long idDG) {
        String query = "SELECT\n" +
                       "    SR.is_exam = 1 AS exam, SR.is_pass = 1 AS pass, SR.is_courseproject = 1 AS cp, SR.is_coursework = 1 AS cw, SR.is_practic = 1 AS practic,\n" +
                       "    SR.examrating, SR.passrating, SR.courseprojectrating AS cprating, SR.courseworkrating AS cwrating, SR.practicrating,\n" +
                       "    LGS.semesternumber, DS.subjectname, SR.id_sessionrating AS idSR, SR.type, S.hourscount, S.subjectcode\n" +
                       "FROM\n" +
                       "    student_semester_status SSS\n" + "    INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "    INNER JOIN sessionrating SR USING (id_student_semester_status)\n" +
                       "    INNER JOIN subject S USING (id_subject)\n" +
                       "    INNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                       "WHERE\n" + "    SSS.id_studentcard = :idSC AND LGS.id_dic_group = :idDG\n" + "ORDER BY\n" +
                       "    LGS.semesternumber, subjectname, exam, pass, cp, cw, practic";
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
                              .addScalar("idSR", StandardBasicTypes.LONG)
                              .addScalar("subjectname").addScalar("subjectcode")
                              .addScalar("type")
                              .addScalar("semesternumber")
                              .addScalar("hoursCount")
                              .setResultTransformer(Transformers.aliasToBean(ResitRatingModel.class));
        q.setLong("idSC", idSC).setLong("idDG", idDG);
        return (List<ResitRatingModel>) getList(q);
    }

    /**
     * Генерация новых SSS и SR для студента при востановлении и переводе
     *
     * @param idStudentCard
     * @param trustAgreement
     * @param governmentFinanced
     * @param idDicGroup
     * @return
     */
    public Long createSSSandSRinNewGroup (long idStudentCard, int trustAgreement, int governmentFinanced, long idDicGroup) {
        try {
            String transferStudent = "0";
            String querySSS =
                    "SELECT * FROM " + FUNCTION_CREATE_SSS + "(" + idStudentCard + ", " + trustAgreement + ", " + governmentFinanced +
                            ", " + transferStudent + ", " + "0" + ", " + idDicGroup + ", " + "''" + ")";
            callFunction(querySSS);

            String querySR = "SELECT * FROM " + FUNCTION_CREATE_SR + "(" + idStudentCard + ", " + idDicGroup + ", " + "''" + ")";
            callFunction(querySR);

            begin();
            String queryMaxSSS = "SELECT MAX(id_student_semester_status) AS idSSS\n" + "FROM student_semester_status\n" +
                    "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" + "WHERE " + "id_studentcard = " +
                    idStudentCard + " " + "AND LGS.id_dic_group = " + idDicGroup;

            Query q = getSession().createSQLQuery(queryMaxSSS).addScalar("idSSS", LongType.INSTANCE);

            String queryUpdateCurrentGroup =
                    "UPDATE studentcard SET id_current_dic_group = " + idDicGroup + " WHERE id_studentcard = " + idStudentCard;
            getSession().createSQLQuery(queryUpdateCurrentGroup).executeUpdate();
            List<Long> list = q.list();

            commit();
            return list.get(0);
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Удаляет все старшие SSS, которые найдет выше idSSS
     *
     * @param idSSS - выше этого SSS
     * @param idSem - выше этого семестра
     */
    public boolean deleteWasteSSS (Long idSSS, Long idSem) {
        try {
            String query = "SELECT " + FUNCTION_DELETE_SSS + "(" + idSSS + ", " + idSem + ")";
            callFunction(query);
            return true;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Set not actual for SR from old semester
     *
     * @param oldSSS
     */
    public boolean setNotActualSR (Long oldSSS) {
        try {
            begin();
            String query =
                    "WITH search AS (\n" + "\tSELECT \tLGS.id_dic_group, SSS.id_studentcard\n" + "\tFROM \tstudent_semester_status SSS\n" +
                            "\t\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                            "\tWHERE \tSSS.id_student_semester_status = " + oldSSS + ")\n" +
                            "UPDATE sessionrating SET is_notactual = 1 WHERE id_sessionrating IN (\n" + "\tSELECT \n" +
                            "\t\tSR.id_sessionrating\n" + "\tFROM\n" + "\t\tsessionrating SR \n" +
                            "\t\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                            "\t\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                            "\t\tINNER JOIN search ON SSS.id_studentcard = search.id_studentcard AND LGS.id_dic_group = search.id_dic_group\n" +
                            "\tWHERE\n" + "\t\t(SR.is_exam = 1 AND SR.examrating <3) OR (SR.passrating = 1 AND SR.passrating <> 1)\n" +
                            "\t\tOR (SR.is_courseproject = 1 AND SR.courseprojectrating < 3) \n" +
                            "\t\tOR (SR.is_coursework = 1 AND SR.courseworkrating < 3) \n" + "\t\tOR (SR.is_practic = 1 AND SR.practicrating < 3))";
            Query q = getSession().createSQLQuery(query);
            q.executeUpdate();
            commit();
            return true;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Устанавливаем новую группу в SC для текущего студента
     *
     * @param currentSSS
     * @return
     */
    public boolean updateCurrentGroup (Long currentSSS) {
        try {
            begin();
            String query = "UPDATE studentcard SET id_current_dic_group = LGS.id_dic_group\n" + "FROM\tstudent_semester_status SSS\n" +
                    "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                    "WHERE \tSSS.id_student_semester_status = " + currentSSS +
                    " AND studentcard.id_studentcard = SSS.id_studentcard";
            Query q = getSession().createSQLQuery(query);
            q.executeUpdate();
            commit();
            return true;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Устанавливает флаг Слушатель, для студентов в подвешанном состоянии
     * TODO: Добавить данный флаг студенту во все семестры в новой группе
     *
     * @param idSSS
     * @param listener
     * @return
     */
    public boolean setListenetFlag (long idSSS, boolean listener) {
        try {
            begin();
            String query = "UPDATE student_semester_status SET is_listener = 1\n" + "WHERE \tid_student_semester_status = " + idSSS;
            Query q = getSession().createSQLQuery(query);
            q.executeUpdate();
            commit();
            return true;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Добавляет запись о произведенных дейсвтиях над студентов
     * @param action
     * @return
     */
    public boolean insertAction (ActionModel action) {
        try {
            begin();
            String query =
                    "INSERT INTO order_action (id_studentcard,\n" +
                            "\tid_link_order_student_status,\n" +
                            "\tid_dic_action,\n" +
                            "\tid_dic_group_from,\n" +
                            "\tid_dic_group_to,\n" +
                            "\tid_institute_from,\n" +
                            "\tid_institute_to,\n" +
                            "\tid_semester,\n" + "        id_user,\n" +
                            "\tdate_action,\n" +
                            "\tdate_start,\n" +
                            "\tdate_finish,\n" +
                            "\torder_number,\n" +
                            "\tseq,\n" +
                            "\tid_dic_scholarship) " +
                            "VALUES (" +
                            action.getIdStudentcard() + ",\n" +
                            action.getIdLinkOrderStudentStatus() + ",\n" +
                            action.getIdDicAction() + ",\n" +
                            action.getIdDicGroupFrom() + ",\n" +
                            action.getIdDicGroupTo() + ",\n" +
                            action.getIdInstituteFrom() + ",\n" +
                            action.getIdInstituteTo() + ",\n" +
                            action.getIdSemester() + ",\n" +
                            action.getIdUser() + ",\n" +
                            action.getDateAction() + ",\n" +
                            action.getDateStart() + ",\n" +
                            action.getDateFinish() + ",\n" +
                            action.getOrderNumber() + ",\n" +
                            action.getSeq() + ",\n" +
                            action.getIdDicScholarship() + "\n" + ")";
            Query q = getSession().createSQLQuery(query);
            q.executeUpdate();
            commit();
            return true;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return false;
        }
        // TODO: check "position" - seq field
    }

    /**
     * Обновляем флаг академического отпуска в предыдущем семестр
     * ВАЖНО - вызывать данную фунецию ДО создания новых запесей
     * @param oldSSS
     * @return
     */
    public boolean setAcademToPrevSSS(Long oldSSS) {
        try {
            begin();
            String query = "UPDATE student_semester_status SET is_academicleave = 1 WHERE id_student_semester_status = (\n" +
                    "\tSELECT id_student_semester_status FROM student_semester_status sss\n" +
                    "\tWHERE id_studentcard = (\n" +
                    "\t\tSELECT id_studentcard FROM student_semester_status WHERE id_student_semester_status = :oldSSS\n" +
                    "\t)\n" +
                    "\tORDER BY id_student_semester_status DESC LIMIT 1 OFFSET 1\n" +
                    ")";
            Query q = getSession().createSQLQuery(query);
            q.setLong("oldSSS", oldSSS);
            q.executeUpdate();
            commit();
            return true;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return false;
        }
    }

    public Pair<Date, Date> getLastSessionDates(String fio) {

        String query = "SELECT LGS.dateofbeginpassweek as dateOfBeginSession,\n" +
                "LGS.dateofendsession as dateOfEndSession\n" +
                "FROM student_semester_status SSS\n" +
                "         INNER JOIN studentcard SC using (id_studentcard)\n" +
                "         INNER JOIN humanface HF using (id_humanface)\n" +
                "         INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "         INNER JOIN dic_group DG using (id_dic_group)\n" +
                "         INNER JOIN curriculum c on DG.id_curriculum = c.id_curriculum\n" +
                "WHERE id_current_dic_group = id_dic_group \n" +
                "  AND family || ' ' || name || ' ' || patronymic ILIKE '%" + fio + "%'\n" +
                "order by lgs.id_semester desc limit 1";

        Query q = getSession().createSQLQuery(query)
                .addScalar("dateOfBeginSession", DateType.INSTANCE)
                .addScalar("dateOfEndSession", DateType.INSTANCE);

        List<Object[]> list = (List<Object[]>) getList(q);

        Pair<Date, Date> res = null;
        res = new Pair<Date, Date>((Date)list.get(0)[0], (Date)list.get(0)[1]);
        return res;
    }
}
