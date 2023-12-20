package org.edec.efficiency.ctrl.renderer;

import org.edec.efficiency.model.ProblemGroup;
import org.edec.efficiency.service.EfficiencyService;
import org.edec.efficiency.service.impl.EfficiencyImpl;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;


public class GroupConfigRenderer implements ListitemRenderer<ProblemGroup> {
    private EfficiencyService efficiencyService = new EfficiencyImpl();

    @Override
    public void render (Listitem li, final ProblemGroup data, int index) throws Exception {
        li.setValue(data);
        new Listcell(data.getGroupname()).setParent(li);
        new Listcell(String.valueOf(data.getCourse())).setParent(li);
        final Listcell lcEfficiency = new Listcell("", data.getEfficiency() ? "/imgs/okCLR.png" : "");
        lcEfficiency.setParent(li);
        lcEfficiency.addEventListener(Events.ON_DOUBLE_CLICK, event -> {
            if (efficiencyService.updateEfficiencyGroup(data.getIdLGS(), !data.getEfficiency())) {
                data.setEfficiency(!data.getEfficiency());
                lcEfficiency.setImage(data.getEfficiency() ? "/imgs/okCLR.png" : "");
            }
        });
    }
}
