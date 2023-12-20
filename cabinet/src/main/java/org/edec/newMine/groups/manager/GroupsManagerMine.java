package org.edec.newMine.groups.manager;

import org.edec.dao.MineDAO;
import org.edec.newMine.groups.model.GroupsModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import java.util.List;

public class GroupsManagerMine extends MineDAO {

    public List<GroupsModel> getGroupsByInstYearStudyAndFormOfStudy(Long idInst, String year, Integer formOfStudies) {
        String fos = (formOfStudies == 1 ? " (1) " : " (2,3) ");
        String query = " select distinct Гр.Код AS idGroupMine, Гр.Название AS groupname,  Гр.Курс AS course, П.Титул AS specialityTitle, Спец.Специальность AS directionCode,\n" +
                "П.Титул AS directionTitle,  П.Код AS idCurriculumMine,  Каф.Название AS chairName,  Каф.Код AS idChairMine,\n" +
                " CASE WHEN Гр.Форма_Обучения = 1 THEN 1 ELSE 2 END AS formOfStudy,\n" +
                " CASE WHEN Гр.Форма_Обучения = 2 THEN 2 WHEN Гр.Форма_Обучения = 3 THEN 1 END AS distanceType,\n" +
                " CASE WHEN ((RIGHT(Гр.Название, 1) = 'Б' OR RIGHT(Гр.Название, 1) = 'В')) THEN 2 WHEN RIGHT(Гр.Название,1) = 'М' THEN 3 ELSE 1 END AS qualification, \n" +
                " CONVERT(FLOAT, REPLACE(Гр.СрокОбучения, ',', '.')) AS periodOfStudy, П.ИмяФайла AS planfileName,\n" +
                " CAST(CONCAT(SUBSTRING(П.УчебныйГод, 0, CHARINDEX('-', П.УчебныйГод)), '-09-01') AS DATETIME) AS createdSchoolYear, \n" +
                " CAST(CONCAT(П.ГодНачалаПодготовки, '-09-01') AS DATETIME) AS enterSchoolYear\n" +
                " from Все_Группы Гр\n" +
                " join Планы П on П.Код = Гр.КодПлана\n" +
                " JOIN Специальности Спец ON Гр.Код_специальности = Спец.код\n" +
                " JOIN Кафедры Каф ON П.КодПрофКафедры = Каф.Код\n" +
                " WHERE Гр.УчебныйГод = '"+year+"' and Гр.Код_Факультета = "+idInst+
                " and Гр.Форма_Обучения in "+fos+
                //" and П.КодСпециальности is not null\n" +
                " order by Гр.Курс, Гр.Название";
        Query q = getSession().createSQLQuery(query)
                .setResultTransformer(Transformers.aliasToBean(GroupsModel.class));
        return (List<GroupsModel>) getList(q);
    }
}
