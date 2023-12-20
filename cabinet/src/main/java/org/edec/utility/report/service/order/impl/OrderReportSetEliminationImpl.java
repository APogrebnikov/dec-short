package org.edec.utility.report.service.order.impl;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.report.manager.OrderReportDAO;
import org.edec.utility.report.model.order.*;
import org.edec.utility.report.model.order.dao.SetEliminationModel;
import org.edec.utility.report.service.order.OrderReportService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderReportSetEliminationImpl implements OrderReportService {
    private OrderReportDAO orderReportDAO = new OrderReportDAO();
    private OrderUtil orderUtil = new OrderUtil();
    private MainOrderModel main;
    private OrderRuleConst orderRuleConst;

    public OrderReportSetEliminationImpl(OrderRuleConst orderRuleConst) {
        this.orderRuleConst = orderRuleConst;
    }

    @Override
    public JRBeanCollectionDataSource getBeanData(Long idOrder) {
        Map mainOrderMap = orderUtil.getMainOrderMap(idOrder);

        if (orderRuleConst.equals(OrderRuleConst.SET_ELIMINATION_RESPECTFUL)) {
            main = getBeanDataForReportWithPaymentDesc(idOrder, false);
        } else {
            main = (MainOrderModel) mainOrderMap.get(orderUtil.MAIN_ORDER);

            List<SectionOrderModel> sections = new ArrayList<>();
            List<SetEliminationModel> setEliminationModels = orderReportDAO.getSetEliminationModel(idOrder);

            for (SetEliminationModel model : setEliminationModels) {
                boolean addSection = true;

                for (SectionOrderModel section : sections) {
                    if (section.getDescription().equals(model.getDescription())) {
                        setCourseForSection(model, section);
                        addSection = false;
                        break;
                    }
                }

                if (addSection) {
                    if (sections.size() > 0) {
                        if (sections.get(sections.size() - 1).getSubsections().get(1).getCourses().size() == 0) {
                            sections.get(sections.size() - 1).getSubsections().remove(1);
                        }

                        if (sections.get(sections.size() - 1).getSubsections().get(0).getCourses().size() == 0) {
                            sections.get(sections.size() - 1).getSubsections().remove(0);
                        }
                    }

                    SectionOrderModel section = new SectionOrderModel();
                    section.setDescription(model.getDescription());
                    section.setFoundation(model.getFoundation());
                    section.setSubsections(new ArrayList<>());

                    SectionOrderModel budgetSection = new SectionOrderModel();
                    budgetSection.setCourses(new ArrayList<>());
                    if (orderRuleConst.equals(OrderRuleConst.SET_ELIMINATION_RESPECTFUL)) {
                        budgetSection.setDescription(null);
                    } else {
                        budgetSection.setDescription("обучающимся за счет бюджетных ассигнований федерального бюджета:");
                    }
                    section.getSubsections().add(budgetSection);

                    SectionOrderModel paymentSection = new SectionOrderModel();
                    paymentSection.setCourses(new ArrayList<>());
                    paymentSection.setDescription("обучающимся на условиях договора об оказании платных образовательных услуг:");
                    section.getSubsections().add(paymentSection);

                    setCourseForSection(model, section);

                    sections.add(section);
                }
            }

            if (sections.size() > 0) {
                if (sections.get(sections.size() - 1).getSubsections().get(1).getCourses().size() == 0) {
                    sections.get(sections.size() - 1).getSubsections().remove(1);
                }

                if (sections.get(sections.size() - 1).getSubsections().get(0).getCourses().size() == 0) {
                    sections.get(sections.size() - 1).getSubsections().remove(0);
                }
            }

            main.setSections(sections);
        }

        main.setPredicatingfio((String) mainOrderMap.get(orderUtil.PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(orderUtil.PREDICATE_POST));
        main.setEmployees((List<EmployeeOrderModel>) mainOrderMap.get(orderUtil.EMPLOYEES));

        List<MainOrderModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    //TODO hardcode, must be fixed
    public JRBeanCollectionDataSource getBeanDataForEliminationNote(Long idOrder) {
        main = getBeanDataForReportWithPaymentDesc(idOrder, true);

        List<MainOrderModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public MainOrderModel getBeanDataForReportWithPaymentDesc(Long idOrder, Boolean isForNotion) {
        Map mainOrderMap = orderUtil.getMainOrderMap(idOrder);
        main = (MainOrderModel) mainOrderMap.get(orderUtil.MAIN_ORDER);

        List<SectionOrderModel> sections = new ArrayList<>();
        List<SetEliminationModel> setEliminationModels = orderReportDAO.getSetEliminationModel(idOrder);

        for (SetEliminationModel model : setEliminationModels) {
            boolean addSection = true;

            String description;

            if (model.getGovernmentFinanced()) {
                description =
                        "Прошу установить срок повторной промежуточной аттестации для ликвидации академических задолженностей до " +
                        DateConverter.convertDateToString(model.getFirstDateStudent()) +
                        " г. обучающимся за счет бюджетных ассигнований федерального" +
                        " бюджета очной формы обучения, не прошедшим промежуточную аттестацию по" +
                        " неуважительной причине и получившим неудовлетворительные результаты:";
            } else {
                description = "Прошу установить срок повторной промежуточной аттестации для ликвидации академических"
                              + " задолженностей до " +
                              DateConverter.convertDateToString(model.getFirstDateStudent()) +
                              " г. обучающимся на условиях договора об оказании платных" +
                              " образовательных услуг очной формы обучения, не прошедшим промежуточную аттестацию" +
                              " по неуважительной причине и получившим неудовлетворительные результаты:";
            }

            //            String description = "Прошу установить срок ликвидации академических задолженностей по итогам осеннего семестра " +
            //                                 (model.getBeginYear().getYear() + 1900) + "-" + (model.getEndYear().getYear() + 1900) +
            //                                 " учебного года до " + DateConverter.convertDateToString(model.getFirstDateStudent()) +
            //                                 "г. следующим студентам очной формы обучения, " +
            //                                 " обучающимся за счет бюджетных ассигнований федерального бюджета:";

            for (SectionOrderModel section : sections) {
                if (section.getDescription().equals(isForNotion ? description : model.getDescription())) {
                    setCourseForSection(model, section, true);
                    addSection = false;
                    break;
                }
            }

            if (addSection) {
                SectionOrderModel section = new SectionOrderModel();
                section.setDescription(isForNotion ? description : model.getDescription());
                section.setFoundation(model.getFoundation());
                section.setSubsections(new ArrayList<>());
                setCourseForSection(model, section, true);
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

        return main;
    }

    private void setCourseForSection(SetEliminationModel model, SectionOrderModel section) {
        boolean addCourse = true;
        if (section.getSubsections().size() != 0) {
            if (model.getGovernmentFinanced()) {
                for (CourseOrderModel course : section.getSubsections().get(0).getCourses()) {
                    if (course.getCourse() - model.getCourse() == 0) {
                        setGroupForCourse(model, course);
                        addCourse = false;
                        break;
                    }
                }
            } else {
                for (CourseOrderModel course : section.getSubsections().get(1).getCourses()) {
                    if (course.getCourse() - model.getCourse() == 0) {
                        setGroupForCourse(model, course);
                        addCourse = false;
                        break;
                    }
                }
            }
        }

        if (addCourse) {
            CourseOrderModel course = new CourseOrderModel();
            course.setCourse(model.getCourse());

            course.setFullcourse("Курс " + model.getCourse());

            setGroupForCourse(model, course);

            if (model.getGovernmentFinanced()) {
                section.getSubsections().get(0).getCourses().add(course);
            } else {
                section.getSubsections().get(1).getCourses().add(course);
            }
        }
    }

    private void setCourseForSection(SetEliminationModel model, SectionOrderModel section, boolean governmentFinanced) {
        if (governmentFinanced) {
            boolean addCourse = true;
            for (CourseOrderModel course : section.getCourses()) {
                if (course.getCourse() - model.getCourse() == 0) {
                    setGroupForCourse(model, course);
                    addCourse = false;
                    break;
                }
            }
            if (addCourse) {
                CourseOrderModel course = new CourseOrderModel();
                course.setCourse(model.getCourse());

                course.setFullcourse("Курс " + model.getCourse());

                setGroupForCourse(model, course);
                section.getCourses().add(course);
            }
        } else {
            boolean addCourse = true;
            if (section.getSubsections().size() != 0) {
                for (CourseOrderModel course : section.getSubsections().get(0).getCourses()) {
                    if (course.getCourse() - model.getCourse() == 0) {
                        setGroupForCourse(model, course);
                        addCourse = false;
                        break;
                    }
                }
            }

            if (addCourse) {
                CourseOrderModel course = new CourseOrderModel();
                course.setCourse(model.getCourse());

                course.setFullcourse("Курс " + model.getCourse());

                setGroupForCourse(model, course);

                if (section.getSubsections().size() == 0) {
                    SectionOrderModel paymentSection = new SectionOrderModel();
                    paymentSection.setCourses(new ArrayList<>());
                    paymentSection.setDescription("обучающимся на условиях договора об оказании платных образовательных услуг:");
                    section.getSubsections().add(paymentSection);
                }

                section.getSubsections().get(0).getCourses().add(course);
            }
        }
    }

    private void setGroupForCourse(SetEliminationModel model, CourseOrderModel course) {
        boolean addGroup = true;
        for (GroupOrderModel group : course.getGroups()) {
            if (group.getGroupname().equals(model.getGroupname())) {
                setStudentForGroup(model, group);
                addGroup = false;
                break;
            }
        }
        if (addGroup) {
            GroupOrderModel group = new GroupOrderModel();
            group.setGroupname(model.getGroupname());
            setStudentForGroup(model, group);
            course.getGroups().add(group);
        }
    }

    private void setStudentForGroup(SetEliminationModel model, GroupOrderModel group) {
        StudentOrderModel student = new StudentOrderModel();
        student.setFio(model.getFio());
        student.setRecordbook(model.getRecordbook());
        group.getStudents().add(student);
    }
}
