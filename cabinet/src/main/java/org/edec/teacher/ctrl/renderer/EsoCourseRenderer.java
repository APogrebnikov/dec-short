package org.edec.teacher.ctrl.renderer;

import org.edec.teacher.model.EsoCourseModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;


public class EsoCourseRenderer implements ListitemRenderer<EsoCourseModel> {
    @Override
    public void render (Listitem li, EsoCourseModel data, int index) throws Exception {
        li.setValue(data);
        new Listcell(String.valueOf(data.getIdEsoCourse())).setParent(li);
        new Listcell(data.getFullname()).setParent(li);
    }
}
