package org.edec.utility.report.service.order.impl;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.constants.OrderStudentJSONConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.report.manager.OrderReportDAO;
import org.edec.utility.report.model.order.*;
import org.edec.utility.report.model.order.dao.TransferEsoModel;
import org.edec.utility.report.service.order.OrderReportService;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class OrderReportTansferImpl implements OrderReportService {
    private OrderReportDAO orderReportDAO = new OrderReportDAO();
    private OrderUtil orderUtil = new OrderUtil();
    private MainOrderModel main;

    @Override
    public JRBeanCollectionDataSource getBeanData (Long idOrder) {
        Map mainOrderMap = orderUtil.getMainOrderMap(idOrder);
        main = (MainOrderModel) mainOrderMap.get(orderUtil.MAIN_ORDER);

        main.setPredicatingfio((String) mainOrderMap.get(orderUtil.PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(orderUtil.PREDICATE_POST));
        main.setEmployees((List<EmployeeOrderModel>) mainOrderMap.get(orderUtil.EMPLOYEES));

        List<SectionOrderModel> sections = new ArrayList<>();
        List<TransferEsoModel> transferModels = orderReportDAO.getTransferEsoModel(idOrder);

        for (TransferEsoModel transfer : transferModels) {

            if (transfer.getAdditional() != null && !transfer.getAdditional().equals("")) {
                JSONObject json = new JSONObject(transfer.getAdditional());
                if (json.has(OrderStudentJSONConst.ORDER_NUMBER) &&
                    json.has(OrderStudentJSONConst.ORDER_DATE)) {
                    Date datePrev = DateConverter.convertStringToDate(
                            json.getString(OrderStudentJSONConst.ORDER_DATE), "yyyy-MM-dd");
                    transfer.setPrevOrderDate(DateConverter.convertDateToString(datePrev));
                    transfer.setPrevOrderNum(json.getString(OrderStudentJSONConst.ORDER_NUMBER));
                    transfer.setDescription(transfer.getDescription().replace("$prevNum$", transfer.getPrevOrderNum()));
                    transfer.setDescription(transfer.getDescription().replace("$prevDate$", transfer.getPrevOrderDate()));
                }
            }

            boolean addSection = true;
            for (SectionOrderModel section : sections) {
                if (section.getDescription().equals(transfer.getDescription())) {
                    setCourseForSection(transfer, section);
                    addSection = false;
                    break;
                }
            }

            if (addSection) {
                SectionOrderModel section = new SectionOrderModel();
                //FIXME: костыль
                section.setDescription(transfer.getDescription());
                section.setFoundation(transfer.getFoundation());
                setCourseForSection(transfer, section);
                sections.add(section);
            }
        }

        for (int i = 0; i < sections.size(); i++) {
            SectionOrderModel section = sections.get(i);
            SectionOrderModel nextSection = sections.size() != (i + 1) ? sections.get(i + 1) : null;

            if (nextSection == null) {
                continue;
            }
            if (section.getDescription().contains("уважител") && !nextSection.getDescription().contains("уважител")) {
                continue;
            }

            section.setFoundation(null);
        }

        main.setSections(sections);
        List<MainOrderModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    //TODO hardcode, must be fixed
    public JRBeanCollectionDataSource getBeanDataForServiceNote (Long idOrder) {
        Map mainOrderMap = orderUtil.getMainOrderMap(idOrder);
        main = (MainOrderModel) mainOrderMap.get(orderUtil.MAIN_ORDER);

        main.setPredicatingfio((String) mainOrderMap.get(orderUtil.PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(orderUtil.PREDICATE_POST));
        main.setEmployees((List<EmployeeOrderModel>) mainOrderMap.get(orderUtil.EMPLOYEES));

        List<SectionOrderModel> sections = new ArrayList<>();
        List<TransferEsoModel> transferModels = orderReportDAO.getTransferEsoModelForServiceNote(idOrder);

        for (TransferEsoModel transfer : transferModels) {
            String formOfStudy = transfer.getDescription().contains("договор")
                                 ? "на условиях договора об оказании платных образовательных услуг"
                                 : "за счет бюджетных ассигнований федерального бюджета";
            transfer.setDescription(
                    "Прошу продлить срок ликвидации академических задолженностей следующим студентам," + " обучающимся " + formOfStudy +
                    ", условно переведенным на следующий курс и не прошедшим" + " промежуточную аттестацию, до " +
                    new SimpleDateFormat("dd.MM.yyyy").format(transfer.getFirstDateStudent()) + ":");
            boolean addSection = true;
            for (SectionOrderModel section : sections) {
                if (section.getDescription().equals(transfer.getDescription())) {
                    setCourseForSection(transfer, section);
                    addSection = false;
                    break;
                }
            }

            if (addSection) {
                SectionOrderModel section = new SectionOrderModel();
                section.setDescription(transfer.getDescription());
                section.setFoundation(transfer.getFoundation());
                setCourseForSection(transfer, section);
                sections.add(section);
            }
        }

        for (int i = 0; i < sections.size(); i++) {
            SectionOrderModel section = sections.get(i);
            SectionOrderModel nextSection = sections.size() != (i + 1) ? sections.get(i + 1) : null;

            if (nextSection == null) {
                continue;
            }
            if (section.getDescription().contains("уважител") && !nextSection.getDescription().contains("уважител")) {
                continue;
            }

            section.setFoundation(null);
        }

        main.setSections(sections);
        List<MainOrderModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    private void setCourseForSection (TransferEsoModel transfer, SectionOrderModel section) {
        boolean addCourse = true;
        for (CourseOrderModel course : section.getCourses()) {
            if (course.getCourse() - transfer.getCourse() == 0) {
                setGroupForCourse(transfer, course);
                addCourse = false;
                break;
            }
        }
        if (addCourse) {
            CourseOrderModel course = new CourseOrderModel();
            course.setCourse(transfer.getCourse());

            if (main.getIdOrderRule().equals(OrderRuleConst.TRANSFER_PROLONGATION.getId())) {
                course.setFullcourse(transfer.getCourse() + " курс");
            }

            if (main.getIdOrderRule().equals(OrderRuleConst.TRANSFER_AFTER_TRANSFER_CONDITIONALLY_RESPECTFUL.getId()) ||
                main.getIdOrderRule().equals(OrderRuleConst.TRANSFER_AFTER_TRANSFER_CONDITIONALLY_NOT_RESPECTFUL.getId())) {
                course.setFullcourse("на " + transfer.getCourse() + " курсе");
            }

            setGroupForCourse(transfer, course);
            section.getCourses().add(course);
        }
    }

    private void setGroupForCourse (TransferEsoModel transfer, CourseOrderModel course) {
        boolean addGroup = true;
        for (GroupOrderModel group : course.getGroups()) {
            if (group.getGroupname().equals(transfer.getGroupname())) {
                setStudentForGroup(transfer, group);
                addGroup = false;
                break;
            }
        }
        if (addGroup) {
            GroupOrderModel group = new GroupOrderModel();
            group.setGroupname(transfer.getGroupname());
            setStudentForGroup(transfer, group);
            course.getGroups().add(group);
        }
    }

    private void setStudentForGroup (TransferEsoModel transfer, GroupOrderModel group) {
        StudentOrderModel student = new StudentOrderModel();
        student.setFio(transfer.getFio());
        student.setRecordbook(transfer.getRecordbook());
        group.getStudents().add(student);
    }
}
