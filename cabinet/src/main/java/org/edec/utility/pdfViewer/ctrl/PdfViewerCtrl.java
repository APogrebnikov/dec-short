package org.edec.utility.pdfViewer.ctrl;

import org.apache.commons.codec.binary.Base64;
import org.edec.utility.sign.service.SignService;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.json.JSONObject;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

import java.io.*;

public class PdfViewerCtrl extends SelectorComposer<Window> {
    public static final String CONTENT = "content";
    public static final String FILE = "file";
    public static final String FILE_NAME = "fileName";
    public static final String SIGN_SERVICE = "sign_service";

    @Wire
    private Hbox hbBtn;

    @Wire
    private Iframe iframePdfViwer;

    @Wire
    private Window winPdfViewer;

    private SignService signService;

    private byte[] buffer = null;
    private File file;
    private ByteArrayOutputStream baos;
    private String fileName;

    @Override
    public void doAfterCompose (Window win) throws Exception {
        super.doAfterCompose(win);
        file = (File) Executions.getCurrent().getArg().get(FILE);
        baos = (ByteArrayOutputStream) Executions.getCurrent().getArg().get(CONTENT);
        fileName = (String) Executions.getCurrent().getArg().get(FILE_NAME);
        signService = (SignService) Executions.getCurrent().getArg().get(SIGN_SERVICE);

        //Для возможности добавить кнопку подписать
        if (signService != null) {
            Button btnSign = new Button("Подписать");
            btnSign.setStyle("font-size: 12pt;");
            btnSign.setTooltiptext("Подписать документ, используя LSS");
            btnSign.setId("btnSignFile");
            btnSign.setParent(hbBtn);
        }

        showFile();
    }

    private void showFile () {
        AMedia amedia;
        try {
            if (file != null) {
                buffer = new byte[(int) file.length()];
                FileInputStream fs = new FileInputStream(file);
                fs.read(buffer);
                fs.close();
                ByteArrayInputStream is = new ByteArrayInputStream(buffer);
                amedia = new AMedia("file", "pdf", "application/pdf", is);
            } else {
                buffer = baos.toByteArray();
                amedia = new AMedia(fileName == null ? "file" : fileName, "pdf", "application/pdf", buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
            PopupUtil.showError("Проблемы с отображением документа, обратитесь к администраторам!");
            winPdfViewer.detach();
            return;
        }
        iframePdfViwer.setContent(amedia);
        iframePdfViwer.setVflex("1");
        iframePdfViwer.setHflex("1");
    }

    @Listen("onClick = #btnSignFile")
    public void signFile() {
        Clients.showBusy("Процесс подписания...");
        //Считываем PDF из фрэйма
        if (buffer == null) {
            buffer = iframePdfViwer.getContent().getByteData();
        }

        /*String res = new String(new Base64().encode(buffer));
        Clients.evalJavaScript("sign('" + res + "','" + fileName + "')");*/
        finishSignTest();
    }

    /**
     * Заглушка для получение обратной связи от JS из окно winReport
     */
    @Listen("onFinish = #winReport")
    public void finishSign(Event ev) {
        Clients.clearBusy();

        Clients.showBusy("Занесение ведомости в базу данных...");
        JSONObject evData = (JSONObject) ev.getData();
        String baseSign = (String) evData.get("Data");
        String serialNumber = (String) evData.get("SerialNumber");
        String thumbPrint = (String) evData.get("Thumbprint");
        byte[] deArr = Base64.decodeBase64(baseSign);

        if (!signService.createFileAndUpdateUI(deArr, serialNumber, thumbPrint)) {
            PopupUtil.showError("Не удалось подписать файл! Обратитесь к администратору");
        } else {
            PopupUtil.showInfo("Файл успешно подписан!");
            hbBtn.setVisible(false);
        }

        Clients.clearBusy();
    }

    public void finishSignTest() {
        String serialNumber = "88005553535";
        String thumbPrint = "12345";
        byte[] deArr = iframePdfViwer.getContent().getByteData();

        if (!signService.createFileAndUpdateUI(deArr, serialNumber, thumbPrint))
            PopupUtil.showError("Не удалось подписать файл! Обратитесь к администратору");
        else
        {
            PopupUtil.showInfo("Файл успешно подписан!");
            hbBtn.setVisible(false);
        }
        getSelf().detach();
        Clients.clearBusy();
    }

    /**
     * Функция ожидающая провал подписи
     */
    @Listen("onErrorSign = #winReport")
    public void errorSign(Event ev) {
        Clients.clearBusy();
        DialogUtil.error(ev.getData().toString());
    }
}
