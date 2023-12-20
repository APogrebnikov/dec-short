package org.edec.commons.manager.mine;

import org.edec.commons.entity.mine.Marks;
import org.edec.dao.MineDAO;
import org.hibernate.Query;

import java.util.List;

public class EntityManagerRegisterMine extends MineDAO {
    public List<Marks> getMarksByRegister (Long idRegister) {
        Query q = getSession().createQuery("from Marks where idRegister = :idRegister");
        q.setLong("idRegister", idRegister);
        return (List<Marks>) getList(q);
    }

}
