package org.edec.signEditor.renderer;

import org.edec.signEditor.model.SignatoryModel;
import org.edec.utility.constants.ActionRuleConst;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class SignatoryModelRenderer implements ListitemRenderer<SignatoryModel> {
    @Override
    public void render (Listitem listitem, SignatoryModel model, int i) throws Exception {
        listitem.setValue(model.getEmployee().getFio());
        listitem.appendChild(new Listcell(model.getEmployee().getFio()));
        listitem.setValue(model.getRole());
        listitem.appendChild(new Listcell(ActionRuleConst.getName(model.getRole()).getName()));
        listitem.setValue(model.getPosition());
        listitem.appendChild(new Listcell(model.getPosition().toString()));
    }
}