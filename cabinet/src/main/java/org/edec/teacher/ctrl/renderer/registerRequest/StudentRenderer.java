package org.edec.teacher.ctrl.renderer.registerRequest;

import org.edec.teacher.model.registerRequest.StudentModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class StudentRenderer implements ListitemRenderer<StudentModel> {

    @Override
    public void render (Listitem listitem, StudentModel studentModel, int i) throws Exception {
        listitem.setValue(studentModel);
        listitem.appendChild(new Listcell(studentModel.getFio()));
    }
}
