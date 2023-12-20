package org.edec.utility.httpclient.manager;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@UtilityClass
public class ReportHttpClient {
    public static final String CERT_FIO = "certfio";
    public static final String CERT_NUMBER = "certnumber";
    public static final String FILE_NAME = "fileName";
    public static final String TYPE_REPORT = "typeReport";
    public static final String DATA = "data";


    public static ByteArrayOutputStream getReport(@NonNull String url, @NonNull String reportType, @NonNull String fileName, JSONObject jsonData, String certnumber, String certfio) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JSONObject params = new JSONObject();
        params.put(FILE_NAME, fileName)
                .put(CERT_FIO, certfio)
                .put(CERT_NUMBER, certnumber)
                .put(TYPE_REPORT, reportType)
                .put(DATA, jsonData.toString());
        try {
            StringEntity entity = new StringEntity(params.toString(), "utf-8");
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", MediaType.APPLICATION_JSON + "; charset=utf8");
            httpPost.setHeader("Content-type", MediaType.APPLICATION_JSON + "; charset=utf8");
            CloseableHttpResponse response = client.execute(httpPost);
            response.getEntity().writeTo(baos);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos;
    }

    public static ByteArrayOutputStream getReport(@NonNull String url, @NonNull String reportType, @NonNull String fileName, JSONObject jsonData) {
        return getReport(url, reportType, fileName, jsonData, null, null);
    }
}
