package org.edec.register.manager;

import org.edec.dao.DAO;
import org.edec.model.GroupModel;
import org.edec.register.model.RetakeSubjectModel;
import org.edec.register.model.StudentModel;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.Date;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class RetakeManager extends DAO {
    public List<GroupModel> getGroupsByFilter (String qualification, Integer course, Long idSem, String groupname) {
        String query = "SELECT id_dic_group AS idDG, id_link_group_semester AS idLGS,\n" + "DG.groupname\n" + "FROM dic_group DG\n" +
                       "INNER JOIN link_group_semester LGS USING (id_dic_group)\n" + "INNER JOIN curriculum CUR USING (id_curriculum)\n" +
                       "WHERE DG.groupname ILIKE '%" + groupname + "%' AND LGS.id_semester = " + idSem + "\n" +
                       (course == 0 ? "" : "AND LGS.course = " + course + "\n") +
                       (qualification.equals("") ? "" : "AND CUR.qualification IN (" + qualification + ")\n") +
                       "ORDER BY LGS.course, DG.groupname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idDG", LongType.INSTANCE)
                              .addScalar("idLGS", LongType.INSTANCE)
                              .addScalar("groupname")
                              .setResultTransformer(Transformers.aliasToBean(GroupModel.class));
        return (List<GroupModel>) getList(q);
    }

    public List<RetakeSubjectModel> getSubjectsByFitler (Long idSem, String listOfIdGroup, String filter) {
        String query = "SELECT DS.subjectname, S.id_subject AS idSubj, LGSS.id_link_group_semester_subject AS idLGSS,\n" +
                       "\tSTRING_AGG(HF.family || ' ' || HF.name || ' ' || HF.patronymic, ', ') AS teachers, DG.groupname,\n" +
                       "\tS.is_exam = 1 AS exam, S.is_pass = 1 AS pass, S.is_courseproject = 1 AS cp, S.is_coursework = 1 AS cw, S.is_practic = 1 AS practic\n" +
                       "FROM link_group_semester LGS\n" + "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                       "\tINNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                       "\tLEFT JOIN link_employee_subject_group LESG USING (id_link_group_semester_subject)\n" +
                       "\tLEFT JOIN employee EMP USING (id_employee)\n" + "\tLEFT JOIN humanface HF USING (id_humanface)\n" +
                       "WHERE LGS.id_semester = " + idSem + "\n" +
                       (listOfIdGroup.equals("") ? "" : "AND LGS.id_dic_group IN (" + listOfIdGroup + ")\n") +
                       "GROUP BY DG.id_dic_group, DS.id_dic_subject, S.id_subject, LGSS.id_link_group_semester_subject\n" +
                       "HAVING STRING_AGG(HF.family || ' ' || HF.name || ' ' || HF.patronymic, ', ') ILIKE '%" + filter + "%'\n" +
                       "\tOR DS.subjectname ILIKE '%" + filter + "%'\n" + "ORDER BY DG.groupname, DS.subjectname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("subjectname")
                              .addScalar("idSubj", LongType.INSTANCE)
                              .addScalar("idLGSS", LongType.INSTANCE)
                              .addScalar("teachers")
                              .addScalar("groupname")
                              .addScalar("exam")
                              .addScalar("pass")
                              .addScalar("cp")
                              .addScalar("cw")
                              .addScalar("practic")
                              .setResultTransformer(Transformers.aliasToBean(RetakeSubjectModel.class));
        return (List<RetakeSubjectModel>) getList(q);
    }

    public boolean openRetake (Long idLGSS, Integer foc, Date dateOfBegin, Date dateOfEnd, Integer vedomType) {
        String query = "SELECT register_open_retake(" + idLGSS + ", " + foc + ", " + "'" +
                       DateConverter.convertDateToSQLStringFormat(dateOfBegin) + "', " + "'" +
                       DateConverter.convertDateToSQLStringFormat(dateOfEnd) + "', " + vedomType + ")";
        return callFunction(query);
    }

    public long getGroupByIdLgss(long idLgss) {
        String query = "SELECT \n" + "\tLGS.id_dic_group AS idDg, \n" + "\tDG.id_institute AS idInst \n" + "FROM \n" +
                "\tlink_group_semester_subject LGSS\n" + "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" + "WHERE \n" + "\tid_link_group_semester_subject = :idLgss\n";
        Query q = getSession().createSQLQuery(query).addScalar("idDg", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(org.edec.teacher.model.registerRequest.GroupModel.class));
        q.setLong("idLgss", idLgss);
        List<org.edec.teacher.model.registerRequest.GroupModel> list = (List<org.edec.teacher.model.registerRequest.GroupModel>) getList(q);
        return list.size() == 0 ? -1 : list.get(0).getIdDg();
    }
}
