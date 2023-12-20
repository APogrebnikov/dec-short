package org.edec.reference.ctrl.renderer;

import org.edec.reference.model.StudentModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class StudentListRenderer implements ListitemRenderer<StudentModel> {

    @Override
    public void render (Listitem item, StudentModel data, int index) throws Exception {
        String referenceType = "";

        referenceType += data.getIndigent() ? "Малоимущий" : "";
        referenceType += data.getInvalid() ? (!referenceType.equals("") ? ", " : "") + "Инвалид" : "";
        referenceType += data.getOrphan() ? (!referenceType.equals("") ? ", " : "") + "Сирота" : "";
        referenceType += data.getVeteran() ? (!referenceType.equals("") ? ", " : "") + "Ветеран" : "";

        item.setValue(data);
        item.appendChild(new Listcell(data.getFio()));
        item.appendChild(new Listcell(data.getGroupName().equals("") ? "Не обнаружено" : data.getGroupName()));
        item.appendChild(new Listcell(referenceType));
    }
}
