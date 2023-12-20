package org.edec.synchroMine.manager.subjectSynch;

import org.edec.curriculumScan.model.Subject;
import org.edec.dao.DAO;
import org.edec.dao.MineDAO;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.FloatType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;

import java.util.List;


public class EntityManagerSubjectDBO extends MineDAO {

    public List<Subject> getSubjectsByLGS(String groupName, Integer semesterNumber) {
        // Обработка семестра
        String semNumberCondition = semesterNumber != null ? "AND ПЧасы.Семестр = "+semesterNumber : "";
        String semNumberConditionForPractic = semesterNumber != null ? "AND (ПлПр.Семестр = "+semesterNumber+" OR ПлПр.Семестр = "+(semesterNumber-1)+")" : "";

        String query =
                "SELECT \n" +
                        "        Пстр.Код AS id,\n" +
                        "        Пстр.Дисциплина AS name, \n" +
                        "        Пстр.КодДисциплины AS idMineSubject,\n" +
                        "        Пстр.ДисциплинаКод AS code,\n" +
                        "        CASE \n" +
                        "         WHEN COALESCE(ПлПр.Экзамен, 0) = 1 THEN 1\n" +
                        "         WHEN ПЧасыС.Экзамен = 1 OR ПЧасы.Экзамен = 1 THEN 1\n" +
                        "         ELSE 0 END AS isExam,\n" +
                        "        CASE " +
                        "         WHEN ПлПр.Код IS NOT NULL AND (ПлПр.Зачёт = 1 OR ПлПр.ЗачётСОценкой = 1) THEN 1\n" +
                        "         WHEN ПЧасыС.Зачет = 1 OR ПЧасыС.ЗачетСОценкой = 1 OR ПЧасы.Зачет = 1 OR ПЧасы.ЗачетСОценкой = 1 THEN 1\n" +
                        "         ELSE 0 END AS isPass,\n" +
                        "        CASE " +
                        "         WHEN COALESCE(ПлПр.КурсовойПроект, 0) = 1 THEN 1\n" +
                        "         WHEN ПЧасыС.КурсовойПроект = 1 OR ПЧасы.КурсовойПроект = 1 THEN 1\n" +
                        "         ELSE 0 END AS isCP,\n" +
                        "        CASE " +
                        "         WHEN COALESCE(ПлПр.КурсоваяРабота, 0) = 1 THEN 1\n" +
                        "         WHEN ПЧасыС.КурсоваяРабота = 1 OR ПЧасы.КурсоваяРабота = 1 THEN 1\n" +
                        "         ELSE 0 END AS isCW,\n" +
                        "        COALESCE (ПЧасыС.Лекции, ПЧасы.Лекций, 0) AS lecHours, \n" +
                        "        COALESCE (ПЧасыС.Практики, ПЧасы.Практик, 0) AS praHours,\n" +
                        "        COALESCE (ПЧасыС.Лабораторные, ПЧасы.Лабораторных, 0) AS labHours, \n" +
                        "        COALESCE (ПЧасыС.СамРабота, ПЧасы.СамРабота, 0) AS ksrHours,\n" +
                        "        COALESCE (ПЧасыС.ЧасовНаЭкзамены, ПЧасы.ЧасовНаЭкзамены, 0) AS examHours,\n" +
                        "        Пстр.ЧасовСамостоятельнаяРабота AS srHours,\n" +
                        "        CASE " +
                        "         WHEN ПлПр.Код IS NOT NULL THEN 1 " +
                        "         ELSE 0 END practicType,\n" +
                        "        ПЧасы.Семестр AS semesterNumber\n" +
                        "FROM \n" +
                        "        Все_Группы Гр\n" +
                        "        INNER JOIN Планы Пл ON Гр.КодПлана = Пл.Код\n" +
                        "        INNER JOIN ПланыСтроки Пстр ON Пл.Код = Пстр.КодПлана\n" +
                        "        INNER JOIN ПланыЧасы ПЧасы ON ПСтр.Код = ПЧасы.КодСтроки\n" +
                        "        LEFT JOIN ПланыЧасыСессии ПЧасыС ON ПЧасы.Код = ПЧасыС.КодСтрокиЧасы\n" +
                        "        LEFT JOIN ПланыПрактики ПлПр ON Пстр.Дисциплина = ПлПр.ВидПрактики AND ПлПр.КодПлана = Пл.Код "+semNumberConditionForPractic+"\n" +
                        "WHERE \n" +
                        "        Гр.Код_Факультета = 27 \n" +
                        "        AND Гр.Название LIKE :groupname AND Гр.Курс = (SELECT MAX(ГрФ.Курс) FROM Все_Группы ГрФ WHERE ГрФ.Название LIKE :groupname) \n" +
                        semNumberCondition;
        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("name")
                .addScalar("idMineSubject", LongType.INSTANCE)
                .addScalar("code")
                .addScalar("isExam", BooleanType.INSTANCE)
                .addScalar("isPass", BooleanType.INSTANCE)
                .addScalar("isCP", BooleanType.INSTANCE)
                .addScalar("isCW", BooleanType.INSTANCE)
                .addScalar("lecHours", FloatType.INSTANCE)
                .addScalar("labHours", FloatType.INSTANCE)
                .addScalar("praHours", FloatType.INSTANCE)
                .addScalar("ksrHours", FloatType.INSTANCE)
                .addScalar("examHours", FloatType.INSTANCE)
                .addScalar("semesterNumber", IntegerType.INSTANCE)
                .addScalar("srHours", FloatType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(Subject.class));
        q.setParameter("groupname", groupName);
        return (List<Subject>) getList(q);
    }
}