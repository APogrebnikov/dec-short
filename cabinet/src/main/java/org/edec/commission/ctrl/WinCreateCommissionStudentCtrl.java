package org.edec.commission.ctrl;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.edec.commission.model.StudentDebtModel;
import org.edec.commission.model.SubjectDebtModel;
import org.edec.commission.service.CommissionService;
import org.edec.commission.service.impl.CommissionServiceESOimpl;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j
public class WinCreateCommissionStudentCtrl extends SelectorComposer<Component> {
    public static final String SELECTED_LIST_ITEM_SUBJECT = "selected_li_subject";
    public static final String WIN_CREATE_COMMISSION = "win_create_commission";

    @Wire
    private Label lInfo;

    @Wire
    private Listbox lbCreateCommissionStudent;

    @Wire
    private Window winCreateCommissionStudent;

    private Listitem selectedListitem;
    private SubjectDebtModel subject;
    private WinCreateCommission winCreateCommission;

    private CommissionService commissionService = new CommissionServiceESOimpl();
    private TemplatePageCtrl template = new TemplatePageCtrl();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        winCreateCommission = (WinCreateCommission) Executions.getCurrent().getArg().get(WIN_CREATE_COMMISSION);
        selectedListitem = (Listitem) Executions.getCurrent().getArg().get(SELECTED_LIST_ITEM_SUBJECT);
        subject = selectedListitem.getValue();

        lInfo.setValue(subject.getSubjectname() + "(" + subject.getFocStr() + "), " + subject.getFulltitle());
        Clients.showBusy(lbCreateCommissionStudent, "Загрузка данных");
        Events.echoEvent("onLater", lbCreateCommissionStudent, null);
    }

    @Listen("onLater = #lbCreateCommissionStudent")
    public void laterOnLbCreateCommissionStudent() {
        ListModelList lmStudents = new ListModelList(subject.getStudents());
        lmStudents.setMultiple(true);
        lbCreateCommissionStudent.setModel(lmStudents);
        lbCreateCommissionStudent.renderAll();
        Clients.clearBusy(lbCreateCommissionStudent);
    }

    @Listen("onClick = #btnCreateCommissionForSubject")
    public void createCommissionForSubject() {
        if (lbCreateCommissionStudent.getSelectedCount() == 0) {
            PopupUtil.showWarning("Для создания комиссии нужно выбрать хотя бы одного студента.");
            return;
        }
        List<StudentDebtModel> students = new ArrayList<>();
        for (Listitem li : lbCreateCommissionStudent.getSelectedItems()) {
            StudentDebtModel student = li.getValue();
            if (!student.isOpenComm())
                students.add(student);
        }
        if (students.size() > 0) {
            if (commissionService.createCommonCommission(subject, students, template.getCurrentUser().getIdHum())) {
                students.forEach(student ->
                    log.info("Была создана комиссия " + DateConverter.convertDateToString(new Date()) + "):" +
                             subject.getSubjectname() + "(" + subject.getFocStr() + ")" +
                             " для студента " + student.getFio() +
                             " сотрудником " + template.getCurrentUser().getFio()));

                for (StudentDebtModel student : students) {
                    subject.getStudents().get(subject.getStudents().indexOf(student)).setOpenComm(true);
                }

            }
        } else
            return;

        //Проверка, если создана для всех комиссия, то помечаем предмет как созданный
        boolean allCreated = true;
        for (StudentDebtModel student : subject.getStudents()) {
            if (!student.isOpenComm()) {
                allCreated = false;
                break;
            }
        }

        if (allCreated)
            winCreateCommission.changeStatusForSubject(subject);

        Clients.showBusy(lbCreateCommissionStudent, "Загрузка данных");
        Events.echoEvent("onLater", lbCreateCommissionStudent, null);
    }

    @Listen("onClick = #btnSaveExit")
    public void saveExit() {
        winCreateCommissionStudent.detach();
    }
}
