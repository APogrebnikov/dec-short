package org.edec.commission.ctrl.renderer;

import org.edec.commission.ctrl.IndexPageCtrl;
import org.edec.commission.ctrl.WinStudentDebtCtrl;
import org.edec.commission.model.StudentCountDebtModel;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmmax
 */
public class StudentCountDebtRenderer implements ListitemRenderer<StudentCountDebtModel> {
    private Object controller;

    public StudentCountDebtRenderer (Object controller) {
        this.controller = controller;
    }

    @Override
    public void render (final Listitem li, final StudentCountDebtModel data, int index) throws Exception {
        li.setValue(data);
        new Listcell(data.getFio()).setParent(li);
        new Listcell(data.getGroupname()).setParent(li);
        new Listcell(data.getDebt() + "").setParent(li);

        Listcell lcAction = new Listcell();
        lcAction.setParent(li);

        Hbox hbox = new Hbox();
        hbox.setParent(lcAction);

        Button btnInfo = new Button("", "/imgs/menu.png");
        btnInfo.addEventListener(Events.ON_CLICK, event -> {
            if (Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winStudentDebt") != null) {
                Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winStudentDebt").detach();
            }
            Map arg = new HashMap();
            arg.put(WinStudentDebtCtrl.STUDENT_DEBT_COUNT, data);
            arg.put(WinStudentDebtCtrl.LIST_ID_SEM, ((IndexPageCtrl) controller).listIdSem);

            Window win = (Window) Executions.createComponents("winStudentDebt.zul", null, arg);
            win.doModal();
        });
        btnInfo.setParent(hbox);

        Button btnDel = new Button("", "/imgs/right.png");
        btnDel.addEventListener(Events.ON_CLICK, event -> ((IndexPageCtrl) controller).addStudentRetake(li));
        btnDel.setParent(hbox);
    }
}
