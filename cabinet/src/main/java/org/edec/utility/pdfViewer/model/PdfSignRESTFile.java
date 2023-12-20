package org.edec.utility.pdfViewer.model;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.edec.utility.httpclient.manager.ReportHttpClient;
import org.edec.utility.httpclient.manager.ReportHttpService;
import org.json.JSONObject;

@AllArgsConstructor
public class PdfSignRESTFile implements PdfSignFile {
    String fileName, typeReport;
    JSONObject jsonData;

    @Override
    public byte[] generateContent() {
        return new ReportHttpService().getReport(fileName, typeReport, jsonData).toByteArray();
    }
}
