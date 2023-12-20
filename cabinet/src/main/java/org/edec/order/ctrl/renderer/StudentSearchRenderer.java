package org.edec.order.ctrl.renderer;

import org.edec.order.model.SearchGroupModel;
import org.edec.order.model.SearchStudentModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ItemRenderer;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;


public class StudentSearchRenderer implements ListitemRenderer<SearchStudentModel> {
    @Override
    public void render (Listitem listitem, SearchStudentModel searchStudentModel, int i) throws Exception {
        listitem.setValue(searchStudentModel);
        new Listcell(searchStudentModel.getSurname()).setParent(listitem);
        new Listcell(searchStudentModel.getName()).setParent(listitem);
        new Listcell(searchStudentModel.getPatronymic()).setParent(listitem);
        new Listcell(searchStudentModel.getGroupname()).setParent(listitem);
    }
}
