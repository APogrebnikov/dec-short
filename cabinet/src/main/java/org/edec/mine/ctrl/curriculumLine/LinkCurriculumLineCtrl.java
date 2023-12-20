package org.edec.mine.ctrl.curriculumLine;

import org.edec.cloud.sync.mine.api.linkCurriculumLine.data.filter.LinkCurriculumLineFilter;
import org.edec.cloud.sync.mine.api.linkCurriculumLine.data.response.LinkCurriculumLineResponse;
import org.edec.cloud.sync.mine.api.linkCurriculumLine.service.ApiSyncMineLinkCurriculumLineImpl;
import org.edec.cloud.sync.mine.api.linkCurriculumLine.service.ApiSyncMineLinkCurriculumLineService;
import org.edec.cloud.sync.mine.api.utility.constants.FormOfControlConst;
import org.edec.commons.component.MultipleSelectComponent;
import org.edec.mine.ctrl.curriculumLine.renderer.LinkCurriculumLineRenderer;
import org.edec.mine.service.curriculumLine.JdbcLinkCurriculumLineService;
import org.edec.mine.service.curriculumLine.impl.JdbcLinkCurriculumLineImpl;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.IncludeSelector;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LinkCurriculumLineCtrl extends IncludeSelector {

    private MultipleSelectComponent<SemesterModel, Long> mscSemester;
    private MultipleSelectComponent<FormOfControlConst, FormOfControlConst> mscFocCabinet;

    @Wire
    private Intbox ibCourseCabinet, ibSemesterNumberCabinet, ibCourseMine, ibSemesterNumberMine;
    @Wire
    private Listbox lbLinkSubjects;
    @Wire
    private Radio rOnlyLinked, rOnlyUnlinked;
    @Wire
    private Textbox tbGroupName, tbSubjectNameCabinet, tbSubjectNameMine;
    @Wire
    private Vlayout vlLinked, vlFoc;

    private ApiSyncMineLinkCurriculumLineService apiSyncMineLinkCurriculumLineService = new ApiSyncMineLinkCurriculumLineImpl();
    private JdbcLinkCurriculumLineService jdbcLinkCurriculumLineService = new JdbcLinkCurriculumLineImpl();

    @Override
    protected void fill() {

        lbLinkSubjects.setItemRenderer(new LinkCurriculumLineRenderer(this::searchSubjects, getSelectedInstitute().getIdInst(),
                getSelectedFormOfStudy().getType()));
        addSemesterMultiSelectComponent();
        addFocCabinetMultiSelectComponent();
    }

    private void addSemesterMultiSelectComponent() {

        mscSemester = new MultipleSelectComponent<>(
                jdbcLinkCurriculumLineService.getSemesters(getSelectedInstitute().getIdInst(), getSelectedFormOfStudy().getType()),
                SemesterModel::getIdSem);
        mscSemester.setParent(vlLinked);
        mscSemester.addEventListener(Events.ON_CHANGING, event -> searchSubjects());
        mscSemester.setHflex("1");
        selectSomeOfRadioComponents();
    }

    private void selectSomeOfRadioComponents() {

        mscSemester.setVisible(rOnlyLinked.isSelected());
        mscSemester.setDisabled(!rOnlyLinked.isSelected());
    }

    private void addFocCabinetMultiSelectComponent() {

        mscFocCabinet = new MultipleSelectComponent<>(
                Arrays.stream(FormOfControlConst.values()).collect(Collectors.toList()),
                foc -> foc);
        mscFocCabinet.setParent(vlFoc);
        mscFocCabinet.addEventListener(Events.ON_CHANGING, event -> searchSubjects());
    }

    @Listen("onClick = #btnSearchLinkSubjects; onOK = #ibCourseCabinet; onOK = #ibSemesterNumberCabinet;" +
            "onOK = #ibCourseMine; onOK = #ibSemesterNumberMine; onOK = #tbGroupName;" +
            "onOK = #tbSubjectNameCabinet; onOK = #tbSubjectNameMine; onCheck = #rOnlyLinked;" +
            "onCheck = #rOnlyUnlinked; onCheck = #rAll;")
    public void searchSubjects() {

        selectSomeOfRadioComponents();
        Clients.showBusy(lbLinkSubjects, "Загрузка данных");
        Events.echoEvent("onLater", lbLinkSubjects, null);
    }

    @Listen("onLater = #lbLinkSubjects")
    public void callLaterEventForLbSubjects() {

        LinkCurriculumLineFilter filter = LinkCurriculumLineFilter
                .builder(getSelectedInstitute().getIdInst(), getSelectedFormOfStudy().getType())

                .onlyUnlinked(rOnlyUnlinked.isSelected()).onlyLinked(rOnlyLinked.isSelected())
                .groupName(tbGroupName.getValue())
                .subjectNameCabinet(tbSubjectNameCabinet.getValue()).subjectNameMine(tbSubjectNameMine.getValue())
                .courseCabinet(ibCourseCabinet.getValue()).courseMine(ibCourseMine.getValue())
                .semesterNumberCabinet(ibSemesterNumberCabinet.getValue()).semesterNumberMine(ibSemesterNumberMine.getValue())
                .selectedSemIds(mscSemester.getSelectedValues()).selectedFocCabinet(mscFocCabinet.getSelectedValues())
                .build();

        ListModelList<LinkCurriculumLineResponse> model = new ListModelList<>(apiSyncMineLinkCurriculumLineService.findLinkCurriculumLineByQuery(filter));
        model.setMultiple(true);
        lbLinkSubjects.setModel(model);
        lbLinkSubjects.renderAll();

        Clients.clearBusy(lbLinkSubjects);
    }

    @Listen("onClick = #btnSetMineParameters")
    public void setMineParameters() {

        if (lbLinkSubjects.getSelectedCount() == 0) {
            DialogUtil.exclamation("Выберите хотя бы одну дисциплину из списка");
            return;
        }

        List<LinkCurriculumLineResponse> selectedItemModels = lbLinkSubjects.getSelectedItems().stream()
                .map(item -> ((LinkCurriculumLineResponse) item.getValue()))
                .collect(Collectors.toList());

        WinLinkSelectedItemsCtrl winLinkSelectedItemsCtrl = new WinLinkSelectedItemsCtrl(selectedItemModels);
        winLinkSelectedItemsCtrl.setParent(getSelf().getParent());
        winLinkSelectedItemsCtrl.addEventListener("onFinishLinkSubjects", event -> searchSubjects());
        winLinkSelectedItemsCtrl.doModal();
    }
}
