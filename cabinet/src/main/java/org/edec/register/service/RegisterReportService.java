package org.edec.register.service;


import org.edec.register.model.report.RegisterDateModel;

import java.util.Date;
import java.util.List;

public interface RegisterReportService {
    List<RegisterDateModel> getRegistersByPeriod(Date from, Date to, long idSem);
}
