package org.edec.dynamicReport.ctrl;

import org.edec.dynamicReport.service.DynamicReportService;
import org.edec.dynamicReport.service.impl.DynamicReportServiceImpl;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.CabinetSelector;
import org.json.JSONObject;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.awt.Button;

/**
 * Created by lunin on 17.03.2018.
 */
public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Button btnDisplayData;

    @Wire
    private Div divDisplayData;

    private DynamicReportService dynamicReportService = new DynamicReportServiceImpl();

    private String measures;
    private String qualityMeasures;
    private String filters;

    @Override
    protected void fill () {
        measures = dynamicReportService.getListMeasures();
        qualityMeasures = dynamicReportService.getListQualityMeasures();
        filters = dynamicReportService.getListFilters();
        Events.echoEvent("onAfterRenderer", divDisplayData, null);
    }

    @Listen("onAfterRenderer = #divDisplayData")
    public void onMyEvent () {
        Clients.evalJavaScript("buildPivotGrid(" + measures + ", " + filters + ");");
    }

    @Listen("onRebuild = #divDisplayData")
    public void displayData (Event ev) {
        Clients.clearBusy();
        try {
            org.zkoss.json.JSONObject object = (org.zkoss.json.JSONObject) ev.getData();
            JSONObject result = dynamicReportService.MDXQuery(new JSONObject(object.toString()));
            Clients.evalJavaScript("rebuildPivotGrid(" + result + ");");
        } catch (Exception e) {
            PopupUtil.showError("Ошибка, попробуйте еще раз");
        }
    }
}
