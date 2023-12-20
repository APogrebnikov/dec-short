package org.edec.signEditor.renderer;

import org.edec.signEditor.model.OrderSectionModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;


public class OrderSectionRenderer implements ListitemRenderer<OrderSectionModel> {


    public void render(Listitem listitem, OrderSectionModel data, int i) {

        Listcell description = new Listcell(data.getDescription());
        Listcell layout = new Listcell(data.getLayout() != null ? Integer.toString(data.getLayout()) : "");
        Listcell name = new Listcell(data.getName());
        Listcell foundation = new Listcell(data.getFoundation());
        Listcell otherdbid = new Listcell(data.getOtherdbid() != null ? Long.toString(data.getOtherdbid()) : "");

        listitem.appendChild(description);
        listitem.appendChild(layout);
        listitem.appendChild(name);
        listitem.appendChild(foundation);
        listitem.appendChild(otherdbid);

        listitem.setValue(data);

        name.setTooltiptext(name.getLabel());
        description.setTooltiptext(description.getLabel());
        foundation.setTooltiptext(foundation.getLabel());


    }

}
