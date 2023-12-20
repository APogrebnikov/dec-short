package org.edec.utility.report.model.order;

import org.edec.order.model.OrderModel;
import org.edec.utility.report.manager.OrderReportDAO;
import org.edec.utility.report.model.order.dao.EmployeeOrderEsoModel;

import java.util.*;

/**
 * Created by dmmax
 */
public class OrderUtil {
    public static final String MAIN_ORDER = "main_order";
    public static final String PREDICATE_FIO = "predicate_fio";
    public static final String PREDICATE_POST = "predicate_post";
    public static final String EMPLOYEES = "employee";

    private OrderReportDAO orderReportDAO = new OrderReportDAO();

    public String getSpeciallity (Integer qualification, String directionCode, String directionTitle, String speciallityCode,
                                  String speciallityTitle) {
        //TODO ЗАГЛУШКА, В СВЯЗИ С ОТСУТСТВИЕМ DIRECTION В БАЗЕ ШАХТ!
        if (directionCode == null || directionTitle == null) {
            String speciallity = getDirection(qualification, speciallityCode, speciallityTitle);

            return speciallity;
        }

        String speciallity = getDirection(qualification, directionCode, directionTitle);

        if (!directionCode.equals(speciallityCode)) {
            speciallity += getProfile(qualification, speciallityCode, speciallityTitle);
        }
        return speciallity;
    }

    private String getDirection (Integer qualification, String directionCode, String directionTitle) {
        String result = "";
        if (qualification == null || directionCode == null || directionTitle == null) {
            return "";
        }
        if (qualification == 1) {
            result = "специальность ";
        } else {
            result = "направление подготовки ";
        }
        result += directionCode + " \"" + directionTitle + "\"";
        return result;
    }

    private String getProfile (Integer qualification, String speciallityCode, String speciallityTitle) {
        String result = "";
        if (qualification == null || speciallityCode == null || speciallityTitle == null) {
            return result;
        }
        if (qualification == 1) {
            result = " специализация ";
        } else if (qualification == 2) {
            result = " профиль ";
        } else if (qualification == 3) {
            result = " специализированная магистерская программа ";
        }
        result += speciallityCode + " \"" + speciallityTitle + "\"";
        return result;
    }

    public Map<String, Object> getMainOrderMap (Long idOrder) {
        Map<String, Object> map = new HashMap<>();
        MainOrderModel main = orderReportDAO.getMainOrderInfoByID(idOrder);

        List<EmployeeOrderEsoModel> employeesOrderModels = orderReportDAO.getEmployeesOrder(idOrder);
        List<EmployeeOrderModel> employees = new ArrayList<>();
        String predicatingfio = null, predicatingpost = null;
        for (EmployeeOrderEsoModel employee : employeesOrderModels) {
            if (employee.getSubquery() != null && !employee.getSubquery().equals("")) {
                if (!orderReportDAO.existsStudentsInOrderBySubquery(idOrder, employee.getSubquery())) {
                    continue;
                }
            }
            if (employee.getActionrule() == 0) {
                if (main.getFormOfStudyId().equals(employee.getFormofstudy())) {
                    main.setExecutorfio(employee.getFio());
                    main.setExecutortel(employee.getPost());
                }
            } else if (employee.getActionrule() == 1) {
                predicatingfio = employee.getFio();
                predicatingpost = employee.getPost();
            } else if (employee.getActionrule() == 2) {
                employees.add(new EmployeeOrderModel(employee.getFio(), employee.getPost()));
            }
        }
        map.put(MAIN_ORDER, main);
        map.put(PREDICATE_FIO, predicatingfio);
        map.put(PREDICATE_POST, predicatingpost);
        map.put(EMPLOYEES, employees);
        return map;
    }
}
