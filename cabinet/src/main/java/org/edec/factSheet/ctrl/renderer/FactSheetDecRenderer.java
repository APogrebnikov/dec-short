package org.edec.factSheet.ctrl.renderer;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.edec.factSheet.ctrl.IndexPageCtrl;
import org.edec.student.factSheet.ctrl.FactSheetCancelCtrl;
import org.edec.factSheet.model.FactSheetStatusEnum;
import org.edec.factSheet.model.FactSheetTableModel;
import org.edec.factSheet.service.FactSheetService;
import org.edec.factSheet.service.impl.FactSheetServiceImpl;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.email.Sender;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Log4j
public class FactSheetDecRenderer implements ListitemRenderer<FactSheetTableModel> {

    private ComponentService componentService = new ComponentServiceESOimpl();
    private FactSheetService factSheetService = new FactSheetServiceImpl();
    private Sender sender;

    private IndexPageCtrl indexPageCtrl;

    public FactSheetDecRenderer(IndexPageCtrl indexPage) {
        try {
            sender = new Sender();
        } catch (IOException e) {
            PopupUtil.showError("Оповещение на почту временно не работает");
            e.printStackTrace();
        }
        this.indexPageCtrl = indexPage;
    }

    @Override
    public void render(Listitem li, FactSheetTableModel data, int i) {
        li.setValue(data);

        if (data.getRegisterNumber().equals("")) {
            componentService.createListcell(li, "№ не присвоен", "color: #000;", "", "");
        } else {
            componentService.createListcell(li, String.valueOf(data.getRegisterNumber()), "color: #000;", "", "");
        }

        componentService.createListcell(li, data.getFio(), "color: #000;", "", "");
        componentService.createListcell(li, data.getGroupname(), "color: #000;", "", "");
        componentService.createListcell(li, data.getTitle(), "color: #000;", "", "");

        componentService.createListcell(li, data.getOfficialSeal() ? "Да" : "Нет", "color: #000;", "", "");
        componentService.createListcell(li, dateToStr(data.getDateCreate()), "color: #000;", "", "");
        componentService.createListcell(li, data.getCreatedByStudent() ? "Да" : "Нет", "color: #000;", "", "");

        Listcell lcStatus = new Listcell();
        lcStatus.setParent(li);
        Combobox cmbStatus = new Combobox();
        cmbStatus.setReadonly(true);
        cmbStatus.setWidth("95px");
        cmbStatus.setModel(new ListModelList<>(FactSheetStatusEnum.values()));
        cmbStatus.setParent(lcStatus);
        cmbStatus.addEventListener("onAfterRender", event -> {
            cmbStatus.setSelectedIndex(FactSheetStatusEnum.getEnumById(data.getIdFactSheetStatus()).ordinal());

            if (FactSheetStatusEnum.getEnumByStatusName(cmbStatus.getValue()) != FactSheetStatusEnum.CREATED) {
                cmbStatus.setDisabled(true);
            }
        });

        Listcell lcCompleted = new Listcell();
        lcCompleted.setParent(li);
        if (FactSheetStatusEnum.getEnumById(data.getIdFactSheetStatus()) == FactSheetStatusEnum.APPROVED) {
            if (data.getDateCompletion() == null) {
                Button btnCompleted = new Button();
                btnCompleted.setLabel("Выполнен");
                btnCompleted.setWidth("100px");
                btnCompleted.setParent(lcCompleted);
                FactSheetTableModel value = li.getValue();

                btnCompleted.addEventListener(Events.ON_CLICK, event -> {
                    btnCompleted.setDisabled(true);

                    if (factSheetService.updateCompletion(data.getIdFactSheet())) {
                        log.info("Справка № " + data.getRegisterNumber() + " была выполнена");

                        indexPageCtrl.callSearch(value);
                    } else {
                        log.info("Возникла ошибка при попытке зафиксировать выполнение справки № " + data.getRegisterNumber());
                    }

                    if (sender != null && data.getGetNotification() && data.getEmail() != null && !data.getEmail().equals("")) {
                        sender.sendSimpleMessage(data.getEmail(), "Сервис заказа справок ИКИТ", "Справка '" + data.getTitle() + "' готова");
                    }
                });
            } else {
                lcCompleted.setLabel(DateConverter.convertTimestampToString(data.getDateCompletion()));
                lcCompleted.setStyle("color: #000; font-size: 12px;");
            }
        }

        Listcell lcReceipt = new Listcell();
        lcReceipt.setParent(li);
        if (data.getDateCompletion() != null) {
            if (data.getDateReceipt() == null) {
                Button btnReceipt = new Button();
                btnReceipt.setLabel("Выдан");
                btnReceipt.setWidth("100px");
                btnReceipt.setParent(lcReceipt);
                FactSheetTableModel value = li.getValue();

                btnReceipt.addEventListener(Events.ON_CLICK, event -> {
                    btnReceipt.setDisabled(true);

                    if (factSheetService.updateReceipt(data.getIdFactSheet())) {
                        log.info("Справка № " + data.getRegisterNumber() + " была получена");

                        indexPageCtrl.callSearch(value);
                    } else {
                        log.info("Возникла ошибка при попытке зафиксировать получение справки № " + data.getRegisterNumber());
                    }

                    if (sender != null && data.getGetNotification() && data.getEmail() != null && !data.getEmail().equals("")) {
                        sender.sendSimpleMessage(data.getEmail(), "Сервис заказа справок ИКИТ",
                                "Справка '" + data.getTitle() + "' получена"
                        );
                    }
                });
            } else {
                lcReceipt.setLabel(DateConverter.convertTimestampToString(data.getDateReceipt()));
                lcReceipt.setStyle("color: #000; font-size: 12px;");
            }
        }


        cmbStatus.addEventListener(Events.ON_CHANGE, event -> {
            if (FactSheetStatusEnum.getEnumByStatusName(cmbStatus.getValue()) == FactSheetStatusEnum.CANCELED) {
                checkStartPage(cmbStatus, li);
            } else {
                FactSheetTableModel selectedHum = li.getValue();
                updateStatus(cmbStatus, li);
                setRegisterNumber(li);

                if (sender != null && selectedHum.getGetNotification() && selectedHum.getEmail() != null &&
                        !selectedHum.getEmail().equals("")) {
                    sender.sendSimpleMessage(selectedHum.getEmail(), "Сервис заказа справок ИКИТ",
                            "Справка '" + selectedHum.getTitle() + "' одобрена"
                    );
                }
            }
        });
        changeStyleForItem(li, data.getIdFactSheetStatus());
    }

    private void changeStyleForItem(Listitem li, int idFactSheetStatus) {
        switch (FactSheetStatusEnum.getEnumById(idFactSheetStatus)) {
            case CREATED:
                li.setStyle("background: #95FF82");
                break;
            case APPROVED:
                break;
            case CANCELED:
                li.setStyle("background: #FF7373");
                break;
        }
    }

    public void updateStatus(Combobox cmbStatus, Listitem listitem) {
        cmbStatus.setDisabled(true);
        FactSheetTableModel value = listitem.getValue();
        if (factSheetService.updateStatus(FactSheetStatusEnum.getEnumByStatusName(cmbStatus.getValue()).getStatusId(), value.getIdFactSheet())) {
            log.info("Статус справки № " + value.getRegisterNumber() + " был изменен на '" + cmbStatus.getValue() + "'");
            indexPageCtrl.callSearch(value);
        } else {
            log.info("Возникла ошибка при попытке изменить статус справки № " + value.getRegisterNumber());
        }
    }

    public void setRegisterNumber(Listitem listitem) {
        FactSheetTableModel value = listitem.getValue();
        if (factSheetService.setRegisterNumber(value.getIdFactSheet(), value.getGroupname())) {
            log.info("Справке с ID " + value.getIdFactSheet() + " был присвоен № регистрации - " + value.getRegisterNumber());
            indexPageCtrl.callSearch(value);
        } else {
            log.info("Возникла ошибка при попытке присвоить № регистрации справке с ID " + value.getIdFactSheet());
        }
    }

    /*public void deleteFactSheet(Listitem listitem) {
        FactSheetTableModel value = listitem.getValue();
        if (factSheetService.deleteFactSheet(value.getIdFactSheet())) {
            log.info("Справке с ID " + value.getIdFactSheet() + " была удалена");
        } else {
            log.info("Возникла ошибка при попытке удалить справку с ID " + value.getIdFactSheet());
        }
    }*/

    public void checkStartPage(Combobox cmbStatus, Listitem listitem) {
        Map arg = new HashMap();
        arg.put(FactSheetCancelCtrl.RENDERER, this);
        arg.put(FactSheetCancelCtrl.CMB_STATUS, cmbStatus);
        arg.put(FactSheetCancelCtrl.LISTITEM, listitem);

        ComponentHelper.createWindow("/factSheet/winRefusalFactSheet.zul", "winRefusalFactSheet", arg).doModal();
    }

    public String booleanToStr(Boolean bool) {

        if (bool) {
            return "Да";
        } else {
            return "Нет";
        }
    }

    public String dateToStr(Date date) {
        SimpleDateFormat dateFormat = null;
        dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        if (date == null) {
            return "Информации нет";
        } else {
            return dateFormat.format(date);
        }
    }
}
