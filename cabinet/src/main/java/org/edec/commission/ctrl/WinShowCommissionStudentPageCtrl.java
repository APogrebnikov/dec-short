package org.edec.commission.ctrl;

import org.apache.log4j.Logger;
import org.edec.commission.model.StudentDebtModel;
import org.edec.commission.model.SubjectDebtModel;
import org.edec.commission.service.CommissionService;
import org.edec.commission.service.impl.CommissionServiceESOimpl;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WinShowCommissionStudentPageCtrl extends SelectorComposer<Component> {
    public static final String SELECTED_LISTITEM = "selected_listitem";
    public static final String COMMISSION_CTRL = "commission_ctrl";

    private static final Logger log = Logger.getLogger(WinShowCommissionStudentPageCtrl.class.getName());

    @Wire
    private Datebox dateBeginComission, dateEndComission;

    @Wire
    private Label lInfo, lCountComission;

    @Wire
    private Listbox lbStudentComission;

    @Wire
    private Window winShowCommissionStudent;

    private SubjectDebtModel selectedSubjDebtModel;
    private IndexPageCtrl indexPageCtrl;
    private Listitem selectedListitem;

    private CommissionService commissionService = new CommissionServiceESOimpl();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        selectedListitem = (Listitem) Executions.getCurrent().getArg().get(SELECTED_LISTITEM);
        indexPageCtrl = (IndexPageCtrl) Executions.getCurrent().getArg().get(COMMISSION_CTRL);
        selectedSubjDebtModel = selectedListitem.getValue();

        lInfo.setValue(selectedSubjDebtModel.getSubjectname() + " (" + selectedSubjDebtModel.getFocStr() + ")");

        dateBeginComission.setValue(selectedSubjDebtModel.getDateofbegincomission());
        dateEndComission.setValue(selectedSubjDebtModel.getDateofendcomission());

        ListModelList lmStudentComission = new ListModelList<>(
                commissionService.getStudentByRegisterCommission(selectedSubjDebtModel.getIdRegComission()));
        lmStudentComission.setMultiple(true);
        lbStudentComission.setModel(lmStudentComission);
    }

    @Listen("onClick = #btnExtendComission")
    public void extendComission() {
        if (selectedSubjDebtModel.isSigned()) {
            PopupUtil.showWarning("Ведомость подписана, изменять нельзя!");
            return;
        }
        if (dateBeginComission.getValue() == null || dateEndComission.getValue() == null) {
            PopupUtil.showWarning("Введите обе даты!");
            return;
        }
        if (dateBeginComission.getValue() != null && dateEndComission.getValue() != null && dateBeginComission.getValue().after(dateEndComission.getValue())) {
            PopupUtil.showWarning("Дата начала не может быть позже даты окончания периода комиссии!");
            return;
        }
        if (commissionService.updateCommissionRegister(selectedSubjDebtModel.getIdRegComission(), dateBeginComission.getValue(),
                                                       dateEndComission.getValue()
        )) {
            log.info("CommissionModel-change-date: " + selectedSubjDebtModel.getSubjectname() + "(" + selectedSubjDebtModel.getFocStr() +
                     ", " + selectedSubjDebtModel.getSemesterStr() + ")" + ", begin: " +
                     DateConverter.convertDateToString(dateBeginComission.getValue()) + ", end: " +
                     DateConverter.convertDateToString(dateEndComission.getValue()));

            PopupUtil.showInfo("Обновления комиссионной ведомости прошло успешно!");

            selectedSubjDebtModel.setDateofbegincomission(dateBeginComission.getValue());
            selectedSubjDebtModel.setDateofendcomission(dateEndComission.getValue());
            selectedListitem.setValue(selectedListitem);
            selectedListitem.getListbox().renderItem(selectedListitem);
        } else {
            PopupUtil.showError("Обновить комисионную ведомость не удалось!");
        }
    }

    @Listen("onClick = #btnComissionStructure")
    public void openWinComissionStructure() {
        if (Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winCommissionStructure") != null) {
            Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winCommissionStructure").detach();
        }

        Map<String, Object> arg = new HashMap<>();
        arg.put(WinCommissionStructureCtrl.ID_COMMISSION, selectedSubjDebtModel.getIdRegComission());

        Window win = (Window) Executions.createComponents("winCommissionStructure.zul", null, arg);
        win.doModal();
    }

    @Listen("onClick = #btnDelComission")
    public void deleteComissionFromChoosenStudent() {
        if (selectedSubjDebtModel.isSigned()) {
            PopupUtil.showWarning("Ведомость подписана, изменять нельзя!");
            return;
        }
        if (lbStudentComission.getSelectedCount() == 0) {
            PopupUtil.showWarning("Нужно выбрать хотя бы одного студента.");
            return;
        }
        String idSRHs = "";
        String listFio = "";
        for (Listitem li : lbStudentComission.getSelectedItems()) {
            StudentDebtModel studentModel = li.getValue();
            listFio += studentModel.getFio();
            idSRHs += studentModel.getIdSrh() + ",";
        }
        idSRHs = idSRHs.substring(0, idSRHs.length() - 1);
        if (commissionService.deleteSRHfromCommRegister(idSRHs)) {
            log.info("CommissionModel-delete-student(" + DateConverter.convertDateToString(new Date()) + "):" +
                     selectedSubjDebtModel.getSubjectname() + "(" + selectedSubjDebtModel.getFocStr() + ", " +
                     selectedSubjDebtModel.getSemesterStr() + ")" + "(" + listFio + ")");
            PopupUtil.showInfo("Студенты успешно удалены из комиссии");
            lbStudentComission.getItems().removeAll(lbStudentComission.getSelectedItems());
            List<Long> listSSSForComission = commissionService.getListSSSForCommission(selectedSubjDebtModel.getIdRegComission());
            if (listSSSForComission.isEmpty() && selectedSubjDebtModel.getTeachers() != null) {
                commissionService.updateStatusNotificationCom(selectedSubjDebtModel.getIdRegComission());
                log.info("statusNotification комиссии поменялмя на 3, idRegCom = " + selectedSubjDebtModel.getIdRegComission());
            }
        } else {
            PopupUtil.showError("Студентов не удалось удалить из комиссии");
        }
    }

    @Listen("onClick = #btnCloseFromComissionStudent")
    public void onExit() {
        if (lbStudentComission.getItemCount() == 0) {
            commissionService.deleteCommission(selectedSubjDebtModel.getIdRegComission());
        }
        indexPageCtrl.searchCommission();
        winShowCommissionStudent.detach();
    }
}
