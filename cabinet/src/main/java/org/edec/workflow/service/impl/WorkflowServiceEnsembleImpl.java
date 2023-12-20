package org.edec.workflow.service.impl;

import lombok.extern.log4j.Log4j;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.order.manager.EntityManagerOrderESO;
import org.edec.utility.fileManager.FilePath;
import org.edec.utility.httpclient.manager.HttpClient;
import org.edec.workflow.model.WorkflowModel;
import org.edec.workflow.service.WorkflowService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.zkoss.zk.ui.Executions;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Max Dimukhametov
 */
@Log4j
public class WorkflowServiceEnsembleImpl implements WorkflowService {

    private static final String URL_GET_ALL_TASKS = "workflow.getAllTasks";
    private static final String URL_GET_ALL_OPERATION_TASKS = "workflow.getAllOperationTasks";
    private static final String URL_GET_ALL_ARCHIVE_TASKS = "workflow.getAllArchiveTasks";
    private static final String URL_GET_TASKS_BY_HUM = "workflow.getTasks";
    private static final String URL_GET_ARCHIVE_TASKS_BY_HUM = "workflow.getArchiveTasks";
    private static final String URL_GET_ARCHIVE_TASKS_CONFIRMING_BY_ID_SESSION = "workflow.getArchiveConfirmingTask";
    private static final String URL_PUT_TASK = "workflow.putTask";

    private Properties properties;
    private UserModel currentUser;
    private EntityManagerOrderESO managerOrderESO = new EntityManagerOrderESO();

    public WorkflowServiceEnsembleImpl () {
        currentUser = new TemplatePageCtrl().getCurrentUser();
        try {
            properties = new FilePath().getDataServiceProp();
        } catch (IOException e) {
            e.printStackTrace();
            log.error(currentUser.getFio(), e);
        }
    }

    public WorkflowServiceEnsembleImpl (ServletContext servletContext) {
        try {
            properties = new FilePath(servletContext).getDataServiceProp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<WorkflowModel> getAllNewTask () {
        return getWorkflowByPath(properties.getProperty(URL_GET_ALL_TASKS));
    }

    @Override
    public List<WorkflowModel> getAllNewOperationTasks () {
        return getWorkflowByPath(properties.getProperty(URL_GET_ALL_OPERATION_TASKS));
    }

    @Override
    public List<WorkflowModel> getAllArchiveTask () {
        return getWorkflowByPath(properties.getProperty(URL_GET_ALL_ARCHIVE_TASKS));
    }

    @Override
    public List<WorkflowModel> getAllNewTasksByIdHum (Long idHumanface) {
        return getWorkflowByPath(properties.getProperty(URL_GET_TASKS_BY_HUM) + idHumanface);
    }

    @Override
    public List<WorkflowModel> getArchiveTaskListByIdHum (Long idHumanface) {
        return getWorkflowByPath(properties.getProperty(URL_GET_ARCHIVE_TASKS_BY_HUM) + idHumanface);
    }

    @Override
    public List<WorkflowModel> getArchiveTasksConfirmingByIdBP (Long idTask) {
        return getWorkflowByPath(properties.getProperty(URL_GET_ARCHIVE_TASKS_CONFIRMING_BY_ID_SESSION) + idTask);
    }

    @Override
    public String updateOrderTask (WorkflowModel data) {
        log.info("Приказ (" + data.getPathFile() + ", id: " + (data.getOrderId() != null ? data.getOrderId() : "") +
                 ") обновлен с действием (" + data.getAction() + ")");
        JSONObject jsonData = new JSONObject();
        jsonData.put("Action", data.getAction());
        jsonData.put("Reason", data.getReason());
        if (currentUser != null) {
            jsonData.put("IdHum", currentUser.getIdHum());
        }
        jsonData.put("OrderId", data.getOrderId());
        jsonData.put("CertNumber", data.getCertNumber());
        jsonData.put("LotusID", data.getLotusID());
        jsonData.put("OrderNumber", data.getOrderNumber());
        JSONObject jsonObject = new JSONObject(
                HttpClient.makeHttpRequest(properties.getProperty(URL_PUT_TASK) + data.getTaskId(), HttpClient.PUT, new ArrayList<>(),
                                           jsonData.toString()
                ));
        if (!jsonObject.has("Status")) {
            return null;
        }
        return jsonObject.get("Status").toString();
    }

    @Override
    public List<Long> getOrdersByStudentFio(String fio) {
        return managerOrderESO.getOrdersByStudentFio(fio);
    }

    private List<WorkflowModel> getWorkflowByPath (String path) {
        try {
            JSONObject jsonObject = new JSONObject(HttpClient.makeHttpRequest(path, HttpClient.GET, new ArrayList<>(), null));
            if (!jsonObject.has("children")) {
                return new ArrayList<>();
            }
            JSONArray jsonArray = jsonObject.getJSONArray("children");
            List<WorkflowModel> workflows = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); ++i) {
                workflows.add(new WorkflowModel(jsonArray.getJSONObject(i)));
            }
            return workflows;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(currentUser.getFio(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<WorkflowModel> getArchiveTasksConfirmingByOrderId (Long idOrder) {
        return getWorkflowByPath(properties.getProperty(URL_GET_ARCHIVE_TASKS_CONFIRMING_BY_ID_SESSION)+"num/" + idOrder);
    }
}