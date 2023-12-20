package org.edec.mine.ctrl.compareCurriculumAndRegister.renderer;

import org.edec.cloud.sync.mine.api.compareRegisterAndCurriculum.data.response.CurriculumSubjectResponse;
import org.edec.cloud.sync.mine.api.utility.constants.MineTypeOfRegisterEnum;
import org.edec.mine.ctrl.compareCurriculumAndRegister.WinFindRegisterCtrl;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.HashMap;
import java.util.Map;

public class CurriculumSubjectRenderer implements ListitemRenderer<CurriculumSubjectResponse> {

    private boolean rightClickEventEnable;
    private Long idInst;
    private Integer formOfStudy;

    public CurriculumSubjectRenderer(boolean rightClickEventEnable, Long idInst, Integer formOfStudy) {
        this.rightClickEventEnable = rightClickEventEnable;
        this.idInst = idInst;
        this.formOfStudy = formOfStudy;
    }

    @Override
    public void render(Listitem li, CurriculumSubjectResponse data, int index) throws Exception {

        li.setValue(data);

        new Listcell(data.getGroupName()).setParent(li);
        new Listcell(data.getSubjectName()).setParent(li);
        new Listcell(String.valueOf(data.getSemesterNumber())).setParent(li);
        new Listcell(MineTypeOfRegisterEnum.getTypeNameById(data.getTypeOfRegister())).setParent(li);

        if (rightClickEventEnable) {
            li.addEventListener(Events.ON_RIGHT_CLICK, event -> {

                Map<String, Object> arg = new HashMap<>();
                arg.put(WinFindRegisterCtrl.ARG_FORM_OF_STUDY, formOfStudy);
                arg.put(WinFindRegisterCtrl.ARG_INSTITUTE, idInst);
                arg.put(WinFindRegisterCtrl.ARG_SELECTED_GROUP, data.getGroupName());

                ComponentHelper
                        .createWindow("/mine/compareCurriculumAndRegister/winFindRegister.zul", "winFindRegister", arg)
                        .doModal();
            });
        }
    }
}
