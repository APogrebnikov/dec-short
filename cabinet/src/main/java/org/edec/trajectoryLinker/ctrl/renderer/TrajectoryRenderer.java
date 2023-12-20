package org.edec.trajectoryLinker.ctrl.renderer;

import org.edec.trajectoryLinker.model.TrajectoryModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class TrajectoryRenderer implements ListitemRenderer<TrajectoryModel> {

    @Override
    public void render (Listitem listitem, TrajectoryModel trajectoryModel, int i) throws Exception {
        listitem.setValue(trajectoryModel);

        new Listcell(trajectoryModel.getTrajectoryName()).setParent(listitem);
    }
}
