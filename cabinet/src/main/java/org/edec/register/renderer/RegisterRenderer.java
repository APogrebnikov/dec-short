package org.edec.register.renderer;

import org.edec.register.ctrl.WinRegisterCtrl;
import org.edec.register.model.RegisterModel;
import org.edec.register.service.RegisterService;
import org.edec.register.service.impl.RegisterServiceImpl;
import org.edec.teacher.service.CompletionCommissionService;
import org.edec.teacher.service.impl.CompletionCommissionImpl;
import org.edec.utility.DateUtility;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.pdfViewer.ctrl.PdfViewerCtrl;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.edec.utility.constants.RegisterConst.TYPE_COMMISSION;
import static org.edec.utility.constants.RegisterConst.TYPE_COMMISSION_NOT_SIGNED;
import static org.edec.utility.constants.RegisterConst.TYPE_MAIN;
import static org.edec.utility.constants.RegisterConst.TYPE_MAIN_NOT_SIGNED;
import static org.edec.utility.constants.RegisterConst.TYPE_RETAKE_INDIV;
import static org.edec.utility.constants.RegisterConst.TYPE_RETAKE_INDIV_NOT_SIGNED;
import static org.edec.utility.constants.RegisterConst.TYPE_RETAKE_MAIN;
import static org.edec.utility.constants.RegisterConst.TYPE_RETAKE_MAIN_NOT_SIGNED;

public class RegisterRenderer implements ListitemRenderer<RegisterModel> {

    private Runnable update;
    private String fioUser;
    private boolean readOnly;

    private CompletionCommissionService completionCommissionService = new CompletionCommissionImpl();
    private RegisterService registerService = new RegisterServiceImpl();

    public RegisterRenderer(Runnable update, String fioUser, boolean readOnly) {
        this.fioUser = fioUser;
        this.update = update;
        this.readOnly = readOnly;
    }

    @Override
    public void render(Listitem li, final RegisterModel data, int i) throws Exception {
        String typeRegister = "";

        switch (data.getRetakeCount()) {
            case TYPE_MAIN:
            case TYPE_MAIN_NOT_SIGNED:
                typeRegister = "Осн. сдача";
                break;
            case TYPE_RETAKE_MAIN:
            case TYPE_RETAKE_MAIN_NOT_SIGNED:
                typeRegister = "Общ. пересдача";
                break;
            case TYPE_RETAKE_INDIV:
            case TYPE_RETAKE_INDIV_NOT_SIGNED:
                typeRegister = "Инд. пересдача";
                break;
            case TYPE_COMMISSION_NOT_SIGNED:
            case TYPE_COMMISSION:
                typeRegister = "Комиссия";
                break;
        }

        new Listcell(data.getSubjectName()).setParent(li);
        new Listcell(data.getGroupName()).setParent(li);
        String teachersStr = data.getTeachers().toString();
        Listcell teachers = new Listcell(teachersStr.length() < 2 ? "" : teachersStr.substring(1, teachersStr.length() - 1));
        teachers.setParent(li);
        teachers.setTooltiptext(teachers.getLabel());

        if (data.getRetakeCount() == TYPE_RETAKE_INDIV || data.getRetakeCount() == TYPE_RETAKE_INDIV_NOT_SIGNED) {
            new Listcell(data.getStudents().get(0).getFio()).setParent(li);
        } else {
            new Listcell("").setParent(li);
        }

        new Listcell(FormOfControlConst.getName(data.getFoc()) != null ? FormOfControlConst.getName(data.getFoc()).getName() : "null")
                .setParent(li);
        new Listcell(typeRegister).setParent(li);
        new Listcell(DateConverter.convertDateToString(data.getSignDate(), "-")).setParent(li);
        new Listcell(data.getRegisterNumber() == null ? "" : data.getRegisterNumber()).setParent(li);

        Listcell lcSynch = new Listcell();
        lcSynch.setParent(li);

        if (data.getSynchStatus() != null && data.getSynchStatus() != 0) {
            Div divSynch = new Div();

            String colorBg = "";

            if (data.getSynchStatus() == 1) {
                colorBg = "#95FF82";
            } else if (data.getSynchStatus() == 2) {
                colorBg = "#EEFC22";
            } else if (data.getSynchStatus() == -1 || data.getSynchStatus() == -2) {
                colorBg = "#FF7373";
            }

            divSynch.setStyle("border: 1px solid black; background: " + colorBg + "; width: 40px; height: 40px; margin: 5px 0px 0px 24px");
            divSynch.setParent(lcSynch);
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);

        Date mainDate = data.getMainDate();

        if (data.getCertNumber() == null && data.getDateOfEnd() != null
            && !DateUtility.isDayBelongsToPeriod(new Date(), data.getDateOfEnd(), 4)
            && data.getDateOfEnd().before(cal.getTime())
            && (data.getDateOfSecondSignEnd()==null || DateUtility.isAfterDate(new Date(), data.getDateOfSecondSignEnd()))) {
                li.setStyle("background: #FF7373");
        }
        else if (data.getCertNumber() == null && mainDate != null && data.getDateEndMainRegister() == null &&
                DateUtility.isAfterDate(new Date(), mainDate) &&
                !DateUtility.isSameDayOrNextWithWeekends(new Date(), mainDate) &&
                (data.getRetakeCount() == -1 || data.getRetakeCount() == 1)) {
            li.setStyle("background: #FF7373");
        }
        else if (data.getCertNumber() == null && data.getDateEndMainRegister() != null &&
                DateUtility.isAfterDate(new Date(), data.getDateEndMainRegister()) &&
                !DateUtility.isSameDayOrNextWithWeekends(new Date(), data.getDateEndMainRegister()) &&
                (data.getRetakeCount() == -1 || data.getRetakeCount() == 1)) {
        }
        else if (data.getCertNumber() != null && data.getCertNumber().equals("  ")) {
            li.setStyle("background: #bff0b6");
        }
        else if (data.getCertNumber() != null && !data.getCertNumber().equals("")) {
            li.setStyle("background: #95FF82");
        }

        createPopup(li, data);
    }

    private void createPopup(Listitem li, RegisterModel data) {

        String popupId = UUID.randomUUID().toString();

        Menupopup menupopup = new Menupopup();
        menupopup.setParent(li.getListbox().getParent());
        menupopup.setId(popupId);

        Menuitem miDeleteRegister = new Menuitem("Удалить ведомость");
        miDeleteRegister.addEventListener(Events.ON_CLICK, e -> deleteRegister(data));

        Menuitem miShowPdf = new Menuitem("Загрузить PDF подписанной ведомости");
        miShowPdf.addEventListener(Events.ON_CLICK, e -> showPdfFileRegister(data));

        Menuitem miAdditionalInformation = new Menuitem("Посмотреть подробнее");
        miAdditionalInformation.addEventListener(Events.ON_CLICK, event -> showAdditionalInformation(data));

        Menuitem miSyncRegister = new Menuitem("Синхронизация ведомости в Шахтах");
        miSyncRegister.addEventListener(Events.ON_CLICK, event -> syncRegister(data));

        Menuitem miGeneratePdf = new Menuitem("Генерация PDF на основе данных");
        miGeneratePdf.addEventListener(Events.ON_CLICK, event -> generatePdfByData(data));

        boolean registerSigned = data.getCertNumber() != null;

        if (registerSigned) {

            miShowPdf.setParent(menupopup);
            if (!readOnly) {
                miSyncRegister.setParent(menupopup);
            }
        } else {
            if (!readOnly &&
                (data.getRetakeCount() == TYPE_RETAKE_INDIV_NOT_SIGNED || data.getRetakeCount() == TYPE_RETAKE_MAIN_NOT_SIGNED)) {
                miDeleteRegister.setParent(menupopup);
            }
        }

        miAdditionalInformation.setParent(menupopup);
        if(data.getIdRegister() != null) {
            miGeneratePdf.setParent(menupopup);
        }

        li.setContext(popupId);
    }

    private void deleteRegister(RegisterModel data) {

        if (registerService.removeRetake(data, fioUser)) {
            PopupUtil.showInfo("Ведомость удалена");
            update.run();
        } else {
            PopupUtil.showError("Удалить ведомость не удалось");
        }
    }

    private void showPdfFileRegister(RegisterModel data) {

        Map<String, Object> arg = new HashMap<>();
        arg.put(PdfViewerCtrl.FILE, registerService.getFileRegister(data.getRegisterUrl(), data.getIdRegister()));
        ComponentHelper.createWindow("/utility/pdfViewer/index.zul", "winPdfViewer", arg).doModal();
    }

    private void showAdditionalInformation(RegisterModel data) {

        Map<String, Object> arg = new HashMap<>();
        arg.put(WinRegisterCtrl.REGISTER, data);
        arg.put(WinRegisterCtrl.UPDATE_INTERFACE, update);
        ComponentHelper.createWindow("/register/winRegister.zul", "winLookRegister", arg).doModal();
    }

    private void syncRegister(RegisterModel data) {

        boolean isRegisterSynced = registerService.reSyncRegister(data.getIdRegister());
        if (isRegisterSynced) {
            PopupUtil.showInfo("Ведомость синхронизирована");
            update.run();
        } else {
            PopupUtil.showInfo("Не удалось синхронизировать ведомость");
            update.run();
        }
    }

    private void generatePdfByData(RegisterModel data) {

        switch (data.getRetakeCount()) {
            case TYPE_COMMISSION_NOT_SIGNED:
            case TYPE_COMMISSION:
                new JasperReportService()
                        .getJasperReportCommRegister(null, completionCommissionService.getCommissionByRegister(data.getIdRegister()))
                        .showPdf();
                break;
            default:
                new JasperReportService()
                        .getJasperReportRegister(data.getIdRegister(), data.getIdSemester(), data.getIdInstitute(), data.getFos(), true)
                        .showPdf();
        }
    }
}
