package org.edec.order.service.orderCreator;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.order.manager.CreateOrderManagerESO;
import org.edec.order.model.OrderRuleModel;
import org.edec.order.model.OrderSectionModel;
import org.edec.order.model.StudentToAddModel;
import org.zkoss.zk.ui.Executions;

import java.util.Date;
import java.util.List;


public class DeductionCreator {
    CreateOrderManagerESO managerCreate = new CreateOrderManagerESO();

    public Long createDeductionStudentInitiative (OrderRuleModel rule, List<StudentToAddModel> listStudentsToAdd, Long idSemester) {
        List<OrderSectionModel> listSections = managerCreate.getListOrderSection(rule.getId());

        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        Long idOrder = managerCreate.createEmptyOrder(rule.getId(), new Date(), idSemester, idHumanface, "");

        for (OrderSectionModel item : listSections) {
            Long idLOS = managerCreate.createLinkOrderSection(idOrder, item.getId(), item.getFoundation(), new Date(), new Date());

            for (StudentToAddModel student : listStudentsToAdd) {
                managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(), student.getFirstDate(), student.getSecondDate(),
                                                           student.getGroup(), ""
                );
            }
        }

        return idOrder;
    }
}
