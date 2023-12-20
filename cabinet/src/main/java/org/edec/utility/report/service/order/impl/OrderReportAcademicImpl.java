package org.edec.utility.report.service.order.impl;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.utility.report.manager.OrderReportDAO;
import org.edec.utility.report.model.order.*;
import org.edec.utility.report.model.order.dao.AcademicEsoModel;
import org.edec.utility.report.service.order.OrderReportService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dmmax
 */
public class OrderReportAcademicImpl implements OrderReportService {
    private OrderReportDAO orderReportDAO = new OrderReportDAO();
    private OrderUtil orderUtil = new OrderUtil();

    @Override
    public JRBeanCollectionDataSource getBeanData (Long idOrder) {
        Map mainOrderMap = orderUtil.getMainOrderMap(idOrder);
        MainOrderModel main = (MainOrderModel) mainOrderMap.get(orderUtil.MAIN_ORDER);

        List<GroupOrderModel> listGroup = new ArrayList<>();
        List<AcademicEsoModel> academicList = orderReportDAO.getAcademicModel(idOrder);
        for (AcademicEsoModel academic : academicList) {
            boolean addGroup = true;
            for (GroupOrderModel group : listGroup) {
                if (group.getGroupname().equals(academic.getGroupname())) {
                    setSection(academic, group);
                    addGroup = false;
                    break;
                }
            }
            if (addGroup) {
                GroupOrderModel newGroup = new GroupOrderModel();
                newGroup.setGroupname(academic.getGroupname());
                newGroup.setPredicatingfio((String) mainOrderMap.get(orderUtil.PREDICATE_FIO));
                newGroup.setPredicatingpost((String) mainOrderMap.get(orderUtil.PREDICATE_POST));
                newGroup.setEmployees((List<EmployeeOrderModel>) mainOrderMap.get(orderUtil.EMPLOYEES));
                setSection(academic, newGroup);
                listGroup.add(newGroup);
            }
        }
        main.setGroups(listGroup);

        List<MainOrderModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    private void setSection (AcademicEsoModel academic, GroupOrderModel group) {
        boolean addSection = true;
        for (SectionOrderModel section : group.getSections()) {
            if (section.getProlongation() == academic.getProlongation()) {
                setSubsection(academic, section);
                addSection = false;
                break;
            }
        }
        if (addSection) {
            SectionOrderModel newSection = new SectionOrderModel();
            newSection.setDescription(academic.getDescription());
            newSection.setProlongation(academic.getProlongation());
            setSubsection(academic, newSection);
            group.getSections().add(newSection);
        }
    }

    private void setSubsection (AcademicEsoModel academic, SectionOrderModel section) {
        boolean addSubSection = true;
        for (SectionOrderModel subSection : section.getSubsections()) {
            if (subSection.getDescription().equals(academic.getSubDescription())) {
                subSection.getStudents().add(new StudentOrderModel(academic.getFio(), academic.getRecordbook()));
                addSubSection = false;
                break;
            }
        }
        if (addSubSection) {
            SectionOrderModel newSubSection = new SectionOrderModel();
            newSubSection.setDescription(academic.getSubDescription());
            newSubSection.getStudents().add(new StudentOrderModel(academic.getFio(), academic.getRecordbook()));
            section.getSubsections().add(newSubSection);
        }
    }
}
