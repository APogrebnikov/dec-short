package org.edec.utility.pdfViewer.ctrl;

import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import org.apache.commons.codec.binary.Base64;
import org.edec.utility.pdfViewer.SimpleSignModel;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.json.JSONObject;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;
import org.zkoss.zul.Progressmeter;
import org.zkoss.zul.Window;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class WinSignFilesCtrl extends SelectorComposer<Window> {

    //Константы
    public static final String SIGN_MODELS = "signModels";

    //Компоненты
    @Wire
    private Label lSignFileInfo, lSignFileUser, lSignFileProgress;
    @Wire
    private Progressmeter progressSignFile;

    //Переменные
    private List<SimpleSignModel> signModels;
    private String certNumber, serialNumber;

    public static void showWindow(@NonNull List<SimpleSignModel> signModels) {
        Map<String, Object> arg = new HashMap<>();
        arg.put(SIGN_MODELS, signModels);
        ComponentHelper.createWindow("/utility/pdfViewer/winSignFiles.zul", "winSignFiles", arg).doModal();
    }

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
        signModels = (List<SimpleSignModel>) Executions.getCurrent().getArg().get(SIGN_MODELS);
        lSignFileInfo.setValue("Будут подписаны файлы в количестве: " + signModels.size());
    }

    @Listen("onClick = #btnSignAllFiles")
    public void sign() {
        certNumber = "";
        serialNumber = "";
        progressSignFile.setValue(0);
        //Запрос на получение сертификата в js
        Clients.evalJavaScript("initSign()");
    }

    /**
     * Получение сертификата из JS метода
     */
    @Listen("onCertIn = #winSignFiles")
    public void getCertFromJS(Event event) {
        JSONObject jsonData = new JSONObject(event.getData().toString());
        certNumber = jsonData.getString("cert");
        serialNumber = jsonData.getString("serialNumber");
        String subject = jsonData.getString("subject");
        subject = subject.split(",")[0].replace("CN=", "");
        lSignFileUser.setValue(subject);
        signFile(0);
    }

    /**
     * Подпись файла
     * @param index – индекс
     */
    private void signFile(int index) {
        SimpleSignModel signModel = signModels.get(index);
        byte[] content = signModel.getPdfSignFile().generateContent();
        String res = new String(Base64.encodeBase64(content));

        //Подпись файла
        Clients.evalJavaScript("signFile('" + res + "', '" + certNumber + "', '" + index + "')");
    }

    /**
     * Завершение подписи одного файла
     */
    @Listen("onSignFile = #winSignFiles")
    public void finishSignFile(Event event) {
        JSONObject jsonData = new JSONObject(event.getData().toString());
        int index = Integer.valueOf(jsonData.getString("Index"));
        //String baseSign = jsonData.getString("Data");
        //Запись подписанного файла
        //byte[] decodeFile = Base64.decodeBase64(baseSign);
        SimpleSignModel signModel = signModels.get(index);
        //Пытаемся обновить файлы и записать в БД
        if (signModel.getSignService().createFileAndUpdateUI(signModel.getPdfSignFile().generateContent(), certNumber, serialNumber)) {
            Integer percent = (index + 1) * 100 / signModels.size();
            progressSignFile.setValue(percent);
            lSignFileInfo.setValue((index + 1) + " из " + signModels.size() + " файлов подписано");
        } else {
            log.error("Error: can't sign file");
            PopupUtil.showError("Не удалось подписать файл. Обратитесь к администратору!");
            return;
        }
        //Проверяем, есть ли ещё файлы
        if (index < (signModels.size() - 1)) {
            signFile(++index);
        } else {
            finishAllSign();
        }
    }

    /**
     * Завершение подписания
     */
    private void finishAllSign() {
        progressSignFile.setValue(100);
        PopupUtil.showInfo("Все файлы подписаны");
        getSelf().detach();
    }

    @Listen("onErrorSign = #winSignFiles")
    public void errorSign(Event ev) {
        DialogUtil.error(ev.getData().toString());
        getSelf().detach();
    }
}
