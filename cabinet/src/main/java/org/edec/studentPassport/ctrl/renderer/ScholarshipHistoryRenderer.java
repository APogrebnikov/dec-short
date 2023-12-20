package org.edec.studentPassport.ctrl.renderer;

import org.edec.scholarship.model.ScholarshipHistoryModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class ScholarshipHistoryRenderer implements ListitemRenderer<ScholarshipHistoryModel> {

    @Override
    public void render(Listitem item, ScholarshipHistoryModel data, int index) throws Exception {
        item.setValue(data);
        new Listcell(data.getName().getName()).setParent(item);
        new Listcell(data.getDateFrom().toString()).setParent(item);
        new Listcell(data.getDateTo() == null ? "-" : data.getDateTo().toString()).setParent(item);
        new Listcell(data.getOrderNumber()).setParent(item);
        new Listcell(data.getDateCancel() == null ? "-" : data.getDateCancel().toString()).setParent(item);
        new Listcell(data.getCancelOrderNumber() == null ? "-" : data.getCancelOrderNumber()).setParent(item);
    }
}
