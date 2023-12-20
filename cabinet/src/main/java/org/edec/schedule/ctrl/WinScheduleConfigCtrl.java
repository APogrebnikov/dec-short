package org.edec.schedule.ctrl;

import org.edec.schedule.model.xls.XlsScheduleContainer;
import org.edec.schedule.service.xls.XlsScheduleParserConfig;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import java.util.List;

public class WinScheduleConfigCtrl extends Window {

    //Переменные
    private XlsScheduleParserConfig config;

    WinScheduleConfigCtrl(XlsScheduleParserConfig config) {
        this.config = config;

        setDefaultSettings();
        fill();
    }

    private void setDefaultSettings() {
        setTitle("Настройка конфигурации расписания");
        setClosable(true);
        setWidth("70%");
        setHeight("90%");
    }

    private void fill() {

        Hlayout hlContent = new Hlayout();
        hlContent.setParent(this);
        hlContent.setHflex("1");
        hlContent.setVflex("1");

        Vlayout currentConfigurationContent = createCurrentConfigurationContent();
        if (currentConfigurationContent == null) {
            new Label("Нет информации по текущей конфигурации").setParent(hlContent);
        } else {
            currentConfigurationContent.setParent(hlContent);
        }
        /*TODO Добавить поиск по другим институтам.
            Может быть необходимо в случае, если не совпало название у нас и в xls файле*/
    }

    private Vlayout createCurrentConfigurationContent() {

        if (config.getHolders().size() == 1) {
            return createIndividualHolderContent();
        } else if (config.getHolders().size() > 1) {
            return createMultipleHoldersContent();
        }
        return null;
    }

    private Vlayout createIndividualHolderContent() {

        Vlayout vlIndividualHolder = new Vlayout();

        Label lHolderName = new Label(config.getSelectedHolder().holderName());
        lHolderName.setParent(vlIndividualHolder);

        Listbox lbScheduleContainers = createListboxScheduleContainers();
        lbScheduleContainers.setParent(vlIndividualHolder);
        refreshLbScheduleContent(lbScheduleContainers, config.getSelectedHolder().schedules());

        return vlIndividualHolder;
    }

    private Vlayout createMultipleHoldersContent() {

        Vlayout vlMultipleHolders = new Vlayout();

        Hlayout hlAction = new Hlayout();
        hlAction.setParent(vlMultipleHolders);

        Combobox cmbHolders = new Combobox();
        cmbHolders.setParent(hlAction);
        cmbHolders.setReadonly(true);
        cmbHolders.setModel(new ListModelList<>(config.getHolders()));
        cmbHolders.addEventListener("onAfterRender", event -> cmbHolders.setSelectedIndex(config.getHolders().indexOf(config.getSelectedHolder())));

        Button btnSetSelectedHolder = new Button("Установить текущей конфигурацией");
        btnSetSelectedHolder.setParent(hlAction);
        btnSetSelectedHolder.setDisabled(true);

        Listbox lbScheduleContainers = createListboxScheduleContainers();
        lbScheduleContainers.setParent(vlMultipleHolders);
        refreshLbScheduleContent(lbScheduleContainers, config.getSelectedHolder().schedules());

        cmbHolders.addEventListener(Events.ON_SELECT, event -> btnSetSelectedHolder.setDisabled(cmbHolders.getSelectedItem().getValue() == config.getSelectedHolder()));
        btnSetSelectedHolder.addEventListener(Events.ON_CLICK, event -> {
            config.setSelectedHolder(cmbHolders.getSelectedItem().getValue());
            refreshLbScheduleContent(lbScheduleContainers, config.getSelectedHolder().schedules());
        });

        return vlMultipleHolders;
    }

    private Listbox createListboxScheduleContainers() {
        Listbox lbScheduleContainers = new Listbox();
        lbScheduleContainers.setItemRenderer((ListitemRenderer<XlsScheduleContainer>) (listitem, data, index) -> {
            new Listcell(String.valueOf(data.course())).setParent(listitem);
            new Listcell(data.qualification().getName()).setParent(listitem);

            Listcell lcLink = new Listcell();
            lcLink.setParent(listitem);

            A aLink = new A("Скачать xls");
            aLink.setParent(lcLink);
            aLink.setHref(data.url());
            aLink.setTarget("_blank");
        });
        return lbScheduleContainers;
    }

    private void refreshLbScheduleContent(Listbox listbox, List<XlsScheduleContainer> scheduleContainers) {
        listbox.setModel(new ListModelList<>(scheduleContainers));
        listbox.renderAll();
    }
}
