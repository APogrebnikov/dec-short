package org.edec.utility.report.service.poi;

import org.apache.poi.util.IOUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.report.OrderReportMainModel;
import org.edec.newOrder.report.service.OrderReportFillService;
import org.edec.utility.doc.service.DocService;
import org.zkoss.zul.Filedownload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordService {

    private final DocService docService = new DocService();

    public void downloadDocx(Long idOrderRule, Long idOrder){
        Map<String, Object> orderData = new OrderReportFillService().getMainOrderMap(idOrder);
        OrderReportMainModel orderReportMainModel = (OrderReportMainModel)orderData.get(OrderReportFillService.MAIN_ORDER);

        Map<String, Object> data = new HashMap<>();
        data.put("institute", orderReportMainModel.getInstitute().toUpperCase());
        data.put("formofstudy", orderReportMainModel.getFormofstudy().toUpperCase());
        data.put("predicate_fio", orderData.get(OrderReportFillService.PREDICATE_FIO));
        data.put("predicate_post", orderData.get(OrderReportFillService.PREDICATE_POST));
        data.put("employees", orderData.get(OrderReportFillService.EMPLOYEES));
        data.put("executor_fio", orderReportMainModel.getExecutorfio());
        data.put("executor_phone", orderReportMainModel.getExecutortel());
        data.put("content", docService.parseOrderLine(idOrder));
        data.put("type_order", orderReportMainModel.getTypeorder());

        XWPFDocument template = docService.getOrderTemplate(idOrderRule);

        template = docService.getDoc(template, data);

        try {
            File file = new File("Приказ.docx");

            FileOutputStream outFile = new FileOutputStream(file);
            template.write(outFile);
            outFile.close();
            Filedownload.save(file, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getDocx(Map<String, Object> data) {
        XWPFDocument template = docService.getNotionRectorTemplate();
        template = docService.getNotion(template, data);

        try {
            File file = new File("Служебная записка.docx");
            FileOutputStream outFile = new FileOutputStream(file);
            template.write(outFile);
            outFile.close();
           // Filedownload.save(file, null);

            return IOUtils.toByteArray(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
