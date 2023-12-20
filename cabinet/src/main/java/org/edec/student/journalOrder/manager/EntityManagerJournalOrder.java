package org.edec.student.journalOrder.manager;

import org.apache.commons.lang.StringUtils;
import org.edec.dao.DAO;
import org.edec.dao.MineDAO;
import org.edec.student.journalOrder.model.JournalOrderModel;
import org.edec.student.journalOrder.model.Scholarship;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;

import java.util.List;
import java.util.stream.Collectors;


public class EntityManagerJournalOrder extends MineDAO {
    public List<JournalOrderModel> getJournalOrder (Long idHum) {
        String query =
                "SELECT \n" + "\t LOSS.first_date as firstDate, id_order_type as idOrderType, os.description as sectionDescription, LOSS.second_date as secondDate, ORR.head_description as headDescription, OH.ordernumber AS orderNumber, DG.groupname, OT.name AS orderType, OH.dateoffinish AS dateSignOrder,\n" +
                "\tEXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend)||' ('||CASE WHEN SEM.season = 0 THEN 'осень' ELSE 'весна' END||')' AS semesterStr\n" +
                "FROM \n" + "\tstudentcard SC \n" + "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SEM USING (id_semester)\n" + "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN link_order_student_status LOSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_order_section LOS USING (id_link_order_section)\n" +
                "\tjoin order_section os on los.id_order_section = os.id_order_section\n" +
                "\tINNER JOIN order_head OH USING (id_order_head)\n" + "\tINNER JOIN order_rule ORR on orr.id_order_rule = oh.id_order_rule \n" +
                "\tINNER JOIN order_type OT USING (id_order_type)\n" + "WHERE \n" +
                "\tSC.id_humanface = :idHum AND OH.id_order_status_type = 6\n" + "ORDER BY \n" + "\tOH.dateoffinish";
        Query q = DAO.getSession().createSQLQuery(query)
                              .addScalar("orderNumber")
                              .addScalar("groupname")
                              .addScalar("orderType")
                              .addScalar("dateSignOrder")
                              .addScalar("semesterStr")
                              .addScalar("headDescription")
                              .addScalar("firstDate")
                              .addScalar("secondDate")
                              .addScalar("sectionDescription")
                              .addScalar("idOrderType", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(JournalOrderModel.class));
        q.setLong("idHum", idHum);
        return (List<JournalOrderModel>) DAO.getStList(q);
    }

    public String getSummOfScholarship(Long otherIdSC, String orderNumber, String year) {
        try {
            String query = "select  ПДС.Сумма \n" +
                    "from  ПриказыДействияСтуденты ПДС \n" +
                    "join Приказы П on П.Код_Приказа = ПДС.КодПриказа \n" +
                    "join Все_Студенты ВС on ВС.Код = ПДС.КодСтудента \n" +
                    "join ВСе_Группы ВГ on ВС.Код_Группы = ВГ.Код \n" +
                    "where П.Номер = '" + orderNumber + "' and ПДС.КодСтудента = " + otherIdSC + "  \n" +
                    "order by ПДС.Сумма desc";
            Query q = org.edec.dao.MineDAO.getSession().createSQLQuery(query);
            if (!q.list().isEmpty()) {
                List<String> listSumm = q.list();
                return listSumm.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Scholarship> getMassSummOfScholarship(List<String> orders, Long otherIdSC) {
        try {
            String orderNumbers = "'0'";
            for (String order : orders) {
                orderNumbers += ",";
                orderNumbers += "'"+order+"'";
            }

            String query = "select П.Номер as orderNum, ПДС.Сумма as summ \n" +
                    "from  ПриказыДействияСтуденты ПДС \n" +
                    "join Приказы П on П.Код_Приказа = ПДС.КодПриказа \n" +
                    "join Все_Студенты ВС on ВС.Код = ПДС.КодСтудента \n" +
                    "join ВСе_Группы ВГ on ВС.Код_Группы = ВГ.Код \n" +
                    "where П.Номер IN (" + orderNumbers + ") and ПДС.КодСтудента = " + otherIdSC + "  \n" +
                    "order by ПДС.Сумма desc";
            Query q = org.edec.dao.MineDAO.getSession()
                    .createSQLQuery(query)
                    .addScalar("orderNum")
                    .addScalar("summ")
                    .setResultTransformer(Transformers.aliasToBean(Scholarship.class));
            return (List<Scholarship>) getList(q);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Long getIdOtherSC (Long idHF) {
        String query = "select distinct sc.other_dbuid\n" +
                "from humanface hf\n" +
                "join studentcard sc using (id_humanface)\n" +
                "join student_semester_status sss using (id_studentcard)\n" +
                "where hf.id_humanface = " + idHF +
                " and sss.is_deducted = 0 order by sc.other_dbuid desc limit 1";
        Query q = DAO.getSession().createSQLQuery(query);
        return (Long) q.uniqueResult();
    }
}
