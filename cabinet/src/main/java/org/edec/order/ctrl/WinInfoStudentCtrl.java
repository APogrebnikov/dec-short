package org.edec.order.ctrl;

import org.edec.order.model.OrderModel;
import org.edec.order.model.StudentModel;
import org.edec.order.service.impl.EditOrderServiceESO;
import org.edec.studentPassport.ctrl.renderer.RatingRenderer;
import org.edec.utility.constants.OrderTypeConst;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;


public class WinInfoStudentCtrl extends SelectorComposer<Component> {
    public final static String STUDENT = "student";
    public final static String ORDER = "order";

    @Wire
    private Listbox lbEduPerformance;

    @Wire
    private Checkbox chDebt;

    @Wire
    private Window winInfoStudent;

    private EditOrderServiceESO serviceESO = new EditOrderServiceESO();
    private StudentModel student;
    private OrderModel order;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        student = (StudentModel) Executions.getCurrent().getArg().get(STUDENT);
        order = (OrderModel) Executions.getCurrent().getArg().get(ORDER);

        if (order.getOrderType().equals(OrderTypeConst.TRANSFER.getType())) {
            chDebt.setChecked(true);
        }

        winInfoStudent.setTitle(student.getFio());

        Events.echoEvent("onLater", lbEduPerformance, null);
    }

    @Listen("onCheck = #chDebt")
    public void checkedDebt () {
        Clients.showBusy(lbEduPerformance, "Загрузка данных");
        Events.echoEvent("onLater", lbEduPerformance, null);
    }

    @Listen("onLater = #lbEduPerformance")
    public void laterOnLbEduPerformance () {
        lbEduPerformance.setItemRenderer(new RatingRenderer());
        lbEduPerformance.setModel(new ListModelList<>(serviceESO.getMarksStudent(student.getId(), chDebt.isChecked())));
        lbEduPerformance.renderAll();
        Clients.clearBusy(lbEduPerformance);
    }
}
