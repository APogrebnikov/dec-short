package org.edec.signEditor.service.impl;

import org.edec.newOrder.model.createOrder.OrderCreateTypeModel;
import org.edec.signEditor.manager.SignatoryEditorManager;
import org.edec.signEditor.model.OrderRuleModel;
import org.edec.signEditor.model.OrderSectionModel;
import org.edec.signEditor.service.OrderRuleService;

import java.util.List;

public class OrderRuleServiceImpl implements OrderRuleService {

    private SignatoryEditorManager manager = new SignatoryEditorManager();

    @Override
    public List<OrderRuleModel> getListOrderRule() {
        return manager.getRuleModel();
    }

    @Override
    public List<OrderCreateTypeModel> getListOrderType() {
        return manager.getListOrderType();
    }

    @Override
    public void createRuleModel(String name, String description, String headDescription, Long idOrderType,
                                Long idInstitute, Long formOfControl, boolean isAutomatic) {
        manager.createRuleModel(name, description, headDescription, idOrderType, idInstitute, formOfControl, isAutomatic);
    }

    @Override
    public void updateOrderRule(Long idOrderRule, String name, String description, String headDescription,
                                Long idOrderType, Long idInstitute, Long formOfControl, boolean isAutomatic) {
        manager.updateOrderRule(idOrderRule, name, description, headDescription, idOrderType, idInstitute, formOfControl, isAutomatic);
    }

    @Override
    public void deleteOrderRule(Long idOrderRule) {
        manager.deleteOrderRule(idOrderRule);
    }

    @Override
    public List<OrderSectionModel> getOrderSection(Long idOrderRule) {
        return manager.getOrderSection(idOrderRule);
    }

    @Override
    public void createOrderSection(String description, Integer layout, String name, String foundation, Long otherdbid, Long idOrderRule) {
        manager.createOrderSection(description, layout, name, foundation, otherdbid, idOrderRule);
    }

    @Override
    public void updateOrderSection(Long idOrderSection, String description, Integer layout, String name, String foundation, Long otherdbid) {
        manager.updateOrderSection(idOrderSection, description, layout, name, foundation, otherdbid);
    }

    @Override
    public void deleteOrderSection(Long idOrderSection) {
        manager.deleteOrderSection(idOrderSection);
    }
}
