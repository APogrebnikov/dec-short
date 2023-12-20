package org.edec.mine.ctrl.student;

import org.edec.cloud.sync.mine.api.commond.data.request.CompareSubjectModel;
import org.edec.cloud.sync.mine.api.student.data.StudentCurrentSemesterDto;
import org.edec.cloud.sync.mine.api.student.data.StudentForComparingFilter;
import org.edec.cloud.sync.mine.api.student.service.ApiSyncMineStudentImpl;
import org.edec.cloud.sync.mine.api.student.service.ApiSyncMineStudentService;
import org.edec.cloud.sync.mine.api.utility.compareResult.CompareResult;
import org.edec.mine.ctrl.student.component.ListboxStudentSearch;
import org.edec.mine.service.student.JdbcStudentService;
import org.edec.mine.service.student.impl.JdbcJdbcStudentImpl;
import org.edec.utility.constants.StudentStatus;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class WinStudentInfoSyncCtrl extends Window {

    private Grid gridStudentInfo;
    private Hlayout hlInfo;
    private Vlayout vlContent, vlStudentInfo, vlOtherStudents;

    private JdbcStudentService jdbcStudentService = new JdbcJdbcStudentImpl();
    private ApiSyncMineStudentService apiSyncMineStudentService = new ApiSyncMineStudentImpl();

    private CompareResult<StudentCurrentSemesterDto> selectedStudent;
    private List<CompareSubjectModel> compareSubjects = new ArrayList<>();

    public WinStudentInfoSyncCtrl(CompareResult<StudentCurrentSemesterDto> selectedStudent) {
        this.selectedStudent = selectedStudent;

        fill();
    }

    private void fill() {

        fillDefaultSettings();
        createStudentInfoLayout();
        createFindOtherStudents();
        createActionButtons();
    }

    private void fillDefaultSettings() {

        setTitle("Информация по студенту");

        if (selectedStudent.getCompareObj() != null && selectedStudent.getLinkedObj() != null) {
            setHeight("60%");
            setWidth("50%");
        } else {
            setHeight("90%");
            setWidth("80%");
        }

        vlContent = new Vlayout();
        vlContent.setParent(this);
        vlContent.setHflex("1");
        vlContent.setVflex("1");

        hlInfo = new Hlayout();
        hlInfo.setParent(vlContent);
        hlInfo.setHflex("1");
        hlInfo.setVflex("1");
    }

    private void createStudentInfoLayout() {

        vlStudentInfo = new Vlayout();
        vlStudentInfo.setParent(hlInfo);
        vlStudentInfo.setHflex("1");
        vlStudentInfo.setVflex("1");

        createGrid();
        prepareData();
        fillGrid();
    }

    private void createGrid() {

        gridStudentInfo = new Grid();
        gridStudentInfo.setParent(vlStudentInfo);
        gridStudentInfo.setHflex("1");
        gridStudentInfo.setVflex("1");

        Rows rows = new Rows();
        rows.setParent(gridStudentInfo);

        Row row = new Row();
        row.setParent(rows);
        new Label("Названия свойств").setParent(row);
        new Label("Значение из шахт").setParent(row);
        new Label("Значение из АСУ ИКИТ").setParent(row);
        new Label("Синхронизировать?").setParent(row);
    }

    private void prepareData() {

        StudentCurrentSemesterDto cabinetObj = selectedStudent.getCompareObj();
        StudentCurrentSemesterDto mineObj = selectedStudent.getLinkedObj();

        compareSubjects.addAll(Arrays.asList(
                new CompareSubjectModel(false, "family", "Фамилия",
                        mineObj == null ? null : mineObj.getFamily(),
                        cabinetObj == null ? null : cabinetObj.getFamily()),

                new CompareSubjectModel(true, "name", "Имя",
                        mineObj == null ? null : mineObj.getName(),
                        cabinetObj == null ? null : cabinetObj.getName()),

                new CompareSubjectModel(true, "patronymic", "Отчество",
                        mineObj == null ? null : mineObj.getPatronymic(),
                        cabinetObj == null ? null : cabinetObj.getPatronymic()),

                new CompareSubjectModel(true, "birthday", "Дата рождения",
                        mineObj == null || mineObj.getBirthday() == null ? null : DateConverter.convertDateToString(mineObj.getBirthday()),
                        cabinetObj == null || cabinetObj.getBirthday() == null ? null : DateConverter.convertDateToString(cabinetObj.getBirthday())),

                new CompareSubjectModel(true, "recordbook", "Зачетная книжка",
                        mineObj == null ? null : mineObj.getRecordbook(),
                        cabinetObj == null ? null : cabinetObj.getRecordbook()),

                new CompareSubjectModel(true, "foreigner", "Иностранец",
                        mineObj == null ? null : mineObj.getForeigner() ? "Да" : "Нет",
                        cabinetObj == null ? null : cabinetObj.getForeigner() ? "Да" : "Нет"),

                new CompareSubjectModel(true, "sex", "Пол",
                        mineObj == null || mineObj.getSex() == null ? null : mineObj.getSex().equals(1) ? "Муж." : "Жен.",
                        cabinetObj == null || cabinetObj.getSex() == null ? null : cabinetObj.getSex().equals(1) ? "Муж." : "Жен."),

                new CompareSubjectModel(true, "conditionOfEducation", "Условия обучения",
                        mineObj == null ? null : mineObj.getConditionOfEducation(),
                        cabinetObj == null ? null : cabinetObj.getConditionOfEducation()),

                new CompareSubjectModel(true, "studentStatus", "Статус студента",
                        mineObj == null || mineObj.getStatus() == null ? null : StudentStatus.getStatusByValue(mineObj.getStatus()).getName(),
                        cabinetObj == null || cabinetObj.getStatus() == null ? null : StudentStatus.getStatusByValue(cabinetObj.getStatus()).getName()),

                new CompareSubjectModel(true, "idStudentcardMine", "Идентификатор студента в Шахтах",
                        mineObj == null ? null : String.valueOf(mineObj.getIdStudentcardMine()),
                        cabinetObj == null ? null : String.valueOf(cabinetObj.getIdStudentcardMine()))
        ));
    }

    private void fillGrid() {

        for (CompareSubjectModel compareSubject : compareSubjects) {
            createRowContent(compareSubject);
        }
    }

    private void createRowContent(CompareSubjectModel compareSubject) {

        Row row = new Row();
        row.setParent(gridStudentInfo.getRows());
        new Label(compareSubject.getColumnKey()).setParent(row);

        //mine value
        Hlayout hlMineValue = new Hlayout();
        hlMineValue.setParent(row);
        hlMineValue.setHflex("1");

        Label lMineValue = new Label();
        lMineValue.setParent(hlMineValue);
        if (compareSubject.getMineValue() == null) {
            row.setStyle("background: #FFFE7E;");
        } else {
            lMineValue.setValue(compareSubject.getMineValue());
        }

        //cabinet value
        Hlayout hlCabinetValue = new Hlayout();
        hlCabinetValue.setParent(row);
        hlCabinetValue.setHflex("1");

        Label lCabinetValue = new Label();
        lCabinetValue.setParent(hlCabinetValue);
        if (compareSubject.getCabinetValue() == null) {
            row.setStyle("background: #FFFE7E;");
        } else {
            lCabinetValue.setValue(compareSubject.getCabinetValue());
        }

        if (!Objects.equals(compareSubject.getMineValue(), compareSubject.getCabinetValue())) {
            row.setStyle("background: #FFFE7E;");
        }


        if (compareSubject.isVisibleCheckbox()) {
            Checkbox checkbox = new Checkbox();
            checkbox.setParent(row);
            checkbox.setChecked(compareSubject.isComparable());
            checkbox.addEventListener(Events.ON_CHECK, event -> compareSubject.setComparable(checkbox.isChecked()));
        } else {
            new Label("").setParent(row);
        }
    }

    private void createFindOtherStudents() {

        if (selectedStudent.getCompareObj() != null && selectedStudent.getLinkedObj() != null) {
            return;
        }

        vlOtherStudents = new Vlayout();
        vlOtherStudents.setParent(hlInfo);
        vlOtherStudents.setHflex("1");
        vlOtherStudents.setVflex("1");

        if (selectedStudent.getCompareObj() != null) {
            createListboxToFoundMineStudents();
        } else if (selectedStudent.getLinkedObj() != null) {
            createListboxToFindHumanfaces();
        }
    }

    private void createListboxToFoundMineStudents() {

        ListboxStudentSearch lbStudentSearch = new ListboxStudentSearch();
        lbStudentSearch.setParent(vlOtherStudents);
        lbStudentSearch.setSearchStudentListener(() -> fillListboxByFoundMineStudents(lbStudentSearch));

        lbStudentSearch.tbFamily.setValue(selectedStudent.getCompareObj().getFamily());
        fillListboxByFoundMineStudents(lbStudentSearch);
    }

    private void fillListboxByFoundMineStudents(ListboxStudentSearch lbStudentSearch) {

        StudentForComparingFilter filter = StudentForComparingFilter.hiddenBuilder()
                .onlyNullCabinet(true)
                .groupname(lbStudentSearch.tbGroupname.getValue()).familyMine(lbStudentSearch.tbFamily.getValue())
                .nameMine(lbStudentSearch.tbName.getValue()).patronymicMine(lbStudentSearch.tbPatronymic.getValue())
                .build();

        lbStudentSearch.setModel(new ListModelList<>(apiSyncMineStudentService.getMineStudents(filter)));
        lbStudentSearch.renderAll();
    }

    private void createListboxToFindHumanfaces() {

        ListboxStudentSearch lbHumanfaceSearch = new ListboxStudentSearch();
        lbHumanfaceSearch.setParent(vlOtherStudents);

        reCreateListHead(lbHumanfaceSearch);
        lbHumanfaceSearch.setMold("paging");
        lbHumanfaceSearch.setAutopaging(true);

        lbHumanfaceSearch.setLinkStudentsListener(this::linkHumanForSelectedStudent);
        lbHumanfaceSearch.setSearchStudentListener(() -> fillListboxByHumanfaces(lbHumanfaceSearch));

        lbHumanfaceSearch.tbFamily.setValue(selectedStudent.getLinkedObj().getFamily());
        fillListboxByFoundMineStudents(lbHumanfaceSearch);
    }

    private void reCreateListHead(ListboxStudentSearch lbHumanfaceSearch) {

        lbHumanfaceSearch.clearListHeadChildren();
        lbHumanfaceSearch.createListheader("Подразделение");
        lbHumanfaceSearch.createListheader("Фамилия");
        lbHumanfaceSearch.createListheader("Имя");
        lbHumanfaceSearch.createListheader("Отчество");
        lbHumanfaceSearch.createListheader("");
    }

    private void linkHumanForSelectedStudent(StudentCurrentSemesterDto selectedHuman) {

        DialogUtil.questionWithYesNoButtons(
                "Уверены, что хотите создать студента на основе пользователя (" +
                selectedHuman.getFamily() + " " + selectedHuman.getName() + " " + selectedHuman.getPatronymic() + ")?",
                "Создать студента?",
                event -> createStudent(selectedHuman));
    }

    private void fillListboxByHumanfaces(ListboxStudentSearch lbHumanfaceSearch) {

        StudentForComparingFilter filter = StudentForComparingFilter.hiddenBuilder()
                .groupname(lbHumanfaceSearch.tbGroupname.getValue()).familyCabinet(lbHumanfaceSearch.tbFamily.getValue())
                .nameCabinet(lbHumanfaceSearch.tbName.getValue()).patronymicCabinet(lbHumanfaceSearch.tbPatronymic.getValue())
                .build();

        lbHumanfaceSearch.setModel(new ListModelList<>(jdbcStudentService.getCabinetHumafnaces(filter)));
        lbHumanfaceSearch.renderAll();
    }

    private void createActionButtons() {

        Hlayout hlActionButtons = new Hlayout();
        hlActionButtons.setParent(vlContent);
        hlActionButtons.setHeight("30px");
        hlActionButtons.setHflex("1");

        Button btnCancel = new Button("Отмена");
        btnCancel.setParent(hlActionButtons);
        btnCancel.addEventListener(Events.ON_CLICK, event -> this.detach());

        if (selectedStudent.getCompareObj() != null && selectedStudent.getLinkedObj() != null) {
            Button btnSync = new Button("Синхронизировать");
            btnSync.setParent(hlActionButtons);
            btnSync.addEventListener(Events.ON_CLICK, event -> syncColumns());
        }
        if (selectedStudent.getCompareObj() == null) {
            Button btnCreate = new Button("Создать студента");
            btnCreate.setParent(hlActionButtons);
            btnCreate.addEventListener(Events.ON_CLICK, event -> createStudent(null));
        }
        if (selectedStudent.getLinkedObj() == null) {
            Button btnDelete = new Button("Удалить из АСУ ИКИТ");
            btnDelete.setParent(hlActionButtons);
            btnDelete.addEventListener(Events.ON_CLICK, event -> deleteStudent());
        }
    }

    private void syncColumns() {

        apiSyncMineStudentService.syncStudent(selectedStudent, compareSubjects);
        detachCurrentWin();
    }

    private void createStudent(StudentCurrentSemesterDto selectedHuman) {

        StudentCurrentSemesterDto mineObject = selectedStudent.getLinkedObj();
        switch (StudentStatus.getStatusByValue(mineObject.getStatus())) {
            case DEDUCTED:
            case EDUCATION_COMPLETED:
                DialogUtil.exclamation("Невозможно создать студента со статусом 'Отчислен' или 'Завершил обучение'");
                return;
        }
        apiSyncMineStudentService.createStudent(selectedStudent, selectedHuman);
        detachCurrentWin();
    }

    private void deleteStudent() {

        apiSyncMineStudentService.deleteStudent(selectedStudent);
        detachCurrentWin();
    }

    private void detachCurrentWin() {
        Events.postEvent("onFinishSync", this, null);
        this.detach();
    }
}
