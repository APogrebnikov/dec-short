package org.edec.utility.pdfViewer.model;

import org.edec.utility.fileManager.FileManager;
import org.edec.utility.pdfViewer.ctrl.PdfViewerCtrl;
import org.edec.utility.pdfViewer.ctrl.PdfViewerDocumentsCtrl;
import org.edec.utility.sign.service.SignService;
import org.edec.utility.zk.ComponentHelper;
import org.edec.workflow.model.WorkflowModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PdfViewer {
    private FileManager fileManager = new FileManager();

    private ByteArrayOutputStream baos;
    private String fileName;
    private String relativePath;
    private SignService signService;
    private WorkflowModel workflowModel;
    private String[] relativePaths;

    public PdfViewer(ByteArrayOutputStream baos, String fileName) {
        this.baos = baos;
        this.fileName = fileName;
    }

    public PdfViewer(SignService signService, ByteArrayOutputStream baos, String fileName) {
        this.signService = signService;
        this.baos = baos;
        this.fileName = fileName;
    }


    public PdfViewer (String relativePath) {
        this.relativePath = relativePath;
    }

    public PdfViewer (WorkflowModel workflowModel) {
        this.workflowModel = workflowModel;
    }

    //для паспорта групп
    public PdfViewer (String[] relativePaths) {
        this.relativePaths = relativePaths;
    }

    public void showPdf () {
        Map<String, Object> arg = new HashMap<>();
        if (relativePath != null) {
            arg.put(PdfViewerCtrl.FILE, fileManager.getFileByRelativePathWithReplacing(relativePath));
        }
        if (baos != null) {
            arg.put(PdfViewerCtrl.CONTENT, baos);
            arg.put(PdfViewerCtrl.FILE_NAME, fileName);
        }
        arg.put(PdfViewerCtrl.SIGN_SERVICE, signService);
        ComponentHelper.createWindow("/utility/pdfViewer/index.zul", "winPdfViewer", arg).doModal();
    }

    public void showRegisters () {
        Map<String, Object> arg = new HashMap<>();

        File[] files = new File[relativePaths.length];
        for (int i = 0; i < files.length; ++i) {
            files[i] = fileManager.getFileByRelativePath(relativePaths[i]);
        }

        arg.put(PdfViewerDocumentsCtrl.DOCUMENTS, files);
        ComponentHelper.createWindow("/utility/pdfViewer/documents.zul", "winPdfViewer", arg).doModal();
    }

    public void showDirectory () {
        Map<String, Object> arg = new HashMap<>();
        if (relativePath != null) {
            arg.put(PdfViewerDocumentsCtrl.DOCUMENTS, fileManager.getFilesByRelativePath(relativePath));
        } else if (workflowModel != null) {
            arg.put(PdfViewerDocumentsCtrl.WORKFLOW_MODEL, workflowModel);
            String regular = "((\\\\)|(/))(order)*(\\d)*(\\u002E)(pdf)";
            workflowModel.setPathFile(workflowModel.getPathFile().toLowerCase());
            if (workflowModel.getPathFile().endsWith(".pdf")) {
                workflowModel.setPathFile(workflowModel.getPathFile().replaceAll(regular, ""));
            }
            arg.put(PdfViewerDocumentsCtrl.DOCUMENTS, fileManager.getFilesByFullPath(workflowModel.getPathFile()));
        }

        ComponentHelper.createWindow("/utility/pdfViewer/documents.zul", "winPdfViewer", arg).doModal();
    }
}
