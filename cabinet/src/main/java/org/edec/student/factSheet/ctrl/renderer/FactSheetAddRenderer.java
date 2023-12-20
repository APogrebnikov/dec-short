package org.edec.student.factSheet.ctrl.renderer;

import org.edec.factSheet.model.FactSheetAddModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class FactSheetAddRenderer implements ListitemRenderer<FactSheetAddModel> {
    private ComponentService componentService = new ComponentServiceESOimpl();

    @Override
    public void render (Listitem li, FactSheetAddModel factSheetAddModel, int i) throws Exception {
        li.setValue(factSheetAddModel);
        String current = "";
        if (factSheetAddModel.getCurrent()) {
            current = " (текущая)";
        }

        componentService.createListcell(li, factSheetAddModel.getFullName(), "color: #000;", "", "background: #fff;");
        componentService.createListcell(li, factSheetAddModel.getGroupName() + current, "color: #000;", "", "background: #fff;");
        componentService.createListcell(li, factSheetAddModel.getRecordBook(), "color: #000;", "", "background: #fff;");
        if (factSheetAddModel.getDeducted()) {
            li.setStyle("background: #95FF82;");
        } else if (factSheetAddModel.getAcademicleave()) {
            li.setStyle("background: #FF7373;");
        }
    }
}
