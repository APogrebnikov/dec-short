package org.edec.contingentMovement.report.sevice;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.studentPassport.model.StudentStatusModel;

public interface ResitMarkService {
    JRBeanCollectionDataSource getResitReport (StudentStatusModel student);
}
