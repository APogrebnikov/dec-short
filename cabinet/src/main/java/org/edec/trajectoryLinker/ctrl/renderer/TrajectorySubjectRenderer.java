package org.edec.trajectoryLinker.ctrl.renderer;

import org.edec.trajectoryLinker.model.SubjectModel;
import org.edec.trajectoryLinker.service.TrajectoryLinkerService;
import org.edec.trajectoryLinker.service.impl.TrajectoryLinkerServiceImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class TrajectorySubjectRenderer implements ListitemRenderer<SubjectModel> {

    private Runnable fillTrajectorySubject;
    private Runnable fillAllSubjects;

    TrajectoryLinkerService service = new TrajectoryLinkerServiceImpl();

    public TrajectorySubjectRenderer (Runnable fillTrajectorySubject, Runnable fillAllSubjects) {
        this.fillTrajectorySubject = fillTrajectorySubject;
        this.fillAllSubjects = fillAllSubjects;
    }

    @Override
    public void render (Listitem listitem, SubjectModel subjectModel, int i) throws Exception {
        listitem.setValue(subjectModel);

        new Listcell(subjectModel.getSubjectCurBlock()).setParent(listitem);
        new Listcell(subjectModel.getSubjectName()).setParent(listitem);
        new Listcell(subjectModel.getSemesterNumber().toString()).setParent(listitem);

        Button btn = new Button("Удалить");

        Listcell btnListcell = new Listcell();
        btn.addEventListener(Events.ON_CLICK, event -> {
            if (service.deleteSubject(subjectModel.getIdCurSubjectTrajectory())) {
                fillTrajectorySubject.run();
                fillAllSubjects.run();

                PopupUtil.showInfo("Предмет успешно удален!");
            } else {
                PopupUtil.showError("Не удалось удалить предмет!");
            }
        });

        btn.setParent(btnListcell);
        btnListcell.setParent(listitem);
    }
}
