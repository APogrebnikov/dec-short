package org.edec.passportGroup.ctrl;

import org.edec.passportGroup.model.ScholarshipInfo;
import org.edec.passportGroup.model.StudentModel;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.passportGroup.service.impl.PassportGroupServiceESO;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Window;

import java.util.Date;


public class WinAddScholarshipStudentCtrl extends SelectorComposer<Component> {
    @Wire
    private Datebox dbDateBegin, dbDateEnd;

    @Wire
    private Window winAddScholarshipForStudent;

    private ScholarshipInfo scholarshipInfo;
    private StudentModel studentModel;
    private String fioUser;
    private Runnable update;
    private Long idHumanface;

    protected PassportGroupService service = new PassportGroupServiceESO();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        scholarshipInfo = (ScholarshipInfo) Executions.getCurrent().getArg().get("scholarshipInfo");
        studentModel = (StudentModel) Executions.getCurrent().getArg().get("studentModel");
        fioUser = (String) Executions.getCurrent().getArg().get("fioUser");
        update = (Runnable) Executions.getCurrent().getArg().get("update");
        idHumanface = (Long) Executions.getCurrent().getArg().get("idHumanface");
    }

    @Listen("onClick = #btnSet")
    public void setScholarship() {
        if(dbDateBegin.getValue() == null || dbDateEnd.getValue() == null) {
            PopupUtil.showWarning("Заполните данные");
            return;
        }

        if(dbDateBegin.getValue().after(dbDateEnd.getValue())) {
            PopupUtil.showWarning("Дата начала больше даты конца");
            return;
        }

        //TODO занесение номера приказа о стипендии
        if(service.setScholarship(studentModel, scholarshipInfo, new Date(), dbDateBegin.getValue(), dbDateEnd.getValue(), "", fioUser, idHumanface)) {
            PopupUtil.showInfo("Стипендия была успешно назначена");
            update.run();
            winAddScholarshipForStudent.detach();
        } else {
            PopupUtil.showError("Не удалось отменить стипендию, обратитесь к администратору");
        }
    }
}
