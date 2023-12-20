package org.edec.chat.manager;

import org.edec.chat.model.Message;
import org.edec.dao.DAO;
import org.edec.model.HumanfaceModel;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by lunin on 22.10.2017.
 */
public class ChatEsoManager extends DAO {

    public List<HumanfaceModel> getHumanfaceModel (String groupName) {
        String query = "SELECT h.id_humanface AS idHum," + "       h.family AS family, h.name AS name" + "  FROM humanface h," +
                       "       studentcard sc, student_semester_status sss," + "       link_group_semester lgs, semester s, dic_group dg" +
                       " WHERE h.id_humanface = sc.id_humanface" + "   AND sc.id_studentcard = sss.id_studentcard" +
                       "   AND sss.id_link_group_semester = lgs.id_link_group_semester" + "   AND lgs.id_semester = s.id_semester" +
                       "   AND lgs.id_dic_group = dg.id_dic_group" + "   AND dg.groupname LIKE :groupName" + "   AND s.is_current_sem = 1";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("idHum", LongType.INSTANCE)
                              .addScalar("family")
                              .addScalar("name")
                              .setResultTransformer(Transformers.aliasToBean(HumanfaceModel.class));

        q.setString("groupName", groupName);
        return (List<HumanfaceModel>) getList(q);
    }

    public boolean addMessage (Long idHum, String message, Long idChat, int userType, Long replyTo, List<String> paths) {
        String query = "SELECT chat_create_message(:idHum, :message, :idChat, :userType, :replyTo, " +
                       (paths.size() == 0 ? null : castListToStringArray(paths)) + ")";
        Query q = getSession().createSQLQuery(query)
                              .setLong("idHum", idHum)
                              .setText("message", message)
                              .setLong("idChat", idChat)
                              .setParameter("userType", userType)
                              .setLong("replyTo", replyTo);
        if (getList(q) == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean createNotification (List<Long> idHums, Long idHumanSender, String message, List<String> paths) {
        String query = "SELECT chat_create_notification(humans, idhumsender, msg, attaches)";
        return callFunction(query);
    }

    public boolean getHuman (Long idHum, String groupName) {
        String query = "SELECT chat_get_human_chat_by_human_and_chat(idhum, groupname)";
        return callFunction(query);
    }

    public List<Message> downloadMessages (Long last) {
        String query = "SELECT CM.id_humanface AS idHuman, CM.message AS information, CM.id_chat AS idDialog," +
                       " CM.id_chat_user_type AS userType, CM.reply AS replyTo, CM.posted AS posted,\n" +
                       "HF.family||' '||HF.name||' '||HF.patronymic AS fullName\n" + "FROM chat_message CM\n" +
                       "INNER JOIN humanface HF ON CM.id_humanface = HF.id_humanface\n" +
                       (last != null ? "WHERE CM.id_chat_message > :last\n" : "");
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idHuman", LongType.INSTANCE)
                              .addScalar("information")
                              .addScalar("idDialog", LongType.INSTANCE)
                              .addScalar("userType")
                              .addScalar("replyTo", LongType.INSTANCE)
                              .addScalar("posted")
                              .addScalar("fullName")
                              .setResultTransformer(Transformers.aliasToBean(Message.class));
        if (last != null) {
            q.setLong("last", last);
        }
        return (List<Message>) getList(q);
    }
}
