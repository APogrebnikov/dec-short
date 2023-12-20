package org.edec.signEditor.renderer;

import org.edec.signEditor.model.RuleModel;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;

public class RuleRenderer implements ComboitemRenderer<RuleModel> {

    @Override
    public void render (Comboitem ci, RuleModel data, int index) throws Exception {
        ci.setValue(data);
        ci.setLabel(data.getRule());
    }
}

