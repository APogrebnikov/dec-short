package org.edec.chairEmployee.ctrl.renderer;

import org.edec.chairEmployee.model.PostModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;


public class EmployeePostRenderer implements ListitemRenderer<PostModel> {
    @Override
    public void render (Listitem li, PostModel data, int index) throws Exception {
        li.setValue(data);

        new Listcell(data.getDepartment()).setParent(li);
        new Listcell(data.getPost()).setParent(li);
    }
}
