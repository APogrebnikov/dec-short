package org.edec.admin.ctrl;

import org.edec.admin.ctrl.renderer.EmployeeRoleRenderer;
import org.edec.admin.model.EmployeeModel;
import org.edec.admin.model.RoleModel;
import org.edec.admin.service.AdminService;
import org.edec.admin.service.impl.AdminServiceESOImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;


public class WinAddRoleEmpCtrl extends SelectorComposer<Component> {
    public static final String ADMIN_PAGE = "admin_page";
    public static final String SELECTED_EMP = "selected_emp";

    // TODO REFACTOR переименовать переменную в соответствии с требованиями к разработке
    @Wire
    private Label lAddRoleEmp;

    @Wire
    private Listbox lbAddRoleEmp;

    private AdminService adminService = new AdminServiceESOImpl();

    private EmployeeModel selectedEmployee;
    private IndexPageCtrl adminPage;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        selectedEmployee = (EmployeeModel) Executions.getCurrent().getArg().get(SELECTED_EMP);
        adminPage = (IndexPageCtrl) Executions.getCurrent().getArg().get(ADMIN_PAGE);

        lAddRoleEmp.setValue(selectedEmployee.getFio());
        lbAddRoleEmp.setItemRenderer(new EmployeeRoleRenderer(false, null));
        lbAddRoleEmp.setModel(new ListModelList<Object>(adminService.getRolesNotEqualEmp(selectedEmployee.getIdEmp())));
        lbAddRoleEmp.renderAll();
    }

    @Listen("onClick = #btnAddWinRoleEmp")
    public void addRoleForEmp () {
        if (lbAddRoleEmp.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите сначала роль");
            return;
        }
        RoleModel selectedRole = lbAddRoleEmp.getSelectedItem().getValue();
        if (adminService.addRoleForEmployee(selectedEmployee.getIdEmp(), selectedRole.getIdRole())) {
            PopupUtil.showInfo("Роль успешно добавлена");
            lbAddRoleEmp.getItems().remove(lbAddRoleEmp.getSelectedItem());
            adminPage.selectedEmployee();
        } else {
            PopupUtil.showError("Роль не удалось добавить");
        }
    }

    @Listen("onClick = #btnExitWinAddRoleEmp")
    public void exitFromWin () {
        getSelf().detach();
    }
}
