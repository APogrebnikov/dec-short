package org.edec.curriculumSchedule.ctrl.renderer;

import org.edec.curriculumSchedule.model.GroupModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class GroupListRenderer implements ListitemRenderer<GroupModel> {
    @Override
    public void render (Listitem listitem, GroupModel groupModel, int i) throws Exception {
        listitem.setValue(groupModel);
        listitem.appendChild(new Listcell(groupModel.getGroupName()));
    }
}
