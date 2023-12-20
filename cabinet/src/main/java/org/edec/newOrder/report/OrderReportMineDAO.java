package org.edec.newOrder.report;

import org.edec.dao.MineDAO;
import org.edec.newOrder.model.report.OrderReportModel;
import org.edec.synchroMine.model.mine.Student;
import org.hibernate.Query;

import java.util.List;

public class OrderReportMineDAO extends MineDAO {
    public List<OrderReportModel> getINN (String ids) {
        if (ids.equals("")) {
            ids = "0";
        }
        String query = "SELECT Документы.Номер_Документа, Все_Студенты.Код FROM Документы\n" +
                "INNER JOIN Все_Студенты ON Документы.Код_Студента = Все_Студенты.Код\n" +
                "WHERE Код_Документа = 3 AND Код_Студента in ("+ ids +")";
        Query q = getSession().createSQLQuery(query);
        return (List<OrderReportModel>) getList(q);
    }

    public boolean checkScholarship(String scholarshipName) {

        String query = "SELECT CASE WHEN EXISTS FROM Стипендия where Название = '" + scholarshipName + "')\n" +
                "THEN CAST(1 AS BIT)" +
                "ELSE CAST(0 AS BIT) END";

        Query q = getSession().createSQLQuery(query);

        return ((List<Boolean>) getList(q)).get(0);
    }
}
