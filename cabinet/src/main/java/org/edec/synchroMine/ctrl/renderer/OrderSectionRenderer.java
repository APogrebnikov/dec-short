package org.edec.synchroMine.ctrl.renderer;

import org.edec.synchroMine.model.mine.OrderAction;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;


public class OrderSectionRenderer implements ListitemRenderer<OrderAction> {
    @Override
    public void render (Listitem li, OrderAction data, int index) throws Exception {
        li.setValue(data);
        new Listcell(String.valueOf(index + 1)).setParent(li);
        new Listcell(data.getText()).setParent(li);
        new Listcell(String.valueOf(data.getNumber())).setParent(li);
        new Listcell(String.valueOf(data.getIdAction())).setParent(li);
        new Listcell(data.isMass() ? "TRUE" : "FALSE").setParent(li);
    }
}
