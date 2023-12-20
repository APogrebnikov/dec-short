package org.edec.synchroMine.manager.groupSync;

import org.edec.dao.DAO;
import org.edec.model.SemesterModel;
import org.edec.synchroMine.model.CounterRegisterModel;
import org.edec.synchroMine.model.GroupCompareResult;
import org.edec.synchroMine.model.SchoolYearModel;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;

import java.util.List;

public class CabinetGroupSyncDAO extends DAO {

    private static final String SELECT_GROUPS_BY_ID_SEMESTER =
            "SELECT DG.groupname AS \"groupname\", LGS.id_link_group_semester AS \"idLGS\", LGS.semesternumber AS \"semester\", cur.id_chair as \"idChair\", \n" +
            "\tLGS.course AS \"course\", CAST(LGS.otherdbid AS Integer) AS \"idGroupMine\", cur.id_curriculum as \"idCurriculum\", \n" +
            "\tdep.otherdbid as \"idChairMineCabinet\", cur.directioncode as \"directionCode\", dep.fulltitle as \"chairName\"\n" +
            "FROM dic_group DG \n" +
            "\tINNER JOIN link_group_semester LGS USING (id_dic_group)\n" +
            "\tjoin curriculum cur using (id_curriculum)\n" +
            "\tjoin department dep on cur.id_chair = dep.id_chair\n" +
            "WHERE LGS.id_semester = :idSem and dep.otherdbid is not null and dep.is_main = true \n" +
            "ORDER BY LGS.course, groupname";

    public List<GroupMineModel> getGroupsBySem(Long idSem) {
        Query q = getSession().createSQLQuery(SELECT_GROUPS_BY_ID_SEMESTER)
                .setResultTransformer(Transformers.aliasToBean(GroupMineModel.class));
        q.setLong("idSem", idSem);
        return (List<GroupMineModel>) getList(q);
    }

    public List<GroupCompareResult> getGroupsBySem2(Long idSem) {
        Query q = getSession().createSQLQuery(SELECT_GROUPS_BY_ID_SEMESTER)
                .setResultTransformer(Transformers.aliasToBean(GroupCompareResult.class));
        q.setLong("idSem", idSem);
        return (List<GroupCompareResult>) getList(q);
    }

    public SchoolYearModel getLastSchoolYear() {
        String query = "select sy.id_schoolyear as idSY, sy.dateofbegin as beginYear, sy.dateofEnd as endYear, \n" +
                "sy.otherdbid as idOtherDB, sy.current_year as isCurrentYear\n" +
                "from schoolyear sy\n" +
                "order by sy.id_schoolyear desc limit 1";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idSY", LongType.INSTANCE)
                .addScalar("idOtherDB", LongType.INSTANCE)
                .addScalar("beginYear")
                .addScalar("endYear")
                .addScalar("isCurrentYear")
                .setResultTransformer(Transformers.aliasToBean(SchoolYearModel.class));

        return (SchoolYearModel) q.uniqueResult();
    }
    public Long getIdSem(int formOfStudy, Long idInst, Long idSY, int curSem){
        String query = "select id_semester \n" +
                "from semester \n" +
                "where is_current_sem = "+curSem+" and formofstudy = " + formOfStudy +
                "and id_institute = "+idInst+" and id_schoolyear = " + idSY;
        Query q = getSession().createSQLQuery(query).addScalar("id_semester", LongType.INSTANCE);
        return (Long) q.uniqueResult();
    }
    public void  createStudentsInSemester(Long oldSem, Long newSem){
        String query = "INSERT INTO Student_Semester_Status\n" +
                " SELECT\n" +
                "    nextval('student_semester_status_id_sequence' ) AS ID_Student_Semester_Status,\n" +
                "    SSS.ID_StudentCard,\n" +
                "    LGS__.id_link_group_semester AS ID_Link_Group_Semester,\n" +
                "    SSS.Is_government_financed,  \n" +
                "    SSS.Is_put_app_for_social_grant,  \n" +
                "    SSS.Is_get_social_grant,  \n" +
                "    0 AS Is_Deducted, \n" +
                "    SSS.Is_Scientificwork,  \n" +
                "    SSS.Is_Publicwork,  \n" +
                "    SSS.Is_Chernobolec,  \n" +
                "    SSS.Is_sirota,  \n" +
                "    SSS.Is_invalid,  \n" +
                "    0 AS Is_SessionProlongation,  \n" +
                "    SSS.Is_Combatants,  \n" +
                "    SSS.Is_AcademicLeave,  \n" +
                "    SSS.Is_Listener,  \n" +
                "    -5 AS SessionResult,  \n" +
                "    SSS.Is_TrustAgreement, \n" +
                "    SSS.FormOfStudy, \n" +
                "    SSS.Is_SecondDegree,  \n" +
                "    SSS.Is_Group_Leader  \n" +
                "FROM\n" +
                "    Student_Semester_Status SSS\n" +
                "    INNER JOIN StudentCard SC ON SC.ID_StudentCard = SSS.ID_StudentCard\n" +
                "    INNER JOIN HumanFace HF ON HF.ID_HumanFace = SC.ID_HumanFace\n" +
                "    INNER JOIN Link_Group_Semester LGS ON LGS.ID_Link_Group_Semester = SSS.ID_Link_Group_Semester \n" +
                "    LEFT JOIN Link_Group_Semester LGS__ ON LGS__.ID_Dic_Group = LGS.ID_Dic_Group AND LGS__.ID_Semester = "+newSem+"\n" +
                " join dic_group dg on dg.id_dic_group = sc.id_current_dic_group and dg.id_dic_group = lgs__.id_dic_group " +
                "WHERE\n" +
                "    LGS.ID_Semester = "+oldSem+
                "    AND\n" +
                "    HF.Is_Active = 1\n" +
                "    AND\n" +
                "    SSS.IS_Deducted = 0\n" +
                "    AND \n" +
                "    LGS__.id_link_group_semester IS NOT NULL" +
                "    and (sss.id_studentcard not in (select sss.id_studentcard from  Student_Semester_Status SSS\n" +
                "   JOIN Link_Group_Semester LGS ON LGS.ID_Link_Group_Semester = SSS.ID_Link_Group_Semester \n" +
                "   where lgs.id_semester = "+newSem+")\n" +
                "and sss.id_link_group_semester not in (select sss.id_link_group_semester from  Student_Semester_Status SSS\n" +
                "   JOIN Link_Group_Semester LGS ON LGS.ID_Link_Group_Semester = SSS.ID_Link_Group_Semester \n" +
                "   where lgs.id_semester = "+newSem+")) ";

        Query q = getSession().createSQLQuery(query);
        q.executeUpdate();
    }
    public List<SemesterModel> getLastSemesters(int limit){
        String query = "select s.id_semester as idSem, s.id_schoolyear as idSchoolYear,  \n" +
                "s.is_current_sem as curSem, s.season as season, s.firstweek as firstWeek \n" +
                "from semester s\n" +
                "order by id_semester desc limit " + limit;
        Query q = getSession().createSQLQuery(query)
                .addScalar("idSem", LongType.INSTANCE)
                .addScalar("idSchoolYear", LongType.INSTANCE)
                .addScalar("curSem", BooleanType.INSTANCE)
                .addScalar("season")
                .addScalar("firstWeek")
                .setResultTransformer(Transformers.aliasToBean(SemesterModel.class));

        return  q.list();
    }

    public int updateCurrentSem(Long idSem, int curSem){
        String query = "update semester set is_current_sem = "+ curSem+" where id_semester = "+idSem;
        Query q = getSession().createSQLQuery(query);
        return q.executeUpdate();
    }
    public List<CounterRegisterModel> getSemestersForRegisters(){
        String query = "with sem as (select id_semester \n" +
                "from semester\n" +
                "where is_current_sem = 1\n" +
                "order by id_semester desc limit 2)\n" +
                "\n" +
                "select c.id_counters as idCounters, c.id_semester as idSemFromCounters, c.register_counter as counterRegister, sem.id_semester as idSem\n" +
                "from sem \n" +
                "left join counters c using (id_semester)";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idCounters", LongType.INSTANCE)
                .addScalar("idSem", LongType.INSTANCE)
                .addScalar("idSemFromCounters", LongType.INSTANCE)
                .addScalar("counterRegister", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(CounterRegisterModel.class));
        return q.list();
    }

    public void  updateCounters(Long idSem, Long counterRegister){
        String query = "insert into counters (id_semester, register_counter) values ("+idSem+", "+counterRegister+")";
        Query q = getSession().createSQLQuery(query);
        q.executeUpdate();
    }
}
