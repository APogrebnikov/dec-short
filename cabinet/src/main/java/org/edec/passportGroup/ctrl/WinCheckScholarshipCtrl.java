package org.edec.passportGroup.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.passportGroup.ctrl.renderer.CheckScholarshipRenderer;
import org.edec.passportGroup.model.GroupModel;
import org.edec.passportGroup.model.SemesterModel;
import org.edec.passportGroup.model.StudentModel;
import org.edec.passportGroup.model.SubjectModel;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.passportGroup.service.impl.PassportGroupServiceESO;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;


public class WinCheckScholarshipCtrl extends CabinetSelector {
    @Wire
    private Combobox cmbInst, cmbFOC, cmbSem, cmbGroup;

    @Wire
    private Listbox lbReport;

    private FormOfStudy currentFOS;
    private InstituteModel currentInstitute;
    private SemesterModel currentSemester;
    private String fioUser;
    private Long idUser;

    private ComponentService componentService = new ComponentServiceESOimpl();
    private PassportGroupService passportGroupService = new PassportGroupServiceESO();

    @Override
    protected void fill () {
        idUser = new TemplatePageCtrl().getCurrentUser().getIdHum();
        fioUser = new TemplatePageCtrl().getCurrentUser().getFio();
        currentInstitute = componentService.fillCmbInst(cmbInst, cmbInst, currentModule.getDepartments());
        currentFOS = componentService.fillCmbFormOfStudy(cmbFOC, cmbFOC, currentModule.getFormofstudy());

        cmbSem.setItemRenderer((Comboitem comboitem, SemesterModel semesterModel, int i) -> {
            comboitem.setValue(semesterModel);
            comboitem.setLabel(semesterModel.getFullName());
        });

        cmbGroup.setItemRenderer((Comboitem comboitem, GroupModel groupModel, int i) -> {
            comboitem.setValue(groupModel);
            comboitem.setLabel(groupModel.getGroupName());
        });

        fillCmbSemesters();
    }

    @Listen("onChange = #cmbInst; onChange = #cmbFOC;")
    public void fillCmbSemesters () {
        if (cmbInst.getSelectedItem() != null && cmbFOC.getSelectedItem() != null) {
            currentInstitute = cmbInst.getSelectedItem().getValue();
            currentFOS = cmbFOC.getSelectedItem().getValue();
        }

        List<SemesterModel> listSemester = passportGroupService.getSemestersByParams(currentInstitute.getIdInst(), 0, currentFOS.getType());
        cmbSem.setModel(new ListModelList<>(listSemester));
    }

    @Listen("onChange = #cmbSem;")
    public void fillCmbGroups () {
        if (cmbSem.getSelectedItem() != null) {
            currentSemester = cmbSem.getSelectedItem().getValue();
            List<GroupModel> listGroups = passportGroupService.getGroupsByFilter(currentSemester.getIdSemester(), 0, "", true, true, true, null);

            cmbGroup.setModel(new ListModelList<>(listGroups));
            cmbGroup.setSelectedIndex(-1);
        }
    }

    @Listen("onChange = #cmbGroup; onOK = #cmbGroup;")
    public void fillReport () {
        if (cmbGroup.getSelectedIndex() != -1) {
            GroupModel selectedGroup = cmbGroup.getSelectedItem().getValue();
            List<SubjectModel> subjects = passportGroupService.getSubjectsByGroup(selectedGroup.getIdLgs());
            List<StudentModel> students = passportGroupService.getStudentsByGroup(selectedGroup.getIdLgs());
            passportGroupService.setScholarshipInfoForListStudents(students);
            students.add(0, new StudentModel());
            students.add(0, new StudentModel());

            for (SubjectModel subject : subjects) {
                if (subject.getIdLgss() != null) {
                    subject.setEmployeeModels(passportGroupService.getEmployeesBySubject(subject.getIdLgss()));
                }
            }

            passportGroupService.setStatisticForSubjects(students, subjects);

            lbReport.setItemRenderer(new CheckScholarshipRenderer(subjects, selectedGroup, fioUser, idUser, this::fillReport));
            lbReport.setModel(new ListModelList<>(students));
            lbReport.renderAll();
        }
    }
}
