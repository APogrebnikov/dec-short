package org.edec.mine.ctrl.curriculumLine;

import org.edec.cloud.sync.mine.api.curriculumLine.data.filter.CurriculumLineFilter;
import org.edec.cloud.sync.mine.api.curriculumLine.data.response.CurriculumLineResponse;
import org.edec.cloud.sync.mine.api.curriculumLine.service.ApiSyncMineCurriculumLineImpl;
import org.edec.cloud.sync.mine.api.curriculumLine.service.ApiSyncMineCurriculumLineService;
import org.edec.cloud.sync.mine.api.utility.constants.FormOfControlConst;
import org.edec.commons.component.MultipleSelectComponent;
import org.edec.mine.ctrl.curriculumLine.renderer.CurriculumLineRenderer;
import org.edec.mine.service.curriculumLine.JdbcCurriculumLineService;
import org.edec.mine.service.curriculumLine.impl.JdbcCurriculumLineImpl;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.IncludeSelector;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CompareCurriculumLineCtrl extends IncludeSelector {

    private MultipleSelectComponent<FormOfControlConst, FormOfControlConst> mscFocCabinet, mscFocMine;
    private MultipleSelectComponent<SemesterModel, Long> mscSemester;

    @Wire
    private Intbox ibCourseCabinet, ibSemesterNumberCabinet, ibCourseMine, ibSemesterNumberMine;
    @Wire
    private Listbox lbCurriculumLine;
    @Wire
    private Radio rOnlyLinked, rOnlyUnlinked;
    @Wire
    private Textbox tbGroupName, tbSubjectNameCabinet, tbSubjectNameMine;
    @Wire
    private Vlayout vlFoc, vlLinked;

    private ApiSyncMineCurriculumLineService apiSyncMineCurriculumLineService = new ApiSyncMineCurriculumLineImpl();
    private JdbcCurriculumLineService jdbcCurriculumLineService = new JdbcCurriculumLineImpl();

    @Override
    protected void fill() {

        lbCurriculumLine.setItemRenderer(new CurriculumLineRenderer(this::searchSubjects));
        createSemMultiSelectComponent();
        createFocMultiSelectComponent();
    }

    private void createSemMultiSelectComponent() {

        mscSemester = new MultipleSelectComponent<>(jdbcCurriculumLineService.getSemesters(
                getSelectedInstitute().getIdInst(), getSelectedFormOfStudy().getType()), SemesterModel::getIdSem);
        mscSemester.setParent(vlLinked);
        mscSemester.addEventListener(Events.ON_CHANGING, event -> searchSubjects());
        mscSemester.setHflex("1");
        mscSemester.selectAllItems();
        selectSomeOfRadioComponents();
    }

    private void selectSomeOfRadioComponents() {

        if (rOnlyLinked.isChecked()) {
            mscSemester.setVisible(true);
            mscSemester.setDisabled(false);
        } else {
            mscSemester.setVisible(false);
            mscSemester.setDisabled(true);
        }
    }

    private void createFocMultiSelectComponent() {

        Hlayout hlContent = new Hlayout();
        hlContent.setParent(vlFoc);

        Vlayout vlMine = new Vlayout();
        vlMine.setParent(hlContent);

        new Label("Шахты:").setParent(vlMine);
        List<FormOfControlConst> focMine = Arrays.stream(FormOfControlConst.values()).collect(Collectors.toList());
        focMine.remove(FormOfControlConst.PRACTIC);
        mscFocMine = new MultipleSelectComponent<>(focMine, foc -> foc);
        mscFocMine.setParent(vlMine);
        mscFocMine.addEventListener(Events.ON_CHANGING, event -> searchSubjects());

        Vlayout vlCabinet = new Vlayout();
        vlCabinet.setParent(hlContent);

        new Label("АСУ ИКИТ:").setParent(vlCabinet);
        mscFocCabinet = new MultipleSelectComponent<>(Arrays.stream(FormOfControlConst.values()).collect(Collectors.toList()), foc -> foc);
        mscFocCabinet.setParent(vlCabinet);
        mscFocCabinet.addEventListener(Events.ON_CHANGING, event -> searchSubjects());
    }

    @Listen("onClick = #btnSearchCurriculumLine; onOK = #ibCourseCabinet; onOK = #ibSemesterNumberCabinet;" +
            "onOK = #ibCourseMine; onOK = #ibSemesterNumberMine; onOK = #tbGroupName;" +
            "onOK = #tbSubjectNameCabinet; onOK = #tbSubjectNameMine; onCheck = #rOnlyLinked;" +
            "onCheck = #rOnlyUnlinked; onCheck = #rAll;")
    public void searchSubjects() {

        selectSomeOfRadioComponents();
        Clients.showBusy(lbCurriculumLine, "Загрузка данных");
        Events.echoEvent("onLater", lbCurriculumLine, null);
    }

    @Listen("onLater = #lbCurriculumLine")
    public void callLaterEventForLbCurriculumLine() {

        CurriculumLineFilter filter = CurriculumLineFilter
                .builder(getSelectedInstitute().getIdInst(), getSelectedFormOfStudy().getType())

                .onlyUnlinked(rOnlyUnlinked.isSelected()).onlyLinked(rOnlyLinked.isSelected())
                .groupName(tbGroupName.getValue()).subjectNameMine(tbSubjectNameMine.getValue())
                .courseMine(ibCourseMine.getValue()).semesterNumberMine(ibSemesterNumberMine.getValue())
                .subjectNameCabinet(tbSubjectNameCabinet.getValue())
                .courseCabinet(ibCourseCabinet.getValue()).semesterNumberCabinet(ibSemesterNumberCabinet.getValue())
                .selectedSemIds(rOnlyLinked.isSelected() ? mscSemester.getSelectedValues() : null)
                .selectedFocCabinet(mscFocCabinet.getSelectedValues()).selectedFocMine(mscFocMine.getSelectedValues())
                .build();

        ListModelList<CurriculumLineResponse> model = new ListModelList<>(apiSyncMineCurriculumLineService.findCurriculumLinesByFilter(filter));
        model.setMultiple(true);
        lbCurriculumLine.setModel(model);
        lbCurriculumLine.renderAll();

        Clients.clearBusy(lbCurriculumLine);
    }

    @Listen("onClick = #btnSetCabinetParameters")
    public void setCabinetParameters() {

        if (lbCurriculumLine.getSelectedCount() == 0) {
            DialogUtil.exclamation("Выберите хотя бы одну дисциплину из списка");
            return;
        }

        List<CurriculumLineResponse> selectedItemModels = lbCurriculumLine.getSelectedItems().stream()
                .map(item -> ((CurriculumLineResponse) item.getValue()))
                .collect(Collectors.toList());

        WinLinkSelectedItemsCtrl winLinkSelectedItemsCtrl = new WinLinkSelectedItemsCtrl(selectedItemModels);
        winLinkSelectedItemsCtrl.setParent(getSelf().getParent());
        winLinkSelectedItemsCtrl.addEventListener("onFinishLinkSubjects", event -> searchSubjects());
        winLinkSelectedItemsCtrl.doModal();
    }

    @Listen("onClick = #btnSyncAllSelectedItems")
    public void syncAllSelectedItems() {

        if (lbCurriculumLine.getSelectedCount() == 0) {
            DialogUtil.exclamation("Выберите хотя бы одну дисциплину из списка");
            return;
        }

        List<CurriculumLineResponse> selectedItemModels = lbCurriculumLine.getSelectedItems().stream()
                .map(item -> ((CurriculumLineResponse) item.getValue()))
                .collect(Collectors.toList());

        showSyncSubjectsWindow(selectedItemModels);
    }

    @Listen("onClick = #btnSyncAllItems")
    public void syncAllItems() {

        if (lbCurriculumLine.getItemCount() == 0) {
            DialogUtil.exclamation("Не найдено не одной дисциплины");
            return;
        }

        List<CurriculumLineResponse> itemModels = lbCurriculumLine.getItems().stream()
                .map(item -> ((CurriculumLineResponse) item.getValue()))
                .collect(Collectors.toList());

        showSyncSubjectsWindow(itemModels);
    }

    private void showSyncSubjectsWindow(List<CurriculumLineResponse> curriculumLinesForSync) {

        WinSyncSubjectsCtrl winSyncSubjectsCtrl = new WinSyncSubjectsCtrl(curriculumLinesForSync);
        winSyncSubjectsCtrl.setParent(getSelf().getParent());
        winSyncSubjectsCtrl.addEventListener("onFinishSync", event -> searchSubjects());
        winSyncSubjectsCtrl.doModal();
    }
}
