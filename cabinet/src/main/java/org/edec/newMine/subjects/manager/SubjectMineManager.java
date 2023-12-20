package org.edec.newMine.subjects.manager;

import org.edec.dao.MineDAO;
import org.edec.newMine.subjects.model.SubjectMineModel;
import org.edec.newMine.subjects.model.SubjectWorkloadModel;
import org.edec.newMine.subjects.model.SubjectsModel;
import org.edec.newMine.subjects.model.SubjectsTeacherModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.NumericBooleanType;

import java.util.List;
import java.util.Set;

public class SubjectMineManager extends MineDAO {
        public List<SubjectsModel> getMineSubjects(String groupname,  Integer semester) {
        String query = "SELECT  distinct Нагр.КодПлана as idCurriculum, Нагр.УчебныйГод as year, Пстр.Дисциплина AS subjectname, Пстр.КодДисциплины AS idSubjMine, \n" +
                " ПлПр.Экзамен AS exam, CASE WHEN (ПлПр.Зачёт = 1 OR ПлПр.ЗачётСОценкой = 1) THEN 1 ELSE 0 END pass,\n" +
                "ПлПр.КурсовойПроект AS cp, ПлПр.КурсоваяРабота AS cw, CASE WHEN ПлПр.ЗачётСОценкой = 1 THEN 1 ELSE 0 END type,\n" +
                "COALESCE (ПЧасыС.Лекции, ПЧасы.Лекций, 0) AS hoursLecture, COALESCE (ПЧасыС.Практики, ПЧасы.Практик, 0) AS hoursPractice,\n" +
                "COALESCE (ПЧасыС.Лабораторные, ПЧасы.Лабораторных, 0) AS hoursLabaratory, COALESCE (ПЧасыС.СамРабота, ПЧасы.СамРабота, 0) AS hoursIndependent,\n" +
                "COALESCE (ПЧасыС.ЧасовНаЭкзамены, ПЧасы.ЧасовНаЭкзамены, 0) AS hoursExam, 1 AS practicType,\n" +
                "Пстр.КодКафедры AS idChairMine, \n" +
                "Пстр.Факультатив AS facultative, Пстр.ДисциплинаКод AS subjectcode,\n" +
                "CASE WHEN График LIKE '%П%' THEN charindex('П', ПГраф.График) - 1\n" +
                "WHEN График like '%У%' THEN charindex('У', ПГраф.График) - 1\n" +
                "ELSE -1 END AS practicStartWeek,\n" +
                "CASE WHEN График LIKE '%П%' THEN len(ПГраф.График) - charindex('П', reverse(ПГраф.График)) - charindex('П', ПГраф.График) + 2\n" +
                "WHEN График LIKE '%У%' THEN len(ПГраф.График) - charindex('У', reverse(ПГраф.График)) - charindex('У', ПГраф.График) + 2\n" +
                "ELSE -1 END AS practicWeekCount,\n" +
                "CASE WHEN График LIKE '%П%' THEN 2\n" +
                "WHEN График LIKE '%У%' THEN 1\n" +
                "END AS typeOfPractice,\n" +
                "Нагр.Сессия AS sessionNumber\n" +
                "FROM  Нагрузка AS Нагр\n" +
                "INNER JOIN ПланыСтроки Пстр ON Нагр.КодПлана = Пстр.КодПлана\n" +
                "INNER JOIN ПланыПрактики ПлПр ON Пстр.Дисциплина = ПлПр.ВидПрактики AND ПлПр.КодПлана = Нагр.КодПлана \n" +
                "INNER JOIN ПланыЧасы ПЧасы ON ПСтр.Код = ПЧасы.КодСтроки AND ПЧасы.Курс = Нагр.Курс\n" +
                "LEFT JOIN ПланыЧасыСессии ПЧасыС ON ПЧасы.Код = ПЧасыС.КодСтрокиЧасы\n" +
                "LEFT JOIN ПланыГрафики ПГраф ON Нагр.КодПлана = ПГраф.КодПлана AND ПГраф.Курс = Нагр.Курс\n" +
                "WHERE   (Нагр.КодДисциплины = Пстр.КодДисциплины\n" +
                "OR Нагр.Дисциплина = Пстр.Дисциплина\n" +
                "OR CONCAT(ПлПр.КатегорияПрактики,' практика/', Пстр.Дисциплина) = Нагр.Дисциплина\n" +
                "OR Нагр.Дисциплина LIKE CONCAT(ПлПр.КатегорияПрактики,' практика/', Пстр.Дисциплина, '%')\n" +
                "OR Нагр.Дисциплина LIKE CONCAT('%/', Пстр.Дисциплина)\n" +
                "OR Нагр.Дисциплина LIKE CONCAT(CONCAT('%/', Пстр.Дисциплина), ',%')\n" +
                "OR REPLACE(REPLACE(Нагр.Дисциплина, '   ', ' '), '  ', ' ') = REPLACE(REPLACE(CONCAT(ПлПр.КатегорияПрактики,' практика/', Пстр.Дисциплина), '   ', ' '), '  ', ' ')\n" +
                "OR Нагр.КодДисциплины = (SELECT Код FROM ПланыДисциплины WHERE Дисциплина = Пстр.Дисциплина))\n" +
                "AND ((ПЧасыС.ВидСессии IS NOT NULL AND ПЧасыС.ВидСессии = "+ ((semester % 2 == 1) ? 2 : 3) +")\n" +
                "OR (ПЧасыС.ВидСессии IS NULL AND ПЧасы.Семестр = "+semester+") \n" +
                "OR (ПЧасыС.ВидСессии IS NULL AND ПЧасы.Семестр = ПлПр.Семестр)) and Нагр.Группа LIKE '"+groupname+"' and Нагр.Семестр = "+semester+" \n" +
                " AND (Нагр.ПроизведённыеДействия IS NULL OR Нагр.ПроизведённыеДействия NOT LIKE 'Строка исключена из нагрузки%')\n" +
                "UNION\n" +
                "SELECT distinct Нагр.КодПлана as idCurriculum, Нагр.УчебныйГод as year, Пстр.Дисциплина AS subjectname, Пстр.КодДисциплины AS idSubjMine, \n" +
                "CASE WHEN (ПЧасыС.Экзамен = 1 OR (ПЧасыС.Код IS NULL AND ПЧасы.Экзамен = 1)) THEN 1 ELSE 0 END exam,\n" +
                "CASE WHEN (ПЧасыС.Зачет = 1 OR ПЧасыС.ЗачетСОценкой = 1 OR (ПЧасыС.Код IS NULL AND (ПЧасы.Зачет = 1 OR ПЧасы.ЗачетСОценкой = 1))) THEN 1 ELSE 0 END pass,\n" +
                "CASE WHEN (ПЧасыС.КурсовойПроект = 1 OR (ПЧасыС.Код IS NULL AND ПЧасы.КурсовойПроект = 1)) THEN 1 ELSE 0 END cp,\n" +
                "CASE WHEN (ПЧасыС.КурсоваяРабота = 1 OR (ПЧасыС.Код IS NULL AND ПЧасы.КурсоваяРабота = 1)) THEN 1 ELSE 0 END cw, \n" +
                "CASE WHEN (ПЧасы.ЗачетСОценкой = 1 OR (ПЧасыС.Код IS NULL AND ПЧасыС.ЗачетСОценкой = 1)) THEN 1 ELSE 0 END type, \n" +
                "COALESCE (ПЧасыС.Лекции, ПЧасы.Лекций, 0) AS hoursLecture, COALESCE (ПЧасыС.Практики, ПЧасы.Практик, 0) AS hoursPractice,\n" +
                "COALESCE (ПЧасыС.Лабораторные, ПЧасы.Лабораторных, 0) AS hoursLabaratory, COALESCE (ПЧасыС.СамРабота, ПЧасы.СамРабота, 0) AS hoursIndependent,\n" +
                "COALESCE (ПЧасыС.ЧасовНаЭкзамены, ПЧасы.ЧасовНаЭкзамены, 0) AS hoursExam, 0 AS practicType, Пстр.КодКафедры AS idChairMine, \n" +
                "Пстр.Факультатив AS facultative,\n" +
                "Пстр.ДисциплинаКод AS subjectcode, NULL AS practicStartWeek, NULL AS practicWeekCount, NULL AS typeOfPractice,\n" +
                "Нагр.Сессия AS sessionNumber\n" +
                "FROM Нагрузка AS Нагр\n" +
                "INNER JOIN ПланыСтроки Пстр ON Нагр.КодПлана = Пстр.КодПлана\n" +
                "INNER JOIN ПланыЧасы ПЧасы ON ПСтр.Код = ПЧасы.КодСтроки AND ПЧасы.Курс = Нагр.Курс\n" +
                "LEFT JOIN ПланыЧасыСессии ПЧасыС ON ПЧасы.Код = ПЧасыС.КодСтрокиЧасы\n" +
                "WHERE   ((ПЧасыС.ВидСессии IS NOT NULL AND ПЧасыС.ВидСессии = "+ ((semester % 2 == 1) ? 2 : 3) +")\n" +
                " OR (ПЧасыС.ВидСессии IS NULL AND ПЧасы.Семестр = "+semester+")) and Нагр.Семестр = "+semester+"\n" +
                " AND (Нагр.КодДисциплины = Пстр.КодДисциплины OR Нагр.Дисциплина = Пстр.Дисциплина\n" +
                "  OR Нагр.КодДисциплины = (SELECT Код FROM ПланыДисциплины WHERE Дисциплина = Пстр.Дисциплина)) and Нагр.Группа LIKE '"+groupname+"'\n" +
                "  AND (Нагр.ПроизведённыеДействия IS NULL OR Нагр.ПроизведённыеДействия NOT LIKE 'Строка исключена из нагрузки%')\n" +
                "  ORDER BY subjectname, typeOfPractice DESC";
        Query q = getSession().createSQLQuery(query)
                .addScalar("subjectname").addScalar("subjectcode").addScalar("idSubjMine", LongType.INSTANCE)
                .addScalar("exam", NumericBooleanType.INSTANCE).addScalar("pass", NumericBooleanType.INSTANCE)
                .addScalar("cp", NumericBooleanType.INSTANCE)
                .addScalar("cw", NumericBooleanType.INSTANCE).addScalar("practicType", NumericBooleanType.INSTANCE)
                .addScalar("idChairMine", LongType.INSTANCE).addScalar("type")
                .addScalar("idCurriculum", LongType.INSTANCE).addScalar("year")
                .addScalar("hoursExam", DoubleType.INSTANCE).addScalar("hoursLecture", DoubleType.INSTANCE)
                .addScalar("hoursPractice", DoubleType.INSTANCE).addScalar("hoursLabaratory", DoubleType.INSTANCE)
                .addScalar("hoursIndependent", DoubleType.INSTANCE).addScalar("facultative")
                .addScalar("practicStartWeek").addScalar("practicWeekCount").addScalar("typeOfPractice")
                .addScalar("sessionNumber")
                .setResultTransformer(Transformers.aliasToBean(SubjectsModel.class));
        return q.list();
    }


    public List<SubjectsModel> getExtramuralSubjects (String groupname, Integer course) {
            String query = "SELECT  distinct ПЧасы.Курс, ПЧасы.Семестр, Нагр.Курс, Нагр.Семестр,Нагр.КодПлана as idCurriculum, \n" +
                    "Нагр.УчебныйГод as year, Пстр.Дисциплина AS subjectname, Пстр.КодДисциплины AS idSubjMine, \n" +
                    "coalesce (ПЧасыС.Экзамен, ПЧасы.Экзамен, 0)  as exam,\n" +
                    "coalesce (ПЧасыС.Зачет, ПЧасы.Зачет, 0)  as pass,\n" +
                    "coalesce (ПЧасыС.КурсоваяРабота, ПЧасы.КурсоваяРабота, 0)  as cw,\n" +
                    "coalesce (ПЧасыС.КурсовойПроект, ПЧасы.КурсовойПроект, 0)  as cp,\n" +
                    "CASE WHEN ПлПр.ЗачётСОценкой = 1 THEN 1 ELSE 0 END as type,\n" +
                    "COALESCE (ПЧасыС.Лекции, ПЧасы.Лекций, 0) AS hoursLecture, COALESCE (ПЧасыС.Практики, ПЧасы.Практик, 0) AS hoursPractice,\n" +
                    "COALESCE (ПЧасыС.Лабораторные, ПЧасы.Лабораторных, 0) AS hoursLabaratory, COALESCE (ПЧасыС.СамРабота, ПЧасы.СамРабота, 0) AS hoursIndependent,\n" +
                    "COALESCE (ПЧасыС.ЧасовНаЭкзамены, ПЧасы.ЧасовНаЭкзамены, 0) AS hoursExam, 1 AS practicType,\n" +
                    "Пстр.КодКафедры AS idChairMine, Пстр.Факультатив AS facultative, Пстр.ДисциплинаКод AS subjectcode,\n" +
                    "CASE WHEN График LIKE '%П%' THEN charindex('П', ПГраф.График) - 1\n" +
                    "WHEN График like '%У%' THEN charindex('У', ПГраф.График) - 1\n" +
                    "ELSE -1 END AS practicStartWeek,\n" +
                    "CASE WHEN График LIKE '%П%' THEN len(ПГраф.График) - charindex('П', reverse(ПГраф.График)) - charindex('П', ПГраф.График) + 2\n" +
                    "WHEN График LIKE '%У%' THEN len(ПГраф.График) - charindex('У', reverse(ПГраф.График)) - charindex('У', ПГраф.График) + 2\n" +
                    "ELSE -1 END AS practicWeekCount,\n" +
                    "CASE WHEN График LIKE '%П%' THEN 2\n" +
                    "WHEN График LIKE '%У%' THEN 1\n" +
                    "END AS typeOfPractice,\n" +
                    "Нагр.Сессия AS sessionNumber\n" +
                    "FROM  Планы П \n" +
                    "JOIN ПланыСтроки Пстр ON П.Код = Пстр.КодПлана\n" +
                    "JOIN ПланыЧасы ПЧасы ON ПСтр.Код = ПЧасы.КодСтроки \n" +
                    "LEFT JOIN ПланыЧасыСессии ПЧасыС ON ПЧасы.Код = ПЧасыС.КодСтрокиЧасы\n" +
                    "JOIN Нагрузка Нагр on Нагр.КодПлана = П.Код and Пстр.КодДисциплины = Нагр.КодДисциплины and (coalesce(Нагр.Экзамен,0)=coalesce(ПЧасыС.Экзамен, ПЧасы.Экзамен, 0))\n"+
                    "LEFT JOIN ПланыПрактики ПлПр ON Пстр.Дисциплина = ПлПр.ВидПрактики AND ПлПр.КодПлана = П.Код\n" +
                    "LEFT JOIN ПланыГрафики ПГраф ON Нагр.КодПлана = ПГраф.КодПлана AND ПГраф.Курс = Нагр.Курс \n" +
                    "where П.Код = (select П.Код\n" +
                    "from Планы П\n" +
                    "Join Все_Группы Г on Г.КодПлана = П.Код\n" +
                    "where Г.Название = '"+groupname+"' and Г.Курс = "+course+") \n" +
                    "and Пчасы.Курс = "+course+"\n" +
                    "and Нагр.ВидКонтроля is not null  \n" +
                    "order by subjectname";
        Query q = getSession().createSQLQuery(query)
                .addScalar("subjectname").addScalar("subjectcode").addScalar("idSubjMine", LongType.INSTANCE)
                .addScalar("exam", NumericBooleanType.INSTANCE).addScalar("pass", NumericBooleanType.INSTANCE)
                .addScalar("cp", NumericBooleanType.INSTANCE)
                .addScalar("cw", NumericBooleanType.INSTANCE).addScalar("practicType", NumericBooleanType.INSTANCE)
                .addScalar("idChairMine", LongType.INSTANCE).addScalar("type")
                .addScalar("idCurriculum", LongType.INSTANCE).addScalar("year")
                .addScalar("hoursExam", DoubleType.INSTANCE).addScalar("hoursLecture", DoubleType.INSTANCE)
                .addScalar("hoursPractice", DoubleType.INSTANCE).addScalar("hoursLabaratory", DoubleType.INSTANCE)
                .addScalar("hoursIndependent", DoubleType.INSTANCE).addScalar("facultative")
                .addScalar("practicStartWeek").addScalar("practicWeekCount").addScalar("typeOfPractice")
                .addScalar("sessionNumber")
                .setResultTransformer(Transformers.aliasToBean(SubjectsModel.class));
        return q.list();

    }

    public List<SubjectsTeacherModel> getTeachers(Long idCurriculum, String year, Long idSubjMine, Integer session) {
        String query = "select distinct Преп.Фамилия as family, Преп.Имя as name, Преп.Отчество as patronymic, " +
                "Преп.Код as idTeacherMine, Нагр.Код as idWorkload\n" +
                "from Нагрузка Нагр\n" +
                "join Преподаватели Преп on Преп.Код = Нагр.КодПреподавателя\n" +
                "where Нагр.КодПлана = " + idCurriculum + " and Нагр.УчебныйГод = '" + year + "' and Нагр.КодДисциплины = " + idSubjMine + " \n" +
                "and Нагр.ВидКонтроля is not null and Нагр.Сессия = " + session;
        Query q = getSession().createSQLQuery(query).addScalar("family").addScalar("name").addScalar("patronymic")
                .addScalar("idWorkload", LongType.INSTANCE).addScalar("idTeacherMine", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(SubjectsTeacherModel.class));
        return q.list();
    }

    public List<SubjectsModel> getMineSubjectsExtraShort(String groupname,  Integer semester) {
            String addsem = "";
            if (semester == 1) {
                addsem = "and ПЧасы.Семестр IN (0,1)";
            } else {
                addsem = "and ПЧасы.Семестр = "+semester;
            }
        String query = "SELECT  distinct План.Код as idCurriculum, План.УчебныйГод as year, \n" +
                "                Пстр.Дисциплина AS subjectname, \n" +
                "                Пстр.КодДисциплины AS idSubjMine, \n" +
                "                CASE WHEN (ПЧасыС.Экзамен = 1 OR (ПЧасыС.Код IS NULL AND ПЧасы.Экзамен = 1)) THEN 1 ELSE 0 END exam,\n" +
                "                CASE WHEN (ПЧасыС.Зачет = 1 OR ПЧасыС.ЗачетСОценкой = 1 OR (ПЧасыС.Код IS NULL AND (ПЧасы.Зачет = 1 OR ПЧасы.ЗачетСОценкой = 1))) THEN 1 ELSE 0 END pass,\n" +
                "                CASE WHEN (ПЧасыС.КурсовойПроект = 1 OR (ПЧасыС.Код IS NULL AND ПЧасы.КурсовойПроект = 1)) THEN 1 ELSE 0 END cp,\n" +
                "                CASE WHEN (ПЧасыС.КурсоваяРабота = 1 OR (ПЧасыС.Код IS NULL AND ПЧасы.КурсоваяРабота = 1)) THEN 1 ELSE 0 END cw, \n" +
                "                CASE WHEN (ПЧасы.ЗачетСОценкой = 1 OR (ПЧасыС.Код IS NULL AND ПЧасыС.ЗачетСОценкой = 1)) THEN 1 ELSE 0 END type, \n" +
                "                COALESCE (ПЧасыС.Лекции, ПЧасы.Лекций, 0) AS hoursLecture, COALESCE (ПЧасыС.Практики, ПЧасы.Практик, 0) AS hoursPractice,\n" +
                "                COALESCE (ПЧасыС.Лабораторные, ПЧасы.Лабораторных, 0) AS hoursLabaratory, COALESCE (ПЧасыС.СамРабота, ПЧасы.СамРабота, 0) AS hoursIndependent,\n" +
                "                COALESCE (ПЧасыС.ЧасовНаЭкзамены, ПЧасы.ЧасовНаЭкзамены, 0) AS hoursExam, 0 AS practicType, Пстр.КодКафедры AS idChairMine, \n" +
                "                Пстр.Факультатив AS facultative, Пстр.ДисциплинаКод AS subjectcode\n" +
                "                FROM  Планы AS План\n" +
                "                INNER JOIN ПланыСтроки Пстр ON План.Код = Пстр.КодПлана\n" +
                "                INNER JOIN ПланыЧасы ПЧасы ON ПСтр.Код = ПЧасы.КодСтроки\n" +
                "                INNER JOIN Все_Группы гр ON гр.КодПлана=План.Код\n" +
                "                LEFT JOIN ПланыЧасыСессии ПЧасыС ON ПЧасы.Код = ПЧасыС.КодСтрокиЧасы\n" +
                "                WHERE \n" +
                "                гр.Название LIKE '"+groupname+"' " +addsem;
        System.out.println(query);
        Query q = getSession().createSQLQuery(query)
                .addScalar("subjectname").addScalar("subjectcode").addScalar("idSubjMine", LongType.INSTANCE)
                .addScalar("exam", NumericBooleanType.INSTANCE).addScalar("pass", NumericBooleanType.INSTANCE)
                .addScalar("cp", NumericBooleanType.INSTANCE)
                .addScalar("cw", NumericBooleanType.INSTANCE).addScalar("practicType", NumericBooleanType.INSTANCE)
                .addScalar("idChairMine", LongType.INSTANCE).addScalar("type")
                .addScalar("idCurriculum", LongType.INSTANCE).addScalar("year")
                .addScalar("hoursExam", DoubleType.INSTANCE).addScalar("hoursLecture", DoubleType.INSTANCE)
                .addScalar("hoursPractice", DoubleType.INSTANCE).addScalar("hoursLabaratory", DoubleType.INSTANCE)
                .addScalar("hoursIndependent", DoubleType.INSTANCE)
                .addScalar("facultative")
                .setResultTransformer(Transformers.aliasToBean(SubjectsModel.class));
        return q.list();
    }



}
