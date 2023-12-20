package org.edec.contingentMovement.manager;

import org.edec.dao.MineDAO;
import org.edec.studentPassport.model.StudentStatusModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;

import java.util.List;

/**
 * @author Alex
 */
public class StudentStatusMineDAO extends MineDAO {
    public List<StudentStatusModel> getStudentByFilter (String fio, String recordbook, String groupname) {
        String query = "SELECT vs.Код AS mineId,\n" +
                "vs.Фамилия as family, vs.Имя as name, vs.Отчество as patronymic,\n" +
                "CASE WHEN vs.Пол = 'Жен' THEN 0 WHEN vs.Пол = 'Муж' THEN 1 END As sex,\n" +
                "vs.Номер_Зачетной_Книжки as recordBook, vg.Название as groupname, vg.Курс as course, \n" +
                "CASE\n" + "WHEN vs.Статус = -1 THEN 1\n" + "ELSE 0\n" + "END as academicLeave,\n" +
                "CASE\n" + "WHEN vs.Статус = 3 THEN 1\n" + "ELSE 0\n" + "END as deducted,  \n" +
                "CASE\n" + "WHEN vs.Статус = 4 THEN 1\n" + "ELSE 0\n" + "END as educationcomplite,  \n" +
                "vs.Дата_Рождения as birthday,\n" +
                "null as idSSS, vs.E_Mail as email,\n" + "CASE\n" + "WHEN vs.Номер_Договора is not null THEN 1\n" + "ELSE 0\n" +
                "END as trustAgreement, \n" + "CASE\n" + "WHEN vs.Номер_Договора is null THEN 1\n" + "ELSE 0\n" +
                "END as governmentFinanced, \n" + "null as idHum, \n" + "null as idDG, \n" + "null as idStudentCard,\n" +
                "vs.Иностранец as foreigner,\n" + "vg.Код_Факультета as idInstitute\n" + "FROM\n" + "[Все_Студенты] vs\n" +
                "INNER JOIN [Все_Группы] vg ON vg.Код=vs.Код_Группы\n" + "WHERE\n" +
                "\t(vs.Фамилия+' '+vs.Имя+' '+vs.Отчество) LIKE :fio\n" + "\tAND vs.Номер_Зачетной_Книжки LIKE :recordbook\n" +
                "\tAND vg.Название LIKE :groupname\n" + "ORDER BY vs.Фамилия, vs.Имя";
        Query q = getSession().createSQLQuery(query)
                .addScalar("mineId", LongType.INSTANCE)
                .addScalar("family")
                .addScalar("name")
                .addScalar("patronymic")
                .addScalar("sex")
                .addScalar("recordBook")
                .addScalar("groupname")
                .addScalar("course")
                .addScalar("academicLeave", BooleanType.INSTANCE)
                .addScalar("deducted", BooleanType.INSTANCE)
                .addScalar("educationcomplite", BooleanType.INSTANCE)
                .addScalar("birthday")
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("email")
                .addScalar("trustAgreement", BooleanType.INSTANCE)
                .addScalar("governmentFinanced", BooleanType.INSTANCE)
                .addScalar("idHum", LongType.INSTANCE)
                .addScalar("idDG", LongType.INSTANCE)
                .addScalar("idStudentCard", LongType.INSTANCE)
                .addScalar("foreigner")
                .addScalar("idInstitute", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(StudentStatusModel.class));
        q.setParameter("fio", "%" + fio + "%")
                .setParameter("recordbook", "%" + recordbook + "%")
                .setParameter("groupname", "%" + groupname + "%");
        return (List<StudentStatusModel>) getList(q);
    }
}
