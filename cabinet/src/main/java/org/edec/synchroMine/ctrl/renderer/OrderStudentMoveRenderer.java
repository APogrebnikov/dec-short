package org.edec.synchroMine.ctrl.renderer;

import org.edec.synchroMine.model.mine.StudentMove;
import org.edec.synchroMine.service.OrderService;
import org.edec.synchroMine.service.impl.OrderMineImpl;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.lang.reflect.Field;


public class OrderStudentMoveRenderer implements ListitemRenderer<StudentMove> {
    private OrderService orderService = new OrderMineImpl();

    @Override
    public void render (Listitem li, final StudentMove data, int index) throws Exception {
        li.setValue(data);

        new Listcell(String.valueOf(index + 1)).setParent(li);

        Listcell lcStudent = new Listcell();
        lcStudent.setParent(li);
        final Longbox longStudent = new Longbox(data.getIdStudent());
        longStudent.setParent(lcStudent);
        longStudent.setInplace(true);

        Listcell listcellGroupFrom = new Listcell(data.getGroupFrom());
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

        new Listcell(String.valueOf(data.getOrderType())).setParent(li);
        new Listcell(data.getReason()).setParent(li);

        Listcell lcDateFrom = new Listcell();
        lcDateFrom.setParent(li);
        final Datebox dbDateFrom = new Datebox(data.getDateFrom());
        dbDateFrom.setParent(lcDateFrom);
        dbDateFrom.setInplace(true);

        Listcell lcDateTo = new Listcell();
        lcDateTo.setParent(li);
        final Datebox dbDateTo = new Datebox(data.getDateTo());
        dbDateTo.setParent(lcDateTo);
        dbDateTo.setInplace(true);

        Listcell lcTypeMove = new Listcell();
        lcTypeMove.setParent(li);
        final Textbox tbTypeMove = new Textbox(data.getMoveType());
        tbTypeMove.setParent(lcTypeMove);
        tbTypeMove.setTooltiptext(data.getMoveType());
        tbTypeMove.setInplace(true);
        tbTypeMove.setStyle("width: 100%;");

        Listcell lcBtn = new Listcell("", "/imgs/okCLR.png");
        lcBtn.setParent(li);
        lcBtn.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent (Event event) throws Exception {
                data.setIdStudent(longStudent.getValue());
                data.setDateFrom(dbDateFrom.getValue());
                data.setDateTo(dbDateTo.getValue());
                data.setMoveType(tbTypeMove.getValue());
                orderService.updateStudentMove(data);
            }
        });
    }
}
