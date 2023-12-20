package org.edec.utility.report.service.order.impl;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.utility.constants.OrderStudentJSONConst;
import org.edec.utility.report.manager.OrderReportDAO;
import org.edec.utility.report.model.order.EmployeeOrderModel;
import org.edec.utility.report.model.order.IndividualOrderModel;
import org.edec.utility.report.model.order.MainOrderModel;
import org.edec.utility.report.model.order.OrderUtil;
import org.edec.utility.report.service.order.OrderReportService;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dmmax
 */
public class OrderReportDeductionImpl implements OrderReportService {
    private OrderReportDAO orderReportDAO = new OrderReportDAO();
    private OrderUtil orderUtil = new OrderUtil();

    @Override
    public JRBeanCollectionDataSource getBeanData (Long idOrder) {
        Map mainOrderMap = orderUtil.getMainOrderMap(idOrder);
        MainOrderModel main = (MainOrderModel) mainOrderMap.get(orderUtil.MAIN_ORDER);
        List<IndividualOrderModel> listIndividual = orderReportDAO.getListIndividuals(idOrder);

        for (IndividualOrderModel student : listIndividual) {
            if (student.getAdditional() != null && !student.getAdditional().equals("")) {
                JSONObject additional = new JSONObject(student.getAdditional());

                if (additional.has(OrderStudentJSONConst.FOUNDATION)) {
                    student.setFoundation(additional.getString(OrderStudentJSONConst.FOUNDATION));
                }
            }
        }

        main.setIndividualsStudents(listIndividual);
        main.setPredicatingfio((String) mainOrderMap.get(orderUtil.PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(orderUtil.PREDICATE_POST));
        main.setEmployees((List<EmployeeOrderModel>) mainOrderMap.get(orderUtil.EMPLOYEES));

        List<MainOrderModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }
}
