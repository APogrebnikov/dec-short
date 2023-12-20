package org.edec.newMine.students.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.newMine.students.ctrl.renderer.StudentsRenderer;
import org.edec.newMine.students.model.StudentsModel;
import org.edec.newMine.students.service.StudentsService;
import org.edec.newMine.students.service.impl.StudentsServiceImpl;
import org.edec.utility.component.model.GroupModel;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;

public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Combobox cmbFos, cmbSemester, cmbGroups, cmbInst;
    @Wire
    private Button btnUpdateStudent, btnUpdateHash, btnCreateStudent, btnDeleteStudent;
    @Wire
    private Listbox lbStudentsFromMine, lbStudentsFromASU;


    private ComponentService componentService = new ComponentServiceESOimpl();
    public TemplatePageCtrl template = new TemplatePageCtrl();

    private StudentsService studentsService = new StudentsServiceImpl();

    private FormOfStudy fos;
    private InstituteModel institute;
    private SemesterModel semester;
    private GroupModel selectedGroup;

    private List<StudentsModel> studentsASU;
    private List<StudentsModel> studentsMine;


    @Override
    protected void fill() throws InterruptedException {
        componentService.fillCmbInst(cmbInst, cmbInst, template.getCurrentModule().getDepartments(), true);
        componentService.fillCmbFormOfStudy(cmbFos, cmbFos, FormOfStudy.ALL.getType(), false);
        btnCreateStudent.setVisible(false);
    }

    @Listen("onAfterRender = #cmbInst; ")
    public void fillCmbSemester() {
        fos = cmbFos.getSelectedItem().getValue();
        institute = cmbInst.getSelectedItem().getValue();
        componentService.fillCmbSem(cmbSemester, institute.getIdInst(), fos.getType(), null);
    }

    @Listen("onChange = #cmbSemester; ")
    public void fillCmbGroup() {
        cmbGroups.getItems().clear();
        semester = cmbSemester.getSelectedItem().getValue();
        componentService.fillCmbGroups(cmbGroups, semester.getIdSem());

        cmbGroups.setSelectedIndex(-1);
        lbStudentsFromASU.getItems().clear();
        lbStudentsFromMine.getItems().clear();
    }
    @Listen(" onChange = #cmbFos ")
    public void changeCmbGroupsAndSem(){
        cmbSemester.setSelectedIndex(-1);
        cmbGroups.setSelectedIndex(-1);
        lbStudentsFromASU.getItems().clear();
        lbStudentsFromMine.getItems().clear();
        fillCmbSemester();
    }

    @Listen("onChange = #cmbGroups;")
    public void fillStudentsListbox() {
        if (cmbGroups.getSelectedIndex() == -1) {
            lbStudentsFromASU.getItems().clear();
            lbStudentsFromMine.getItems().clear();
            return;
        }
        lbStudentsFromASU.getItems().clear();
        lbStudentsFromMine.getItems().clear();
        selectedGroup = cmbGroups.getSelectedItem().getValue();
        studentsASU = studentsService.getStudentsFromESO(selectedGroup.getIdLGS());
        studentsMine = studentsService.getStudentsFromDBO(selectedGroup.getGroupname());

        for (StudentsModel studentModelMine : studentsMine) {
            studentModelMine.setGroupname(selectedGroup.getGroupname());
            for (StudentsModel studentModelEso : studentsASU) {
                if (studentModelEso.getOtherStudentModel() != null) {
                    continue;
                }
                if (studentModelMine.getIdStudCardMine().equals(studentModelEso.getIdStudCardMine())) {
                    studentModelEso.setOtherStudentModel(studentModelMine);
                    studentModelMine.setOtherStudentModel(studentModelEso);
                    break;
                } else if (studentModelMine.getFio().equals(studentModelEso.getFio())) {
                    studentModelEso.setOtherStudentModel(studentModelMine);
                    studentModelMine.setOtherStudentModel(studentModelEso);
                    break;
                }
            }
        }

        fillListboxStudentESO();
        fillListboxStudentMine();
    }

    private void fillListboxStudentESO() {
        ListModelList lmlStudentsEso = new ListModelList<>(studentsASU);
        lmlStudentsEso.setMultiple(true);
        lbStudentsFromASU.setItemRenderer(new StudentsRenderer(this::fillStudentsListbox, false));
        lbStudentsFromASU.setMultiple(true);
        lbStudentsFromASU.setCheckmark(true);
        lbStudentsFromASU.setAttribute("data", studentsASU);
        lbStudentsFromASU.setModel(lmlStudentsEso);
        lbStudentsFromASU.renderAll();
    }

    private void fillListboxStudentMine() {
        lbStudentsFromMine.setModel(new ListModelList<>(studentsMine));
        lbStudentsFromMine.setItemRenderer(new StudentsRenderer(this::fillStudentsListbox, true));
        lbStudentsFromMine.renderAll();
    }

    @Listen("onClick = #btnUpdateStudent")
    public void updateAllStudents() {
        if (cmbGroups.getSelectedIndex() == -1) {
            PopupUtil.showWarning("Сначала выберите группу");
            return;
        }
        studentsService.refreshStudentEsoByGroup(studentsMine, selectedGroup.getGroupname());
        fillStudentsListbox();
    }

    @Listen("onClick = #btnUpdateHash")
    public void updateAllStudentsHash() {
        if (cmbGroups.getSelectedIndex() == -1) {
            PopupUtil.showWarning("Сначала выберите группу");
            return;
        }
        studentsService.updateStudentsHashByGroup(studentsMine, selectedGroup.getGroupname());
        PopupUtil.showInfo("hash успешно обновлен");
        fillStudentsListbox();
    }

    @Listen("onClick = #btnDeleteStudent")
    public void deleteEsoStudents() {
        if (lbStudentsFromASU.getSelectedCount() != 0) {
            int i = 0;
            for (Listitem item : lbStudentsFromASU.getSelectedItems()) {
                StudentsModel selectedStudent = item.getValue();
                if (studentsService.deleteSSS(selectedStudent.getIdSSS())) {
                    i++;
                }
            }
            if (i != 0) {
                PopupUtil.showInfo("Успешно удалено студентов :" + i);
            }
            fillStudentsListbox();
        } else {
            PopupUtil.showWarning("Выберите студентов для удаления!");
        }

    }
}
