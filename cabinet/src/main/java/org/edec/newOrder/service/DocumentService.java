package org.edec.newOrder.service;

import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.enums.DocumentEnum;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.enums.ParamEnum;
import org.edec.newOrder.model.report.ProtocolComissionerModel;
import org.edec.newOrder.report.ReportService;
import org.edec.studentPassport.service.impl.StudentPassportServiceESOimpl;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.service.poi.WordService;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DocumentService {
    private FileManager manager = new FileManager();
    private ReportService reportService = new ReportService();
    private StudentPassportServiceESOimpl studentPassportServiceESOimpl = new StudentPassportServiceESOimpl();
    private WordService wordService = new WordService();

    public void generateDocument (DocumentEnum documentEnum, OrderEditModel order, HashMap<ParamEnum, Object> params) {
        // TODO
        JasperReport document = null;
        ByteArrayOutputStream baosDocument;

        switch (documentEnum) {
            case SET_ELIMINATION_NOTION:
                document = reportService.getJasperReportForServiceNote(order, (Date) params.get(ParamEnum.DATE_FROM),
                                                                       (String) params.get(ParamEnum.SEMESTER)
                );
                break;
            case MATERIAL_SUPPORT:
                document = reportService.getJasperReportForMaterialSupportProtocol(order, (Date) params.get(ParamEnum.DATE_BEGIN),
                        (Date) params.get(ParamEnum.DATE_END),(Date) params.get(ParamEnum.DATE_FROM),
                        (String) params.get(ParamEnum.NUMBER_PROTOCOL), (List<ProtocolComissionerModel>) params.get(ParamEnum.LIST_STUDENT),
                        (List<OrderCreateStudentModel>) params.get(ParamEnum.LIST_REFUSAL)
                );
                break;
            case STUDENT_INDEX_CARD:
                baosDocument = studentPassportServiceESOimpl.generateIndexCard((Long)params.get(ParamEnum.ID_STUDENTCARD_MINE), (String)params.get(ParamEnum.GROUP_NAME));
                if (baosDocument != null) {
                    manager.createAttachForOrderUrl(order, baosDocument.toByteArray(), (String) params.get(ParamEnum.DOCUMENT_NAME));
                }
                break;
            case NOTION_RECTOR:
                manager.createAttachForOrderUrl(order, wordService.getDocx(reportService.getParamsFromNotionRector(order)), (String) params.get(ParamEnum.DOCUMENT_NAME));
                return;
        }

        if (document == null) {
            return;
        }

        manager.createAttachForOrderUrl(order, document.getFile(), (String) params.get(ParamEnum.DOCUMENT_NAME));
    }
}
