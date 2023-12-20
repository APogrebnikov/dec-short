package org.edec.contingentMovement.manager;

import org.edec.contingentMovement.model.GroupModel;
import org.edec.contingentMovement.model.ReportModel;
import org.edec.dao.DAO;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;

import java.util.Date;
import java.util.List;

public class ReportManagerDAO extends DAO {

    /**
     * Получаем список всех всех групп курса
     * В текущей версии жестко зашит последний семестр и ИКИТ в качестве института
     */
    public List<GroupModel> getGroupList (Integer fos) {
        String query = "SELECT DG.id_dic_group AS idDG, DG.groupname AS groupname\n" +
                "FROM dic_group DG\n" +
                "INNER JOIN link_group_semester LGS ON LGS.id_dic_group = DG.id_dic_group\n" +
                "INNER JOIN semester SE ON SE.id_semester=LGS.id_semester\n" +
                "WHERE SE.is_current_sem = 1 AND SE.id_institute = 1 AND SE.formofstudy = :fos ORDER BY DG.groupname";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idDG", LongType.INSTANCE)
                .addScalar("groupname")
                .setInteger("fos", fos)
                .setResultTransformer(Transformers.aliasToBean(GroupModel.class));
        List<GroupModel> list = q.list();
        return (list != null && list.size()>0) ? list : null;
    }

    /**
     * Записываем одну запись измерения
     */
    public boolean insertOneMeasure(ReportModel reportModel, Date dateCreated) {
        String query = "INSERT INTO contingent (\n" +
                "\tid_dic_group,\n" +
                "\tbudget,\n" +
                "\tunbudget,\n" +
                "\tbudget_academic,\n" +
                "\tunbudget_academic,\n" +
                "\tbudget_move_in,\n" +
                "\tunbudget_move_in,\n" +
                "\tbudget_move_out,\n" +
                "\tunbudget_move_out,\n" +
                "\tbudget_academic_move_out,\n" +
                "\tunbudget_academic_move_out,\n" +
                "\tdate_created\n" +
                ")\n" +
                "VALUES\n" +
                "(\n" +
                "\t(SELECT dgi.id_dic_group FROM dic_group dgi WHERE dgi.groupname = :groupname LIMIT 1),\n" +
                "\t:budget,\n" +
                "\t:unbudget,\n" +
                "\t:budget_academic,\n" +
                "\t:unbudget_academic,\n" +
                "\t:budget_move_in,\n" +
                "\t:unbudget_move_in,\n" +
                "\t:budget_move_out,\n" +
                "\t:unbudget_move_out,\n" +
                "\t:budget_academic_move_out,\n" +
                "\t:unbudget_academic_move_out,\n" +
                "\t:date_created\n" +
                ") \n" +
                "ON CONFLICT (id_dic_group, date_created) \n" +
                "DO\n" +
                " UPDATE SET   \n" +
                "\tbudget = :budget,\n" +
                "\tunbudget = :unbudget,\n" +
                "\tbudget_academic = :budget_academic,\n" +
                "\tunbudget_academic = :unbudget_academic,\n" +
                "\tbudget_move_in = :budget_move_in,\n" +
                "\tunbudget_move_in = :unbudget_move_in,\n" +
                "\tbudget_move_out = :budget_move_out,\n" +
                "\tunbudget_move_out = :unbudget_move_out,\n" +
                "\tbudget_academic_move_out = :budget_academic_move_out," +
                "\tunbudget_academic_move_out = :unbudget_academic_move_out";
        Query q = getSession().createSQLQuery(query)
                .setString("groupname", reportModel.getGroupName())
                .setInteger("budget", reportModel.getBudgetStudents().size())
                .setInteger("unbudget", reportModel.getUnBudgetStudents().size())
                .setInteger("budget_academic", reportModel.getBudgetAcademicStudents().size())
                .setInteger("unbudget_academic", reportModel.getUnBudgetAcademicStudents().size())
                .setInteger("budget_move_in", reportModel.getBudgetMoveInStudents().size())
                .setInteger("unbudget_move_in", reportModel.getUnBudgetMoveInStudents().size())
                .setInteger("budget_move_out", reportModel.getBudgetMoveOutStudents().size())
                .setInteger("unbudget_move_out", reportModel.getUnBudgetMoveOutStudents().size())
                .setInteger("budget_academic_move_out", reportModel.getBudgetAcademicMoveOutStudents().size())
                .setInteger("unbudget_academic_move_out", reportModel.getUnBudgetAcademicMoveOutStudents().size())
                .setDate("date_created", dateCreated);
        try
        {
            executeUpdate(q);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    /**
     * Получение списка строк отчета за конкретную дату
     * @param date
     * @return
     */
    public List<ReportModel> getMoveReportForDate(Date date) {
        String query = "SELECT groupName, \n" +
                "budget as countOfBudget, \n" +
                "unbudget as countOfUnBudget, \n" +
                "budget_academic as countOfBudgetAcademic, \n" +
                "unbudget_academic as countOfUnBudgetAcademic\n" +
                "FROM contingent CO\n" +
                "INNER JOIN Dic_Group DG ON DG.id_dic_group = CO.id_dic_group\n" +
                "WHERE date_created = :date";
        Query q = getSession().createSQLQuery(query)
                .addScalar("groupName")
                .addScalar("countOfBudget", IntegerType.INSTANCE)
                .addScalar("countOfUnBudget", IntegerType.INSTANCE)
                .addScalar("countOfBudgetAcademic", IntegerType.INSTANCE)
                .addScalar("countOfUnBudgetAcademic", IntegerType.INSTANCE)
                .setDate("date",date)
                .setResultTransformer(Transformers.aliasToBean(ReportModel.class));
        return q.list();
    }
}
