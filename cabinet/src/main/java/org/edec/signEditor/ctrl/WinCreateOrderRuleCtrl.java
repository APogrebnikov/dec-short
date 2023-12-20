package org.edec.signEditor.ctrl;

import org.edec.newOrder.model.createOrder.OrderCreateTypeModel;
import org.edec.signEditor.model.OrderRuleModel;
import org.edec.signEditor.service.OrderRuleService;
import org.edec.signEditor.service.impl.OrderRuleServiceImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import java.util.ArrayList;
import java.util.List;

public class WinCreateOrderRuleCtrl extends SelectorComposer<Component> {

    @Wire
    private Textbox tbNameOrderRule, tbDescription, tbHeadDescription;

    @Wire
    private Listbox lbFK, lbIsAuto;

    @Wire
    private Combobox cmbTypeRule;

    private Runnable updateTableOrderRule;
    private OrderRuleModel orderRuleModel;
    private OrderRuleService service = new OrderRuleServiceImpl();
    private int index = -1;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        updateTableOrderRule = (Runnable) Executions.getCurrent().getArg().get("fillListOrderRule");
        orderRuleModel = (OrderRuleModel) Executions.getCurrent().getArg().get("rule");

        List<OrderCreateTypeModel> typeList = service.getListOrderType();

        cmbTypeRule.setModel(new ListModelList<>(typeList));
        cmbTypeRule.setItemRenderer((ComboitemRenderer<OrderCreateTypeModel>) (comboitem, orderCreateTypeModel, i) -> {
            comboitem.setValue(orderCreateTypeModel);
            comboitem.setLabel(orderCreateTypeModel.getName());
        });

        if (orderRuleModel != null) {
            tbNameOrderRule.setValue(orderRuleModel.getName());
            tbDescription.setValue(orderRuleModel.getDescription());
            tbHeadDescription.setValue(orderRuleModel.getHeadDescription());

            for (OrderCreateTypeModel type : typeList) {
                index++;
                if (type.getId().equals(orderRuleModel.getIdOrderType())) {
                    return;
                }
            }

            if (orderRuleModel.getFormOfControl() == 1) {
                lbFK.setSelectedIndex(0);
            } else {
                lbFK.setSelectedIndex(1);
            }

            if (orderRuleModel.isAutomatic()) {
                lbIsAuto.setSelectedIndex(0);
            } else {
                lbIsAuto.setSelectedIndex(1);
            }
        }
    }

    @Listen("onAfterRender = #cmbTypeRule")
    public void updateTypeRule() {
        if (index != -1) {
            cmbTypeRule.setSelectedItem(cmbTypeRule.getItems().get(index));
        }
    }

    @Listen("onClick = #btnCreate")
    public void createOrderRule() {
        if (cmbTypeRule.getSelectedIndex() == -1) {
            PopupUtil.showWarning("Выберите тип приказа!");
            return;
        }

        String name = tbNameOrderRule.getValue();
        String description = tbDescription.getValue();
        String headDesc = tbHeadDescription.getValue();
        Long formCtrl = Long.parseLong(lbFK.getSelectedItem().getValue());
        Boolean isAuto = lbIsAuto.getSelectedIndex() == 0;
        Long type = ((OrderCreateTypeModel) cmbTypeRule.getSelectedItem().getValue()).getId();

        if (orderRuleModel != null) {
            service.updateOrderRule(orderRuleModel.getIdOrderRule(), name, description, headDesc, type, 1L, formCtrl, isAuto);
        } else {
            service.createRuleModel(name, description, headDesc, type, 1L, formCtrl, isAuto);
        }

        updateTableOrderRule.run();
        PopupUtil.showInfo("Правило было успешно сохранено!");
    }
}