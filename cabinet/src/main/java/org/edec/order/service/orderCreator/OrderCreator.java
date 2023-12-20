package org.edec.order.service.orderCreator;

import org.edec.model.GroupModel;
import org.edec.order.model.OrderRuleModel;
import org.edec.order.model.StudentToAddModel;
import org.edec.order.model.dao.OrderImportModel;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.constants.OrderTypeConst;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class OrderCreator {
    public static final String FIRST_DATE = "fDate";
    public static final String SECOND_DATE = "sDate";
    public static final String THIRD_DATE = "thDate";
    public static final String ORDER_RULE = "orRu";
    public static final String DESCRIPTION = "desc";
    public static final String ID_SEMESTER = "idSem";
    public static final String IN_SESSION = "inSession";
    public static final String AFTER_PASS_WEEK = "afterPassWeek";
    public static final String ID_INSTITUTE = "idInst";
    public static final String LIST_STUDENTS_TO_ADD = "listStudents";
    public static final String GROUPS = "groups";

    public Long executeCreate (HashMap<String, Object> args) throws UnsupportedOperationException {
        OrderRuleModel rule = (OrderRuleModel) args.get(ORDER_RULE);

        switch (OrderTypeConst.getByType(rule.getIdOrderType())) {
            case ACADEMIC:
                if (OrderRuleConst.getById(rule.getId()).equals(OrderRuleConst.ACADEMIC_IN_SESSION)) {
                    return new AcademicalCreator().createAcademicalOrderInSession(rule, (Date) args.get(FIRST_DATE),
                                                                                  (Date) args.get(SECOND_DATE),
                                                                                  (String) args.get(DESCRIPTION),
                                                                                  (Long) args.get(ID_SEMESTER)
                    );
                } else if (OrderRuleConst.getById(rule.getId()).equals(OrderRuleConst.ACADEMIC_NOT_IN_SESSION)) {
                    return new AcademicalCreator().createAcademicalOrderNotInSession(rule, (Date) args.get(FIRST_DATE),
                                                                                     (Date) args.get(SECOND_DATE),
                                                                                     (Date) args.get(THIRD_DATE),
                                                                                     (String) args.get(DESCRIPTION),
                                                                                     (Long) args.get(ID_SEMESTER)
                    );
                } else {
                    throw new UnsupportedOperationException();
                }
            case SOCIAL:
                new SocialCreator();
                if (OrderRuleConst.getById(rule.getId()).equals(OrderRuleConst.SOCIAL_IN_SESSION)) {
                    return new SocialCreator().createSocialOrderInSession(rule, (Date) args.get(FIRST_DATE), (Date) args.get(SECOND_DATE),
                                                                          (String) args.get(DESCRIPTION), (Long) args.get(ID_SEMESTER)
                    );
                } else if (OrderRuleConst.getById(rule.getId()).equals(OrderRuleConst.SOCIAL_NEW_REFERENCE)) {
                    return new SocialCreator().createSocialOrderNewReference(rule, (Date) args.get(FIRST_DATE),
                                                                             (Date) args.get(SECOND_DATE), (String) args.get(DESCRIPTION),
                                                                             (Long) args.get(ID_SEMESTER), (Boolean) args.get(IN_SESSION)
                    );
                } else {
                    throw new UnsupportedOperationException();
                }
            case DEDUCTION:
                if (OrderRuleConst.getById(rule.getId()).equals(OrderRuleConst.DEDUCTION_INITIATIVE)) {
                    return new DeductionCreator().createDeductionStudentInitiative(rule, (ArrayList<StudentToAddModel>) args.get(
                            LIST_STUDENTS_TO_ADD), (Long) args.get(ID_SEMESTER));
                } else {
                    throw new UnsupportedOperationException();
                }
            case SOCIAL_INCREASED:
                if (OrderRuleConst.getById(rule.getId()).equals(OrderRuleConst.SOCIAL_INCREASED_IN_SESSION)) {
                    if ((boolean) args.get(IN_SESSION)) {
                        return new SocialIncreasedCreator().createSocialIncreasedOrderInSession(rule, (Date) args.get(FIRST_DATE),
                                                                                                (Date) args.get(SECOND_DATE),
                                                                                                (String) args.get(DESCRIPTION),
                                                                                                (Long) args.get(ID_SEMESTER)
                        );
                    } else {
                        return new SocialIncreasedCreator().createSocialIncreasedOrderNotInSession(rule, (Date) args.get(FIRST_DATE),
                                                                                                   (Date) args.get(SECOND_DATE),
                                                                                                   (String) args.get(DESCRIPTION),
                                                                                                   (Long) args.get(ID_SEMESTER)
                        );
                    }
                } else if (OrderRuleConst.getById(rule.getId()).equals(OrderRuleConst.SOCIAL_INCREASED_NEW_REFERENCE)) {
                    return new SocialIncreasedCreator().createSocialIncreasedOrderNewReference(rule, (Date) args.get(FIRST_DATE),
                                                                                               (Date) args.get(SECOND_DATE),
                                                                                               (String) args.get(DESCRIPTION),
                                                                                               (Long) args.get(ID_SEMESTER),
                                                                                               (Boolean) args.get(IN_SESSION)
                    );
                } else {
                    throw new UnsupportedOperationException();
                }
            case TRANSFER:
                if (OrderRuleConst.getById(rule.getId()).equals(OrderRuleConst.TRANSFER)) {
                    return new TransferCreator().createTransferOrder(rule, (Date) args.get(FIRST_DATE), (String) args.get(DESCRIPTION),
                                                                     (Long) args.get(ID_SEMESTER), (List<GroupModel>) args.get(GROUPS)
                    );
                } else if (OrderRuleConst.getById(rule.getId()).equals(OrderRuleConst.TRANSFER_CONDITIONALLY)) {
                    return new TransferCreator().createTransferConditionallyOrder(rule, (Date) args.get(FIRST_DATE),
                                                                                  (Date) args.get(SECOND_DATE),
                                                                                  (String) args.get(DESCRIPTION),
                                                                                  (Long) args.get(ID_SEMESTER),
                                                                                  (List<GroupModel>) args.get(GROUPS)
                    );
                } else if (OrderRuleConst.getById(rule.getId()).equals(OrderRuleConst.TRANSFER_AFTER_TRANSFER_CONDITIONALLY_RESPECTFUL)) {
                    return new TransferCreator().createTransferAfterTransferConditionally(rule, (Date) args.get(FIRST_DATE),
                                                                                          (Date) args.get(SECOND_DATE),
                                                                                          (String) args.get(DESCRIPTION),
                                                                                          (Long) args.get(ID_SEMESTER),
                                                                                          (List<GroupModel>) args.get(GROUPS), true
                    );
                } else if (OrderRuleConst.getById(rule.getId())
                                         .equals(OrderRuleConst.TRANSFER_AFTER_TRANSFER_CONDITIONALLY_NOT_RESPECTFUL)) {
                    return new TransferCreator().createTransferAfterTransferConditionally(rule, (Date) args.get(FIRST_DATE),
                                                                                          (Date) args.get(SECOND_DATE),
                                                                                          (String) args.get(DESCRIPTION),
                                                                                          (Long) args.get(ID_SEMESTER),
                                                                                          (List<GroupModel>) args.get(GROUPS), false
                    );
                } else if (OrderRuleConst.getById(rule.getId()).equals(OrderRuleConst.TRANSFER_PROLONGATION)) {
                    return new TransferCreator().createTransferProlongation(rule, (Date) args.get(FIRST_DATE), (Date) args.get(SECOND_DATE),
                                                                            (String) args.get(DESCRIPTION), (Long) args.get(ID_SEMESTER),
                                                                            (List<GroupModel>) args.get(GROUPS)
                    );
                } else {
                    throw new UnsupportedOperationException();
                }
            case SET_ELIMINATION_DEBTS:
                if (OrderRuleConst.getById(rule.getId()).equals(OrderRuleConst.SET_ELIMINATION_RESPECTFUL)) {
                    return new SetEliminationDebtsCreator().createEliminationRespectful(rule, (ArrayList<StudentToAddModel>) args.get(
                            LIST_STUDENTS_TO_ADD), (Long) args.get(ID_SEMESTER));
                } else if (OrderRuleConst.getById(rule.getId()).equals(OrderRuleConst.SET_ELIMINATION_NOT_RESPECTFUL)) {
                    return new SetEliminationDebtsCreator().createEliminationNotRespectful(rule, (Date) args.get(FIRST_DATE),
                                                                                           (String) args.get(DESCRIPTION),
                                                                                           (Long) args.get(ID_SEMESTER),
                                                                                           (Boolean) args.get(AFTER_PASS_WEEK)
                    );
                } else {
                    throw new UnsupportedOperationException();
                }
            default:
                throw new UnsupportedOperationException();
        }
    }
}
