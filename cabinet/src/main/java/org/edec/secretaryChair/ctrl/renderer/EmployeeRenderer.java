package org.edec.secretaryChair.ctrl.renderer;

import org.edec.secretaryChair.model.EmployeeModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;


public class EmployeeRenderer implements ListitemRenderer<EmployeeModel> {

    @Override
    public void render (Listitem li, EmployeeModel data, int index) throws Exception {
        li.setValue(data);

        new Listcell("", "/imgs/addaltCLR.png").setParent(li);
        new Listcell(data.getFio()).setParent(li);
        new Listcell(data.getRole()).setParent(li);
    }
}
