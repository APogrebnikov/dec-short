package org.edec.synchroMine.ctrl.renderer;

import org.edec.synchroMine.model.mine.Order;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.converter.DateConverter;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;


public class OrderRenderer implements ListitemRenderer<Order> {
    private ComponentService componentService = new ComponentServiceESOimpl();

    @Override
    public void render (Listitem li, Order data, int index) throws Exception {
        li.setValue(data);
        componentService.createListcell(li, String.valueOf(index + 1), "", "", "");
        componentService.createListcell(li, String.valueOf(data.getId()), "", "", "");
        componentService.createListcell(li, data.getNumber(), "", "", "");
        componentService.createListcell(li, DateConverter.convertDateToString(data.getDateCreated()), "", "", "");
        componentService.createListcell(li, DateConverter.convertDateToString(data.getDateSigned()), "", "", "");
        componentService.createListcell(li, data.getOrderType(), "", "", "");
        componentService.createListcell(li, data.getDescription(), "", "", "");
    }
}
