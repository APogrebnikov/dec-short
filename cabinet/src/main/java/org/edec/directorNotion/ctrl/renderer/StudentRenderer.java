package org.edec.directorNotion.ctrl.renderer;

import org.edec.directorNotion.model.StudentModel;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class StudentRenderer implements ListitemRenderer<StudentModel> {

    public StudentRenderer () {
    }

    @Override
    public void render (Listitem li, final StudentModel data, final int index) throws Exception {
        li.setValue(data);

        new Listcell().setParent(li);
        new Listcell(data.getFamily()).setParent(li);
        new Listcell(data.getName()).setParent(li);
        new Listcell(data.getPatronymic()).setParent(li);
        new Listcell(data.getRecordbook()).setParent(li);
        new Listcell(data.getGroupname()).setParent(li);
        new Listcell(String.valueOf(data.getCourse())).setParent(li);
    }
}
