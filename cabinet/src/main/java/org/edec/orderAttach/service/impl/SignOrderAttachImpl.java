package org.edec.orderAttach.service.impl;

import org.edec.commons.model.OrderAttachModel;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.orderAttach.manager.OrderAttachDAO;
import org.edec.orderAttach.model.OrderWithAttachModel;
import org.edec.teacher.model.register.RegisterModel;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.httpclient.manager.ReportHttpClient;
import org.edec.utility.httpclient.manager.ReportHttpService;
import org.edec.utility.sign.service.SignService;
import org.json.JSONObject;
import org.zkoss.zul.Listitem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;

public class SignOrderAttachImpl implements SignService {
    private FileManager fileManager = new FileManager();
    private OrderAttachDAO orderAttachDAO = new OrderAttachDAO();
    private ReportHttpService reportHttpService = new ReportHttpService();
    private TemplatePageCtrl template = new TemplatePageCtrl();

    private OrderAttachModel orderAttach;
    private OrderWithAttachModel order;

    public SignOrderAttachImpl(OrderAttachModel orderAttach, OrderWithAttachModel order) {
        this.orderAttach = orderAttach;
        this.order = order;
    }

    @Override
    public RegisterModel getRegisterModel() {
        return null;
    }

    @Override
    public boolean hasSignRegister() {
        return false;
    }

    @Override
    public boolean createFileAndUpdateUI(byte[] bytesFile, String certnumber, String thumbPrint) {
        if (bytesFile == null || bytesFile.length == 0) {
            return false;
        }
        String orderUrl = orderAttach.getOrder().getUrl();
        String fileName = orderAttach.getJSONData().getString(ReportHttpClient.FILE_NAME);
        String pathAttachDirectory = orderUrl + File.separator + "attach";
        String pathSignAttachDirectory = orderUrl + File.separator + "signed" + File.separator + "attach";
        String fio = template.getCurrentUser().getFio();
        try {
            if (orderAttachDAO.checkOnSignAttachFile(orderAttach.getIdOrderAttach())) {
                throw new IllegalArgumentException("OrderAttach файл уже подписан");
            }
            if (fileManager.checkOnExistsFile(pathSignAttachDirectory + File.separator + fileName)) {
                throw new FileAlreadyExistsException("Файл (" + pathSignAttachDirectory +  File.separator + fileName + ") уже добавлен");
            }
            if (fileManager.checkOnExistsFile(pathAttachDirectory + File.separator + fileName)) {
                throw new FileAlreadyExistsException("Файл (" + pathAttachDirectory + ") уже добавлен");
            }
            ByteArrayOutputStream report = reportHttpService.getReport(fileName, orderAttach.getTypeReport(),
                    new JSONObject(orderAttach.getJSONData().getString(ReportHttpClient.DATA)), certnumber, fio);
            if (!fileManager.createFileAndDirectories(pathSignAttachDirectory, fileName, bytesFile) ||
                    !fileManager.createFileAndDirectories(pathAttachDirectory, fileName, report.toByteArray())) {
                throw new FileSystemException("Не удалось создать файл");
            }
            if (orderAttachDAO.updateOrderAttachAfterSign(certnumber, fio, fileName, orderAttach.getIdOrderAttach())) {
                orderAttach.setCertNumber(certnumber);
                orderAttach.setFileName(fileName);
                orderAttach.setCertFio(fio);
                order.checkStatus();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
