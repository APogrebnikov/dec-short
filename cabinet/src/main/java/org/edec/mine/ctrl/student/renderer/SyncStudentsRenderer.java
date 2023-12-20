package org.edec.mine.ctrl.student.renderer;

import org.edec.cloud.sync.mine.api.student.data.StudentCurrentSemesterDto;
import org.edec.cloud.sync.mine.api.utility.compareResult.CompareResult;
import org.edec.cloud.sync.mine.api.utility.compareResult.CompareResultStatus;
import org.edec.mine.ctrl.student.WinStudentInfoSyncCtrl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class SyncStudentsRenderer implements ListitemRenderer<CompareResult<StudentCurrentSemesterDto>> {

    private Runnable searchStudentsAction;

    public SyncStudentsRenderer(Runnable searchStudentsAction) {
        this.searchStudentsAction = searchStudentsAction;
    }

    @Override
    public void render(Listitem li, CompareResult<StudentCurrentSemesterDto> data, int index) throws Exception {

        li.setValue(data);

        StudentCurrentSemesterDto compareObj = data.getCompareObj();
        StudentCurrentSemesterDto linkedObj = data.getLinkedObj();

        new Listcell(compareObj == null ? linkedObj.getGroupname() : compareObj.getGroupname()).setParent(li);
        createListcells(li, compareObj);
        createListcells(li, linkedObj);

        li.addEventListener(Events.ON_RIGHT_CLICK, event -> showCompareWindow(li.getListbox().getParent().getParent(), data));

        li.setStyle(data.getCompareStatus() == CompareResultStatus.FULL_EQUAL
                ? ""
                : data.getCompareStatus() == CompareResultStatus.PARTLY_EQUAL
                ? "background: #FFFE7E;"
                : "background: #FF7373;");
    }

    private void createListcells(Listitem li, StudentCurrentSemesterDto obj) {

        if (obj == null) {
            new Listcell().setParent(li);
            new Listcell().setParent(li);
            new Listcell().setParent(li);
        } else {
            new Listcell(obj.getFamily()).setParent(li);
            new Listcell(obj.getName()).setParent(li);
            new Listcell(obj.getPatronymic()).setParent(li);
        }
    }

    private void showCompareWindow(Component parent, CompareResult<StudentCurrentSemesterDto> data) {

        WinStudentInfoSyncCtrl win = new WinStudentInfoSyncCtrl(data);
        win.setParent(parent);
        win.addEventListener("onFinishSync", event -> searchStudentsAction.run());
        win.doModal();
    }
}
