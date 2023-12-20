package org.edec.teacher.manager;

import org.edec.dao.DAO;
import org.edec.teacher.model.dao.RegisterRequestESOModel;
import org.edec.teacher.model.registerRequest.GroupModel;
import org.edec.teacher.model.registerRequest.RegisterRequestModel;
import org.edec.utility.constants.FormOfControlConst;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.Collections;
import java.util.List;

public class EntityManagerRegisterRequest extends DAO {

    public List<RegisterRequestESOModel> getRequestHistory (long idHumanface, long idLgss, int foc) {
        String query = "SELECT\n" + "\tHF.name as name,\n" + "\tHF.family as family,\n" + "\tHF.patronymic as patronymic,\n" +
                       "\tRR.dateofapplying as applyingDate,\n" + "\tRR.additional_information as additionalInfo,\n" +
                       "\tRR.request_status as status,\n" + "\tRR.id_student_semester_status as idSss\n" + "FROM\n" +
                       "\tregister_request RR\n" + "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                       "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                       "\tINNER JOIN humanface HF ON SC.id_humanface = HF.id_humanface\n" +
                       "\tINNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester_subject)\n" +
                       "\tINNER JOIN subject SUB USING (id_subject)\n" + "WHERE\n" + "\tRR.id_humanface = " + idHumanface + "\n" +
                       "\tAND RR.id_link_group_semester_subject = " + idLgss + "\n" + "\tAND RR.formofcontrol = " + foc + "\n" +
                       "\tORDER BY RR.dateofapplying DESC, RR.request_status ASC\n";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("name")
                              .addScalar("family")
                              .addScalar("patronymic")
                              .addScalar("applyingDate")
                              .addScalar("additionalInfo")
                              .addScalar("status")
                              .addScalar("idSss", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(RegisterRequestESOModel.class));

        return (List<RegisterRequestESOModel>) getList(q);
    }

    public long getGroupByIdLgss (long idLgss) {
        String query = "SELECT \n" + "\tLGS.id_dic_group AS idDg, \n" + "\tDG.id_institute AS idInst \n" + "FROM \n" +
                       "\tlink_group_semester_subject LGSS\n" + "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN dic_group DG USING (id_dic_group)\n" + "WHERE \n" + "\tid_link_group_semester_subject = :idLgss\n";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idDg", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(GroupModel.class));
        q.setLong("idLgss", idLgss);

        List<GroupModel> list = (List<GroupModel>) getList(q);
        return list.size() == 0 ? -1 : list.get(0).getIdDg();
    }

    public boolean createRequest (RegisterRequestModel registerRequestModel) {
        String query = "INSERT INTO register_request (id_humanface," + " id_link_group_semester_subject," + " id_student_semester_status," +
                       " formofcontrol," + " dateofapplying)" + "VALUES (:idHf, :idLgss, :idSss, :foc, :dateOfApplying)";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idHf", registerRequestModel.getIdHumanface())
         .setParameter("idLgss", registerRequestModel.getIdLgss())
         .setParameter("idSss", registerRequestModel.getStudent().getIdSSS())
         .setParameter("foc", registerRequestModel.getFoc())
         .setDate("dateOfApplying", registerRequestModel.getApplyingDate());
        return executeUpdate(q);
    }
}
