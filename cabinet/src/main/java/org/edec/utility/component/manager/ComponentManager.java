package org.edec.utility.component.manager;

import org.edec.dao.DAO;
import org.edec.model.GroupSemesterModel;
import org.edec.utility.component.model.*;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ComponentManager extends DAO {
    public List<InstituteModel> getAll() {
        String query = "SELECT fulltitle, shorttitle, id_institute AS idInst, otherdbid AS idInstMine FROM institute";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idInst", LongType.INSTANCE)
                .addScalar("fulltitle")
                .addScalar("shorttitle")
                .addScalar("idInstMine", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(InstituteModel.class));
        return (List<InstituteModel>) getList(q);
    }

    public List<GroupModel> getGroupBySem(Long idSem, String qualification) {
        return getGroupBySem(idSem, qualification, null);
    }

    public List<GroupModel> getGroupBySem(Long idSem, String qualification, String courses) {
        String query = "SELECT DG.groupname, DG.id_dic_group AS idDG,\n" +
                "\tLGS.id_link_group_semester AS idLGS, LGS.course\n" +
                "FROM dic_group DG\n" +
                "\tINNER JOIN curriculum CUR USING (id_curriculum)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_dic_group)\n" +
                "WHERE LGS.id_semester = :idSem\n" +
                (qualification != null ? "\tAND CUR.qualification IN (" + qualification + ")\n" : "") +
                (courses != null ? "\tAND LGS.course IN (" + courses + ")\n" : "") +
                "ORDER BY LGS.course, CUR.qualification, DG.groupname";
        Query q = getSession().createSQLQuery(query)
                .addScalar("groupname")
                .addScalar("idDG", LongType.INSTANCE)
                .addScalar("idLGS", LongType.INSTANCE)
                .addScalar("course")
                .setResultTransformer(Transformers.aliasToBean(GroupModel.class));
        q.setLong("idSem", idSem);
        return (List<GroupModel>) getList(q);
    }

    public List<SemesterModel> getSemester(Long idInst, Integer fos, Integer season) {
        String query = "SELECT SEM.id_semester AS idSem, (SEM.is_current_sem=1) AS curSem,\n" +
                "\tSEM.season, SY.dateofbegin AS dateOfBegin, SY.dateofend AS dateOfEnd\n" +
                "FROM  semester SEM\n" +
                "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                "WHERE \n" + "\tid_institute = :idInst AND formofstudy " +
                (fos != null && fos != 3 ? "=:fos" : " in (1,2) ") + " AND CAST(SEM.season AS TEXT) ILIKE '%" + (season == null ? "" : season) +
                "%'\n" + "ORDER BY \n" + "\tid_semester DESC";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idSem", LongType.INSTANCE)
                .addScalar("curSem")
                .addScalar("season")
                .addScalar("dateOfBegin")
                .addScalar("dateOfEnd")
                .setResultTransformer(Transformers.aliasToBean(SemesterModel.class));
        q.setLong("idInst", idInst);
        if (fos != null && fos != 3) {
            q.setInteger("fos", fos);
        }
        return (List<SemesterModel>) getList(q);
    }

    public List<YearModel> getYears() {
        String query = "SELECT id_schoolyear As idSchoolYear, dateofbegin As dateOfBegin, dateofend As dateOfEnd FROM schoolyear ORDER BY dateOfBegin";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idSchoolYear", LongType.INSTANCE)
                .addScalar("dateOfBegin")
                .addScalar("dateOfEnd")
                .setResultTransformer(Transformers.aliasToBean(YearModel.class));
        return (List<YearModel>) getList(q);
    }

    public List<ChairModel> getChairs() {
        String query =
                "SELECT id_chair AS idChair, id_department AS idDepartment, fulltitle AS fulltitle, shorttitle AS shorttitle, otherdbid AS otherdbid " +
                        "FROM department WHERE is_active=1 AND is_main=TRUE ORDER BY fulltitle";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idChair", LongType.INSTANCE)
                .addScalar("idDepartment", LongType.INSTANCE)
                .addScalar("fulltitle")
                .addScalar("shorttitle")
                .addScalar("otherdbid", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ChairModel.class));
        List<ChairModel> result = new ArrayList<>();
        ChairModel cm = new ChairModel();
        cm.setFulltitle("-");
        cm.setIdChair(null);
        result.add(cm);
        List<ChairModel> list = (List<ChairModel>) getList(q);
        result.addAll(list);
        return result;
    }
}
