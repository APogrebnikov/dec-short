package org.edec.manager;

import org.edec.dao.DAO;
import org.edec.model.SemesterModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;


public class EntityManagerSemesterESO extends DAO {
    public List<SemesterModel> getSemesters (Long idInst, Integer formOfStudy, Integer current) {
        String query = "SELECT\tSEM.id_semester AS idSem, SEM.is_current_sem = 1 AS curSem,\n" +
                       "\tSY.dateOfBegin, SY.dateOfEnd, SEM.season, SEM.firstweek AS firstWeek\n" + "FROM\tsemester SEM\n" +
                       "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                       "WHERE\tSEM.id_institute = :idInst AND SEM.formofstudy = :formOfStudy " +
                       (current == null ? "" : "AND SEM.is_current_sem = " + current) + "\n" + "ORDER BY SEM.id_semester DESC";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idSem", LongType.INSTANCE)
                              .addScalar("curSem")
                              .addScalar("dateOfBegin")
                              .addScalar("dateOfEnd")
                              .addScalar("season")
                              .addScalar("firstWeek")
                              .setResultTransformer(Transformers.aliasToBean(SemesterModel.class));
        q.setLong("idInst", idInst).setInteger("formOfStudy", formOfStudy);
        return (List<SemesterModel>) getList(q);
    }
}
