package org.edec.studentPassport.ctrl.renderer;

import org.apache.poi.ss.formula.functions.Even;
import org.edec.commons.component.DatepickerComponent;
import org.edec.scholarship.model.ScholarshipModel;
import org.edec.scholarship.service.ScholarshipService;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class ScholarshipRenderer implements ListitemRenderer<ScholarshipModel> {

    private final ScholarshipService scholarshipService;

    public ScholarshipRenderer(ScholarshipService scholarshipService) {this.scholarshipService = scholarshipService;}

    @Override
    public void render(Listitem listitem, ScholarshipModel scholarshipModel, int i) throws Exception {
        listitem.setValue(scholarshipModel);

        new Listcell(scholarshipModel.getType().getName()).setParent(listitem);

        DatepickerComponent dpcDateFrom = new DatepickerComponent(scholarshipModel.getDateFrom());
        dpcDateFrom.addEventListener("onUpdate", event ->  {
            scholarshipModel.setDateFrom(dpcDateFrom.getValue());
            scholarshipService.updateScholarship(scholarshipModel);
            Events.echoEvent("onRefresh", dpcDateFrom, scholarshipModel.getDateFrom());
        });
        dpcDateFrom.setParent(listitem);

        DatepickerComponent dpcDateTo = new DatepickerComponent(scholarshipModel.getDateTo());
        dpcDateTo.addEventListener("onUpdate", event ->  {
            scholarshipModel.setDateTo(dpcDateTo.getValue());
            scholarshipService.updateScholarship(scholarshipModel);
            Events.echoEvent("onRefresh", dpcDateTo, scholarshipModel.getDateTo());
        });
        dpcDateTo.setParent(listitem);

        Button btn = new Button("","/imgs/crossCLR.png");

        btn.addEventListener(Events.ON_CLICK, event -> {
            scholarshipService.deleteScholarship(scholarshipModel);
            dpcDateFrom.onRefresh(null);
            dpcDateTo.onRefresh(null);
        });

        Listcell lcBtnClear = new Listcell();
        btn.setParent(lcBtnClear);
        lcBtnClear.setParent(listitem);
    }
}
