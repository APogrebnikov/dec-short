package org.edec.curriculumSchedule.manager;

import org.edec.curriculumSchedule.model.GroupModel;
import org.edec.dao.DAO;
import org.edec.synchroMine.model.mine.Group;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CurriculumScheduleManager extends DAO {

    public List<GroupModel> getGroupsByFilter (long idSemester, int course, boolean isBachelor, boolean isMaster, boolean isEngineer) {
        String query = "SELECT \n" +
                       "    DG.id_dic_group as idDg," +
                       "    LGS.id_link_group_semester as idLgs," +
                       "    DG.groupName as groupName, " +
                       "    DG.dateofbegin as dateOfBeginStudy, " +
                       "    DG.dateofend as dateOfEndStudy, " +
                       "    LGS.dateofbeginsemester as dateOfBeginSemester, " +
                       "    LGS.dateofendsemester as dateOfEndSemester, " +
                       "    LGS.dateofbeginpassweek as dateOfBeginPassWeek, " +
                       "    LGS.dateofendpassweek as dateOfEndPassWeek, " +
                       "    LGS.dateofbeginsession as dateOfBeginSession, " +
                       "    LGS.dateofendsession as dateOfEndSession, " +
                       "    LGS.dateofbeginvacation as dateOfBeginVacation, " +
                       "    LGS.dateofendvacation as dateOfEndVacation, " +
                       "    LGS.dateofbeginpractic as dateOfBeginPractice, " +
                       "    LGS.dateofendpractic as dateOfEndPractice, " +
                       "DG.dateofcertificationfrom as dateOfCertificationFrom," +
                       "DG.dateofcertificationto as dateOfCertificationTo " + "FROM" +
                       "    link_group_semester LGS" +
                       "    INNER JOIN dic_group DG ON LGS.id_dic_group = DG.id_dic_group" +
                       "    INNER JOIN curriculum CUR ON DG.id_curriculum = CUR.id_curriculum" +
                       " WHERE" + "    LGS.id_semester = " + idSemester + (course != 0 ? " AND    LGS.course = " + course : "") + "    AND CUR.qualification IN (" +
                       (isBachelor ? "2" : "null") + ", " + (isMaster ? "3" : "null") + ", " + (isEngineer ? "1" : "null") + ")" +
                       " ORDER BY DG.groupname";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("idDg", LongType.INSTANCE)
                              .addScalar("idLgs", LongType.INSTANCE)
                              .addScalar("groupName")
                              .addScalar("dateOfBeginStudy")
                              .addScalar("dateOfEndStudy")
                              .addScalar("dateOfBeginSemester")
                              .addScalar("dateOfEndSemester")
                              .addScalar("dateOfBeginPassWeek")
                              .addScalar("dateOfEndPassWeek")
                              .addScalar("dateOfBeginSession")
                              .addScalar("dateOfEndSession")
                              .addScalar("dateOfBeginVacation")
                              .addScalar("dateOfEndVacation")
                              .addScalar("dateOfCertificationFrom")
                              .addScalar("dateOfCertificationTo")
                              .addScalar("dateOfBeginPractice")
                              .addScalar("dateOfEndPractice")
                              .setResultTransformer(Transformers.aliasToBean(GroupModel.class));
        return (List<GroupModel>) getList(q);
    }

    public boolean updateSemesterGroupInformation (GroupModel group) {
        String querySemesterGroup =
                "UPDATE link_group_semester \n" + "SET dateofbeginsemester = COALESCE (:dateofbeginsemester, dateofbeginsemester)," +
                " dateofendsemester = COALESCE (:dateofendsemester,dateofendsemester)," +
                " dateofbeginpassweek = COALESCE (:dateofbeginpassweek,dateofbeginpassweek)," +
                " dateofendpassweek = COALESCE (:dateofendpassweek,dateofendpassweek)," +
                " dateofbeginsession = COALESCE (:dateofbeginsession,dateofbeginsession)," +
                " dateofendsession = COALESCE (:dateofendsession,dateofendsession)," +
                " dateofbeginvacation = COALESCE (:dateofbeginvacation,dateofbeginvacation)," +
                " dateofendvacation = COALESCE (:dateofendvacation,dateofendvacation),\n" +
                " dateofbeginpractic = COALESCE (:dateofbeginpractice, dateofbeginpractic),\n" +
                " dateofendpractic = COALESCE (:dateofendpractice, dateofendpractic)\n" +
                "WHERE id_dic_group = :idDicGroup AND id_link_group_semester = :idLgs";
        Query qSemGroup = getSession().createSQLQuery(querySemesterGroup)
                                      .setDate("dateofbeginsemester", group.getDateOfBeginSemester())
                                      .setDate("dateofendsemester", group.getDateOfEndSemester())
                                      .setDate("dateofbeginpassweek", group.getDateOfBeginPassWeek())
                                      .setDate("dateofendpassweek", group.getDateOfEndPassWeek())
                                      .setDate("dateofbeginsession", group.getDateOfBeginSession())
                                      .setDate("dateofendsession", group.getDateOfEndSession())
                                      .setDate("dateofbeginvacation", group.getDateOfBeginVacation())
                                      .setDate("dateofendvacation", group.getDateOfEndVacation())
                                      .setDate("dateofbeginpractice", group.getDateOfBeginPractice())
                                      .setDate("dateofendpractice", group.getDateOfEndPractice())
                                      .setLong("idDicGroup", group.getIdDg())
                                      .setLong("idLgs", group.getIdLgs());

        return executeUpdate(qSemGroup);
    }

    public boolean updateGroupInformation(GroupModel group) {
        String querySemesterGroup = "UPDATE dic_group \n" + "SET dateofbegin = COALESCE (:dateofbegin,dateofbegin)," +
                " dateofend = COALESCE (:dateofend,dateofend),  " +
                "dateofcertificationfrom = COALESCE (:dateofcertificationfrom,dateofcertificationfrom)," +
                "dateofcertificationto = COALESCE (:dateofcertificationto,dateofcertificationto) "
                + "WHERE id_dic_group = :idDicGroup \n";
        Query qSemGroup = getSession().createSQLQuery(querySemesterGroup)
                .setDate("dateofbegin", group.getDateOfBeginStudy())
                .setDate("dateofend", group.getDateOfEndStudy())
                .setDate("dateofcertificationfrom", group.getDateOfCertificationFrom())
                .setDate("dateofcertificationto", group.getDateOfCertificationTo())
                .setLong("idDicGroup", group.getIdDg());

        return executeUpdate(qSemGroup);
    }
}
