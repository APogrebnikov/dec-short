package org.edec.factSheet.manager;

import org.edec.dao.DAO;
import org.edec.factSheet.model.FactSheetAddModel;
import org.edec.factSheet.model.FactSheetCounterYearModel;
import org.edec.factSheet.model.TypeFactSheetModel;
import org.edec.factSheet.model.FactSheetTableModel;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.Date;
import java.util.List;

public class FactSheetManager extends DAO {

    public static final String INSERT_FACT_SHEET = "INSERT INTO fact_sheet (id_humanface, register_number, id_type_fact_sheet, id_fact_sheet_status, official_seal, created_by_student, groupname)\n" +
            "VALUES (:idHum, :registerNumber, :idTypeFactSheet, :idFactSheetStatus, :officialSeal, :createdByStudent, :groupname)";

    private List<FactSheetCounterYearModel> counterYear;

    public boolean addFactSheet(Long idHum, int idTypeFactSheet, boolean officialSeal, boolean createdByStudent, String groupname) {
        Query q = getSession().createSQLQuery(INSERT_FACT_SHEET)
                .setParameter("idHum", idHum)
                .setParameter("registerNumber", "")
                .setParameter("idTypeFactSheet", idTypeFactSheet)
                .setParameter("idFactSheetStatus", 0)
                .setParameter("officialSeal", officialSeal)
                .setParameter("createdByStudent", createdByStudent)
                .setString("groupname", groupname);
        return executeUpdate(q);
    }

    public boolean setRegisterNumber(Long idFactSheet, String groupname) {
        counterYear = getCounterYear(groupname);
        int count = (int) getSession().createSQLQuery(
                "UPDATE counter_year SET counter_fact_sheet = (counter_fact_sheet + 1) " + " WHERE EXTRACT(YEAR FROM year) = " +
                        DateConverter.convertDateToYearString(new Date()) + " AND id_institute = " + counterYear.get(0).getIdInstitute() +
                        " AND formofstudy = " + counterYear.get(0).getFormofstudy() + " RETURNING counter_fact_sheet").uniqueResult();
        String query = "UPDATE fact_sheet\n" + "\tSET register_number = :registerNumber\n" + "\tWHERE id_fact_sheet = :idFactSheet";
        Query q = getSession().createSQLQuery(query)
                .setParameter("registerNumber", Integer.toString(count))
                .setLong("idFactSheet", idFactSheet);
        return executeUpdate(q);
    }

    public boolean deleteFactSheet(Long idFactSheet) {
        String query = "DELETE FROM fact_sheet\n" + "\tWHERE id_fact_sheet = :idFactSheet";
        Query q = getSession().createSQLQuery(query).setLong("idFactSheet", idFactSheet);
        return executeUpdate(q);
    }

    public List<FactSheetTableModel> getFactSheetsHistory(Long idHum) {
        String query = "SELECT DISTINCT HF.family, HF.name, HF.patronymic, FS.groupname, FS.is_receipt AS isReceipt, \n" +
                "FS.register_number AS registerNumber, FS.id_fact_sheet_status AS idFactSheetStatus, TFS.title, " +
                "FS.official_seal AS officialSeal, FS.date_create AS dateCreate, FS.date_completion AS dateCompletion, " +
                "FS.id_fact_sheet AS idFactSheet\n" + "FROM studentcard SC\n" +
                "INNER JOIN humanface HF ON SC.id_humanface = HF.id_humanface \n" +
                "INNER JOIN fact_sheet FS ON SC.id_humanface = FS.id_humanface\n" +
                "INNER JOIN type_fact_sheet TFS ON FS.id_type_fact_sheet = TFS.id_type_fact_sheet\n" +
                "WHERE FS.id_humanface = :idHum\n" + "ORDER BY FS.date_create DESC";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family")
                .addScalar("name")
                .addScalar("patronymic")
                .addScalar("groupname")
                .addScalar("registerNumber")
                .addScalar("idFactSheetStatus")
                .addScalar("title")
                .addScalar("officialSeal")
                .addScalar("dateCreate")
                .addScalar("dateCompletion")
                .addScalar("isReceipt")
                .addScalar("idFactSheet", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(FactSheetTableModel.class));
        q.setParameter("idHum", idHum);
        return (List<FactSheetTableModel>) getList(q);
    }

    private List<FactSheetCounterYearModel> getCounterYear(String groupname) {
        String query = "SELECT SEM.id_institute AS idInstitute, SEM.formofstudy\n" + "                FROM dic_group DG \n" +
                "                INNER JOIN link_group_semester LGS USING (id_dic_group) \n" +
                "                INNER JOIN semester SEM USING (id_semester) \n" +
                "                WHERE DG.groupname = :groupname order by SEM.id_semester desc";
        Query q = getSession().createSQLQuery(query)
                .addScalar("formofstudy")
                .addScalar("idInstitute", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(FactSheetCounterYearModel.class));
        q.setParameter("groupname", groupname);
        return (List<FactSheetCounterYearModel>) getList(q);
    }

    public List<FactSheetTableModel> getAllFactSheetsByFilter(String fullName, String groupname, String title, String status,
                                                              String regNumber, Date dateCreateFrom, Date dateCreateTo, Long idInst,
                                                              Integer formOfStudy, Date year) {

        String currentDayMinusTwoMonths = DateConverter.convertDateToStringByFormat(DateConverter.addMonthsToDate(new Date(), -2), "yyyy-MM-dd");

        String query =
                "SELECT DISTINCT (FS.register_number = '') as isNullRegNumber, FS.id_humanface AS idHumanface, HF.family, HF.name, HF.patronymic, DG.groupname, HF.get_notification AS getNotification, HF.email, \n" +
                        "FS.register_number AS registerNumber, FS.id_fact_sheet_status AS idFactSheetStatus, TFS.title, " +
                        "FS.official_seal AS officialSeal, FS.date_create AS dateCreate, FS.date_completion AS dateCompletion, " +
                        "FS.is_receipt AS isReceipt, FS.id_fact_sheet AS idFactSheet, FS.date_receipt AS dateReceipt, FS.created_by_student AS createdByStudent,\n" +
                        "cast((substring(fs.register_number, '^[0-9]+')) as numeric)\n" +

                        "FROM humanface HF\n" +
                        "INNER JOIN studentcard SC USING (id_humanface)\n" +
                        "INNER JOIN fact_sheet FS ON SC.id_humanface = FS.id_humanface\n" +
                        "INNER JOIN type_fact_sheet TFS ON FS.id_type_fact_sheet = TFS.id_type_fact_sheet\n" +
                        "INNER JOIN dic_group DG ON FS.groupname = DG.groupname\n" +
                        "INNER JOIN link_group_semester LGS ON LGS.id_dic_group = DG.id_dic_group\n" +
                        "INNER JOIN semester SEM USING (id_semester)\n" +

                        "WHERE SEM.id_institute = :idInst AND SEM.formofstudy = :fos and FS.date_create > '" + currentDayMinusTwoMonths + "'\n" +
                        "AND HF.family||' '||HF.name||' '||HF.patronymic ILIKE '%" + fullName + "%' \n" + "AND DG.groupname LIKE '%" + groupname +
                        "%' AND TFS.title LIKE '%" + title + "%' AND \n" +
                        (status.equals("") ? "" : "id_fact_sheet_status IN (" + status + ") AND ") + "CAST (FS.register_number AS TEXT) LIKE '%" +
                        regNumber + "%'\n" +
                        (dateCreateFrom != null ? "AND FS.date_create >= '" + DateConverter.convertTimestampToString(dateCreateFrom) + "'\n" : "") +
                        (dateCreateTo != null ? "AND FS.date_create <= '" + DateConverter.convertTimestampToString(dateCreateTo) + "'\n" : "") +
                        (year != null ? "AND EXTRACT(YEAR FROM date_create) = '" + DateConverter.convertDateToYearString(year) + "'\n" : "") +
                        "ORDER BY FS.id_fact_sheet_status, isNullRegNumber desc, cast((substring(fs.register_number, '^[0-9]+')) as numeric) desc";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idHumanface", LongType.INSTANCE)
                .addScalar("family")
                .addScalar("name")
                .addScalar("patronymic")
                .addScalar("groupname")
                .addScalar("getNotification")
                .addScalar("email")
                .addScalar("dateReceipt")
                .addScalar("createdByStudent")
                .addScalar("registerNumber")
                .addScalar("idFactSheetStatus")
                .addScalar("title")
                .addScalar("officialSeal")
                .addScalar("dateCreate")
                .addScalar("dateCompletion")
                .addScalar("isReceipt")
                .addScalar("idFactSheet", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(FactSheetTableModel.class));
        q.setLong("idInst", idInst).setInteger("fos", formOfStudy);

        return (List<FactSheetTableModel>) getList(q);
    }

    public List<TypeFactSheetModel> getTypeFactSheet() {
        String query = "SELECT id_type_fact_sheet AS idTypeFactSheet, title FROM type_fact_sheet";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idTypeFactSheet")
                .addScalar("title")
                .setResultTransformer(Transformers.aliasToBean(TypeFactSheetModel.class));
        return (List<TypeFactSheetModel>) getList(q);
    }

    public List<FactSheetAddModel> getSearchStudents(String fullName, String groupName, String recordBook, Long idInst,
                                                     Integer formOfStudy) {
        String query =
                "SELECT HF.id_humanface AS idHumanface, HF.family||' '||HF.name||' '||HF.patronymic AS fullName, DG.groupname, SC.recordbook, HF.email," +
                        "HF.get_notification AS getNotification, MAX(SSS.is_deducted) = 1 AS deducted, MAX(SSS.is_academicleave) = 1 AS academicleave,\n" +
                        "CASE WHEN (DG.id_dic_group = SC.id_current_dic_group) THEN true\n" + "ELSE false\n" + "END AS current\n" +
                        "FROM studentcard SC\n" + "INNER JOIN humanface HF ON SC.id_humanface = HF.id_humanface \n" +
                        "INNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                        "INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" + "INNER JOIN semester SEM USING (id_semester)" +
                        "INNER JOIN dic_group DG ON LGS.id_dic_group = DG.id_dic_group\n" +
                        "WHERE SEM.id_institute = :idInst AND SEM.formofstudy = :fos\n" +
                        "AND HF.family||' '||HF.name||' '||HF.patronymic ILIKE '%" + fullName + "%' " + "AND DG.groupname LIKE '%" + groupName +
                        "%' AND SC.recordbook LIKE '%" + recordBook + "%'\n" +
                        "GROUP BY idHumanface, DG.groupname, SC.recordbook, DG.id_dic_group, SC.id_current_dic_group\n" + "ORDER BY fullName ";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idHumanface", LongType.INSTANCE)
                .addScalar("fullName")
                .addScalar("groupName")
                .addScalar("recordBook")
                .addScalar("email")
                .addScalar("getNotification")
                .addScalar("deducted")
                .addScalar("academicleave")
                .addScalar("current")
                .setResultTransformer(Transformers.aliasToBean(FactSheetAddModel.class));
        q.setLong("idInst", idInst).setInteger("fos", formOfStudy);
        return (List<FactSheetAddModel>) getList(q);
    }

    public List<FactSheetAddModel> getHumanInfo(Long idHum) {
        String query = "SELECT email, family, name, patronymic, get_notification AS getNotification " + "FROM humanface " +
                "WHERE :idHum = id_humanface";
        Query q = getSession().createSQLQuery(query)
                .addScalar("email")
                .addScalar("family")
                .addScalar("name")
                .addScalar("patronymic")
                .addScalar("getNotification")
                .setResultTransformer(Transformers.aliasToBean(FactSheetAddModel.class));
        q.setParameter("idHum", idHum);
        return (List<FactSheetAddModel>) getList(q);
    }

    public boolean updateStatus(int idFactSheetStatus, Long idFactSheet) {
        String query = "UPDATE fact_sheet\n" + "\tSET id_fact_sheet_status = :idFactSheetStatus\n" + "\tWHERE id_fact_sheet = :idFactSheet";
        Query q = getSession().createSQLQuery(query)
                .setParameter("idFactSheetStatus", idFactSheetStatus)
                .setLong("idFactSheet", idFactSheet);
        return executeUpdate(q);
    }

    public boolean updateCompletion(Long idFactSheet) {
        String query = "UPDATE fact_sheet\n" + "\tSET date_completion = now()\n" + "\tWHERE id_fact_sheet = :idFactSheet";
        Query q = getSession().createSQLQuery(query).setLong("idFactSheet", idFactSheet);
        return executeUpdate(q);
    }

    public boolean updateReceipt(Long idFactSheet) {
        String query =
                "UPDATE fact_sheet\n" + "\tSET is_receipt = :isReceipt, date_receipt = now()\n" + "\tWHERE id_fact_sheet = :idFactSheet";
        Query q = getSession().createSQLQuery(query).setParameter("isReceipt", true).setLong("idFactSheet", idFactSheet);
        return executeUpdate(q);
    }

    public List<String> getStudentGroups(Long idHumanFace) {
        String query = "SELECT DISTINCT(g.groupname) FROM student_semester_status\n" +
                "\tINNER JOIN studentcard s2 ON student_semester_status.id_studentcard = s2.id_studentcard\n" +
                "\tINNER JOIN humanface h2 ON s2.id_humanface = h2.id_humanface\n" +
                "\tINNER JOIN link_group_semester semester ON student_semester_status.id_link_group_semester = semester.id_link_group_semester\n" +
                "\tINNER JOIN dic_group g ON semester.id_dic_group = g.id_dic_group\n" +
                "\tWHERE h2.id_humanface = :idHumanFace\n" +
                "\tAND s2.id_current_dic_group = g.id_dic_group\n" +
                "\tORDER BY groupname DESC";

        Query q = getSession().createSQLQuery(query);

        q.setParameter("idHumanFace", idHumanFace, LongType.INSTANCE);

        return (List<String>) getList(q);
    }
}
