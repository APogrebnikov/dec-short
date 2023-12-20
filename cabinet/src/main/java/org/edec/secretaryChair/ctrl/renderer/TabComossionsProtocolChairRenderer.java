package org.edec.secretaryChair.ctrl.renderer;

import org.edec.secretaryChair.model.ChairsComissionsProtocolsModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class TabComossionsProtocolChairRenderer implements ListitemRenderer<ChairsComissionsProtocolsModel> {
    @Override
    public void render(Listitem li, ChairsComissionsProtocolsModel data, int i) throws Exception {
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
        if (data.getCertnumber() == null){
            li.setStyle("background : #ff9494; text-align : center");
        } else if (data.getCertnumber() != null){
            li.setStyle("background : #99FF99; text-align : center");
        }
    }
}


