package org.edec.newOrder.ctrl;

import org.edec.newOrder.ctrl.renderer.OrderMainRenderer;
import org.edec.newOrder.model.*;
import org.edec.newOrder.service.esoImpl.OrderMainService;
import org.edec.newOrder.service.orderCreator.other.MaterialSupportOrderService;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.*;

/**
 * Created by dmmax
 */
public class IndexPageCtrl extends CabinetSelector {
    @Wire
    private Combobox cmbInst, cmbType, cmbStatus, cmbFormOfStudy, cmbTypeOrder;

    @Wire
    private Groupbox gbCreateOrder;

    @Wire
    private Listbox lbOrder;

    @Wire
    private Listheader lhrEdit, lhrDateCreate, lhrDateSign, lhrDateFinish, lhrDelete;

    @Wire
    private Textbox tbFio;

    @Wire
    private Vbox vbInst, vbFormOfStudy;

    private ComponentService componentService = new ComponentServiceESOimpl();
    private OrderMainService orderService = new OrderMainService();

    private FormOfStudy currentFOS;
    private InstituteModel currentInstitute;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        fill();
    }

    protected void fill() {
        currentInstitute = componentService.fillCmbInst(cmbInst, vbInst, currentModule.getDepartments());
        currentFOS = componentService.fillCmbFormOfStudy(cmbFormOfStudy, vbFormOfStudy, currentModule.getFormofstudy());
        fillCmbStatus();
        fillCmbType();
        fillCmbRuleOrder();
        if (currentModule.isReadonly()) {
            gbCreateOrder.setVisible(false);
            lhrEdit.setVisible(false);
            lhrDateCreate.setVisible(false);
            lhrDateSign.setVisible(false);
            lhrDelete.setVisible(false);
        } else {
            lhrDateFinish.setVisible(false);
        }
        lbOrder.setItemRenderer(new OrderMainRenderer(currentModule.isReadonly()));
        callLater();
    }

    private void fillCmbStatus() {
        List<OrderStatusModel> statuses = new ArrayList<>();
        statuses.add(new OrderStatusModel(null, "Все"));
        statuses.addAll(orderService.getAllStatus());
        cmbStatus.setModel(new ListModelList<>(statuses));
        cmbStatus.setItemRenderer((ComboitemRenderer<OrderStatusModel>) (ci, data, index) -> {
            ci.setValue(data);
            ci.setLabel(data.getName());
        });
        cmbStatus.addEventListener("onAfterRender", event -> cmbStatus.setSelectedIndex(0));
    }

    private void fillCmbRuleOrder() {
        List<OrderRuleFilterModel> typesOrder = new ArrayList<>();
        typesOrder.add(new OrderRuleFilterModel(null, "Все"));
        typesOrder.addAll(orderService.getOrderRuleFilter());
        cmbTypeOrder.setModel(new ListModelList<>(typesOrder));
        cmbTypeOrder.setItemRenderer((ComboitemRenderer<OrderRuleFilterModel>) (ci, data, index) -> {
            ci.setLabel(data.getName());
            ci.setValue(data);
        });
        cmbTypeOrder.addEventListener("onAfterRender", event -> cmbTypeOrder.setSelectedIndex(0));
    }

    private void fillCmbType() {
        List<OrderTypeModel> types = new ArrayList<>();
        types.add(new OrderTypeModel(null, "Все"));
        types.addAll(orderService.getOrderTypeList());
        cmbType.setModel(new ListModelList<>(types));
        cmbType.setItemRenderer((ComboitemRenderer<OrderTypeModel>) (ci, data, index) -> {
            ci.setValue(data);
            ci.setLabel(data.getName());
        });
        cmbType.addEventListener("onAfterRender", event -> cmbType.setSelectedIndex(0));
    }

    @Listen("onClick = #btnCreateNewOrder")
    public void createOrder() {
        Map arg = new HashMap();
        arg.put(WinCreateOrderCtrl.UPDATE_UI, (Runnable) this::refreshData);
        ComponentHelper.createWindow("/newOrder/winCreateOrder.zul", "winCreateOrder", arg).doModal();
    }

    @Listen("onClick = #btnTest")
    public void Test() {
        MaterialSupportOrderService ms = new MaterialSupportOrderService();
        ms.manualGenerate();
    }

    @Listen("onChange = #cmbInst; onChange = #cmbStatus; onChange = #cmbFormOfStudy; " +
            "onChange = #cmbType; onChange = #cmbTypeOrder; onChange = #tbFio; onOK=#tbFio;")
    public void search() {
        callLater();
    }

    @Listen("onLater = #lbOrder")
    public void laterOnLbOrder() {
        refreshData();
        Clients.clearBusy(lbOrder);
    }

    public void refreshData() {
        lbOrder.setModel(new ListModelList<>(orderService.getOrderArchiveByFilter(
                ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInst(),
                ((FormOfStudy) cmbFormOfStudy.getSelectedItem().getValue()).getType(),
                ((OrderStatusModel) cmbStatus.getSelectedItem().getValue()).getIdStatus(),
                ((OrderTypeModel) cmbType.getSelectedItem().getValue()).getIdType(), tbFio.getValue(),
                ((OrderRuleFilterModel) cmbTypeOrder.getSelectedItem().getValue()).getIdOrderRule()
        )));
        lbOrder.renderAll();
    }

    private void callLater() {
        Clients.showBusy(lbOrder, "Загрузка данных");
        Events.echoEvent("onLater", lbOrder, null);
    }
}
