package org.edec.student.factSheet.ctrl;

import org.apache.log4j.Logger;
import org.edec.factSheet.ctrl.IndexPageCtrl;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.factSheet.model.FactSheetAddModel;
import org.edec.factSheet.model.TypeFactSheetModel;
import org.edec.factSheet.service.FactSheetService;
import org.edec.student.factSheet.ctrl.renderer.FactSheetAddRenderer;
import org.edec.factSheet.service.impl.FactSheetServiceImpl;
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

public class FactSheetAddCtrl extends SelectorComposer<Component> {

    private static final Logger log = Logger.getLogger(FactSheetAddCtrl.class.getName());


    public static final String FORM_OF_STUDY = "formOfStudy";
    public static final String INST = "inst";
    public static final String LB_FACT_SHEET = "lb_fact_sheet";
    public static final String INDEX_PAGE_CTRL = "indexPageCtrl";

    @Wire
    private Button btnOk, btnCancel;
    @Wire
    private Checkbox cbOfficialSealAdd;
    @Wire
    private Combobox cmbKind;

    private Listbox lbFactSheet;
    @Wire
    private Listbox lbFactSheetAdd;
    @Wire
    private Textbox tbFullNameAdd, tbGroupAdd, tbRecordBook;
    @Wire
    private Window winAddFactSheet;


    private IndexPageCtrl factSheetDecCtrl;
    private FactSheetService factSheetService = new FactSheetServiceImpl();
    private TemplatePageCtrl template = new TemplatePageCtrl();
    private Sender sender;

    private Long idInst;
    private Integer formOfStudy;

    @Override
    public void doAfterCompose(Component comp) {
        try {
            super.doAfterCompose(comp);
            sender = new Sender();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cmbKind.setModel(new ListModelArray<>(factSheetService.getTypeFactSheet()));
        factSheetDecCtrl = (IndexPageCtrl) Executions.getCurrent().getArg().get(INDEX_PAGE_CTRL);
        lbFactSheet = (Listbox) Executions.getCurrent().getArg().get(LB_FACT_SHEET);

        idInst = (Long) Executions.getCurrent().getArg().get(INST);
        formOfStudy = (Integer) Executions.getCurrent().getArg().get(FORM_OF_STUDY);

        lbFactSheetAdd.setCheckmark(true);
    }

    @Listen("onClick = #btnCancel")
    public void cancel() {
        winAddFactSheet.detach();
    }

    @Listen("onSelect = #cmbKind")
    public void choiceType() {
        TypeFactSheetModel value = cmbKind.getSelectedItem().getValue();

        if (value.getIdTypeFactSheet() == 2) {
            cbOfficialSealAdd.setChecked(true);
            cbOfficialSealAdd.setDisabled(true);
        } else {
            cbOfficialSealAdd.setChecked(false);
            cbOfficialSealAdd.setDisabled(false);
        }
    }

    @Listen("onClick = #btnOk")
    public void add() {
        if (lbFactSheetAdd.getSelectedItem() == null) {
            DialogUtil.error("Студент не выбран", "Ошибка");
            return;
        }
        TypeFactSheetModel selectedType = cmbKind.getSelectedItem().getValue();

        FactSheetAddModel selectedHum = lbFactSheetAdd.getSelectedItem().getValue();

        FactSheetAddModel factSheetAddModel = lbFactSheetAdd.getSelectedItem().getValue();

        if (factSheetService.addFactSheet(factSheetAddModel.getIdHumanface(), selectedType.getIdTypeFactSheet(), cbOfficialSealAdd.isChecked(),
                false, factSheetAddModel.getGroupName())) {
            log.info(factSheetAddModel.getFullName() + " - заказал справку (Операцию выполнил - " + template.getCurrentUser().getFio() + ")");
        } else {
            log.info("У пользователя " + factSheetAddModel.getFullName() +
                    " возникла ошибка при попытке заказать справку (Операцию выполнил - " + template.getCurrentUser().getFio() + ")");
            DialogUtil.error("Справку не удалось создать. Обратитесь к администратору", "Ошибка");
            return;
        }

        String deadline;

        if (cbOfficialSealAdd.isChecked())
            deadline = "Срок выполнения 10 рабочих дней";
        else
            deadline = "Срок выполнения 3 рабочих дня";

        if (selectedHum.getGetNotification() && selectedHum.getEmail() != null && !selectedHum.getEmail().equals("")) {
            try {
                sender.sendSimpleMessage(selectedHum.getEmail(), "Сервис заказа справок ИКИТ",
                        "Справка '" + selectedType.getTitle() + "' поступила на рассмотрение\n" + deadline);
            } catch (MessagingException e) {
                PopupUtil.showError("Не удалось отправить e-mail");
                e.printStackTrace();
            }
        }

        DialogUtil.info("Ваша заявка принята\n" + deadline, "Успешно");
        winAddFactSheet.detach();
        factSheetDecCtrl.callSearch(null);
    }

    @Listen("onOK=#tbFullNameAdd; onOK=#tbGroupAdd; onOK=#tbRecordBook;")
    public void fill() {
        lbFactSheetAdd.setItemRenderer(new FactSheetAddRenderer());
        lbFactSheetAdd.setModel(new ListModelList<>(
                factSheetService.getSearchStudents(tbFullNameAdd.getValue(), tbGroupAdd.getValue(), tbRecordBook.getValue(),
                        idInst, formOfStudy)));
        lbFactSheetAdd.renderAll();
    }

}