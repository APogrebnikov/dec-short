package org.edec.commons.renderer;

import org.edec.model.GroupModel;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;


public class GroupRenderer implements ComboitemRenderer<GroupModel> {
    @Override
    public void render (Comboitem ci, GroupModel data, int index) throws Exception {
        ci.setValue(data);
        ci.setLabel(data.getGroupname());
    }
}
