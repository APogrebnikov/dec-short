package org.edec.newOrder.ctrl.renderer;

import org.edec.newOrder.model.addStudent.SearchStudentModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * Created by antonskripacev on 03.01.17.
 */
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
