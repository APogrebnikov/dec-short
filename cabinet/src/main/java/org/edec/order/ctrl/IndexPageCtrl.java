package org.edec.order.ctrl;

import org.edec.order.ctrl.delegate.IndexPageCtrlDelegate;
import org.edec.order.ctrl.renderer.OrderRenderer;
import org.edec.order.model.OrderStatusModel;
import org.edec.order.model.OrderTypeModel;
import org.edec.order.service.OrderService;
import org.edec.order.service.impl.OrderServiceESOimpl;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IndexPageCtrl extends CabinetSelector {
    @Wire
    private Combobox cmbInst, cmbType, cmbStatus, cmbFormOfStudy;

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
    private OrderService orderService = new OrderServiceESOimpl();

    protected void fill () {
        componentService.fillCmbInst(cmbInst, vbInst, currentModule.getDepartments());
        componentService.fillCmbFormOfStudy(cmbFormOfStudy, vbFormOfStudy, currentModule.getFormofstudy());
        fillCmbStatus();
        fillCmbType();
        if (currentModule.isReadonly()) {
            gbCreateOrder.setVisible(false);
            lhrEdit.setVisible(false);
            lhrDateCreate.setVisible(false);
            lhrDateSign.setVisible(false);
            lhrDelete.setVisible(false);
        } else {
            lhrDateFinish.setVisible(false);
        }
        lbOrder.setItemRenderer(new OrderRenderer(currentModule.isReadonly()));
        callLater();
    }

    private void fillCmbStatus () {
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

    private void fillCmbType () {
        List<OrderTypeModel> types = new ArrayList<>();
        types.add(new OrderTypeModel(null, "Все"));
        types.addAll(orderService.getDistinctType());
        cmbType.setModel(new ListModelList<>(types));
        cmbType.setItemRenderer((ComboitemRenderer<OrderTypeModel>) (ci, data, index) -> {
            ci.setValue(data);
            ci.setLabel(data.getName());
        });
        cmbType.addEventListener("onAfterRender", event -> cmbType.setSelectedIndex(0));
    }

    @Listen("onClick = #btnCreateNewOrder")
    public void createOrder () {
        FormOfStudy selectedFos = cmbFormOfStudy.getSelectedItem().getValue();
        InstituteModel selectedInst = cmbInst.getSelectedItem().getValue();
        if (selectedFos.getType() == 3 || selectedInst.getIdInst() == null) {
            PopupUtil.showWarning("Выберите форму контроля и институт!");
            return;
        }

        Map<String, Object> arg = new HashMap<>();
        arg.put(WinCreateOrderCtrl.DELEGATE, new IndexPageCtrlDelegate(this));
        arg.put(WinCreateOrderCtrl.INSTITUTE_MODEL, selectedInst);
        arg.put(WinCreateOrderCtrl.FORM_OF_STUDY, selectedFos);
        ComponentHelper.createWindow("/order/winCreateOrder.zul", "winCreateOrder", arg).doModal();
    }

    @Listen("onChange = #cmbInst; onChange = #cmbStatus; onChange = #cmbFormOfStudy; " +
            "onChange = #cmbType; onClick = #tbFio; onOK=#tbFio;")
    public void search () {
        callLater();
    }

    @Listen("onLater = #lbOrder")
    public void laterOnLbOrder () {
        refreshData();
        Clients.clearBusy(lbOrder);
    }

    public void refreshData () {
        lbOrder.setModel(new ListModelList<>(orderService.getOrderArchiveByFilter(
                ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInst(),
                ((FormOfStudy) cmbFormOfStudy.getSelectedItem().getValue()).getType(),
                ((OrderStatusModel) cmbStatus.getSelectedItem().getValue()).getIdStatus(),
                ((OrderTypeModel) cmbType.getSelectedItem().getValue()).getIdType(), tbFio.getValue()
        )));
        lbOrder.renderAll();
    }

    private void callLater () {
        Clients.showBusy(lbOrder, "Загрузка данных");
        Events.echoEvent("onLater", lbOrder, null);
    }
}
