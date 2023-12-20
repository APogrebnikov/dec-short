package org.edec.manager;

import org.edec.dao.DAO;
import org.edec.model.GroupModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;

import java.util.List;


public class EntityManagerGroupsESO extends DAO {
    public List<GroupModel> getGroupsBySemester (Long idSemester) {
        String query = "SELECT\tdg.id_dic_group as idDG, dg.groupname as groupname,\n" +
                       "\tlgs.id_link_group_semester AS idLGS, se.id_institute AS idInstitute\n" + "FROM\tdic_group dg\n" +
                       "\tINNER JOIN link_group_semester lgs USING (id_dic_group)\n" + "\tINNER JOIN semester se USING (id_semester)\n" +
                       "WHERE\tlgs.id_semester = :idSemester\n" + "ORDER BY dg.groupname DESC";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idDG", StandardBasicTypes.LONG)
                              .addScalar("idLGS", StandardBasicTypes.LONG)
                              .addScalar("groupname")
                              .addScalar("idInstitute", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(GroupModel.class));
        q.setLong("idSemester", idSemester);
        return (List<GroupModel>) getList(q);
    }
}
