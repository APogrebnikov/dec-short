package org.edec.newMine.orders.manager;

import org.edec.dao.MineDAO;
import org.edec.newMine.orders.model.OrderActionsModel;
import org.edec.newMine.orders.model.OrderStudentsModel;
import org.edec.newMine.orders.model.OrdersModel;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;

import java.util.Date;
import java.util.List;

public class OrdersMineManager extends MineDAO {

    public List<OrdersModel> getMineOrder(Date dateCreateOrdersFrom) {
        String dateCreateStr = DateConverter.convertDateToStringByFormat(dateCreateOrdersFrom, "yyyy-MM-dd");

        String query = "select distinct П.Код_приказа as idOrerMine, cast(П.Дата as date) as dateSign, cast(П.ДатаСоздания as date) as dateCreate, П.Номер as ordersNumber,   \n" +
                "П.Приказ as ordersDescription\n" +
                "from Приказы П\n" +
                "join ПриказыДействия ПД on ПД.КодПриказа = П.Код_Приказа\n" +
                "join ПриказыВидыДействий ПВД on ПВД.Код = ПД.КодДействия\n" +
                "join ПриказыДействияСтуденты ПДС ON ПДС.КодПриказа = П.Код_приказа\n" +
                "where П.КодФакультета = 27 and П.Дата is not null\n" +
                "and  cast(П.ДатаСоздания as date) >= '"+dateCreateStr+"' \n" +
                "order by dateSign desc, dateCreate";
        Query q = getSession().createSQLQuery(query)
                .addScalar("dateSign", DateType.INSTANCE).addScalar("dateCreate",  DateType.INSTANCE)
                .addScalar("ordersNumber")
                .addScalar("ordersDescription")
                .addScalar("idOrerMine", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrdersModel.class));
        return q.list();
    }

    public List<OrderActionsModel> getOrdersActions(Long idOrderMine){
        String query = "select distinct ПД.Код as idOrdersAction, ПВД.Действие as ordersAction\n" +
                "from ПриказыДействияСтуденты ПДС\n" +
                "join ПриказыДействия ПД on ПДС.КодДействияПриказа = ПД.Код\n" +
                "join ПриказыВидыДействий ПВД on ПВД.Код = ПД.КодДействия\n" +
                "where ПДС.КодПриказа = " + idOrderMine;
        Query q  = getSession().createSQLQuery(query)
                .addScalar("idOrdersAction", LongType.INSTANCE)
                .addScalar("ordersAction")
                .setResultTransformer(Transformers.aliasToBean(OrderActionsModel.class));
        return q.list();
    }

    public List<OrderStudentsModel> getStudents (Long idOrder, Long idOrdersAction) {
        String query = "select Г.Курс as course, Г.Название as groupname,  С.Фамилия+' '+С.Имя+' '+С.Отчество as fio, " +
                "С.Номер_зачетной_Книжки as recordbook, coalesce(ПДС.Сумма, '-') as summ,\n" +
                "ДатаС as dateFrom, датаПо as dateTo, С.Код as idStudent\n"+
                "from Все_Студенты С\n" +
                "join ПриказыДействияСтуденты ПДС on С.Код = ПДС.КодСтудента\n" +
                "join Все_Группы Г on Г.Код =  С.Код_Группы\n" +
                "where ПДС.КодПриказа = "+idOrder+" and ПДС.КодДействияПриказа = "+idOrdersAction+"\n" +
                "order by Г.Курс, Г.Название, fio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio").addScalar("groupname").addScalar("recordbook").addScalar("summ")
                .addScalar("course").addScalar("dateFrom",DateType.INSTANCE).addScalar("dateTo",DateType.INSTANCE)
                .addScalar("idStudent",LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderStudentsModel.class));

        return q.list();
    }
}


