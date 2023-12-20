package org.edec.utility.pdfViewer.ctrl;

import org.apache.log4j.Logger;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.edec.workflow.ctrl.IndexPageCtrl;
import org.edec.workflow.model.WorkflowModel;
import org.edec.workflow.service.WorkflowService;
import org.edec.workflow.service.impl.WorkflowServiceEnsembleImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * Контроллер диалогового окна отклонения.
 *
 * @author DChiginev
 */
public class ViewCancelDialogPageCtrl extends SelectorComposer<Component> {
    public static final Logger log = Logger.getLogger(ViewCancelDialogPageCtrl.class.getName());

    public static final String WORKFLOW_MODEL = "workflow_model";
    public static final String WIN_PDFVIEWER = "win_pdfviewer";

    @Wire
    private Window winCancelDialog;
    @Wire
    private Textbox txBoxCancelReason;

    private IndexPageCtrl pageCtrl;
    private UserModel currentUser;
    private WorkflowModel workflowModel;
    private Window winPdfViewer;

    private WorkflowService workflowService = new WorkflowServiceEnsembleImpl();

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        pageCtrl = (IndexPageCtrl) Executions.getCurrent().getSession().getAttribute("workflow_index_page");
        currentUser = new TemplatePageCtrl().getCurrentUser();
        workflowModel = (WorkflowModel) Executions.getCurrent().getArg().get(WORKFLOW_MODEL);
        winPdfViewer = (Window) Executions.getCurrent().getArg().get(WIN_PDFVIEWER);
    }

    /**
     * Метод обработки события ON_CLICK кнопки "Отклонить".
     */
    @Listen("onClick = #btnCancel")
    public void cancel () {
        try {
            if (txBoxCancelReason.getValue() != null && !txBoxCancelReason.getValue().equals("")) {
                workflowModel.setStatus("Отклонена");
                workflowModel.setAction(WorkflowModel.STATUS_UNSIGNED_EN);
                workflowModel.setReason(txBoxCancelReason.getValue());
                String status = workflowService.updateOrderTask(workflowModel);
                if (status == null || status.equals("Error")) {
                    System.out.println("Error update data of task ID" + workflowModel.getTaskId() + ". Data respons not received.");
                    log.error("Error update data of task ID" + workflowModel.getTaskId() + ". Data respons not received.");
                    PopupUtil.showError("Ошибка обновления статуса задачи.");
                }
                winCancelDialog.detach();
                winPdfViewer.detach();
                pageCtrl.refreshTaskList();
            } else {
                DialogUtil.exclamation("Вы не указали причину отклонения.");
                txBoxCancelReason.focus();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(currentUser.getFio() + ", ID task: " + workflowModel.getTaskId(), e);
        }
    }
}
