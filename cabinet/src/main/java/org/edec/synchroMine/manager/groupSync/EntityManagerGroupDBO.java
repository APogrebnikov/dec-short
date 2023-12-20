package org.edec.synchroMine.manager.groupSync;

import org.edec.dao.MineDAO;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;

import java.util.List;

/**
 * @author Alex
 */
public class EntityManagerGroupDBO extends MineDAO {
    public List<GroupMineModel> getGroupsBySemester (String year, Long instituteId) {
        String query = "SELECT gr.[Код] AS idGroupMine,\n" + "\tgr.[КодПлана] AS idCurriculumMine,\n" + "\tgr.[Название] AS groupname,\n" +
                       "\tgr.[Код_Специальности] AS idDirectionMine,\n" + "\tgr.[Форма_Обучения] AS formOfStudy,\n" +
                       "\tsp.[Срок_Обучения] AS periodOfStudy,\n" + "\tpl.[КодТипаПлана] AS qualification,\n" +
                       "\tka.[Название] AS chairName,\n" + "\tsp.[Код_Кафедры] AS chairIdMine,\n" +
                       "\tsp.[Название_Спец] AS specialityTitle,\n" + "\tsp.[Специальность] AS directionCode,\n" +
                       "\tpl.[ИмяФайла] AS planfileName,\n" + "\tgr.[Курс] AS course\n" + "\tFROM [Деканат].[dbo].[Все_Группы] gr \n" +
                       "\tINNER JOIN Деканат.dbo.Планы pl ON pl.Код=gr.КодПлана \n" +
                       "\tINNER JOIN Деканат.dbo.Специальности sp ON sp.Код=gr.Код_Специальности \n" +
                       "\tLEFT JOIN Деканат.dbo.Кафедры ka ON ka.Код=sp.Код_Кафедры \n" +
                       "\tWHERE gr.УчебныйГод LIKE :YEAR AND gr.Код_Факультета=:idInst";

        Query q = getSession().createSQLQuery(query).addScalar("idGroupMine", LongType.INSTANCE) //Код группы
                              .addScalar("idCurriculumMine", LongType.INSTANCE) //Код плана
                              .addScalar("groupname") //Название
                              .addScalar("idDirectionMine", LongType.INSTANCE) //Код специальности
                              .addScalar("formOfStudy", IntegerType.INSTANCE) //[Форма_обучения]
                              .addScalar("periodOfStudy", FloatType.INSTANCE) //[Срок_обучения]
                              .addScalar("qualification", IntegerType.INSTANCE) //Код Типа плана
                              .addScalar("chairName")  //Имя кафедры
                              .addScalar("idChairMine", LongType.INSTANCE)  //Код кафедры
                              .addScalar("specialityTitle") //[Название_Спец]
                              .addScalar("directionCode") //[Специальность]
                              .addScalar("planfileName") //[ИмяФайла]
                              .addScalar("course", IntegerType.INSTANCE) //[Курс]
                              .setResultTransformer(Transformers.aliasToBean(GroupMineModel.class));
        //Date enterYear;
        //Integer generation;
        //String qualificationCode;
        //String directionTitle;
        q.setString("YEAR", year).setLong("idInst", instituteId);
        return (List<GroupMineModel>) getList(q);
    }
}
