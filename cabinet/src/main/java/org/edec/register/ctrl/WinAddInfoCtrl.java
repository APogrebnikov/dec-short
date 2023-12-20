package org.edec.register.ctrl;

import org.edec.register.model.RegisterRequestModel;
import org.edec.register.service.RegisterRequestService;
import org.edec.register.service.impl.RegisterRequestServiceImpl;
import org.edec.register.service.impl.RegisterRequestServiceImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class WinAddInfoCtrl extends SelectorComposer<Component> {
    public static final String REGISTER_REQUEST = "register_request";
    public static final String UPDATE_REGISTER_REQUEST = "update_register_request";

    @Wire
    private Button btnDeny;

    @Wire
    private Window winAddInfo;

    @Wire
    private Textbox tbAdditionalInformation;

    private RegisterRequestService service = new RegisterRequestServiceImpl();

    Runnable updateRegisterRequest;
    RegisterRequestModel registerRequest;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        updateRegisterRequest = (Runnable) Executions.getCurrent().getArg().get(UPDATE_REGISTER_REQUEST);
        registerRequest = (RegisterRequestModel) Executions.getCurrent().getArg().get(REGISTER_REQUEST);
    }

    @Listen("onClick = #btnDeny")
    public void denyRegisterRequest() {
        if (service.denyRequest(registerRequest.getIdRegisterRequest(), tbAdditionalInformation.getValue())) {
            PopupUtil.showInfo("Заявка успешно отклонена!");

            service.sendTeacherNotification(registerRequest.getEmail(), "Ваша заявка на открытие ведомости у студента "
                            + registerRequest.getStudentFullName() + " по предмету \"" + registerRequest.getSubjectName()
                            + "\" отклонена!",
                    registerRequest.isGetNotification());
            updateRegisterRequest.run();
        } else {
            PopupUtil.showError("Отклонить заявку не удалось!");
        }
        winAddInfo.detach();
    }
}