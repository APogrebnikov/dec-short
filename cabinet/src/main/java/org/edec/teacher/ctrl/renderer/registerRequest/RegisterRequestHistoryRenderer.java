package org.edec.teacher.ctrl.renderer.registerRequest;

import org.edec.teacher.model.registerRequest.RegisterRequestModel;
import org.edec.utility.constants.RegisterRequestStatusConst;
import org.edec.utility.converter.DateConverter;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.text.SimpleDateFormat;

public class RegisterRequestHistoryRenderer implements ListitemRenderer<RegisterRequestModel> {

    @Override
    public void render (Listitem listitem, RegisterRequestModel registerRequestModel, int i) throws Exception {
        listitem.setValue(registerRequestModel);

        Listcell lcAdditionalInfo = new Listcell(registerRequestModel.getAdditionalInfo());
        lcAdditionalInfo.setTooltiptext(registerRequestModel.getAdditionalInfo());

        listitem.appendChild(new Listcell(registerRequestModel.getStudent().getFio()));
        listitem.appendChild(new Listcell(DateConverter.convertDateToString(registerRequestModel.getApplyingDate())));
        listitem.appendChild(lcAdditionalInfo);

        switch (registerRequestModel.getStatus()) {
            case RegisterRequestStatusConst.APPROVED:
                listitem.setStyle("background: #99ff99;");
                break;
            case RegisterRequestStatusConst.DENIED:
                listitem.setStyle("background: #FF7373;");
                break;
            case RegisterRequestStatusConst.UNDER_CONSIDERATION:
                listitem.setStyle("background: #FFFFFF;");
                break;
        }
    }
}
