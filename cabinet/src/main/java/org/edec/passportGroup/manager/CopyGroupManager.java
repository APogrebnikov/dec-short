package org.edec.passportGroup.manager;

import org.edec.admin.model.EmployeeModel;
import org.edec.dao.DAO;
import org.edec.passportGroup.model.*;
import org.edec.passportGroup.model.eso.StudentModelESO;
import org.edec.passportGroup.model.eso.SubjectModelESO;
import org.edec.passportGroup.model.eso.SubjectReportModelESO;
import org.edec.passportGroup.model.eso.TeacherModelESO;
import org.edec.passportGroup.model.passportGroupReport.PassportGroupModel;
import org.edec.register.model.RetakeModel;
import org.edec.register.service.RegisterService;
import org.edec.synchroMine.model.eso.entity.LinkGroupSemester;
import org.edec.utility.constants.FormOfControlConst;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alex on 03.07.19.
 * Сервис для создания и заполнения ГАК групп
 */
public class CopyGroupManager extends DAO {

    /**
     * Получаем список семестров для существующей группы
     * @param idDicGroup
     * @return
     */
    public List<LinkGroupSemesterModel> getLgsForGroup (Long idDicGroup) {
        String query = "SELECT " +
                "id_link_group_semester as idLgs, " +
                "id_semester as semesterNumber " +
                "FROM " +
                "link_group_semester " +
                "WHERE " +
                "id_dic_group = :idDicGroup ORDER BY id_link_group_semester";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idLgs", LongType.INSTANCE)
                .addScalar("semesterNumber",IntegerType.INSTANCE)
                .setLong("idDicGroup",idDicGroup)
                .setResultTransformer(Transformers.aliasToBean(LinkGroupSemesterModel.class));
        return (List<LinkGroupSemesterModel>) getList(q);
    }

    /**
     * Создаем недостающие семестры для группы
     * @param listLgsForCopy
     * @return
     */
    public boolean createLgsForGroup (List<Long> listLgsForCopy, Long idNewDicGroup) {
        String query = "INSERT INTO link_group_semester(id_semester,\n" +
                "  id_dic_group,\n" +
                "  dateofbeginsemester,\n" +
                "  dateofendsemester,\n" +
                "  dateofbeginsession,\n" +
                "  dateofendsession,\n" +
                "  dateofbeginpassweek,\n" +
                "  dateofendpassweek,\n" +
                "  dateofbeginvacation,\n" +
                "  dateofendvacation,\n" +
                "  dateofbegingrant,\n" +
                "  dateofendgrant,\n" +
                "  semesternumber,\n" +
                "  course,\n" +
                "  otherdbid,\n" +
                "  student_count_burden,\n" +
                "  is_efficiency,\n" +
                "  dateofbeginpractic,\n" +
                "  dateofendpractic) \n" +
                " select  \n" +
                "\tid_semester,\n" +
                "\t:idNewDicGroup,\n" +
                "\tdateofbeginsemester,\n" +
                "\tdateofendsemester,\n" +
                "\tdateofbeginsession,\n" +
                "\tdateofendsession,\n" +
                "\tdateofbeginpassweek,\n" +
                "\tdateofendpassweek,\n" +
                "\tdateofbeginvacation,\n" +
                "\tdateofendvacation,\n" +
                "\tdateofbegingrant,\n" +
                "\tdateofendgrant,\n" +
                "\tsemesternumber,\n" +
                "\tcourse,\n" +
                "\totherdbid,\n" +
                "\tstudent_count_burden,\n" +
                "\tis_efficiency,\n" +
                "\tdateofbeginpractic,\n" +
                "\tdateofendpractic\n" +
                " from link_group_semester where id_link_group_semester IN (:listLgs) RETURNING id_link_group_semester";
        Query q = getSession().createSQLQuery(query)
                .setLong("idNewDicGroup", idNewDicGroup)
                .setParameterList("listLgs", listLgsForCopy);
        List<Long> list = (List<Long>) getList(q);
        if(list.size() == 0){
            return false;
        } else {
            return true;
        }
    }

    public boolean createLgssForGroup (Long idOldDicGroup, Long idNewDicGroup) {
        String query = "INSERT INTO link_group_semester_subject(\n" +
                "\tid_link_group_semester,\n" +
                "\tid_subject,\n" +
                "\tid_esocourse,\n" +
                "\tid_esocourse2,\n" +
                "\tesogradecurrent,\n" +
                "\tesogrademax,\n" +
                "\tesostudycount,\n" +
                "\thourscourseproject,\n" +
                "\thourscoursework,\n" +
                "\thourscontroldistance,\n" +
                "\thoursconsult,\n" +
                "\thourscontrolselfstudy,\n" +
                "\texamdate,\n" +
                "\tpassdate,\n" +
                "\ttmpcourseprojectdate,\n" +
                "\ttmpcourseworkdate,\n" +
                "\tprintstatus,\n" +
                "\texamaudience,\n" +
                "\tconsultationdate,\n" +
                "\tformexam,\n" +
                "\tconsultationaudience,\n" +
                "\tpracticdate,\n" +
                "\tlessonscount,\n" +
                "\tstatepart,\n" +
                "\tis_efficiency_performance,\n" +
                "\tis_efficiency_attendance,\n" +
                "\tis_efficiency_eok) \n" +
                "SELECT   \n" +
                "\tlgss2.id_link_group_semester,\n" +
                "\tlgss.id_subject,\n" +
                "\tlgss.id_esocourse,\n" +
                "\tlgss.id_esocourse2,\n" +
                "\tlgss.esogradecurrent,\n" +
                "\tlgss.esogrademax,\n" +
                "\tlgss.esostudycount,\n" +
                "\tlgss.hourscourseproject,\n" +
                "\tlgss.hourscoursework,\n" +
                "\tlgss.hourscontroldistance,\n" +
                "\tlgss.hoursconsult,\n" +
                "\tlgss.hourscontrolselfstudy,\n" +
                "\tlgss.examdate,\n" +
                "\tlgss.passdate,\n" +
                "\tlgss.tmpcourseprojectdate,\n" +
                "\tlgss.tmpcourseworkdate,\n" +
                "\tlgss.printstatus,\n" +
                "\tlgss.examaudience,\n" +
                "\tlgss.consultationdate,\n" +
                "\tlgss.formexam,\n" +
                "\tlgss.consultationaudience,\n" +
                "\tlgss.practicdate,\n" +
                "\tlgss.lessonscount,\n" +
                "\tlgss.statepart,\n" +
                "\tlgss.is_efficiency_performance,\n" +
                "\tlgss.is_efficiency_attendance,\n" +
                "\tlgss.is_efficiency_eok\n" +
                "FROM \n" +
                "link_group_semester lgs\n" +
                "INNER JOIN link_group_semester_subject lgss ON lgss.id_link_group_semester = lgs.id_link_group_semester\n" +
                "INNER JOIN link_group_semester lgss2 ON lgss2.id_dic_group = :idNewDicGroup AND lgss2.id_semester=lgs.id_semester\n" +
                "INNER JOIN dic_group dg ON dg.id_dic_group = lgs.id_dic_group\n" +
                "WHERE dg.id_dic_group = :idOldDicGroup RETURNING id_link_group_semester_subject";
        Query q = getSession().createSQLQuery(query)
                .setLong("idNewDicGroup", idNewDicGroup)
                .setLong("idOldDicGroup", idOldDicGroup);
        List<Long> list = (List<Long>) getList(q);
        if(list.size() == 0){
            return false;
        } else {
            return true;
        }
    }
}