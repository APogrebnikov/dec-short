package org.edec.newOrder.service.orderCreator.concept;

import org.edec.newOrder.service.orderCreator.*;
import org.edec.newOrder.service.orderCreator.academic.AcademicFirstCourseOrderService;
import org.edec.newOrder.service.orderCreator.academic.AcademicInSessionOrderService;
import org.edec.newOrder.service.orderCreator.academic.AcademicIncreasedOrderService;
import org.edec.newOrder.service.orderCreator.academic.AcademicIndividualOrderService;
import org.edec.newOrder.service.orderCreator.academic.AcademicNotInSessionOrderService;
import org.edec.newOrder.service.orderCreator.academic.CancelAcademicalScholarshipByPracticeOrderService;
import org.edec.newOrder.service.orderCreator.academic.CancelAcademicalScholarshipInSessionOrderService;
import org.edec.newOrder.service.orderCreator.elimination.practice.SetFirstEliminationAfterPracticeOrderService;
import org.edec.newOrder.service.orderCreator.elimination.SetEliminationOrderService;
import org.edec.newOrder.service.orderCreator.elimination.SetEliminationSecondTimeOrderService;
import org.edec.newOrder.service.orderCreator.elimination.session.SetFirstEliminationOrderService;
import org.edec.newOrder.service.orderCreator.other.DeductionByComissionResultOrderService;
import org.edec.newOrder.service.orderCreator.other.DeductionInitiativeOrderService;
import org.edec.newOrder.service.orderCreator.other.MaterialSupportOrderService;
import org.edec.newOrder.service.orderCreator.other.ProlongationWinterSessionOrderService;
import org.edec.newOrder.service.orderCreator.social.CancelSocialIncreasedByPracticeOrderService;
import org.edec.newOrder.service.orderCreator.social.CancelSocialIncreasedInSessionOrderService;
import org.edec.newOrder.service.orderCreator.social.SocialInSessionOrderService;
import org.edec.newOrder.service.orderCreator.social.SocialIncreasedInSessionOrderService;
import org.edec.newOrder.service.orderCreator.social.SocialIncreasedNewReferenceOrderService;
import org.edec.newOrder.service.orderCreator.social.SocialNewReferenceOrderService;
import org.edec.newOrder.service.orderCreator.transfer.TransferAfterTransferConditionallyOrderService;
import org.edec.newOrder.service.orderCreator.transfer.TransferConditionallyOrderService;
import org.edec.newOrder.service.orderCreator.transfer.TransferConditionallyRespectfulService;
import org.edec.newOrder.service.orderCreator.transfer.TransferOrderService;
import org.edec.newOrder.service.orderCreator.transfer.TransferProlongationNotRespectfulOrderService;
import org.edec.newOrder.service.orderCreator.transfer.TransferProlongationRespectfulOrderService;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.zk.DialogUtil;

public class OrderServiceFactory {
    private OrderServiceFactory() {}

    public static OrderService getServiceByRule (Long idRule, FormOfStudy formOfStudy, Long idInstitute) {
        OrderService orderService = null;

        switch (OrderRuleConst.getById(idRule)) {
            case ACADEMIC_INCREASED:
                orderService = new AcademicIncreasedOrderService();
                break;
            case PROLONGATION_ELIMINATION_WINTER:
                orderService = new ProlongationWinterSessionOrderService();
                break;
            case MATERIAL_SUPPORT:
                orderService = new MaterialSupportOrderService();
                break;
            case TRANSFER_AFTER_TRANSFER_CONDITIONALLY_NOT_RESPECTFUL:
                orderService = new TransferAfterTransferConditionallyOrderService();
                break;
            case SET_ELIMINATION_RESPECTFUL:
                orderService = new SetEliminationOrderService(true);
                break;
            case SET_ELIMINATION_NOT_RESPECTFUL:
                orderService = new SetEliminationOrderService(false);
                break;
            case TRANSFER_PROLONGATION:
                //orderService = new TransferProlongationOrderService();
                break;
            case TRANSFER_CONDITIONALLY:
                orderService = new TransferConditionallyOrderService();
                break;
            case TRANSFER:
                orderService = new TransferOrderService();
                break;
            case SOCIAL_IN_SESSION:
                orderService = new SocialInSessionOrderService();
                break;
            case ACADEMIC_IN_SESSION:
                orderService = new AcademicInSessionOrderService();
                break;
            case DEDUCTION_INITIATIVE:
                orderService = new DeductionInitiativeOrderService(formOfStudy, idInstitute);
                break;
            case SOCIAL_NEW_REFERENCE:
                orderService = new SocialNewReferenceOrderService();
                break;
            case ACADEMIC_NOT_IN_SESSION:
                orderService = new AcademicNotInSessionOrderService();
                break;
            case SOCIAL_INCREASED_IN_SESSION:
                orderService = new SocialIncreasedInSessionOrderService();
                break;
            case SOCIAL_INCREASED_NEW_REFERENCE:
                orderService = new SocialIncreasedNewReferenceOrderService();
                break;
            case CANCEL_ACADEMICAL_SCHOLARSHIP_IN_SESSION:
                orderService = new CancelAcademicalScholarshipInSessionOrderService();
                break;
            case TRANSFER_CONDITIONALLY_RESPECTFUL:
                orderService = new TransferConditionallyRespectfulService();
                break;
            case CANCEL_SCHOLARSHIP_AFTER_PRACTICE:
                orderService = new CancelAcademicalScholarshipByPracticeOrderService();
                break;
            case SET_ELIMINATION_AFTER_TRANSFER_RESPECTFUL:
                orderService = new TransferProlongationRespectfulOrderService();
                break;
            case SET_ELIMINATION_AFTER_PRACTICE:
                orderService = new SetFirstEliminationAfterPracticeOrderService();
                break;
            case SET_ELIMINATION_AFTER_TRANSFER_NOT_RESPECTFUL:
                orderService = new TransferProlongationNotRespectfulOrderService();
                break;
            case DEDUCTION_BY_COMMISSION_RESULT:
                orderService = new DeductionByComissionResultOrderService();
                break;
            case SET_FIRST_ELIMINATION:
                orderService = new SetFirstEliminationOrderService();
                break;
            case SET_SECOND_ELIMINATION:
                orderService = new SetEliminationSecondTimeOrderService();
                break;
            case ACADEMIC_FIRST_COURSE:
                orderService = new AcademicFirstCourseOrderService();
                break;
            case CANCEL_SOCIAL_INCREASED_BY_PRACTICE:
                orderService = new CancelSocialIncreasedByPracticeOrderService();
                break;
            case CANCEL_SOCIAL_INCREASED_IN_SESSION:
                orderService = new CancelSocialIncreasedInSessionOrderService();
                break;
            case ACADEMIC_INDIVIDUAL:
                orderService = new AcademicIndividualOrderService();
                break;
            default:
                DialogUtil.error("Данный приказ еще не был введен в систему, обратитесь к администратору!");
                return orderService;
        }

        orderService.setOrderRuleConst(OrderRuleConst.getById(idRule));

        return orderService;
    }
}
