package org.edec.contingentMovement.manager;

import org.edec.contingentMovement.model.GroupModel;
import org.edec.contingentMovement.model.StudentMoveModel;
import org.edec.dao.MineDAO;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportManagerMineDAO extends MineDAO {

    public List<GroupModel> getAllGroups (String fos, Integer insitute) {
        String formOfStudy = "VG.Форма_Обучения IN ("+fos+")";

        String query = "SELECT \n" +
                "\tDistinct(VG.Название) AS groupname, SP.Название AS speciality, CA.Сокращение AS shortDepartment, CA.Название AS department, SP.Название_Спец AS specname\n" +
                "\tFROM Все_Студенты VS\n" +
                "\tINNER JOIN Все_группы VG ON VG.код = VS.код_группы\n" +
                "\tINNER JOIN Специальности SP ON SP.код = VG.Код_Специальности\n" +
                "\tINNER JOIN Кафедры CA ON CA.код = SP.Код_Кафедры\n" +
                "\tWHERE\n" +
                "\tVS.Статус NOT IN (3,4)\n" +
                "\tAND "+formOfStudy+"\n" +
                "\tAND VG.Код_Факультета = :institute\n" +
                "\tORDER BY VG.Название;";
        Query q = getSession().createSQLQuery(query)
                .addScalar("groupname")
                .addScalar("speciality")
                .addScalar("shortDepartment")
                .addScalar("department")
                .addScalar("specname")
                .setInteger("institute", insitute)
                .setResultTransformer(Transformers.aliasToBean(GroupModel.class));
        List<GroupModel> list = q.list();
        return (list != null && list.size()>0) ? list : null;
    }

    /**
     * Текущее количество бюджетников для конкретной группы
     */
    public List<StudentMoveModel> getCountBudget (String groupName) {
        String query = "SELECT\n" +
                "VS.код AS id,\n" +
                "ISNULL(VS.Фамилия, '') + ' ' + ISNULL(VS.Имя, '') + ' ' + ISNULL(VS.Отчество, '') AS fio\n" +
                "FROM Все_Студенты VS\n" +
                "INNER JOIN Все_группы VG ON VG.код = VS.код_группы\n" +
                "INNER JOIN Статус_Студента SS ON SS.код = VS.статус\n" +
                "WHERE\n" +
                "VS.Статус NOT IN (3, 4)\n" +
                "AND\n" +
                "(VS.Основания = 'ОО' OR VS.Основания = 'ЦН')\n" +
                "AND \n" +
                "VG.Название = :groupName ORDER BY VS.Фамилия";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("fio")
                .setString("groupName", groupName)
                .setResultTransformer(Transformers.aliasToBean(StudentMoveModel.class));
        return getCountResult(q);
    }

    /**
     * Текущее количество платников для конкретной группы
     */
    public List<StudentMoveModel> getCountUnBudget (String groupName) {
        String query = "SELECT \n" +
                "VS.код AS id,\n" +
                "ISNULL(VS.Фамилия, '') + ' ' + ISNULL(VS.Имя, '') + ' ' + ISNULL(VS.Отчество, '') AS fio\n" +
                "FROM Все_Студенты VS\n" +
                "INNER JOIN Все_группы VG ON VG.код = VS.код_группы\n" +
                "INNER JOIN Статус_Студента SS ON SS.код = VS.статус\n" +
                "WHERE\n" +
                "VS.Статус NOT IN (3, 4)\n" +
                "AND\n" +
                "VS.Основания = 'СН'\n" +
                "AND \n" +
                "VG.Название = :groupName ORDER BY VS.Фамилия";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("fio")
                .setString("groupName", groupName)
                .setResultTransformer(Transformers.aliasToBean(StudentMoveModel.class));
        return getCountResult(q);
    }

    /**
     * Текущее количество бюджетников академистов для конкретной группы
     */
    public List<StudentMoveModel> getCountBudgetAcademic (String groupName) {
        String query = "SELECT \n" +
                "VS.код AS id,\n" +
                "ISNULL(VS.Фамилия, '') + ' ' + ISNULL(VS.Имя, '') + ' ' + ISNULL(VS.Отчество, '') AS fio\n" +
                "FROM Все_Студенты VS\n" +
                "INNER JOIN Все_группы VG ON VG.код = VS.код_группы\n" +
                "INNER JOIN Статус_Студента SS ON SS.код = VS.статус\n" +
                "WHERE\n" +
                "VS.Статус = -1 \n" +
                "AND\n" +
                "(VS.Основания = 'ОО' OR VS.Основания = 'ЦН')\n" +
                "AND \n" +
                "VG.Название = :groupName ORDER BY VS.Фамилия";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("fio")
                .setString("groupName", groupName)
                .setResultTransformer(Transformers.aliasToBean(StudentMoveModel.class));
        return getCountResult(q);
    }

    /**
     * Текущее количество платников академистов для конкретной группы
     */
    public List<StudentMoveModel> getCountUnBudgetAcademic (String groupName) {
        String query = "SELECT \n" +
                "VS.код AS id,\n" +
                "ISNULL(VS.Фамилия, '') + ' ' + ISNULL(VS.Имя, '') + ' ' + ISNULL(VS.Отчество, '') AS fio\n" +
                "FROM Все_Студенты VS\n" +
                "INNER JOIN Все_группы VG ON VG.код = VS.код_группы\n" +
                "INNER JOIN Статус_Студента SS ON SS.код = VS.статус\n" +
                "WHERE\n" +
                "VS.Статус = -1 \n" +
                "AND\n" +
                "VS.Основания = 'СН'\n" +
                "AND \n" +
                "VG.Название = :groupName ORDER BY VS.Фамилия";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("fio")
                .setString("groupName", groupName)
                .setResultTransformer(Transformers.aliasToBean(StudentMoveModel.class));
        return getCountResult(q);
    }

    /**
     * Количество прибывших в группу платников(перевод, восстановление) для конкретной группы
     */
    public List<StudentMoveModel> getCountUnBudgetMoveIn (String groupName, Date fromDate, Date toDate) {
        String query = "SELECT \n" +
                "VS.код AS id,\n" +
                "ISNULL(VS.Фамилия, '') + ' ' + ISNULL(VS.Имя, '') + ' ' + ISNULL(VS.Отчество, '') AS fio,\n" +
                "ISNULL(PE.Документ, '') + ' ' + Тип_Перемещения AS reason\n" +
                "FROM Все_Студенты VS\n" +
                "INNER JOIN Все_группы VG ON VG.код = VS.код_группы\n" +
                "INNER JOIN Статус_Студента SS ON SS.код = VS.статус\n" +
                "INNER JOIN Перемещения PE ON PE.код_студента = VS.код\n" +
                "WHERE\n" +
                "(PE.Из_Группы <> :groupName OR PE.Из_Группы IS NULL)\n" +
                "AND PE.В_Группу = :groupName\n" +
                "AND PE.В_Группу IS NOT NULL\n" +
                "AND PE.Основания_Обучения IN ('СН', 'Сверхплановый набор')\n" +
                "AND (PE.Дата_Перемещения >= :fromDate AND PE.Дата_Перемещения <= :toDate) ORDER BY VS.Фамилия";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("fio")
                .addScalar("reason")
                .setString("groupName", groupName)
                .setDate("fromDate", fromDate)
                .setDate("toDate", toDate)
                .setResultTransformer(Transformers.aliasToBean(StudentMoveModel.class));
;
        return getCountResult(q);
    }

    /**
     * Количество прибывших в группу бюджетников для конкретной группы
     */
    public List<StudentMoveModel> getCountBudgetMoveIn (String groupName, Date fromDate, Date toDate) {
        String query = "SELECT \n" +
                "VS.код AS id,\n" +
                "ISNULL(VS.Фамилия, '') + ' ' + ISNULL(VS.Имя, '') + ' ' + ISNULL(VS.Отчество, '') AS fio,\n" +
                "ISNULL(PE.Документ, '') + ' ' + Тип_Перемещения AS reason\n" +
                "FROM Все_Студенты VS\n" +
                "INNER JOIN Все_группы VG ON VG.код = VS.код_группы\n" +
                "INNER JOIN Статус_Студента SS ON SS.код = VS.статус\n" +
                "INNER JOIN Перемещения PE ON PE.код_студента = VS.код\n" +
                "WHERE\n" +
                "(PE.Из_Группы <> :groupName OR PE.Из_Группы IS NULL) \n" +
                "AND PE.В_Группу = :groupName\n" +
                "AND PE.В_Группу IS NOT NULL\n" +
                "AND PE.Основания_Обучения NOT IN ('СН', 'Сверхплановый набор')\n" +
                "AND (PE.Дата_Перемещения >= :fromDate AND PE.Дата_Перемещения <= :toDate) ORDER BY VS.Фамилия";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("fio")
                .addScalar("reason")
                .setString("groupName", groupName)
                .setDate("fromDate", fromDate)
                .setDate("toDate", toDate)
                .setResultTransformer(Transformers.aliasToBean(StudentMoveModel.class));
        return getCountResult(q);
    }

    /**
     * Количество выбывших(отчисленных, переведенных) из группы бюджетников для конкретной группы
     */
    public List<StudentMoveModel> getCountBudgetMoveOut (String groupName, Date fromDate, Date toDate) {
        String query = "SELECT \n" +
                "VS.код AS id,\n" +
                "ISNULL(VS.Фамилия, '') + ' ' + ISNULL(VS.Имя, '') + ' ' + ISNULL(VS.Отчество, '') AS fio,\n" +
                "ISNULL(PE.Документ, '') + ' ' + Тип_Перемещения AS reason\n" +
                "FROM Все_Студенты VS\n" +
                "INNER JOIN Все_группы VG ON VG.код = VS.код_группы\n" +
                "INNER JOIN Статус_Студента SS ON SS.код = VS.статус\n" +
                "INNER JOIN Перемещения PE ON PE.код_студента = VS.код\n" +
                "WHERE\n" +
                "PE.Из_Группы = :groupName \n" +
                "AND (PE.В_Группу <> :groupName OR PE.В_Группу <> 'Академ.отпуск' OR PE.В_Группу IS NULL)\n" +
                "AND PE.ПрошлыйСтатус <> -1 \n" +
                "AND PE.КодПриказа = 4\n" +
                "AND PE.Основания_Обучения NOT IN ('СН', 'Сверхплановый набор')\n" +
                "AND (PE.Дата_Перемещения >= :fromDate AND PE.Дата_Перемещения <= :toDate) ORDER BY VS.Фамилия";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("fio")
                .addScalar("reason")
                .setString("groupName", groupName)
                .setDate("fromDate", fromDate)
                .setDate("toDate", toDate)
                .setResultTransformer(Transformers.aliasToBean(StudentMoveModel.class));
        return getCountResult(q);
    }

    /**
     * Количество выбывших из группы платников для конкретной группы
     */
    public List<StudentMoveModel> getCountUnBudgetMoveOut (String groupName, Date fromDate, Date toDate) {
        String query = "SELECT \n" +
                "VS.код AS id,\n" +
                "ISNULL(VS.Фамилия, '') + ' ' + ISNULL(VS.Имя, '') + ' ' + ISNULL(VS.Отчество, '') AS fio,\n" +
                "ISNULL(PE.Документ, '') + ' ' + Тип_Перемещения AS reason\n" +
                "FROM Все_Студенты VS\n" +
                "INNER JOIN Все_группы VG ON VG.код = VS.код_группы\n" +
                "INNER JOIN Статус_Студента SS ON SS.код = VS.статус\n" +
                "INNER JOIN Перемещения PE ON PE.код_студента = VS.код\n" +
                "WHERE\n" +
                "PE.Из_Группы = :groupName \n" +
                "AND (PE.В_Группу <> :groupName OR PE.В_Группу <> 'Академ.отпуск' OR PE.В_Группу IS NULL)\n" +
                "AND PE.ПрошлыйСтатус <> -1 \n" +
                "AND PE.КодПриказа = 4\n" +
                "AND PE.Основания_Обучения IN ('СН', 'Сверхплановый набор')\n" +
                "AND (PE.Дата_Перемещения >= :fromDate AND PE.Дата_Перемещения <= :toDate) ORDER BY VS.Фамилия";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("fio")
                .addScalar("reason")
                .setString("groupName", groupName)
                .setDate("fromDate", fromDate)
                .setDate("toDate", toDate)
                .setResultTransformer(Transformers.aliasToBean(StudentMoveModel.class));
        return getCountResult(q);
    }

    /**
     * Количество выбывших академистов бюджетников для конкретной группы
     */
    /*
    public List<String> getCountBudgetAcademicMoveOut (String groupName, Date fromDate, Date toDate) {
        String query = "SELECT \n" +
                "VS.Фамилия + ' ' + VS.Имя + ' ' + VS.Отчество\n" +
                "FROM Все_Студенты VS\n" +
                "INNER JOIN Все_группы VG ON VG.код = VS.код_группы\n" +
                "INNER JOIN Статус_Студента SS ON SS.код = VS.статус\n" +
                "INNER JOIN Перемещения PE ON PE.код_студента = VS.код\n" +
                "WHERE\n" +
                "PE.Из_Группы = :groupName \n" +
                "AND PE.В_Группу = 'Академ.отпуск'\n" +
                "AND PE.В_Группу IS NOT NULL\n" +
                "AND PE.Основания_Обучения NOT IN ('СН', 'Сверхплановый набор')\n" +
                "AND (PE.Дата_Перемещения > :fromDate AND PE.Дата_Перемещения < :toDate) ORDER BY VS.Фамилия";
        Query q = getSession().createSQLQuery(query)
                .setString("groupName", groupName)
                .setDate("fromDate", fromDate)
                .setDate("toDate", toDate);
        return getCountResult(q);
    }*/
    public List<StudentMoveModel> getCountBudgetAcademicMoveOut (String groupName, Date fromDate, Date toDate) {
        String query = "SELECT \n" +
                "VS.код AS id,\n" +
                "ISNULL(VS.Фамилия, '') + ' ' + ISNULL(VS.Имя, '') + ' ' + ISNULL(VS.Отчество, '') AS fio,\n" +
                "ISNULL(PE.Документ, '') + ' ' + Тип_Перемещения AS reason\n" +
                "FROM Все_Студенты VS\n" +
                "INNER JOIN Все_группы VG ON VG.код = VS.код_группы\n" +
                "INNER JOIN Статус_Студента SS ON SS.код = VS.статус\n" +
                "INNER JOIN Перемещения PE ON PE.код_студента = VS.код\n" +
                "WHERE\n" +
                "PE.Из_Группы = :groupName \n" +
                "AND PE.ПрошлыйСтатус = -1 \n" +
                "AND PE.КодПриказа = 4\n" +
                "AND PE.Основания_Обучения NOT IN ('СН', 'Сверхплановый набор')\n" +
                "AND (PE.Дата_Перемещения >= :fromDate AND PE.Дата_Перемещения <= :toDate) ORDER BY VS.Фамилия";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("fio")
                .addScalar("reason")
                .setString("groupName", groupName)
                .setDate("fromDate", fromDate)
                .setDate("toDate", toDate)
                .setResultTransformer(Transformers.aliasToBean(StudentMoveModel.class));
        return getCountResult(q);
    }

    /**
     * Количество выбывших академистов платников для конкретной группы
     */
    public List<StudentMoveModel> getCountUnBudgetAcademicMoveOut (String groupName, Date fromDate, Date toDate) {
        String query = "SELECT \n" +
                "VS.код AS id,\n" +
                "ISNULL(VS.Фамилия, '') + ' ' + ISNULL(VS.Имя, '') + ' ' + ISNULL(VS.Отчество, '') AS fio,\n" +
                "ISNULL(PE.Документ, '') + ' ' + Тип_Перемещения AS reason\n" +
                "FROM Все_Студенты VS\n" +
                "INNER JOIN Все_группы VG ON VG.код = VS.код_группы\n" +
                "INNER JOIN Статус_Студента SS ON SS.код = VS.статус\n" +
                "INNER JOIN Перемещения PE ON PE.код_студента = VS.код\n" +
                "WHERE\n" +
                "PE.Из_Группы = :groupName \n" +
                "AND PE.ПрошлыйСтатус = -1 \n" +
                "AND PE.КодПриказа = 4\n" +
                "AND PE.Основания_Обучения IN ('СН', 'Сверхплановый набор')\n" +
                "AND (PE.Дата_Перемещения >= :fromDate AND PE.Дата_Перемещения <= :toDate) ORDER BY VS.Фамилия";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("fio")
                .addScalar("reason")
                .setString("groupName", groupName)
                .setDate("fromDate", fromDate)
                .setDate("toDate", toDate)
                .setResultTransformer(Transformers.aliasToBean(StudentMoveModel.class));
        return getCountResult(q);
    }

    /**
     * Количество прибывших академистов бюджетников для конкретной группы
     */
    public List<StudentMoveModel> getCountBudgetAcademicMoveIn (String groupName, Date fromDate, Date toDate) {
        String query = "SELECT \n" +
                "VS.код AS id,\n" +
                "ISNULL(VS.Фамилия, '') + ' ' + ISNULL(VS.Имя, '') + ' ' + ISNULL(VS.Отчество, '') AS fio,\n" +
                "ISNULL(PE.Документ, '') + ' ' + Тип_Перемещения AS reason\n" +
                "FROM Все_Студенты VS\n" +
                "INNER JOIN Все_группы VG ON VG.код = VS.код_группы\n" +
                "INNER JOIN Статус_Студента SS ON SS.код = VS.статус\n" +
                "INNER JOIN Перемещения PE ON PE.код_студента = VS.код\n" +
                "WHERE\n" +
                "PE.Из_Группы = :groupName \n" +
                "AND PE.ПрошлыйСтатус = -1 \n" +
                "AND PE.Основания_Обучения NOT IN ('СН', 'Сверхплановый набор')\n" +
                "AND (PE.Дата_Перемещения >= :fromDate AND PE.Дата_Перемещения <= :toDate) ORDER BY VS.Фамилия";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("fio")
                .addScalar("reason")
                .setString("groupName", groupName)
                .setDate("fromDate", fromDate)
                .setDate("toDate", toDate)
                .setResultTransformer(Transformers.aliasToBean(StudentMoveModel.class));
        return getCountResult(q);
    }

    /**
     * Количество прибывших академистов платников для конкретной группы
     */
    public List<StudentMoveModel> getCountUnBudgetAcademicMoveIn (String groupName, Date fromDate, Date toDate) {
        String query = "SELECT \n" +
                "VS.код AS id,\n" +
                "ISNULL(VS.Фамилия, '') + ' ' + ISNULL(VS.Имя, '') + ' ' + ISNULL(VS.Отчество, '') AS fio,\n" +
                "ISNULL(PE.Документ, '') + ' ' + Тип_Перемещения AS reason\n" +
                "FROM Все_Студенты VS\n" +
                "INNER JOIN Все_группы VG ON VG.код = VS.код_группы\n" +
                "INNER JOIN Статус_Студента SS ON SS.код = VS.статус\n" +
                "INNER JOIN Перемещения PE ON PE.код_студента = VS.код\n" +
                "WHERE\n" +
                "PE.В_Группу = :groupName \n" +
                "AND PE.ПрошлыйСтатус = -1 \n" +
                "AND PE.Основания_Обучения IN ('СН', 'Сверхплановый набор')\n" +
                "AND (PE.Дата_Перемещения >= :fromDate AND PE.Дата_Перемещения <= :toDate) ORDER BY VS.Фамилия";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("fio")
                .addScalar("reason")
                .setString("groupName", groupName)
                .setDate("fromDate", fromDate)
                .setDate("toDate", toDate)
                .setResultTransformer(Transformers.aliasToBean(StudentMoveModel.class));
        return getCountResult(q);
    }

    /**
     * Обработка одноцифрового результата
     * @param q
     * @return
     */
    public List<StudentMoveModel> getCountResult (Query q)
    {
        List<StudentMoveModel> list = q.list();
        return (list != null && list.size()>0) ? list : new ArrayList<>();
    }

}
