package org.edec.register.renderer;

import org.edec.register.ctrl.WinAddInfoCtrl;
import org.edec.register.model.RegisterRequestModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RegisterRequestStatusConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class RegisterRequestRenderer implements ListitemRenderer<RegisterRequestModel> {

    private Runnable updateRegisterRequests;

    public RegisterRequestRenderer (Runnable updateRegisterRequests) {
        this.updateRegisterRequests = updateRegisterRequests;
    }

    @Override
    public void render (Listitem listitem, RegisterRequestModel registerRequestModel, int i) throws Exception {
        listitem.setValue(registerRequestModel);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Listcell lcBtnDeny = new Listcell();
        if (registerRequestModel.getStatus() == RegisterRequestStatusConst.UNDER_CONSIDERATION) {
            Button btnDeny = new Button("Отклонить");
            btnDeny.setTooltiptext("Отклонить заявку");
            btnDeny.setHoverImage("/imgs/docsCLR.png");
            btnDeny.setParent(lcBtnDeny);
            btnDeny.addEventListener(Events.ON_CLICK, onClickDenyBtn(registerRequestModel));
        }

        Listcell lcAdditionalInfo = new Listcell(registerRequestModel.getAdditionalInformation());
        lcAdditionalInfo.setTooltiptext(registerRequestModel.getAdditionalInformation());

        listitem.appendChild(new Listcell());
        listitem.appendChild(new Listcell(registerRequestModel.getTeacherFullName()));
        listitem.appendChild(new Listcell(registerRequestModel.getStudentFullName()));
        listitem.appendChild(new Listcell(registerRequestModel.getGroupName()));
        listitem.appendChild(new Listcell(registerRequestModel.getSubjectName()));
        listitem.appendChild(new Listcell(getFocStrValue(registerRequestModel.getFoc())));
        listitem.appendChild(new Listcell(DateConverter.convert2dateToString(registerRequestModel.getDateOfBeginSemester(),
                                                                             registerRequestModel.getDateOfEndSemester()
        ) + " " + (registerRequestModel.getSeasonSemester() == 0 ? "осень" : "весна")));
        listitem.appendChild(new Listcell(simpleDateFormat.format(registerRequestModel.getDateOfApplying())));
        listitem.appendChild(lcAdditionalInfo);
        listitem.appendChild(lcBtnDeny);

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

    private String getFocStrValue (int foc) {
        if (foc == FormOfControlConst.EXAM.getValue()) {
            return FormOfControlConst.EXAM.getName();
        }
        if (foc == FormOfControlConst.PASS.getValue()) {
            return FormOfControlConst.PASS.getName();
        }
        if (foc == FormOfControlConst.CP.getValue()) {
            return FormOfControlConst.CP.getName();
        }
        if (foc == FormOfControlConst.CW.getValue()) {
            return FormOfControlConst.CW.getName();
        }
        if (foc == FormOfControlConst.PRACTIC.getValue()) {
            return FormOfControlConst.PRACTIC.getName();
        }
        return "";
    }

    private EventListener<Event> onClickDenyBtn (RegisterRequestModel request) {
        return event -> {
            if (Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winAddInfo") != null) {
                Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winAddInfo").detach();
            }

            Map arg = new HashMap();
            arg.put(WinAddInfoCtrl.UPDATE_REGISTER_REQUEST, updateRegisterRequests);
            arg.put(WinAddInfoCtrl.REGISTER_REQUEST, request);

            ComponentHelper.createWindow("/register/winAddInfo.zul", "winAddInfo", arg).doModal();
        };
    }
}
