package org.edec.student.factSheet.ctrl.renderer;

import org.apache.log4j.Logger;
import org.edec.factSheet.ctrl.renderer.FactSheetDecRenderer;
import org.edec.factSheet.model.FactSheetStatusEnum;
import org.edec.factSheet.model.FactSheetTableModel;
import org.edec.factSheet.service.FactSheetService;
import org.edec.factSheet.service.impl.FactSheetServiceImpl;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FactSheetHistoryRenderer implements ListitemRenderer<FactSheetTableModel> {
    private ComponentService componentService = new ComponentServiceESOimpl();
    private FactSheetService factSheetService = new FactSheetServiceImpl();
    private static final Logger log = Logger.getLogger(FactSheetDecRenderer.class.getName());

    @Override
    public void render (Listitem li, FactSheetTableModel factSheetTableModel, int i) throws Exception {
        li.setValue(factSheetTableModel);

        if (factSheetTableModel.getRegisterNumber().equals("")) {
            componentService.createListcell(li, "№ не присвоен", "color: #000;", "", "");
        } else {
            componentService.createListcell(
                    li, String.valueOf(factSheetTableModel.getRegisterNumber()), "color: #000;", "", "background: #fff;");
        }
        componentService.createListcell(
                li, factSheetTableModel.getFamily() + " " + factSheetTableModel.getName() + " " + factSheetTableModel.getPatronymic(),
                "color: #000;", "", "background: #fff;"
        );
        componentService.createListcell(li, factSheetTableModel.getGroupname(), "color: #000;", "", "background: #fff;");
        componentService.createListcell(li, factSheetTableModel.getTitle(), "color: #000;", "", "background: #fff;");
        componentService.createListcell(li, BooleanToStr(factSheetTableModel.getOfficialSeal()), "color: #000;", "", "background: #fff;");
        FactSheetStatusEnum factSheetStatusEnum = FactSheetStatusEnum.getEnumById(factSheetTableModel.getIdFactSheetStatus());
        componentService.createListcell(li, DateToStr(factSheetTableModel.getDateCreate()), "color: #000;", "", "background: #fff;");
        componentService.createListcell(li, factSheetStatusEnum.toString(), "color: #000;", "", "background: #fff;");
        componentService.createListcell(li, DateToStr(factSheetTableModel.getDateCompletion()), "color: #000;", "", "background: #fff;");
        componentService.createListcell(li, BooleanToStr(factSheetTableModel.getReceipt()), "color: #000;", "", "background: #fff;");

        Listcell lcСancel = new Listcell();
        lcСancel.setParent(li);
        if (FactSheetStatusEnum.getEnumById(factSheetTableModel.getIdFactSheetStatus()) == FactSheetStatusEnum.CREATED) {
            Button btnСancel = new Button();
            btnСancel.setLabel("Отменить");
            btnСancel.setWidth("130px");
            btnСancel.setParent(lcСancel);

            btnСancel.addEventListener(Events.ON_CLICK, event -> {
                if (btnСancel.getLabel().equals("Отменить")) {
                    btnСancel.setLabel("Восстановить");
                    factSheetTableModel.setDeleted(true);
                } else {
                    btnСancel.setLabel("Отменить");
                    factSheetTableModel.setDeleted(false);
                }
            });
        }
    }

    public String BooleanToStr (Boolean bool) {
        if (bool) {
            return "Да";
        } else {
            return "Нет";
        }
    }

    public void deleteFactSheet (Listbox listbox) {
        for (Listitem listitem : listbox.getItems()) {
            FactSheetTableModel value = listitem.getValue();
            if (value.getDeleted()) {
                if (factSheetService.deleteFactSheet(value.getIdFactSheet())) {
                    log.info("Справке с ID " + value.getIdFactSheet() + " была удалена");
                } else {
                    log.info("Возникла ошибка при попытке удалить справку с ID " + value.getIdFactSheet());
                }
            }
        }
    }

    public String DateToStr (Date date) {
        SimpleDateFormat dateFormat = null;
        dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        if (date == null) {
            return "Информации нет";
        } else {
            return dateFormat.format(date);
        }
    }
}
