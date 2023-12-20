package org.edec.newMine.groups.manager;

import org.edec.dao.DAO;
import org.edec.newMine.groups.model.GroupsModel;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.Date;
import java.util.List;

public class GroupsManagerESO extends DAO {

    public List<GroupsModel> getGroupsBySem(Long idSem) {
        String query = "SELECT distinct DG.groupname AS groupname, LGS.id_link_group_semester AS idLGS, LGS.semesternumber AS semester, cur.id_chair as idChair, \n" +
                "LGS.course AS course, CAST(LGS.otherdbid AS Integer) AS idGroupMine, cur.id_curriculum as idCurriculum, \n" +
                "dep.otherdbid as idChairMineCabinet, cur.directioncode as directionCode, dep.fulltitle as chairName, cur.planfilename as planfileName\n" +
                "FROM dic_group DG \n" +
                "\tINNER JOIN link_group_semester LGS USING (id_dic_group)\n" +
                "\tjoin curriculum cur using (id_curriculum)\n" +
                "\tjoin department dep on cur.id_chair = dep.id_chair\n" +
                "WHERE LGS.id_semester = :idSem  AND DG.is_active = 1 " +
                "and dep.otherdbid is not null " +
                "and dep.is_main = true\n" +
                "ORDER BY LGS.course, groupname";
        Query q = getSession().createSQLQuery(query)
                .addScalar("groupname").addScalar("idLGS", LongType.INSTANCE)
                .addScalar("semester").addScalar("idChair", LongType.INSTANCE)
                .addScalar("course").addScalar("idGroupMine")
                .addScalar("idCurriculum", LongType.INSTANCE)
                .addScalar("idChairMineCabinet", LongType.INSTANCE)
                .addScalar("directionCode")
                .addScalar("chairName")
                .addScalar("planfileName")
                .setResultTransformer(Transformers.aliasToBean(GroupsModel.class));
        q.setLong("idSem", idSem);
        return (List<GroupsModel>) getList(q);
    }

    public boolean updateGroup(Long idLGS, Integer idGroupMine) {
        String query = "UPDATE link_group_semester SET otherdbid = :idGroupMine WHERE id_link_group_semester = :idLGS";
        Query q = getSession().createSQLQuery(query).setLong("idGroupMine", idGroupMine).setLong("idLGS", idLGS);
        return executeUpdate(q);
    }

    public boolean updateChairs(Long idCurriculum, Integer idChairMine) {
        String query = "update curriculum set id_chair = (select id_chair from department where otherdbid = "+idChairMine+") \n" +
                "where id_curriculum = " + idCurriculum;
        Query q = getSession().createSQLQuery(query);
        return executeUpdate(q);
    }

    public boolean updatePlanFileName(Long idCurriculum, String planFileName) {
        String query = "update curriculum set planfilename = '"+planFileName+"' \n" +
                "where id_curriculum = " + idCurriculum;
        Query q = getSession().createSQLQuery(query);
        return executeUpdate(q);
    }

    public Long getIdSchoolYearByBeginDate(Date dateBegin) {
        String query = "SELECT id_schoolyear FROM schoolyear WHERE EXTRACT(YEAR FROM dateofbegin) = " +
                DateConverter.convertDateToYearString(dateBegin);
        Query q = getSession().createSQLQuery(query).addScalar("id_schoolyear", LongType.INSTANCE);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long getIdChairByNameAndODI(String fulltitle, Integer odi) {
        String query = "SELECT id_chair FROM department WHERE otherdbid = :odi OR fulltitle LIKE :fulltitle";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id_chair", LongType.INSTANCE)
                .setLong("odi", odi)
                .setString("fulltitle", fulltitle);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long getIdDirectionByTitleAndCode(String title, String code) {
        String query = "SELECT id_direction FROM direction WHERE" +
              //  " title LIKE :title AND " +
                " code LIKE :code";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id_direction", LongType.INSTANCE)
              //  .setString("title", title)
                .setString("code", code);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long createOrGetCurriculum(GroupsModel data, Long idCreatedSchoolYear, Long idEnterSchoolYear) {
        String query =
                "SELECT create_curriculum(" + idEnterSchoolYear + ", " + idCreatedSchoolYear + ",'" + data.getPlanfileName() + "', " +
                        data.getQualification() + ", '" +
                        (data.getQualificationCode() == null ? data.getDirectionCode() : data.getQualificationCode()) + "', '" +
                        (data.getDirectionTitle() == null ? data.getSpecialityTitle() : data.getDirectionTitle()) + "', " + data.getIdChair() +
                        ", " + data.getIdDirection() + ", " + data.getFormOfStudy() + ", " + data.getPeriodOfStudy() + ", " + data.getGeneration() +
                        ") AS idCurriculum";
        Query q = getSession().createSQLQuery(query).addScalar("idCurriculum", LongType.INSTANCE);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long createOrGetDicGroup(Long idCurriculum, Long idInst, Boolean military, String groupname) {
        String query = "SELECT create_dic_group(" + idCurriculum + ", " + idInst + ", " + (military ? 0 : 1) + ", '" + groupname +
                "') AS idDicGroup";
        Query q = getSession().createSQLQuery(query).addScalar("idDicGroup", LongType.INSTANCE);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long createOrGetLGS(Integer course, Integer semesterNumber, Long idDicGroup, Long idSem, Integer idGroupMine) {
        String query = "SELECT create_link_group_semester(" + course + ", " + semesterNumber + ", " + idDicGroup + ", " + idSem + ", " +
                idGroupMine + ") AS idLGS";
        Query q = getSession().createSQLQuery(query).addScalar("idLGS", LongType.INSTANCE);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public void transferGroupsInSemester(String year, Long idSem, boolean springSem){
        String query = "INSERT INTO Link_Group_Semester\n" +
                "SELECT\n" +
                "    --DG.GroupName,\n" +
                "    nextval('link_group_semester_id_sequence' ) AS ID_Link_Group_Semester,    \n" +
                "    S.ID_Semester,\n" +
                "    DG.ID_Dic_Group,\n" +
                "    date '2000-01-01' AS dateofbeginsemester, -- Дата начала семестра\n" +
                "    date '2000-01-01' AS dateofendsemester, -- Дата окончания семестра\n" +
                "    date '2000-01-01' AS dateofbeginsession, -- Дата начала сессии\n" +
                "    date '2000-01-01' AS dateofendsession, -- Дата окончания сессии\n" +
                "    date '2000-01-01' AS dateofbeginpassweek, -- Дата начала зачётной недели\n" +
                "    date '2000-01-01' AS dateofendpassweek, -- Дата окончания зачётной недели\n" +
                "    date '2000-01-01' AS dateofbeginvacation, -- Дата начала каникул\n" +
                "    date '2000-01-01' AS dateofendvacation, -- Дата окончания каникул\n" +
                "    date '2000-01-01' AS dateofbegingrant, -- Дата начала выплаты стипендии\n" +
                "    date '2000-01-01' AS dateofendgrant, -- Дата окончания выплаты стипендии\n" +
                "    ( EXTRACT( YEAR FROM SY.DateOfBegin ) - EXTRACT( YEAR FROM DG.DateOfBegin ) ) * 2 \n" +
                "    + CASE WHEN S.ID_Semester = ( SELECT MIN( ID_Semester ) FROM Semester MS WHERE MS.ID_SchoolYear = SY.ID_SchoolYear and Cur.FormOfStudy = MS.FormOfStudy ) THEN 1 ELSE 2 END AS SemesterNumber,\n" +
                "    ( EXTRACT( YEAR FROM SY.DateOfBegin ) - EXTRACT( YEAR FROM DG.DateOfBegin ) + 1 ) AS Course\n" +
                "FROM\n" +
                "    Dic_Group DG,\n" +
                "    Curriculum Cur,\n" +
                "    Semester S,\n" +
                "    SchoolYear SY\n" +
                "WHERE\n" +
                "    S.ID_SchoolYear = SY.ID_SchoolYear\n" +
                "    AND\n" +
                "    Cur.ID_Curriculum = DG.ID_Curriculum\n" +
                "    AND\n" +
                "    Cur.FormOfStudy = S.FormOfStudy\n" +
                "    AND\n" +
                "    DG.ID_Institute = S.ID_Institute\n" +
                "    AND\n" +
                "    S.ID_semester IN ("+idSem+")\n" +
                "    AND NOT EXISTS\n" +
                "    (\n" +
                "        SELECT *\n" +
                "        FROM \n" +
                "            Link_Group_Semester LGS_\n" +
                "        WHERE\n" +
                "            LGS_.ID_Semester = S.ID_Semester\n" +
                "            AND\n" +
                "            LGS_.ID_Dic_Group = DG.ID_Dic_Group\n" +
                "    ) \n" + (springSem == true ? "AND dg.dateofend > '"+year+"-04-01' " : "AND dg.dateofend > '"+year+"-09-01' " );
        Query q = getSession().createSQLQuery(query);
        q.executeUpdate();
    }

}
