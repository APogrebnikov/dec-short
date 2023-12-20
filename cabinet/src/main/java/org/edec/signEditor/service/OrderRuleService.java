package org.edec.signEditor.service;

import org.edec.newOrder.model.createOrder.OrderCreateTypeModel;
import org.edec.signEditor.model.OrderRuleModel;
import org.edec.signEditor.model.OrderSectionModel;

import java.util.List;

public interface OrderRuleService {

    List<OrderRuleModel> getListOrderRule();
    List<OrderCreateTypeModel> getListOrderType();

    void createRuleModel(String name, String description, String headDescription, Long idOrderType,
                                         Long idInstitute, Long formOfControl, boolean isAutomatic);

    void updateOrderRule(Long idOrderRule, String name, String description, String headDescription, Long idOrderType,
                                         Long idInstitute, Long formOfControl, boolean isAutomatic);

    void deleteOrderRule(Long idOrderRule);

    List<OrderSectionModel> getOrderSection(Long idOrderRule);

    void createOrderSection(String description, Integer layout, String name, String foundation,Long otherdbid, Long idOrderRule);

    void updateOrderSection(Long idOrderSection, String description, Integer layout, String name, String foundation,Long otherdbid);

    void deleteOrderSection(Long idOrderSection);

}
