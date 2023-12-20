package org.edec.newMine.subjects.ctrl;

import lombok.extern.log4j.Log4j;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.DepartmentModel;
import org.edec.newMine.groups.model.GroupsModel;
import org.edec.newMine.groups.service.GroupsService;
import org.edec.newMine.groups.service.impl.GroupsServiceImpl;
import org.edec.newMine.subjects.ctrl.renderer.SubjectRenderer;
import org.edec.newMine.subjects.model.GroupsEsoModel;
import org.edec.newMine.subjects.model.SubjectsModel;
import org.edec.newMine.subjects.service.SubjectService;
import org.edec.newMine.subjects.service.impl.SubjectServiceImpl;
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

import java.util.*;

@Log4j
public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Combobox cmbFos, cmbSemester, cmbGroups, cmbInst;
    @Wire
    private Button btnSynchSubjectGroupByHours, btnSynchAllNotSynchSubject, btnSynchSubjectByGroup, btnGetNotSynchSubject, btnLinkTeacherForSubjects;
    @Wire
    private Listbox lbSubjectGroupMine, lbSubjecGroupASU;
    @Wire
    private Checkbox shortForm;

    private ComponentService componentService = new ComponentServiceESOimpl();
    public TemplatePageCtrl template = new TemplatePageCtrl();
    private SubjectService service = new SubjectServiceImpl();
    private GroupsService groupsService = new GroupsServiceImpl();
    private  List<SubjectsModel> subjectsASU = new ArrayList<>();
    private List<SubjectsModel> subjectsMine = new ArrayList<>();
    private List<DepartmentModel> departments;

    private FormOfStudy fos;
    private InstituteModel institute;
    private SemesterModel semester;
    private GroupsEsoModel selectedGroup;

    @Override
    protected void fill() throws InterruptedException {
        componentService.fillCmbInst(cmbInst, cmbInst, template.getCurrentModule().getDepartments(), true);
        componentService.fillCmbFormOfStudy(cmbFos, cmbFos, FormOfStudy.ALL.getType(), false);

    }

    @Listen("onAfterRender = #cmbInst; ")
    public void fillCmbSemester() {
        fos = cmbFos.getSelectedItem().getValue();
        institute = cmbInst.getSelectedItem().getValue();
        componentService.fillCmbSem(cmbSemester, institute.getIdInst(), fos.getType(), null);
        if (departments == null) {
            departments = service.getDepartments();
        }
    }

    @Listen("onChange = #cmbSemester;")
    public void fillCmbGroup() {
        cmbGroups.getItems().clear();
        semester = cmbSemester.getSelectedItem().getValue();
        cmbGroups.setModel(new ListModelList<>(service.getESOGroupsBySemester(semester.getIdSem())));
        
        lbSubjectGroupMine.getItems().clear();
        lbSubjecGroupASU.getItems().clear();
    }

    @Listen(" onChange = #cmbFos ")
    public void changeCmbGroupsAndSem(){
        cmbSemester.setSelectedIndex(-1);
        cmbGroups.setSelectedIndex(-1);
        lbSubjectGroupMine.getItems().clear();
        lbSubjecGroupASU.getItems().clear();
        fillCmbSemester();
    }

    @Listen("onChange =#cmbGroups")
    public void fillSubjectsListboxes() {
        lbSubjecGroupASU.getItems().clear();
        selectedGroup = cmbGroups.getSelectedItem().getValue();
        subjectsASU = service.getSubjectsByIdLGS(selectedGroup.getIdLGS());
        subjectsMine = service.getSubjectGroupMine(selectedGroup.getGroupname(), ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInstMine(),
                selectedGroup.getSemester(), selectedGroup.getCourse(), semester.getDateOfBegin(), fos.getType(), shortForm.isChecked());
        service.fillOtherSubjectsModelAndChair(subjectsASU, subjectsMine, departments);
        fillListboxSubjectsASU();
        filListboxSubjectsMine();

    }

    private void fillListboxSubjectsASU( ) {
        lbSubjecGroupASU.setModel(new ListModelList<>(subjectsASU));
        lbSubjecGroupASU.setItemRenderer(new SubjectRenderer(selectedGroup.getIdLGS(), this::fillSubjectsListboxes, false));
        lbSubjecGroupASU.renderAll();
    }

    private void filListboxSubjectsMine () {
        ListModelList<SubjectsModel> lmlMineSubjects = new ListModelList<>(subjectsMine);
        lmlMineSubjects.setMultiple(true);
        lbSubjectGroupMine.setModel(lmlMineSubjects);
        lbSubjectGroupMine.setCheckmark(true);
        lbSubjectGroupMine.setMultiple(true);
        lbSubjectGroupMine.setItemRenderer(new SubjectRenderer(selectedGroup.getIdLGS(), this::fillSubjectsListboxes, true));
        lbSubjectGroupMine.renderAll();
    }

    @Listen("onClick = #btnSynchSubjectGroupByHours")
    public void synchAllGroupSubjectByHours() {
        if (semester != null && selectedGroup != null) {
            //for (GroupsModel group : groupsService.getCabinetGroupsBySemester(semester.getIdSem())) {
                List<SubjectsModel> subjectsDEC = service.getSubjectsByIdLGS(selectedGroup.getIdLGS());
                List<SubjectsModel> subjectsMine = service.getSubjectGroupMine(selectedGroup.getGroupname(), ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInstMine(),
                        selectedGroup.getSemester(), selectedGroup.getCourse(), semester.getDateOfBegin(), fos.getType(), shortForm.isChecked());
                if (subjectsDEC.size() != 0) {
                    service.fillOtherSubjectsModelAndChair(subjectsDEC, subjectsMine, departments);
                    for (SubjectsModel subjectGroupDec : subjectsDEC) {
                        SubjectsModel otherSubject = subjectGroupDec.getOtherSubject();
                        if (otherSubject == null) {
                            continue;
                        }
                        if (!Objects.equals(subjectGroupDec.getSubjectcode(), otherSubject.getSubjectcode())) {
                            if (service.updateSubjectCode(subjectGroupDec.getIdSubj(), otherSubject.getSubjectcode())) {
                                log.info("Обновлен subject code " + subjectGroupDec.getSubjectname() + ", " + otherSubject.getSubjectcode());
                            }
                        }

                        if (subjectGroupDec.getHoursAudCount() == 0) {
                            if (service.updateAudSubject(subjectGroupDec, otherSubject)) {
                                log.info("Обновлены ауд.часы " + subjectGroupDec.getSubjectname() + ", " + otherSubject.getSubjectcode());
                            }
                        }
                    }
                }
         //   }
        } else {
            PopupUtil.showWarning("Выберите семестр и группу!");
        }

    }

    @Listen("onClick = #btnSynchSubjectByGroup")
    public void synchroGroupSubject() {

        if (selectedGroup != null) {
            if (!lbSubjectGroupMine.getSelectedItems().isEmpty()) {
                List<SubjectsModel> subjectsMineSelect = new ArrayList<>();
                for (Listitem item : lbSubjectGroupMine.getSelectedItems()) {
                    subjectsMineSelect.add(item.getValue());
                }
                service.createSubjects(subjectsMineSelect, selectedGroup.getIdLGS());
                fillSubjectsListboxes();
            } else {
                PopupUtil.showWarning("Выберите предметы!");
            }
        } else {
            PopupUtil.showWarning("Выберите группу!");
        }
    }

    @Listen("onClick= #btnLinkTeacherForSubjects")
    public void linckTeacherForSubjects(){
        if (selectedGroup != null) {
            service.linkTeachersForSubjects(subjectsMine, selectedGroup.getIdLGS());
            PopupUtil.showInfo("Преподаватели успешно прикреплены!");
        } else {
            PopupUtil.showWarning("Выберите группу!");
        }
    }

}
