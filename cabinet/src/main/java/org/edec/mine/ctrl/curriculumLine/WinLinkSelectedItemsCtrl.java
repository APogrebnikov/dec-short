package org.edec.mine.ctrl.curriculumLine;

import org.apache.commons.lang3.StringUtils;
import org.edec.cloud.sync.mine.api.curriculumLine.data.response.CurriculumLineResponse;
import org.edec.cloud.sync.mine.api.curriculumLine.service.ApiSyncMineCurriculumLineImpl;
import org.edec.cloud.sync.mine.api.curriculumLine.service.ApiSyncMineCurriculumLineService;
import org.edec.cloud.sync.mine.api.linkCurriculumLine.data.response.LinkCurriculumLineResponse;
import org.edec.cloud.sync.mine.api.linkCurriculumLine.service.ApiSyncMineLinkCurriculumLineImpl;
import org.edec.cloud.sync.mine.api.linkCurriculumLine.service.ApiSyncMineLinkCurriculumLineService;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import java.util.List;

public class WinLinkSelectedItemsCtrl<T> extends Window {

    private Textbox tbSubjectName;
    private Intbox ibCourse, ibSemesterNumber;

    private ApiSyncMineCurriculumLineService apiSyncMineCurriculumLineService = new ApiSyncMineCurriculumLineImpl();
    private ApiSyncMineLinkCurriculumLineService apiSyncMineLinkCurriculumLineService = new ApiSyncMineLinkCurriculumLineImpl();

    private boolean isLinkCurriculumLineType;
    private List<CurriculumLineResponse> selectedCurriculumLines;
    private List<LinkCurriculumLineResponse> selectedLinkCurriculumLines;

    public WinLinkSelectedItemsCtrl(List selectedItems) {

        if (selectedItems != null && selectedItems.get(0) instanceof LinkCurriculumLineResponse) {
            this.selectedLinkCurriculumLines = selectedItems;
            this.isLinkCurriculumLineType = true;
        } else {
            this.selectedCurriculumLines = selectedItems;
            this.isLinkCurriculumLineType = false;
        }

        fillDefaultSettings();
        createComponents();
    }

    private void fillDefaultSettings() {

        setTitle("Сопоставление выбранных дисциплин");
        setClosable(true);
        setWidth("30%");
        setHeight("40%");
    }

    private void createComponents() {

        Vlayout vlComponents = new Vlayout();
        vlComponents.setParent(this);


        String labelName = isLinkCurriculumLineType ? "Параметры системы шахты" : "Параметры системы АСУ ИКИТ";
        new Label(labelName).setParent(vlComponents);

        tbSubjectName = new Textbox();
        tbSubjectName.setParent(vlComponents);
        tbSubjectName.setHflex("1");
        tbSubjectName.setPlaceholder("Название предмета");

        ibCourse = new Intbox();
        ibCourse.setParent(vlComponents);
        ibCourse.setHflex("1");
        ibCourse.setPlaceholder("Курс");

        ibSemesterNumber = new Intbox();
        ibSemesterNumber.setParent(vlComponents);
        ibSemesterNumber.setHflex("1");
        ibSemesterNumber.setPlaceholder("Семестр");

        Hlayout hlActions = new Hlayout();
        hlActions.setParent(vlComponents);

        Button btnCancel = new Button("Отмена");
        btnCancel.setParent(hlActions);
        btnCancel.addEventListener(Events.ON_CLICK, event -> closeDialog());

        Button btnSave = new Button("Сохранить");
        btnSave.setParent(hlActions);
        btnSave.addEventListener(Events.ON_CLICK, event -> saveSubjects());
    }

    private void closeDialog() {
        this.detach();
    }

    private void saveSubjects() {

        if (StringUtils.isBlank(tbSubjectName.getValue())
                || ibCourse.getValue() == null
                || ibSemesterNumber.getValue() == null) {
            PopupUtil.showWarning("Заполните все поля!");
            return;
        }
        if (isLinkCurriculumLineType) {
            apiSyncMineLinkCurriculumLineService.linkListOfSubjects(selectedLinkCurriculumLines, tbSubjectName.getValue(), ibCourse.getValue(), ibSemesterNumber.getValue());
        } else {
            apiSyncMineCurriculumLineService.updateListOfCurriculumLine(selectedCurriculumLines, tbSubjectName.getValue(), ibCourse.getValue(), ibSemesterNumber.getValue());
        }
        Events.postEvent("onFinishLinkSubjects", this, null);
        this.detach();
    }
}
