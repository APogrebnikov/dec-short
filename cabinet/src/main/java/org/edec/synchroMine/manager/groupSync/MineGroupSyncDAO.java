package org.edec.synchroMine.manager.groupSync;

import org.edec.dao.MineDAO;
import org.edec.synchroMine.model.GroupCompareResult;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import java.util.List;

public class MineGroupSyncDAO extends MineDAO {

    private static final String SELECT_MINE_GROUPS_BY_INST_YEAR_AND_FOS =
            //language=SQL
            "SELECT Гр.Код AS \"idGroupMine\"," +
            " Гр.Название AS \"groupname\", " +
            "Гр.Курс AS \"course\",\n" +
            "Спец.Название_спец AS \"specialityTitle\"," +
            " Спец.Специальность AS \"directionCode\",\n" +
            "RTRIM(LTRIM(CASE    WHEN Пл.Титул LIKE '%профиль: %' THEN SUBSTRING(Пл.Титул, CHARINDEX('профиль:', Пл.Титул) + LEN('профлиь:') + 1, LEN(Пл.Титул))\n" +
            "        WHEN Пл.Титул LIKE '%профиль подготовки %' THEN SUBSTRING(Пл.Титул, CHARINDEX('профиль подготовки', Пл.Титул) + LEN('профиль подготовки') + 1, LEN(Пл.Титул))\n" +
            "        WHEN Пл.Титул LIKE '%профиль %' THEN SUBSTRING(Пл.Титул, CHARINDEX('профиль', Пл.Титул) + LEN('профлиь') + 1, LEN(Пл.Титул))\n" +
            "        WHEN Пл.Титул LIKE '% программа подготовки %' THEN SUBSTRING(Пл.Титул, CHARINDEX('программа подготовки', Пл.Титул) + LEN('программа подготовки') + 1, LEN(Пл.Титул))\n" +
            "        WHEN Пл.Титул LIKE '% программа  подготовки %' THEN SUBSTRING(Пл.Титул, CHARINDEX('программа  подготовки', Пл.Титул) + LEN('программа  подготовки') + 1, LEN(Пл.Титул))\n" +
            "        WHEN Пл.Титул LIKE '% программа %' THEN SUBSTRING(Пл.Титул, CHARINDEX('программа', Пл.Титул) + LEN('программа') + 1, LEN(Пл.Титул)) \n" +
                    "END)) AS \"directionTitle\", \n" +
            "Пл.Код AS \"idCurriculumMine\", " +
            "Каф.Название AS \"chairName\"," +
            " Каф.Код AS \"idChairMine\",\n" +
            "CASE WHEN Гр.Форма_Обучения = 1 THEN 1 ELSE 2 END AS \"formOfStudy\",\n" +
            "CASE WHEN Гр.Форма_Обучения = 2 THEN 2 WHEN Гр.Форма_Обучения = 3 THEN 1 END AS \"distanceType\",\n" +
            "CASE WHEN ((RIGHT(Гр.Название, 1) = 'Б' OR RIGHT(Гр.Название, 1) = 'В')) THEN 2 WHEN RIGHT(Гр.Название,1) = 'М' THEN 3 ELSE 1 END AS \"qualification\", \n" +
            "CONVERT(FLOAT, REPLACE(Гр.СрокОбучения, ',', '.')) AS periodOfStudy, Пл.ИмяФайла AS \"planfileName\",\n" +
            "CAST(CONCAT(SUBSTRING(Пл.УчебныйГод, 0, CHARINDEX('-', Пл.УчебныйГод)), '-09-01') AS DATETIME) AS \"createdSchoolYear\", \n" +
            "CAST(CONCAT(Пл.ГодНачалаПодготовки, '-09-01') AS DATETIME) AS \"enterSchoolYear\"\n" +
            "FROM Все_Группы Гр\n" +
            "INNER JOIN Планы Пл ON Гр.КодПлана = Пл.Код\n" +
            "INNER JOIN Специальности Спец ON Гр.Код_специальности = Спец.код\n" +
            "LEFT JOIN Кафедры Каф ON Пл.КодПрофКафедры = Каф.Код\n" +
            "WHERE Гр.код_Факультета = :idInst AND Гр.учебныйГод = :year " +
            "AND Гр.Форма_Обучения IN (:formOfStudies)\n" +
            "ORDER BY course, groupname";

    private static final String SELECT_MINE_GROUPS_BY_INST_YEAR_AND_FOS2 =
            "SELECT Гр.Код AS \"idGroupMine\"," +
            " Гр.Название AS \"groupname\", " +
            " Гр.Курс AS \"course\"\n" +
            "FROM Все_Группы Гр\n" +
            "WHERE Гр.код_Факультета = :idInst " +
            "AND Гр.учебныйГод = :year " +
            "AND Гр.Форма_Обучения IN (:formOfStudies)\n" +
            "ORDER BY course, groupname";

    public List<GroupMineModel> getGroupsByInstYearStudyAndFormOfStudy(Long idInst, String year, List<Integer> formOfStudies) {
        Query q = getSession().createSQLQuery(SELECT_MINE_GROUPS_BY_INST_YEAR_AND_FOS)
                .setResultTransformer(Transformers.aliasToBean(GroupMineModel.class));
        q.setLong("idInst", idInst).setString("year", year)
                .setParameterList("formOfStudies", formOfStudies);
        return (List<GroupMineModel>) getList(q);
    }

    public List<GroupCompareResult> getGroupsByInstYearStudyAndFormOfStudy2(Long idInst, String year, List<Integer> formOfStudies) {
        Query q = getSession().createSQLQuery(SELECT_MINE_GROUPS_BY_INST_YEAR_AND_FOS2)
                .setResultTransformer(Transformers.aliasToBean(GroupCompareResult.class));
        q.setLong("idInst", idInst).setString("year", year)
                .setParameterList("formOfStudies", formOfStudies);
        return (List<GroupCompareResult>) getList(q);
    }
}
