package org.edec.rest.manager;

import org.edec.dao.DAO;
import org.edec.rest.model.student.request.AttendanceResultMsg;
import org.edec.rest.model.student.request.ReportMsg;
import org.edec.rest.model.student.response.Attendance;
import org.hibernate.Query;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.Date;

public class FeedBackDAO extends DAO {

    public boolean insertFeedback(ReportMsg reportMsg, String login) {
        Transaction tx = getSession().beginTransaction();
        String query = "INSERT INTO feedback " +
                "(message, data, os, osversion, appversion, createdat, login, date) VALUES " +
                "(:message, :data, :os, :osversion, :appversion, :createdat, :login, :date)";
        Query q = getSession().createSQLQuery(query);
        q.setString("message", reportMsg.getText());
        q.setString("data", reportMsg.getData());
        q.setString("os", reportMsg.getOs());
        q.setString("osversion", reportMsg.getOsVersion());
        q.setString("appversion", reportMsg.getAppVersion());
        q.setTimestamp("createdat", new Date());
        q.setDate("date", new Date());
        q.setString("login", login);
        int res = q.executeUpdate();
        tx.commit();
        return (res > 0) ? true : false;
    }

}
