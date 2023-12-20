package org.edec.notification.manager;

import org.edec.dao.DAO;
import org.edec.model.HumanfaceModel;
import org.edec.notification.model.eso.DepartmentHumanESO;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import java.util.List;


public class NotificationEsoManager extends DAO {

    public HumanfaceModel getHumanfaceModel (Long idHum) {
        String query = "SELECT humanface.family, humanface.name, humanface.patronymic\n" + "       FROM humanface\n" +
                       "       WHERE humanface.id_humanface = :idHum";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("family")
                              .addScalar("name")
                              .addScalar("patronymic")
                              .setResultTransformer(Transformers.aliasToBean(HumanfaceModel.class));
        q.setLong("idHum", idHum);

        List<?> list = getList(q);
        return list.size() == 0 ? null : (HumanfaceModel) list.get(0);
    }

    public List<DepartmentHumanESO> getStudentDepartments (Long idInst, Integer formOfStudy, Integer course, String groupname,
                                                           String qualification, Boolean isGroupLeader, String fio) {
        String query = "SELECT  HF.family, HF.name, HF.patronymic, HF.id_humanface AS idHum,\n" + "    DG.groupname AS department\n" +
                       "FROM  humanface HF\n" + "  INNER JOIN studentcard SC USING (id_humanface)\n" +
                       "  INNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                       "  INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "  INNER JOIN semester SEM USING (id_semester)\n" + "  INNER JOIN dic_group DG USING (id_dic_group)\n" +
                       "  INNER JOIN curriculum CUR USING (id_curriculum)\n" +
                       "WHERE SEM.is_current_sem = 1 AND SEM.id_institute = :idInst AND SEM.formofstudy = :fos\n" +
                       "    AND CAST(LGS.course AS TEXT) ILIKE :course\n" + "    AND DG.groupname ILIKE :groupname\n" +
                       "    AND CUR.qualification IN (" + qualification + ")\n" +
                       "    AND CAST(SSS.is_group_leader AS TEXT) ILIKE :groupLeader\n" +
                       "    AND HF.family||' '||HF.name||' '||HF.patronymic ILIKE :fio\n" +
                       "ORDER BY LGS.course, CUR.qualification, groupname, family, name, patronymic;";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("family")
                              .addScalar("name")
                              .addScalar("patronymic")
                              .addScalar("idHum", LongType.INSTANCE)
                              .addScalar("department")
                              .setResultTransformer(Transformers.aliasToBean(DepartmentHumanESO.class));
        q.setLong("idInst", idInst)
         .setInteger("fos", formOfStudy)
         .setParameter("course", course == 0 ? "%%" : String.valueOf(course), StandardBasicTypes.STRING)
         .setParameter("groupname", "%" + groupname + "%", StandardBasicTypes.STRING)
         .setParameter("groupLeader", isGroupLeader ? "%1%" : "%%", StandardBasicTypes.STRING)
         .setParameter("fio", "%" + fio + "%", StandardBasicTypes.STRING);
        return (List<DepartmentHumanESO>) getList(q);
    }

    public List<DepartmentHumanESO> getEmployeeDepartments (String fio, String department, Boolean isSecretaryChair) {
        String query = "SELECT  HF.family, HF.name, HF.patronymic, HF.id_humanface AS idHum,\n" + "    DEP.fulltitle AS department\n" +
                       "FROM  humanface HF\n" + "    INNER JOIN employee EMP USING (id_humanface)\n" +
                       "    INNER JOIN link_employee_department LED USING (id_employee)\n" +
                       "    INNER JOIN department DEP USING (id_department)\n" + "WHERE\n" +
                       "    HF.family||' '||HF.name||' '||HF.patronymic ILIKE :fio\n" + "    AND DEP.fulltitle ILIKE :department\n" +
                       "    AND CAST(LED.id_employee_role AS TEXT) ILIKE :empRole";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("family")
                              .addScalar("name")
                              .addScalar("patronymic")
                              .addScalar("idHum", LongType.INSTANCE)
                              .addScalar("department")
                              .setResultTransformer(Transformers.aliasToBean(DepartmentHumanESO.class));
        q.setParameter("fio", "%" + fio + "%", StandardBasicTypes.STRING)
         .setParameter("department", "%" + department + "%", StandardBasicTypes.STRING)
         .setParameter("empRole", isSecretaryChair ? "27" : "%%", StandardBasicTypes.STRING);
        return (List<DepartmentHumanESO>) getList(q);
    }
}
