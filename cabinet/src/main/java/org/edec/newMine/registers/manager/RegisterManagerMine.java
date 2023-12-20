package org.edec.newMine.registers.manager;

import org.edec.dao.MineDAO;
import org.edec.newMine.registers.model.RegistersSubjectsModel;
import org.edec.newMine.registers.model.StudentsRatingModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;

import java.util.List;

public class RegisterManagerMine extends MineDAO {
    public List<RegistersSubjectsModel> getGroupsSubject (String groupname, Integer semester, String year) {
        String query = "select distinct В.Код as idMineRegister,В.Тип_Ведомости  as typeMineRegister, Пл.Код as idCurriculumMine, ПлСтр.КодДисциплины as otherIdSubject, " +
                "Гр.Код as otherIdGroup,ПлСтр.Дисциплина as subjectname, \n" +
                "В.Семестр as semesterNumber,  ПлСтр.ДисциплинаКод as codeSubject,  Гр.Название as groupname\n" +
                "from  Все_Группы Гр\n" +
                "join Планы Пл on Пл.Код = Гр.КодПлана\n" +
                "left join ПланыСтроки ПлСтр on Пл.Код = ПлСтр.КодПлана and ПлСтр.КодПлана = Гр.КодПлана \n" +
                "join ПланыЧасы ПлЧ on ПлЧ.КодСтроки = ПлСтр.Код\n" +
                "join Специальности Спец on Спец.Код = Пл.КодСпециальности\n" +
                "join Все_Студенты Ст on Ст.Код_Группы = Гр.Код\n" +
                "left JOIN Ведомости В ON В.Код_Группы = Гр.Код  and В.Дисциплина = ПлСтр.Дисциплина  --and  В.Семестр = ПлЧ.Семестр \n" +
                "LEFT JOIN ПланыЧасыСессии ПЧасыС ON ПлЧ.Код = ПЧасыС.КодСтрокиЧасы\n" +
                "LEFT JOIN ПланыПрактики ПлПр ON ПлСтр.Дисциплина = ПлПр.ВидПрактики AND ПлПр.КодПлана = Пл.Код\n" +
                "where Гр.Название = '"+groupname+"' and В.Семестр = "+semester+" and В.Год = '"+year +"'  and В.Преподаватель<>'' \n" +
                "and (ПлПр.Экзамен<>0 or ПЧасыС.Экзамен<>0 or ПлЧ.Экзамен<>0\n" +
                "or ПлПр.Зачёт<>0 or ПлПр.ЗачётСОценкой<>0 or ПЧасыС.Зачет<>0 or ПЧасыС.ЗачетСОценкой<>0 or ПлЧ.Зачет<>0 or ПлЧ.ЗачетСОценкой<>0\n" +
                "or ПЧасыС.КурсовойПроект<>0 or ПлЧ.КурсовойПроект<>0\n" +
                "or ПЧасыС.КурсоваяРабота <>0 or ПлЧ.КурсоваяРабота<>0) \n" +
                "order by subjectname  ";
        Query q = getSession().createSQLQuery(query)
                .addScalar("otherIdSubject", LongType.INSTANCE).addScalar("otherIdGroup", LongType.INSTANCE)
                .addScalar("subjectname").addScalar("semesterNumber", IntegerType.INSTANCE)
                .addScalar("typeMineRegister").addScalar("idMineRegister", LongType.INSTANCE)
                .addScalar("idCurriculumMine", LongType.INSTANCE)
                .addScalar("codeSubject").addScalar("groupname").setResultTransformer(Transformers.aliasToBean(RegistersSubjectsModel.class));
        return (List<RegistersSubjectsModel>) getList(q);
    }

    public List<StudentsRatingModel> getStudentsAndMarks(Long idSubjectsMine, int typeRegister, Long idRegisterMine) {
        String query = "SELECT distinct  Ст.Код as idStudentMine, Ст.Фамилия as family, Ст.Имя as name, Ст.Отчество as patronymic, " +
                "REPLACE(В.Год,'-','/') AS schoolyear, В.Семестр AS semesternumber, В.Курс AS course, В.Дисциплина AS subjectName,\n" +
                " В.Часов AS hoursCount, В.ЗЕТ AS creditUnit, В.Тип_Ведомости AS typeRegister, CAST(О.Итог AS INTEGER) AS total,\n" +
                " В.Код AS idMineRegister, О.Итоговая_Оценка as mainRating, COALESCE(О.Дата_Сдачи, В.Дата_Экзамена) AS mainDateOfPass, \n" +
                " О.Пересдача1 as ratingRetake1, О.Дата_Пересдачи1 as dateRetake1, О.Пересдача2 as ratingRetake2, " +
                "О.Дата_Пересдачи2 as dateRetake2, О.Пересдача3 as ratingRetake3, О.Дата_Пересдачи3 as dateRetake3,\n" +
                "ПлПр.ВидПрактики AS typePractic\n" +
                " FROM Оценки О\n" +
                " JOIN Ведомости В ON В.Код = О.Код_Ведомости\n" +
                " left JOIN Все_Группы Гр ON В.Код_Группы = Гр.Код\n" +
                " left join Все_Студенты Ст on  О.Код_Студента = Ст.Код\n" +
                " LEFT JOIN ПланыСтроки ПСтр ON Пстр.Дисциплина = В.Дисциплина AND ПСтр.КодПлана = (SELECT TOP 1 КодПлана FROM Все_группы WHERE Название = Гр.Название ORDER BY КодПлана DESC)\n" +
                " LEFT JOIN ПланыПрактики ПлПр ON Пстр.Дисциплина = ПлПр.ВидПрактики AND ПлПр.КодПлана = ПСтр.КодПлана AND ПлПр.Семестр = В.Семестр\n" +
                " WHERE О.Итог not in (6, 0)  and ПСтр.КодДисциплины = "+idSubjectsMine+" and В.Тип_Ведомости = "+typeRegister+" and В.Код = "+idRegisterMine+"\n" +
                " ORDER BY Ст.Фамилия,course, semesternumber";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idStudentMine", LongType.INSTANCE)
                .addScalar("family").addScalar("name").addScalar("patronymic")
                .addScalar("schoolyear").addScalar("semesternumber").addScalar("course")
                .addScalar("subjectName").addScalar("hoursCount").addScalar("creditUnit", DoubleType.INSTANCE)
                .addScalar("typeRegister").addScalar("total").addScalar("idMineRegister", LongType.INSTANCE)
                .addScalar("mainRating").addScalar("mainDateOfPass").addScalar("ratingRetake1")
                .addScalar("dateRetake1").addScalar("ratingRetake2").addScalar("dateRetake2")
                .addScalar("ratingRetake3").addScalar("dateRetake3").addScalar("typePractic")
                .setResultTransformer(Transformers.aliasToBean(StudentsRatingModel.class));
        return (List<StudentsRatingModel>) getList(q);

    }

}
