package org.edec.newOrder.ctrl;

import org.edec.newOrder.service.CreateOrderService;
import org.edec.newOrder.service.esoImpl.CreateOrderServiceESO;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WinGroupChooserCtrl extends CabinetSelector {
    public static final String SELECTED_GROUPS = "order_model";
    public static final String UPDATE_SELECTED_GROUPS = "update_selected_groups";
    @Wire
    private Window winGroupChooser;
    @Wire
    private Listbox lbGroups;
    @Wire
    private Textbox tbGroupName;
    @Wire
    private Button btnApply;
    @Wire
    private Checkbox chbOnlySelected;
    private Runnable updateSelectedGroups;
    private Set<String> selectedGroups;
    private Set<String> allGroups;

    private CreateOrderServiceESO createOrderService = new CreateOrderServiceESO();

    @Override
    protected void fill() {
        updateSelectedGroups = (Runnable) Executions.getCurrent().getArg().get(UPDATE_SELECTED_GROUPS);
        selectedGroups = (Set<String>) Executions.getCurrent().getArg().get(SELECTED_GROUPS);

        allGroups = new HashSet<>(createOrderService.getListGroupsByCurrentSemForFoc());

        lbGroups.setItemRenderer((ListitemRenderer<String>) (listitem, s, i) -> {
            listitem.setValue(s);
            new Listcell(s).setParent(listitem);
        });

        ListModelList<String> listModelList = new ListModelList<>(allGroups);
        listModelList.setMultiple(true);
        lbGroups.setModel(listModelList);

        lbGroups.addEventListener("onAfterRender", e -> {
            lbGroups.getItems().forEach(el -> {
                if(selectedGroups.contains(el.<String>getValue())) {
                    el.setSelected(true);
                }
            });
        });

        lbGroups.addEventListener(Events.ON_CLICK, e -> {
            selectedGroups.removeAll(
                    lbGroups.getItems().stream().filter(row -> !row.isSelected()).map(Listitem::<String>getValue).collect(Collectors.toList())
            );

            selectedGroups.addAll(
                    lbGroups.getItems().stream().filter(Listitem::isSelected).map(Listitem::<String>getValue).collect(Collectors.toList())
            );

            updateSelectedGroups.run();
        });

        lbGroups.addEventListener(Events.ON_SELECT, e-> {
            selectedGroups.removeAll(
                    lbGroups.getItems().stream().filter(row -> !row.isSelected()).map(Listitem::<String>getValue).collect(Collectors.toList())
            );

            selectedGroups.addAll(
                    lbGroups.getItems().stream().filter(Listitem::isSelected).map(Listitem::<String>getValue).collect(Collectors.toList())
            );

            updateSelectedGroups.run();
        });

        lbGroups.setMultiple(true);
        lbGroups.setCheckmark(true);
        lbGroups.renderAll();

        tbGroupName.addEventListener(Events.ON_OK, e -> {
            ListModelList<String> listModel = new ListModelList<>(
                    allGroups.stream().filter(el -> el.toLowerCase().contains(tbGroupName.getValue().toLowerCase())).collect(Collectors.toList())
            );
            listModel.setMultiple(true);

            lbGroups.setModel(listModel);
            lbGroups.setMultiple(true);
            lbGroups.setCheckmark(true);
            lbGroups.renderAll();
        });

        btnApply.addEventListener(Events.ON_CLICK, e -> {
            winGroupChooser.detach();
        });

        chbOnlySelected.addEventListener(Events.ON_CLICK, e -> {
            ListModelList<String> listModel = null;
            if(chbOnlySelected.isChecked()) {
                listModel = new ListModelList<>(selectedGroups);
            } else {
                listModel = new ListModelList<>(
                        allGroups.stream().filter(el -> el.toLowerCase().contains(tbGroupName.getValue().toLowerCase())).collect(Collectors.toList())
                );
            }

            listModel.setMultiple(true);

            lbGroups.setModel(listModel);
            lbGroups.setMultiple(true);
            lbGroups.setCheckmark(true);
            lbGroups.renderAll();
        });
    }
}
