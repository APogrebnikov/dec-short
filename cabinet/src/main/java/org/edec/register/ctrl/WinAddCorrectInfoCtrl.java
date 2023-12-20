package org.edec.register.ctrl;

import org.edec.register.model.RegisterRequestModel;
import org.edec.register.service.RegisterRequestService;
import org.edec.register.service.impl.RegisterRequestServiceImpl;
import org.edec.teacher.model.correctRequest.CorrectRequestModel;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.util.Date;

public class WinAddCorrectInfoCtrl extends SelectorComposer<Component> {
    public static final String CORRECT_REQUEST = "register_request";
    public static final String UPDATE_CORRECT_REQUEST = "update_correct_request";

    @Wire
    private Button btnDeny;

    @Wire
    private Window winAddCorrectInfo;

    @Wire
    private Textbox tbAdditionalInformation;

    private RegisterRequestService service = new RegisterRequestServiceImpl();

    Runnable updateCorrectRequest;
    CorrectRequestModel correctRequest;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        updateCorrectRequest = (Runnable) Executions.getCurrent().getArg().get(UPDATE_CORRECT_REQUEST);
        correctRequest = (CorrectRequestModel) Executions.getCurrent().getArg().get(CORRECT_REQUEST);
    }

    @Listen("onClick = #btnDeny")
    public void denyRegisterRequest() {
        correctRequest.setAdditionalInformation(tbAdditionalInformation.getValue());
        correctRequest.setDateOfAnswering(new Date());
        if (service.denyCorrectRequest(correctRequest)) {
            PopupUtil.showInfo("Заявка о корректировке успешно отклонена!");
            // TODO: добработать механизм оповещения
            /*
            service.sendTeacherNotification(registerRequest.getEmail(), "Ваша заявка на открытие ведомости у студента "
                            + registerRequest.getStudentFullName() + " по предмету \"" + registerRequest.getSubjectName()
                            + "\" отклонена!",
                    registerRequest.isGetNotification());
            */
        } else {
            PopupUtil.showError("Отклонить заявку не удалось!");
        }
        updateCorrectRequest.run();
        winAddCorrectInfo.detach();
    }
}