package org.edec.profile.ctrl.renderer;

import org.edec.main.model.ModuleModel;
import org.edec.profile.ctrl.WinModuleStartPageCtrl;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;


public class ModuleStartPageRenderer implements ListitemRenderer<ModuleModel> {
    private WinModuleStartPageCtrl startPageCtrl;

    public ModuleStartPageRenderer (WinModuleStartPageCtrl startPageCtrl) {
        this.startPageCtrl = startPageCtrl;
    }

    private ComponentService componentService = new ComponentServiceESOimpl();

    @Override
    public void render (Listitem li, final ModuleModel data, int index) throws Exception {
        li.setValue(data);
        componentService.createListcell(li, String.valueOf(index + 1), "color: #777;", "", "");
        componentService.createListcell(li, data.getName(), "color: #777;", "", "");

        li.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent (Event event) throws Exception {
                startPageCtrl.choseStartPage(data);
            }
        });
    }
}