package org.edec.chairEmployee.ctrl;

import org.edec.chairEmployee.ctrl.renderer.DepartmentEmployeeRenderer;
import org.edec.chairEmployee.ctrl.renderer.EmployeePostRenderer;
import org.edec.chairEmployee.ctrl.renderer.EmployeeRenderer;
import org.edec.chairEmployee.model.PostModel;
import org.edec.chairEmployee.service.ChairEmployeeService;
import org.edec.chairEmployee.service.impl.ChairEmployeeImpl;
import org.edec.main.model.DepartmentModel;
import org.edec.utility.component.model.EmployeeModel;
import org.edec.utility.component.renderer.DepartmentRenderer;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;


public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Button btnAddPostForEmployee;
    @Wire
    private Combobox cmbSex, cmbPost;
    @Wire
    private Hbox hbEmpAction,hbHasSign;
    // TODO REFACTOR переименовать переменные в соответствии с требованиями к разработке
    @Wire
    private Label lStatusEmployee, lChoosenDepartment;
    @Wire
    private Listbox lbEmployee, lbDepartment, lbEmployeeDepartment, lbDepartmentForEmployee, lbEmployeePost;
    @Wire
    private Textbox tbFamily, tbName, tbPatronymic, tbLdap, tbSearchEmployee, tbSearchInDepartment, tbSearchDepartmentForEmployee, tbEmail;

    //Сервисы
    private ChairEmployeeService chairEmployeeService = new ChairEmployeeImpl();

    //Переменные
    private List<DepartmentModel> departments;
    private List<PostModel> posts;

    protected void fill () {
        lbEmployee.setItemRenderer(new EmployeeRenderer());
        lbDepartment.setItemRenderer(new DepartmentRenderer());
        lbEmployeePost.setItemRenderer(new EmployeePostRenderer());
        lbDepartmentForEmployee.setItemRenderer(new DepartmentRenderer());
        posts = chairEmployeeService.getPosts();
        departments = chairEmployeeService.getDepartments();
        lbDepartment.setModel(new ListModelList<>(departments));
        lbDepartment.renderAll();
        lbDepartmentForEmployee.setModel(new ListModelList<>(departments));
        cmbPost.setModel(new ListModelList<>(posts));
        cmbPost.setItemRenderer((ComboitemRenderer<PostModel>) (comboitem, postModel, i) -> {
            comboitem.setValue(postModel);
            comboitem.setLabel(postModel.getPost());
        });
    }

    @Listen("onOK = #tbSearchEmployee")
    public void searchEmployee () {
        if (tbSearchEmployee.getValue().equals("")) {
            PopupUtil.showWarning("Напишите ФИО сотрудника");
            return;
        }
        lbEmployee.setModel(new ListModelList<>(chairEmployeeService.getEmployeeByFilter(tbSearchEmployee.getValue(), null)));
        lbEmployee.renderAll();
    }

    @Listen("onClick = #btnAddNewEmployee")
    public void addNewEmployee () {
        lStatusEmployee.setValue("Создание нового сотрудника");
        tbFamily.focus();
        tbFamily.setValue("");
        tbName.setValue("");
        tbEmail.setValue("");
        tbPatronymic.setValue("");
        cmbSex.setSelectedIndex(-1);
        tbLdap.setValue("");
        hbEmpAction.getChildren().clear();
        hbHasSign.getChildren().clear();
        cmbPost.setDisabled(true);
        lbDepartmentForEmployee.setDisabled(true);
        btnAddPostForEmployee.setDisabled(true);
        lbEmployeePost.getItems().clear();
        Checkbox chbHasSign = new Checkbox();
        chbHasSign.setParent(hbHasSign);
        chbHasSign.setChecked(true);

        Button btnCreateEmployee = new Button("Создать");
        btnCreateEmployee.setParent(hbEmpAction);
        btnCreateEmployee.addEventListener(Events.ON_CLICK, event -> {
            if (!checkFilling()) {
                PopupUtil.showWarning("Заполните все поля по сотруднику!");
                return;
            }
            Long idEmp = chairEmployeeService.createEmployee(
                    tbFamily.getValue(), tbName.getValue(), tbPatronymic.getValue(), cmbSex.getSelectedIndex(), tbLdap.getValue(), chbHasSign.isChecked());
            if (idEmp == null) {
                PopupUtil.showError("Не удалось создать сотрудника");
                return;
            }
            refreshListBoxEmployeeByIdEmpAndSelectEmp(idEmp);
        });
    }

    private void refreshListBoxEmployeeByIdEmpAndSelectEmp (Long idEmp) {
        List<EmployeeModel> tmpEmployees = chairEmployeeService.getEmployeeByFilter("", idEmp);
        lbEmployee.setModel(new ListModelList<>(tmpEmployees));
        lbEmployee.renderAll();
        if (tmpEmployees.size() > 0) {
            lbEmployee.setSelectedIndex(0);
            tbSearchEmployee.setValue(tmpEmployees.get(0).getFIO());
        }
        Events.echoEvent(Events.ON_SELECT, lbEmployee, null);
    }

    @Listen("onSelect = #lbEmployee")
    public void selectEmployee () {
        final EmployeeModel selectedEmployee = lbEmployee.getSelectedItem().getValue();
        lStatusEmployee.setValue("Обновление данных сотрудника");
        tbFamily.setValue(selectedEmployee.getFamily());
        tbName.setValue(selectedEmployee.getName());
        tbEmail.setValue(selectedEmployee.getEmail());
        tbPatronymic.setValue(selectedEmployee.getPatronymic());
        cmbSex.setSelectedIndex(selectedEmployee.getSex());
        tbLdap.setValue(selectedEmployee.getLoginLdap());
        hbEmpAction.getChildren().clear();
        hbHasSign.getChildren().clear();

        Checkbox chbHasSign = new Checkbox();
        chbHasSign.setParent(hbHasSign);

        if (selectedEmployee.getIdEmp() != null) {
            Button btnSave = new Button("Сохранить");
            btnSave.setParent(hbEmpAction);
            chbHasSign.setChecked(selectedEmployee.getHasSign());
            btnSave.addEventListener(Events.ON_CLICK, event -> {
                if (!checkFilling()) {
                    PopupUtil.showWarning("Заполните все поля по сотруднику!");
                    return;
                }
                selectedEmployee.setFamily(tbFamily.getValue());
                selectedEmployee.setName(tbName.getValue());
                selectedEmployee.setPatronymic(tbPatronymic.getValue());
                selectedEmployee.setSex(cmbSex.getSelectedIndex());
                selectedEmployee.setLoginLdap(tbLdap.getValue());
                selectedEmployee.setEmail(tbEmail.getValue());
                selectedEmployee.setHasSign(chbHasSign.isChecked());
                if (chairEmployeeService.updateParamsHum(selectedEmployee)) {
                    PopupUtil.showInfo("Данные сотрудника успешно обновлены!");
                } else {
                    PopupUtil.showError("Данные по сотруднику не удалось обновить");
                }
            });
            cmbPost.setDisabled(false);
            btnAddPostForEmployee.setDisabled(false);
            lbDepartmentForEmployee.setDisabled(false);
            lbEmployeePost.setModel(new ListModelList<>(chairEmployeeService.getPostsByIdEmp(selectedEmployee.getIdEmp())));
            lbEmployeePost.renderAll();
        } else {
            cmbPost.setDisabled(true);
            lbDepartmentForEmployee.setDisabled(true);
            btnAddPostForEmployee.setDisabled(true);
            lbEmployeePost.getItems().clear();
            Button btnAddEmployee = new Button("Сделать сотрудником");
            btnAddEmployee.setParent(hbEmpAction);
            btnAddEmployee.addEventListener(Events.ON_CLICK, event -> {
                if (!checkFilling()) {
                    PopupUtil.showWarning("Заполните все поля по сотруднику!");
                    return;
                }
                Long idEmp = chairEmployeeService.createEmployeeByHum(tbLdap.getValue(), selectedEmployee.getIdHum());
                if (idEmp == null) {
                    PopupUtil.showError("Не удалось сохранить сотрудника");
                    return;
                }
                refreshListBoxEmployeeByIdEmpAndSelectEmp(idEmp);
            });
        }
    }

    @Listen("onOK = #tbSearchInDepartment")
    public void findDepartmentsByFulltitle () {
        lbDepartment.setModel(new ListModelList<>(chairEmployeeService.getDepartmentsByName(tbSearchInDepartment.getValue(), departments)));
        lbDepartment.renderAll();
    }

    // TODO REFACTOR переименовать метод в соответствии с требованиями к разработке
    @Listen("onSelect = #lbDepartment")
    public void selectedDepartment () {
        DepartmentModel department = lbDepartment.getSelectedItem().getValue();
        lChoosenDepartment.setValue("Выбранная кафедра: " + department.getFulltitle());
        lbEmployeeDepartment.setItemRenderer(new DepartmentEmployeeRenderer(posts));
        lbEmployeeDepartment.setModel(new ListModelList<>(chairEmployeeService.getEmployeeByDepartment(department.getIdDepartment())));
        lbEmployeeDepartment.renderAll();
    }

    @Listen("onOK = #tbSearchDepartmentForEmployee")
    public void searchDepartmentForEmployee () {
        lbDepartmentForEmployee.setModel(
                new ListModelList<>(chairEmployeeService.getDepartmentsByName(tbSearchDepartmentForEmployee.getValue(), departments)));
        lbDepartmentForEmployee.renderAll();
    }

    @Listen("onClick = #btnAddPostForEmployee")
    public void addPostForEmployee () {
        if (cmbPost.getSelectedItem() == null || lbDepartmentForEmployee.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите должность и кафедру");
            return;
        }
        final EmployeeModel selectedEmployee = lbEmployee.getSelectedItem().getValue();
        final PostModel selectedPost = cmbPost.getSelectedItem().getValue();
        final DepartmentModel selectedDepartment = lbDepartmentForEmployee.getSelectedItem().getValue();
        DialogUtil.questionWithYesNoButtons(
                "Вы действительно хотите добавить должность \"" + selectedPost.getPost() + "\" на кафедре \"" +
                selectedDepartment.getFulltitle() + "\", сотруднику " + selectedEmployee.getFIO() + "?", "Внимание!", event -> {
                    if (event.getName().equals(DialogUtil.ON_YES)) {
                        if (chairEmployeeService.createLED(
                                selectedEmployee.getIdEmp(), selectedPost.getIdPost(), selectedDepartment.getIdDepartment())) {
                            PopupUtil.showInfo("Должность успешно добавлена");
                            lbEmployeePost.setModel(new ListModelList<>(chairEmployeeService.getPostsByIdEmp(selectedEmployee.getIdEmp())));
                            lbEmployeePost.renderAll();
                        } else {
                            PopupUtil.showError("Не удалось добавить должность");
                        }
                    }
                });
    }

    private boolean checkFilling () {
        return !tbFamily.getValue().equals("") && !tbName.getValue().equals("") && !tbPatronymic.getValue().equals("") &&
               cmbSex.getSelectedIndex() != -1 && !tbLdap.getValue().equals("");
    }
}