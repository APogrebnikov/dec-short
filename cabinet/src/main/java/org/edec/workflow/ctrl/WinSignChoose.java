package org.edec.workflow.ctrl;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.edec.workflow.model.WorkflowModel;
import org.edec.workflow.service.WorkflowService;
import org.edec.workflow.service.impl.WorkflowServiceEnsembleImpl;
import org.zkoss.json.JSONObject;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Progressmeter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmmax on 20.04.2017.
 */
public class WinSignChoose extends SelectorComposer<Component> {
    @Wire
    private Progressmeter currMet;
    @Wire
    private Label lbInfo;
    @Wire
    private Label lbProgress;
    @Wire
    private Label lbUser;
    @Wire
    private Button btnSign;

    String cert = "";
    String serialNumber = "";
    Integer percent;

    public static final Logger log = Logger.getLogger(WinSignChoose.class.getName());

    public static final String SELECTED_WF_TASK = "selected_wf_task";

    private FileManager fileManager = new FileManager();

    private WorkflowService workflowService = new WorkflowServiceEnsembleImpl();

    private List<WorkflowModel> selectedTasks;

    private List<String> paths = new ArrayList<String>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        selectedTasks = (List<WorkflowModel>) Executions.getCurrent().getArg().get(SELECTED_WF_TASK);
        if (selectedTasks != null) {
            lbInfo.setValue("Будут подписанны приказы в количестве: " + selectedTasks.size());
        } else {
            btnSign.setDisabled(true);
        }
    }

    @Listen("onClick = #btnSign")
    public void testSign() {
        cert = "";
        currMet.setValue(0);

        //Clients.showBusy("Процесс подписания...");
        getCertFromJS();
    }

    /**
     * Запрос на получение сертификата в js
     */
    public void getCertFromJS() {
        Clients.evalJavaScript("initSign()");
    }

    /**
     * Получение сертификата из js метода
     */
    @Listen("onCertIn = #winPdfViewer")
    public void setCertFromJS(Event ev) {
        JSONObject evData = (JSONObject) ev.getData();
        cert = (String) evData.get("cert");
        serialNumber = (String) evData.get("serialNumber");
        String subject = (String) evData.get("subject");
        subject = subject.split(",")[0];
        subject = subject.replace("CN=", "");

        lbUser.setValue(subject);

        initSign(0);
    }

    /**
     * Вызов подписания одного документа
     *
     * @param step
     */
    public void initSign(Integer step) {
        WorkflowModel wf = selectedTasks.get(step);
        String regular = "((\\\\)|(/))(order)*(\\d)*(\\u002E)(pdf)";
        wf.setPathFile(wf.getPathFile().toLowerCase());
        if (wf.getPathFile().endsWith(".pdf")) {
            wf.setPathFile(wf.getPathFile().replaceAll(regular, ""));
        }
        //TODO: Проверить насколько корректно [0]
        for (int i = 0; i < fileManager.getFilesByFullPath(wf.getPathFile()).length; i++) {
            if (fileManager.getFilesByFullPath(wf.getPathFile())[i].isFile()) {
                File file = fileManager.getFilesByFullPath(wf.getPathFile())[i];
                paths.add(file.getAbsolutePath());
                byte[] buffer = null;
                try {
                    buffer = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Base64 base64 = new Base64();
                String res = new String(base64.encode(buffer));

                //Вызов JS по подписи
                Clients.evalJavaScript("signOne('" + res + "','" + cert + "','" + step + "')");
            }
        }
    }

    /**
     * Завершение одной задачи
     */
    @Listen("onSignOne = #winPdfViewer")
    public void workflowStep(Event ev) {
        JSONObject evData = (JSONObject) ev.getData();
        Integer step = Integer.valueOf((String) evData.get("Step"));
        String baseSign = (String) evData.get("Data");
        //Запись подписанного файла
        byte deArr[] = Base64.decodeBase64(baseSign);
        FileOutputStream fos;
        try {
            //System.out.println(listOfFile.get(step).getAbsolutePath());
            fos = new FileOutputStream(paths.get(step));
            fos.write(deArr);
            fos.close();
            percent = (step + 1) * 100 / selectedTasks.size();
            currMet.setValue(percent);
            lbInfo.setValue((step + 1) + " из " + selectedTasks.size() + " подписано");
        } catch (IOException e) {
            log.error("Error update data of task ID" + paths.get(step) + ". file not found", e);
            Messagebox.show("Не удалось подписать файл" + paths.get(step), "Ошибка при подписании", org.zkoss.zul.Messagebox.OK,
                            org.zkoss.zul.Messagebox.ERROR
            );
            return;
        } finally {
            Clients.clearBusy();
        }

        //Завершение одной задачи на человеке
        WorkflowModel wf = selectedTasks.get(step);
        wf.setAction(WorkflowModel.STATUS_SIGNED_EN);
        wf.setStatus("Подписана");
        wf.setCertNumber(serialNumber);
        String status = workflowService.updateOrderTask(wf);
        if (status == null || status.equals("Error")) {
            System.out.println("Error update data of task ID" + wf.getTaskId() + ". Data respons not received.");
            log.error("Error update data of task ID" + wf.getTaskId() + ". Data respons not received.");
            Messagebox.show("Ошибка обновления статуса задачи. Обратитесь к адмиинистратору.", "Ошибка!", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        //Проверяем, есть ли еще файлы на подпись
        if (step < (selectedTasks.size() - 1)) {
            //Переходим к следующему документу
            initSign(++step);
        } else {
            //Завершаем процесс мультиподписи
            finishAllSign();
        }
    }

    /**
     * Завершение подписания
     */
    @Listen("onFinishSign = #winPdfViewer")
    public void finishAllSign() {
        currMet.setValue(100);
        PopupUtil.showInfo("Все выбранные файлы подписаны");
        IndexPageCtrl pageCtrl = (IndexPageCtrl) Executions.getCurrent().getSession().getAttribute("workflow_index_page");
        pageCtrl.refreshTaskList();
        getSelf().detach();
    }

    @Listen("onErrorSign = #winPdfViewer")
    public void errorSign(Event ev) {
        Clients.clearBusy();
        DialogUtil.error(ev.getData().toString());
        IndexPageCtrl pageCtrl = (IndexPageCtrl) Executions.getCurrent().getSession().getAttribute("workflow_index_page");
        pageCtrl.refreshTaskList();
        getSelf().detach();
    }
}
