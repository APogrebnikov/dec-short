package org.edec.reference.ctrl;

import lombok.extern.log4j.Log4j;
import org.edec.reference.ctrl.renderer.ReferenceListRenderer;
import org.edec.reference.ctrl.renderer.StudentListRenderer;
import org.edec.reference.model.ReferenceModel;
import org.edec.reference.model.StudentModel;
import org.edec.reference.service.ReferenceService;
import org.edec.reference.service.impl.ReferenceServiceImpl;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.ReferenceType;
import org.edec.utility.constants.ReferenceTypeConst;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Textbox tbStudentFioSearch;

    @Wire
    private Button btnStudentFioSearch, btnCreateOrphanReference, btnCreateDsppReference,
            btnSaveStatus, btnCreateInvalidReference, btnCreateVeteranReference,
            btnEditReference, btnDeleteReference;

    @Wire
    private Checkbox chbOrphan, chbInvalid, chbIndigent, chbVeteran;

    @Wire
    private Listbox lbStudents, lbReferences;

    @Wire
    private Datebox dateOfBirth;

    @Wire
    private Combobox cmbInvalidType, cmbInst;

    @Wire
    private Label lbStudentFio;

    private ReferenceService service = new ReferenceServiceImpl();

    private ComponentService componentService = new ComponentServiceESOimpl();

    private List<StudentModel> studentsList = new ArrayList<>();

    private List<ReferenceModel> referencesList = new ArrayList<>();

    private ListModelList<StudentModel> studentModels;

    private ListModelList<ReferenceModel> referenceModels;

    private StudentModel student;

    private Runnable updateReferencesList = this::fillReferencesList;

    private InstituteModel currentInstitute;

    @Override
    protected void fill() throws InterruptedException {
        currentInstitute = componentService.fillCmbInst(cmbInst, cmbInst, currentModule.getDepartments());
    }

    @Listen("onClick=#btnStudentFioSearch;onOK=#tbStudentFioSearch")
    public void findStudents() {
        student = null;
        clearStudentInformation();
        fillStudentsList();

        lbReferences.getItems().clear();
    }

    private void fillStudentsList() {
        studentsList = service.getStudents(tbStudentFioSearch.getText(), currentInstitute.getIdInst());

        studentModels = new ListModelList<>(studentsList);
        lbStudents.setModel(studentModels);
        lbStudents.setItemRenderer(new StudentListRenderer());
        lbStudents.renderAll();
    }

    @Listen("onSelect = #lbStudents")
    public void getSelectedStudent() {
        student = lbStudents.getSelectedItem().getValue();

        fillReferencesList();

        clearStudentInformation();
        fillStudentInformation(student);
    }

    @Listen("onSelect = #lbReferences")
    public void getSelectedReference() {
        btnEditReference.setDisabled(false);
        btnDeleteReference.setDisabled(false);
    }

    private void fillReferencesList() {
        referencesList = service.getReferences(student.getIdStudentcard());

        referenceModels = new ListModelList<>(referencesList);
        lbReferences.setModel(referenceModels);
        lbReferences.setItemRenderer(new ReferenceListRenderer());
        lbReferences.renderAll();

        btnEditReference.setDisabled(true);
        btnDeleteReference.setDisabled(true);
    }

    private void fillStudentInformation(StudentModel student) {
        lbStudentFio.setValue(student.getFio());
        dateOfBirth.setValue(student.getDateOfBirth());

        if (student.getOrphan()) {
            dateOfBirth.setVisible(true);
            chbOrphan.setChecked(true);
        }

        if (student.getInvalid()) {
            cmbInvalidType.setSelectedIndex(student.getInvalidType() - 1);

            chbInvalid.setChecked(true);
            cmbInvalidType.setVisible(true);
            btnCreateInvalidReference.setDisabled(false);
        }

        if (student.getIndigent()) {
            chbIndigent.setChecked(true);
            btnCreateDsppReference.setDisabled(false);
        }

        if (student.getVeteran()) {
            chbVeteran.setChecked(true);
            btnCreateVeteranReference.setDisabled(false);
        }
    }

    private void clearStudentInformation() {
        dateOfBirth.setValue(null);
        cmbInvalidType.setSelectedIndex(0);
        lbStudentFio.setValue("");

        chbOrphan.setChecked(false);
        chbInvalid.setChecked(false);
        chbIndigent.setChecked(false);
        chbVeteran.setChecked(false);

        btnCreateInvalidReference.setDisabled(true);
        btnCreateDsppReference.setDisabled(true);
        btnEditReference.setDisabled(true);
        btnCreateVeteranReference.setDisabled(true);
        btnDeleteReference.setDisabled(true);

        dateOfBirth.setVisible(false);
        cmbInvalidType.setVisible(false);
    }

    @Listen("onCheck = #chbOrphan")
    public void setChbOrphan() {
        if (chbOrphan.isChecked()) {
            dateOfBirth.setVisible(true);
            btnCreateOrphanReference.setDisabled(false);
        } else {
            dateOfBirth.setVisible(false);
            btnCreateOrphanReference.setDisabled(true);
        }
    }

    @Listen("onCheck = #chbIndigent")
    public void setChbIndigent() {
        if (chbIndigent.isChecked()) {
            btnCreateDsppReference.setDisabled(false);
        } else {
            btnCreateDsppReference.setDisabled(true);
        }
    }

    @Listen("onCheck = #chbInvalid")
    public void setChbInvalid() {
        if (chbInvalid.isChecked()) {
            btnCreateInvalidReference.setDisabled(false);
            cmbInvalidType.setVisible(true);
        } else {
            btnCreateInvalidReference.setDisabled(true);
            cmbInvalidType.setVisible(false);
        }
    }

    @Listen("onCheck = #chbVeteran")
    public void setChbVeteran() {
        if (chbVeteran.isChecked()) {
            btnCreateVeteranReference.setDisabled(false);
        } else {
            btnCreateVeteranReference.setDisabled(true);
        }
    }

    @Listen("onClick = #btnSaveStatus")
    public void saveStudentStatus() {
        if (lbStudents.getSelectedIndex() != -1) {
            if (chbOrphan.isChecked()) {
                if (!service.updateStudentDateOfBirth(student.getIdHumanface(), dateOfBirth.getValue())) {
                    PopupUtil.showError("Обновить дату рождения не удалось!");
                }
            }
            if (!service.updateStudentStatus(chbInvalid.isChecked(), chbIndigent.isChecked(), chbOrphan.isChecked(), chbVeteran.isChecked(),
                    chbInvalid.isChecked() ? cmbInvalidType.getSelectedIndex() + 1 : 0, student.getIdStudentcard()
            )) {

                PopupUtil.showError("Обновить статус не удалось!");
            } else {
                log.info("Пользователь " + template.getCurrentUser().getFio() + " изменил статус у студента " + student.getFio() + ":" +
                        " инвалид - " + chbInvalid.isChecked() + ";" + " малоимущий - " + chbIndigent.isChecked() + ";" + " cирота - " +
                        chbOrphan.isChecked() + ";" +
                        (chbInvalid.isChecked() ? " тип инвалидности - " + cmbInvalidType.getSelectedIndex() : ""));

                PopupUtil.showInfo("Статус был успешно обновлен!");

                fillStudentsList();
                clearStudentInformation();
            }
        } else {
            PopupUtil.showWarning("Сначала выберите студента!");
        }
    }

    @Listen("onClick = #btnCreateOrphanReference")
    public void createOrphanReference() {
        ReferenceModel reference = new ReferenceModel();
        reference.setIdStudentcard(student.getIdStudentcard());

        if (lbStudents.getSelectedIndex() != -1) {
            openEditReferenceWindow(ReferenceTypeConst.REFERENCE_ORPHAN_CREATE, reference);
        } else {
            PopupUtil.showWarning("Сначала выберите студента!");
        }
    }

    @Listen("onClick = #btnCreateDsppReference")
    public void createDsppReference() {
        ReferenceModel reference = new ReferenceModel();
        reference.setIdStudentcard(student.getIdStudentcard());

        if (lbStudents.getSelectedIndex() != -1) {
            openEditReferenceWindow(ReferenceTypeConst.REFERENCE_DSPP_CREATE, reference);
        } else {
            PopupUtil.showWarning("Сначала выберите студента!");
        }
    }

    @Listen("onClick = #btnCreateInvalidReference")
    public void createInvalidReference() {
        ReferenceModel reference = new ReferenceModel();
        reference.setIdStudentcard(student.getIdStudentcard());

        if (lbStudents.getSelectedIndex() != -1) {
            openEditReferenceWindow(ReferenceTypeConst.REFERENCE_INVALID_CREATE, reference);
        } else {
            PopupUtil.showWarning("Сначала выберите студента!");
        }
    }

    @Listen("onClick = #btnCreateVeteranReference")
    public void createVeteranReference() {
        ReferenceModel reference = new ReferenceModel();
        reference.setIdStudentcard(student.getIdStudentcard());

        if (lbStudents.getSelectedIndex() != -1) {
            openEditReferenceWindow(ReferenceTypeConst.REFERENCE_VETERAN_CREATE, reference);
        } else {
            PopupUtil.showWarning("Сначала выберите студента!");
        }
    }

    @Listen("onClick = #btnEditReference")
    public void editReference() {
        ReferenceModel reference = lbReferences.getSelectedItem().getValue();

        String windowType = "";
        if (reference.getRefType() == ReferenceType.INDIGENT.getValue()) {
            windowType = ReferenceTypeConst.REFERENCE_DSPP_EDIT;
        } else if (reference.getRefType() == ReferenceType.ORPHAN.getValue()) {
            windowType = ReferenceTypeConst.REFERENCE_ORPHAN_EDIT;
        } else if (reference.getRefType() == ReferenceType.INVALID.getValue()) {
            windowType = ReferenceTypeConst.REFERENCE_INVALID_EDIT;
        } else if (reference.getRefType() == ReferenceType.VETERAN.getValue()) {
            windowType = ReferenceTypeConst.REFERENCE_VETERAN_EDIT;
        }

        openEditReferenceWindow(windowType, reference);
    }

    @Listen("onClick = #btnDeleteReference")
    public void deleteReference() {
        ReferenceModel reference = lbReferences.getSelectedItem().getValue();

        service.deleteFiles(reference);

        if (service.deleteReference(reference.getIdRef())) {
            PopupUtil.showInfo("Справка успешно удалена!");

            log.info("Пользователь " + template.getCurrentUser().getFio() + " удалил справку " +
                    ((reference.getRefType() == ReferenceType.INDIGENT.getValue()) ? "УСЗН" : "об инвалидности") + " у студента " +
                    student.getFio());
        } else {
            PopupUtil.showError("Удаление справки не удалось!");
        }

        fillReferencesList();
    }

    private void openEditReferenceWindow(String windowType, ReferenceModel reference) {
        reference.setIdSemester(student.getIdSemester());

        Map arg = new HashMap();

        arg.put(WinEditReferenceCtrl.REFERENCE, reference);
        arg.put(WinEditReferenceCtrl.WINDOW_TYPE, windowType);
        arg.put(WinEditReferenceCtrl.UPDATE_REFERENCES_LIST, updateReferencesList);
        arg.put(WinEditReferenceCtrl.CURRENT_USER, template.getCurrentUser().getFio());
        arg.put(WinEditReferenceCtrl.INSTITUTE, currentInstitute.getIdInst());
        arg.put(WinEditReferenceCtrl.STUDENT, student.getFio());

        ComponentHelper.createWindow("/reference/winEditReference.zul", "winEditReference", arg).doModal();
    }

    @Listen("onClick = #btnExcelReport")
    public void saveExcelReport() {
        try {
            service.writeIntoExcel();
            PopupUtil.showInfo("Файл успешно создан!");
        } catch (Exception e) {
            e.printStackTrace();
            PopupUtil.showError("Ошибка создания отчета. Обратитесь к администратору.");
        }
    }
}
