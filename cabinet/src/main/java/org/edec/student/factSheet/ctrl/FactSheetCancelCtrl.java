package org.edec.student.factSheet.ctrl;

import org.edec.factSheet.ctrl.renderer.FactSheetDecRenderer;
import org.edec.factSheet.model.FactSheetStatusEnum;
import org.edec.factSheet.model.FactSheetTableModel;
import org.edec.utility.email.Sender;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import javax.mail.MessagingException;

public class FactSheetCancelCtrl extends SelectorComposer<Component> {

    public static final String RENDERER = "renderer";
    public static final String CMB_STATUS = "cmb_status";
    public static final String LISTITEM = "listitem";

    @Wire
    private Button btnOk, btnCancel;
    @Wire
    private Window winRefusalFactSheet;
    @Wire
    private Textbox tbCancel;

    private FactSheetDecRenderer factSheetDecRenderer;
    private Sender sender;

    private Combobox cmbStatus;
    private Listitem listitem;

    private int registerNumber;

    @Override
    public void doAfterCompose(Component comp) {
        try {
            super.doAfterCompose(comp);
            sender = new Sender();
        } catch (Exception e) {
            e.printStackTrace();
        }
        factSheetDecRenderer = (FactSheetDecRenderer) Executions.getCurrent().getArg().get(RENDERER);
        cmbStatus = (Combobox) Executions.getCurrent().getArg().get(CMB_STATUS);
        listitem = (Listitem) Executions.getCurrent().getArg().get(LISTITEM);
    }

    @Listen("onClick = #btnCancel")
    public void cancel() {
        winRefusalFactSheet.detach();
        cmbStatus.setValue(FactSheetStatusEnum.CREATED.toString());
    }

    @Listen("onClick = #btnOk")
    public void accept() {
        if (tbCancel.getValue().equals("")) {
            DialogUtil.exclamation("Опишите причину", "Ошибка");
            tbCancel.setFocus(true);
            return;
        }
        factSheetDecRenderer.updateStatus(cmbStatus, listitem);
        FactSheetTableModel selectedHum = listitem.getValue();

        if (sender != null && selectedHum.getGetNotification() && selectedHum.getEmail() != null && !selectedHum.getEmail().equals("")) {
            try {
                sender.sendSimpleMessage(selectedHum.getEmail(), "Сервис заказа справок ИКИТ",
                        "Справка '" + selectedHum.getTitle() + "' отменена\nПричина: " + tbCancel.getValue());
            } catch (MessagingException e) {
                PopupUtil.showError("Не удалось отправить e-mail");
                e.printStackTrace();
            }
        }

        winRefusalFactSheet.detach();
    }

}