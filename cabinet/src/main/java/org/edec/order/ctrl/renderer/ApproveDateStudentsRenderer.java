package org.edec.order.ctrl.renderer;

import org.edec.order.model.OrderModel;
import org.edec.order.model.StudentToAddModel;
import org.edec.utility.constants.OrderTypeConst;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.Date;
import java.util.List;


public class ApproveDateStudentsRenderer implements ListitemRenderer<StudentToAddModel> {
    OrderModel order;

    public ApproveDateStudentsRenderer (OrderModel order) {
        this.order = order;
    }

    @Override
    public void render (Listitem listitem, final StudentToAddModel studentToAddModel, int i) throws Exception {
        Listcell lcFio = new Listcell(studentToAddModel.getFio());

        Listcell lcFirstDate = new Listcell();
        final Datebox firstDatebox = new Datebox(studentToAddModel.getFirstDate());
        firstDatebox.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
            @Override
            public void onEvent (Event event) throws Exception {
                studentToAddModel.setFirstDate(firstDatebox.getValue());
            }
        });
        lcFirstDate.appendChild(firstDatebox);

        Listcell lcSecondDate = new Listcell();
        final Datebox secondDatebox = new Datebox(studentToAddModel.getSecondDate());
        secondDatebox.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
            @Override
            public void onEvent (Event event) throws Exception {
                studentToAddModel.setSecondDate(secondDatebox.getValue());
            }
        });
        lcSecondDate.appendChild(secondDatebox);

        listitem.appendChild(lcFio);
        listitem.appendChild(lcFirstDate);
        listitem.appendChild(lcSecondDate);

        renderHeaders(listitem);
    }

    private void renderHeaders (Listitem listitem) {
        List<Listheader> listHeaders = listitem.getListbox().getListhead().getChildren();

        switch (OrderTypeConst.getByType(order.getOrderType())) {
            case ACADEMIC:
                ((Label) listHeaders.get(1).getFirstChild()).setValue("Дата назначения");
                ((Label) listHeaders.get(2).getFirstChild()).setValue("Дата окончания");
                break;
            case SOCIAL:
                ((Label) listHeaders.get(1).getFirstChild()).setValue("Дата назначения");
                ((Label) listHeaders.get(2).getFirstChild()).setValue("Дата окончания");
                break;
            case SOCIAL_INCREASED:
                ((Label) listHeaders.get(1).getFirstChild()).setValue("Дата назначения");
                ((Label) listHeaders.get(2).getFirstChild()).setValue("Дата окончания");
                break;
            case DEDUCTION:
                ((Label) listHeaders.get(1).getFirstChild()).setValue("Дата отчисления");
                ((Label) listHeaders.get(2).getFirstChild()).setValue("Дата прекращения выплат");
                break;
            case TRANSFER:
                ((Label) listHeaders.get(1).getFirstChild()).setValue("Перевести с");
                ((Label) listHeaders.get(2).getFirstChild()).setValue("Дата ликвидации по");
            default:
                break;
        }
    }
}
