package org.edec.contingentMovement.ctrl.renderer;

import org.edec.model.GroupModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * Created by dmmax
 */
public class GroupLbRenderer implements ListitemRenderer<GroupModel> {
    @Override
    public void render (Listitem li, GroupModel data, int index) throws Exception {
        li.setValue(data);
        String groupname = data.getGroupname();
        new Listcell(groupname).setParent(li);
    }
}
