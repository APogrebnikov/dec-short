package org.edec.successful.manager;

import org.edec.dao.DAO;
import org.edec.successful.model.RatingModel;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.GovFinancedConst;
import org.edec.utility.converter.DateConverter;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;

import java.util.Collections;
import java.util.Date;
import java.util.List;


public class SuccessfulEsoDAO extends DAO {

    public List<RatingModel> getRatingByFilter (Long idInst, Long idSemester, Long idDepart, FormOfStudy fos, GovFinancedConst govFin,
                                                String levels, String groupName, Date lastDate, String courses, Long idChair, boolean includeAcademics,
                                                boolean includePractic, boolean includeFizRa, boolean onlyPassWeek) {

        String lastDateCheck = " (RE.signdate < '" + DateConverter.convertTimestampToSQLStringFormate(lastDate != null ? lastDate : new Date()) +
                               "' OR RE.signdate IS NULL)";
        String instCheck = idInst != null ? " AND SE.id_institute = " + idInst : "";
        String semestrCheck = idSemester != null ? " AND SE.id_semester = " + idSemester : "";
        String departCheck = idDepart != null ? " AND DE.id_department = " + idDepart : "";
        String fosCheck = fos != null && fos != FormOfStudy.ALL ? " AND CU.formofstudy = " + fos.getType() : "";
        String govFinCheck = (govFin != null && govFin.getType() != null) ? " AND SSS.is_government_financed = " + govFin.getType() : "";
        String levelCheck = (levels != null && !levels.equals("")) ? " AND CU.qualification IN (" + levels + ")" : "";
        String groupNameCheck = (groupName != null && (!groupName.equals("-") && !groupName.equals("")))
                                ? " AND DG.groupname like '" + groupName + "'"
                                : " AND DG.groupname like '%%'";
        String coursesCheck = (courses != null && !courses.equals("")) ? " AND LGS.course IN (" + courses + ")" : "";
        String chairCheck = (idChair != null) ? " AND SU.id_chair = " + idChair : "";
        String currentGroupCheck = " AND SC.id_current_dic_group = DG.id_dic_group";
        String retakeCheck = " AND SRH.retake_count > 0 ";
        String academicCheck = !includeAcademics ? " AND (not isstudentacademicleave(SC.id_studentcard) or isstudentacademicleave(SC.id_studentcard) is null) " : "";
        String practicCheck = !includePractic ? " AND not issubjectpractic(SU.id_subject) " : "";
        String fizRaCheck = !includeFizRa ? " AND not is_physcical_culture(DS.subjectname) " : "";
        String deductCheck = " AND SSS.is_deducted = 0 AND " +
                             (!includeAcademics
                                ? "isstudentdeductedcurrentsem(SC.id_studentcard) is not null "
                                : "(isstudentdeductedcurrentsem(SC.id_studentcard) is not null or isstudentacademicleave(SC.id_studentcard))"
                             );
        String passWeekCheck = onlyPassWeek ? " AND \n" + "(case\n" + "         when SU.is_pass = 1 then lgss.passdate\n" +
                               "         when SU.is_exam = 1 then lgss.examdate end\n" + ") >= lgs.dateofbeginpassweek AND\n" + "(case\n" +
                               "         when SU.is_pass = 1 then lgss.passdate\n" +
                               "         when SU.is_exam = 1 then lgss.examdate end\n" + ") <= lgs.dateofendpassweek " : "";

        String query =
                "SELECT \tSC.recordbook, DG.id_dic_group AS idGroup,\n" +
                "                       SSS.id_student_semester_status AS idStudent,\n" +
                "                       HF.id_humanface AS idHumanface,\n" +
                "                       SC.id_studentcard AS idStudentcard,\n" + "                       SE.id_semester AS idSemester,\n" +
                "                       SU.id_subject AS idSubject,\n" + "                       DG.groupname AS groupname,\n" +
                "                       HF.family || ' ' || HF.name || ' ' || HF.patronymic AS fio,\n" +
                "                       DS.subjectname AS subjectName,\n" + "                       LGS.course AS course,\n" +
                "                       CU.formofstudy AS formOfStudy,\n" + "                       DR.code AS specialityCode,\n" +
                "                       CU.qualification AS lvl,\n" +
                "                       SSS.is_government_financed AS govFinanced,\n" + "                       SRH.is_pass AS pass,\n" +
                "                       SRH.is_exam AS exam,\n" + "                       SRH.is_courseproject AS cp,\n" +
                "                       SRH.is_coursework AS cw,\n" + "                       SRH.is_practic AS practic,\n" +
                "                       max(SRH.newrating) AS rating,\n" + "                       max(RE.signdate) AS signdate,\n" +
                "                       CASE WHEN DE2.shorttitle is not null THEN '(' || DE2.shorttitle || ') ' || DE2.fulltitle\n" +
                "                       ELSE DE2.fulltitle\n" + "                       END AS tChairFulltitle,\n" +
                "                       CH2.id_chair AS tChairId,\n" + "                       DE.fulltitle AS eChairFulltitle,\n" +
                "                       DE.shorttitle AS eChairShorttitle,\n" + "                       CH.id_chair AS eChairId\n" +
                "FROM\n" + "  semester SE\n" + "  INNER JOIN link_group_semester LGS USING (id_semester)\n" +
                "  INNER JOIN dic_group DG USING (id_dic_group)\n" + "  INNER JOIN curriculum CU ON DG.id_curriculum=CU.id_curriculum\n" +
                "  LEFT JOIN direction DR ON CU.id_direction = DR.id_direction\n" + "  INNER JOIN chair CH ON CU.id_chair=CH.id_chair\n" +
                "  LEFT JOIN department DE ON CH.id_chair=DE.id_chair AND DE.is_main = true\n" +
                "  INNER JOIN student_semester_status SSS USING (id_link_group_semester)\n" +
                "  INNER JOIN studentcard SC USING (id_studentcard)\n" + "  INNER JOIN humanface HF USING (id_humanface)\n" +
                "  INNER JOIN sessionrating SR USING (id_student_semester_status)\n" + "  INNER JOIN subject SU USING (id_subject)\n" +
                "  INNER JOIN link_group_semester_subject LGSS ON SU.id_subject = LGSS.id_subject\n" +
                "  INNER JOIN dic_subject DS USING (id_dic_subject)\n" + "  LEFT JOIN chair CH2 ON SU.id_chair=CH2.id_chair\n" +
                "  LEFT JOIN department DE2 ON CH2.id_chair=DE2.id_chair AND DE2.is_main = true\n" +
                "  INNER JOIN sessionratinghistory SRH using(id_sessionrating)\n" + "  LEFT JOIN register RE USING (id_register)\n" +
                "WHERE\n" + "" + lastDateCheck + instCheck + semestrCheck + departCheck + fosCheck + govFinCheck + levelCheck +
                groupNameCheck + coursesCheck + chairCheck + currentGroupCheck + retakeCheck
                       // + practicCheck
                        + fizRaCheck + deductCheck +
                academicCheck + passWeekCheck + " group by SC.recordbook, DG.id_dic_group,\n" + "SSS.id_student_semester_status,\n" + "HF.id_humanface,\n" +
                "SC.id_studentcard,\n" + "SE.id_semester,\n" + "SU.id_subject,\n" + "DG.groupname,\n" +
                "HF.family || ' ' || HF.name || ' ' || HF.patronymic,\n" + "DS.subjectname,\n" + "LGS.course,\n" + "CU.formofstudy,\n" +
                "CU.qualification,\n" + "SSS.is_government_financed,\n" + "SU.is_exam,\n" + "SU.is_pass,\n" + "SRH.is_pass,\n" +
                "SRH.is_exam,\n" + "SRH.is_courseproject,\n" + "SRH.is_coursework,\n" + "SRH.is_practic, DR.code, DE.shorttitle,\n" +
                "tChairFulltitle,\n" + "CH2.id_chair,\n" + "DE.fulltitle,\n" + "CH.id_chair";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("idGroup", LongType.INSTANCE)
                              .addScalar("idStudent", LongType.INSTANCE)
                              .addScalar("idHumanface", LongType.INSTANCE)
                              .addScalar("idStudentcard", LongType.INSTANCE)
                              .addScalar("idSemester", LongType.INSTANCE)
                              .addScalar("idSubject", LongType.INSTANCE)
                              .addScalar("groupname")
                              .addScalar("fio")
                              .addScalar("subjectName")
                              .addScalar("recordbook")
                              .addScalar("eChairShorttitle")
                              .addScalar("specialityCode")
                              .addScalar("course")
                              .addScalar("formOfStudy")
                              .addScalar("lvl")
                              .addScalar("govFinanced", BooleanType.INSTANCE)
                              .addScalar("pass", BooleanType.INSTANCE)
                              .addScalar("exam", BooleanType.INSTANCE)
                              .addScalar("cp", BooleanType.INSTANCE)
                              .addScalar("cw", BooleanType.INSTANCE)
                              .addScalar("practic", BooleanType.INSTANCE)
                              .addScalar("rating")
                              .addScalar("signdate", DateType.INSTANCE)
                              .addScalar("tChairFulltitle")
                              .addScalar("tChairId", LongType.INSTANCE)
                              .addScalar("eChairFulltitle")
                              .addScalar("eChairId", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(RatingModel.class));
        return (List<RatingModel>) getList(q);
    }


    public List<String> getTeachersBySubject(Long idSubject) {
        String query = "select distinct family || ' ' || name || ' ' || patronymic as fio from link_employee_subject_group lesg\n" +
                       "join link_group_semester_subject lgss on lesg.id_link_group_semester_subject = lgss.id_link_group_semester_subject\n" +
                       "join employee e on lesg.id_employee = e.id_employee\n" + "join humanface h2 on e.id_humanface = h2.id_humanface\n" +
                       "where lgss.id_subject = " + idSubject;

        return (List<String>)getSession().createSQLQuery(query).list();
    }
}
