package org.edec.rest.manager;

import org.apache.commons.lang.time.DateUtils;
import org.edec.dao.DAO;
import org.edec.synchroMine.model.eso.entity.Order;
import org.edec.synchroMine.model.eso.entity.OrderStatusType;
import org.edec.utility.constants.OrderStatusConst;
import org.edec.utility.constants.ScholarshipTypeConst;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.type.DateType;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.List;


public class LoginRestDAO extends DAO {

    public String checkToken(String token) {

        String query = "SELECT username FROM api_token WHERE key = :key";
        Query q = getSession().createSQLQuery(query);
        q.setString("key", token);
        List<String> list = (List<String>) getList(q);
        return (list != null && list.size() > 0) ? list.get(0) : "";
    }

    public String startUserSession(String username, Integer durationDays) {

        removeOldSessions(username);

        String token = generateToken();

        // Заглушка для тестирования кабинета преподавателя
        if (username.equalsIgnoreCase("apogrebnikov")) {
            token = "test-teacher";
        }

        Date startDate = new Date();
        Date endDate = DateUtils.addDays(startDate, durationDays);
        String query = "INSERT INTO api_session (token, starttime, endtime, username) VALUES (:token, :starttime, :endtime, :username)";
        Query q = getSession().createSQLQuery(query);
        q.setString("token", token);
        q.setDate("starttime", startDate);
        q.setDate("endtime", endDate);
        q.setString("username", username);
        executeUpdate(q);
        return token;
    }

    public Boolean removeOldSessions(String username) {
        String query = "DELETE FROM api_session WHERE username ilike :username";
        Query q = getSession().createSQLQuery(query);
        q.setString("username", username);
        executeUpdate(q);
        return true;
    }

    public String checkUserToken(String token) {
        String query = "SELECT username FROM api_session WHERE token = :token AND endtime > :curtime";
        Query q = getSession().createSQLQuery(query);
        q.setString("token", token);
        q.setDate("curtime", new Date());
        List<String> list = (List<String>) getList(q);
        String username = (list != null && list.size() > 0) ? list.get(0) : "";
        // Подмена для родителя
        if (username.startsWith("PA_")) {
            String subquery = "SELECT sc.ldap_login FROM\n" +
                    "parent pa\n" +
                    "INNER JOIN studentcard sc ON sc.id_studentcard = pa.id_studentcard\n" +
                    "WHERE username = :username";
            Query subq = getSession().createSQLQuery(subquery);
            subq.setString("username", username);
            List<String> sublist = (List<String>) getList(subq);
            return (sublist != null && sublist.size() > 0) ? sublist.get(0) : "";
        }
        return username;
    }

    public Long getHumanfaceByToken(String token) {
        String query = "SELECT hf.id_humanface\n" +
                "FROM humanface hf\n" +
                "LEFT JOIN studentcard sc USING (id_humanface)\n" +
                "LEFT JOIN employee em USING (id_humanface)\n" +
                "INNER JOIN api_session aps ON aps.token = :token AND endtime > :curtime\n" +
                "WHERE LOWER(em.other_ad) = LOWER(aps.username) OR LOWER(sc.ldap_login) = LOWER(aps.username)";
        Query q = getSession().createSQLQuery(query);
        q.setString("token", token);
        q.setDate("curtime", new Date());
        List<Long> list = (List<Long>) getList(q);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    public String generateToken() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        String token = bytes.toString();
        token = token.substring(3);
        return token;
    }
}
