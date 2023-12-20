package org.edec.register.manager;

import org.edec.dao.DAO;
import org.edec.register.model.RegisterRequestModel;
import org.edec.register.model.SimpleRatingModel;
import org.edec.teacher.model.correctRequest.CorrectRequestModel;
import org.edec.utility.constants.RegisterRequestStatusConst;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.javatuples.Triplet;

import java.util.*;

public class RegisterRequestManager extends DAO {

    public List<RegisterRequestModel> getAllRegisterRequests(long idInstitute, int fos, int status) {
        String query = "SELECT RR.id_register_request AS idRegisterRequest,\n" +
                "\tTHF.family || ' ' || THF.name || ' ' || THF.patronymic AS teacherFullName,\n" +
                "\tSHF.family || ' ' || SHF.name || ' ' || SHF.patronymic AS studentFullName,\n" + "\tTHF.email as email,\n" +
                "\tTHF.get_notification as getNotification,\n" + "\tDG.groupname as groupName,\n" +
                "\tDG.id_dic_group as idGroup,\n" + "\tDS.subjectname as subjectName,\n" + "\tSEM.season as seasonSemester,\n" +
                "\tSY.dateofbegin as dateOfBeginSemester,\n" + "\tSY.dateofend as dateOfEndSemester,\n" +
                "\tRR.dateofapplying as dateOfApplying,\n" + "\tRR.dateofanswering as dateOfAnswering,\n" +
                "\tRR.additional_information as additionalInformation,\n" + "\tRR.request_status as status,\n" +
                "\tRR.formofcontrol as foc,\n" + "\tRR.id_link_group_semester_subject as idLgss,\n" +
                "\tRR.id_student_semester_status as idSss\n" + "\tFROM register_request RR\n" +
                "\tINNER JOIN humanface THF ON RR.id_humanface = THF.id_humanface\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                "\tINNER JOIN humanface SHF ON SC.id_humanface = SHF.id_humanface\n" +
                "\tINNER JOIN link_group_semester_subject LGSS ON RR.id_link_group_semester_subject = LGSS.id_link_group_semester_subject\n" +
                "\tINNER JOIN subject SUB USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "\tINNER JOIN link_group_semester LGS ON LGSS.id_link_group_semester = LGS.id_link_group_semester\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" + "\tINNER JOIN semester SEM USING (id_semester)\n" +
                "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" + "\tWHERE SEM.id_institute = :idInstitute\n" +
                "\tAND SEM.formofstudy " + (fos != 3 ? "= " + fos : " in (1,2)") +
                "\tAND RR.request_status " + (status != RegisterRequestStatusConst.ALL ? "= " + status : " in (0,1,2)") +
                "\tORDER BY RR.dateofapplying DESC, RR.request_status ASC\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idRegisterRequest", LongType.INSTANCE)
                .addScalar("idLgss", LongType.INSTANCE)
                .addScalar("idGroup", LongType.INSTANCE)
                .addScalar("idSss", LongType.INSTANCE)
                .addScalar("teacherFullName")
                .addScalar("studentFullName")
                .addScalar("groupName")
                .addScalar("subjectName")
                .addScalar("dateOfApplying")
                .addScalar("dateOfAnswering")
                .addScalar("additionalInformation")
                .addScalar("status")
                .addScalar("foc")
                .addScalar("dateOfBeginSemester")
                .addScalar("dateOfEndSemester")
                .addScalar("seasonSemester")
                .addScalar("email")
                .addScalar("getNotification", BooleanType.INSTANCE)
                .setParameter("idInstitute", idInstitute)
                .setResultTransformer(Transformers.aliasToBean(RegisterRequestModel.class));
        return (List<RegisterRequestModel>) getList(q);
    }

    public boolean updateRequestStatus(long idRegisterRequest, int requestStatus, Date dateOfAnswering, String additionalInformation) {
        String query = "UPDATE register_request \n" + "SET request_status = :requestStatus,\n" + "dateofanswering = :dateOfAnswering,\n" +
                "additional_information = :additionalInformation\n" + "WHERE id_register_request = :idRegisterRequest\n" + "\n";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idRegisterRequest", idRegisterRequest)
                .setParameter("requestStatus", requestStatus)
                .setParameter("dateOfAnswering", dateOfAnswering)
                .setParameter("additionalInformation", additionalInformation);
        return executeUpdate(q);
    }

    public Set<Triplet> getAllOpenCounts(){
        String query = "SELECT \n" +
                "SEM.id_institute AS idInstitute, SEM.formofstudy AS fos, COUNT(*) AS count\n" +
                "\tFROM register_request RR\n" +
                "\tINNER JOIN humanface THF ON RR.id_humanface = THF.id_humanface\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                "\tINNER JOIN humanface SHF ON SC.id_humanface = SHF.id_humanface\n" +
                "\tINNER JOIN link_group_semester_subject LGSS ON RR.id_link_group_semester_subject = LGSS.id_link_group_semester_subject\n" +
                "\tINNER JOIN subject SUB USING (id_subject)\n" +
                "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "\tINNER JOIN link_group_semester LGS ON LGSS.id_link_group_semester = LGS.id_link_group_semester\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN semester SEM USING (id_semester)\n" +
                "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                "\tWHERE RR.request_status = 0\n" +
                "\tGROUP BY SEM.id_institute, SEM.formofstudy";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idInstitute", LongType.INSTANCE)
                .addScalar("fos", IntegerType.INSTANCE)
                .addScalar("count", IntegerType.INSTANCE);
        List<Object[]> list = (List<Object[]>) getList(q);

        Set<Triplet> res = new HashSet<>();
        for (Object[] o : list) {
            res.add(Triplet.with(o[0], o[1], o[2]));
        }
        return res;
    }

    public List<CorrectRequestModel> getAllCorrectRequests (long idInstitute, int fos, int status, int retakeCount) {
        String query = "SELECT \n" +
                "        cr.id_correct_request as id,\n" +
                "        cr.id_sessionratinghistory as idSRH,\n" +
                "        cr.id_humanface as idHumanface,\n" +
                "        cr.newrating as newRating,\n" +
                "        cr.oldrating as oldRating,\n" +
                "        cr.status as status,\n" +
                "        cr.dateofapplying as dateOfApplying,\n" +
                "        cr.dateofanswering as dateOfAnswering,\n" +
                "        cr.additionalinformation as additionalInformation,\n" +
                "        cr.thumbprint as thumbprint,\n" +
                "        cr.certnumber as certnumber,\n" +
                "        cr.file_path as filePath,\n" +
                "        HFT.family||' '||HFT.name||' '||HFT.patronymic as teacherFIO,\n" +
                "        HFS.family||' '||HFS.name||' '||HFS.patronymic as studentFIO,\n" +
                "        ds.subjectname as subjectName,\n" +
                "        DG.groupname as groupName,\n" +
                "        re.register_number as regNumber,\n" +
                "        CASE " +
                "           WHEN srh.is_exam = 1 THEN 1 " +
                "           WHEN srh.is_pass = 1 THEN 2" +
                "           WHEN srh.is_courseproject = 1 THEN 3" +
                "           WHEN srh.is_coursework = 1 THEN 4" +
                "           WHEN srh.is_practic = 1 THEN 5" +
                "        END as FC,\n" +
                "        CONCAT(\n" +
                "           CASE WHEN SE.season = 0 THEN 'осень' ELSE 'весна' END,\n" +
                "           ' ',\n" +
                "           date_part('year',SY.dateofbegin),\n" +
                "           '/',\n" +
                "           date_part('year',SY.dateofend)\n" +
                "           ) AS season\n"+
                "FROM \n" +
                "\tcorrect_request cr\n" +
                "\tINNER JOIN sessionratinghistory SRH ON SRH.id_sessionratinghistory = cr.id_sessionratinghistory\n" +
                "\tINNER JOIN register RE ON RE.id_register = SRH.id_register\n" +
                "\tINNER JOIN sessionrating SR ON SRH.id_sessionrating = SR.id_sessionrating\n" +
                "\tINNER JOIN student_semester_status SSS ON SSS.id_student_semester_status = SR.id_student_semester_status\n" +
                "\tINNER JOIN link_group_semester LGS ON LGS.id_link_group_semester = SSS.id_link_group_semester\n" +
                "\tINNER JOIN dic_group DG ON LGS.id_dic_group = DG.id_dic_group\n" +
                "\tINNER JOIN semester SE ON SE.id_semester = LGS.id_semester\n" +
                "\tINNER JOIN schoolyear SY ON SE.id_schoolyear = SY.id_schoolyear\n" +
                "\tINNER JOIN studentcard SC ON SC.id_studentcard = SSS.id_studentcard\n" +
                "\tINNER JOIN humanface HFS ON HFS.id_humanface = SC.id_humanface\n" +
                "\tINNER JOIN subject SU ON SU.id_subject = SR.id_subject\n" +
                "\tINNER JOIN dic_subject DS ON DS.id_dic_subject = SU.id_dic_subject\n" +
                "\tINNER JOIN humanface HFT ON HFT.id_humanface = cr.id_humanface\n" +
                "\n" +
                "WHERE\n" +
                "\tcr.delete_status = false AND" +
                "\tSE.id_institute = :idInstitute AND cr.file_path IS NOT NULL and srh.retake_count = " + retakeCount;

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("idSRH", LongType.INSTANCE)
                .addScalar("idHumanface", LongType.INSTANCE)
                .addScalar("oldRating", IntegerType.INSTANCE)
                .addScalar("newRating", IntegerType.INSTANCE)
                .addScalar("status", IntegerType.INSTANCE)
                .addScalar("dateOfApplying", DateType.INSTANCE)
                .addScalar("dateOfAnswering", DateType.INSTANCE)
                .addScalar("additionalInformation")
                .addScalar("thumbprint")
                .addScalar("certnumber")
                .addScalar("filePath")
                .addScalar("teacherFIO")
                .addScalar("studentFIO")
                .addScalar("subjectName")
                .addScalar("groupName")
                .addScalar("regNumber")
                .addScalar("FC")
                .addScalar("season")
                .setResultTransformer(Transformers.aliasToBean(CorrectRequestModel.class))
                .setParameter("idInstitute", idInstitute);
        return (List<CorrectRequestModel>) getList(q);
    }

    public List<SimpleRatingModel> getRatingHistoryForStudent (long idSRH) {
        String query = "SELECT DISTINCT ON (srhRe.retake_count) \n" +
                "\tsrhRe.id_sessionratinghistory AS idSRH,\n" +
                "\tsrhRe.newrating AS newRating,\n" +
                "\tsrhRe.signdate AS signDate,\n" +
                "\tsrhRe.retake_count AS retakeCount\n" +
                "FROM sessionratinghistory srhMain\n" +
                "LEFT JOIN sessionratinghistory srhRe ON \n" +
                "\tsrhMain.id_sessionrating=srhRe.id_sessionrating \n" +
                "\tAND srhMain.is_exam=srhRe.is_exam\n" +
                "\tAND srhMain.is_pass=srhRe.is_pass\n" +
                "\tAND srhMain.is_courseproject=srhRe.is_courseproject\n" +
                "\tAND srhMain.is_coursework=srhRe.is_coursework\n" +
                "WHERE srhMain.id_sessionratinghistory = :idSRHin";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idSRH", LongType.INSTANCE)
                .addScalar("newRating", IntegerType.INSTANCE)
                .addScalar("signDate", DateType.INSTANCE)
                .addScalar("retakeCount", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(SimpleRatingModel.class))
                .setParameter("idSRHin", idSRH);
        return (List<SimpleRatingModel>) getList(q);
    }

    public boolean updateSRHforCorrect(CorrectRequestModel crm) {
        String query = "UPDATE sessionratinghistory SET oldrating = newrating, newrating = :newrating WHERE id_sessionratinghistory = :idSRH";
        Query q = getSession().createSQLQuery(query)
                .setParameter("newrating", crm.getNewRating())
                .setParameter("idSRH", crm.getIdSRH());
        return executeUpdate(q);
    }

    public boolean updateCorrectRequest(CorrectRequestModel crm) {
        String query = "UPDATE correct_request SET status = :status, additionalinformation = :addinfo, dateofanswering=:dateofanswering, notification_status = :notificationStatus WHERE id_correct_request = :idCR";
        Query q = getSession().createSQLQuery(query)
                .setParameter("status", crm.getStatus())
                .setDate("dateofanswering", crm.getDateOfAnswering())
                .setParameter("addinfo",crm.getAdditionalInformation())
                .setParameter("idCR", crm.getId())
                .setParameter("notificationStatus", crm.getNotificationStatus());
        return executeUpdate(q);
    }

    public int getAllUnreadNotificationsCount(Long idHumanface) {

        String query = "SELECT COUNT(*)\n" +
                       "FROM link_notification_humanface\n" +
                       "         JOIN comment c2 " +
                       "            on link_notification_humanface.id_link_notification_humanface = c2.id_link_notification_humanface\n" +
                       "         JOIN notification n on link_notification_humanface.id_notification = n.id_notification\n" +
                       "WHERE n.id_sender = :idHumanface\n" +
                       "AND c2.is_read = FALSE";

        Query q = getSession().createSQLQuery(query)
                              .setLong("idHumanface", idHumanface);

        return (((Long)getList(q).get(0)).intValue());
    }

    public int getUnreadPersonalNotificationsCount(Long idHumanface) {

        String query = "SELECT COUNT(*) as unreadMessageCounter \n" +
                "FROM link_notification_humanface\n" +
                "LEFT JOIN comment c2 on link_notification_humanface.id_link_notification_humanface = c2.id_link_notification_humanface \n" +
                "WHERE link_notification_humanface.id_humanface = " + idHumanface + "\n" +
                " AND ((link_notification_humanface.is_read = FALSE \n" +
                " AND link_notification_humanface.status = 2)\n" +
                " OR (id_sender = 0 \n AND c2.is_read = FALSE))";

        Query q = getSession().createSQLQuery(query);
        List<Long> list = (List<Long>) getList(q);
        if (list.size() > 0) {
            return (list.get(0)).intValue();
        } else {
            return 0;
        }
    }

    public boolean setDeleteStatus(long idCorrectRequest) {
        String query = "UPDATE correct_request \n" +
                "SET delete_status = true \n" +
                "WHERE id_correct_request = " + idCorrectRequest + "\n";

        Query q = getSession().createSQLQuery(query);

        return executeUpdate(q);
    }
}

