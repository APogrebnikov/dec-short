package org.edec.admin.ctrl.renderer;

import org.edec.admin.ctrl.IndexPageCtrl;
import org.edec.admin.model.EmployeeModel;
import org.edec.admin.model.RoleModel;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

public class EmployeeRoleRenderer implements ListitemRenderer<Object> {
    private boolean isBtnDeleteNeeded;
    // TODO REFACTOR избавиться от связки с контроллером
    private IndexPageCtrl adminPage;

    public EmployeeRoleRenderer (boolean isBtnDeleteNeeded, IndexPageCtrl adminPage) {
        this.isBtnDeleteNeeded = isBtnDeleteNeeded;
        this.adminPage = adminPage;
    }

    @Override
    public void render (final Listitem li, final Object data, int index) throws Exception {
        li.setValue(data);
        if (data instanceof EmployeeModel) {
            new Listcell(((EmployeeModel) data).getFio()).setParent(li);
        } else if (data instanceof RoleModel) {
            new Listcell(((RoleModel) data).getName()).setParent(li);
            if (isBtnDeleteNeeded) {
                Listcell lcBtn = new Listcell();
                lcBtn.setParent(li);

                Button btnDelete = new Button("", "/imgs/del.png");
                btnDelete.setParent(lcBtn);
                btnDelete.addEventListener(Events.ON_CLICK,
                                           event -> DialogUtil.questionWithYesNoButtons("Вы действительно хотите удалить роль?",
                                                                                        "Внимание!", event1 -> {
                                                       if (event1.getName().equals(DialogUtil.ON_YES)) {
                                                           adminPage.deleteRoleEmp((RoleModel) data, li);
                                                       }
                                                   }
                                           )
                );
            }
        }
    }
}
