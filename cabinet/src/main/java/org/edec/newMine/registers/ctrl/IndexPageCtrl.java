package org.edec.newMine.registers.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.newMine.registers.ctrl.renderer.RegisterSubjectsRenderer;
import org.edec.newMine.registers.service.RegisterService;
import org.edec.newMine.registers.service.impl.RegisterServiceImpl;
import org.edec.utility.component.model.GroupModel;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

public class IndexPageCtrl extends CabinetSelector {
    @Wire
    private Combobox cmbFos, cmbSemester, cmbGroups, cmbInst;
    @Wire
    private Listbox lbSubjects, lbRatings;

    private ComponentService componentService = new ComponentServiceESOimpl();
    public TemplatePageCtrl template = new TemplatePageCtrl();

    private FormOfStudy fos;
    private InstituteModel institute;
    private SemesterModel semester;
    private GroupModel selectedGroup;

    private RegisterService service = new RegisterServiceImpl();

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
    }

    @Listen("onChange = #cmbSemester; ")
    public void fillCmbGroup() {
        semester = cmbSemester.getSelectedItem().getValue();
        componentService.fillCmbGroups(cmbGroups, semester.getIdSem());

        cmbGroups.setSelectedIndex(-1);
        lbSubjects.getItems().clear();
        lbRatings.getItems().clear();
    }
    @Listen(" onChange = #cmbFos ")
    public void changeCmbGroupsAndSem(){
        cmbSemester.setSelectedIndex(-1);
        cmbGroups.setSelectedIndex(-1);
        lbSubjects.getItems().clear();
        lbRatings.getItems().clear();
        fillCmbSemester();
    }

    @Listen("onChange = #cmbGroups")
    public void fillSubjectsListbox() {
        selectedGroup = cmbGroups.getSelectedItem().getValue();
        lbSubjects.setModel(new ListModelList<>(service.getSubjectsGroup(selectedGroup, semester)));
        lbSubjects.setItemRenderer(new RegisterSubjectsRenderer(lbRatings));
        lbSubjects.renderAll();
    }
}
