package org.edec.mine.ctrl.curriculumLine;

import org.edec.cloud.sync.mine.api.commond.data.request.CompareSubjectModel;
import org.edec.cloud.sync.mine.api.curriculumLine.data.response.CurriculumLineResponse;
import org.edec.cloud.sync.mine.api.curriculumLine.service.ApiSyncMineCurriculumLineImpl;
import org.edec.cloud.sync.mine.api.curriculumLine.service.ApiSyncMineCurriculumLineService;
import org.edec.mine.dao.curriculumLine.CurriculumLineDao;
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
import java.util.Objects;

public class WinCompareSubjectsCtrl extends Window {

    private Grid grid;
    private Vlayout vlContent;

    private CurriculumLineDao curriculumLineDao = new CurriculumLineDao();
    private ApiSyncMineCurriculumLineService apiSyncMineCurriculumLineService = new ApiSyncMineCurriculumLineImpl();

    private CurriculumLineResponse curriculumLine;
    private List<CompareSubjectModel> compareSubjects = new ArrayList<>();

    public WinCompareSubjectsCtrl(CurriculumLineResponse curriculumLine) {

        this.curriculumLine = curriculumLine;

        fillDefaultSettings();
        createGrid();
        prepareData();
        fillGrid();
        createButtons();
    }

    private void fillDefaultSettings() {

        setTitle("Сохранение информации из системы Шахты");
        setClosable(true);
        setHeight("50%");
        setWidth("40%");
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
        new Label("Названия свойств").setParent(row);
        new Label("Значение из шахт").setParent(row);
        new Label("Значение из АСУ ИКИТ").setParent(row);
        new Label("Синхронизировать?").setParent(row);
    }

    private void prepareData() {

        CurriculumLineResponse subjectCabinet = curriculumLineDao.getSubjectByParams(curriculumLine.getGroupname(), curriculumLine.getCourseCabinet(),
                curriculumLine.getSemesterNumberCabinet(), curriculumLine.getSubjectNameCabinet());
        if (subjectCabinet == null) {
            throw new IllegalArgumentException("Не удалось найти предмет в системе АСУ ИКИТ по выбранному предмету из шахт");
        }
        compareSubjects.addAll(Arrays.asList(
                new CompareSubjectModel(false, "", "Название предмета",
                        curriculumLine.getSubjectName(),
                        subjectCabinet.getSubjectName()),
                new CompareSubjectModel(true, "subjectcode", "Код предмета",
                        curriculumLine.getSubjectCode(),
                        subjectCabinet.getSubjectCode()),
                new CompareSubjectModel(true, "is_facultative", "Факультатив",
                        curriculumLine.getIsFacultative() ? "Да" : "Нет",
                        subjectCabinet.getIsFacultative() ? "Да" : "Нет"),
                new CompareSubjectModel(true, "hourscount", "Часов всего",
                        doubleToStringConvert(curriculumLine.getHoursCount()),
                        doubleToStringConvert(subjectCabinet.getHoursCount())),
                new CompareSubjectModel(true, "hoursaudcount", "Аудиторных часов",
                        doubleToStringConvert(curriculumLine.getHoursAudCount()),
                        doubleToStringConvert(subjectCabinet.getHoursAudCount())),
                new CompareSubjectModel(true, "hourspractic", "Часы практики",
                        doubleToStringConvert(curriculumLine.getHoursPractice()),
                        doubleToStringConvert(subjectCabinet.getHoursPractice())),
                new CompareSubjectModel(true, "hourslabor", "Часы лаб. работы",
                        doubleToStringConvert(curriculumLine.getHoursLabor()),
                        doubleToStringConvert(subjectCabinet.getHoursLabor())),
                new CompareSubjectModel(true, "hourslection", "Часы лекц. работ",
                        doubleToStringConvert(curriculumLine.getHoursLection()),
                        doubleToStringConvert(subjectCabinet.getHoursLection())),
                new CompareSubjectModel(false, "", "ФК",
                        curriculumLine.focString(), subjectCabinet.focString())
        ));
    }

    private String doubleToStringConvert(Double value) {
        return value == null ? "" : String.valueOf(value);
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
        new Label(compareSubject.getMineValue()).setParent(row);
        new Label(compareSubject.getCabinetValue()).setParent(row);
        if (compareSubject.isVisibleCheckbox()) {
            Checkbox checkbox = new Checkbox();
            checkbox.setParent(row);
            checkbox.setChecked(compareSubject.isComparable());
            checkbox.addEventListener(Events.ON_CHECK, event -> {
                compareSubject.setComparable(checkbox.isChecked());
            });
        } else {
            new Label("").setParent(row);
        }

        if (!Objects.equals(compareSubject.getMineValue(), compareSubject.getCabinetValue())) {
            row.setStyle("background: #FFFE7E;");
        }
    }

    private void createButtons() {

        Hlayout hlActions = new Hlayout();
        hlActions.setParent(vlContent);
        hlActions.setHeight("40px");
        hlActions.setHflex("1");

        Button btnCancel = new Button("Отмена");
        btnCancel.setParent(hlActions);
        btnCancel.addEventListener(Events.ON_CLICK, event -> this.detach());

        Button btnSave = new Button("Сохранить");
        btnSave.setParent(hlActions);
        btnSave.addEventListener(Events.ON_CLICK, event -> saveColumns());
    }

    private void saveColumns() {

        apiSyncMineCurriculumLineService.syncPropertiesCurriculumLine(curriculumLine.getId(), compareSubjects);
        Events.postEvent("onFinishSync", this, null);
        this.detach();
    }
}
