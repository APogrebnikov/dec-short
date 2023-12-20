package org.edec.passportGroup.ctrl.renderer;

import org.edec.passportGroup.model.GroupModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * Created by antonskripacev on 09.04.17.
 */
public class GroupListRenderer implements ListitemRenderer<GroupModel> {

    @Override
    public void render (Listitem listitem, final GroupModel groupModel, int i) throws Exception {
        listitem.setValue(groupModel);
        listitem.appendChild(new Listcell(groupModel.getGroupName()));
        listitem.appendChild(new Listcell(groupModel.getCourse().toString()));

        String qualification;

        if (groupModel.getQualification() == 1) {
            qualification = "Инженер";
        } else if (groupModel.getQualification() == 2) {
            qualification = "Бакалавр";
        } else {
            qualification = "Магистр";
        }

        listitem.appendChild(new Listcell(qualification));
    }
}
