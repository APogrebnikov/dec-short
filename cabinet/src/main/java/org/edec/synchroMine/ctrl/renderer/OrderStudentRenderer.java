package org.edec.synchroMine.ctrl.renderer;

import org.edec.synchroMine.model.mine.OrderActionStudent;
import org.edec.synchroMine.service.OrderService;
import org.edec.synchroMine.service.impl.OrderMineImpl;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.lang.reflect.Field;


public class OrderStudentRenderer implements ListitemRenderer<OrderActionStudent> {
    private ComponentService componentService = new ComponentServiceESOimpl();
    private OrderService orderService = new OrderMineImpl();

    @Override
    public void render (Listitem li, final OrderActionStudent data, int index) throws Exception {
        li.setValue(data);
        componentService.createListcell(li, String.valueOf(index + 1), "", "", "");
        componentService.createListcell(li, data.getFio(), "", "", "");

        Listcell lcStudent = new Listcell();
        lcStudent.setParent(li);
        final Longbox longStudent = new Longbox(data.getIdStudent());
        longStudent.setParent(lcStudent);
        longStudent.setInplace(true);
        longStudent.setStyle("width: 100%;");

        Listcell listcellGroupFrom = new Listcell(data.getIdGroupFrom().toString());
        listcellGroupFrom.setParent(li);
        Popup popup = new Popup();
        popup.setParent(listcellGroupFrom);
        popup.setId("popup" + data.getIdStudent() + "uid" + li.getUuid());
        li.setPopup("popup" + data.getIdStudent() + "uid" + li.getUuid());

        Vbox vbPopup = new Vbox();
        vbPopup.setParent(popup);
        for (Field field : data.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(data);
            if (value != null) {
                new Label(field.getName() + ": " + value).setParent(vbPopup);
            }
        }

        Listcell lcFoundation = new Listcell();
        lcFoundation.setParent(li);
        final Textbox tbFoundation = new Textbox(data.getFoundation());
        tbFoundation.setParent(lcFoundation);
        tbFoundation.setInplace(true);
        tbFoundation.setTooltiptext(data.getFoundation());
        tbFoundation.setStyle("width: 100%;");

        Listcell lcStip = new Listcell();
        lcStip.setParent(li);
        final Textbox tbStip = new Textbox(data.getScholarship());
        tbStip.setParent(lcStip);
        tbStip.setInplace(true);
        tbStip.setTooltiptext(data.getScholarship());
        tbStip.setStyle("width: 100%;");

        Listcell lcSumm = new Listcell();
        lcSumm.setParent(li);
        final Textbox tbSumm = new Textbox(data.getSumm());
        tbSumm.setParent(lcSumm);
        tbSumm.setInplace(true);

        Listcell lcAttr1 = new Listcell();
        lcAttr1.setParent(li);
        final Textbox tbAttr1 = new Textbox(data.getAttr1());
        tbAttr1.setParent(lcAttr1);
        tbAttr1.setInplace(true);
        tbAttr1.setTooltiptext(data.getAttr1());
        tbAttr1.setStyle("width: 100%;");

        Listcell lcBtn = new Listcell("", "/imgs/okCLR.png");
        lcBtn.setParent(li);
        lcBtn.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent (Event event) throws Exception {
                data.setIdStudent(longStudent.getValue());
                data.setFoundation(tbFoundation.getValue());
                data.setScholarship(tbStip.getValue());
                data.setSumm(tbSumm.getValue());
                data.setAttr1(tbAttr1.getValue());
                orderService.updateOrderActionStudent(data);
            }
        });
    }
}
