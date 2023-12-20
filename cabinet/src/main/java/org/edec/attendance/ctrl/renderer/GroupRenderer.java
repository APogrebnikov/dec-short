package org.edec.attendance.ctrl.renderer;

import org.edec.model.GroupSemesterModel;
import org.zkoss.zul.*;


public class GroupRenderer implements ComboitemRenderer<GroupSemesterModel>
{
    @Override
    public void render(Comboitem ci, GroupSemesterModel data, int i) throws Exception {
        ci.setValue(data);
        ci.setLabel(data.getGroupname());
    }
}
