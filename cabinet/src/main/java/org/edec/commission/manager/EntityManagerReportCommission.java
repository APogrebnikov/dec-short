package org.edec.commission.manager;

import org.edec.commission.model.CommissionStudentReportModel;
import org.edec.dao.DAO;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import java.util.Date;
import java.util.List;

public class EntityManagerReportCommission extends DAO {

    public List<CommissionStudentReportModel> getStudentForReport(int formofstudy, Date dateOfBegin, Date dateOfEnd) {

        String query = "SELECT  HF.family||' '||HF.name||' '||HF.patronymic AS fio, DG.groupname,\n" + "\tDS.subjectname, \n" +
                "\tEXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend)||'('||CASE WHEN SEM.season = 0 THEN 'осень' ELSE 'весна' END||')' AS semesterStr,\n" +
                "\t(SELECT fulltitle FROM department WHERE id_chair = S.id_chair AND is_main = TRUE) AS chair,\n" + "\tCASE\n" +
                "\t\tWHEN SRH.is_exam = 1 THEN 'Экзамен'\n" + "\t\tWHEN SRH.is_pass = 1 THEN 'Зачет'\n" +
                "\t\tWHEN SRH.is_courseproject = 1 THEN 'КП'\n" + "\t\tWHEN SRH.is_coursework = 1 THEN 'КР'\n" +
                "\t\tWHEN SRH.is_practic = 1 THEN 'Практика'\n" +
                "\tEND AS focStr, RC.comission_date AS commissionDate, RC.classroom,\n" +
                "\tSSS.is_government_financed = 1 AS isGovernmentFinanced,\n" + "CASE\n" +
                "      WHEN R.certnumber IS NULL THEN ''\n" + "      WHEN SRH.newrating = 5 THEN 'Отлично'\n" +
                "      WHEN SRH.newrating = 4 THEN 'Хорошо'\n" + "      WHEN SRH.newrating = 3 THEN 'Удовл.'\n" +
                "      WHEN SRH.newrating = 2 THEN 'Неудовл.'\n" + "      WHEN SRH.newrating = 1 THEN 'Зачтено'\n" +
                "      WHEN SRH.newrating = -1 THEN 'Не изучал'\n" + "      WHEN SRH.newrating = -2 THEN 'Незачтено'\n" +
                "      WHEN SRH.newrating = -3 THEN 'Неявка'\n" + "  ELSE '' END AS rating\n" +
                "FROM    register_comission RC\n" +
                "\tINNER JOIN subject S ON RC.id_subject = S.id_subject\n" +
                "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "\tINNER JOIN register R USING (id_register)\n" +
                "\tINNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "WHERE SEM.formofstudy = :formofstudy\n" +
                "   AND RC.dateofbegincomission = :dateOfBegin AND RC.dateofendcomission = :dateOfEnd\n" +
                "ORDER BY fio, groupname";

        Query q = getSession().createSQLQuery(query)
                .addScalar("fio").addScalar("groupname").addScalar("subjectname").addScalar("semesterStr")
                .addScalar("chair").addScalar("focStr").addScalar("rating").addScalar("commissionDate")
                .addScalar("classroom").addScalar("isGovernmentFinanced")
                .setResultTransformer(Transformers.aliasToBean(CommissionStudentReportModel.class))
                .setInteger("formofstudy", formofstudy).setDate("dateOfBegin", dateOfBegin)
                .setDate("dateOfEnd", dateOfEnd);

        return (List<CommissionStudentReportModel>) getList(q);
    }
}
