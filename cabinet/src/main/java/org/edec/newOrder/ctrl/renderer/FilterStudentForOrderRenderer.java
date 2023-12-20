package org.edec.newOrder.ctrl.renderer;

import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class FilterStudentForOrderRenderer implements ListitemRenderer<OrderCreateStudentModel> {
    @Override
    public void render(Listitem li, OrderCreateStudentModel data, int index) throws Exception {


        Listcell fio  = new Listcell(data.getFio());
       Listcell groupname = new Listcell(data.getGroupname());

        li.appendChild(fio);
        li.appendChild(groupname);
        li.setValue(data);

    }
}

