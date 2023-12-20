package org.edec.contingentMovement.ctrl.renderer;

import org.edec.contingentMovement.model.ResitRatingModel;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class AddedSubjectRenderer implements ListitemRenderer<ResitRatingModel> {

    @Override
    public void render(Listitem li, ResitRatingModel data, int index) throws Exception {

        li.setValue(data);

        new Listcell(data.getSubjectname()).setParent(li);
        new Listcell(String.valueOf(data.getSemesternumber())).setParent(li);
        new Listcell(String.valueOf(data.getHoursCount())).setParent(li);
        new Listcell(data.getFoc() + (data.getType() == 1 ? " (диф.)" : "")).setParent(li);
        new Listcell(data.getStrRatingShort()).setParent(li);

        Listcell lcAction = new Listcell();
        lcAction.setParent(li);

        Button btnDel = new Button("Удалить");
        btnDel.setParent(lcAction);
        btnDel.addEventListener(Events.ON_CLICK, event -> Events.postEvent("onDelete", li.getListbox(), data));
    }
}
