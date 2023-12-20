package org.edec.newMine.registers.manager;

import org.edec.dao.DAO;
import org.hibernate.Query;

public class RegisterManagerASU extends DAO {
    public int getSemesternumber(Long idLgs) {
        String query = "select semesternumber from link_group_semester where id_link_group_semester = " + idLgs;
        Query q = getSession().createSQLQuery(query);
        return (int) q.uniqueResult();
    }

}
