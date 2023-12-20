package org.edec.trajectory.ctrl.renderer;

import org.edec.commons.entity.dec.DicTrajectory;
import org.edec.trajectory.model.TrajectoryModel;
import org.edec.trajectory.service.TrajectoryService;
import org.edec.trajectory.service.impl.TrajectoryImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class TrajectoryRenderer implements ListitemRenderer<TrajectoryModel> {

    //Сервисы
    private TrajectoryService trajectoryService = new TrajectoryImpl();

    //Переменные
    private List<DicTrajectory> dicTrajectories;

    public TrajectoryRenderer (List<DicTrajectory> dicTrajectories) {
        this.dicTrajectories = dicTrajectories;
    }

    @Override
    public void render (Listitem listitem, TrajectoryModel trajectory, int i) throws Exception {
        listitem.setValue(trajectory);

        //1 колонка
        Listcell lcName = new Listcell();
        lcName.setParent(listitem);
        Combobox cmbName = new Combobox();
        cmbName.setParent(lcName);
        cmbName.setModel(new ListModelList<>(dicTrajectories));
        cmbName.setAutocomplete(true);
        cmbName.setHflex("1");
        cmbName.setValue(trajectory.getName());

        //2 колонка
        String curriculum = trajectory.getCurriculum().getSpecialityTitle() + "(" + trajectory.getCurriculum().getDirectionCode() + "), " +
                            trajectory.getCurriculum().getCreatedYear() + (trajectory.getCurriculum().getPlanFileName() != null
                                                                           ? ", " + trajectory.getCurriculum().getPlanFileName()
                                                                           : "");
        Listcell lcCurriculum = new Listcell(curriculum);
        lcCurriculum.setParent(listitem);
        lcCurriculum.setTooltiptext(curriculum);

        //3 колонка
        Listcell lcCurrentYear = new Listcell("", trajectory.getCurrentYear() ? "/imgs/okCLR.png" : "");
        lcCurrentYear.setParent(listitem);

        //4 колонка
        Listcell lcAction = new Listcell();
        lcAction.setParent(listitem);
        Button btnSave = new Button("Сохранить");
        btnSave.setParent(lcAction);
        btnSave.addEventListener(Events.ON_CLICK, event -> {
            if (cmbName.getValue().equals("")) {
                Clients.showNotification("Нужно назвать траекторию", cmbName);
                return;
            }
            DicTrajectory selectedDicTrajectory = dicTrajectories.stream()
                                                                 .filter(dicTrajectory -> dicTrajectory.getName()
                                                                                                       .equals(cmbName.getValue()))
                                                                 .findFirst()
                                                                 .orElse(null);
            if (selectedDicTrajectory == null) {
                selectedDicTrajectory = new DicTrajectory();
                selectedDicTrajectory.setName(cmbName.getValue());
                selectedDicTrajectory = trajectoryService.createDicTrajectory(selectedDicTrajectory);
                dicTrajectories.add(selectedDicTrajectory);
            }
            trajectory.setName(cmbName.getValue());
            trajectory.setIdDicTrajectory(selectedDicTrajectory.getId());
            trajectoryService.updateOrCreateTrajectory(trajectory);
            PopupUtil.showInfo("Траектория обновлена");
        });
        Button btnRemove = new Button("Удалить");
        btnRemove.setParent(lcAction);
        btnRemove.addEventListener(Events.ON_CLICK, event -> {
            PopupUtil.showError("Функция еще не рeализована");
        });
    }
}