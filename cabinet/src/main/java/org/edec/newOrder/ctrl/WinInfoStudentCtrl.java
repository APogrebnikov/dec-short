package org.edec.newOrder.ctrl;

import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.service.esoImpl.EditOrderService;
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

/**
 * Created by antonskripacev on 02.01.17.
 */
public class WinInfoStudentCtrl extends SelectorComposer<Component> {
    public final static String STUDENT = "student";
    public final static String ORDER = "order";

    @Wire
    private Listbox lbEduPerformance;

    @Wire
    private Checkbox chDebt;

    @Wire
    private Window winInfoStudent;

    private EditOrderService serviceESO = new EditOrderService();
    private StudentModel student;
    private OrderEditModel order;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        student = (StudentModel) Executions.getCurrent().getArg().get(STUDENT);
        order = (OrderEditModel) Executions.getCurrent().getArg().get(ORDER);

        // TODO настройка кнопки "Показывать только долги"

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
