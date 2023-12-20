package org.edec.utility.report.service.order.impl;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.utility.report.manager.OrderReportDAO;
import org.edec.utility.report.model.order.*;
import org.edec.utility.report.model.order.dao.SocialIncreaseEsoModel;
import org.edec.utility.report.service.order.OrderReportService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class OrderReportSocialIncreaseImpl implements OrderReportService {
    private OrderReportDAO orderReportDAO = new OrderReportDAO();
    private OrderUtil orderUtil = new OrderUtil();

    @Override
    public JRBeanCollectionDataSource getBeanData (Long idOrder) {
        Map mainOrderMap = orderUtil.getMainOrderMap(idOrder);
        MainOrderModel main = (MainOrderModel) mainOrderMap.get(orderUtil.MAIN_ORDER);

        main.setPredicatingfio((String) mainOrderMap.get(orderUtil.PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(orderUtil.PREDICATE_POST));
        main.setEmployees((List<EmployeeOrderModel>) mainOrderMap.get(orderUtil.EMPLOYEES));

        List<SocialIncreaseEsoModel> socialIncreaseModels = orderReportDAO.getListSocialIncrease(idOrder);
        List<SectionOrderModel> sections = new ArrayList<>();

        for (SocialIncreaseEsoModel socialInc : socialIncreaseModels) {
            boolean addSection = true;
            for (SectionOrderModel section : sections) {
                if (section.getDescription().equals(socialInc.getDescription())) {
                    setSubsectionForSection(section, socialInc);
                    addSection = false;
                    break;
                }
            }
            if (addSection) {
                SectionOrderModel section = new SectionOrderModel();
                section.setDescription(socialInc.getDescription());
                section.setFoundation(socialInc.getFoundation());
                setSubsectionForSection(section, socialInc);
                sections.add(section);
            }
        }
        main.setSections(sections);
        for (SectionOrderModel section : main.getSections()) {
            if (section.getDescription().contains("Прекратить")) {
                main.setTypeorder(main.getTypeorder() + "\nО прекращении выплаты государственной социальной стипендии");
            }
        }

        List<MainOrderModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    private void setSubsectionForSection (SectionOrderModel section, SocialIncreaseEsoModel socialInc) {
        boolean addSubSection = true;
        for (SectionOrderModel subSection : section.getSubsections()) {
            if (subSection.getDescription().equals(socialInc.getDescriptionDate())) {
                setCourseForSubSection(subSection, socialInc);
                addSubSection = false;
                break;
            }
        }
        if (addSubSection) {
            SectionOrderModel subSection = new SectionOrderModel();
            subSection.setDescription(socialInc.getDescriptionDate());
            setCourseForSubSection(subSection, socialInc);
            section.getSubsections().add(subSection);
        }
    }

    private void setCourseForSubSection (SectionOrderModel subSection, SocialIncreaseEsoModel socialInc) {
        boolean addCourse = true;
        for (CourseOrderModel course : subSection.getCourses()) {
            if (course.getCourse() - socialInc.getCourse() == 0) {
                setGroupForCourse(course, socialInc);
                addCourse = false;
                break;
            }
        }
        if (addCourse) {
            CourseOrderModel course = new CourseOrderModel();
            course.setCourse(socialInc.getCourse());
            setGroupForCourse(course, socialInc);
            subSection.getCourses().add(course);
        }
    }

    private void setGroupForCourse (CourseOrderModel course, SocialIncreaseEsoModel socialInc) {
        boolean addGroup = true;
        for (GroupOrderModel group : course.getGroups()) {
            if (group.getGroupname().equals(socialInc.getGroupname())) {
                setStudentForGroup(group, socialInc);
                addGroup = false;
                break;
            }
        }
        if (addGroup) {
            GroupOrderModel group = new GroupOrderModel();
            group.setGroupname(socialInc.getGroupname());
            setStudentForGroup(group, socialInc);
            course.getGroups().add(group);
        }
    }

    private void setStudentForGroup (GroupOrderModel group, SocialIncreaseEsoModel socialInc) {
        StudentOrderModel student = new StudentOrderModel();
        student.setFio(socialInc.getFio());
        student.setRecordbook(socialInc.getRecordbook());
        group.getStudents().add(student);
    }
}
