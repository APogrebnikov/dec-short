package org.edec.profile.manager;

import org.edec.dao.DAO;
import org.edec.profile.model.ProfileModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;

import java.util.Date;
import java.util.List;


public class ProfileManager extends DAO {
    public ProfileModel getProfileByHumId (Long idHum) {
        //language=PostgreSQL
        String query = "SELECT \n" +
                       "\t  HF.family || ' ' || HF.name || ' ' || HF.patronymic AS fio, \n" +
                       "\t  HF.birthday AS birthDay, \n" +
                       "\t  HF.email, HF.get_notification AS getNotification, \n" +
                       "\t  CASE \n" +
                       "\t\t  WHEN REF.date_start IS NOT NULL AND REF.date_finish IS NULL \n" +
                       "\t\t\t  THEN 'Без ограничения по времени' \n" +
                       "\t\t  WHEN REF.date_start IS NULL \n" +
                       "\t\t\t  THEN '' \n" +
                       "\t\t  ELSE to_char(REF.date_finish, 'YYYY-MM-DD') \n" +
                       "\t  END AS referenceDateFinish \n" +
                       "FROM \n" +
                       "\t  humanface HF \n" +
                       "\t  LEFT JOIN studentcard SC USING (id_humanface) \n" +
                       "\t  LEFT JOIN dic_group DG ON SC.id_current_dic_group = DG.id_dic_group \n" +
                       "\t  LEFT JOIN reference REF ON REF.id_reference = ( \n" +
                       "\t\t  SELECT MAX(id_reference) \n" +
                       "\t\t  FROM reference REF2 \n" +
                       "\t\t  WHERE REF2.id_studentcard = SC.id_studentcard) \n" +
                       "WHERE \n" +
                       "\t  HF.id_humanface = :idHum \n";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("birthDay")
                              .addScalar("email")
                              .addScalar("getNotification")
                              .addScalar("referenceDateFinish")
                              .setParameter("idHum", idHum)
                              .setResultTransformer(Transformers.aliasToBean(ProfileModel.class));

        return ((List<ProfileModel>)getList(q)).get(0);
    }

    public boolean updateBirthDay (Long idHum, Date dateBirthDay) {
        String query = "UPDATE humanface SET birthday = :birthday WHERE id_humanface = :idHum";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idHum", idHum).setDate("birthday", dateBirthDay);
        return executeUpdate(q);
    }

    public boolean updateEmail (Long idHum, String email) {
        String query = "UPDATE humanface SET email = :email WHERE id_humanface = :idHum";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idHum", idHum).setParameter("email", email, StringType.INSTANCE);
        return executeUpdate(q);
    }

    public boolean updateGetNotification (Long idHum, Boolean getNotification) {
        String query = "UPDATE humanface SET get_notification = :getNotification WHERE id_humanface = :idHum";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idHum", idHum).setParameter("getNotification", getNotification);
        return executeUpdate(q);
    }

    public boolean updateStartPage (String path, Long idHum) {
        String query = "UPDATE humanface SET start_page = :path WHERE id_humanface = :idHum";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("path", path, StringType.INSTANCE).setLong("idHum", idHum);
        return executeUpdate(q);
    }

    public boolean updateStartPageForParent (String path, Long idParent) {
        String query = "UPDATE parent SET start_page = :path WHERE id_parent = :idParent";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("path", path, StringType.INSTANCE).setLong("idParent", idParent);
        return executeUpdate(q);
    }

    public List<Long> getIdEmployee(Long idHumanface){
        String query = "select id_employee\n" +
                "from employee\n" +
                "where id_humanface = " + idHumanface;
        Query q = getSession().createSQLQuery(query);
        return (List<Long>) q.list();
    }
}
