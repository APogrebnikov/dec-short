package org.edec.teacher.ctrl;

import javafx.stage.FileChooser;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.teacher.model.DigitalSignatureModel;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;

import java.io.*;
import java.nio.charset.Charset;


public class WinChooseDigitalBlankCtrl extends SelectorComposer<Component> {
    @Wire
    private Groupbox gbNewSignature, gbProlongationSignature, gbRestoreSignature;
    @Wire
    private Button btnRequestNew, btnRequestAccess, btnRequestNewAfterLoss, btnRequestAccessAfterLoss, btnRequestGetBackAfterLoss;
    @Wire
    private Button btnActOfReturnRutokenProlongation, btnRequestForFabricationKeyProlongation;
    @Wire
    private Button btnRequestRecallKeyAfterLoss, btnRequestForFabricationKeyLoss;
    @Wire
    private Button btnDownloadGuide;


    UserModel currentUser = (UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER);

    DigitalSignatureModel model = new DigitalSignatureModel();


    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        model.setFio(currentUser.getFio());
        model.setShortFio(currentUser.getShortFIO());
    }


    @Listen("onClick = #btnRequestNew; onClick = #btnRequestNewAfterLoss")
    public void downloadRequest() throws IOException {
        //downloadBlank(new File("C:/FileServer/blanksForDigitalSignature/Заявление о согласии на использование цп.doc"));
        // Новое Заявление №1
        downloadBlank(new File("C:/FileServer/blanksForDigitalSignature/Заявление №1 - О создании ключей ЭП.docx"));
    }
    @Listen("onClick = #btnRequestAccess; onClick = #btnRequestAccessAfterLoss")
    public void downloadAccessRequest() throws IOException {
        //downloadBlank(new File("C:/FileServer/blanksForDigitalSignature/Заявление о согласии на использование цп.doc"));
        // Новое Заявление №2
        downloadBlank(new File("C:/FileServer/blanksForDigitalSignature/Заявление №2 - О согласии использования ЭП.docx"));
    }
    @Listen("onClick = #btnRequestGetBackAfterLoss;")
    public void downloadGetBackRequest() throws IOException {
        //downloadBlank(new File("C:/FileServer/blanksForDigitalSignature/Заявление о согласии на использование цп.doc"));
        // Новое Заявление №3
        downloadBlank(new File("C:/FileServer/blanksForDigitalSignature/Заявление №3 - Об отзыве ЭП.docx"));
    }

    @Listen("onClick = #btnRequestForFabricationKeyNew; #btnRequestForFabricationKeyProlongation; #btnRequestForFabricationKeyLoss;")
    public void downloadRequestForFabricationKey() throws IOException {
        downloadBlank(new File("C:/FileServer/blanksForDigitalSignature/Заявление о создании ключей.doc"));
    }

    @Listen("onClick = #btnActOfReturnRutokenProlongation;")
    public void downloadActOfReturnRutoken() throws IOException {
        downloadBlank(new File("C:/FileServer/blanksForDigitalSignature/Заявление о смнене ключей цифровой подписи.doc"));
    }

    @Listen("onClick = #btnRequestRecallKeyAfterLoss;")
    public void downloadRequestRecallKey() throws IOException {
        downloadBlank(new File("C:/FileServer/blanksForDigitalSignature/Заявление об отзыве сертификата.doc"));
    }

    public void downloadBlank(File blankFile) throws IOException {
        try {
            Filedownload.save(blankFile, "application/doc");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Listen("onClick = #btnDownloadGuide")
    public void downloadGuide() throws FileNotFoundException {
        String path = "C:/FileServer/blanksForDigitalSignature/Руководство 2.0.pdf";
        String contentType = "application/pdf";
        Filedownload.save(new File(path), contentType);
    }
}
