package org.edec.fcmNotifications.manager;

import org.edec.dao.DAO;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import java.util.List;

public class fcmNotificationsManager extends DAO {

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

//    public String getTokenByIdHumanface (long idHumanface) {
//        String query = "SELECT token \n" +
//                       "FROM fcm_token \n" +
//                       "WHERE id_humanface = :idHumanface \n";
//
//        Query q = getSession().createSQLQuery(query)
//                              .setParameter("idHumanface", idHumanface);
//        List<String> list = (List<String>) getList(q);
//        return (list != null && list.size() > 0) ? list.get(0) : "";
//    }

    public List<String> getTokenByIdHumanface(Long idHumanface){
        String query = "select token\n" +
                "from fcm_token\n" +
                "where id_humanface = " + idHumanface;

        Query q = getSession().createSQLQuery(query);
        return  (List<String>) q.list();
    }
}
