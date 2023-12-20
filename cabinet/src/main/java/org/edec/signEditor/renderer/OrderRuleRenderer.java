package org.edec.signEditor.renderer;

import org.edec.signEditor.model.OrderRuleModel;
import org.edec.studentOrder.model.StudentOrderModel;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class OrderRuleRenderer implements ListitemRenderer<OrderRuleModel> {


    public void render(Listitem listitem, OrderRuleModel data, int i) throws Exception {

        String isAuto;
        Button btnSignEmployee = new Button("", "/imgs/admin.png");

        btnSignEmployee.addEventListener(Events.ON_CLICK, e -> {


            Map arg = new HashMap();
            arg.put("idOrderRule", data.getIdOrderRule());

            ComponentHelper.createWindow("/signEditor/winSignEmployee.zul", "winSignEmployee", arg).doModal();

        });

        if (data.isAutomatic() == true) {
            isAuto = "+";
        } else isAuto = "-";

        Listcell name = new Listcell(data.getName());
        Listcell description = new Listcell(data.getDescription());
        Listcell headDescription = new Listcell(data.getHeadDescription());
        Listcell idOrderType = new Listcell(Long.toString(data.getIdOrderType()));
        Listcell idInstitute = new Listcell(Long.toString(data.getIdInstitute()));
        Listcell formOfControl = new Listcell(Long.toString(data.getFormOfControl()));
        Listcell isAutomatic = new Listcell(isAuto);
        Listcell btn = new Listcell();
        btn.appendChild(btnSignEmployee);


        listitem.appendChild(name);
        listitem.appendChild(description);
        listitem.appendChild(headDescription);
        listitem.appendChild(idOrderType);
        listitem.appendChild(idInstitute);
        listitem.appendChild(formOfControl);
        listitem.appendChild(isAutomatic);
        listitem.appendChild(btn);

        listitem.setValue(data);

        name.setTooltiptext(name.getLabel());
        description.setTooltiptext(description.getLabel());
        headDescription.setTooltiptext(headDescription.getLabel());


    }


}
