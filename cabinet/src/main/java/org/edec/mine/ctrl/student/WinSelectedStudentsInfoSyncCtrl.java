package org.edec.mine.ctrl.student;

import org.edec.cloud.sync.mine.api.commond.data.request.CompareSubjectModel;
import org.edec.cloud.sync.mine.api.student.data.StudentCurrentSemesterDto;
import org.edec.cloud.sync.mine.api.student.service.ApiSyncMineStudentImpl;
import org.edec.cloud.sync.mine.api.student.service.ApiSyncMineStudentService;
import org.edec.cloud.sync.mine.api.utility.compareResult.CompareResult;
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

public class WinSelectedStudentsInfoSyncCtrl extends Window {

    private Grid gridStudentProperties;
    private Vlayout vlContent;

    private ApiSyncMineStudentService apiSyncMineStudentService = new ApiSyncMineStudentImpl();

    private List<CompareResult<StudentCurrentSemesterDto>> selectedStudents;
    private List<CompareSubjectModel> compareProperties = new ArrayList<>();

    public WinSelectedStudentsInfoSyncCtrl(List<CompareResult<StudentCurrentSemesterDto>> selectedStudents) {
        this.selectedStudents = selectedStudents;

        fill();
    }

    private void fill() {

        fillDefaultSettings();
        createAndFillGrid();
        createButtonsLayout();
    }

    private void fillDefaultSettings() {

        setTitle("Синхронизация свойств студентов");
        setHeight("40%");
        setWidth("50%");

        vlContent = new Vlayout();
        vlContent.setParent(this);
        vlContent.setHflex("1");
        vlContent.setVflex("1");
    }

    private void createAndFillGrid() {

        createGrid();
        prepareData();
        fillGrid();
    }

    private void createGrid() {

        gridStudentProperties = new Grid();
        gridStudentProperties.setParent(vlContent);
        gridStudentProperties.setHflex("1");
        gridStudentProperties.setVflex("1");

        Rows rows = new Rows();
        rows.setParent(gridStudentProperties);

        Row headerRow = new Row();
        headerRow.setParent(rows);

        new Label("Название свойства").setParent(headerRow);
        new Label("Синхронизировать?").setParent(headerRow);
    }

    private void prepareData() {

        compareProperties.addAll(Arrays.asList(
           new CompareSubjectModel("birthday", "Дата рождения"),
           new CompareSubjectModel("recordbook", "Зачетная книжка"),
           new CompareSubjectModel("sex", "Пол"),
           new CompareSubjectModel("foreigner", "Иностранец"),
           new CompareSubjectModel("conditionOfEducation", "Условия обучения"),
           new CompareSubjectModel("studentStatus", "Статус студента"),
           new CompareSubjectModel("idStudentcardMine", "Идентификатор студента в Шахтах")
        ));
    }

    private void fillGrid() {

        for (CompareSubjectModel compareProperty : compareProperties) {
            createRowContent(compareProperty);
        }
    }

    private void createRowContent(CompareSubjectModel compareProperty) {

        Row row = new Row();
        row.setParent(gridStudentProperties.getRows());

        new Label(compareProperty.getColumnKey()).setParent(row);

        if (compareProperty.isVisibleCheckbox()) {
            Checkbox checkbox = new Checkbox();
            checkbox.setParent(row);
            checkbox.setChecked(compareProperty.isComparable());
            checkbox.addEventListener(Events.ON_CHECK, event -> compareProperty.setComparable(checkbox.isChecked()));
        } else {
            new Label("").setParent(row);
        }
    }

    private void createButtonsLayout() {

        Hlayout hlActions = new Hlayout();
        hlActions.setParent(vlContent);
        hlActions.setHeight("50px");
        hlActions.setHflex("1");

        Button btnCancel = new Button("Отмена");
        btnCancel.setParent(hlActions);
        btnCancel.addEventListener(Events.ON_CLICK, event -> this.detach());

        Button btnSync = new Button("Синхронизировать");
        btnSync.setParent(hlActions);
        btnSync.addEventListener(Events.ON_CLICK, event -> syncProperties());
    }

    private void syncProperties() {

        apiSyncMineStudentService.syncSelectedStudents(selectedStudents, compareProperties);
        Events.postEvent("onFinishSync", this, null);
        this.detach();
    }
}
