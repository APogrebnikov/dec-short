package org.edec.utility.component.renderer;

import org.edec.utility.component.model.GroupModel;
import org.zkoss.zul.*;

import java.util.List;


public class GroupRenderer implements ListitemRenderer<GroupModel>, ComboitemRenderer<GroupModel> {
    @Override
    public void render (Listitem li, GroupModel data, int index) throws Exception {
        li.setValue(data);
        Listcell lcGroupname = new Listcell(data.getGroupname());
        lcGroupname.setParent(li);
        lcGroupname.setHflex("1");

        Listcell lcCourse = new Listcell(String.valueOf(data.getCourse()));
        lcCourse.setParent(li);
        lcCourse.setHflex("1");
    }

    @Override
    public void render (Comboitem ci, GroupModel data, int i) throws Exception {
        ci.setValue(data);
        ci.setLabel(data.getGroupname());
    }
}
