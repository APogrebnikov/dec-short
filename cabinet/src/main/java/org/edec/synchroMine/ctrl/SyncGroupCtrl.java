package org.edec.synchroMine.ctrl;

import org.edec.model.SemesterModel;
import org.edec.synchroMine.ctrl.renderer.GroupRenderer;
import org.edec.synchroMine.manager.groupSync.CabinetGroupSyncDAO;
import org.edec.synchroMine.model.CounterRegisterModel;
import org.edec.synchroMine.model.SchoolYearModel;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.edec.synchroMine.service.GroupSyncService;
import org.edec.synchroMine.service.impl.GroupSyncImpl;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SyncGroupCtrl extends SelectorComposer<Component> {

    static final String SELECTED_FORM_OF_STUDY = "selectedFormOfStudy";
    static final String SELECTED_INST = "selectedInst";
    static final String SELECTED_SEMESTER = "selectedSemester";

    CabinetGroupSyncDAO manager = new CabinetGroupSyncDAO();

    @Wire
    private Listbox lbGroupMine, lbGroupESO;
    @Wire
    private Button btnCreateSpringSem;

    private GroupSyncService groupSyncService = new GroupSyncImpl();

    private FormOfStudy selectedFormOfStudy;
    private SemesterModel selectedSemester;
    private InstituteModel selectedInst;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        createLazyEventInRootComponent();
        checkParameterAndFill();

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        if (today.get(Calendar.MONTH) == 0 || today.get(Calendar.MONTH) == 1 ) {
            btnCreateSpringSem.setDisabled(false);
        }
    }

    private void createLazyEventInRootComponent() {
        getSelf().addEventListener("onLater", event -> refreshDataInListboxes());
    }

    private void checkParameterAndFill() {
        getAttributesFromSession();
        if (selectedFormOfStudy == null || selectedSemester == null || selectedInst == null) {
            return;
        }
        fillingComponents();
        callLazyLoadingListbox();
    }

    private void getAttributesFromSession() {
        selectedFormOfStudy = (FormOfStudy) Executions.getCurrent().getAttribute(SELECTED_FORM_OF_STUDY);
        selectedInst = (InstituteModel) Executions.getCurrent().getAttribute(SELECTED_INST);
        selectedSemester = (SemesterModel) Executions.getCurrent().getAttribute(SELECTED_SEMESTER);
    }

    private void fillingComponents() {
        lbGroupMine.setItemRenderer(new GroupRenderer(selectedInst, selectedSemester));
        lbGroupESO.setItemRenderer(new GroupRenderer(selectedInst, selectedSemester));
    }

    private void callLazyLoadingListbox() {
        Clients.showBusy(getSelf(), "Загрузка данных");
        Events.echoEvent("onLater", getSelf(), null);
    }

    private void refreshDataInListboxes() {
        List<GroupMineModel> cabinetGroups = groupSyncService.getCabinetGroupsBySemester(selectedSemester);
        List<GroupMineModel> mineGroups = groupSyncService.getMineGroupsByInstSemesterAndFormOfStudy(selectedInst,
                selectedSemester, selectedFormOfStudy);
        groupSyncService.linkCabinetAndMineGroups(cabinetGroups, mineGroups);

        lbGroupMine.setModel(new ListModelList<>(mineGroups));
        lbGroupMine.renderAll();
        lbGroupESO.setModel(new ListModelList<>(cabinetGroups));
        lbGroupESO.renderAll();
        Clients.clearBusy(getSelf());
    }

    @Listen("onClick = #btnCreateSpringSem")
    public void createSpringSem() {
        SchoolYearModel schoolYear = manager.getLastSchoolYear();
        Long idOldSemFullTime = manager.getIdSem(1, 1L, schoolYear.getIdSY(), 1);
        Long idOldSemExtramural = manager.getIdSem(2, 1L, schoolYear.getIdSY(), 1);

        changeCurrentSemester();

        Long idNewSemFullTime = manager.getIdSem(1, 1L, schoolYear.getIdSY(), 1);
        Long idNewSemExtramural = manager.getIdSem(2, 1L, schoolYear.getIdSY(), 1);
        manager.createStudentsInSemester(idOldSemFullTime, idNewSemFullTime);
        manager.createStudentsInSemester(idOldSemExtramural, idNewSemExtramural);
        createCounterForRegister();
        PopupUtil.showInfo("Весенние семестры создались успешно!");

    }

    private void changeCurrentSemester() {
        List<SemesterModel> lastSemList = manager.getLastSemesters(4);
        for (SemesterModel semester : lastSemList) {
            if (!semester.isCurSem()) {
                manager.updateCurrentSem(semester.getIdSem(), 1);
            } else {
                manager.updateCurrentSem(semester.getIdSem(), 0);
            }
        }

    }

    public void createCounterForRegister() {
        List<CounterRegisterModel> listSemForRegister = manager.getSemestersForRegisters();
        for (CounterRegisterModel semForRegister : listSemForRegister) {
            if (semForRegister.getIdSemFromCounters() == null) {
                manager.updateCounters(semForRegister.getIdSem(), 0L);
            }
        }
    }

}
