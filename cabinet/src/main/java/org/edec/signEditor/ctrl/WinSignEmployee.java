package org.edec.signEditor.ctrl;

import lombok.extern.log4j.Log4j;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.ModuleModel;
import org.edec.signEditor.model.EmployeeModel;
import org.edec.signEditor.model.SignatoryModel;
import org.edec.signEditor.renderer.EmployeeRenderer;
import org.edec.signEditor.renderer.SignatoryModelRenderer;
import org.edec.signEditor.service.SignatoryEditorService;
import org.edec.signEditor.service.impl.SignatoryEditorServiceImpl;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.ActionRuleConst;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.*;

import static org.zkoss.zk.ui.Executions.getCurrent;

@Log4j
public class WinSignEmployee extends SelectorComposer<Component> {

    @Wire
    private Combobox cmbInst, cmbEmp, cmbRole, cmbFos;

    @Wire
    private Textbox tbPosition, tbPost, tbSubquery, tbEmpFilter, tbRoleFilter, tbPositionFilter;

    @Wire
    private Groupbox gbInst;

    @Wire
    private Checkbox cbPrint;

    @Wire
    private Button btnAddEmp, btnSaveEmp, btnRemoveEmp;

    @Wire
    private Listbox lbSignatory;

    private ComponentService componentService = new ComponentServiceESOimpl();
    private SignatoryEditorService signatoryEditorService = new SignatoryEditorServiceImpl();
    private TemplatePageCtrl template = new TemplatePageCtrl();

    private List<SignatoryModel> signatories;
    private List<SignatoryModel> filteredSignatories;
    private List<EmployeeModel> employees;
    private SignatoryModel editedSignatory;

    private Long idOrderRule;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        idOrderRule = (Long) Executions.getCurrent().getArg().get("idOrderRule");

        signatories = signatoryEditorService.getSignatoryList(idOrderRule);
        filteredSignatories = new ArrayList<>(signatories);

        ListModelList<SignatoryModel> lmSignatories = new ListModelList<>(filteredSignatories);

        lbSignatory.setAttribute("data", filteredSignatories);
        lbSignatory.setModel(lmSignatories);
        lbSignatory.renderAll();
        lbSignatory.addEventListener(Events.ON_SELECT, event -> {
            editedSignatory = filteredSignatories.get(lbSignatory.getSelectedIndex());

            fillEditPanel();
        });

        clearEditPanel();

        template.checkModuleByRole(getCurrent().getDesktop().getRequestPath(), getPage());
        ModuleModel currentModule = template.getCurrentModule();
        if (currentModule != null) {
            init();
        }
    }

    private void init() {
        employees = signatoryEditorService.getEmployeeList();

        fillCmbEmp();

        lbSignatory.setItemRenderer(new SignatoryModelRenderer());
    }

    private void fillCmbEmp() {
        cmbEmp.setModel(new ListModelList<>(employees));
        cmbEmp.setItemRenderer(new EmployeeRenderer());
    }

    private void fillEditPanel() {

        EmployeeModel emp = editedSignatory.getEmployee();
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).equals(emp)) {
                cmbEmp.setSelectedIndex(i);
                break;
            }
        }
        cmbEmp.setDisabled(false);

        cmbRole.setSelectedIndex(editedSignatory.getRole());

        tbPosition.setValue(editedSignatory.getPosition().toString());
        tbPost.setValue(editedSignatory.getPost());
        if (editedSignatory.getFos() != null) {
            cmbFos.setSelectedIndex(editedSignatory.getFos() - 1);
        } else {
            cmbFos.setSelectedIndex(-1);
        }

        cbPrint.setChecked(editedSignatory.getPrint());

        tbSubquery.setValue(editedSignatory.getSubquery());

        btnAddEmp.setDisabled(false);
        btnSaveEmp.setDisabled(false);
        btnRemoveEmp.setDisabled(false);
    }

    private void clearEditPanel() {
        editedSignatory = new SignatoryModel();

        cmbEmp.setSelectedIndex(-1);
        cmbEmp.setDisabled(true);
        cmbRole.setSelectedIndex(0);
        tbPosition.setValue("");
        tbPost.setValue("");
        tbSubquery.setValue("");

        btnAddEmp.setDisabled(false);
        btnSaveEmp.setDisabled(true);
        btnRemoveEmp.setDisabled(true);
        cbPrint.setChecked(true);
    }

    @Listen("onOK = #tbEmpFilter; onOK = #tbRoleFilter; onOK = #tbPositionFilter")
    public void filterSignatoryList() {
        filteredSignatories = signatoryEditorService.filterSignatoryList(signatories
                , tbEmpFilter.getText()
                , tbRoleFilter.getText()
                , tbPositionFilter.getText());

        ListModelList<SignatoryModel> lmSignatories = new ListModelList<>(filteredSignatories);
        lbSignatory.setAttribute("data", filteredSignatories);
        lbSignatory.setModel(lmSignatories);
    }

    @Listen("onChange = #cmbRole")
    public void uncheckPrinting() {
        if (cmbRole.getSelectedIndex() == ActionRuleConst.NOTIFICATION.getValue()) {
            cbPrint.setChecked(false);
        }
    }

    @Listen("onClick = #btnAddEmp")
    public void addSignatory() {
        editedSignatory = new SignatoryModel();
        lbSignatory.setSelectedIndex(-1);

        cmbEmp.setSelectedIndex(-1);
        cmbEmp.setText("Выберите нового сотрудника");
        cmbEmp.setDisabled(false);
        cmbRole.setSelectedIndex(-1);
        tbPosition.setValue("");
        tbPost.setValue("");
        tbSubquery.setValue("");

        btnAddEmp.setDisabled(true);
        btnSaveEmp.setDisabled(false);
        btnRemoveEmp.setDisabled(true);
    }

    @Listen("onClick = #btnSaveEmp")
    public void saveSignatory() {
        if (lbSignatory.getSelectedIndex() != -1) {
            editedSignatory.setIdLre(filteredSignatories.get(lbSignatory.getSelectedIndex()).getIdLre());
        }

        if (cmbEmp.getSelectedIndex() == -1) {
            Clients.showNotification("Выберите сотрудника!", Clients.NOTIFICATION_TYPE_WARNING, null, null, 2000);
            return;
        }

        if (cmbRole.getSelectedIndex() == -1) {
            Clients.showNotification("Выберите роль для сотрудника!", Clients.NOTIFICATION_TYPE_WARNING, null, null, 2000);
            return;
        }
        if (tbPosition.getText().equals("")) {
            Clients.showNotification("Укажите позицию!", Clients.NOTIFICATION_TYPE_WARNING, null, null, 2000);
            return;
        }

        editedSignatory.setEmployee(employees.get(cmbEmp.getSelectedIndex()));
        editedSignatory.setRole(cmbRole.getSelectedIndex());
        editedSignatory.setIdRule(idOrderRule);
        editedSignatory.setPrint(cbPrint.isChecked());

        try {
            editedSignatory.setPosition(Integer.parseInt(tbPosition.getText()));
        } catch (NumberFormatException nfe) {
            Clients.showNotification("Позиция должна быть задана числом", Clients.NOTIFICATION_TYPE_WARNING, null, null, 2000);
            return;
        }

        editedSignatory.setPost(tbPost.getText());
        editedSignatory.setSubquery(tbSubquery.getText());

        if (editedSignatory.getPosition() < 0) {
            Clients.showNotification("Значение позиции должно быть больше либо равно нулю!", Clients.NOTIFICATION_TYPE_WARNING, null, null, 2000);
            return;
        }

        if (editedSignatory.getRole() == ActionRuleConst.CONCORDANT.getValue()) {
            editedSignatory.setSign(true);
        } else {
            editedSignatory.setSign(false);
        }

        if (cmbFos.getSelectedIndex() != -1) {
            editedSignatory.setFos(cmbFos.getSelectedIndex() + 1);
        }

        if (editedSignatory.getIdLre() != null) {
            if (signatoryEditorService.updateSignatory(editedSignatory)) {
                Clients.showNotification("Лицо было успешно изменено!", Clients.NOTIFICATION_TYPE_INFO, null, null, 2000);

                signatories = signatoryEditorService.getSignatoryList(idOrderRule);
                filterSignatoryList();

                for (int i = 0; i < filteredSignatories.size(); i++) {
                    if (filteredSignatories.get(i).getIdLre() == editedSignatory.getIdLre())
                        lbSignatory.setSelectedIndex(i);
                }

                log.info("Пользователь " + template.getCurrentUser().getFio()
                        + " изменил " + ActionRuleConst.getName(editedSignatory.getRole()).getName()
                        + " " + editedSignatory.getEmployee().getFio()
                        + " по типу правила " + "\'" + editedSignatory.getRule() + "\'");
            } else {
                Clients.showNotification("Обновление не удалось!", Clients.NOTIFICATION_TYPE_ERROR, null, null, 2000);
            }
        } else {
            if (signatoryEditorService.addSignatory(editedSignatory)) {
                Clients.showNotification("Лицо было успешно добавлено", Clients.NOTIFICATION_TYPE_INFO, null, null, 2000);

                signatories = signatoryEditorService.getSignatoryList(idOrderRule);
                filterSignatoryList();

                log.info("Пользователь " + template.getCurrentUser().getFio()
                        + " добавил " + ActionRuleConst.getName(editedSignatory.getRole()).getName()
                        + " " + editedSignatory.getEmployee().getFio()
                        + " по типу правила " + "\'" + editedSignatory.getRule() + "\'");

                clearEditPanel();
            } else {
                Clients.showNotification("Добавление не удалось!", Clients.NOTIFICATION_TYPE_ERROR, null, null, 2000);
            }

        }

    }

    @Listen("onClick = #btnRemoveEmp")
    public void removeSignatory() {
        if (signatoryEditorService.deleteSignatory(filteredSignatories.get(lbSignatory.getSelectedIndex()).getIdLre())) {
            log.info("Пользователь " + template.getCurrentUser().getFio()
                    + " удалил " + ActionRuleConst.getName(filteredSignatories.get(lbSignatory.getSelectedIndex()).getRole()).getName()
                    + " " + filteredSignatories.get(lbSignatory.getSelectedIndex()).getEmployee().getFio()
                    + " по типу правила " + "\'" + editedSignatory.getRule() + "\'");
            filteredSignatories.remove(lbSignatory.getSelectedIndex()).getIdLre();
            ListModelList<SignatoryModel> lmSignatories = new ListModelList<>(filteredSignatories);
            lbSignatory.setAttribute("data", filteredSignatories);
            lbSignatory.setModel(lmSignatories);

            Clients.showNotification("Лицо было успешно удалено", Clients.NOTIFICATION_TYPE_INFO, null, null, 2000);

            clearEditPanel();

        } else {
            Clients.showNotification("Удаление не удалось!", Clients.NOTIFICATION_TYPE_ERROR, null, null, 2000);
        }
    }

}
