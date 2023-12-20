package org.edec.subject.manager;

import org.edec.dao.DAO;
import org.edec.subject.model.eso.SubjectModelEso;
import org.edec.subject.model.eso.TeacherModelEso;
import org.edec.utility.component.model.SemesterModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;

import java.util.Collections;
import java.util.List;

public class SubjectManager extends DAO {

    public List<SemesterModel> getSemester (Long idInst, Integer fos, Integer season) {
        String query = "SELECT \n" + "\tSEM.id_semester AS idSem, (SEM.is_current_sem=1) AS curSem,\n" +
                       "\tSEM.season, SY.dateofbegin AS dateOfBegin, SY.dateofend AS dateOfEnd, SEM.formofstudy AS formofstudy\n" +
                       "FROM \n" + "\tsemester SEM\n" + "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" + "WHERE \n" +
                       "\tid_institute = :idInst AND formofstudy " + (fos != 3 ? "=:fos" : " in (1,2) ") +
                       " AND CAST(SEM.season AS TEXT) ILIKE '%" + (season == null ? "" : season) + "%'\n" + "ORDER BY \n" +
                       "\tid_semester DESC";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idSem", LongType.INSTANCE)
                              .addScalar("curSem")
                              .addScalar("season")
                              .addScalar("dateOfBegin")
                              .addScalar("dateOfEnd")
                              .addScalar("formofstudy")
                              .setResultTransformer(Transformers.aliasToBean(SemesterModel.class));
        q.setLong("idInst", idInst);
        if (fos != 3) {
            q.setInteger("fos", fos);
        }
        return (List<SemesterModel>) getList(q);
    }

    //получение предметов по семестру
    public List<SubjectModelEso> getSubjectsByFilter (long idDepartment, long idSem) {
        String query = "SELECT \n" + "\tdistinct LGSS.id_subject as idSubject,\n" + "\tLESG.id_link_employee_subject_group as idLesg,\n" +
                       "\tDG.id_dic_group as idDg,\n" + "\tDG.groupname as groupname,\n" +
                       "\tLGSS.id_link_group_semester_subject as idLgss,\n" + "\tDSBJ.subjectname as subjectName,\n" +
                       "\tSEM.season as season,\n" + "\tEMP.id_employee as idEmp,\n" +
                       "\tHF.family || ' ' || HF.name || ' ' || HF.patronymic as fioTeacher,\n" + "\tDEPEMP.fulltitle as depTitle\n" +
                       "FROM \n" + "\tlink_group_semester_subject LGSS\n" +
                       "\tINNER JOIN link_group_semester LGS using (id_link_group_semester)\n" +
                       "\tLEFT JOIN link_employee_subject_group LESG using (id_link_group_semester_subject)\n" +
                       "\tLEFT JOIN employee EMP ON LESG.id_employee = EMP.id_employee\n" +
                       "\tLEFT JOIN humanface HF using(id_humanface)\n" +
                       "\tLEFT JOIN link_employee_department LED ON EMP.id_employee = LED.id_employee" +
                       "\tLEFT JOIN department DEPEMP ON LED.id_department = DEPEMP.id_department\n" +
                       "\tINNER JOIN dic_group DG using (id_dic_group)\n" + "\tINNER JOIN subject SBJ using(id_subject)\n" +
                       "\tINNER JOIN chair CH1 ON SBJ.id_chair = CH1.id_chair\n" +
                       "\tINNER JOIN department SBJDEP ON CH1.id_chair = SBJ.id_chair\n" +
                       "\tINNER JOIN dic_subject DSBJ using(id_dic_subject)\n" +
                       "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                       "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                       "\tINNER JOIN curriculum CUR ON DG.id_curriculum = CUR.id_curriculum\n" +
                       "\tINNER JOIN chair CH ON CUR.id_chair = CH.id_chair\n" +
                       "\tINNER JOIN department DEP ON CH.id_chair = DEP.id_chair\n" + "WHERE \n" + "\tDEP.id_department = " +
                       idDepartment + "\tAND CASE WHEN DEPEMP.id_department is NOT NULL THEN DEPEMP.otherdbid IS NOT NULL ELSE" +
                       " SEM.id_semester = " + idSem + " END \n" + "\tAND SEM.id_semester = " + idSem +
                       "\tORDER BY groupname desc, subjectName asc\n";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idSubject", LongType.INSTANCE)
                              .addScalar("idLgss", LongType.INSTANCE)
                              .addScalar("subjectName")
                              .addScalar("season")
                              .addScalar("groupName")
                              .addScalar("idDg", LongType.INSTANCE)
                              .addScalar("fioTeacher")
                              .addScalar("idLesg", LongType.INSTANCE)
                              .addScalar("depTitle")
                              .addScalar("idEmp", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(SubjectModelEso.class));
        return (List<SubjectModelEso>) getList(q);
    }

    // Получение всех преподавателей
    public List<TeacherModelEso> getTeachers (long idDepartment) {
        String query = "SELECT \n" + "\tlesg.id_link_employee_subject_group as idLesg,\n" + "\temp.id_employee as idTeacher,\n" +
                       "\thf.family || ' ' || hf.name || ' ' || hf.patronymic as fullName,\n" +
                       "\tled.id_link_employee_department as idLed,\n" + "\tled.is_hide as isHidden,\n" + "\tdep.fulltitle as depTitle\n" +
                       "FROM \n" + "\tlink_group_semester lgs\n" +
                       "\tINNER JOIN link_group_semester_subject lgss using(id_link_group_semester)\n" +
                       "\tINNER JOIN subject sbj using(id_subject)\n" + "\tINNER JOIN dic_subject dsbj using(id_dic_subject)\n" +
                       "\tINNER JOIN link_employee_subject_group lesg using(id_link_group_semester_subject)\n" +
                       "\tINNER JOIN dic_group dg using(id_dic_group)\n" + "\tINNER JOIN employee emp using(id_employee)\n" +
                       "\tINNER JOIN humanface hf using(id_humanface)\n" +
                       "\tINNER JOIN link_employee_department led using(id_employee)\n" +
                       "\tINNER JOIN institute inst using(id_institute)\n" + "\tINNER JOIN department dep using(id_department)\n" +
                       "\tWHERE dep.otherdbid IS NOT NULL " + "\tAND dep.id_department = " + idDepartment + "\n" +
                       "\tORDER BY emp.id_employee";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("idLesg", LongType.INSTANCE)
                              .addScalar("idTeacher", LongType.INSTANCE)
                              .addScalar("fullName")
                              .addScalar("depTitle")
                              .addScalar("idLed", LongType.INSTANCE)
                              .addScalar("isHidden", BooleanType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(TeacherModelEso.class));
        return (List<TeacherModelEso>) getList(q);
    }

    // Прикрепить преподавателя к предмету
    public boolean addTeacher (Long idTeacher, Long idLgss) {
        String query = "INSERT INTO link_employee_subject_group \n" + "\t(id_employee, id_link_group_semester_subject) " + "VALUES \n" +
                       "\t(:idTeacher, :idLgss)";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idTeacher", idTeacher).setLong("idLgss", idLgss);
        return executeUpdate(q);
    }

    // Отвязать преподавателя от предмета
    public boolean removeTeacherFromSubject (Long idLesg) {
        String query = "DELETE FROM\n" + "\tlink_employee_subject_group\n" + "WHERE\n" + "\tid_link_employee_subject_group = :idLesg";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idLesg", idLesg);
        return executeUpdate(q);
    }

    public boolean changeTeacherVisibility (Long idLed, boolean isHide) {
        String query =
                "UPDATE link_employee_department\n" + "SET is_hide = :isHide\n" + "WHERE\n" + "\tid_link_employee_department = :idLed";
        Query q = getSession().createSQLQuery(query);
        q.setBoolean("isHide", isHide);
        q.setLong("idLed", idLed);
        return executeUpdate(q);
    }
}
