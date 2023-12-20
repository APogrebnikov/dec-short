package org.edec.admin.ctrl;

import org.edec.admin.model.DepartmentModel;
import org.edec.admin.model.ModuleModel;
import org.edec.admin.model.RoleModel;
import org.edec.admin.service.AdminService;
import org.edec.admin.service.impl.AdminServiceESOImpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.List;


public class WinAddModuleRoleCtrl extends SelectorComposer<Component> {
    public static final String ADMIN_PAGE = "admin_page";
    public static final String SELECTED_ROLE = "selected_role";

    @Wire
    private Checkbox chReadOnly;

    @Wire
    private Combobox cmbFormOfStudy;

    // TODO REFACTOR переименовать переменные в соответствии с требованиями к разработке
    @Wire
    private Label lSelectedModule, lSelectedDepartment, lSelectedInstitute;

    @Wire
    private Listbox lbModuleList, lbDepartmentList;

    @Wire
    private Textbox tbSearchDepartment;

    private AdminService adminService = new AdminServiceESOImpl();
    private ComponentService componentService = new ComponentServiceESOimpl();

    private List<DepartmentModel> departments;
    private IndexPageCtrl adminPage;
    private RoleModel selectedRole;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        adminPage = (IndexPageCtrl) Executions.getCurrent().getArg().get(ADMIN_PAGE);
        selectedRole = (RoleModel) Executions.getCurrent().getArg().get(SELECTED_ROLE);

        componentService.fillCmbFormOfStudy(cmbFormOfStudy, null, FormOfStudy.ALL.getType());
        lbModuleList.setItemRenderer((ListitemRenderer<ModuleModel>) (li, data, index) -> {
            li.setValue(data);
            new Listcell(data.getName()).setParent(li);
        });
        lbModuleList.setModel(new ListModelList<Object>(adminService.getAllModules()));
        lbDepartmentList.setItemRenderer((ListitemRenderer<DepartmentModel>) (li, data, index) -> {
            li.setValue(data);
            new Listcell(data.getDepartmentTitle()).setParent(li);
            new Listcell(data.getInstituteTitle() == null ? "Все" : data.getInstituteTitle()).setParent(li);
        });
        Clients.showBusy(lbDepartmentList, "Загрузка данных");
        Events.echoEvent("onLater", lbDepartmentList, null);
    }

    // TODO REFACTOR переименовать метод в соответствии с требованиями к разработке
    @Listen("onLater = #lbDepartmentList")
    public void onLaterDepartmentList() {
        if (departments == null) {
            departments = adminService.getAllDepartments();
            lbDepartmentList.setModel(new ListModelList<Object>(departments));
        } else {
            lbDepartmentList
                    .setModel(new ListModelList<Object>(adminService.getDepartmentsByFilter(tbSearchDepartment.getValue(), departments)));
        }
        lbDepartmentList.renderAll();
        Clients.clearBusy(lbDepartmentList);
    }

    // TODO REFACTOR переименовать метод в соответствии с требованиями к разработке
    @Listen("onSelect = #lbModuleList")
    public void selectedModule() {
        ModuleModel selectedModule = lbModuleList.getSelectedItem().getValue();
        lSelectedModule.setValue(selectedModule.getName());
        // TODO REFACTOR в нескольких местах используется константа "data", необходимо вынести ее в константы класса
        lSelectedModule.setAttribute("data", selectedModule);
    }

    // TODO REFACTOR переименовать метод в соответствии с требованиями к разработке
    @Listen("onSelect = #lbDepartmentList")
    public void selectedDepartment() {
        DepartmentModel selectedDepartment = lbDepartmentList.getSelectedItem().getValue();
        lSelectedDepartment.setValue(selectedDepartment.getDepartmentTitle());
        lSelectedInstitute.setValue(selectedDepartment.getInstituteTitle());
        lSelectedDepartment.setAttribute("data", selectedDepartment);
    }

    @Listen("onClick = #btnCreateModuleRole")
    public void createModuleRole() {
        if (lSelectedDepartment.getAttribute("data") == null || lSelectedModule.getAttribute("data") == null) {
            PopupUtil.showWarning("Выберите модуль и подразделение!");
            return;
        }
        DepartmentModel selectedDepartment = (DepartmentModel) lSelectedDepartment.getAttribute("data");
        ModuleModel selectedModule = (ModuleModel) lSelectedModule.getAttribute("data");
        Integer formOfstudy = ((FormOfStudy) cmbFormOfStudy.getSelectedItem().getValue()).getType();
        Boolean readonly = chReadOnly.isChecked();
        if (adminService
                .addModuleForRole(selectedDepartment.getIdDepartment(), selectedModule.getIdModule(), selectedRole.getIdRole(), formOfstudy,
                                  readonly
                )) {
            PopupUtil.showInfo("Модуль успешно добавлен к роли!");
            adminPage.selectedRole();
        } else {
            PopupUtil.showError("Не получилось добавить модуль.");
        }
    }

    @Listen("onOK = #tbSearchDepartment")
    public void searchDepartment() {
        Clients.showBusy(lbDepartmentList, "Загрузка данных");
        Events.echoEvent("onLater", lbDepartmentList, null);
    }
}
