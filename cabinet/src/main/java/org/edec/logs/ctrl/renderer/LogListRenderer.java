package org.edec.logs.ctrl.renderer;

import org.edec.logs.model.LogModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class LogListRenderer implements ListitemRenderer<LogModel> {
    @Override
    public void render(Listitem item, LogModel data, int i) throws Exception {
        item.setValue(data);

        Listcell dateTimeCell = new Listcell(data.getDate().toString() + " " + data.getTime());
        Listcell levelCell = new Listcell(data.getLevel());
        Listcell textCell = new Listcell(data.getText());

        textCell.setStyle("white-space: nowrap; overflow: hidden; text-overflow: ellipsis; padding-left: 15px; padding-right: 15px");
        textCell.setTooltiptext(data.getText());

        item.appendChild(dateTimeCell);
        item.appendChild(levelCell);
        item.appendChild(textCell);
    }
}
