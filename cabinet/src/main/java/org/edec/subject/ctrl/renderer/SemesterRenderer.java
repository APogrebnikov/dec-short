package org.edec.subject.ctrl.renderer;

import org.edec.utility.component.model.GroupModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.model.StudentModel;
import org.edec.utility.converter.DateConverter;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class SemesterRenderer implements ComboitemRenderer<SemesterModel>, ListitemRenderer<Object> {
    @Override
    public void render (Comboitem ci, SemesterModel data, int index) throws Exception {
        ci.setValue(data);
        ci.setLabel(DateConverter.convert2dateToString(data.getDateOfBegin(), data.getDateOfEnd()) + " " +
                    (data.getSeason() == 0 ? "осень" : "весна") + " / " + (data.getFormofstudy() == 1 ? "очное" : "заочное"));
    }

    @Override
    public void render (Listitem li, Object o, int index) throws Exception {
        li.setValue(o);
        if (o instanceof GroupModel) {
            GroupModel group = (GroupModel) o;
            li.setLabel("Семестр " + group.getSemester());
        } else if (o instanceof StudentModel) {
            StudentModel student = (StudentModel) o;
            li.setLabel("Семестр " + student.getSemester());
        }
    }
}