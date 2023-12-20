package org.edec.newMine.orders.manager;

import org.edec.dao.DAO;
import org.edec.newMine.orders.model.OrderStudentsModel;
import org.edec.utility.constants.ScholarshipTypeConst;
import org.hibernate.Query;

import java.text.SimpleDateFormat;
import java.util.List;

public class OrderEsoManager extends DAO {

    public void updateScholarshipDatesFromExport(ScholarshipTypeConst typeScholarship, List<OrderStudentsModel> students) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (OrderStudentsModel student : students) {
            //language=GenericSQL
            String query = "UPDATE studentcard\n" +
                    "SET order_info = jsonb_set(order_info, CAST('{" + typeScholarship.getJsonName() + "}' AS text[]),\n" +
                    "                           jsonb_build_object('dateFrom',\n'" + sdf.format(student.getDateFrom()) + "'\n"+
                    "                             , 'dateTo',\n'" + sdf.format(student.getDateTo())+"'\n" +
                    "                           ), true)\n" +
                    "WHERE other_dbuid = " + student.getIdStudent();

            Query q = getSession().createSQLQuery(query);
            executeUpdate(q) ;
        }
    }
}
