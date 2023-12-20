package org.edec.synchroMine.manager.groupSync;

import java.util.List;

import org.edec.dao.DAO;
import org.edec.synchroMine.model.eso.entity.LinkGroupSemester;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.type.LongType;

public class LinkGroupSemesterDAO extends DAO {

    public List<Long> getIdLGSbySemAndGroupname (Long id_sem, String groupname, Integer course) {
        String tempCourse = "";
        if (course > 0) {
            tempCourse = String.valueOf(course);
        }
        String query = "SELECT LGS.id_link_group_semester AS idLGS\n" + "FROM link_group_semester LGS\n" +
                       "INNER JOIN dic_group DG ON LGS.id_dic_group = DG.id_dic_group\n" + "WHERE DG.groupname LIKE '" + groupname +
                       "' AND LGS.id_semester = " + id_sem + "\n" + "AND CAST(LGS.course AS TEXT) ILIKE '%" + tempCourse + "%'";
        return (List<Long>) getList(getSession().createSQLQuery(query).addScalar("idLGS", LongType.INSTANCE));
    }

    public LinkGroupSemester getLGSbyGroupname (Long id_sem, String groupname) {
        try {
            begin();
            String query =
                    "SELECT LGS.* " + "FROM link_group_semester LGS " + "INNER JOIN dic_group DG ON LGS.id_dic_group = DG.id_dic_group " +
                    "WHERE DG.groupname LIKE '" + groupname + "' AND LGS.id_semester = " + id_sem + " ";
            List<LinkGroupSemester> lgs = getSession().createSQLQuery(query).addEntity(LinkGroupSemester.class).list();
            commit();
            return (lgs != null && lgs.size() > 0) ? lgs.get(0) : null;
        } catch (HibernateException ex) {
            rollback();
            ex.printStackTrace();
            return null;
        }
    }

    public List<LinkGroupSemester> getCustomQuery (String stringQuery) {
        Query q = getSession().createSQLQuery(stringQuery).addEntity(LinkGroupSemester.class);
        return (List<LinkGroupSemester>) getList(q);
    }

    public List<LinkGroupSemester> getAll () throws Exception {
        return getCustomQuery("SELECT * FROM link_group_semester");
    }

    public LinkGroupSemester get (Long id) throws Exception {
        List<LinkGroupSemester> list = getCustomQuery("SELECT * FROM link_group_semester\n" + "WHERE id_link_group_semester " + id);
        return list.size() == 0 ? null : list.get(0);
    }
}
