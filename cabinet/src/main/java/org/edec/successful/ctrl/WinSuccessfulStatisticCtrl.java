package org.edec.successful.ctrl;

import org.edec.utility.zk.CabinetSelector;
import org.zkoss.chart.Charts;
import org.zkoss.chart.model.CategoryModel;
import org.zkoss.chart.model.DefaultCategoryModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Popup;

public class WinSuccessfulStatisticCtrl extends SelectorComposer<Component> {

    @Wire
    private Charts chart;

    @Wire
    private Label lblSettings;

    @Wire
    private Popup popupSettings;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        CategoryModel model = new DefaultCategoryModel();
        model.setValue("КИ17-03Б", "Осень 16/17", 75);
        model.setValue("КИ17-03Б", "Весна 16/17", 55);
        model.setValue("КИ17-03Б", "Осень 17/18", 75);
        model.setValue("КИ17-03Б", "Весна 17/18", 55);
        model.setValue("КИ17-03Б", "Осень 18/19", 62);
        model.setValue("КИ17-03Б", "Весна 18/19", 60);
        model.setValue("КИ17-16Б", "Осень 17/18", 88);
        model.setValue("КИ17-16Б", "Весна 17/18", 82);
        model.setValue("КИ17-16Б", "Осень 18/19", 70);
        model.setValue("КИ17-16Б", "Весна 18/19", 75);

        chart.getYAxis().setTitle("Успеваемость, %");
        chart.getXAxis().setTitle("Семестр");

        chart.getYAxis().setMin(0);
        chart.getYAxis().setMax(100);

        chart.setModel(model);
    }

    @Listen("onClick = #lblSettings")
    public void openFilters() {
        popupSettings.open(lblSettings, "after_start");
    }
}
