package org.edec.commission.report.dao;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.edec.commission.model.PeriodCommissionModel;
import org.edec.commission.report.model.CommissionEmployeeModel;
import org.edec.commission.report.model.NotionModel;
import org.edec.commission.report.model.RatingEsoModel;
import org.edec.commission.report.model.notion.NotionDto;
import org.edec.commission.report.model.notion.NotionFilter;
import org.edec.commission.report.model.protocol.ProtocolModel;
import org.edec.commission.report.model.schedule.ListOfStudentByChair;
import org.edec.commission.report.model.schedule.ListOfStudentByFormOfStudy;
import org.edec.dao.DAO;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;

import java.util.Collections;
import java.util.Date;
import java.util.List;


public class CommissionReportDAO extends DAO {
    public List<CommissionEmployeeModel> getCommissionsEmployee(Long idRegCommission) {
        String query = "SELECT\n" + "\tHFteacher.family||' '||HFteacher.name||' '||HFteacher.patronymic AS fio, LD.leader\n" + "FROM\n" +
                "\tregister_comission RC\n" + "\tINNER JOIN led_comission LD USING (id_register_comission)\n" +
                "\tINNER JOIN link_employee_department LED USING (id_link_employee_department)\n" +
                "\tINNER JOIN employee EMP USING (id_employee)\n" + "\tINNER JOIN humanface HFteacher USING (id_humanface)\n" +
                "WHERE\n" + "\tid_register_comission = " + idRegCommission + "\n" + "ORDER BY\n" + "\tLD.leader DESC, fio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("leader")
                .setResultTransformer(Transformers.aliasToBean(CommissionEmployeeModel.class));
        return (List<CommissionEmployeeModel>) getList(q);
    }

    public List<ProtocolModel> getProtocols(Long idCommissionRegister) {
        String query = "SELECT\n" + "\tHF.family||' '||SUBSTRING(HF.name, 1, 1)||'. '||SUBSTRING(HF.patronymic, 1, 1)||'.' AS fio,\n" +
                "\tDS.subjectname AS subject, DG.groupname,\n" + "\tRC.comission_date AS commissionDate," +
                "\tCASE WHEN SRH.is_exam = 1 THEN 'экзамена'\n" + "\t\tWHEN SRH.is_pass = 1 THEN 'зачета'\n" +
                "\t\tWHEN SRH.is_courseproject = 1 THEN 'курсового проекта'\n" +
                "\t\tWHEN SRH.is_coursework = 1 THEN 'курсовой работы'\n" + "\t\tWHEN SRH.is_practic = 1 THEN 'практики'\n" +
                "\tELSE '' END AS formOfControl,\n" + "\tCASE WHEN SRH.newrating = 1 THEN 'зачтено'\n" +
                "\t\tWHEN SRH.newrating = 2 THEN 'неудовлетворительно'\n" + "\t\tWHEN SRH.newrating = 3 THEN 'удовлетворительно'\n" +
                "\t\tWHEN SRH.newrating = 4 THEN 'хорошо'\n" + "\t\tWHEN SRH.newrating = 5 THEN 'отлично'\n" +
                "\t\tWHEN SRH.newrating = -2 THEN 'не зачтено'\n" + "\t\tWHEN SRH.newrating = -3 THEN 'не явился'\n" +
                "\tELSE '' END AS ratingStr\n" + "FROM\n" + "\tregister_comission RC\n" +
                "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "\tINNER JOIN register R USING (id_register)\n" + "\tINNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "\tINNER JOIN institute INST USING (id_institute)\n" + "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN studentcard SC USING (id_studentcard)\n" + "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "WHERE\n" + "\tid_register_comission = " + idCommissionRegister + "\n" + "ORDER BY\n" + "\tfio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("subject")
                .addScalar("ratingStr")
                .addScalar("groupname")
                .addScalar("formOfControl")
                .addScalar("commissionDate")
                .setResultTransformer(Transformers.aliasToBean(ProtocolModel.class));
        return (List<ProtocolModel>) getList(q);
    }

    public List<NotionModel> getNotionModelsByListSSS(String listId) {
        String query =
                "SELECT \n" + "\tSC.recordbook AS recordbook,\n" + "\tHF.family || ' ' || HF.name || ' ' || HF.patronymic AS fio,\n" +
                        "\tDG.groupname AS groupname,\n" + "\tCUR.directioncode as directionNumber,\n" +
                        "\tCASE WHEN SEM.season = 0 THEN 'осеннего' ELSE 'весеннего' END AS season,\n" +
                        "\tCUR.specialitytitle as directionName,\n" +
                        "\tCASE WHEN SEM.formofstudy = 1 THEN 'очная' ELSE 'заочная' END AS formOfStudy,\n" +
                        "\tTO_CHAR(SY.dateofbegin, 'yyyy')||'/'||TO_CHAR(SY.dateofend, 'yyyy') AS schoolYear,\n" +
                        "\t\tCAST((select course from link_group_semester lgs2\n" +
                        "\t\tinner join student_semester_status sss2 using(id_link_group_semester)\n" +
                        "\t\tinner join semester sem2 using(id_semester)\n" +
                        "\t\twhere sss2.id_studentcard = sss.id_studentcard and lgs2.id_dic_group = dg.id_dic_group and sem2.is_current_sem = 1) AS VARCHAR) AS course,\n" +
                        "\tSSS.id_student_semester_status AS idSSS\n" + "from \n" + "\thumanface HF\n" +
                        "\tINNER JOIN studentcard SC USING(id_humanface)\n" + "\tINNER JOIN student_semester_status SSS USING(id_studentcard)\n" +
                        "\tINNER JOIN link_group_semester LGS USING(id_link_group_semester)\n" + "\tINNER JOIN dic_group dg USING(id_dic_group)\n" +
                        "\tINNER JOIN curriculum CUR USING(id_curriculum)\n" + "\tINNER JOIN semester SEM USING(id_semester)\n" +
                        "\tINNER JOIN schoolyear SY USING(id_schoolyear)\n" + "where id_student_semester_status in (" + listId + ")";
        Query q = getSession().createSQLQuery(query)
                .addScalar("recordbook")
                .addScalar("fio")
                .addScalar("groupname")
                .addScalar("directionNumber")
                .addScalar("season")
                .addScalar("directionName")
                .addScalar("formOfStudy")
                .addScalar("schoolYear")
                .addScalar("course")
                .addScalar("idSSS", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(NotionModel.class));
        return (List<NotionModel>) getList(q);
    }

    public List<RatingEsoModel> getMarksForStudentInOrder(Long idSSS) {
        String query = "SELECT \n" +
                "\tSR.is_exam = 1 AS exam, SR.is_pass = 1 AS pass, SR.is_courseproject = 1 AS cp, SR.is_coursework = 1 AS cw, SR.is_practic = 1 AS practic,\n" +
                "\tSR.examrating, SR.passrating, SR.courseprojectrating AS cprating, SR.courseworkrating AS cwrating, SR.practicrating,\n" +
                "\tDS.subjectname,\n" + "\tCASE\n" +
                "\t\tWHEN SEM.season = 0 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend)||' осеннего семестра'\n" +
                "\t\tWHEN SEM.season = 1 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend)||' весеннего семестра'\n" +
                "\tEND AS semester,\n" + "\tsubject.type AS type\n" + "FROM\n" + "\tstudentcard SC\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" + "\tINNER JOIN subject USING (id_subject)\n" +
                "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" + "WHERE\n" + "\tid_dic_group = \n" +
                "\t\t(SELECT id_dic_group FROM link_group_semester lgs2 \n" +
                "\t\t INNER JOIN student_semester_status sss2 USING(id_link_group_semester)\n" +
                "\t\t WHERE sss2.id_student_semester_status = :idSSS)\n" + "\tAND id_studentcard = \n" +
                "\t\t(SELECT id_studentcard FROM student_semester_status sss2\n" +
                "\t\t WHERE sss2.id_student_semester_status = :idSSS)\n" + "\tAND is_current_sem = 0 ORDER BY subjectname\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("exam")
                .addScalar("pass")
                .addScalar("cp")
                .addScalar("cw")
                .addScalar("practic")
                .addScalar("examrating")
                .addScalar("passrating")
                .addScalar("cprating")
                .addScalar("cwrating")
                .addScalar("practicrating")
                .addScalar("subjectname")
                .addScalar("semester")
                .addScalar("type")
                .setParameter("idSSS", idSSS)
                .setResultTransformer(Transformers.aliasToBean(RatingEsoModel.class));
        return (List<RatingEsoModel>) getList(q);
    }

    public List<ListOfStudentByFormOfStudy> getSceduliesByFormOfStudy(Date dateOfBegin, Date dateOfEnd, Integer formOfStudy) {
        String query =
                "SELECT\n" + "  HF.family||' '||HF.name||' '||HF.patronymic AS fio, DG.groupname, SC.recordbook, courseStudent.course,\n" +
                        "  CASE WHEN SSS.is_government_financed = 1 THEN 'Бюджетная' ELSE 'Платная' END\n" +
                        "  || ' основа обучения:' AS formOfStudy\n" + "FROM\n" + "  humanface HF\n" +
                        "  INNER JOIN studentcard SC USING (id_humanface)\n" + "  INNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                        "  INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                        "  INNER JOIN semester SEM USING (id_semester)\n" + "  INNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                        "  INNER JOIN dic_group DG USING (id_dic_group)\n" + "  INNER JOIN (\n" +
                        "    SELECT LGS.course, LGS.id_dic_group AS idDG\n" + "    FROM link_group_semester LGS\n" +
                        "      INNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester AND SEM.is_current_sem = 1\n" +
                        "  ) AS courseStudent ON DG.id_dic_group = courseStudent.idDG\n" + "  INNER JOIN curriculum CUR USING (id_curriculum)\n" +
                        "  INNER JOIN sessionrating SR USING (id_student_semester_status)\n" +
                        "  INNER JOIN sessionratinghistory SRH USING (id_sessionrating)\n" + "  INNER JOIN register R USING (id_register)\n" +
                        "  INNER JOIN register_comission RC USING (id_register)\n" + "WHERE\n" + "  RC.dateofbegincomission = :dateOfBegin\n" +
                        "  AND RC.dateofendcomission = :dateOfEnd\n" + "  AND SEM.formofstudy = :fos\n" + "GROUP BY\n" +
                        "  fio, recordbook, DG.groupname, CUR.qualification, courseStudent.course, SSS.is_government_financed\n" + "ORDER BY\n" +
                        "  courseStudent.course, CUR.qualification, groupname, fio";

        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("groupname")
                .addScalar("recordbook")
                .addScalar("course")
                .addScalar("formOfStudy")
                .setResultTransformer(Transformers.aliasToBean(ListOfStudentByFormOfStudy.class));
        q.setParameter("dateOfBegin", dateOfBegin).setParameter("dateOfEnd", dateOfEnd).setInteger("fos", formOfStudy);
        return (List<ListOfStudentByFormOfStudy>) getList(q);
    }

    public List<ListOfStudentByChair> getListOfModuleByChair(Date dateOfBegin, Date dateOfEnd, Integer formOfStudy) {
        String query = "SELECT\n" + "  HF.family||' '||HF.name||' '||HF.patronymic AS fio, DG.groupname,\n" +
                "  DEP.fulltitle, DS.subjectname||' ('||\n" + "  CASE\n" + "      WHEN SRH.is_exam = 1 THEN 'экзамен'\n" +
                "      WHEN SRH.is_pass = 1 THEN 'зачет'\n" + "      WHEN SRH.is_courseproject = 1 THEN 'курсовой проект'\n" +
                "      WHEN SRH.is_coursework = 1 THEN 'курсовая работа'\n" + "      WHEN SRH.is_practic = 1 THEN 'практика'\n" +
                "  END||')' AS subjectname, SSS.is_government_financed as budget\n" + "FROM\n" + "  humanface HF\n" +
                "  INNER JOIN studentcard SC USING (id_humanface)\n" +
                "  INNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "  INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "  INNER JOIN semester SEM USING (id_semester)\n" + "  INNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                "  INNER JOIN dic_group DG USING (id_dic_group)\n" + "  INNER JOIN curriculum CUR USING (id_curriculum)\n" +
                "  INNER JOIN sessionrating SR USING (id_student_semester_status)\n" +
                "  INNER JOIN sessionratinghistory SRH USING (id_sessionrating)\n" +
                "  INNER JOIN register R USING (id_register)\n" + "  INNER JOIN register_comission RC USING (id_register)\n" +
                "  INNER JOIN subject S ON RC.id_subject = S.id_subject\n" + "  INNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "  INNER JOIN chair C ON S.id_chair = C.id_chair\n" +
                "  INNER JOIN department DEP ON C.id_chair = DEP.id_chair AND DEP.is_main = TRUE\n" + "WHERE\n" +
                "  RC.dateofbegincomission = :dateOfBegin AND RC.dateofendcomission = :dateOfEnd\n" +
                "  AND SEM.formofstudy = :fos\n" + "ORDER BY\n" + "  fio, DEP.fulltitle, DS.subjectname, groupname";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("groupname")
                .addScalar("fulltitle")
                .addScalar("subjectname")
                .addScalar("budget", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ListOfStudentByChair.class));
        q.setParameter("dateOfBegin", dateOfBegin).setParameter("dateOfEnd", dateOfEnd).setInteger("fos", formOfStudy);
        return (List<ListOfStudentByChair>) getList(q);
    }

    public List<NotionDto> getNotionDtoModel(@NonNull NotionFilter notionFilter) {

        PeriodCommissionModel period = notionFilter.getPeriod();
        String idSemester = notionFilter.getIdSemester() != null ? String.valueOf(notionFilter.getIdSemester()) : "";

        String query = "WITH maxSSS AS (SELECT id_studentcard, id_dic_group, MAX(id_student_semester_status) as idSSS\n" +
                "                FROM student_semester_status SSS\n" +
                "                       INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "                       INNER JOIN semester SEM USING (id_semester)\n" +
                "                WHERE CAST(LGS.id_semester AS TEXT) LIKE '%%'\n" +
                "                  AND SEM.formofstudy = " + notionFilter.getFormOfStudy() + "\n" +
                "                  AND SEM.id_institute = " + notionFilter.getIdInst() + "\n" +
                "                GROUP BY id_studentcard, id_dic_group),\n" +
                "studentInfo AS (SELECT HF.family, HF.name, HF.patronymic, HF.id_humanface,\n" +
                "                       DG.id_dic_group, DG.groupname, LGS.course,\n" +
                "                       CUR.specialitytitle AS programTitle, CUR.directioncode AS programCode,\n" +
                "                       DIR.title AS specialityTitle, DIR.code AS specialityCode\n" +
                "                     FROM humanface HF\n" +
                "                            INNER JOIN studentcard SC USING (id_humanface)\n" +
                "                            INNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "                            INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "                            INNER JOIN dic_group DG USING (id_dic_group)\n" +
                "                            INNER JOIN curriculum CUR USING (id_curriculum)\n" +
                "                            LEFT JOIN direction DIR USING (id_direction)\n" +
                "                            INNER JOIN maxSSS ON maxSSS.idSSS = sss.id_student_semester_status)\n" +
                "SELECT studentInfo.*, DS.subjectname AS subjectName, LGS.semesternumber AS semesterNumber,\n" +
                "       CASE WHEN SRH.is_exam = 1 THEN 'Экзамен'\n" +
                "         WHEN SRH.type = 1 AND SRH.is_pass = 1 THEN 'Диф. зачет'\n" +
                "         WHEN SRH.is_pass = 1 THEN 'Зачет'\n" +
                "         WHEN SRH.is_courseproject = 1 THEN 'КП'\n" +
                "         WHEN SRH.is_coursework = 1 THEN 'КР'\n" +
                "         WHEN SRH.is_practic = 1 THEN 'Практика'\n" +
                "         ELSE '???' END         AS focStr\n" +
                "FROM register_comission RC\n" +
                "       INNER JOIN subject S USING (id_subject)\n" +
                "       INNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "       INNER JOIN register R USING (id_register)\n" +
                "       INNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                "       INNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "       INNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "       INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "       INNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "       INNER JOIN dic_group DG USING (id_dic_group)\n" +
                "       INNER JOIN studentcard SC USING (id_studentcard)\n" +
                "       INNER JOIN humanface HF USING (id_humanface)\n" +
                "       INNER JOIN studentInfo ON HF.id_humanface = studentInfo.id_humanface AND DG.id_dic_group = studentInfo.id_dic_group\n" +
                "WHERE CAST(LGS.id_semester AS TEXT) LIKE '%" + idSemester + "%'\n" +
                "  AND HF.family||' '||HF.name||' '||HF.patronymic||' '||DG.groupname||' '||DS.subjectname||' '||\n" +
                "      (SELECT fulltitle FROM department WHERE id_chair = S.id_chair\n" +
                "                                          AND is_main IS true) ILIKE '%" + notionFilter.getCommonFilter() + "%'\n" +
                "  AND SRH.newrating <> 1 AND SRH.newrating < 3\n" +
                "  AND SEM.formofstudy = " + notionFilter.getFormOfStudy() + "\n" +
                "  AND SEM.id_institute = " + notionFilter.getIdInst() + "\n" +
                (period == null ? "" : "\tAND RC.dateofbegincomission = '" + period.getDateOfBegin() + "' AND RC.dateofendcomission = '" + period.getDateOfEnd() + "'\n") +
                "ORDER BY DG.groupname, HF.family, LGS.semesternumber, DS.subjectname";

        Query q = getSession().createSQLQuery(query)
                .addScalar("family").addScalar("name").addScalar("patronymic").addScalar("groupName")
                .addScalar("course").addScalar("programTitle").addScalar("programCode")
                .addScalar("specialityTitle").addScalar("specialityCode")
                .addScalar("subjectName").addScalar("semesterNumber").addScalar("focStr")
                .setResultTransformer(Transformers.aliasToBean(NotionDto.class));

        return (List<NotionDto>) getList(q);
    }
}