package org.edec.register.renderer;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.register.ctrl.WinAddCorrectInfoCtrl;
import org.edec.register.ctrl.WinCorrectSuccessCtrl;
import org.edec.register.ctrl.WinDeleteCorrectRequestCtrl;
import org.edec.teacher.model.correctRequest.CorrectRequestModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RegisterRequestStatusConst;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class CorrectRequestRenderer implements ListitemRenderer<CorrectRequestModel> {
    private Runnable updateCorrectRequests;
    private TemplatePageCtrl template;

    public CorrectRequestRenderer(Runnable updateCorrectRequests, TemplatePageCtrl template) {
        this.updateCorrectRequests = updateCorrectRequests;
        this.template = template;
    }

    @Override
    public void render (Listitem listitem, CorrectRequestModel correctRequestModel, int i) throws Exception {
        listitem.setValue(correctRequestModel);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Listcell lcBtnDeny = new Listcell();
        if (correctRequestModel.getStatus() == RegisterRequestStatusConst.CREATED) {
            Button btnSuccess = new Button("", "/imgs/edit.png");
            btnSuccess.setTooltiptext("Обработать заявку");
            btnSuccess.setOrient("vertical");
            //btnSuccess.setHoverImage("/imgs/docsCLR.png");
            btnSuccess.setWidth("43px");
            btnSuccess.setHeight("40px");
            btnSuccess.setParent(lcBtnDeny);
            btnSuccess.addEventListener(Events.ON_CLICK, onClickSuccessBtn(correctRequestModel));

            Button btnDeny = new Button("", "/imgs/crossCLR.png");
            btnDeny.setTooltiptext("Отклонить заявку");
            btnDeny.setOrient("vertical");
            btnDeny.setWidth("43px");
            btnDeny.setHeight("40px");
            //btnDeny.setHoverImage("/imgs/docsCLR.png");
            btnDeny.setParent(lcBtnDeny);
            btnDeny.addEventListener(Events.ON_CLICK, onClickDenyBtn(correctRequestModel));
        }

        if (correctRequestModel.getStatus() != RegisterRequestStatusConst.APPROVED &&
            correctRequestModel.getStatus() != RegisterRequestStatusConst.DENIED) {
            Button btnDelete = new Button("", "/imgs/del.png");
            btnDelete.setTooltiptext("Удалить заявку");
            btnDelete.setOrient("vertical");
            btnDelete.setWidth("43px");
            btnDelete.setHeight("40px");
            btnDelete.setParent(lcBtnDeny);
            btnDelete.addEventListener(Events.ON_CLICK, onClickDeleteBtn(correctRequestModel));
        }

        createButtonShow(lcBtnDeny, correctRequestModel);

        Listcell lcAdditionalInfo = new Listcell(correctRequestModel.getAdditionalInformation());
        lcAdditionalInfo.setTooltiptext(correctRequestModel.getAdditionalInformation());

        listitem.appendChild(new Listcell());
        listitem.appendChild(new Listcell(correctRequestModel.getTeacherFIO()));
        listitem.appendChild(new Listcell(correctRequestModel.getStudentFIO()));
        listitem.appendChild(new Listcell(correctRequestModel.getGroupName()));
        listitem.appendChild(new Listcell(correctRequestModel.getSubjectName()));
        listitem.appendChild(new Listcell(correctRequestModel.getFormControl()));
        listitem.appendChild(new Listcell(correctRequestModel.getSeason()));
        listitem.appendChild(new Listcell(simpleDateFormat.format(correctRequestModel.getDateOfApplying())));
        listitem.appendChild(lcAdditionalInfo);
        listitem.appendChild(lcBtnDeny);

        switch (correctRequestModel.getStatus()) {
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

    private EventListener<Event> onClickDenyBtn (CorrectRequestModel request) {
        return event -> {
            if (Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winAddCorrectInfo") != null) {
                Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winAddCorrectInfo").detach();
            }

            Map arg = new HashMap();
            arg.put(WinAddCorrectInfoCtrl.UPDATE_CORRECT_REQUEST, updateCorrectRequests);
            arg.put(WinAddCorrectInfoCtrl.CORRECT_REQUEST, request);

            ComponentHelper.createWindow("/register/winAddCorrectInfo.zul", "winAddCorrectInfo", arg).doModal();
        };
    }

    private EventListener<Event> onClickSuccessBtn (CorrectRequestModel request) {
        return event -> {
            if (Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winAddCorrectInfo") != null) {
                Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winAddCorrectInfo").detach();
            }

            Map arg = new HashMap();
            arg.put(WinCorrectSuccessCtrl.UPDATE_CORRECT_REQUEST, updateCorrectRequests);
            arg.put(WinCorrectSuccessCtrl.CORRECT_REQUEST, request);

            ComponentHelper.createWindow("/register/winCorrectSuccess.zul", "winCorrectSuccess", arg).doModal();
        };
    }

    private EventListener<Event> onClickDeleteBtn (CorrectRequestModel request) {
        return event -> {
            if (Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winDeleteCorrectRequest") != null) {
                Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winDeleteCorrectRequest").detach();
            }

            Map arg = new HashMap();
            arg.put(WinDeleteCorrectRequestCtrl.UPDATE_CORRECT_REQUEST, updateCorrectRequests);
            arg.put(WinDeleteCorrectRequestCtrl.CORRECT_REQUEST, request);
            arg.put(WinDeleteCorrectRequestCtrl.TEMPLATE, template);

            ComponentHelper.createWindow("/register/winDeleteCorrectRequest.zul", "winDeleteCorrectRequest", arg).doModal();
        };
    }


    private void createButtonShow(Listcell listcell, CorrectRequestModel data) {
        Button btnShow = new Button("", "/imgs/pdf.png");
        btnShow.setTooltiptext("Просмотреть служебную записку");
        btnShow.setWidth("43px");
        btnShow.setHeight("40px");
        btnShow.setOrient("vertical");
        btnShow.setParent(listcell);

        if(data.getId() == null) {
            btnShow.setVisible(false);
        } else {
            btnShow.addEventListener(Events.ON_CLICK, e -> new JasperReportService().getJasperReportCorrectRating(data).showPdf());
        }
    }
}
