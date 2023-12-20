package org.edec.student.FAQ.manger;

import org.edec.dao.DAO;
import org.edec.student.FAQ.model.ChangePasswordModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import java.util.List;

public class ChangePasswordManager extends DAO {

    public void changePassword(String newPassword, Long idParent) {
        String query = "UPDATE parent SET password = '" + newPassword + "'\n" +
                "where id_parent = " + idParent;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public String getOldPassword(Long idParent){
        String query = "select password as oldPassword\n" +
                "from parent\n" +
                "where id_parent = " + idParent;
        Query q = getSession().createSQLQuery(query).addScalar("oldPassword");
        return (String)q.uniqueResult();
    }

}
