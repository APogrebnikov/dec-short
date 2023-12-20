package org.edec.workflow.ctrl.renderer;

import org.edec.newOrder.report.ReportService;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.zk.ComponentHelper;
import org.edec.workflow.ctrl.WinArcchiveCtrl;
import org.edec.workflow.model.WorkflowModel;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class TaskArchiveRenderer implements ListitemRenderer<WorkflowModel> {
    private ComponentService componentService = new ComponentServiceESOimpl();

    @Override
    public void render (Listitem li, final WorkflowModel data, int index) throws Exception {
        li.setValue(data);
        componentService.createListcell(li, String.valueOf(index + 1), "", "cwf-listitem-label", "");
        componentService.createListcell(li, data.getSubject() + " " + data.getOrderNumber(), "margin-left: 5px", "cwf-listitem-label", "");
        componentService.createListcell(li, formatDate(data.getTimeCreated()), "", "", "");
        componentService.createListcell(li, formatDate(data.getTimeComplited()), "", "", "");
        componentService.createListcell(li, data.getStatusTask(), "", "", "");

        Listcell lcPdf = new Listcell();
        Button btnShowPdf = new Button("", "/imgs/pdf.png");
        btnShowPdf.setParent(lcPdf);

        btnShowPdf.addEventListener(Events.ON_CLICK, e -> {
            JasperReport jasperReport = new ReportService().getJasperForOrder(data.getOrderId());
            if (jasperReport != null){
                jasperReport.showPdf();
            } else {
                org.zkoss.zhtml.Messagebox.show("Ошибка при загрузке документов, обратитесь к администратору");
            }
        });

        lcPdf.setParent(li);

        li.addEventListener(Events.ON_CLICK, getEvent(data));
    }

    private String formatDate(String date) {
        if(date == null || date.equals("")) {
            return "";
        }

        Date d = DateConverter.convertStringToDate(date, "yyyy-MM-dd HH:mm:ss");
        return DateConverter.convertDateToStringByFormat(d, "dd.MM.yyyy HH:mm:ss");
    }

    private EventListener<Event> getEvent (final WorkflowModel data) {
        return event -> {
            if (Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winArchive") != null) {
                Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winArchive").detach();
            }

            Map arg = new HashMap();
            arg.put(WinArcchiveCtrl.SELECTED_WORKFLOW, data);
            ComponentHelper.createWindow("/workflow/winArchive.zul", "winArchive", arg).doModal();
        };
    }
}
