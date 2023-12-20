package org.edec.utility.component.renderer;

import org.edec.main.model.DepartmentModel;
import org.zkoss.zul.*;


public class DepartmentRenderer implements ComboitemRenderer<DepartmentModel>, ListitemRenderer<DepartmentModel> {
    @Override
    public void render (Comboitem ci, DepartmentModel data, int index) throws Exception {
        ci.setValue(data);
        String title = (data.getShorttitle() != null && !data.getShorttitle().equals("")) ? data.getShorttitle() : data.getFulltitle();
        ci.setLabel(title);
    }

    @Override
    public void render (Listitem li, DepartmentModel data, int index) throws Exception {
        li.setValue(data);
        new Listcell(data.getFulltitle()).setParent(li);
        new Listcell(data.getShorttitle()).setParent(li);
    }
}
