package org.edec.mine.ctrl.curriculumLine;

import org.edec.cloud.sync.mine.api.commond.data.request.CompareSubjectModel;
import org.edec.cloud.sync.mine.api.curriculumLine.data.response.CurriculumLineResponse;
import org.edec.cloud.sync.mine.api.curriculumLine.service.ApiSyncMineCurriculumLineImpl;
import org.edec.cloud.sync.mine.api.curriculumLine.service.ApiSyncMineCurriculumLineService;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WinSyncSubjectsCtrl extends Window {

    private Grid grid;
    private Vlayout vlContent;

    private ApiSyncMineCurriculumLineService apiSyncMineCurriculumLineService = new ApiSyncMineCurriculumLineImpl();

    private List<CurriculumLineResponse> selectedCurriculumLines;
    private List<CompareSubjectModel> compareSubjects = new ArrayList<>();

    public WinSyncSubjectsCtrl(List<CurriculumLineResponse> selectedCurriculumLines) {
        this.selectedCurriculumLines = selectedCurriculumLines;

        fillDefaultSettings();
        createGrid();
        prepareData();
        fillGrid();
        createButtons();
    }

    private void fillDefaultSettings() {

        setTitle("Сохранение информации из системы Шахты");
        setClosable(true);
        setWidth("30%");
        setHeight("40%");
    }

    private void createGrid() {

        vlContent = new Vlayout();
        vlContent.setParent(this);
        vlContent.setHflex("1");
        vlContent.setVflex("1");

        grid = new Grid();
        grid.setParent(vlContent);
        grid.setHflex("1");
        grid.setVflex("1");

        Rows rows = new Rows();
        rows.setParent(grid);

        Row row = new Row();
        row.setParent(rows);
        new Label("Название свойства").setParent(row);
        new Label("Синхронизировать?").setParent(row);
    }

    private void prepareData() {
        compareSubjects.addAll(Arrays.asList(
                new CompareSubjectModel("subjectcode", "Код предмета"),
                new CompareSubjectModel("is_facultative", "Факультатив"),
                new CompareSubjectModel("hourscount", "Часов всего"),
                new CompareSubjectModel("hoursaudcount", "Аудиторных часов"),
                new CompareSubjectModel("hourspractic", "Часы практики"),
                new CompareSubjectModel("hourslabor", "Часы лаб. работы"),
                new CompareSubjectModel("hourslection", "Часы лекц. работ")));
    }

    private void fillGrid() {

        for (CompareSubjectModel compareSubject : compareSubjects) {
            createRowContent(compareSubject);
        }
    }

    private void createRowContent(CompareSubjectModel compareSubject) {

        Row row = new Row();
        row.setParent(grid.getRows());

        new Label(compareSubject.getColumnKey()).setParent(row);
        if (compareSubject.isVisibleCheckbox()) {
            Checkbox checkbox = new Checkbox();
            checkbox.setParent(row);
            checkbox.setChecked(compareSubject.isComparable());
            checkbox.addEventListener(Events.ON_CHECK, event -> compareSubject.setComparable(checkbox.isChecked()));
        } else {
            new Label("").setParent(row);
        }
    }

    private void createButtons() {

        Hlayout hlActions = new Hlayout();
        hlActions.setParent(vlContent);
        hlActions.setHeight("40px");
        hlActions.setHflex("1");
        hlActions.setValign("right");

        Button btnCancel = new Button("Отмена");
        btnCancel.setParent(hlActions);
        btnCancel.addEventListener(Events.ON_CLICK, event -> this.detach());

        Button btnSave = new Button("Синхронизировать");
        btnSave.setParent(hlActions);
        btnSave.addEventListener(Events.ON_CLICK, event -> saveColumns());
    }

    private void saveColumns() {

        boolean someComparable = compareSubjects.stream()
                .anyMatch(CompareSubjectModel::isComparable);

        if(!someComparable) {
            DialogUtil.info("Нужно выбрать хотя бы одно свойство!");
            return;
        }

        apiSyncMineCurriculumLineService.syncPropertiesForListOfCurriculumLine(selectedCurriculumLines, compareSubjects);
        Events.postEvent("onFinishSync", this, null);
        this.detach();
    }
}
