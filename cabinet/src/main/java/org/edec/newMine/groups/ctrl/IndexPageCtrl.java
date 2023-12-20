package org.edec.newMine.groups.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.newMine.groups.ctrl.render.GroupsRenderer;
import org.edec.newMine.groups.model.GroupsModel;
import org.edec.newMine.groups.service.GroupsService;
import org.edec.newMine.groups.service.impl.GroupsServiceImpl;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import java.util.List;

public class IndexPageCtrl  extends CabinetSelector {
    @Wire
    private Listbox lbGroupsFromMine, lbGroupsFromASU;
    @Wire
    private Combobox cmbFos, cmbSemester, cmbInst;
    @Wire
    private Button btnTransferGroupsInAutumnSem;

    private ComponentService componentService = new ComponentServiceESOimpl();
    public TemplatePageCtrl template = new TemplatePageCtrl();
    private FormOfStudy fos;
    private InstituteModel institute;
    private SemesterModel semester;
    private List<GroupsModel> groupsMine;
    private List<GroupsModel> groupsASU;

    private GroupsService groupsService = new GroupsServiceImpl();

    @Override
    protected void fill() throws InterruptedException {
        componentService.fillCmbInst(cmbInst, cmbInst, template.getCurrentModule().getDepartments(), true);
        componentService.fillCmbFormOfStudy(cmbFos, cmbFos, FormOfStudy.ALL.getType(), false);
    }

    @Listen("onAfterRender = #cmbInst; onChange = #cmbFos ")
    public void fillCmbSemester() {
        fos = cmbFos.getSelectedItem().getValue();
        institute = cmbInst.getSelectedItem().getValue();
        componentService.fillCmbSem(cmbSemester, institute.getIdInst(), fos.getType(), null);
        cmbSemester.setSelectedIndex(-1);

        lbGroupsFromMine.getItems().clear();
        lbGroupsFromASU.getItems().clear();
    }

    @Listen("onChange = #cmbSemester; ")
    public void fillCmbGroup() {
        semester = cmbSemester.getSelectedItem().getValue();
        if (semester!= null) {
            fillListboxesGroup();
        }

    }

    private void fillListboxesGroup() {
        groupsMine = groupsService.getMineGroupsByInstSemesterAndFormOfStudy(institute.getIdInstMine(), semester, fos.getType());
        groupsASU = groupsService.getCabinetGroupsBySemester(semester.getIdSem());

        for (GroupsModel groupMine : groupsMine) {
            for (GroupsModel groupASU : groupsASU) {
                if (groupASU.getLinkedGroup() != null) {
                    continue;
                }
                if (groupASU.getGroupname().equals(groupMine.getGroupname())) {
                    groupASU.setLinkedGroup(groupMine);
                    groupMine.setLinkedGroup(groupASU);
                    break;
                }
            }
        }

        fillListboxMine();
        fillListboxASU();
    }

    private void fillListboxMine() {
        lbGroupsFromMine.setModel(new ListModelList<>(groupsMine));
        lbGroupsFromMine.setItemRenderer(new GroupsRenderer(this::fillListboxesGroup, true, semester, institute));
        lbGroupsFromMine.renderAll();
    }

    private void fillListboxASU() {
        lbGroupsFromASU.setModel(new ListModelList<>(groupsASU));
        lbGroupsFromASU.setItemRenderer(new GroupsRenderer(this::fillListboxesGroup, false, semester, institute));
        lbGroupsFromASU.renderAll();
    }

    @Listen("onClick = #btnTransferGroupsInAutumnSem")
    public void transferGroupsInAutumnSemester(){

    }
}
