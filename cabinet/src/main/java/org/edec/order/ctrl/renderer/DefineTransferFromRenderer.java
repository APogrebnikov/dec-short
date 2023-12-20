package org.edec.order.ctrl.renderer;

import org.edec.order.model.OrderModel;
import org.edec.order.model.StudentModel;
import org.edec.utility.constants.OrderRuleConst;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;


public class DefineTransferFromRenderer implements ListitemRenderer<StudentModel> {
    private OrderModel order;

    public DefineTransferFromRenderer (OrderModel model) {
        super();
        this.order = model;
    }

    @Override
    public void render (Listitem listitem, final StudentModel studentModel, int i) throws Exception {
        if (i == 0) {
            if (order.getIdOrderRule().equals(OrderRuleConst.TRANSFER_PROLONGATION.getId())) {
                ((Label) listitem.getListbox().getListhead().getChildren().get(2).getChildren().get(0)).setValue("Дата ликвидации");
            }
        }

        listitem.setValue(studentModel);
        Listcell cellFio = new Listcell(studentModel.getFio());
        cellFio.setParent(listitem);

        Listcell cellGroup = new Listcell(studentModel.getGroupname());
        cellGroup.setParent(listitem);

        final Listcell cellDateTransfer = new Listcell();
        final Datebox dbTransfer = new Datebox();
        dbTransfer.setValue(studentModel.getFirstDate());
        dbTransfer.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
            @Override
            public void onEvent (Event event) throws Exception {
                studentModel.setFirstDate(dbTransfer.getValue());
            }
        });

        dbTransfer.setParent(cellDateTransfer);
        cellDateTransfer.setParent(listitem);
    }
}
