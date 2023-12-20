package org.edec.utility.component.renderer;

import org.edec.model.SemesterModel;
import org.edec.utility.converter.DateConverter;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class SemesterListRenderer implements ListitemRenderer<SemesterModel> {
    @Override
    public void render (Listitem li, SemesterModel data, int i) throws Exception {
        li.setValue(data);

        new Listcell(DateConverter.convert2dateToString(data.getDateOfBegin(), data.getDateOfEnd())).setParent(li);
        new Listcell(data.getSeason() == 0 ? "Осень" : "Весна").setParent(li);
    }
}
