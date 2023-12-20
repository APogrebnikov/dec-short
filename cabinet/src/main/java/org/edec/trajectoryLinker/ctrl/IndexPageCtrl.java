package org.edec.trajectoryLinker.ctrl;


import org.edec.trajectoryLinker.ctrl.renderer.BlockRenderer;
import org.edec.trajectoryLinker.ctrl.renderer.TrajectoryRenderer;
import org.edec.trajectoryLinker.ctrl.renderer.TrajectorySubjectRenderer;
import org.edec.trajectoryLinker.model.BlockModel;
import org.edec.trajectoryLinker.model.SubjectModel;
import org.edec.trajectoryLinker.model.TrajectoryModel;
import org.edec.trajectoryLinker.service.TrajectoryLinkerService;
import org.edec.trajectoryLinker.service.impl.TrajectoryLinkerServiceImpl;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IndexPageCtrl extends CabinetSelector {

    @Wire
    Listbox lbTrajectories, lbTrajectorySubjects, lbBlocks;

    @Wire
    Label trajectoryName;

    private List<SubjectModel> subjects = new ArrayList<>();
    private List<BlockModel> blocks = new ArrayList<>();
    private List<TrajectoryModel> trajectories = new ArrayList<>();
    private List<SubjectModel> trajectorySubjects = new ArrayList<>();
    private TrajectoryModel selectedTrajectory;

    private TrajectoryLinkerService service = new TrajectoryLinkerServiceImpl();

    private Runnable fillTrajectorySubjects = this::fillTrajectorySubjects;
    private Runnable fillAllSubjects = this::fillAllSubjects;
    private Runnable updateTrajectorySubjects = this::updateTrajectorySubjects;

    @Override
    protected void fill() {

        trajectories = service.getAllTrajectories();
        lbTrajectories.setModel(new ListModelList<>(trajectories));

        lbTrajectories.setAttribute("data", trajectories);
        lbTrajectories.setItemRenderer(new TrajectoryRenderer());
        lbTrajectories.renderAll();

    }

    @Listen("onSelect = #lbTrajectories")
    public void selectTrajectory() {
        lbTrajectorySubjects.getItems().clear();
        selectedTrajectory = lbTrajectories.getSelectedItem().getValue();
        trajectoryName.setValue(selectedTrajectory.getTrajectoryName());

        fillTrajectorySubjects();
        fillAllSubjects();
    }

    public void updateTrajectorySubjects() {
        List<BlockModel> selectedBlocks = lbBlocks.getItems().stream().map((s) -> (BlockModel) s.getValue()).collect(Collectors.toList());
        List<SubjectModel> selectedSubjects = new ArrayList<>();

        for (BlockModel block : selectedBlocks) {
            for (SubjectModel subjectModel : block.getSubjects()) {
                if (subjectModel.getLinked()) selectedSubjects.add(subjectModel);
            }
        }

        if(!service.updateTrajectorySubject(selectedTrajectory.getIdTrajectory(),trajectorySubjects,selectedSubjects)){
            PopupUtil.showError("Привязать предметы к траектории не удалось!");
        }

        fillTrajectorySubjects();
    }

    public void fillTrajectorySubjects() {
        lbTrajectorySubjects.getItems().clear();

        trajectorySubjects = service.getSelectedTrajectorySubjects(selectedTrajectory.getIdTrajectory());

        if (trajectorySubjects.size() == 0) {
            lbTrajectorySubjects.getItems().clear();
            lbTrajectorySubjects.setEmptyMessage("Список привязанных к данной траектории предметов пуст");
        } else {
            lbTrajectorySubjects.setModel(new ListModelList<>(trajectorySubjects));
            lbTrajectorySubjects.setAttribute("data", trajectorySubjects);
            lbTrajectorySubjects.setItemRenderer(new TrajectorySubjectRenderer(fillTrajectorySubjects, fillAllSubjects));
            lbTrajectorySubjects.renderAll();
        }
    }

    public void fillAllSubjects() {

        subjects = service.filterAllSubjectsList(trajectorySubjects, service.getAllSubjects());

        blocks = service.groupSubjectsByBlocks(subjects);

        lbBlocks.setModel(new ListModelList<>(blocks));
        lbBlocks.setAttribute("data", blocks);
        lbBlocks.setItemRenderer(new BlockRenderer(updateTrajectorySubjects));
        lbBlocks.renderAll();

    }

}
