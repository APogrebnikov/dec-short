package org.edec.newOrder.service;

import org.edec.newOrder.model.createOrder.OrderCreateRuleModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.createOrder.OrderCreateTypeModel;
import org.edec.newOrder.model.createOrder.OrderSectionModel;

import java.util.List;


public interface CreateOrderService {
    List<OrderCreateTypeModel> getOrderTypes();
    List<OrderCreateRuleModel> getOrderRulesByInstituteAndType (long idInstitute, Long type);
    Long getCurrentSemester (long institute, int formOfControl);
    List<OrderCreateStudentModel> searchStudentsForOrderCreation (Long semester,Long nextSemester, String fio);
    List<OrderSectionModel> getSection(long idOrderRule);
}
