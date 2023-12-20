package org.edec.mine.ctrl.compareCurriculumAndRegister;

import org.edec.cloud.sync.mine.api.compareRegisterAndCurriculum.data.filter.RegisterFilter;
import org.edec.cloud.sync.mine.api.compareRegisterAndCurriculum.service.ApiSyncMineCompareRegisterAndCurriculumImpl;
import org.edec.cloud.sync.mine.api.compareRegisterAndCurriculum.service.ApiSyncMineCompareRegisterAndCurriculumService;
import org.edec.mine.ctrl.compareCurriculumAndRegister.renderer.RegisterRenderer;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class WinFindRegisterCtrl extends SelectorComposer<Window> {

    public static final String ARG_SELECTED_GROUP = "selectedGroup";
    public static final String ARG_INSTITUTE = "institute";
    public static final String ARG_FORM_OF_STUDY = "formOfStudy";

    @Wire
    private Intbox ibSemesterNumber;
    @Wire
    private Listbox lbFindRegister;
    @Wire
    private Textbox tbGroupName, tbSubjectName;

    private final ApiSyncMineCompareRegisterAndCurriculumService service = new ApiSyncMineCompareRegisterAndCurriculumImpl();

    private Long idInst;
    private Integer formOfStudy;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        String groupname = (String) Executions.getCurrent().getArg().get(ARG_SELECTED_GROUP);
        idInst = (Long) Executions.getCurrent().getArg().get(ARG_INSTITUTE);
        formOfStudy = (Integer) Executions.getCurrent().getArg().get(ARG_FORM_OF_STUDY);
        lbFindRegister.setItemRenderer(new RegisterRenderer());
        tbGroupName.setValue(groupname);
    }

    @Listen("onOK = #tbGroupName; onOK = #tbSubjectName; onOK = #ibSemesterNumber;")
    public void searchRegister() {

        Clients.showBusy(lbFindRegister, "Загрузка данных");
        Events.echoEvent("onLater", lbFindRegister, null);
    }

    @Listen("onLater = #lbFindRegister")
    public void lazyLoadData() {

        RegisterFilter filter = RegisterFilter
                .builder(idInst, formOfStudy)

                .groupName(tbGroupName.getValue()).semesterNumber(ibSemesterNumber.getValue())
                .subjectName(tbSubjectName.getValue())
                .build();

        lbFindRegister.setModel(new ListModelList<>(service.findListOfRegisterByFilter(filter)));
        lbFindRegister.renderAll();

        Clients.clearBusy(lbFindRegister);
    }
}
