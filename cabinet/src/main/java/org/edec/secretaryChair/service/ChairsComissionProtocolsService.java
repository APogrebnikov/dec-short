package org.edec.secretaryChair.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.edec.secretaryChair.model.ChairsComissionsProtocolsModel;

import java.util.List;

public interface ChairsComissionProtocolsService {
    List<ChairsComissionsProtocolsModel> getComissionsProtocols(Long idSem, Long idChair);
    HSSFWorkbook getComissionsProtocolReport(List<ChairsComissionsProtocolsModel> protocolComissionsList);
}
