package org.edec.admin.ctrl.renderer;

import org.edec.admin.ctrl.IndexPageCtrl;
import org.edec.admin.model.ModuleModel;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;


public class ModuleForRoleRenderer implements ListitemRenderer<ModuleModel> {
    private boolean isBtnDeleteNeeded;
    // TODO REFACTOR избавиться от связки с контроллером
    private IndexPageCtrl adminPage;

    public ModuleForRoleRenderer (boolean isBtnDeleteNeeded, IndexPageCtrl adminPage) {
        this.isBtnDeleteNeeded = isBtnDeleteNeeded;
        this.adminPage = adminPage;
    }

    @Override
    public void render (Listitem li, final ModuleModel data, int index) throws Exception {
        li.setValue(data);
        new Listcell(data.getName()).setParent(li);
        new Listcell(data.getDepartmentTitle()).setParent(li);
        new Listcell(data.getInstituteTitle() == null ? "Все" : data.getInstituteTitle()).setParent(li);

        if (data.getFormofstudy() == 1) {
            new Listcell("Очная").setParent(li);
        } else if (data.getFormofstudy() == 2) {
            new Listcell("Заочное").setParent(li);
        } else if (data.getFormofstudy() == 3) {
            new Listcell("Все").setParent(li);
        }

        new Listcell("", data.getReadonly() ? "" : "/imgs/okCLR.png").setParent(li);
        if (isBtnDeleteNeeded) {
            Listcell lcBtn = new Listcell();
            lcBtn.setParent(li);

            Button btnDelete = new Button("", "/imgs/del.png");
            btnDelete.setParent(lcBtn);
            btnDelete.addEventListener(Events.ON_CLICK, event -> adminPage.removeModuleFromRole(data));
        }
    }
}
