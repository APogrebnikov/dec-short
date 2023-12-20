package org.edec.commission.service;

import java.util.Date;

public interface CommissionReportService {
    byte[] getXlsxForStudentCommission (int formofstudy, Date dateOfBegin, Date dateOfEnd);
}
