package org.edec.trajectoryLinker.ctrl.renderer;

import org.edec.trajectoryLinker.model.SubjectModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class SubjectRenderer implements ListitemRenderer<SubjectModel> {

    @Override
    public void render (Listitem listitem, SubjectModel data, int i) throws Exception {
        listitem.setValue(data);

        new Listcell().setParent(listitem);
        new Listcell(data.getSubjectName()).setParent(listitem);
        new Listcell(data.getSemesterNumber().toString()).setParent(listitem);
    }
}
