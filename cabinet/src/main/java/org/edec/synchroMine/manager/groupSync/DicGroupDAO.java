package org.edec.synchroMine.manager.groupSync;

import java.util.List;

import org.edec.dao.DAO;
import org.edec.synchroMine.model.eso.entity.DicGroup;
import org.hibernate.Query;

public class DicGroupDAO extends DAO {

    public DicGroup getDicGroupByName (String name) {
        String query = "SELECT * FROM dic_group dg WHERE groupname like '" + name + "'";
        Query q = getSession().createSQLQuery(query).addEntity(DicGroup.class);
        List<?> list = getList(q);
        return list.size() == 0 ? null : (DicGroup) list.get(0);
    }

    public List<DicGroup> getAll () throws Exception {
        Query q = getSession().createQuery("from DicGroup");
        return (List<DicGroup>) getList(q);
    }

    public DicGroup get (Long id) throws Exception {
        Query q = getSession().createQuery("from DicGroup where id_dic_group = :id");
        q.setLong("id", id);
        List<?> list = getList(q);
        return list.size() == 0 ? null : (DicGroup) list.get(0);
    }
}
