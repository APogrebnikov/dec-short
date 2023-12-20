package org.edec.utility.component.renderer;

import org.edec.utility.component.model.SubjectModel;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;


public class SubjectRenderer implements ComboitemRenderer<SubjectModel> {
    @Override
    public void render (Comboitem ci, SubjectModel data, int index) throws Exception {
        ci.setValue(data);
        ci.setLabel(data.getSubjectname());
    }
}
