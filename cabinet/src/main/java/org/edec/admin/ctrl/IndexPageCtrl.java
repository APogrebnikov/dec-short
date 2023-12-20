package org.edec.admin.ctrl;

import org.edec.admin.ctrl.renderer.EmployeeRoleRenderer;
import org.edec.admin.ctrl.renderer.ModuleForRoleRenderer;
import org.edec.admin.ctrl.renderer.ModuleRenderer;
import org.edec.admin.model.EmployeeModel;
import org.edec.admin.model.ModuleModel;
import org.edec.admin.model.RoleModel;
import org.edec.admin.service.AdminService;
import org.edec.admin.service.impl.AdminServiceESOImpl;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.HashMap;
import java.util.Map;


public class IndexPageCtrl extends CabinetSelector {
    @Wire
    private Button btnAddRole, btnAddModule, btnSaveModule;
    @Wire
    private Datebox dbSynchroGate, dbSynchroEokPerformance;
    // TODO REFACTOR переименовать переменную в соответствии с требованиями к разработке
    @Wire
    private Label lModule;
    @Wire
    private Listbox lbEmployee, lbEmployeeRole, lbEmployeeModule, lbRole, lbModuleForRole, lbModule, lbRoleForModule;
    @Wire
    private Textbox tbSearchFio, tbAddRole, tbAddModuleName, tbAddModuleUrl, tbAddModuleImage;

    private AdminService adminService = new AdminServiceESOImpl();

    private EmployeeModel selectedEmployee;
    private RoleModel selectedRole;

    protected void fill() {
        lbEmployee.setItemRenderer(new EmployeeRoleRenderer(false, this));
        lbEmployeeRole.setItemRenderer(new EmployeeRoleRenderer(true, this));
        lbEmployeeModule.setItemRenderer(new ModuleForRoleRenderer(false, null));
    }

    @Listen("onOK = #tbSearchFio")
    public void searchEmployee() {
        if (tbSearchFio.getValue().length() < 3) {
            PopupUtil.showWarning("Введите хотя бы 3 символа для поиска");
            return;
        }
        lbEmployee.setModel(new ListModelList<Object>(adminService.getEmployeesByFilter(tbSearchFio.getValue())));
    }

    // TODO REFACTOR переименовать методы в соответствии с требованиями к разработке
    @Listen("onSelect = #lbEmployee")
    public void selectedEmployee() {
        selectedEmployee = lbEmployee.getSelectedItem().getValue();
        lbEmployeeRole.getItems().clear();
        lbEmployeeModule.getItems().clear();
        lbEmployeeRole.setModel(new ListModelList<Object>(adminService.getRolesByEmp(selectedEmployee.getIdEmp())));
        lbEmployeeRole.renderAll();
    }

    // TODO REFACTOR переименовать методы в соответствии с требованиями к разработке
    @Listen("onSelect = #lbEmployeeRole")
    public void selectedEmployeeRole() {
        lbEmployeeModule.getItems().clear();
        lbEmployeeModule.setModel(new ListModelList<Object>(
                adminService.getModulesByRole(((RoleModel) lbEmployeeRole.getSelectedItem().getValue()).getIdRole())));
        lbEmployeeModule.renderAll();
    }

    @Listen("onClick = #btnAddRoleEmp")
    public void addRoleForEmployee() {
        if (lbEmployee.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите сначала сотрудника");
            return;
        }

        Map arg = new HashMap();
        arg.put(WinAddRoleEmpCtrl.ADMIN_PAGE, this);
        arg.put(WinAddRoleEmpCtrl.SELECTED_EMP, lbEmployee.getSelectedItem().getValue());

        ComponentHelper.createWindow("/admin/winAddRoleEmp.zul", "winAddRoleEmp", arg).doModal();
    }

    public void deleteRoleEmp(RoleModel role, Listitem li) {
        if (adminService.deleteRoleEmp(selectedEmployee.getIdEmp(), role.getIdRole())) {
            PopupUtil.showInfo("Роль успешно удалена");
            lbEmployeeRole.getItems().remove(li);
        } else {
            PopupUtil.showError("Роль не удалось удалить");
        }
    }

    @Listen("onSelect = #tabCreateRoles")
    public void selectedCreateRoles() {
        lbModuleForRole.setItemRenderer(new ModuleForRoleRenderer(true, this));
        lbRole.setItemRenderer(new EmployeeRoleRenderer(false, null));
        lbRole.setModel(new ListModelList<Object>(adminService.getAllRoles()));
        lbRole.renderAll();
    }

    @Listen("onSelect = #lbRole")
    public void selectedRole() {
        selectedRole = lbRole.getSelectedItem().getValue();
        lbModuleForRole.setModel(new ListModelList<Object>(adminService.getModulesByRole(selectedRole.getIdRole())));
        lbModuleForRole.renderAll();
    }

    @Listen("onClick = #btnAddRole")
    public void addRole() {
        btnAddRole.setVisible(false);
        tbAddRole.setVisible(true);
    }

    @Listen("onOK = #tbAddRole")
    public void createNewRole() {
        if (!tbAddRole.getValue().equals("")) {
            if (adminService.addRole(tbAddRole.getValue())) {
                lbRole.setModel(new ListModelList<Object>(adminService.getAllRoles()));
                lbRole.renderAll();
            }
        }
        tbAddRole.setValue("");
        btnAddRole.setVisible(true);
        tbAddRole.setVisible(false);
    }

    @Listen("onClick = #btnAddModuleToRole")
    public void addModuleForRole() {
        if (selectedRole == null) {
            PopupUtil.showWarning("Нужно выбрать роль");
            return;
        }

        Map arg = new HashMap();
        arg.put(WinAddModuleRoleCtrl.ADMIN_PAGE, this);
        arg.put(WinAddModuleRoleCtrl.SELECTED_ROLE, selectedRole);

        ComponentHelper.createWindow("/admin/winAddModule.zul", "winAddModule", arg).doModal();
    }

    // TODO REFACTOR переименовать методы в соответствии с требованиями к разработке
    @Listen("onSelect = #tabCreateModule")
    public void selectedTabCreateModule() {
        lbModule.setItemRenderer(new ModuleRenderer());
        lbModule.setModel(new ListModelList<>(adminService.getAllModules()));
        lbModule.renderAll();
    }

    @Listen("onClick = #btnAddModule")
    public void addModule() {
        lModule.setVisible(false);
        btnAddModule.setVisible(false);
        tbAddModuleName.setVisible(true);
        tbAddModuleUrl.setVisible(true);
        tbAddModuleImage.setVisible(true);
        btnSaveModule.setVisible(true);
    }

    @Listen("onSelect = #lbModule")
    public void selectLbModule() {
        ModuleModel selectedModule = lbModule.getSelectedItem().getValue();
        lbRoleForModule.setModel(new ListModelList<>(adminService.getRolesByModule(selectedModule.getIdModule())));
        lbRoleForModule.renderAll();
    }

    @Listen("onClick = #btnSaveModule")
    public void createModule() {
        if (tbAddModuleName.getValue().equals("") || tbAddModuleUrl.getValue().equals("") || tbAddModuleImage.getValue().equals("")) {
            PopupUtil.showWarning("Нужно заполнить название и путь!");
            return;
        }

        if (adminService.addModule(tbAddModuleName.getValue(), tbAddModuleUrl.getValue(), tbAddModuleImage.getValue())) {
            selectedTabCreateModule();
        }

        lModule.setVisible(true);
        btnAddModule.setVisible(true);
        tbAddModuleName.setVisible(false);
        tbAddModuleUrl.setVisible(false);
        tbAddModuleImage.setVisible(false);
        btnSaveModule.setVisible(false);
        tbAddModuleUrl.setValue("");
        tbAddModuleName.setValue("");
    }

    public void removeModuleFromRole(ModuleModel module) {
        if (adminService.deleteModuleFromRole(module.getIdModuleRoleDep())) {
            PopupUtil.showInfo("Модуль успешно удален");
            selectedRole();
        } else {
            PopupUtil.showError("Модуль не удалось удалить");
        }
    }
}
