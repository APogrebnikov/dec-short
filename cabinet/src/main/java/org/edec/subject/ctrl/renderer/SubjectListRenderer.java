package org.edec.subject.ctrl.renderer;

import org.edec.subject.model.SubjectModel;
import org.edec.utility.constants.FormOfStudy;
import org.zkoss.zul.*;

public class SubjectListRenderer implements ListitemRenderer<SubjectModel> {

    @Override
    public void render (Listitem listitem, SubjectModel model, int i) throws Exception {
        Listcell checkCell = new Listcell();
        listitem.appendChild(checkCell);
        listitem.setValue(model.getSubjectName());
        listitem.appendChild(new Listcell(model.getSubjectName()));
        listitem.setValue(model.getGroupName());
        listitem.appendChild(new Listcell(model.getGroupName()));
    }
}