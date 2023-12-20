package org.edec.main.manager;

import org.edec.dao.DAO;
import org.hibernate.Query;

import java.util.List;

public class FCMManager extends DAO {

    public Long createTokenByIdHumanface (long idHumanface, String token) {
        String query = "INSERT INTO fcm_token (id_humanface, token) \n" +
                "\t VALUES (:idHumanface, :token) \n" +
                "\t RETURNING id_fcm_token";

        Query q = getSession().createSQLQuery(query)
                .setParameter("idHumanface", idHumanface)
                .setParameter("token", token);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

}
