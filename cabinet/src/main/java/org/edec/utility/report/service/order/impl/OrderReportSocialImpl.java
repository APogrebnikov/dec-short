package org.edec.utility.report.service.order.impl;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.utility.constants.ScholarshipSizeConst;
import org.edec.utility.report.manager.OrderReportDAO;
import org.edec.utility.report.model.order.*;
import org.edec.utility.report.model.order.dao.SocialEsoModel;
import org.edec.utility.report.service.order.OrderReportService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class OrderReportSocialImpl implements OrderReportService {
    private OrderReportDAO orderReportDAO = new OrderReportDAO();
    private OrderUtil orderUtil = new OrderUtil();

    @Override
    public JRBeanCollectionDataSource getBeanData (Long idOrder) {
        Map mainOrderMap = orderUtil.getMainOrderMap(idOrder);
        MainOrderModel main = (MainOrderModel) mainOrderMap.get(orderUtil.MAIN_ORDER);

        main.setPredicatingfio((String) mainOrderMap.get(orderUtil.PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(orderUtil.PREDICATE_POST));
        main.setEmployees((List<EmployeeOrderModel>) mainOrderMap.get(orderUtil.EMPLOYEES));

        List<SocialEsoModel> socialModels = orderReportDAO.getListSocial(idOrder);
        List<SectionOrderModel> sections = new ArrayList<>();

        for (SocialEsoModel social : socialModels) {
            boolean addSection = true;
            for (SectionOrderModel section : sections) {
                if (section.getDescription().equals(social.getDescription())) {
                    setCourseForSection(section, social);
                    addSection = false;
                    break;
                }
            }
            if (addSection) {
                SectionOrderModel section = new SectionOrderModel();
                section.setDescription(social.getDescription());
                section.setFoundation(social.getFoundation());
                setCourseForSection(section, social);
                sections.add(section);
            }
        }
        main.setSections(sections);

        List<MainOrderModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    private void setCourseForSection (SectionOrderModel section, SocialEsoModel social) {
        boolean addCourse = true;
        for (CourseOrderModel course : section.getCourses()) {
            if (course.getCourse() - social.getCourse() == 0) {
                setGroupForCourse(course, social);
                addCourse = false;
                break;
            }
        }
        if (addCourse) {
            CourseOrderModel course = new CourseOrderModel();
            course.setCourse(social.getCourse());
            setGroupForCourse(course, social);
            section.getCourses().add(course);
        }
    }

    private void setGroupForCourse (CourseOrderModel course, SocialEsoModel social) {
        boolean addGroup = true;
        for (GroupOrderModel group : course.getGroups()) {
            if (group.getGroupname().equals(social.getGroupname())) {
                setStudentForGroup(group, social);
                addGroup = false;
                break;
            }
        }
        if (addGroup) {
            GroupOrderModel group = new GroupOrderModel();
            group.setGroupname(social.getGroupname());
            setStudentForGroup(group, social);
            course.getGroups().add(group);
        }
    }

    private void setStudentForGroup (GroupOrderModel group, SocialEsoModel social) {
        StudentOrderModel student = new StudentOrderModel();
        student.setFio(social.getFio());
        student.setRecordbook(social.getRecordbook());

        if (social.isSirota()) {
            student.setScholarship(ScholarshipSizeConst.SOCIAL_FOR_ORPHAN.getSize());
        } else {
            student.setScholarship(ScholarshipSizeConst.SOCIAL.getSize());
        }

        group.getStudents().add(student);
    }
}
