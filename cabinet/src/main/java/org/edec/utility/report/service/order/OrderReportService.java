package org.edec.utility.report.service.order;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Created by dmmax
 */
public interface OrderReportService {
    public JRBeanCollectionDataSource getBeanData (Long idOrder);
}
