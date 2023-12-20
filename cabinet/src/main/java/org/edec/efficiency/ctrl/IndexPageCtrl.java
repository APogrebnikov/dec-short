package org.edec.efficiency.ctrl;

import org.edec.efficiency.ctrl.renderer.*;
import org.edec.efficiency.model.ConfigurationEfficiency;
import org.edec.efficiency.model.ProblemGroup;
import org.edec.efficiency.model.StatusEfficiency;
import org.edec.efficiency.service.EfficiencyService;
import org.edec.efficiency.service.impl.EfficiencyImpl;
import org.edec.model.EmployeeModel;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.List;


public class IndexPageCtrl extends CabinetSelector {
    @Wire
    private Button btnSave;
    @Wire
    private Checkbox chAttendance, chPerformance, chActivityEok, chFizra, chMagister;
    @Wire
    private Combobox cmbProblemStudentCourse, cmbManageStudentCourse, cmbCourseAllStudent;
    @Wire
    private Label lRedLevel, lYellowLevel, lGreenLevel;
    @Wire
    private Listbox lbGroupConfig, lbSubjectConfig, lbProblemGroup, lbManageGroup, lbAllStudent;
    @Wire
    private Slider slRed, slGreen;
    @Wire
    private Tab tabConfiguration, tabProblemStudent, tabManageStudent, tabAllStudent;
    @Wire
    private Tabbox tbEfficiency;
    @Wire
    private Tabpanel tabpConfigure;
    @Wire
    private Textbox tbProblemStudentGroupname, tbProblemStudentFio, tbManageStudentGroupname, tbManageStudentFio;
    @Wire
    private Textbox tbAllStudentGroupname, tbAllStudentFio;

    private EfficiencyService efficiencyService = new EfficiencyImpl();

    private ConfigurationEfficiency configuration;
    private Long idEmp;
    private Long idSem;

    @Override
    protected void fill () {
        idSem = efficiencyService.getCurrentSem(currentModule.getDepartments().get(0).getIdInstitute(), currentModule.getFormofstudy());
        idEmp = efficiencyService.getEmployee(template.getCurrentUser().getIdHum());
        lbManageGroup.setItemRenderer(new GroupManageRenderer(idEmp));
        Clients.showBusy(lbManageGroup, "Загрузка данных");
        Events.echoEvent("onLater", lbManageGroup, null);
        if (currentModule.isReadonly()) {
            tabConfiguration.setDisabled(true);
            tabConfiguration.setVisible(false);
            tabProblemStudent.setDisabled(true);
            tabProblemStudent.setVisible(false);
            tabAllStudent.setDisabled(true);
            tabAllStudent.setVisible(false);
        } else {
            fillNotReadonly();
        }
    }

    private void fillNotReadonly () {
        List<EmployeeModel> employees = efficiencyService.getEmployeeByDepartment(currentModule.getDepartments().get(0).getIdDepartment());
        lbProblemGroup.setItemRenderer(new GroupRenderer(employees));
        lbAllStudent.setItemRenderer(new GroupAllStudentRenderer(employees));
        Clients.showBusy(lbProblemGroup, "Загрузка данных");
        Events.echoEvent("onLater", lbProblemGroup, null);
        configuration = efficiencyService.getConfiguration(idSem);
        chAttendance.setChecked(configuration.getAttendance());
        chActivityEok.setChecked(configuration.getEok());
        chPerformance.setChecked(configuration.getPerformance());
        chMagister.setChecked(configuration.getMaster());
        chFizra.setChecked(configuration.getPhyscul());
        String qualification = configuration.getMaster() ? "1,2,3" : "1,2";
        lbGroupConfig.setItemRenderer(new GroupConfigRenderer());
        lbGroupConfig.setModel(new ListModelList<>(efficiencyService.getProblemGroupsConfiguration(idSem, qualification)));

        lbSubjectConfig.setItemRenderer(new SubjectGroupRenderer());
        slRed.setMaxpos(configuration.getMinGreenLevel() - 2);
        slRed.setCurpos(configuration.getMaxRedLevel());
        slGreen.setMinpos(configuration.getMaxRedLevel() + 2);
        slGreen.setCurpos(configuration.getMinGreenLevel());
        changeSliders();
    }

    @Listen("onScroll = #slRed; onScroll = #slGreen;")
    public void changeSliders () {
        int maxRedLevel = slRed.getCurpos();
        int minGreenLevel = slGreen.getCurpos();
        slRed.setMaxpos(minGreenLevel - 2);
        slGreen.setMinpos(maxRedLevel + 2);
        lRedLevel.setValue("Красный уровень: 0 - " + slRed.getCurpos());
        lYellowLevel.setValue("Желтый уровень: " + (slRed.getCurpos() + 1) + " - " + (slGreen.getCurpos() - 1));
        lGreenLevel.setValue("Зеленый уровень: " + slGreen.getCurpos() + " - 100");
    }

    @Listen("onSelect = #lbGroupConfig")
    public void selectGroupConfig () {
        ProblemGroup selectedGroup = lbGroupConfig.getSelectedItem().getValue();
        lbSubjectConfig.setModel(new ListModelList<>(selectedGroup.getSubjects().size() == 0
                                                     ? efficiencyService.getProblemSubjectGroups(selectedGroup.getIdLGS())
                                                     : selectedGroup.getSubjects()));
        lbSubjectConfig.renderAll();
    }

    @Listen("onClick = #btnSave")
    public void saveConfiguration () {
        if (!chAttendance.isChecked() && !chActivityEok.isChecked() && !chPerformance.isChecked()) {
            PopupUtil.showWarning("Нужно учитывать хотя бы один из параметров: Успеваемость/Посещаемость/Активность ЭОК");
            return;
        }
        configuration.setAttendance(chAttendance.isChecked());
        configuration.setEok(chActivityEok.isChecked());
        configuration.setMaster(chMagister.isChecked());
        configuration.setPerformance(chPerformance.isChecked());
        configuration.setPhyscul(chFizra.isChecked());
        configuration.setMaxRedLevel(slRed.getCurpos());
        configuration.setMinGreenLevel(slGreen.getCurpos());
        Clients.showBusy("Обновление конфигурации");
        Events.echoEvent("onLater", btnSave, null);
    }

    @Listen("onLater = #btnSave")
    public void saveSettings () {
        if (efficiencyService.updateConfiguration(configuration)) {
            PopupUtil.showInfo("Удалось обновить настройки!");
            fillNotReadonly();
        } else {
            PopupUtil.showError("Не удалось обновить настройки!");
        }
        Clients.clearBusy();
    }

    @Listen("onClick = #btnSearchProblemStudent; onSelect = #tabProblemStudent;")
    public void searchProblemStudent () {
        Clients.showBusy(lbProblemGroup, "Загрузка данных");
        Events.echoEvent("onLater", lbProblemGroup, null);
    }

    @Listen("onLater = #lbProblemGroup")
    public void onLaterLbProblemGroup () {
        lbProblemGroup.setModel(new ListModelList<>(
                efficiencyService.getProblemGroups(idSem, null, cmbProblemStudentCourse.getSelectedIndex(),
                                                   tbProblemStudentGroupname.getValue(), tbProblemStudentFio.getValue(),
                                                   StatusEfficiency.CREATED.getStatus() + ", " + StatusEfficiency.CONFIRM.getStatus(), false
                )));
        lbProblemGroup.renderAll();
        Clients.clearBusy(lbProblemGroup);
    }

    @Listen("onClick = #btnSearchManageStudent; onSelect = #tabManageStudent;")
    public void searchManagetStudent () {
        Clients.showBusy(lbManageGroup, "Загрузка данных");
        Events.echoEvent("onLater", lbManageGroup, null);
    }

    @Listen("onLater = #lbManageGroup")
    public void onLaterLbManageGroup () {
        lbManageGroup.setModel(new ListModelList<>(
                efficiencyService.getProblemGroups(idSem, idEmp, cmbManageStudentCourse.getSelectedIndex(),
                                                   tbManageStudentGroupname.getValue(), tbManageStudentFio.getValue(),
                                                   StatusEfficiency.CREATED.getStatus() + ", " + StatusEfficiency.CONFIRM.getStatus(), false
                )));
        lbManageGroup.renderAll();
        Clients.clearBusy(lbManageGroup);
    }

    @Listen("onClick = #btnSearchAllStudent; onSelect = #tabAllStudent;")
    public void searchAllStudent () {
        Clients.showBusy(lbAllStudent, "Загрузка данных");
        Events.echoEvent("onLater", lbAllStudent, null);
    }

    @Listen("onLater = #lbAllStudent;")
    public void onLaterLbAllStudent () {
        lbAllStudent.setModel(new ListModelList<>(
                efficiencyService.getProblemGroups(idSem, null, cmbCourseAllStudent.getSelectedIndex(), tbAllStudentGroupname.getValue(),
                                                   tbAllStudentFio.getValue(),
                                                   StatusEfficiency.CREATED.getStatus() + ", " + StatusEfficiency.CONFIRM.getStatus(), true
                )));
        lbAllStudent.renderAll();
        Clients.clearBusy(lbAllStudent);
    }
}