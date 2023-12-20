package org.edec.synchroMine.ctrl;

import org.edec.model.SemesterModel;
import org.edec.synchroMine.ctrl.renderer.GroupRenderer;
import org.edec.synchroMine.ctrl.renderer.SyncGroupRenderer;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.edec.synchroMine.service.GroupSyncService;
import org.edec.synchroMine.service.impl.GroupSyncImpl;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.constants.FormOfStudy;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

public class SyncGroupCtrl2 extends SelectorComposer<Component> {

    static final String SELECTED_FORM_OF_STUDY = "selectedFormOfStudy";
    static final String SELECTED_INST = "selectedInst";
    static final String SELECTED_SEMESTER = "selectedSemester";

    @Wire
    private Listbox lbSyncGroup;

    private GroupSyncService groupSyncService = new GroupSyncImpl();

    private FormOfStudy selectedFormOfStudy;
    private SemesterModel selectedSemester;
    private InstituteModel selectedInst;


    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        createLazyEventInRootComponent();
        checkParameterAndFill();
    }

    private void createLazyEventInRootComponent() {
        getSelf().addEventListener("onLater", event -> refreshDataInListboxes());
    }

    private void checkParameterAndFill() {
        getAttributesFromSession();
        if (selectedFormOfStudy == null || selectedSemester == null || selectedInst == null) {
            return;
        }
        lbSyncGroup.setItemRenderer(new SyncGroupRenderer(selectedInst, selectedSemester));
        callLazyLoadingListbox();
    }

    private void getAttributesFromSession() {
        selectedFormOfStudy = (FormOfStudy) Executions.getCurrent().getAttribute(SELECTED_FORM_OF_STUDY);
        selectedInst = (InstituteModel) Executions.getCurrent().getAttribute(SELECTED_INST);
        selectedSemester = (SemesterModel) Executions.getCurrent().getAttribute(SELECTED_SEMESTER);
    }

    private void callLazyLoadingListbox() {
        Clients.showBusy(getSelf(), "Загрузка данных");
        Events.echoEvent("onLater", getSelf(), null);
    }

    private void refreshDataInListboxes() {

        lbSyncGroup.setModel(new ListModelList<>(groupSyncService.compareGroupInMineAndCabinet(
                selectedInst, selectedSemester, selectedFormOfStudy
        )));
        lbSyncGroup.renderAll();

        Clients.clearBusy(getSelf());
    }
}
