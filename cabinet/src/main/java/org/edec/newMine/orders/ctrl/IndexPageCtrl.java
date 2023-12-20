package org.edec.newMine.orders.ctrl;

import org.edec.newMine.orders.ctrl.renderer.OrdersRenderer;
import org.edec.newMine.orders.model.OrdersModel;
import org.edec.newMine.orders.service.OrderService;
import org.edec.newMine.orders.service.impl.OrdersServiceImpl;
import org.edec.utility.DateUtility;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class IndexPageCtrl extends CabinetSelector {
    @Wire
    private Listbox lbMineOrders;
    @Wire
    private Textbox tbOrdersNumber, tbOrdersType, tbOrdersDescription;
    @Wire
    private Datebox dbDateCreate, dbDateSign, dbOrdersFrom;

    private OrderService service = new OrdersServiceImpl();
    List<OrdersModel> listOrdersMine =  new ArrayList<>();


    @Override
    protected void fill() throws InterruptedException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, -1);

        dbOrdersFrom.setValue(cal.getTime());
        fillOrdersListbox(cal.getTime());
    }

    @Listen("onChange= #dbOrdersFrom" )
    public void findOrders() {
        fillOrdersListbox(dbOrdersFrom.getValue());
    }

    public void fillOrdersListbox(Date dateCreateOrdersFrom) {
        listOrdersMine = service.getMineOrder(dateCreateOrdersFrom);
        lbMineOrders.setModel(new ListModelList<>(listOrdersMine));
        lbMineOrders.setItemRenderer(new OrdersRenderer());
        lbMineOrders.renderAll();
    }

    @Listen("onOK = #tbOrdersNumber; onOK = #tbOrdersDescription; onChange = #dbDateCreate; onChange = #dbDateSign")
    public void filterOrders() {
        List<OrdersModel> tmpListOrdersMine =  new ArrayList<>(listOrdersMine);
        if (tbOrdersNumber.getValue() != null) {
            tmpListOrdersMine = tmpListOrdersMine.stream().filter(order -> order.getOrdersNumber().toLowerCase().contains(tbOrdersNumber.getValue().toLowerCase())).collect(Collectors.toList());
        }
        if (tbOrdersDescription.getValue() != null) {
            tmpListOrdersMine = tmpListOrdersMine.stream().filter(order -> order.getOrdersDescription().toLowerCase().contains(tbOrdersDescription.getValue().toLowerCase())).collect(Collectors.toList());
        }
        if (dbDateCreate.getValue() != null) {
            tmpListOrdersMine = tmpListOrdersMine.stream().filter(order -> DateUtility.isSameDay(order.getDateCreate(),dbDateCreate.getValue())).collect(Collectors.toList());
        }
        if (dbDateSign.getValue() != null) {
            tmpListOrdersMine = tmpListOrdersMine.stream().filter(order ->  DateUtility.isSameDay(order.getDateSign(), dbDateSign.getValue())).collect(Collectors.toList());
        }
        lbMineOrders.setModel(new ListModelList<>(tmpListOrdersMine));
        lbMineOrders.setItemRenderer(new OrdersRenderer());
        lbMineOrders.renderAll();
    }


}
