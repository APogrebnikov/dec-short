package org.edec.register.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.register.service.RegisterRequestService;
import org.edec.register.service.impl.RegisterRequestServiceImpl;
import org.edec.teacher.model.correctRequest.CorrectRequestModel;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

public class WinDeleteCorrectRequestCtrl extends SelectorComposer<Component> {
    public static final String CORRECT_REQUEST = "register_request";
    public static final String UPDATE_CORRECT_REQUEST = "update_correct_request";
    public static final String TEMPLATE = "template";

    @Wire
    private Button btnDelete;

    @Wire
    private Button btnCancel;

    @Wire
    private Window winDeleteCorrectRequest;

    private RegisterRequestService service = new RegisterRequestServiceImpl();

    Runnable updateCorrectRequest;
    CorrectRequestModel correctRequest;
    TemplatePageCtrl template;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        updateCorrectRequest = (Runnable) Executions.getCurrent().getArg().get(UPDATE_CORRECT_REQUEST);
        correctRequest = (CorrectRequestModel) Executions.getCurrent().getArg().get(CORRECT_REQUEST);
        template = (TemplatePageCtrl) Executions.getCurrent().getArg().get(TEMPLATE);
    }

    @Listen("onClick = #btnDelete")
    public void deleteCorrectRequest() {
        if(service.setDeleteStatus(correctRequest, template)) {
            PopupUtil.showInfo("Заявка на корректировку успешно удалена!");
            updateCorrectRequest.run();
        } else {
            PopupUtil.showError("Удалить заявку на корректировку не удалось!");
        }

        winDeleteCorrectRequest.detach();

    }

    @Listen("onClick = #btnCancel")
    public void cancelBtnClick() {
        winDeleteCorrectRequest.detach();
    }
}
