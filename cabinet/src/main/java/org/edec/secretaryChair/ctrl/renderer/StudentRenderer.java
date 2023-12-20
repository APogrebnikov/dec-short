package org.edec.secretaryChair.ctrl.renderer;

import org.edec.teacher.model.commission.StudentModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;


public class StudentRenderer implements ListitemRenderer<StudentModel> {
    @Override
    public void render (Listitem li, StudentModel data, int index) throws Exception {
        new Listcell(String.valueOf(index + 1)).setParent(li);
        new Listcell(data.getFio()).setParent(li);
        new Listcell(data.getGroupname()).setParent(li);
    }
}
