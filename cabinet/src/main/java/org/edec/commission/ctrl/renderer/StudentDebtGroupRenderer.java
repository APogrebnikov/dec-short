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
 * Created by luninok on 17.01.2018.
 */
public class StudentDebtGroupRenderer implements ListitemRenderer<StudentCountDebtModel> {
    private Object controller;

    public StudentDebtGroupRenderer (Object controller) {
        this.controller = controller;
    }

    @Override
    public void render (final Listitem li, final StudentCountDebtModel data, int index) throws Exception {
        li.setValue(data);

        new Listcell(data.getFio()).setParent(li);
        new Listcell(data.getGroupname()).setParent(li);

        Listcell lcAction = new Listcell();
        lcAction.setStyle("text-align: -webkit-center");
        lcAction.setParent(li);

        Hbox hbox = new Hbox();
        hbox.setParent(lcAction);

        Button btnDel = new Button("", "/imgs/crossCLR.png");
        btnDel.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent (Event event) throws Exception {
                ((IndexPageCtrl) controller).removeStudentDebt(li);
            }
        });
        btnDel.setParent(hbox);
    }
}