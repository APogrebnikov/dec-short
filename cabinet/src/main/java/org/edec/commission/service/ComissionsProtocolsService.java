package org.edec.commission.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.edec.commission.model.ComissionsProtocolsModel;

import java.util.List;

public interface ComissionsProtocolsService {

    List<ComissionsProtocolsModel> getComissionsProtocols(Long idSem);
    HSSFWorkbook getComissionsProtocolReport(List<ComissionsProtocolsModel> protocolComissionsList);

}
