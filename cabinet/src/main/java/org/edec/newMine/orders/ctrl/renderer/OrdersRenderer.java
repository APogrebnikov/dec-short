package org.edec.newMine.orders.ctrl.renderer;

import org.edec.newMine.orders.ctrl.WinOrdersInfo;
import org.edec.newMine.orders.model.OrdersModel;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.HashMap;
import java.util.Map;

public class OrdersRenderer implements ListitemRenderer<OrdersModel> {
    @Override
    public void render(Listitem li, OrdersModel data, int i) throws Exception {
        li.setValue(data);
        new Listcell(i+1 + ".").setParent(li);
        new Listcell(data.getOrdersNumber()).setParent(li);
        new Listcell(DateConverter.convertDateToStringByFormat(data.getDateCreate(), "dd.MM.yyyy")).setParent(li);
        new Listcell(DateConverter.convertDateToStringByFormat(data.getDateSign(), "dd.MM.yyyy")).setParent(li);
        new Listcell(data.getOrdersDescription()).setParent(li);

        li.addEventListener(Events.ON_CLICK, event -> {
            Map<String, Object> arg = new HashMap<>();

            arg.put(WinOrdersInfo.ID_ORDER_MINE, data.getIdOrerMine());
            ComponentHelper.createWindow("/newMine/orders/winOrdersInfo.zul", "winOrdersInfo", arg).doModal();
        });
    }
}
