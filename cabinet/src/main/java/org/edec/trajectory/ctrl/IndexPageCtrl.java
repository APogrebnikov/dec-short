package org.edec.trajectory.ctrl;

import org.edec.commons.model.CurriculumModel;
import org.edec.commons.entity.dec.DicTrajectory;
import org.edec.commons.model.DirectionModel;
import org.edec.commons.model.SchoolYearModel;
import org.edec.trajectory.ctrl.renderer.TrajectoryRenderer;
import org.edec.trajectory.model.TrajectoryModel;
import org.edec.trajectory.service.TrajectoryService;
import org.edec.trajectory.service.impl.TrajectoryImpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class IndexPageCtrl extends CabinetSelector {

    //Компоненты
    @Wire
    private Combobox cmbTrajectoryName, cmbSchoolYear, cmbTrajectoryCurriculum;
    @Wire
    private Listbox lbDirection, lbTrajectory;

    //Сервисы
    private TrajectoryService trajectoryService = new TrajectoryImpl();

    //Переменные
    private List<DicTrajectory> dicTrajectories;

    @Override
    protected void fill () {
        for (SchoolYearModel schoolYearModel : trajectoryService.getAllSchoolYears()) {
            Comboitem ci = new Comboitem(schoolYearModel.toString());
            ci.setParent(cmbSchoolYear);
            ci.setValue(schoolYearModel);
        }
        cmbSchoolYear.setSelectedIndex(0);
        Events.echoEvent(Events.ON_SELECT, cmbSchoolYear, null);

        //Рендерер
        lbDirection.setItemRenderer((listitem, o, i) -> {
            DirectionModel direction = (DirectionModel) o;
            listitem.setValue(o);
            new Listcell(direction.getTitle() + " (" + direction.getCode() + ")").setParent(listitem);
        });
    }

    @Listen("onSelect = #cmbSchoolYear")
    public void selectedCmbSchoolYear () {
        SchoolYearModel selectedSchoolYear = cmbSchoolYear.getSelectedItem().getValue();
        lbDirection.setModel(new ListModelList<>(trajectoryService.getDirectionBySchoolYear(selectedSchoolYear.getIdSchoolYear())));
        lbDirection.renderAll();
    }

    @Listen("onClick = #lbDirection")
    public void selectDirection () {
        if (lbDirection.getSelectedItem() == null) {
            return;
        }
        //chTrajectoryCurrentYear.setChecked(false);
        cmbTrajectoryCurriculum.setValue("");
        cmbTrajectoryCurriculum.getItems().clear();
        cmbTrajectoryName.setValue("");

        DirectionModel selectedDirection = lbDirection.getSelectedItem().getValue();

        dicTrajectories = trajectoryService.getDicTrajectories(selectedDirection.getIdDirection());
        cmbTrajectoryName.setModel(new ListModelList<>(dicTrajectories));

        lbTrajectory.setItemRenderer(new TrajectoryRenderer(dicTrajectories));
        lbTrajectory.setModel(new ListModelList<>(trajectoryService.getTrajectoryByDirection(selectedDirection)));
        lbTrajectory.renderAll();

        for (CurriculumModel curriculum : selectedDirection.getListCurriculum()) {
            Comboitem ci = new Comboitem(curriculum.getSpecialityTitle() + " (" + curriculum.getDirectionCode() + ") " +
                                         FormOfStudy.getFormOfStudyByType(curriculum.getFormOfStudy()).getShortName() + ", " +
                                         curriculum.getCreatedYear() +
                                         (curriculum.getPlanFileName() != null ? ", " + curriculum.getPlanFileName() : ""));
            ci.setValue(curriculum);
            ci.setParent(cmbTrajectoryCurriculum);
        }
        //Скрываем лишнюю информацию, если элемент всего 1
        if (selectedDirection.getListCurriculum().size() == 1) {
            cmbTrajectoryCurriculum.setVisible(false);
            cmbTrajectoryCurriculum.setSelectedIndex(0);
        } else {
            cmbTrajectoryCurriculum.setVisible(true);
        }
    }

    @Listen("onClick = #btnTrajectoryCreate")
    public void clickOnCreateTrajectory () {
        if (cmbTrajectoryCurriculum.getSelectedItem() == null || cmbTrajectoryName.getValue().equals("")) {
            PopupUtil.showWarning("Заполните название траектории и выберите учебный план");
            return;
        }
        CurriculumModel selectedCurriculum = cmbTrajectoryCurriculum.getSelectedItem().getValue();
        DicTrajectory dicTrajectory;
        if (cmbTrajectoryName.getSelectedItem() == null) {
            dicTrajectory = new DicTrajectory();
            dicTrajectory.setName(cmbTrajectoryName.getValue());
            dicTrajectory = trajectoryService.createDicTrajectory(dicTrajectory);
            dicTrajectories.add(dicTrajectory);
        } else {
            dicTrajectory = cmbTrajectoryName.getSelectedItem().getValue();
        }

        TrajectoryModel trajectory = new TrajectoryModel();
        //trajectory.setCurrentYear(chTrajectoryCurrentYear.isChecked());
        trajectory.setCurrentYear(false);
        trajectory.setCurriculum(selectedCurriculum);
        trajectory.setIdDicTrajectory(dicTrajectory.getId());
        trajectoryService.updateOrCreateTrajectory(trajectory);

        lbTrajectory.setItemRenderer(new TrajectoryRenderer(dicTrajectories));
        Events.echoEvent(Events.ON_CLICK, lbDirection, null);
    }
}
