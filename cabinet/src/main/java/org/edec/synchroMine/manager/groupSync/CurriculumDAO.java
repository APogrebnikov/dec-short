package org.edec.synchroMine.manager.groupSync;

import java.util.Date;
import java.util.List;

import org.edec.dao.DAO;
import org.edec.synchroMine.model.eso.entity.Curriculum;
import org.hibernate.Query;
import org.hibernate.type.LongType;

public class CurriculumDAO extends DAO {

    public Integer getGroupCount (Curriculum cur) {
        String query = "SELECT COUNT(*) AS counter FROM dic_group dg WHERE id_curriculum = :id";
        Query q = getSession().createSQLQuery(query).addScalar("counter", LongType.INSTANCE).setLong("id", cur.getId());
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0).intValue();
    }

    public Curriculum getCurriculumByCode (String directioncode, Date date, Integer formOfStudy, Integer qualification) {
        String query =
                "SELECT CUR.*\n" + "FROM curriculum CUR\n" + "INNER JOIN schoolyear SY ON CUR.created_school_year = SY.id_schoolyear\n" +
                "WHERE CUR.directioncode LIKE :directioncode\n" +
                "\t\tAND EXTRACT(YEAR FROM SY.dateofbegin) = DATE_PART('year', :date) AND formofstudy = :formOfStudy\n" +
                "\t\tAND CUR.qualification = :qualification";
        Query q = getSession().createSQLQuery(query).addEntity(Curriculum.class);
        q.setString("directioncode", directioncode)
         .setDate("date", date)
         .setInteger("formOfStudy", formOfStudy)
         .setInteger("qualification", qualification);
        List<?> list = getList(q);
        return list.size() == 0 ? null : (Curriculum) list.get(0);
    }

    public List<Curriculum> getAll () throws Exception {
        Query q = getSession().createQuery("from Curriculum");
        return (List<Curriculum>) getList(q);
    }

    public Curriculum get (Long id) throws Exception {
        Query q = getSession().createQuery("from Curriculum where id_curriculum = :id");
        q.setLong("id", id);
        List<?> list = getList(q);
        return list.size() == 0 ? null : (Curriculum) list.get(0);
    }
}
