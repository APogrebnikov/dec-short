package org.edec.signEditor.ctrl;


import org.edec.signEditor.model.OrderSectionModel;
import org.edec.signEditor.service.OrderRuleService;
import org.edec.signEditor.service.impl.OrderRuleServiceImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

public class WinCreateSectionCtrl extends SelectorComposer<Component> {
    @Wire
    private Textbox tbDescriptionSection, tbNameSection, tbLayout, tbOtherdbId, tbFoundation;

    private Runnable updateTableOrderSection;
    private OrderSectionModel orderSectionModel;
    private Long idOrderRule;

    private OrderRuleService service = new OrderRuleServiceImpl();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        updateTableOrderSection = (Runnable) Executions.getCurrent().getArg().get("fillListOrderSection");
        orderSectionModel = (OrderSectionModel) Executions.getCurrent().getArg().get("section");
        idOrderRule = (Long) Executions.getCurrent().getArg().get("id");

        if (orderSectionModel != null) {
            tbDescriptionSection.setValue(orderSectionModel.getDescription());
            tbNameSection.setValue(orderSectionModel.getName());
            if (orderSectionModel.getFoundation() == null) {
                tbFoundation.setValue("");
            } else {
                tbFoundation.setValue(orderSectionModel.getFoundation());
            }

            tbLayout.setValue(orderSectionModel.getLayout().toString());

            if (orderSectionModel.getOtherdbid() == null) {
                tbOtherdbId.setValue("");
            } else {
                tbOtherdbId.setValue(orderSectionModel.getOtherdbid().toString());
            }
        }
    }

    @Listen("onClick = #btnCreateSection")
    public void createSection() {
        String description = tbDescriptionSection.getValue();
        Integer layout = Integer.parseInt(tbLayout.getValue());
        String name = tbNameSection.getValue();
        String foundation = tbFoundation.getValue();
        Long otherdbid = Long.parseLong(tbOtherdbId.getValue());

        if (orderSectionModel != null) {
            service.updateOrderSection(orderSectionModel.getIdOrderSection(), description, layout, name, foundation, otherdbid);
        } else {
            service.createOrderSection(description, layout, name, foundation, otherdbid, idOrderRule);
        }
        updateTableOrderSection.run();

        PopupUtil.showInfo("Секция была успешно сохранена!");
    }
}