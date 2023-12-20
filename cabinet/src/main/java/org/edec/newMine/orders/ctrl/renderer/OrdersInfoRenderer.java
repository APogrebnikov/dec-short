package org.edec.newMine.orders.ctrl.renderer;

import org.edec.newMine.orders.model.OrderStudentsModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class OrdersInfoRenderer implements ListitemRenderer<OrderStudentsModel> {
    @Override
    public void render(Listitem li, OrderStudentsModel data, int i) throws Exception {
        li.setValue(data);
        li.appendChild(new Listcell(i+1 +". "+data.getFio()));
        li.appendChild(new Listcell(String.valueOf(data.getCourse())));
        li.appendChild(new Listcell(data.getGroupname()));
        li.appendChild(new Listcell(data.getRecordbook()));
        li.appendChild(new Listcell(data.getSumm()));

    }
}
