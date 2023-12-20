package org.edec.commission.ctrl.renderer;

import org.edec.commission.model.CommissionStructureModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * Created by dmmax
 */
public class CommissionStructureRenderer implements ListitemRenderer<CommissionStructureModel> {
    @Override
    public void render (Listitem li, CommissionStructureModel data, int index) throws Exception {
        li.setValue(data);

        new Listcell("", (data.isLeader() ? "/imgs/okCLR.png" : "")).setParent(li);
        new Listcell(data.getFio()).setParent(li);
        new Listcell(data.getRolename()).setParent(li);
    }
}
