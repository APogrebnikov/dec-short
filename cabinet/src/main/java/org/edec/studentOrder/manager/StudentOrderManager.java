package org.edec.studentOrder.manager;

import org.edec.dao.DAO;
import org.edec.studentOrder.model.OrderModel;
import org.edec.studentOrder.model.StudentOrderModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;

public class StudentOrderManager extends DAO {
    public List<StudentOrderModel> searchStudent (String name, String family, int sem) {

        String query = "select id_student_semester_status as idStudentSemesterStatus, family, name, patronymic, groupname\n" +
                       "from humanface \n" + " inner join  studentcard using (id_humanface)\n" +
                       "inner join  student_semester_status using (id_studentcard)\n" +
                       "inner join  link_group_semester using (id_link_group_semester)\n" + " inner join  semester using (id_semester)\n" +
                       " inner join  dic_group using (id_dic_group)\n" + "\n" + "where family ilike '%" + family + "%' and name ilike '%" +
                       name + "%' and id_semester = " + sem + " ";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("idStudentSemesterStatus", LongType.INSTANCE)
                              .addScalar("family")
                              .addScalar("name")
                              .addScalar("patronymic")
                              .addScalar("groupname")
                              .setResultTransformer(Transformers.aliasToBean(StudentOrderModel.class));
        List<StudentOrderModel> list = q.list();
        return list;
    }

    public List<OrderModel> getOrder (long id) {

        String query =
                "select id_order_head as idOrderHead, descriptionspec, dateofbegin as dateOfBegin, orr.name as typeOrder, os.name as section\n" +
                "from \n" + "link_order_student_status loss \n" + "inner join link_order_section los  using(id_link_order_section)\n" +
                "inner join order_section os using (id_order_section)\n" + "inner join order_head oh using(id_order_head)\n" +
                "inner join order_rule orr on oh.id_order_rule = orr.id_order_rule\n" + "\n" + "\n" +
                "where id_student_semester_status = " + id + "\n" + " ";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("idOrderHead", LongType.INSTANCE)
                              .addScalar("descriptionspec")
                              .addScalar("dateOfBegin")
                              .addScalar("typeOrder")
                              .addScalar("section")
                              .setResultTransformer(Transformers.aliasToBean(OrderModel.class));
        List<OrderModel> list = q.list();
        return list;
    }
}
