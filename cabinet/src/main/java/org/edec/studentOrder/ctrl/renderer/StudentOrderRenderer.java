package org.edec.studentOrder.ctrl.renderer;

import org.edec.studentOrder.model.StudentOrderModel;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.function.Consumer;

public class StudentOrderRenderer implements ListitemRenderer<StudentOrderModel> {
    Consumer<StudentOrderModel> action;

    public StudentOrderRenderer (Consumer<StudentOrderModel> action) {
        this.action = action;
    }

    @Override
    public void render (Listitem listitem, StudentOrderModel searchStudent, int i) throws Exception {

        Listcell family = new Listcell(searchStudent.getFamily());
        Listcell name = new Listcell(searchStudent.getName());
        Listcell patronymic = new Listcell(searchStudent.getPatronymic());
        Listcell groupname = new Listcell(searchStudent.getGroupname());
        listitem.appendChild(family);
        listitem.appendChild(name);
        listitem.appendChild(patronymic);
        groupname.setParent(listitem);

        listitem.addEventListener(Events.ON_CLICK, event -> {
            action.accept(searchStudent);
        });
    }
}
