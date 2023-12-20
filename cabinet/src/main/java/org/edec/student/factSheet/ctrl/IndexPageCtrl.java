package org.edec.student.factSheet.ctrl;

import lombok.extern.log4j.Log4j;
import org.edec.factSheet.model.FactSheetAddModel;
import org.edec.factSheet.model.TypeFactSheetModel;
import org.edec.factSheet.service.FactSheetService;
import org.edec.factSheet.service.impl.FactSheetServiceImpl;

import org.edec.utility.email.Sender;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Button btnOrder, btnHistory;
    @Wire
    private Checkbox cbOfficialSeal;
    @Wire
    private Combobox cmbKind, cmbGroup;
    @Wire
    private Label lStatusEmail;
    @Wire
    private Spinner spin;
    @Wire
    private Vlayout vlGroup;

    @Wire
    private Label labelReferenceMaterial;

    @Wire
    private Label txtReferenceMaterial;

    private FactSheetService factSheetService = new FactSheetServiceImpl();

    private Sender sender;

    private FactSheetAddModel currentHuman;

    private List<String> studentGroups;

    protected void fill() {
        try {
            sender = new Sender();
        } catch (IOException e) {
            PopupUtil.showError("Оповещение на почту временно не работает");
            e.printStackTrace();
        }
        currentHuman = factSheetService.getHumanInfo(template.getCurrentUser().getIdHum()).get(0);
        cmbKind.setModel(new ListModelList<>(factSheetService.getTypeFactSheet()));
        if (!currentHuman.getGetNotification() || currentHuman.getEmail() == null || currentHuman.getEmail().equals("")) {
            lStatusEmail
                    .setValue("Оповещение на почту не придет, потому что почта не заполнена или отключено уведомление в модуле 'Профиль'");
            lStatusEmail.setStyle("color: #FF7373");
        } else {
            lStatusEmail.setValue("Оповещение придет на почту '" + currentHuman.getEmail() + "', поменять можно в модуле 'Профиль'");
            lStatusEmail.setStyle("color: #09bd09;");
        }

        studentGroups = factSheetService.getStudentGroups(template.getCurrentUser().getIdHum());

        if (studentGroups.size() > 1) {
            vlGroup.setVisible(true);

            for (String groupName : studentGroups) {
                cmbGroup.appendItem(groupName);
            }

            cmbGroup.setSelectedIndex(0);
        }
    }

    @Listen("onClick = #btnHistory")
    public void checkStartPage() {
        Map arg = new HashMap();
        arg.put(FactSheetHistoryCtrl.PROFILE_PAGE, this);

        ComponentHelper.createWindow("/student/factSheet/winFactSheetHistory.zul", "winFactSheetHistory", arg).doModal();
    }

    @Listen("onSelect = #cmbKind")
    public void choiceType() {
        TypeFactSheetModel value = cmbKind.getSelectedItem().getValue();

        if (value.getIdTypeFactSheet() == 2) {
            cbOfficialSeal.setChecked(true);
            cbOfficialSeal.setDisabled(true);
        } else {
            cbOfficialSeal.setChecked(false);
            cbOfficialSeal.setDisabled(false);
        }
    }

    @Listen("onClick = #btnOrder")
    public void order() {
        if (cmbKind.getSelectedItem() == null) {
            PopupUtil.showWarning("Нужно выбрать тип справки");
            return;
        }

        if (cmbGroup.getItemCount() != 0 && cmbGroup.getSelectedItem() == null) {
            PopupUtil.showWarning("Нужно выбрать группу");
            return;
        }

        TypeFactSheetModel value = cmbKind.getSelectedItem().getValue();

        int count = spin.getValue();
        for (int i = 0; i < count; i++) {
            if (factSheetService
                    .addFactSheet(template.getCurrentUser().getIdHum(), value.getIdTypeFactSheet(), cbOfficialSeal.isChecked(), true,
                                  cmbGroup.getItemCount() != 0
                                  ? studentGroups.get(cmbGroup.getSelectedIndex())
                                  : template.getCurrentUser().getGroupname()
                    )) {
                log.info(template.getCurrentUser().getFio() + " - Заказал справку");
            } else {
                log.info("У пользователя " + template.getCurrentUser().getFio() + " возникла ошибка при попытке заказать справку");
                Messagebox.show("Не удалось создать заявку. Обратитесь к администратору или в деканат.", "Ошибка", Messagebox.OK,
                                Messagebox.ERROR
                );
                return;
            }
        }

        String deadline;

        if (cbOfficialSeal.isChecked()) {
            deadline = "Срок выполнения 10 рабочих дней";
        } else {
            deadline = "Срок выполнения 3 рабочих дня";
        }

        if (currentHuman.getGetNotification() && currentHuman.getEmail() != null && sender != null) {
            try {
                sender.sendSimpleMessage(
                        currentHuman.getEmail(), "Сервис заказа справок ИКИТ", "Справка поступила на рассмотрение\n" + deadline);
            } catch (MessagingException e) {
                PopupUtil.showError("Не удалось отправить сообщение на почту: " + e.getMessage());
                e.printStackTrace();
            }
        }

        Messagebox.show("Ваша заявка принята\n" + deadline, "Успешно", Messagebox.OK, Messagebox.INFORMATION);
    }

    @Listen("onClick = #labelReferenceMaterial")
    public void dropDown() {

    }
}