package org.edec.secretaryChair.manager;

import org.edec.dao.DAO;
import org.edec.secretaryChair.model.ChairsComissionsProtocolsModel;
import org.edec.secretaryChair.model.CommissionModel;
import org.edec.secretaryChair.model.EmployeeModel;
import org.edec.secretaryChair.model.StudentCommissionModel;
import org.edec.teacher.model.commission.StudentModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class EntityManagerSecretaryChair extends DAO {
    public List<SemesterModel> getSemestersByIdChair(Long idChair, Integer formOfStudy) {
        String query = "SELECT DISTINCT LGS.id_semester AS idSem, SY.dateofbegin AS dateOfBegin, SY.dateofend AS dateOfEnd, \n" +
                "\tSEM.season, SEM.formofstudy\n" + "FROM register_comission RC\n" + "\tINNER JOIN subject S USING (id_subject)\n" +
                "\tINNER JOIN chair CH USING (id_chair)\n" + "\tINNER JOIN department DEP USING (id_chair)" +
                "\tINNER JOIN register R USING (id_register)\n" + "\tINNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" + "WHERE DEP.id_chair = " + idChair + "\n" + "" +
                (formOfStudy == 3 ? "" : "\tAND SEM.formofstudy = " + formOfStudy) + "\n" + "ORDER BY idSem DESC";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idSem", LongType.INSTANCE)
                .addScalar("dateOfBegin")
                .addScalar("dateOfEnd")
                .addScalar("season")
                .addScalar("formofstudy")
                .setResultTransformer(Transformers.aliasToBean(SemesterModel.class));
        return (List<SemesterModel>) getList(q);
    }

    public List<CommissionModel> getCommissionByChair(Long idSem, Long idChair, Integer formOfStudy, boolean singed) {
        String query = "SELECT\n" + "\tRC.id_register_comission AS id, RC.classroom, S.id_chair AS idChair, is_physcical_culture(DS.subjectname) AS isPhysicalCulture,\n" +
                "\tRC.comission_date AS commissionDate, DS.subjectname AS subjectName,\n" +
                "\tRC.dateofbegincomission AS dateBegin, RC.dateofendcomission AS dateEnd,\n" + "\tCASE\n" +
                "\t\tWHEN SRH.is_exam = 1 THEN 1\n" + "\t\tWHEN SRH.is_pass = 1 THEN 2\n" +
                "\t\tWHEN SRH.is_courseproject = 1 THEN 3\n" + "\t\tWHEN SRH.is_coursework = 1 THEN 4\n" +
                "\t\tWHEN SRH.is_practic = 1 THEN 5\n" + "\tEND AS formOfControl,\n" +
                "\tEXTRACT(YEAR FROM SY.dateOfBegin)||'/'||EXTRACT(YEAR FROM SY.dateOfEnd)||' '||\n" + "\t\tCASE\n" +
                "\t\t\tWHEN SEM.season = 0 THEN 'осень'\n" + "\t\t\tWHEN SEM.season = 1 THEN 'весна'\n" +
                "\t\tEND AS semesterStr,\n" + "\tCASE\n" + "\t\tWHEN (RC.classroom IS NULL AND RC.comission_date IS NULL) THEN 0\n" +
                "\t\tWHEN (RC.classroom IS NOT NULL AND R.certnumber IS NULL) THEN 1\n" +
                "\t\tWHEN (R.certnumber IS NOT NULL) THEN 2\n" +
                "\tEND AS status," +
                "\tSRH.check_commission AS checkStatus, " +
                "R.id_register AS idRegister, RC.statusnotification as statusNotification \n" +
                "FROM student_semester_status SSS\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" +
                "\tINNER JOIN sessionratinghistory SRH USING (id_sessionrating)\n" +
                "\tINNER JOIN register R USING (id_register)\n" + "\tINNER JOIN register_comission RC USING (id_register)\n" +
                "\tINNER JOIN subject S ON RC.id_subject = S.id_subject\n" + "\tINNER JOIN chair CH USING (id_chair)\n" +
                "\tINNER JOIN department DEP USING (id_chair)" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "WHERE DEP.id_chair = :idChair\n" + (idSem == null ? "" : "\t" + "AND SEM.id_semester = " + idSem + "\n") +
                (singed ? "" : "\t" + "AND R.certnumber IS NULL\n") +
                (formOfStudy == 3 ? "" : "\t" + "AND SEM.formofstudy = " + formOfStudy + "\n") +
                "GROUP BY id, subjectname, formOfControl, semesterStr, idChair, R.id_register, isPhysicalCulture, SRH.check_commission \n" +
                "ORDER BY semesterStr, status";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE).addScalar("classroom").addScalar("isPhysicalCulture")
                .addScalar("commissionDate").addScalar("subjectName").addScalar("formOfControl")
                .addScalar("semesterStr").addScalar("dateBegin").addScalar("dateEnd")
                .addScalar("idChair", LongType.INSTANCE)
                .addScalar("status")
                .addScalar("checkStatus", BooleanType.INSTANCE)
                .addScalar("idRegister", LongType.INSTANCE)
                .addScalar("statusNotification", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(CommissionModel.class));
        q.setLong("idChair", idChair);
        return (List<CommissionModel>) getList(q);
    }

    public List<CommissionModel> getCommissionByDate(Long idCommission) {
        String query =
                "WITH selectedCommission AS (SELECT RC.id_register_comission AS idComm, SSS.id_studentcard, SR.id_student_semester_status,\n" +
                        "          SR.id_subject, RC.dateofbegincomission, RC.dateofendcomission\n" +
                        "       FROM register_comission RC\n" +
                        "           INNER JOIN register R USING (id_register)\n" +
                        "           INNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                        "           INNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                        "           INNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                        "       WHERE RC.id_register_comission = " + idCommission + ")\n" +
                        "  SELECT RC.comission_date AS commissionDate, RC.id_subject AS idSubject, is_physcical_culture(DS.subjectname) AS isPhysicalCulture\n" +
                        "  FROM register_comission RC\n" +
                        "    INNER JOIN subject S ON RC.id_subject = S.id_subject\n" +
                        "    INNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                        "    INNER JOIN register R USING (id_register)\n" +
                        "    INNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                        "    INNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                        "    INNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                        "    INNER JOIN selectedCommission COMM ON RC.id_register_comission <> COMM.idComm AND SSS.id_studentcard = COMM.id_studentcard\n" +
                        "                                      AND (SR.id_student_semester_status, SR.id_subject) <> (COMM.id_student_semester_status, COMM.id_subject)\n" +
                        "  WHERE R.certnumber IS NULL AND RC.comission_date IS NOT NULL\n" +
                        "  GROUP BY RC.id_register_comission,isPhysicalCulture\n" +
                        "  ORDER BY commissionDate;";
        Query q = getSession().createSQLQuery(query)
                .addScalar("commissionDate").addScalar("isPhysicalCulture")
                .addScalar("idSubject", StandardBasicTypes.LONG)
                .setResultTransformer(Transformers.aliasToBean(CommissionModel.class));
        return (List<CommissionModel>) getList(q);
    }

    public List<EmployeeModel> getEmployeesByDepartment(Long idChair) {
        String query = "SELECT HF.family||' '||HF.name||' '||HF.patronymic AS fio, LED.id_link_employee_department AS idLED,\n" +
                "\tEMP.id_employee AS idEmployee, ER.rolename AS role\n" + "FROM link_employee_department LED \n" +
                "\tINNER JOIN department DEP USING (id_department)\n" + "\tINNER JOIN employee_role ER USING (id_employee_role)\n" +
                "\tINNER JOIN employee EMP USING (id_employee)\n" + "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "WHERE LED.is_hide = FALSE AND DEP.id_chair = :idChair\n" + "ORDER BY fio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("idLED", LongType.INSTANCE)
                .addScalar("idEmployee", LongType.INSTANCE)
                .addScalar("role")
                .setResultTransformer(Transformers.aliasToBean(EmployeeModel.class));
        q.setLong("idChair", idChair);
        return (List<EmployeeModel>) getList(q);
    }

    public List<EmployeeModel> getCommissionEmployee(Long idCommissionRegister) {
        String query = "SELECT HF.family||' '||HF.name||' '||HF.patronymic AS fio, LED.id_link_employee_department AS idLED,\n" +
                "\tEMP.id_employee AS idEmployee, ER.rolename AS role, LEDC.leader, LEDC.pos, LEDC.sign\n" +
                "FROM link_employee_department LED \n" + "\tINNER JOIN employee_role ER USING (id_employee_role)\n" +
                "\tINNER JOIN employee EMP USING (id_employee)\n" + "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "\tINNER JOIN led_comission LEDC USING (id_link_employee_department)\n" +
                "WHERE LEDC.id_register_comission = :idRC\n" + "ORDER BY fio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("idLED", LongType.INSTANCE)
                .addScalar("idEmployee", LongType.INSTANCE)
                .addScalar("role")
                .addScalar("leader")
                .addScalar("pos")
                .setResultTransformer(Transformers.aliasToBean(EmployeeModel.class));
        q.setLong("idRC", idCommissionRegister);
        return (List<EmployeeModel>) getList(q);
    }

    public List<StudentModel> getStudentsByCommission(Long idCommission) {
        String query = "SELECT HF.family||' '||HF.name||' '||HF.patronymic AS fio,\n" +
                "\tDG.groupname\n" +
                "FROM register_comission RC \n" +
                "\tINNER JOIN register R USING (id_register)\t\n" +
                "\tINNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "WHERE RC.id_register_comission = :idCommission \n" +
                "ORDER BY fio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio").addScalar("groupname")
                .setResultTransformer(Transformers.aliasToBean(StudentModel.class));
        q.setLong("idCommission", idCommission);
        return (List<StudentModel>) getList(q);
    }

    public List<StudentCommissionModel> getStudentsForCheckFreeDate(Long idComm, Date dateComm, boolean day) {
        String dateCondition;
        if (day) {
            dateCondition = "\tAND CAST(RC.comission_date AS DATE) = date '" + DateConverter.convertDateToSQLStringFormat(dateComm) + "'";
        } else {
            String date = DateConverter.convertTimestampToSQLStringFormate(dateComm);
            //Второе условие специально для физ-ры. Условие сработает во всех случаях, кроме двух физических культур
            dateCondition = "\tAND DATE(RC.comission_date) = DATE('" + date + "') AND NOT(is_physcical_culture(DS.subjectname) = TRUE AND is_physcical_culture(searchRegister.subjectname))\n";
        }

        //language=PostgreSQL
        String query = "SELECT HF.family, HF.name, HF.patronymic, DG.groupname, RC.comission_date AS dateCommission,\n" +
                "\tDS.subjectname, is_physcical_culture(DS.subjectname) AS isPhysicalCulture,\n" +
                "CASE WHEN SRH.is_exam = 1 THEN 1\n" +
                "  WHEN SRH.is_pass = 1 THEN 2\n" +
                "  WHEN SRH.is_courseproject = 1 THEN 3\n" +
                "  WHEN SRH.is_coursework = 1 THEN 4\n" +
                "  WHEN SRH.is_practic = 1 THEN 5 END AS foc,\n" +
                "  SC.id_studentcard AS idStudentCard, RC.id_subject AS idSubject\n" +
                "FROM register_comission RC\n" +
                "  INNER JOIN subject S USING (id_subject)\n" +
                "  INNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "  INNER JOIN register R USING (id_register)\n" +
                "  INNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                "  INNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "  INNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "  INNER JOIN studentcard SC USING (id_studentcard)\n" +
                "  INNER JOIN humanface HF USING (id_humanface)\n" +
                "  INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "  INNER JOIN dic_group DG USING (id_dic_group)\n" +
                "  INNER JOIN (SELECT SSS.id_studentcard, RC.id_register_comission, SSS.id_student_semester_status, SR.id_subject, DS.subjectname\n" +
                "      FROM register_comission RC\n" +
                "      INNER JOIN register R USING (id_register)\n" +
                "      INNER JOIN subject S ON RC.id_subject = S.id_subject\n" +
                "      INNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "      INNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                "      INNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "      INNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "      WHERE RC.id_register_comission = " + idComm +
                ") AS searchRegister ON SSS.id_studentcard = searchRegister.id_studentcard\n" +
                "          AND RC.id_register_comission <> searchRegister.id_register_comission\n" +
                "          AND (SR.id_student_semester_status, SR.id_subject) <> (searchRegister.id_student_semester_status, searchRegister.id_subject)\n" +
                "WHERE R.certnumber IS NULL\n" + dateCondition;

        Query q = getSession().createSQLQuery(query)
                .addScalar("family").addScalar("name").addScalar("patronymic")
                .addScalar("subjectname").addScalar("groupname")
                .addScalar("dateCommission").addScalar("foc").addScalar("isPhysicalCulture")
                .addScalar("idStudentCard", StandardBasicTypes.LONG)
                .addScalar("idSubject", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(StudentCommissionModel.class));
        return (List<StudentCommissionModel>) getList(q);
    }

    public boolean updateCommissionInfo(Date dateCommission, String classroom, Long idCommission) {
        Query q = getSession().createSQLQuery("UPDATE register_comission SET comission_date = :dateCommission, classroom = :classroom\n" +
                "WHERE id_register_comission = :idCommission");
        q.setTimestamp("dateCommission", dateCommission).setString("classroom", classroom).setLong("idCommission", idCommission);
        return executeUpdate(q);
    }

    public boolean updateCommissionStatusNotification(Long idComission, Integer status){
        String statusNotification = (status == 0  ? " 1 " : " 2 ");
        Query q = getSession().createSQLQuery("UPDATE register_comission SET statusNotification = " + statusNotification +
                " WHERE id_register_comission = :idCommission");
        q.setLong("idCommission", idComission);
        return executeUpdate(q);
    }


    public boolean deleteCommissionStaff(Long idCommission) {
        Query q = getSession().createSQLQuery("DELETE FROM led_comission WHERE id_register_comission = :idCommission");
        q.setLong("idCommission", idCommission);
        return executeUpdate(q);
    }

    public boolean addCommissionStaff(EmployeeModel employee, Long idCommission) {
        Query q = getSession().createSQLQuery("INSERT INTO led_comission(id_register_comission, id_link_employee_department, leader) " +
                " VALUES (:idCommission, :idLED, :leader)");
        q.setLong("idCommission", idCommission).setLong("idLED", employee.getIdLED()).setInteger("leader", employee.getLeader());
        return executeUpdate(q);
    }

    public List<ChairsComissionsProtocolsModel> getComissionsProtocols(Long idSemester, Long idChair){
        String query = "select distinct hf.family||' '||hf.name||' '||hf.patronymic as fioStudent, (SELECT to_date(to_char(RC.comission_date, 'yyyy-MM-dd'), 'yyyy-MM-dd')) as dateComission,\n" +
                "                case\n" +
                "                    when srh.is_practic = 1  then 'Практика'\n" +
                "                    when srh.is_coursework = 1  then 'КР'\n" +
                "                    when srh.is_courseproject = 1  then 'КП'\n" +
                "                    when srh.is_exam = 1 then 'Экзамен'\n" +
                "                    when srh.is_pass = 1  then 'Зачет' end as foc,\n" +
                "  (SELECT to_char(RC.comission_date, 'dd.MM.yyyy')) as comDateStr, "+
                "                substring(cast (RC.comission_date as text), 12, 5) as timeComission, srh.protocol_number as protocolNumber,\n" +
                "                reg.id_register as idRegister, rc.id_register_comission as idRegCom, reg.register_number as regNumber, srh.retake_count as retakeCount,\n" +
                "                dep.fulltitle as chairsName, ds.subjectname as subjectname, reg.certnumber as certnumber, dg.groupname as groupname\n" +
                " from  register reg\n" +
                "          join sessionratinghistory srh on srh.id_register = reg.id_register\n" +
                "          join sessionrating sr on sr.id_sessionrating = srh.id_sessionrating\n" +
                "          join register_comission rc on rc.id_register = reg.id_register\n" +
                "          join led_comission lc on rc.id_register_comission = lc.id_register_comission\n" +
                "          join link_employee_department led on lc.id_link_employee_department = led.id_link_employee_department\n" +
                "          join department dep on led.id_department = dep.id_department\n" +
                "          join student_semester_status sss on sr.id_student_semester_status = sss.id_student_semester_status\n" +
                "          join studentcard sc on sss.id_studentcard = sc.id_studentcard\n" +
                "          join dic_group dg on sc.id_current_dic_group = dg.id_dic_group\n" +
                "          join humanface hf on sc.id_humanface = hf.id_humanface\n" +
                "          join subject s on sr.id_subject = s.id_subject\n" +
                "          join dic_subject ds on s.id_dic_subject = ds.id_dic_subject\n" +
                "where reg.id_semester =  "+idSemester+" and srh.retake_count in (3, -3)  and dep.id_chair =" + idChair +
                " and (SELECT to_date(to_char(RC.comission_date, 'yyyy-MM-dd'), 'yyyy-MM-dd')) <= now()  and dep.is_main = true\n" +
                "order by  dateComission desc ";

        Query q = getSession().createSQLQuery(query)
                .addScalar("dateComission")
                .addScalar("timeComission")
                .addScalar("protocolNumber")
                .addScalar("chairsName")
                .addScalar("fioStudent")
                .addScalar("subjectname")
                .addScalar("regNumber")
                .addScalar("retakeCount")
                .addScalar("certnumber")
                .addScalar("comDateStr")
                .addScalar("groupname")
                .addScalar("idRegister", LongType.INSTANCE)
                .addScalar("idRegCom", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ChairsComissionsProtocolsModel.class));
        return q.list();

    }
}
