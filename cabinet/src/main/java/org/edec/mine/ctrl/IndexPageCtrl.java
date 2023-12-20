package org.edec.mine.ctrl;

import org.edec.mine.ctrl.compareCurriculumAndRegister.CurriculumSubjectCtrl;
import org.edec.mine.ctrl.compareCurriculumAndRegister.RegisterCtrl;
import org.edec.mine.ctrl.curriculumLine.CompareCurriculumLineCtrl;
import org.edec.mine.ctrl.curriculumLine.LinkCurriculumLineCtrl;
import org.edec.mine.ctrl.student.SyncStudentsCtrl;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Include;
import org.zkoss.zul.Vbox;

public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Combobox cmbInst, cmbFormOfStudy;
    @Wire
    private Include includeLinkCurriculumLine, includeCompareCurriculumLine, includeSyncStudents;
    @Wire
    private Include includeCompareCurRegCurriculumSubject, includeCompareCurRegRegister;
    @Wire
    private Vbox vbInst, vbFormOfStudy;

    private ComponentService componentService = new ComponentServiceESOimpl();

    @Override
    protected void fill() {

        componentService.fillCmbInst(cmbInst, vbInst, currentModule.getDepartments());
        componentService.fillCmbFormOfStudy(cmbFormOfStudy, vbFormOfStudy, currentModule.getFormofstudy());
        getSelf().addEventListener("onLater", event -> fillAllTabsDefaultInfo());
        Events.echoEvent("onLater", getSelf(), null);
    }

    private void fillAllTabsDefaultInfo() {

        InstituteModel selectedInst = cmbInst.getSelectedItem().getValue();
        FormOfStudy selectedFormOfStudy = cmbFormOfStudy.getSelectedItem().getValue();

        LinkCurriculumLineCtrl.setPropertiesToInclude(includeLinkCurriculumLine, selectedInst, selectedFormOfStudy);
        includeLinkCurriculumLine.invalidate();

        CompareCurriculumLineCtrl.setPropertiesToInclude(includeCompareCurriculumLine, selectedInst, selectedFormOfStudy);
        includeCompareCurriculumLine.invalidate();

        SyncStudentsCtrl.setPropertiesToInclude(includeSyncStudents, selectedInst, selectedFormOfStudy);
        includeSyncStudents.invalidate();

        CurriculumSubjectCtrl.setPropertiesToInclude(includeCompareCurRegCurriculumSubject, selectedInst, selectedFormOfStudy);
        includeCompareCurRegCurriculumSubject.invalidate();

        RegisterCtrl.setPropertiesToInclude(includeCompareCurRegRegister, selectedInst, selectedFormOfStudy);
        includeCompareCurRegRegister.invalidate();
    }

    @Listen("onSelect = #tabLinkCurriculumLine")
    public void selectedLinkCurriculumLineTab() {
        includeLinkCurriculumLine.invalidate();
    }

    @Listen("onSelect = #tabCompareCurriculumLine")
    public void selectedCompareCurriculumLineTab() {
        includeCompareCurriculumLine.invalidate();
    }

    @Listen("onSelect = #tabSyncStudents")
    public void selectedSyncStudentsTab() {
        includeSyncStudents.invalidate();
    }

    @Listen("onSelect = #tabCompareCurRegCurriculumSubject")
    public void selectedCompareCurRegCurriculumSubject() {
        includeCompareCurRegCurriculumSubject.invalidate();
    }

    @Listen("onSelect = #tabCompareCurRegRegister")
    public void selectCompareCurRegRegister() {
        includeCompareCurRegRegister.invalidate();
    }
}
