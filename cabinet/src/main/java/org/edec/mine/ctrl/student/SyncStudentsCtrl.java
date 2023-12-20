package org.edec.mine.ctrl.student;

import org.edec.cloud.sync.mine.api.student.data.StudentCurrentSemesterDto;
import org.edec.cloud.sync.mine.api.student.data.StudentForComparingFilter;
import org.edec.cloud.sync.mine.api.student.service.ApiSyncMineStudentImpl;
import org.edec.cloud.sync.mine.api.student.service.ApiSyncMineStudentService;
import org.edec.cloud.sync.mine.api.utility.compareResult.CompareResult;
import org.edec.commons.component.MultipleSelectComponent;
import org.edec.mine.ctrl.student.renderer.SyncStudentsRenderer;
import org.edec.utility.constants.ConditionOfEducation;
import org.edec.utility.constants.StudentStatus;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.IncludeSelector;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SyncStudentsCtrl extends IncludeSelector {

    private MultipleSelectComponent<StudentStatus, Integer> mscStudentStatusMine, mscStudentStatusCabinet;
    private MultipleSelectComponent<ConditionOfEducation, String> mscConditionOfEducationMine, mscConditionOfEducationCabinet;

    @Wire
    private Checkbox chOnlyNullMine, chOnlyNullCabinet;
    @Wire
    private Checkbox chOnlyForeignerMine, chOnlyForeignerCabinet;

    @Wire
    private Listbox lbStudents;

    @Wire
    private Textbox tbGroupName;
    @Wire
    private Textbox tbCabinetFamily, tbCabinetName, tbCabinetPatronymic;
    @Wire
    private Textbox tbMineFamily, tbMineName, tbMinePatronymic;
    @Wire
    private Vlayout vlStudentStatus, vlConditionOfEducation;

    private ApiSyncMineStudentService apiSyncMineStudentService = new ApiSyncMineStudentImpl();

    @Override
    protected void fill() {

        lbStudents.setItemRenderer(new SyncStudentsRenderer(this::searchStudents));
        createBandboxesStudentStatus();
        createBandboxesConditionOfEducation();
    }

    private void createBandboxesStudentStatus() {

        Hlayout hlContent = new Hlayout();
        hlContent.setParent(vlStudentStatus);

        Vlayout vlMine = new Vlayout();
        vlMine.setParent(hlContent);

        new Label("Шахты: ").setParent(vlMine);
        mscStudentStatusMine = new MultipleSelectComponent<>(Arrays.asList(StudentStatus.values()), StudentStatus::getMineValue);
        mscStudentStatusMine.setFillLabelListener(StudentStatus::getShortName);
        mscStudentStatusMine.setParent(vlMine);
        mscStudentStatusMine.addEventListener(Events.ON_CHANGING, event -> searchStudents());

        Vlayout vlCabinet = new Vlayout();
        vlCabinet.setParent(hlContent);

        new Label("АСУ ИКИТ: ").setParent(vlCabinet);
        mscStudentStatusCabinet = new MultipleSelectComponent<>(Arrays.asList(StudentStatus.values()), StudentStatus::getMineValue);
        mscStudentStatusCabinet.setFillLabelListener(StudentStatus::getShortName);
        mscStudentStatusCabinet.setParent(vlCabinet);
        mscStudentStatusCabinet.addEventListener(Events.ON_CHANGING, event -> searchStudents());
    }

    private void createBandboxesConditionOfEducation() {

        Hlayout hlContent = new Hlayout();
        hlContent.setParent(vlConditionOfEducation);

        Vlayout vlMine = new Vlayout();
        vlMine.setParent(hlContent);

        new Label("Шахты: ").setParent(vlMine);
        mscConditionOfEducationMine = new MultipleSelectComponent<>(Arrays.asList(ConditionOfEducation.values()), ConditionOfEducation::getShortName);
        mscConditionOfEducationMine.setFillLabelListener(ConditionOfEducation::getShortName);
        mscConditionOfEducationMine.setParent(vlMine);
        mscConditionOfEducationMine.addEventListener(Events.ON_CHANGING, event -> searchStudents());

        Vlayout vlCabinet = new Vlayout();
        vlCabinet.setParent(hlContent);

        new Label("АСУ ИКИТ: ").setParent(vlCabinet);
        mscConditionOfEducationCabinet = new MultipleSelectComponent<>(Arrays.asList(ConditionOfEducation.values()), ConditionOfEducation::getShortName);
        mscConditionOfEducationCabinet.setFillLabelListener(ConditionOfEducation::getShortName);
        mscConditionOfEducationCabinet.setParent(vlCabinet);
        mscConditionOfEducationCabinet.addEventListener(Events.ON_CHANGING, event -> searchStudents());
    }

    @Listen("onCheck = #chOnlyNullMine; onCheck = #chOnlyNullCabinet; onOK = #tbGroupName;" +
            "onCheck = #chOnlyForeignerMine; onCheck = #chOnlyForeignerCabinet;" +
            "onOK = #tbCabinetFamily; onOK = #tbCabinetName; onOK = #tbCabinetPatronymic;" +
            "onOK = #tbMineFamily; onOK = #tbMineName; onOK = #tbMinePatronymic;" +
            "onClick = #btnSearch;")
    public void searchStudents() {

        StudentForComparingFilter filter = StudentForComparingFilter
                .builder(getSelectedInstitute().getIdInst(), getSelectedFormOfStudy().getType())

                .onlyNullCabinet(chOnlyNullCabinet.isChecked()).onlyNullMine(chOnlyNullMine.isChecked())
                .foreignerCabinet(chOnlyForeignerCabinet.isChecked()).foreignerMine(chOnlyForeignerMine.isChecked())
                .groupname(tbGroupName.getValue())
                .familyCabinet(tbCabinetFamily.getValue()).familyMine(tbMineFamily.getValue())
                .nameCabinet(tbCabinetName.getValue()).nameMine(tbMineName.getValue())
                .patronymicCabinet(tbCabinetPatronymic.getValue()).patronymicMine(tbMinePatronymic.getValue())
                .selectedStatusesCabinet(mscStudentStatusCabinet.getSelectedValues())
                .selectedStatusesMine(mscStudentStatusMine.getSelectedValues())
                .selectedConditionOfEducationsCabinet(mscConditionOfEducationCabinet.getSelectedValues())
                .selectedConditionOfEducationsMine(mscConditionOfEducationMine.getSelectedValues())
                .build();

        ListModelList<CompareResult<StudentCurrentSemesterDto>> lmStudents = new ListModelList<>(apiSyncMineStudentService.getCompareResultWithStudents(filter));
        lmStudents.setMultiple(true);

        lbStudents.setModel(lmStudents);
        lbStudents.renderAll();
    }

    @Listen("onClick = #btnSyncAllSelectedItems")
    public void syncSelectedItems() {

        if (lbStudents.getSelectedCount() == 0) {
            DialogUtil.exclamation("Выберите хотя бы одного студента для синхронизации!");
            return;
        }

        List<CompareResult<StudentCurrentSemesterDto>> selectedStudents = lbStudents.getSelectedItems().stream()
                .map(item -> ((CompareResult<StudentCurrentSemesterDto>) item.getValue()))
                .collect(Collectors.toList());
        openDialogWindowForSyncStudentProperties(selectedStudents);
    }

    @Listen("onClick = #btnSyncAllItems")
    public void syncAllItems() {

        if (lbStudents.getItemCount() == 0) {
            DialogUtil.exclamation("Список студентов пуст, синхронизация невозможна!");
            return;
        }

        List<CompareResult<StudentCurrentSemesterDto>> students = lbStudents.getItems().stream()
                .map(item -> ((CompareResult<StudentCurrentSemesterDto>) item.getValue()))
                .collect(Collectors.toList());
        openDialogWindowForSyncStudentProperties(students);
    }

    private void openDialogWindowForSyncStudentProperties(List<CompareResult<StudentCurrentSemesterDto>> students) {

        WinSelectedStudentsInfoSyncCtrl win = new WinSelectedStudentsInfoSyncCtrl(students);
        win.setParent(getSelf().getParent());
        win.addEventListener("onFinishSync", event -> searchStudents());
        win.doModal();
    }
}
