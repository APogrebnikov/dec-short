package org.edec.workflow.ctrl.renderer;

import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.pdfViewer.model.PdfViewer;
import org.edec.workflow.model.WorkflowModel;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

/**
 * @author Max Dimukhametov
 */
public class TaskRenderer implements ListitemRenderer<WorkflowModel> {
    private ComponentService componentService = new ComponentServiceESOimpl();
    private Boolean readonly;

    public TaskRenderer (Boolean readonly) {
        this.readonly = readonly;
    }

    @Override
    public void render (final Listitem li, final WorkflowModel data, int index) throws Exception {
        li.setValue(data);
        componentService.createListcell(li, String.valueOf(index + 1), "", "cwf-listitem-label", "");
        componentService.createListcell(li, data.getSubject() + " " + data.getOrderNumber(), "margin-left: 5px", "cwf-listitem-label", "");
        componentService.createListcell(li, data.getTimeCreated(), "color: #333;", "cwf-listitem-label", "");
        if (readonly) {
            componentService.createListcell(li, data.getFio(), "", "cwf-listitem-label", "");
        } else {
            new Listcell().setParent(li);
        }

        Listcell lcBtn = new Listcell();
        lcBtn.setParent(li);
        Button btn = new Button("Просмотр");
        btn.setParent(lcBtn);
        btn.setSclass("cwf-btn");
        btn.setIconSclass("z-icon-search");
        btn.addEventListener(Events.ON_CLICK, addEvent(li, data));
        li.addEventListener(Events.ON_DOUBLE_CLICK, addEvent(li, data));
    }

    private EventListener addEvent (final Listitem li, final WorkflowModel data) {
        return new EventListener<Event>() {

            @Override
            public void onEvent (Event event) throws Exception {
                li.setSelected(true);
                PdfViewer pdfViewer = new PdfViewer(data);
                pdfViewer.showDirectory();
            }
        };
    }
}
