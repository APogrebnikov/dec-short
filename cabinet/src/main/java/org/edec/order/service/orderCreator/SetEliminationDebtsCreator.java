package org.edec.order.service.orderCreator;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.model.GroupModel;
import org.edec.order.manager.CreateOrderManagerESO;
import org.edec.order.model.OrderRuleModel;
import org.edec.order.model.OrderSectionModel;
import org.edec.order.model.StudentToAddModel;
import org.edec.order.model.StudentToCreateModel;
import org.zkoss.zk.ui.Executions;

import java.util.Date;
import java.util.List;

public class SetEliminationDebtsCreator {
    CreateOrderManagerESO managerCreate = new CreateOrderManagerESO();

    public Long createEliminationRespectful (OrderRuleModel rule, List<StudentToAddModel> listStudentsToAdd, Long idSemester) {
        List<OrderSectionModel> listSections = managerCreate.getListOrderSection(rule.getId());

        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        Long idOrder = managerCreate.createEmptyOrder(rule.getId(), new Date(), idSemester, idHumanface, "");

        for (OrderSectionModel item : listSections) {
            Long idLOS = managerCreate.createLinkOrderSection(idOrder, item.getId(), item.getFoundation(), new Date(), new Date());

            for (StudentToAddModel student : listStudentsToAdd) {
                if ((item.getName().endsWith("(Договор)") && !student.getGovernmentFinanced()) ||
                    (item.getName().endsWith("(Бюджет)") && student.getGovernmentFinanced())) {
                    managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(), student.getFirstDate(), null, student.getGroup(),
                                                               ""
                    );
                }
            }
        }

        return idOrder;
    }

    public Long createEliminationNotRespectful (OrderRuleModel rule, Date firstDate, String description, Long idSemester,
                                                Boolean isAfterPassWeek) {
        List<OrderSectionModel> listSections = managerCreate.getListOrderSection(rule.getId());

        List<StudentToCreateModel> listStudents;

        if (isAfterPassWeek) {
            listStudents = managerCreate.getStudentsForSetEliminationAfterPassWeek(idSemester);
        } else {
            listStudents = managerCreate.getStudentsForSetEliminationAfterSession(idSemester);
        }

        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        Long idOrder = managerCreate.createEmptyOrder(rule.getId(), new Date(), idSemester, idHumanface, description);

        for (OrderSectionModel item : listSections) {
            Long idLOS = managerCreate.createLinkOrderSection(idOrder, item.getId(), item.getFoundation(), firstDate, null);

            for (StudentToCreateModel student : listStudents) {
                if ((item.getName().endsWith("(Договор)") && !student.getGovernmentFinanced()) ||
                    (item.getName().endsWith("(Бюджет)") && student.getGovernmentFinanced())) {
                    managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(), firstDate, null, student.getGroupname(), "");
                }
            }
        }

        return idOrder;
    }
}
