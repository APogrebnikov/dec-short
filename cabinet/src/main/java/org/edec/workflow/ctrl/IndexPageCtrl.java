package org.edec.workflow.ctrl;

import lombok.extern.log4j.Log4j;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.edec.workflow.ctrl.renderer.TaskArchiveRenderer;
import org.edec.workflow.ctrl.renderer.TaskRenderer;
import org.edec.workflow.model.WorkflowModel;
import org.edec.workflow.service.WorkflowService;
import org.edec.workflow.service.impl.WorkflowServiceEnsembleImpl;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Hbox hbSignChoose;

    @Wire
    private Listbox lbTaskList, lbArchiveTaskList;

    @Wire
    private Listheader lhrExecutor;

    @Wire
    private Textbox tbFilterByFio;

    private WorkflowService workflowService = new WorkflowServiceEnsembleImpl();

    private boolean superVisor = false;

    protected void fill() {
        if (currentModule.isReadonly()) {
            superVisor = true;
        } else {
            lhrExecutor.setVisible(false);
            hbSignChoose.setVisible(true);
        }
        Executions.getCurrent().getSession().setAttribute("workflow_index_page", this);
        lbTaskList.setItemRenderer(new TaskRenderer(superVisor));
        lbArchiveTaskList.setItemRenderer(new TaskArchiveRenderer());
        Clients.showBusy(lbTaskList, "Загрузка данных");
        Events.echoEvent("onLater", lbTaskList, null);
    }

    @Listen("onClick = #btnRefresh")
    public void refreshTaskList() {
        Clients.showBusy(lbTaskList, "Загрузка данных");
        Events.echoEvent("onLater", lbTaskList, null);
    }

    @Listen("onLater = #lbTaskList")
    public void onLaterTaskList() {
        ListModelList<WorkflowModel> lmWfList;
        if (superVisor) {
            lmWfList = new ListModelList<>(workflowService.getAllNewTask());
        } else {
            lmWfList = new ListModelList<>(workflowService.getAllNewTasksByIdHum(getCurrentUser().getIdHum()));
            lmWfList.setMultiple(true);
            lbTaskList.setCheckmark(true);
            lbTaskList.setMultiple(true);
        }
        lbTaskList.setModel(lmWfList);
        lbTaskList.renderAll();
        Clients.clearBusy(lbTaskList);
    }

    @Listen("onSelect = #tabArchive")
    public void selectTabArchive() {
        Clients.showBusy(lbArchiveTaskList, "Загрузка данных");
        Events.echoEvent("onLater", lbArchiveTaskList, null);
    }

    @Listen("onClick = #btnArchiveRefresh; onOK = #tbFilterByFio")
    public void refreshArchiveTaskList() {
        Clients.showBusy(lbArchiveTaskList, "Загрузка данных");
        Events.echoEvent("onLater", lbArchiveTaskList, null);
    }

    @Listen("onLater = #lbArchiveTaskList")
    public void onLaterArchiveTaskList() {
        List<WorkflowModel> listTask;
        if (superVisor) {
            listTask = workflowService.getAllArchiveTask();
        } else {
            listTask = workflowService.getArchiveTaskListByIdHum(getCurrentUser().getIdHum());
        }

        if (!tbFilterByFio.getText().isEmpty()) {
            List<Long> orderIDs = workflowService.getOrdersByStudentFio(tbFilterByFio.getText());

            listTask = listTask.stream()
                               .filter(task -> orderIDs.contains((long) task.getOrderId().intValue()))
                               .collect(Collectors.toList());
        }

        lbArchiveTaskList.setModel(new ListModelList<>(listTask));
        lbArchiveTaskList.renderAll();
        Clients.clearBusy(lbArchiveTaskList);
    }

    @Listen("onClick = #btnSignChoose")
    public void signChoose() {
        if (lbTaskList.getSelectedCount() == 0) {
            PopupUtil.showWarning("Выберите хотя бы одну задачу");
            return;
        }
        List<WorkflowModel> selectedWorkflowTask = new ArrayList<>();
        for (Listitem li : lbTaskList.getSelectedItems()) {
            selectedWorkflowTask.add(li.getValue());
        }
        Map arg = new HashMap();
        arg.put(WinSignChoose.SELECTED_WF_TASK, selectedWorkflowTask);
        ComponentHelper.createWindow("/workflow/winSignChoose.zul", "winPdfViewer", arg).doModal();
    }
}
