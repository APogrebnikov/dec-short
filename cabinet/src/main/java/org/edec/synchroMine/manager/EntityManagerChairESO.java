package org.edec.synchroMine.manager;

import org.edec.dao.DAO;
import org.hibernate.Query;
import org.hibernate.type.LongType;

import java.util.List;


public class EntityManagerChairESO extends DAO {

    public Long getChairIdByODI (Long otherId) {
        String query = "SELECT id_chair " + "FROM department de " + "WHERE otherdbid=" + otherId;
        Query q = getSession().createSQLQuery(query).addScalar("id_chair", LongType.INSTANCE);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }
}
