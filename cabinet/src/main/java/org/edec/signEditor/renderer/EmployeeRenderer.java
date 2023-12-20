package org.edec.signEditor.renderer;

import org.edec.signEditor.model.EmployeeModel;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;

public class EmployeeRenderer implements ComboitemRenderer<EmployeeModel> {

    @Override
    public void render (Comboitem ci, EmployeeModel data, int index) throws Exception {
        ci.setValue(data);
        ci.setLabel(data.getFio());
    }
}