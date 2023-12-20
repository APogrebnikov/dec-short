package org.edec.newMine.students.manager;

import org.edec.dao.MineDAO;
import org.edec.newMine.students.model.StudentsModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;

public class StudentsManagerMine extends MineDAO {
    public List<StudentsModel> getStudentsByGroupName(String groupName) {
        String query = "SELECT Ст.Код AS idStudCardMine, Ст.Имя AS name, Ст.Фамилия AS family, Ст.Отчество AS patronymic,Ст.Дата_Рождения AS birthday,\n" +
                "\tСт.Статус AS status, Ст.Номер_Зачетной_книжки AS recordbook, Ст.УслОбучения AS condOfEducation,\n" +
                "\tCASE WHEN Ст.Пол LIKE 'Муж' OR Ст.Пол LIKE 'мужской' THEN 1 ELSE 0 END AS sex,\n" +
                "\tСт.hash AS hash\n" +
                "FROM Все_Студенты Ст\n" +
                "\tINNER JOIN Все_Группы Гр ON Ст.Код_Группы = Гр.Код\n" +
                "WHERE Гр.Название LIKE :groupname\n" +
                "ORDER BY family, name, patronymic";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idStudCardMine", LongType.INSTANCE)
                .addScalar("name")
                .addScalar("family")
                .addScalar("patronymic")
                .addScalar("status")
                .addScalar("recordbook")
                .addScalar("condOfEducation")
                .addScalar("sex")
                .addScalar("birthday")
                .addScalar("hash")
                .setResultTransformer(Transformers.aliasToBean(StudentsModel.class));
        q.setString("groupname", groupName);
        return (List<StudentsModel>) getList(q);
    }
}
