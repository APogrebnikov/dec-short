package org.edec.utility.component.renderer;

import org.edec.utility.component.model.ChairModel;
import org.edec.utility.component.model.InstituteModel;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;

/**
 * Created by Alex
 */
public class ChairRenderer implements ComboitemRenderer<ChairModel> {
    @Override
    public void render (Comboitem ci, ChairModel data, int index) throws Exception {
        ci.setValue(data);
        ci.setLabel(data.getFulltitle());
        ci.setTooltip(data.getFulltitle());
    }
}
