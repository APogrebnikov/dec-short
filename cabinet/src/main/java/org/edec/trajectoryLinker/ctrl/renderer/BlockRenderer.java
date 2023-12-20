package org.edec.trajectoryLinker.ctrl.renderer;

import org.edec.trajectoryLinker.model.BlockModel;
import org.edec.trajectoryLinker.model.SubjectModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

public class BlockRenderer implements ListitemRenderer<BlockModel> {

    private ComponentService componentService = new ComponentServiceESOimpl();
    private Runnable updateTrajectorySubjects;

    public BlockRenderer (Runnable updateTrajectorySubjects) {
        this.updateTrajectorySubjects = updateTrajectorySubjects;
    }

    @Override
    public void render (Listitem listitem, BlockModel blockModel, int i) throws Exception {
        listitem.setValue(blockModel);

        Listcell lc = new Listcell();
        lc.setParent(listitem);

        Groupbox gb = new Groupbox();
        gb.setParent(lc);
        gb.setMold("3d");
        gb.setOpen(true);

        Caption caption = new Caption();
        caption.setParent(gb);
        Label blockName = new Label(blockModel.getBlockName());
        blockName.setParent(caption);

        final Listbox lbSubject = new Listbox();
        lbSubject.setCheckmark(true);
        lbSubject.setParent(gb);

        Listhead lh = new Listhead();
        lh.setParent(lbSubject);

        lbSubject.setModel(new ListModelList<>(blockModel.getSubjects()));
        lbSubject.setAttribute("data", blockModel.getSubjects());
        lbSubject.setItemRenderer(new SubjectRenderer());

        if (blockModel.getSubjectWasLinked()) {
            for (int j = 0; j < blockModel.getSubjects().size(); j++) {
                if (blockModel.getSubjects().get(j).getLinked()) {
                    lbSubject.setSelectedIndex(j);
                    blockName.setValue(blockName.getValue() + " (" + blockModel.getSubjects().get(j).getSubjectName() + ")");
                    break;
                }
            }
        }

        lbSubject.addEventListener(Events.ON_SELECT, e -> {

            for (int j = 0; j < blockModel.getSubjects().size(); j++) {
                blockModel.getSubjects().get(j).setLinked(false);
            }

            SubjectModel chosenSubject = blockModel.getSubjects().get(lbSubject.getSelectedItem().getIndex());
            blockName.setValue(chosenSubject.getSubjectCurBlock() + " (" + chosenSubject.getSubjectName() + ")");
            blockModel.getSubjects().get(lbSubject.getSelectedItem().getIndex()).setLinked(true);

            updateTrajectorySubjects.run();
        });

        componentService.getListheader("", "26px", "", "").setParent(lh);
        componentService.getListheader("Название", "", "4", "").setParent(lh);
        componentService.getListheader("Номер семестра", "", "1", "center").setParent(lh);

        lbSubject.renderAll();
    }
}
