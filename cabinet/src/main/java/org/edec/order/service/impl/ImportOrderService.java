package org.edec.order.service.impl;

import org.edec.order.manager.CreateOrderManagerESO;
import org.edec.order.manager.ImportOrderManager;
import org.edec.order.model.OrderModel;
import org.edec.order.model.dao.OrderImportModel;
import org.edec.utility.constants.OrderRuleConst;

import java.util.List;


public class ImportOrderService {
    private ImportOrderManager importOrderManager = new ImportOrderManager();
    private CreateOrderManagerESO createOrderManagerESO = new CreateOrderManagerESO();

    public boolean startTransfer (OrderModel model) {

        if (model.getIdOrderRule().equals(OrderRuleConst.ACADEMIC_IN_SESSION.getId())
            || model.getIdOrderRule().equals(OrderRuleConst.ACADEMIC_NOT_IN_SESSION.getId())) {
            return transferAcademicOrderToOrderActionTable(model.getIdOrder());
        }

        else if (model.getIdOrderRule().equals(OrderRuleConst.SET_ELIMINATION_RESPECTFUL.getId()) ||
            model.getIdOrderRule().equals(OrderRuleConst.SET_ELIMINATION_NOT_RESPECTFUL.getId())) {
            return transferSetEliminationOrderToOrderActionTable(model.getIdOrder());
        }

        else if (model.getIdOrderRule().equals(OrderRuleConst.SOCIAL_INCREASED_IN_SESSION.getId()) ||
                model.getIdOrderRule().equals(OrderRuleConst.SOCIAL_INCREASED_NEW_REFERENCE.getId())){
            return transferSocialOrderToOrderActionTable(model.getIdOrder());
        }

        return true;
    }

    private boolean transferAcademicOrderToOrderActionTable (Long idOrder) {
        List<OrderImportModel> listStudents = importOrderManager.getOrderDataById(idOrder);

        for (OrderImportModel student : listStudents) {
            if(!checkIfOrderWasAlreadyTransferred(student.getIdLOSS())){
                if(!importOrderManager.importStudentFromAcademOrder(student)) return false;
            }
        }

        return true;
    }

    private boolean transferSetEliminationOrderToOrderActionTable (Long idOrder) {
        List<OrderImportModel> listStudents = importOrderManager.getOrderDataById(idOrder);

        Long idSem = createOrderManagerESO.getNextSemester(listStudents.get(0).getIdSemester());

        for (OrderImportModel student : listStudents) {
            student.setIdSemester(idSem);

            if(!checkIfOrderWasAlreadyTransferred(student.getIdLOSS())) {
                if(!importOrderManager.importStudentFromSetEliminationOrder(student)) return false;
            }
        }

        return true;
    }

    private boolean transferSocialOrderToOrderActionTable(Long idOrder){
        List<OrderImportModel> listStudents = importOrderManager.getOrderDataById(idOrder);

        Long idSem = createOrderManagerESO.getNextSemester(listStudents.get(0).getIdSemester());

        for(OrderImportModel student : listStudents){
            student.setIdSemester(idSem);

            if(!checkIfOrderWasAlreadyTransferred(student.getIdLOSS())) {
                if(!importOrderManager.importStudentFromSocialOrder(student)) return false;
            }
        }

        return true;
    }

    private boolean checkIfOrderWasAlreadyTransferred(Long idLOSS){
        return importOrderManager.checkIfOrderWasAlreadyTransferred(idLOSS);
    }
}
