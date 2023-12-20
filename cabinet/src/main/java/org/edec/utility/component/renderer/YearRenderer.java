package org.edec.utility.component.renderer;

import org.edec.utility.component.model.GroupModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.model.StudentModel;
import org.edec.utility.component.model.YearModel;
import org.edec.utility.converter.DateConverter;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * @author Alex
 */
public class YearRenderer implements ComboitemRenderer<YearModel> {
    @Override
    public void render (Comboitem ci, YearModel data, int index) throws Exception {
        ci.setValue(data);
        ci.setLabel(DateConverter.convert2dateToString(data.getDateOfBegin(), data.getDateOfEnd()));
    }
}

