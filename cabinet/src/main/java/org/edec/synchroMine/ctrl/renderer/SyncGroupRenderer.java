package org.edec.synchroMine.ctrl.renderer;

import org.edec.model.SemesterModel;
import org.edec.synchroMine.model.GroupCompareResult;
import org.edec.utility.compare.compareResult.CompareResult;
import org.edec.utility.compare.compareResult.CompareResultStatus;
import org.edec.utility.component.model.InstituteModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.Arrays;

public class SyncGroupRenderer implements ListitemRenderer<CompareResult<GroupCompareResult>> {

    private InstituteModel institute;
    private SemesterModel semester;

    public SyncGroupRenderer(InstituteModel selectedInst, SemesterModel selectedSemester) {
        institute = selectedInst;
        semester = selectedSemester;
    }

    @Override
    public void render(Listitem li, CompareResult<GroupCompareResult> data, int index) throws Exception {

        li.setValue(data);
        li.setStyle(data.getCompareStatus() == CompareResultStatus.FULL_EQUAL
                ? ""
                : data.getCompareStatus() == CompareResultStatus.PARTLY_EQUAL
                    ? "background: #FFFE7E;"
                    : "background: #FF7373;");

        createListCellForGroup(data.getCompareObj(), li);
        new Listcell().setParent(li);
        createListCellForGroup(data.getLinkedObj(), li);
    }

    private void createListCellForGroup(GroupCompareResult group, Listitem listitem) {
        if (group == null) {
            listitem.getChildren().addAll(Arrays.asList(
                    new Listcell(),
                    new Listcell(),
                    new Listcell()
            ));
        } else {
            new Listcell(group.getGroupname()).setParent(listitem);
            new Listcell(String.valueOf(group.getCourse())).setParent(listitem);
            new Listcell(String.valueOf(group.getIdGroupMine())).setParent(listitem);
        }
    }
}
