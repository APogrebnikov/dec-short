package org.edec.newOrder.service;

import org.edec.newOrder.ctrl.WinGroupChooserCtrl;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zhtml.Li;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.*;
import org.zkoss.zul.impl.XulElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ComponentProvider {
    private final String ATR_TYPE = "type";

    public XulElement provideComponent(ComponentEnum componentEnum) {
        return provideComponent(componentEnum, "");
    }

    public XulElement provideComponent(ComponentEnum componentEnum, String initialValue) {
        if (componentEnum == null) {
            return null;
        }

        if (initialValue == null) {
            initialValue = "";
        }

        XulElement el = null;

        switch (componentEnum) {
            case DATEBOX:
                el = new Datebox();
                try {
                    ((Datebox) el).setValue(DateConverter.convertStringToDate(initialValue, "dd.MM.yyyy"));
                } catch (Exception e) {
                    // DO NOTHING
                }
                break;
            case CHECKBOX:
                el = new Checkbox();
                try {
                    ((Checkbox) el).setChecked(Boolean.getBoolean(initialValue));
                } catch (Exception e) {
                    // DO NOTHING
                }
                break;
            case TEXTBOX:
                el = new Textbox();
                ((Textbox) el).setValue(initialValue);
                break;
            case TEXTBOX_MULTILINE:
                el = new Textbox();
                ((Textbox) el).setValue(initialValue);
                ((Textbox) el).setMultiline(true);
                ((Textbox) el).setRows(4);
                break;
            case COMBOBOX_SEMESTER:
                el = createCombobox("Весна", "Осень");
                break;
            case COMBOBOX_COURSE:
                el = createCombobox("1", "2", "3", "4", "5", "6");
                break;
            case COMBOBOX_PERFORMANCE:
                el = createCombobox("хорошо", "хорошо и отлично", "отлично");
                break;
            case FILE:
                Vbox vbox = new Vbox();
                vbox.setAlign("center");
                Textbox nameFile = new Textbox("Название файла");
                nameFile.setReadonly(true);
                Button btnAddDocument = new Button("Добавить");
                btnAddDocument.setUpload("true,maxsize=3000000,multiple=true");
                vbox.setStyle(" margin-left: auto; margin-right: auto");
                vbox.setAttribute("Media", "");
                btnAddDocument.addEventListener(Events.ON_UPLOAD, (EventListener<UploadEvent>) event -> {

                    Arrays.stream(event.getMedias()).forEach(m -> {
                        nameFile.setValue(m.getName());
                        nameFile.focus();
                        vbox.setAttribute("Media", m);
                    });
                });

                vbox.appendChild(nameFile);
                vbox.appendChild(btnAddDocument);
                el = vbox;
                break;
            case GROUP_CHOOSER:
                Vbox vbox2 = new Vbox();
                vbox2.setAlign("center");
                vbox2.setStyle(" margin-left: auto; margin-right: auto; cursor: pointer");
                final Label lbCountGroups = new Label("Выберите группы");
                vbox2.appendChild(lbCountGroups);

                final Set<String> selectedGroups = new HashSet<>();

                vbox2.addEventListener(Events.ON_CLICK, e -> {
                    HashMap<String, Object> args = new HashMap<>();

                    Runnable updateListGroupsFunc = () -> {
                        if (selectedGroups.size() == 0) {
                            lbCountGroups.setValue("Выберите группы");
                            lbCountGroups.setTooltiptext("");
                        } else if (selectedGroups.size() == 1) {
                            lbCountGroups.setValue(selectedGroups.iterator().next());
                            lbCountGroups.setTooltiptext("");
                        } else {
                            lbCountGroups.setValue("Выбрано групп: " + selectedGroups.size());
                            lbCountGroups.setTooltiptext(selectedGroups.stream().collect(Collectors.joining("\n")));
                        }

                        vbox2.setAttribute("groups", selectedGroups);
                    };

                    args.put(WinGroupChooserCtrl.SELECTED_GROUPS, selectedGroups);
                    args.put(WinGroupChooserCtrl.UPDATE_SELECTED_GROUPS, updateListGroupsFunc);

                    ComponentHelper.createWindow("/newOrder/winGroupChooser.zul", "winGroupChooser", args).doModal();
                });

                el = vbox2;
                break;
        }

        el.setAttribute(ATR_TYPE, componentEnum);

        return el;
    }

    public Object getValueComponent(XulElement element) {
        if (element.getAttribute(ATR_TYPE) == null) {
            return null;
        }

        switch ((ComponentEnum) element.getAttribute(ATR_TYPE)) {
            case CHECKBOX:
                return ((Checkbox) element).isChecked();
            case DATEBOX:
                return ((Datebox) element).getValue();
            case TEXTBOX:
            case TEXTBOX_MULTILINE:
                return ((Textbox) element).getValue();
            case COMBOBOX_PERFORMANCE:
            case COMBOBOX_SEMESTER:
            case COMBOBOX_COURSE:
                return ((Combobox) element).getValue();
            case FILE:
                return element.getAttribute("Media");
            case GROUP_CHOOSER:
                return element.getAttribute("groups");
        }

        return null;
    }

    public Object getValueComponentForChangeColor(XulElement element) {
        if (element.getAttribute(ATR_TYPE) == null) {
            return null;
        }

        switch ((ComponentEnum) element.getAttribute(ATR_TYPE)) {
            case CHECKBOX:
                return ((Checkbox) element).isChecked();
            case DATEBOX:
                return ((Datebox) element).getValue();
            case TEXTBOX:
                return ((Textbox) element).getValue();
            case TEXTBOX_MULTILINE:
                return ((Textbox) element).getValue();
            case FILE:
                return element.getAttribute("Media");
            case GROUP_CHOOSER:
                return element.getAttribute("groups");
        }

        return null;
    }

    public Listheader createListheader(Listhead lh, String name, String sclass, String style) {
        Listheader lhr = new Listheader();
        Label label = new Label(name);
        label.setParent(lhr);
        label.setSclass(sclass);
        lhr.setParent(lh);
        lhr.setStyle(style);

        return lhr;
    }

    public Listcell createListcell(Listitem li, String name, String style) {
        // TODO исправить как будет время
        String shortName = name.length() <= 60 ? name : (name.substring(0, 59) + "...");

        final Listcell lc = new Listcell(shortName);
        lc.setTooltiptext(shortName);
        lc.setParent(li);
        lc.setStyle(style);

        return lc;
    }

    public Listcell createListcell(Listitem li, XulElement component, String style) {
        final Listcell lc = new Listcell();
        lc.appendChild(component);
        lc.setParent(li);
        lc.setStyle(style);

        return lc;
    }

    private Combobox createCombobox(String... listElements) {
        Combobox cmb = new Combobox();
        cmb.setStyle("width: 100px");
        cmb.setReadonly(true);
        cmb.getItems().addAll(Arrays.stream(listElements).map(Comboitem::new).collect(Collectors.toList()));
        cmb.setSelectedIndex(0);
        return cmb;
    }
}
