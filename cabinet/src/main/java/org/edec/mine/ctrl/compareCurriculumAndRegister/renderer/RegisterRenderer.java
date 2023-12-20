package org.edec.mine.ctrl.compareCurriculumAndRegister.renderer;

import org.edec.cloud.sync.mine.api.compareRegisterAndCurriculum.data.response.RegisterResponse;
import org.edec.cloud.sync.mine.api.utility.constants.MineTypeOfRegisterEnum;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class RegisterRenderer implements ListitemRenderer<RegisterResponse> {

    @Override
    public void render(Listitem li, RegisterResponse data, int index) throws Exception {

        li.setValue(data);
        new Listcell(data.getGroupName()).setParent(li);
        new Listcell(data.getSubjectName()).setParent(li);
        new Listcell(String.valueOf(data.getSemesterNumber())).setParent(li);
        new Listcell(MineTypeOfRegisterEnum.getTypeNameById(data.getTypeOfRegister())).setParent(li);
    }
}
