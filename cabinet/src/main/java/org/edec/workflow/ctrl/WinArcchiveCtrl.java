package org.edec.workflow.ctrl;

import org.edec.utility.pdfViewer.model.PdfViewer;
import org.edec.workflow.ctrl.renderer.TaskArchiveConfirmingRenderer;
import org.edec.workflow.model.WorkflowModel;
import org.edec.workflow.service.WorkflowService;
import org.edec.workflow.service.impl.WorkflowServiceEnsembleImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;


public class WinArcchiveCtrl extends SelectorComposer<Component> {
    public static final String SELECTED_WORKFLOW = "selected_workflow";

    @Wire
    private Label lProcessSign, lDateBeginBP, lDateEndBP;

    @Wire
    private Listbox lbConfirming;

    private WorkflowService workflowService = new WorkflowServiceEnsembleImpl();

    private WorkflowModel selectedWorkflow;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        selectedWorkflow = (WorkflowModel) Executions.getCurrent().getArg().get(SELECTED_WORKFLOW);
        fill();
    }

    private void fill () {
        lProcessSign.setValue("Процесс подписания:(" + selectedWorkflow.getSubject() + ")");
        lDateBeginBP.setValue(selectedWorkflow.getTimeCreated());
        lDateEndBP.setValue(selectedWorkflow.getTimeComplited());
        lbConfirming.setItemRenderer(new TaskArchiveConfirmingRenderer());
        lbConfirming.setModel(
                new ListModelList<Object>(workflowService.getArchiveTasksConfirmingByIdBP(Long.valueOf(selectedWorkflow.getSessionId()))));
        lbConfirming.renderAll();
    }

    @Listen("onClick = #btnShowArchiveAttachFile")
    public void showAttachFiles () {
        PdfViewer pdfViewer = new PdfViewer(selectedWorkflow);
        pdfViewer.showDirectory();
    }
}