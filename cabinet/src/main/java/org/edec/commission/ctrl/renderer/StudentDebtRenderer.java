package org.edec.commission.ctrl.renderer;

import org.edec.commission.model.StudentDebtModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * Renderer списка кафедр.
 *
 * @author DChiginev
 */
public class StudentDebtRenderer implements ListitemRenderer<StudentDebtModel> {
    /**
     * Render.
     */
    public void render (Listitem li, StudentDebtModel data, int index) {
        li.setValue(data);

        new Listcell(data.getSubjectname()).setParent(li);
        new Listcell(data.getFocStr()).setParent(li);
        new Listcell(data.getFulltitle()).setParent(li);
        new Listcell(data.getSemesterStr()).setParent(li);

        li.setStyle("background: #" + (data.isOpenComm() ? "ffe2bd;" : "fff;"));
    }
}