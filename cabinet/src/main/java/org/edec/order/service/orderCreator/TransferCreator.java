package org.edec.order.service.orderCreator;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.model.GroupModel;
import org.edec.order.manager.CreateOrderManagerESO;
import org.edec.order.model.OrderRuleModel;
import org.edec.order.model.OrderSectionModel;
import org.edec.order.model.StudentToCreateModel;
import org.edec.utility.constants.OrderStudentJSONConst;
import org.json.JSONObject;
import org.zkoss.zk.ui.Executions;

import java.util.Date;
import java.util.List;


public class TransferCreator {
    CreateOrderManagerESO managerCreate = new CreateOrderManagerESO();

    public Long createTransferOrder (OrderRuleModel rule, Date firstDate, String description, Long idSemester, List<GroupModel> groups) {
        List<OrderSectionModel> listSections = managerCreate.getListOrderSection(rule.getId());

        String groupsStr = "";

        for (GroupModel group : groups) {
            groupsStr += ("'" + group.getGroupname() + "',");
        }

        if (groupsStr.length() > 0) {
            groupsStr = groupsStr.substring(0, groupsStr.length() - 1);
        }

        List<StudentToCreateModel> listStudents = managerCreate.getStudentsForTransferOrder(idSemester, groupsStr);

        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        Long idOrder = managerCreate.createEmptyOrder(rule.getId(), new Date(), idSemester, idHumanface, description);

        for (OrderSectionModel item : listSections) {
            Long idLOS = managerCreate.createLinkOrderSection(idOrder, item.getId(), item.getFoundation(), firstDate, null);

            for (StudentToCreateModel student : listStudents) {
                int allSemesters = (int) (student.getPeriodOfStudy() * 2);

                if (allSemesters == student.getSemesternumber()) {
                    continue;
                }

                if ((item.getName().endsWith("(Договор)") && !student.getGovernmentFinanced()) ||
                    (item.getName().endsWith("(Бюджет)") && student.getGovernmentFinanced())) {
                    managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(), firstDate, null, student.getGroupname(), "");
                }
            }
        }

        return idOrder;
    }

    public Long createTransferConditionallyOrder (OrderRuleModel rule, Date firstDate, Date secondDate, String description, Long idSemester,
                                                  List<GroupModel> groups) {
        List<OrderSectionModel> listSections = managerCreate.getListOrderSection(rule.getId());

        String groupsStr = "";

        for (GroupModel group : groups) {
            groupsStr += ("'" + group.getGroupname() + "',");
        }

        if (groupsStr.length() > 0) {
            groupsStr = groupsStr.substring(0, groupsStr.length() - 1);
        }

        List<StudentToCreateModel> listStudents = managerCreate.getStudentsForTransferConditionallyOrder(idSemester, groupsStr);

        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        Long idOrder = managerCreate.createEmptyOrder(rule.getId(), new Date(), idSemester, idHumanface, description);

        for (OrderSectionModel item : listSections) {
            Long idLOS = managerCreate.createLinkOrderSection(idOrder, item.getId(), item.getFoundation(), firstDate, secondDate);

            if (item.getName().endsWith(" ув. причина)")) {
                continue;
            }

            for (StudentToCreateModel student : listStudents) {
                int allSemesters = (int) (student.getPeriodOfStudy() * 2);

                if (allSemesters == student.getSemesternumber()) {
                    continue;
                }

                if ((item.getName().endsWith("(Бюджет, неув. причина)") && student.getGovernmentFinanced()) ||
                    (item.getName().endsWith("(Договор, неув. причина)") && !student.getGovernmentFinanced())) {
                    managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(), firstDate, secondDate, student.getGroupname(), "");
                }
            }
        }

        return idOrder;
    }

    public Long createTransferAfterTransferConditionally (OrderRuleModel rule, Date firstDate, Date secondDate, String description,
                                                          Long idSemester, List<GroupModel> groups, boolean isRespectful) {
        List<OrderSectionModel> listSections = managerCreate.getListOrderSection(rule.getId());

        StringBuilder groupsStrBuilder = new StringBuilder();
        for (GroupModel group : groups) {
            groupsStrBuilder.append("'").append(group.getGroupname()).append("',");
        }
        String groupsStr = groupsStrBuilder.toString();

        if (groupsStr.length() > 0) {
            groupsStr = groupsStr.substring(0, groupsStr.length() - 1);
        }

        List<StudentToCreateModel> listStudents = managerCreate.getStudentsForTransferAfterTransfer(idSemester, groupsStr, isRespectful);

        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        Long idOrder = managerCreate.createEmptyOrder(rule.getId(), new Date(), idSemester, idHumanface, description);

        for (OrderSectionModel item : listSections) {
            Long idLOS = managerCreate.createLinkOrderSection(idOrder, item.getId(), item.getFoundation(), firstDate, secondDate);

            for (StudentToCreateModel student : listStudents) {
                if (student.getDeductedCurSem() != null && student.getDeductedCurSem()) {
                    continue;
                }

                if (student.getDeductedCurSem() == null) {
                    continue;
                }

                if ((item.getName().endsWith("(Бюджет)") && student.getGovernmentFinanced()) ||
                    (item.getName().endsWith("(Договор)") && !student.getGovernmentFinanced())) {
                    JSONObject additionalJson = new JSONObject();
                    additionalJson.put(OrderStudentJSONConst.ORDER_NUMBER, student.getPrevOrderNumber());
                    additionalJson.put(OrderStudentJSONConst.ORDER_DATE, student.getPrevOrderDateSign());

                    managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(), firstDate, secondDate, student.getGroupname(),
                                                               additionalJson.toString()
                    );
                }
            }
        }

        return idOrder;
    }

    public Long createTransferProlongation (OrderRuleModel rule, Date firstDate, Date secondDate, String description, Long idSemester,
                                            List<GroupModel> groups) {
        List<OrderSectionModel> listSections = managerCreate.getListOrderSection(rule.getId());

        String groupsStr = "";

        for (GroupModel group : groups) {
            groupsStr += ("'" + group.getGroupname() + "',");
        }

        if (groupsStr.length() > 0) {
            groupsStr = groupsStr.substring(0, groupsStr.length() - 1);
        }

        List<StudentToCreateModel> listStudents = managerCreate.getStudentsForTransferProlongation(idSemester, groupsStr);

        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        Long idOrder = managerCreate.createEmptyOrder(rule.getId(), new Date(), idSemester, idHumanface, description);

        for (OrderSectionModel item : listSections) {
            Long idLOS = managerCreate.createLinkOrderSection(idOrder, item.getId(), item.getFoundation(), firstDate, secondDate);

            for (StudentToCreateModel student : listStudents) {
                if (student.getPrevOrderTransferTo() != null && student.getPrevOrderTransferTo().after(new Date())) {
                    continue;
                }
                if (student.getPrevOrderTransferTo() == null && student.getPrevOrderTransferToProl() != null &&
                    student.getPrevOrderTransferToProl().after(new Date())) {
                    continue;
                }

                if ((item.getName().endsWith("(Бюджет)") && student.getGovernmentFinanced()) ||
                    (item.getName().endsWith("(Договор)") && !student.getGovernmentFinanced())) {
                    JSONObject additionalJson = new JSONObject();
                    additionalJson.put(OrderStudentJSONConst.ORDER_NUMBER, student.getPrevOrderNumber());
                    additionalJson.put(OrderStudentJSONConst.ORDER_DATE, student.getPrevOrderDateSign());

                    managerCreate.createLinkOrderStudentStatus(idLOS, student.getId(), firstDate, secondDate, student.getGroupname(),
                                                               additionalJson.toString()
                    );
                }
            }
        }

        return idOrder;
    }
}
