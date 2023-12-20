package org.edec.admin.ctrl.renderer;

import org.edec.admin.model.ModuleModel;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;


public class ModuleRenderer implements ListitemRenderer<ModuleModel> {
    @Override
    public void render (Listitem li, ModuleModel data, int index) throws Exception {
        li.setValue(data);
        new Listcell(data.getName()).setParent(li);

        Listcell lcBtn = new Listcell();
        lcBtn.setParent(li);

        Button btnDel = new Button("", "/imgs/del.png");
        btnDel.setParent(lcBtn);
        btnDel.addEventListener(Events.ON_CLICK, event -> PopupUtil.showError("Не-зя!"));
    }
}
