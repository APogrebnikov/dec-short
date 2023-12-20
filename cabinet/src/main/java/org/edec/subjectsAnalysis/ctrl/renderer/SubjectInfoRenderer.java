package org.edec.subjectsAnalysis.ctrl.renderer;

import org.edec.subjectsAnalysis.model.SubjectsInfoModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class SubjectInfoRenderer implements ListitemRenderer<SubjectsInfoModel> {
    @Override
    public void render(Listitem li, SubjectsInfoModel data, int i) throws Exception {
        li.appendChild(new Listcell(data.getSubjectname()));
        li.appendChild(new Listcell(data.getFio()));
        li.appendChild(new Listcell(data.getGroupname()));
        li.appendChild(new Listcell(data.getChair()));
    }
}
