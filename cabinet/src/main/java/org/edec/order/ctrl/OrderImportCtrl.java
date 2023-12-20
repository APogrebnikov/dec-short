package org.edec.order.ctrl;

import org.edec.order.model.OrderModel;
import org.edec.order.service.impl.ImportOrderService;
import org.edec.order.service.impl.OrderServiceESOimpl;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import java.util.List;
import java.util.stream.Collectors;

public class OrderImportCtrl extends SelectorComposer {

    @Wire
    private Listbox lbOrder;

    @Wire
    private Textbox tbSearchByNumber, tbSearchByDescription;

    private InstituteModel currentInstitute;
    private ComponentService componentService;

    private ImportOrderService importOrderService = new ImportOrderService();
    private OrderServiceESOimpl orderServiceESOimpl = new OrderServiceESOimpl();

    private List<OrderModel> listOrders, filteredListOrders;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        fill();
    }

    protected void fill () {
        //TODO заглушка для института
        listOrders = orderServiceESOimpl.getOrderArchiveByFilter(1L, 3, null, null, "");

        listOrders = listOrders.stream().filter(orderModel -> orderModel.getNumber() != null).collect(Collectors.toList());

        lbOrder.setItemRenderer((Listitem listitem, OrderModel orderModel, int i) -> {
            listitem.setValue(orderModel);

            new Listcell().setParent(listitem);
            new Listcell(orderModel.getNumber()).setParent(listitem);
            new Listcell(orderModel.getDescription()).setParent(listitem);
        });

        lbOrder.setModel(new ListModelList<>(listOrders));
    }

    @Listen("onClick = #btnImport")
    public void onClickBtnImport () {
        if (lbOrder.getSelectedIndex() == -1 || lbOrder.getSelectedItem().getValue() == null) {
            PopupUtil.showWarning("Сначала выберите приказ!");
            return;
        }

        OrderModel model = lbOrder.getSelectedItem().getValue();

        if(importOrderService.startTransfer(model)){
            PopupUtil.showInfo("Приказ был проведен успешно!");
        } else {
            PopupUtil.showError("Возникли проблемы с проведением приказа, попробуйте еще раз!");
        }

    }

    @Listen("onOK= #tbSearchByNumber; onOK = #tbSearchByDescription")
    public void filterValues(){

        filteredListOrders = listOrders.stream().collect(Collectors.toList());

        if(!tbSearchByNumber.getValue().equals("")){
            filteredListOrders = listOrders.stream()
                                           .filter(orderModel ->
                                                           orderModel.getNumber().toLowerCase().contains(tbSearchByNumber.getValue().toLowerCase()))
                                           .collect(Collectors.toList());
        }

        if(!tbSearchByDescription.getValue().equals("")){
            filteredListOrders = listOrders.stream().filter(orderModel ->
                                                                    orderModel.getDescription().toLowerCase().contains(tbSearchByDescription.getValue().toLowerCase()))
                                           .collect(Collectors.toList());
        }

        lbOrder.setModel(new ListModelList<>(filteredListOrders));
    }
}
