package org.edec.utility.httpclient.manager;

import lombok.NonNull;
import org.edec.utility.fileManager.FilePath;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ReportHttpService {
    private Properties properties;
    private String reportUrl;

    public ReportHttpService() {
        try {
            properties = new FilePath().getDataServiceProp();
        } catch (IOException e) {
            e.printStackTrace();
        }
        reportUrl = properties.getProperty("rest.jasper");
    }

    public ByteArrayOutputStream getReport(@NonNull String fileName, @NonNull String typeReport, JSONObject jsonData, String certnumber, String certfio) {
        return ReportHttpClient.getReport(reportUrl, typeReport, fileName, jsonData, certnumber, certfio);
    }

    public ByteArrayOutputStream getReport(@NonNull String fileName, @NonNull String typeReport, JSONObject jsonData) {
        return ReportHttpClient.getReport(reportUrl, typeReport, fileName, jsonData);
    }

    public ByteArrayOutputStream getStudentIndexCard(String fileName, @NonNull Long idStudentCard, @NonNull String groupName) {
        return ReportHttpClient.getReport(reportUrl, "Учетная карточка студента", fileName,
                new JSONObject().put("idStudentCard", idStudentCard).put("groupName", groupName));
    }
}
