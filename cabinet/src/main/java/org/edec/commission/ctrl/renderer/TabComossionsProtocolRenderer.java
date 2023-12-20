package org.edec.commission.ctrl.renderer;

import org.edec.commission.model.ComissionsProtocolsModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class TabComossionsProtocolRenderer implements ListitemRenderer<ComissionsProtocolsModel> {
    @Override
    public void render(Listitem li, ComissionsProtocolsModel data, int i) throws Exception {
        li.appendChild(new Listcell(data.getComDateStr()));
        Listcell lcProtocolNumber = new Listcell();
        if (data.getProtocolNumber() == null){
            lcProtocolNumber.setLabel("-");
        } else {
            lcProtocolNumber.setLabel(data.getProtocolNumber());
        }
        li.appendChild(lcProtocolNumber);
        li.appendChild(new Listcell(data.getSubjectname()));
        li.appendChild(new Listcell(data.getFioStudent()));
        li.appendChild(new Listcell(data.getGroupname()));
        li.appendChild(new Listcell(data.getChairsName()));
        if (data.getCertnumber() == null){
            li.setStyle("background : #ff9494; text-align : center");
        } else if (data.getCertnumber() != null){
            li.setStyle("background : #99FF99; text-align : center");
        }
    }
}
