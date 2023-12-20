package org.edec.order.ctrl.renderer;

import org.edec.order.model.StudentModel;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.Date;


public class DefineProlongationRenderer implements ListitemRenderer<StudentModel> {
    @Override
    public void render (Listitem listitem, final StudentModel studentModel, int i) throws Exception {
        listitem.setValue(studentModel);
        Listcell cellFio = new Listcell(studentModel.getFio());
        cellFio.setParent(listitem);

        Listcell cellGroup = new Listcell(studentModel.getGroupname());
        cellGroup.setParent(listitem);

        final Listcell cellDateProlongation = new Listcell();
        final Datebox dbProlongation = new Datebox();
        dbProlongation.addEventListener(Events.ON_CHANGE, event -> studentModel.setDateProlongation(dbProlongation.getValue()));

        Listcell cellCheckProlongation = new Listcell();
        final Checkbox checkProlongation = new Checkbox();
        checkProlongation.setChecked(studentModel.getProlongatedManualy());

        checkProlongation.addEventListener(Events.ON_CLICK, event -> {
            checkProlongation.setChecked(!checkProlongation.isChecked());
            studentModel.setProlongatedManualy(checkProlongation.isChecked());
            if (checkProlongation.isChecked()) {
                cellDateProlongation.appendChild(dbProlongation);
            } else {
                cellDateProlongation.removeChild(dbProlongation);
            }
        });

        cellCheckProlongation.addEventListener(Events.ON_CLICK, event -> {
            checkProlongation.setChecked(!checkProlongation.isChecked());
            studentModel.setProlongatedManualy(checkProlongation.isChecked());
            if (checkProlongation.isChecked()) {
                cellDateProlongation.appendChild(dbProlongation);
            } else {
                cellDateProlongation.removeChild(dbProlongation);
            }
        });

        cellFio.addEventListener(Events.ON_CLICK, event -> {
            checkProlongation.setChecked(!checkProlongation.isChecked());
            studentModel.setProlongatedManualy(checkProlongation.isChecked());
            if (checkProlongation.isChecked()) {
                cellDateProlongation.appendChild(dbProlongation);
            } else {
                cellDateProlongation.removeChild(dbProlongation);
            }
        });

        cellGroup.addEventListener(Events.ON_CLICK, event -> {
            checkProlongation.setChecked(!checkProlongation.isChecked());
            studentModel.setProlongatedManualy(checkProlongation.isChecked());
            if (checkProlongation.isChecked()) {
                cellDateProlongation.appendChild(dbProlongation);
            } else {
                cellDateProlongation.removeChild(dbProlongation);
            }
        });

        cellCheckProlongation.appendChild(checkProlongation);
        cellCheckProlongation.setParent(listitem);
        cellDateProlongation.setParent(listitem);
    }
}
