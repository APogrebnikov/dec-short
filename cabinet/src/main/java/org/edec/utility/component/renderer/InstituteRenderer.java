package org.edec.utility.component.renderer;

import org.edec.utility.component.model.InstituteModel;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;

/**
 * Created by dmmax
 */
public class InstituteRenderer implements ComboitemRenderer<InstituteModel> {
    @Override
    public void render (Comboitem ci, InstituteModel data, int index) throws Exception {
        ci.setValue(data);
        ci.setLabel(data.getShorttitle());
    }
}
