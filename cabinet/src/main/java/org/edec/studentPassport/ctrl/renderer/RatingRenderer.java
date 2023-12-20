package org.edec.studentPassport.ctrl.renderer;

import org.edec.utility.component.model.RatingModel;
import org.edec.utility.constants.RatingConst;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * @author Max Dimukhametov
 */
public class RatingRenderer implements ListitemRenderer<RatingModel> {
    @Override
    public void render (Listitem li, RatingModel data, int index) throws Exception {
        li.setValue(data);

        new Listcell(String.valueOf(index + 1)).setParent(li);
        new Listcell(data.getSubjectname()).setParent(li);
        new Listcell(data.getFoc()).setParent(li);
        new Listcell(data.getSemester()).setParent(li);
        new Listcell(RatingConst.getNameByRating(data.getRating())).setParent(li);

        if ((data.getRating() == 2 || data.getRating() < 1) && data.getRating() != -1) {
            li.setStyle("background: #ffcccc;");
        }
    }
}
