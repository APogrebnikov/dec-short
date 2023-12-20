package org.edec.efficiency.ctrl.renderer;

import org.edec.model.EmployeeModel;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;


public class EmployeeRenderer implements ComboitemRenderer<EmployeeModel> {
    @Override
    public void render (Comboitem comboitem, EmployeeModel employeeModel, int i) throws Exception {
        comboitem.setValue(employeeModel);
        comboitem.setLabel(employeeModel.getShortFio());
    }
}
