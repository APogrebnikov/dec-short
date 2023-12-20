package org.edec.studentOrder.ctrl.renderer;

import org.edec.studentOrder.model.OrderModel;
import org.edec.utility.converter.DateConverter;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class OrderRenderer implements ListitemRenderer<OrderModel> {
    public void render (Listitem listitem, OrderModel searchStudent, int i) throws Exception {

        new Listcell(Long.toString(searchStudent.getIdOrderHead())).setParent(listitem);
        Listcell nameOrder = new Listcell(searchStudent.getDescriptionspec());
        Listcell dateOfbegin = new Listcell(DateConverter.convertDateToString(searchStudent.getDateOfBegin()));
        Listcell typeOrdre = new Listcell(searchStudent.getTypeOrder());
        Listcell section = new Listcell(searchStudent.getSection());
        listitem.appendChild(nameOrder);
        listitem.appendChild(dateOfbegin);
        listitem.appendChild(typeOrdre);
        listitem.appendChild(section);
    }
}
