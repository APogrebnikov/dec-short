package org.edec.synchroMine.ctrl;

import org.edec.synchroMine.ctrl.renderer.OrderSectionRenderer;
import org.edec.synchroMine.ctrl.renderer.OrderStudentMoveRenderer;
import org.edec.synchroMine.ctrl.renderer.OrderStudentRenderer;
import org.edec.synchroMine.model.mine.Order;
import org.edec.synchroMine.service.OrderService;
import org.edec.synchroMine.service.impl.OrderMineImpl;
import org.edec.utility.component.model.InstituteModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Vbox;


public class WinOrderInfoCtrl extends SelectorComposer<Component> {
    public static final String ORDER = "order";
    public static final String INST = "inst";

    @Wire
    private Label lOrderInfo;

    @Wire
    private Listbox lbOrderSectionStudent, lbOrderStudentMove, lbOrderSection;

    @Wire
    private Vbox vbSectionOrder;

    private OrderService orderService = new OrderMineImpl();

    private Order order;
    private InstituteModel inst;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        order = (Order) Executions.getCurrent().getArg().get(ORDER);
        inst = (InstituteModel) Executions.getCurrent().getArg().get(INST);

        lOrderInfo.setValue("Приказ " + order.getId() + ", " + order.getNumber() + ", " + order.getDescription());
        lbOrderSectionStudent.setItemRenderer(new OrderStudentRenderer());
        lbOrderStudentMove.setItemRenderer(new OrderStudentMoveRenderer());
        lbOrderSection.setItemRenderer(new OrderSectionRenderer());

        Clients.showBusy(lbOrderSectionStudent, "Загрузка данных");
        Events.echoEvent("onLater", lbOrderSectionStudent, null);
        Clients.showBusy(lbOrderStudentMove, "Загрузка дананых");
        Events.echoEvent("onLater", lbOrderStudentMove, null);
        Clients.showBusy(lbOrderSection, "Загрузка дананых");
        Events.echoEvent("onLater", lbOrderSection, null);
    }

    @Listen("onLater = #lbOrderSectionStudent")
    public void onLaterlbOrderSectionStudent () {
        lbOrderSectionStudent.setModel(new ListModelList<>(orderService.getOrderStudents(order.getId())));
        lbOrderSectionStudent.renderAll();
        Clients.clearBusy(lbOrderSectionStudent);
    }

    @Listen("onLater = #lbOrderStudentMove")
    public void onLaterlbOrderStudentMove () {
        lbOrderStudentMove.setModel(new ListModelList<>(orderService.getStudentMove(order.getNumber(), inst.getIdInstMine())));
        lbOrderStudentMove.renderAll();
        Clients.clearBusy(lbOrderStudentMove);
    }

    @Listen("onLater = #lbOrderSection")
    public void onLaterSection () {
        lbOrderSection.setModel(new ListModelList<>(orderService.getOrderAction(order.getId())));
        lbOrderSection.renderAll();
        Clients.clearBusy(lbOrderSection);
    }
}
