package org.edec.register.renderer;

import org.edec.register.model.SubjectModel;
import org.edec.utility.constants.FormOfControlConst;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class SubjectRenderer implements ListitemRenderer<SubjectModel> {
    private Runnable update;

    public SubjectRenderer (Runnable update) {
        this.update = update;
    }

    @Override
    public void render (Listitem li, SubjectModel data, int i) throws Exception {
        li.setValue(data);
        new Listcell(data.getSubjectName()).setParent(li);
        new Listcell(data.getGroupName()).setParent(li);
        String teachersStr = data.getTeachers().toString();
        Listcell teachers = new Listcell(teachersStr.length() < 2 ? "" : teachersStr.substring(1, teachersStr.length() - 1));
        teachers.setParent(li);
        teachers.setTooltiptext(teachers.getLabel());
        new Listcell(
                FormOfControlConst.getName(data.getFoc()) != null ? FormOfControlConst.getName(data.getFoc()).getName() : "null").setParent(
                li);
    }
}
