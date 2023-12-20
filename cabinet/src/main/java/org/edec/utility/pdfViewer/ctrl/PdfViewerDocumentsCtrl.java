package org.edec.utility.pdfViewer.ctrl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.newOrder.report.ReportService;
import org.edec.utility.pdfViewer.model.SignVis;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.edec.workflow.ctrl.IndexPageCtrl;
import org.edec.workflow.model.WorkflowModel;
import org.edec.workflow.service.WorkflowService;
import org.edec.workflow.service.impl.WorkflowServiceEnsembleImpl;
import org.zkoss.json.JSONObject;
import org.zkoss.util.media.AMedia;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.*;
import java.util.*;
import java.util.List;


public class PdfViewerDocumentsCtrl extends SelectorComposer<Component> {
    public static final Logger log = Logger.getLogger(PdfViewerDocumentsCtrl.class.getName());

    public static final String DOCUMENTS = "documents";
    public static final String WORKFLOW_MODEL = "workflow_model";

    @Wire
    private Iframe iframePdfViwer;

    @Wire
    private Listbox lbDocSign, lbAttach, lbCanceled;

    @Wire
    private Vbox vbDocs, vbContent;

    @Wire
    private Window winPdfViewer;

    private File currentFile;
    private File accessSignFile;
    private File[] documents;
    private UserModel currentUser;
    private WorkflowModel workflowModel;

    private Hbox hbContent;
    private Button btnSignAccess;

    private WorkflowService workflowService = new WorkflowServiceEnsembleImpl();
    private ReportService jasperReportService = new ReportService();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        documents = (File[]) Executions.getCurrent().getArg().get(DOCUMENTS);
        workflowModel = (WorkflowModel) Executions.getCurrent().getArg().get(WORKFLOW_MODEL);
        currentUser = new TemplatePageCtrl().getCurrentUser();
        fillListboxes();
        if (workflowModel != null) {
            fillFowWorkflow();
        }
        if (lbDocSign.getItems().size() > 0) {
            Events.echoEvent(Events.ON_CLICK, lbDocSign.getItems().get(0), null);
        }
    }

    private void fillCheckContent() {

        hbContent = new Hbox();
        hbContent.setParent(vbContent);
        hbContent.setHflex("1");
        hbContent.setAlign("center");
        hbContent.setPack("right");

        if(accessSignFile != null) {
            btnSignAccess = new Button("Проверить ЭП согласующих");
            btnSignAccess.setTooltip("Проверить подписи согласующих");
            btnSignAccess.setParent(hbContent);
            btnSignAccess.setStyle("color: #000; border: solid 1px #ff7e00; font-size: 18px; font-weight: 600;");
            btnSignAccess.addEventListener(Events.ON_CLICK, event -> {
                checkPdf(accessSignFile);
            });
        }

        Button btnSign = new Button("Проверить подписи");
        btnSign.setParent(hbContent);
        btnSign.setStyle("color: #000; border: solid 1px #ff7e00; font-size: 18px; font-weight: 600;");
        btnSign.addEventListener(Events.ON_CLICK, event -> {
            checkPdf(currentFile);
        });

    }

    private void fillContent() {
        hbContent = new Hbox();
        hbContent.setParent(vbContent);
        hbContent.setHflex("1");
        hbContent.setAlign("center");
        hbContent.setPack("right");

        if (accessSignFile != null) {
            btnSignAccess = new Button("Проверить ЭП согласующих");
            btnSignAccess.setTooltip("Проверить подписи согласующих");
            btnSignAccess.setParent(hbContent);
            btnSignAccess.setStyle("color: #000; border: solid 1px #ff7e00; font-size: 18px; font-weight: 600;");
            btnSignAccess.addEventListener(Events.ON_CLICK, event -> {
                checkPdf(accessSignFile);
            });
        }

        Button btnSign = new Button("Подписать");
        btnSign.setParent(hbContent);
        btnSign.setIconSclass("z-icon-check");
        btnSign.setStyle("color: #000; border: solid 1px #ff7e00; font-size: 18px; font-weight: 600;");
        btnSign.addEventListener(Events.ON_CLICK, event -> {
            if (Objects.equals(workflowModel.getSignReq(), WorkflowModel.ACTION_PREVIEW_AND_UNSIGN_AND_SIGN) ||
                    Objects.equals(workflowModel.getSignReq(), WorkflowModel.ACTION_PREVIEW_AND_UNSIGN_AND_SIGN_FINAL)) {
                Messagebox.show("Вы действительно желаете подписать документ?", "Внимание!", Messagebox.YES | Messagebox.NO,
                        Messagebox.QUESTION, event1 -> {
                            if (event1.getName().equals(Messagebox.ON_YES)) {
                                signPdf();
                            }
                        }
                );
            } else {
                Messagebox.show("Вы действительно желаете подтвердить?", "Внимание!", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
                        event12 -> {
                            if (event12.getName().equals(Messagebox.ON_YES)) {
                                confirm();
                            }
                        }
                );
            }
        });

        Button btnCancel = new Button("Отправить на доработку");
        btnCancel.setParent(hbContent);
        btnCancel.setIconSclass("z-icon-ban");
        btnCancel.setStyle("color: #000; border: solid 1px #000; font-size: 18px;");
        btnCancel.addEventListener(Events.ON_CLICK, event -> {
            Map arg = new HashMap();
            arg.put(ViewCancelDialogPageCtrl.WORKFLOW_MODEL, workflowModel);
            arg.put(ViewCancelDialogPageCtrl.WIN_PDFVIEWER, winPdfViewer);
            ComponentHelper.createWindow("/utility/pdfViewer/winCancelDialog.zul", "winCancelDialog", arg).doModal();
        });
    }

    /**
     * Метод подписания.
     */
    public void signPdf() {
        String filePath = currentFile.getAbsolutePath();
        Clients.showBusy("Процесс подписания...");
        File pdf = new File(filePath);
        try {
            FileInputStream fis = new FileInputStream(pdf);
            byte[] buffer = IOUtils.toByteArray(fis);
            Base64 base64 = new Base64();
            String res = new String(base64.encode(buffer));
            String pdfname = workflowModel.getSubject();
            Clients.evalJavaScript("sign('" + res + "','" + pdfname + "')");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(currentUser.getFio() + ", ID task: " + workflowModel.getTaskId(), e);
        }
    }

    /**
     * Метод проверки подписи через API vipnet
     */
    public void checkPdf(File file) {
        String filePath = file.getAbsolutePath();
        File pdf = new File(filePath);
        try {
            FileInputStream fis = new FileInputStream(pdf);
            byte[] buffer = IOUtils.toByteArray(fis);
            Base64 base64 = new Base64();
            String res = new String(base64.encode(buffer));
            String pdfname = currentFile.getName();
            Clients.evalJavaScript("verifySign('" + res + "','" + pdfname + "')");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(currentUser.getFio() + ", ID task: " + workflowModel.getTaskId(), e);
        }
    }

    /**
     * Метод подтверждения.
     */
    private void confirm() {
        try {
            workflowModel.setAction(WorkflowModel.STATUS_SIGNED_EN);
            workflowModel.setStatus("Подтверждена");
            if (workflowService.updateOrderTask(workflowModel) != null) {
                IndexPageCtrl pageCtrl = (IndexPageCtrl) Executions.getCurrent().getSession().getAttribute("workflow_index_page");
                pageCtrl.refreshTaskList();
                Messagebox.show("Подтверждено.", "Готово!", Messagebox.OK, Messagebox.INFORMATION, event -> {
                    if (event.getName().equals(Messagebox.ON_OK)) {
                        winPdfViewer.detach();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(currentUser.getFio() + ", ID task: " + workflowModel.getTaskId(), e);
        }
    }

    @Listen("onFinish = #winPdfViewer;")
    public void signPdfFinish(Event ev) {
        Clients.clearBusy();
        FileInputStream fis = null;
        try {
            JSONObject evData = (JSONObject) ev.getData();
            String baseSign = (String) evData.get("Data");
            String serialNumber = (String) evData.get("SerialNumber");
            //String certEnd = (String) evData.get("NotAfter");
            //String certBegin = (String) evData.get("NoteBefore");
            if (StringUtils.isEmpty(serialNumber)) {
                System.out.println("Serial number пустой!" + workflowModel.getOrderId() + ", у подписавшего " + workflowModel.getFio());
                DialogUtil.error("Не удалось получить сертифкат. Необходимо повторить процедуру подписания.", "Ошибка при подписании");
                return;
            }
            byte deArr[] = Base64.decodeBase64(baseSign);
            if (deArr.length == 0) {
                System.out.println("JavaScript error. Byte array not load.");
                Messagebox.show("Не получилось подписать. Обратитесь к администратору.", "Ошибка!", Messagebox.OK, Messagebox.ERROR);
            } else {
                String pathFile = currentFile.getAbsolutePath();
                FileOutputStream fos = new FileOutputStream(new File(pathFile));
                fos.write(deArr);
                fos.close();
                File file = new File(pathFile);
                if (!file.exists()) {
                    Messagebox.show("Не получилось подписать документ. Обратитесь к администратору.", "Ошибка!", Messagebox.OK,
                                    Messagebox.ERROR
                    );
                } else {
                    workflowModel.setAction(WorkflowModel.STATUS_SIGNED_EN);
                    workflowModel.setStatus("Подписана");
                    workflowModel.setCertNumber(serialNumber);
                    String status = workflowService.updateOrderTask(workflowModel);
                    if (status == null || status.equals("Error")) {
                        System.out.println("Error update data of task ID" + workflowModel.getTaskId() + ". Data respons not received.");
                        log.error("Error update data of task ID" + workflowModel.getTaskId() + ". Data respons not received.");
                        Messagebox.show("Ошибка обновления статуса задачи. Обратитесь к адмиинистратору.", "Ошибка!", Messagebox.OK,
                                        Messagebox.ERROR
                        );
                        return;
                    } else {
                        PopupUtil.showInfo("Документ успешно подписан.");
                    }
                    IndexPageCtrl pageCtrl = (IndexPageCtrl) Executions.getCurrent().getSession().getAttribute("workflow_index_page");
                    pageCtrl.refreshTaskList();
                    winPdfViewer.detach();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error(currentUser.getFio() + ", ID task: " + workflowModel.getTaskId(), e);
            Messagebox.show("Не удалось сохранить подписанный файл. Обратитесь к администратору.", "Ошибка!", Messagebox.OK,
                            Messagebox.ERROR
            );
        } catch (Exception e) {
            e.printStackTrace();
            log.error(currentUser.getFio() + ", ID task: " + workflowModel.getTaskId(), e);
            Messagebox.show("Неизвестная ошибка. Обратитесь к администратору.", "Ошибка!", Messagebox.OK, Messagebox.ERROR);
        }
    }

    @Listen("onErrorSign = #winPdfViewer")
    public void errorSign(Event ev) {
        Clients.clearBusy();
        DialogUtil.error(ev.getData().toString());
    }

    private void showFile(File file, Boolean sign) {
        boolean showSignVis=false;
        List<SignVis> signs = new ArrayList<>();

        while (vbContent.getChildren().size() > 1) {
            vbContent.getChildren().remove(1);
        }
        if (sign && workflowModel != null &&
                (workflowModel.getStatusTask() == null || !workflowModel.getStatusTask().equals("Подтверждена"))
                && !new TemplatePageCtrl().getCurrentModule().isReadonly()) {
            fillContent();
        } else {
            if(sign || file.getAbsoluteFile().toString().contains("signed")){
                fillCheckContent();
            }
            if(file.getAbsoluteFile().toString().contains("signed")){
                List<WorkflowModel> wflist = workflowService.getArchiveTasksConfirmingByIdBP(Long.valueOf(workflowModel.getSessionId()));
                for (WorkflowModel model : wflist) {
                    if(model.getCertNumber()!=null && !model.getCertNumber().equals("") && model.getFio()!=null && !model.getFio().contains("Гуц ")){
                        boolean find = false;
                        for (SignVis signVis : signs) {
                            if(signVis.getFio().equals(model.getFio())){
                                signVis.setFio(model.getFio());
                                signVis.setSign(model.getCertNumber());
                                signVis.setDate(model.getTimeCreated());
                                find = true;
                            }
                        }
                        if (!find) {
                            SignVis signObj = new SignVis();
                            signObj.setFio(model.getFio());
                            signObj.setSign(model.getCertNumber());
                            signObj.setDate(model.getTimeCreated());
                            signs.add(signObj);
                        }
                    }
                }
                showSignVis = true;
                try {
                    generateSignedVisualization(signs);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        }
        if(showSignVis){
            try {
                file = generateSignedVisualization(signs);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            if (btnSignAccess!=null) {
                btnSignAccess.setVisible(false);
            }
        } else if (sign) {
            if (btnSignAccess!=null) {
                btnSignAccess.setVisible(true);
            }
        }

        AMedia amedia = null;
        try {
            byte[] buffer = new byte[(int) file.length()];
            FileInputStream fs = new FileInputStream(file);
            fs.read(buffer);
            fs.close();
            // Подмена буфера, если требуется визуализация из приказа
            if (showSignVis) {
                buffer = generateAlternativeSignView(workflowModel.getOrderId());
                if (buffer == null) {
                    org.zkoss.zhtml.Messagebox.show("Ошибка при загрузке документов, обратитесь к администратору");
                    return;
                }
            }
            ByteArrayInputStream is = new ByteArrayInputStream(buffer);
            amedia = new AMedia("file", "pdf", "application/pdf", is);
        } catch (IOException e) {
            e.printStackTrace();
            org.zkoss.zhtml.Messagebox.show("Проблемы с отображением документа, обратитесь к администраторам!");
        }
        iframePdfViwer.setContent(amedia);
        iframePdfViwer.setVflex("1");
        iframePdfViwer.setHflex("1");


    }

    private void fillListboxes() {
        if(documents != null) {
            for (File file : documents) {
                if (file.isFile()) {
                    addListcell(lbDocSign, file.getName(), file, true);
                } else {
                    if (file.getName().equals("attach")) {
                        for (File fileAttach : file.listFiles()) {
                            addListcell(lbAttach, fileAttach.getName(), fileAttach, false);
                        }
                    } else if (file.getName().equals("canceled")) {
                        for (File fileCanceled : file.listFiles()) {
                            addListcell(lbCanceled, fileCanceled.getName(), fileCanceled, false);
                        }
                    } else if (file.getName().equals("signed")) {
                        for (File fileSigned : file.listFiles()) {
                            Listitem li = addListcell(lbAttach, fileSigned.getName(), fileSigned, false);
                            if (lbAttach.getItems().size() > 1) {
                                lbAttach.insertBefore(li, lbAttach.getFirstChild());
                            }
                            accessSignFile = fileSigned;
                        }
                    }
                }
            }
        } else {
            org.zkoss.zhtml.Messagebox.show("Ошибка при загрузке документов, обратитесь к администратору");
        }
    }

    private void fillFowWorkflow() {
        Hbox hboxTitle = new Hbox();
        hboxTitle.setParent(vbDocs);
        hboxTitle.setHflex("1");
        hboxTitle.setAlign("center");
        hboxTitle.setStyle("background: #5f8a96; border-bottom: none; height: 30px;");
        Span spanTitle = new Span();
        spanTitle.setParent(hboxTitle);
        spanTitle.setClass("z-icon-info-circle");
        spanTitle.setStyle("margin-left: 5px; font-size: 20px; color: #fff;");
        Label lTitle = new Label("Информация");
        lTitle.setParent(hboxTitle);
        lTitle.setStyle(
                "color: #fff; font-family: opensans,arial,freesans,sans-serif; font-weight: 700; font-size: 15px; font-style: normal;");

        Vbox vboxName = new Vbox();
        vboxName.setParent(vbDocs);
        Label lName = new Label("Название:");
        lName.setParent(vboxName);
        lName.setStyle("font-family: opensans,arial,freesans,sans-serif; font-weight: 700; font-size: 15px; font-style: normal;");
        Label lNameInfo = new Label(workflowModel.getSubject());
        lNameInfo.setParent(vboxName);
        lNameInfo.setStyle("font-family: opensans,arial,freesans,sans-serif; font-size: 15px; font-style: normal;");

        Vbox vboxType = new Vbox();
        vboxType.setParent(vbDocs);
        Label lType = new Label("Тип:");
        lType.setParent(vboxType);
        lType.setStyle("font-family: opensans,arial,freesans,sans-serif; font-weight: 700; font-size: 15px; font-style: normal;");
        Label lTypeInfo = new Label(workflowModel.getOrderType());
        lTypeInfo.setParent(vboxType);
        lTypeInfo.setStyle("font-family: opensans,arial,freesans,sans-serif; font-size: 15px; font-style: normal;");
    }

    private Listitem addListcell(Listbox listbox, String name, File file, boolean sign) {
        Listitem li = new Listitem(name);
        li.setParent(listbox);
        li.addEventListener(Events.ON_CLICK, getEvent(file, sign));

        if(file.getName().contains(".docx")){
            li.addEventListener(Events.ON_RIGHT_CLICK,getDownloadEvent(file));
        }

        return li;
    }

    private EventListener<Event> getDownloadEvent(final File file){
        return event -> {
            try {
                Filedownload.save(file, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        };
    }

    private EventListener<Event> getEvent(final File file, final Boolean sign) {
        return event -> {
            currentFile = file;
            showFile(file, sign);
        };
    }

    /**
     * Генерирует визуализированное представление электронной подписи на согласованном документе
     * используя технику Штампа
     * @param signs
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    private File generateSignedVisualization(List<SignVis> signs) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(currentFile.getAbsolutePath());
        //TODO: Подумать, что делать тут.
        String outPath="C:\\temp\\1234stomp.pdf";
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outPath));
        BaseFont bf = BaseFont.createFont("C:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        PdfContentByte canvas = stamper.getUnderContent(reader.getNumberOfPages());
        int i = 0 ;
        for (SignVis sign : signs) {
            Rectangle rect = new Rectangle(200, 30+(50*i), 400, 75+(50*i));
            rect.setBorder(Rectangle.BOX);
            rect.setBorderWidth(1);
            rect.setBackgroundColor(BaseColor.WHITE);
            rect.setBorderColor(BaseColor.BLACK);
            canvas.rectangle(rect);
            ColumnText ct = new ColumnText(canvas);
            ct.setSimpleColumn(rect);
            Font catFont = new Font(bf, 8, Font.NORMAL);

            ct.addElement(new Paragraph("        Документ подписан электронной подписью", catFont));
            Font catFont2 = new Font(bf, 6, Font.NORMAL);

            ct.addElement(new Paragraph("     Сертификат: "+sign.getSign(), catFont2));
            ct.addElement(new Paragraph("     Владелец: "+sign.getFio(), catFont2));
            ct.addElement(new Paragraph("     Дата подписи: "+sign.getDate(), catFont2));
            ct.addElement(new Paragraph(" ", catFont2));

            ct.go();
            i++;
        }

        stamper.close();
        return new File(outPath);
    }

    /**
     * Генерирует визуализированное представление электронной подписи на согласованном документе
     * используя технику скрытой подписи и заполнения из БД
     * @param idOrder
     * @return
     */
    private byte[] generateAlternativeSignView(Long idOrder){
        JasperReport jasperReport = jasperReportService.getJasperForOrder(idOrder);
        if (jasperReport != null) {
            byte[] bytes = jasperReport.getFile();
            return bytes;
        } else {
            return null;
        }
    }
}
