package org.edec.secretaryChair.service.impl;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.edec.report.xls.XlsReport;
import org.edec.secretaryChair.manager.EntityManagerSecretaryChair;
import org.edec.secretaryChair.model.ChairsComissionsProtocolsModel;
import org.edec.secretaryChair.service.ChairsComissionProtocolsService;

import java.util.List;

public class ChairsComissionsProtocolsServiceImpl implements ChairsComissionProtocolsService {
    EntityManagerSecretaryChair manager = new EntityManagerSecretaryChair();
    @Override
    public List<ChairsComissionsProtocolsModel> getComissionsProtocols(Long idSem, Long idChair) {
        return manager.getComissionsProtocols(idSem, idChair);
    }

    @Override
    public HSSFWorkbook getComissionsProtocolReport(List<ChairsComissionsProtocolsModel> protocolComissionsList) {

        XlsReport<ChairsComissionsProtocolsModel> xlsReport = new XlsReport<>(protocolComissionsList);
        xlsReport
                .addColumn("Дата", "comDateStr", 114)
                .addColumn("Семестр", "semester", 151)
                .addColumn("№ протокола", "protocolNumber", 115)
                .addColumn("Предмет", "subjectname", 383)
                .addColumn("ФИО студента", "fioStudent", 333)
                .addColumn("Группа", "groupname", 115);

        return xlsReport.generateReport();
    }
}
