package org.edec.chairEmployee.ctrl.renderer;

import org.edec.utility.component.model.EmployeeModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * @author Max Dimukhametov
 */
public class EmployeeRenderer implements ListitemRenderer<EmployeeModel> {
    @Override
    public void render (Listitem li, EmployeeModel data, int index) throws Exception {
        li.setValue(data);

        new Listcell(data.getFIO()).setParent(li);
        new Listcell(data.getSex() == 0 ? "жен." : "муж.").setParent(li);
        new Listcell(data.getLoginLdap()).setParent(li);
    }
}
