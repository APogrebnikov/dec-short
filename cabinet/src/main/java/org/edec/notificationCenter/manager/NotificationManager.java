package org.edec.notificationCenter.manager;

import org.edec.dao.DAO;
import org.edec.notificationCenter.model.CommentaryModel;
import org.edec.notificationCenter.model.FeedbackModel;
import org.edec.notificationCenter.model.NotificationModel;
import org.edec.notificationCenter.model.receiverTypes.CourseModel;
import org.edec.notificationCenter.model.receiverTypes.EmployeeModel;
import org.edec.notificationCenter.model.receiverTypes.GroupModel;
import org.edec.notificationCenter.model.receiverTypes.StudentModel;
import org.edec.utility.constants.FormOfStudyConst;
import org.edec.utility.constants.ReceiverStatusConst;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.TimestampType;

import java.util.Date;
import java.util.List;

public class NotificationManager extends DAO {

    public Long createNotification(Long idSender, Long notificationType) {
        String query = "INSERT INTO notification (date_created, id_dic_notification, id_sender) " +
                       "VALUES (now(), " + notificationType +
                       ", " + idSender + ") RETURNING id_notification";

        Query q = getSession().createSQLQuery(query);

        return (Long) getList(q).get(0);
    }

    /**
     * Отметить список сообщений как прочитаные
     * @param idNotifications
     * @param idUser
     * @return
     */
    public boolean updateNotificationStatus(List<Long> idNotifications, Long idUser) {
        String query = "UPDATE link_notification_humanface SET is_read = true WHERE id_notification in (:ids) AND id_humanface = :idhuman";

        Query q = getSession().createSQLQuery(query)
                .setParameterList("ids", idNotifications)
                .setLong("idhuman", idUser);

        return executeUpdate(q);
    }

    public Long createSystemNotification(String header, String text){
        String query = "INSERT INTO notification" +
                       "(message,  header, date_created, id_dic_notification, is_sent, id_sender) " +
                       "VALUES (:message, :header, now(), 0, true, 0) RETURNING id_notification";

        Query q = getSession().createSQLQuery(query)
                .setParameter("message", text)
                .setParameter("header", header);
        return (Long) getList(q).get(0);
    };

    public List<NotificationModel> getAllNotifications(Long idHumanface) {

        String query = "SELECT n.id_notification as id,\n" +
                       " n.header as header,\n" +
                       " n.message as text,\n" +
                       " n.is_sent as isSent,\n" +
                       " dn.notification_type as receiverType,\n" +
                       " (SELECT COUNT(*) " + "    FROM link_notification_humanface\n" +
                       "    INNER JOIN comment c2 on link_notification_humanface.id_link_notification_humanface = c2.id_link_notification_humanface\n" +
                       "    WHERE id_notification = n.id_notification\n" +
                       "    AND id_sender != 0\n" +
                       "    AND c2.is_read = FALSE) as unreadMessageCounter,\n" + " n.date_created as datePosted \n" +
                       " FROM notification n \n" +
                       "JOIN dic_notification dn using (id_dic_notification)\n" +
                       /* бэкдор для просмотра всех уведомлений */
                       (idHumanface != 214973 && idHumanface != 214964
                        ? "where id_sender = " + idHumanface + "\n"
                        : "where id_sender != 0") +
                       "ORDER BY n.date_created DESC";

        Query q = getSession().createSQLQuery(query).addScalar("id", LongType.INSTANCE)
                              .addScalar("header")
                              .addScalar("datePosted", DateType.INSTANCE)
                              .addScalar("text").addScalar("receiverType")
                              .addScalar("unreadMessageCounter", IntegerType.INSTANCE)
                              .addScalar("isSent", BooleanType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(NotificationModel.class));

        return (List<NotificationModel>) getList(q);
    }

    public boolean saveNotificationInfo(NotificationModel notification) {
        String query = "UPDATE notification SET header = :header,message = :message " + "WHERE id_notification = :idNotification";

        Query q = getSession().createSQLQuery(query).setParameter("header", notification.getHeader())
                              .setParameter("message", notification.getText()).setParameter("idNotification", notification.getId());
        return executeUpdate(q);
    }

    public List<StudentModel> searchStudents(String fio, String groupname, int fos,long idInst, boolean isGroupLeader) {
        String query = "SELECT h.id_humanface as idHumanface,\n" +
                       "family || ' ' || name || ' ' || patronymic as fio,\n" +
                       "g.groupname as groupname \n" +
                       "FROM student_semester_status sss\n" +
                       "JOIN studentcard s ON sss.id_studentcard = s.id_studentcard\n" +
                       "JOIN humanface h ON s.id_humanface = h.id_humanface\n" +
                       "JOIN link_group_semester semester ON sss.id_link_group_semester = semester.id_link_group_semester\n" +
                       "JOIN semester sem using (id_semester) \n" +
                       "JOIN dic_group g ON semester.id_dic_group = g.id_dic_group\n" +
                       "WHERE lower(family || ' ' || name || ' ' || patronymic) ILIKE '%" + fio + "%'\n" +
                       "AND lower(g.groupname) ILIKE '%" + groupname + "%'\n" +
                       "AND id_current_dic_group = g.id_dic_group\n" +
                       "AND sem.formofstudy = " + fos +
                       "AND sem.is_current_sem = 1 \n" +
                       "AND g.id_institute = " + idInst +
                       "      AND s.id_studentcard =\n" +
                       "   (SELECT MAX(s2.id_studentcard) FROM studentcard s2\n" +
                       "      INNER JOIN humanface h2 ON s2.id_humanface = h2.id_humanface\n" +
                       "      WHERE h2.id_humanface = h.id_humanface)\n"
                       + (isGroupLeader ? "AND sss.is_group_leader = 1 \n" : "") +
                       "GROUP BY idHumanface,groupname\n" + "ORDER BY fio,groupname DESC";

        Query q = getSession().createSQLQuery(query).addScalar("idHumanface", LongType.INSTANCE)
                              .addScalar("fio")
                              .addScalar("groupName")
                              .setResultTransformer(Transformers.aliasToBean(StudentModel.class));

        return (List<StudentModel>) getList(q);
    }

    public List<StudentModel> getStudents(Long idNotification, boolean isGroupLeader) {
        String query = "SELECT DISTINCT(h2.id_humanface) as idHumanface,\n" +
                       "family || ' ' || name || ' ' || patronymic as fio,\n" +
                       "lnh.status as status,\n" +
                       "g.groupname as groupname\n" +
                       "FROM link_notification_humanface lnh\n" +
                       "INNER JOIN humanface h2 ON lnh.id_humanface = h2.id_humanface\n" +
                       "INNER JOIN studentcard s2 ON h2.id_humanface = s2.id_humanface\n" +
                       "INNER JOIN student_semester_status s4 ON s2.id_studentcard = s4.id_studentcard\n" +
                       "INNER JOIN link_group_semester semester ON s4.id_link_group_semester = semester.id_link_group_semester\n" +
                       "INNER JOIN dic_group g ON semester.id_dic_group = g.id_dic_group\n" +
                       "WHERE id_notification = " + idNotification + "\n" +
                       "AND id_current_dic_group = g.id_dic_group\n" +
                       "AND s2.id_studentcard = \n" +
                       "    (SELECT s3.id_studentcard\n" + "     FROM studentcard s3\n" +
                       "      INNER JOIN humanface h3 ON s3.id_humanface = h3.id_humanface\n" +
                       "      WHERE h2.id_humanface = h3.id_humanface" + " ORDER BY s3.id_studentcard DESC LIMIT 1)\n" +
                       (isGroupLeader ? "AND s4.is_group_leader = 1 \n" : "");

        Query q = getSession().createSQLQuery(query)
                              .addScalar("idHumanface", LongType.INSTANCE)
                              .addScalar("fio")
                              .addScalar("groupName")
                              .addScalar("status", IntegerType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentModel.class));

        return (List<StudentModel>) getList(q);
    }

    public List<GroupModel> searchGroups(String groupname, int fos, long idInst) {
        String query =
                "SELECT id_dic_group as idGroup, groupname as groupName\n" +
                " FROM dic_group\n" +
                "JOIN link_group_semester USING(id_dic_group)\n" +
                "JOIN semester USING(id_semester)\n" +
                " WHERE lower(groupname) ILIKE '%" + groupname + "%'\n" +
                " AND formofstudy = " + fos + "\n" +
                " AND semester.is_current_sem = 1\n" +
                " AND dic_group.id_institute = " + idInst;

        Query q = getSession().createSQLQuery(query)
                              .addScalar("idGroup", LongType.INSTANCE)
                              .addScalar("groupName")
                              .setResultTransformer(Transformers.aliasToBean(GroupModel.class));

        return (List<GroupModel>) getList(q);
    }

    public List<GroupModel> getGroups(Long idNotification) {
        String query = "SELECT g.id_dic_group as idGroup, g.groupname as groupName, lnh.status as status\n" +
                       "FROM link_notification_humanface lnh\n" + "  INNER JOIN humanface h2 ON lnh.id_humanface = h2.id_humanface\n" +
                       "  INNER JOIN studentcard s2 ON h2.id_humanface = s2.id_humanface\n" +
                       "  INNER JOIN student_semester_status s4 ON s2.id_studentcard = s4.id_studentcard\n" +
                       "  INNER JOIN link_group_semester semester ON s4.id_link_group_semester = semester.id_link_group_semester\n" +
                       "  INNER JOIN dic_group g ON semester.id_dic_group = g.id_dic_group\n" +
                       "  WHERE g.id_dic_group = s2.id_current_dic_group AND id_notification = " + idNotification + "\n" +
                       "  AND s2.id_studentcard = \n" + "    (SELECT MAX(s3.id_studentcard)\n" + "     FROM studentcard s3\n" +
                       "      INNER JOIN humanface h3 ON s3.id_humanface = h3.id_humanface\n" +
                       "      WHERE h2.id_humanface = h3.id_humanface)\n" + "  GROUP BY g.id_dic_group, g.groupname, lnh.status\n";

        Query q = getSession().createSQLQuery(query).addScalar("idGroup", LongType.INSTANCE).addScalar("groupName").addScalar("status")
                              .setResultTransformer(Transformers.aliasToBean(GroupModel.class));

        return (List<GroupModel>) getList(q);
    }

    public List<Long> getStudentsByGroup(Long idGroup) {

        String query = "\n" + "SELECT h.id_humanface\n" + " FROM student_semester_status sss\n" +
                       " INNER JOIN studentcard s ON sss.id_studentcard = s.id_studentcard\n" +
                       " INNER JOIN humanface h ON s.id_humanface = h.id_humanface\n" +
                       " INNER JOIN link_group_semester semester ON sss.id_link_group_semester = semester.id_link_group_semester\n" +
                       " INNER JOIN dic_group g ON semester.id_dic_group = g.id_dic_group\n" + " WHERE id_current_dic_group = " + idGroup +
                       " GROUP BY h.id_humanface\n" + "\n";

        Query q = getSession().createSQLQuery(query);

        return (List<Long>) getList(q);
    }

    //TODO:добавить комментарии в бд по статусам
    public boolean saveReceiver(Long idHumanface, Long idNotification, int status) {

        String query =
                "INSERT INTO link_notification_humanface (id_humanface, id_notification, status) " +
                "VALUES (:idHumanface, :idNotification, :status)";

        Query q = getSession().createSQLQuery(query)
                              .setParameter("idHumanface", idHumanface)
                              .setParameter("idNotification", idNotification)
                              .setParameter("status", status);

        return executeUpdate(q);
    }

    //Получаем все ответы по уведомлению
    public List<FeedbackModel> getFeedback(Long idNotification) {
        String query = "SELECT h.id_humanface as idHumanface,\n" +
                       "       family || ' ' || name || ' ' || patronymic as fio,\n" +
                       "       lnh.id_link_notification_humanface as idLinkNotificationHumanface,\n" +
                       "       (SELECT count(*)\n" +
                       "        FROM comment c\n" +
                       "        WHERE c.id_link_notification_humanface = lnh.id_link_notification_humanface\n" +
                       "          AND c.id_sender = h.id_humanface\n" +
                       "          AND c.is_read = FALSE) as unreadMessageCount,\n" +
                       "       (SELECT c2.date_posted\n" +
                       "        from link_notification_humanface lnh2\n" +
                       "               join comment c2\n" +
                       "                 on lnh2.id_link_notification_humanface = c2.id_link_notification_humanface\n" +
                       "        where lnh2.id_notification = :idNotification\n" +
                       "          and lnh2.id_link_notification_humanface = lnh.id_link_notification_humanface\n" +
                       "        order by date_posted desc\n" + "        limit 1)  as dateLastMessage\n" + "\n" +
                       "FROM link_notification_humanface lnh\n" + "       join humanface h on lnh.id_humanface = h.id_humanface\n" +
                       "where id_notification = :idNotification\n" +
                       "order by dateLastMessage desc nulls last";

        Query q = getSession().createSQLQuery(query).addScalar("idHumanface", LongType.INSTANCE)
                              .addScalar("idLinkNotificationHumanface", LongType.INSTANCE).addScalar("fio")
                              .addScalar("unreadMessageCount", IntegerType.INSTANCE).setParameter("idNotification", idNotification)
                              .setResultTransformer(Transformers.aliasToBean(FeedbackModel.class));

        return (List<FeedbackModel>) getList(q);
    }

    public boolean sendNotification(Long idNotification) {
        String query =
                "UPDATE link_notification_humanface" + " SET status = " + ReceiverStatusConst.NOTIFIED + ",\n" + " date_sent = now() \n" +
                " WHERE id_notification = " + idNotification;

        Query q = getSession().createSQLQuery(query);

        return executeUpdate(q);
    }

    public boolean deleteReceiver(Long idLinkNotificationHumanface) {
        String query =
                "DELETE FROM link_notification_humanface\n" + "WHERE id_link_notification_humanface = " + idLinkNotificationHumanface;

        Query q = getSession().createSQLQuery(query);

        return executeUpdate(q);
    }

    public Long getLinkNotificationHumanface(Long idNotification, Long idHumanface) {
        String query = "SELECT id_link_notification_humanface \n" + "FROM link_notification_humanface \n " + "WHERE id_notification = " +
                       idNotification + "\n" + "AND id_humanface = " + idHumanface + "\n";

        Query q = getSession().createSQLQuery(query);

        return ((List<Long>) getList(q)).get(0).longValue();
    }

    public List<EmployeeModel> searchEmployees(String fio) {
        String query =
                "select h2.id_humanface as idHumanface,\n " + "family || ' ' || name || ' ' || patronymic as fio \n" + "from employee\n" +
                "inner join humanface h2 on employee.id_humanface = h2.id_humanface\n" +
                "WHERE lower(family || ' ' || name || ' ' || patronymic) ilike '%" + fio + "%' \n" + "group by idHumanface, fio \n" +
                "order by fio desc \n";

        Query q = getSession().createSQLQuery(query).addScalar("idHumanface", LongType.INSTANCE).addScalar("fio")
                              .setResultTransformer(Transformers.aliasToBean(EmployeeModel.class));

        return (List<EmployeeModel>) getList(q);
    }

    public List<EmployeeModel> getEmployees(Long idNotification) {
        String query = "SELECT h2.id_humanface as idHumanface, \n" + "family || ' ' || name || ' ' || patronymic as fio, \n" +
                       "status as status\n" + "from link_notification_humanface lnh\n" +
                       "INNER JOIN humanface h2 on lnh.id_humanface = h2.id_humanface\n" + "WHERE id_notification = " + idNotification +
                       "\n" + "order by status asc, fio desc \n";

        Query q = getSession().createSQLQuery(query).addScalar("idHumanface", LongType.INSTANCE).addScalar("fio")
                              .addScalar("status", IntegerType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(EmployeeModel.class));

        return (List<EmployeeModel>) getList(q);
    }

    //Курсы

    public List<Long> getStudentsByCourses(Long idInstitute, String courseSubquery) {

        String query = "SELECT h2.id_humanface from dic_group\n" +
                       "                              inner join link_group_semester lgs on dic_group.id_dic_group = lgs.id_dic_group\n" +
                       "                              inner join semester on lgs.id_semester = semester.id_semester\n" +
                       "                              inner join dic_group dg on lgs.id_dic_group = dg.id_dic_group\n" +
                       "                              inner join student_semester_status sss on lgs.id_link_group_semester = sss.id_link_group_semester\n" +
                       "                              inner join studentcard s2 on sss.id_studentcard = s2.id_studentcard\n" +
                       "                              inner join humanface h2 on s2.id_humanface = h2.id_humanface\n" +
                       "where lgs.id_semester in (select id_semester from semester where id_institute = " + idInstitute + " and is_current_sem = 1)\n" +
                       "  and \n(" + courseSubquery + ")\n" +
                       "  and sss.is_deducted != 1\n" +
                       "  and dg.id_dic_group = s2.id_current_dic_group;";

        Query q = getSession().createSQLQuery(query);

        return (List<Long>) getList(q);
    }

    public List<CourseModel> getCourses(Long idNotification) {
        String query =
                "select semester.course as courseNumber, link_notification_humanface.status as status, sem.formofstudy as fos from link_notification_humanface\n" +
                "inner join humanface h2 on link_notification_humanface.id_humanface = h2.id_humanface\n" +
                "inner join studentcard s2 on h2.id_humanface = s2.id_humanface\n" +
                "inner join student_semester_status s4 on s2.id_studentcard = s4.id_studentcard\n" +
                "inner join link_group_semester semester on s4.id_link_group_semester = semester.id_link_group_semester\n" +
                "inner join semester sem on semester.id_semester = sem.id_semester\n" +
                "inner join dic_group dg on semester.id_dic_group = dg.id_dic_group \n" +
                "inner join semester s3 on semester.id_semester = s3.id_semester\n" + "where id_notification = :idNotification " +
                "and s3.id_institute = 1 \n" + "and sem.is_current_sem = 1 \n" + "and s4.is_deducted != 1 \n" +
                "and dg.id_dic_group = s2.id_current_dic_group \n" + "group by semester.course, link_notification_humanface.status, sem.formofstudy";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("courseNumber")
                              .addScalar("status")
                              .addScalar("fos")
                              .setParameter("idNotification", idNotification)
                              .setResultTransformer(Transformers.aliasToBean(CourseModel.class));

        return (List<CourseModel>) getList(q);
    }

    public List<CommentaryModel> getCommentaries(Long idSender, Long idLinkNotificationHumanface) {
        String query = "select id_comment as idCommentary,\n" + "date_posted as datePosted,\n" + "message,\n" + "id_sender as idSender,\n" +
                       "comment.is_read as isRead,\n" + "(case when comment.id_sender = :idSender then 'Вы' " +
                       "else (case when hf.id_humanface is null then 'Деканат' else hf.name end) end) as nameSender\n" + "from comment\n" +
                       "inner join link_notification_humanface using (id_link_notification_humanface)\n" +
                       "left join humanface hf on comment.id_sender = hf.id_humanface\n " +
                       "where id_link_notification_humanface = :idLinkNotificationHumanface\n" + "order by id_comment\n";

        Query q = getSession().createSQLQuery(query).addScalar("idCommentary", LongType.INSTANCE)
                              .addScalar("datePosted", TimestampType.INSTANCE).addScalar("nameSender").addScalar("message")
                              .addScalar("isRead", BooleanType.INSTANCE).addScalar("idSender", LongType.INSTANCE)
                              .setParameter("idLinkNotificationHumanface", idLinkNotificationHumanface).setParameter("idSender", idSender)
                              .setResultTransformer(Transformers.aliasToBean(CommentaryModel.class));

        return (List<CommentaryModel>) getList(q);
    }

    public Date sendCommentary(Long idSender, Long idLinkNotificationHumanface, String text) {
        String query = "insert into comment (id_sender, id_link_notification_humanface, message)\n" + "values (" + idSender + "," +
                       idLinkNotificationHumanface + ", :commentText) returning date_posted";

        Query q = getSession().createSQLQuery(query).setParameter("commentText", text);

        return ((Date) getList(q).get(0));
    }

    public boolean updateCommentStatus(Long idComment) {
        String query = "update comment set is_read = TRUE where id_comment = " + idComment;

        Query q = getSession().createSQLQuery(query);

        return executeUpdate(q);
    }

    public List<NotificationModel> getUserNotifications(Long idHumanface) {

        String query = "select n.header as header,\n" +
                       " n.id_notification as id,\n" +
                       " n.message as text,\n" +
                       " n.date_created as datePosted,\n" +
                       " lnh.id_link_notification_humanface as idLinkNotificationHumanface,\n" +
                       " lnh.date_sent as dateSent,\n" +
                       " lnh.is_read as isRead,\n" +
                       " n.id_sender as idSender,\n" +
                       " (SELECT count(*)\n" +
                       "     FROM comment c\n" +
                       "     WHERE c.id_link_notification_humanface = lnh.id_link_notification_humanface\n" +
                       "        AND c.id_sender != :idHumanface\n" +
                       "        AND c.is_read = FALSE) as unreadMessageCounter\n" +
                       "from link_notification_humanface lnh\n" +
                       "join notification n on lnh.id_notification = n.id_notification\n" +
                       "where id_humanface = :idHumanface and lnh.status = " + ReceiverStatusConst.NOTIFIED + "\n" +
                       "order by isRead asc, unreadMessageCounter desc, dateSent desc";

        Query q = getSession().createSQLQuery(query).addScalar("id", LongType.INSTANCE)
                              .addScalar("header").addScalar("text")
                              .addScalar("dateSent", DateType.INSTANCE)
                              .addScalar("isRead", BooleanType.INSTANCE)
                              .addScalar("idSender", LongType.INSTANCE)
                              .addScalar("unreadMessageCounter", IntegerType.INSTANCE)
                              .addScalar("idLinkNotificationHumanface", LongType.INSTANCE)
                              .setLong("idHumanface", idHumanface)
                              .setResultTransformer(Transformers.aliasToBean(NotificationModel.class));

        return (List<NotificationModel>) getList(q);
    }

    public Integer getUserNotificationsCount(Long idHumanface) {

        String query = "SELECT count(c.*) as unreadMessageCounter\n" +
                "     FROM comment c\n" +
                "     INNER JOIN link_notification_humanface lnh ON c.id_link_notification_humanface = lnh.id_link_notification_humanface\n" +
                "       WHERE\n" +
                "        c.id_sender != :idHumanface\n" +
                "        AND c.is_read = FALSE";

        Query q = getSession().createSQLQuery(query)
                .addScalar("unreadMessageCounter", IntegerType.INSTANCE)
                .setLong("idHumanface", idHumanface);
        List<Integer> list = (List<Integer>) getList(q);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    public boolean updatePersonalNotificationStatus(Long idNotificationHumanface) {
        String query = "UPDATE link_notification_humanface" + " SET is_read = true\n" + " WHERE id_link_notification_humanface = " +
                       idNotificationHumanface;

        Query q = getSession().createSQLQuery(query);

        return executeUpdate(q);
    }

    public boolean removeAllReceivers(Long idNotification){
        String query =
                "DELETE FROM link_notification_humanface WHERE id_notification = " + idNotification;

        Query q = getSession().createSQLQuery(query);

        return executeUpdate(q);
    }

    public boolean removeNotification(Long idNotification){
        String query =
                "DELETE FROM notification WHERE id_notification = " + idNotification;

        Query q = getSession().createSQLQuery(query);

        return executeUpdate(q);
    }

    public boolean updateNotificationStatus(Long idNotification){
        String query = "UPDATE notification SET is_sent = true WHERE id_notification = " + idNotification;
        Query q = getSession().createSQLQuery(query);

        return executeUpdate(q);
    }


}
