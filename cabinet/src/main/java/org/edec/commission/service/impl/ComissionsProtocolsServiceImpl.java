package org.edec.commission.service.impl;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.edec.commission.manager.EntityManagerCommission;
import org.edec.commission.model.ComissionsProtocolsModel;
import org.edec.commission.service.ComissionsProtocolsService;
import org.edec.report.xls.XlsReport;

import java.util.List;

public class ComissionsProtocolsServiceImpl implements ComissionsProtocolsService {
    EntityManagerCommission manager = new EntityManagerCommission();
    @Override
    public List<ComissionsProtocolsModel> getComissionsProtocols(Long idSem) {
        return manager.getComissionsProtocols(idSem);
    }

    @Override
    public HSSFWorkbook getComissionsProtocolReport(List<ComissionsProtocolsModel> protocolComissionsList) {

        XlsReport<ComissionsProtocolsModel> xlsReport = new XlsReport<>(protocolComissionsList);
        xlsReport
                .addColumn("Дата", "comDateStr", 114)
                .addColumn("Семестр", "semester", 151)
                .addColumn("№ протокола", "protocolNumber", 115)
                .addColumn("Предмет", "subjectname", 383)
                .addColumn("ФИО студента", "fioStudent", 333)
                .addColumn("Группа", "groupname", 115)
                .addColumn("Кафедра", "chairsName", 333);

        return xlsReport.generateReport();
    }
}
