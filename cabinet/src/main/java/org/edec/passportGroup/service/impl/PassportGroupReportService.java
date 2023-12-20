package org.edec.passportGroup.service.impl;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.passportGroup.manager.PassportGroupManager;
import org.edec.passportGroup.model.GroupModel;
import org.edec.passportGroup.model.LinkGroupSemesterModel;
import org.edec.passportGroup.model.passportGroupReport.PassportGroupModel;
import org.edec.passportGroup.model.passportGroupReport.StudentModel;
import org.edec.passportGroup.model.passportGroupReport.StudentResult;
import org.edec.passportGroup.model.passportGroupReport.SubjectModel;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.model.jasperReport.ReportSrc;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Listitem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class PassportGroupReportService {
    private PassportGroupManager passportGroupManager = new PassportGroupManager();
    private JasperReportService jasperReportService = new JasperReportService();

    public JasperReport getReport(List<GroupModel> listGroups) {
        Collection<Map<String, ?>> args = new ArrayList<>();
        for (GroupModel el : listGroups) {
            HashMap<String, Object> toAdd = getDateForGroup(el);
            if(toAdd != null && toAdd.size() != 0) {
                args.add(toAdd);
            }
        }
        if (args.size() > 0) {
            return jasperReportService.getPassportGroupReport(args);
        } else {
            return null;
        }
    }

    public HashMap<String, Object> getDateForGroup(GroupModel groupModel) {
        HashMap<String, Object> arg = new HashMap<>();
        List<PassportGroupModel> modelList = passportGroupManager.getPassportGroupModel(groupModel.getIdLgs());

        List<SubjectModel> subjects = new ArrayList<>();

        List<SubjectModel> exam = new ArrayList<>();
        List<SubjectModel> pass = new ArrayList<>();
        List<SubjectModel> cp = new ArrayList<>();
        List<SubjectModel> cw = new ArrayList<>();
        List<SubjectModel> practic = new ArrayList<>();
        List<SubjectModel> difPass = new ArrayList<>();

        List<StudentModel> students = new ArrayList<>();
        for (PassportGroupModel model : modelList) {
            boolean addStudent = false;
            for (StudentModel studentModel : students) {
                if (studentModel.getId_sss().equals(model.getId_sss())) {
                    if(model.getIs_active() == 1) {
                        setSubjects(studentModel, model);
                    }
                    addStudent = true;
                    break;
                }
            }
            if (!addStudent) {
                StudentModel studentModel = new StudentModel(model.getFio(), model.getGovernment(), model.getProlongation(), model.getId_sss());
                if(model.getIs_active() == 1) {
                    setSubjects(studentModel, model);
                }
                students.add(studentModel);
            }
            if(model.getIs_active() == 1) {
                if (model.getIs_exam() == 1) {
                    boolean addSubject = false;
                    for (SubjectModel subjectModel : exam) {
                        if (model.getSubjectname().equals(subjectModel.getSubject())) {
                            if (model.getExamrating() > 2) {
                                subjectModel.setPassCount(subjectModel.getPassCount() + 1);
                            }
                            subjectModel.setCount(subjectModel.getCount() + 1);
                            addSubject = true;
                            break;
                        }
                    }
                    if (!addSubject) {
                        SubjectModel subjectModel = new SubjectModel();
                        subjectModel.setSubject(model.getSubjectname());
                        subjectModel.setFormofcontrol("экзамен");
                        subjectModel.setHoursCount(model.getHoursCount());
                        if (model.getExamrating() > 2)
                            subjectModel.setPassCount(1);
                        subjectModel.setCount(1);
                        exam.add(subjectModel);
                    }
                }
                if (model.getIs_pass() == 1 && model.getType() == 0) {
                    boolean addSubject = false;
                    for (SubjectModel subjectModel : pass) {
                        if (model.getSubjectname().equals(subjectModel.getSubject())) {
                            if (model.getPassrating() == 1) {
                                subjectModel.setPassCount(subjectModel.getPassCount() + 1);
                            }
                            subjectModel.setCount(subjectModel.getCount() + 1);
                            addSubject = true;
                            break;
                        }
                    }
                    if (!addSubject) {
                        SubjectModel subjectModel = new SubjectModel();
                        subjectModel.setSubject(model.getSubjectname());
                        subjectModel.setFormofcontrol("зачет");
                        subjectModel.setHoursCount(model.getHoursCount());
                        if (model.getPassrating() == 1) {
                            subjectModel.setPassCount(1);
                        }
                        subjectModel.setCount(1);
                        pass.add(subjectModel);
                    }
                }
                if (model.getIs_courseproject() == 1) {
                    boolean addSubject = false;
                    for (SubjectModel subjectModel : cp) {
                        if (model.getSubjectname().equals(subjectModel.getSubject())) {
                            if (model.getCourseprojectrating() > 2)
                                subjectModel.setPassCount(subjectModel.getPassCount() + 1);
                            subjectModel.setCount(subjectModel.getCount() + 1);
                            addSubject = true;
                            break;
                        }
                    }
                    if (!addSubject) {
                        SubjectModel subjectModel = new SubjectModel();
                        subjectModel.setSubject(model.getSubjectname());
                        subjectModel.setFormofcontrol("КП");
                        subjectModel.setHoursCount(model.getHoursCount());
                        if (model.getCourseprojectrating() > 2)
                            subjectModel.setPassCount(1);
                        subjectModel.setCount(1);
                        cp.add(subjectModel);
                    }
                }
                if (model.getIs_coursework() == 1) {
                    boolean addSubject = false;
                    for (SubjectModel subjectModel : cw) {
                        if (model.getSubjectname().equals(subjectModel.getSubject())) {
                            if (model.getCourseworkrating() > 2)
                                subjectModel.setPassCount(subjectModel.getPassCount() + 1);
                            subjectModel.setCount(subjectModel.getCount() + 1);
                            addSubject = true;
                            break;
                        }
                    }
                    if (!addSubject) {
                        SubjectModel subjectModel = new SubjectModel();
                        subjectModel.setSubject(model.getSubjectname());
                        subjectModel.setFormofcontrol("КР");
                        subjectModel.setHoursCount(model.getHoursCount());
                        if (model.getCourseworkrating() > 2)
                            subjectModel.setPassCount(1);
                        subjectModel.setCount(1);
                        cw.add(subjectModel);
                    }
                }
                if (model.getIs_practic() == 1 && model.getIs_pass() != 1) {
                    boolean addSubject = false;
                    for (SubjectModel subjectModel : practic) {
                        if (model.getSubjectname().equals(subjectModel.getSubject())) {
                            if (model.getPracticrating() > 2 || model.getPracticrating() == 1)
                                subjectModel.setPassCount(subjectModel.getPassCount() + 1);
                            subjectModel.setCount(subjectModel.getCount() + 1);
                            addSubject = true;
                            break;
                        }
                    }
                    if (!addSubject) {
                        SubjectModel subjectModel = new SubjectModel();
                        subjectModel.setSubject(model.getSubjectname());
                        subjectModel.setFormofcontrol("практика");
                        subjectModel.setHoursCount(model.getHoursCount());
                        if (model.getPracticrating() > 2 || model.getPracticrating() == 1)
                            subjectModel.setPassCount(1);
                        subjectModel.setCount(1);
                        practic.add(subjectModel);
                    }
                }
                if (model.getIs_pass() == 1 && model.getType() == 1) {
                    boolean addSubject = false;
                    for (SubjectModel subjectModel : difPass) {
                        if (model.getSubjectname().equals(subjectModel.getSubject())) {
                            if (model.getPassrating() > 2) {
                                subjectModel.setPassCount(subjectModel.getPassCount() + 1);
                            }
                            subjectModel.setCount(subjectModel.getCount() + 1);
                            addSubject = true;
                            break;
                        }
                    }
                    if (!addSubject) {
                        SubjectModel subjectModel = new SubjectModel();
                        subjectModel.setSubject(model.getSubjectname());
                        subjectModel.setFormofcontrol("диф.зач.");
                        subjectModel.setHoursCount(model.getHoursCount());
                        if (model.getPassrating() > 2) {
                            subjectModel.setPassCount(1);
                        }
                        subjectModel.setCount(1);
                        difPass.add(subjectModel);
                    }
                }
            }
        }

        subjects.addAll(exam);
        subjects.addAll(pass);
        subjects.addAll(cp);
        subjects.addAll(cw);
        subjects.addAll(practic);
        subjects.addAll(difPass);

        if (subjects.size() == 0) {
            return null;
        }

        for (SubjectModel subjectModel : subjects) {
            String percent = "0";
            if (subjectModel.getCount() > 0) {
                percent = String.valueOf(
                        Math.round((double) subjectModel.getPassCount() * 100 / subjectModel.getCount()));
            }
            subjectModel.setStatistic(percent + "%(" + subjectModel.getPassCount() + ")");
        }

        int commonCount = 0;
        int commonPassCount = 0;
        Double hoursCount = 0.0;
        for (StudentModel studentModel : students) {
            for (SubjectModel subjectModel : studentModel.getExams()) {
                studentModel.getResults().add(new StudentResult(subjectModel.getRating()));
                hoursCount = subjectModel.getHoursCount();
            }
            for (SubjectModel subjectModel : studentModel.getPass()) {
                studentModel.getResults().add(new StudentResult(subjectModel.getRating()));
                hoursCount = subjectModel.getHoursCount();
            }
            for (SubjectModel subjectModel : studentModel.getCp()) {
                studentModel.getResults().add(new StudentResult(subjectModel.getRating()));
                hoursCount = subjectModel.getHoursCount();
            }
            for (SubjectModel subjectModel : studentModel.getCw()) {
                studentModel.getResults().add(new StudentResult(subjectModel.getRating()));
                hoursCount = subjectModel.getHoursCount();
            }
            for (SubjectModel subjectModel : studentModel.getPractices()) {
                studentModel.getResults().add(new StudentResult(subjectModel.getRating()));
                hoursCount = subjectModel.getHoursCount();
            }
            for (SubjectModel subjectModel : studentModel.getDifPass()) {
                studentModel.getResults().add(new StudentResult(subjectModel.getRating()));
                hoursCount = subjectModel.getHoursCount();
            }
            String percent = "0";
            if (studentModel.getCountSubject() > 0) {
                percent = String.valueOf(
                        Math.round((double) studentModel.getPassCountSubject() * 100 / studentModel.getCountSubject()));
            }
            studentModel.setResult(percent + "%(" + studentModel.getPassCountSubject() + ")");
            commonCount += studentModel.getCountSubject();
            commonPassCount += studentModel.getPassCountSubject();
        }
        String percent = "0";
        if (commonCount != 0) {
            percent = String.valueOf(Math.round((double) commonPassCount * 100 / commonCount));
        }

        arg.put("stat_result", percent + "%");
        arg.put("hoursCount", hoursCount);

        JRBeanCollectionDataSource dataSourceSubjects = new JRBeanCollectionDataSource(subjects);
        arg.put("subjects", dataSourceSubjects);


        SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");

        arg.put("groupname", groupModel.getGroupName());
        arg.put("course", groupModel.getCourse() + " курс");
        arg.put("year", groupModel.getDateBegin() + "-" + groupModel.getDateEnd());
        arg.put("semester", (groupModel.getSemesterNumber() % 2 == 0) ? "весений семестр" : "осенний семестр");

        JRBeanCollectionDataSource dataSouceStudents = new JRBeanCollectionDataSource(students);
        arg.put("students", dataSouceStudents);

        return arg;
    }

    private void setSubjects(StudentModel student, PassportGroupModel model) {
        //Экзамен
        if (model.getIs_exam() == 1) {
            student.setCountSubject(student.getCountSubject() + 1);
            if (model.getExamrating() > 2) {
                student.setPassCountSubject(student.getPassCountSubject() + 1);
            }
            student.getExams().add(new SubjectModel("экзамен", model.getSubjectname(),
                    Integer.toString(RatingConst.getDataByRating(model.getExamrating()).getRating()), model.getHoursCount()));

        }
        //Зачет
        if (model.getIs_pass() == 1 && model.getType() == 0) {
            student.setCountSubject(student.getCountSubject() + 1);
            if (model.getPassrating() == 1) {
                student.setPassCountSubject(student.getPassCountSubject() + 1);
            }
            student.getPass().add(new SubjectModel("зачет", model.getSubjectname(),
                    Integer.toString(RatingConst.getDataByRating((model.getPassrating())).getRating()), model.getHoursCount()));
        }
        //КП
        if (model.getIs_courseproject() == 1) {
            student.setCountSubject(student.getCountSubject() + 1);
            if (model.getCourseprojectrating() > 2) {
                student.setPassCountSubject(student.getPassCountSubject() + 1);
            }
            student.getCp().add(new SubjectModel("КП", model.getSubjectname(),
                    Integer.toString(RatingConst.getDataByRating((model.getCourseprojectrating())).getRating()), model.getHoursCount()));
        }
        //КВ
        if (model.getIs_coursework() == 1) {
            student.setCountSubject(student.getCountSubject() + 1);
            if (model.getCourseworkrating() > 2) {
                student.setPassCountSubject(student.getPassCountSubject() + 1);
            }
            student.getCw().add(new SubjectModel("КВ", model.getSubjectname(),
                    Integer.toString(RatingConst.getDataByRating((model.getCourseworkrating())).getRating()), model.getHoursCount()));
        }
        //Практика
        if (model.getIs_practic() == 1 && model.getIs_pass() != 1) {
            student.setCountSubject(student.getCountSubject() + 1);
            if (model.getPracticrating() > 2 || model.getPracticrating() == 1) {
                student.setPassCountSubject(student.getPassCountSubject() + 1);
            }
            student.getPractices().add(new SubjectModel("прак.", model.getSubjectname(),
                    Integer.toString(RatingConst.getDataByRating((model.getPracticrating())).getRating()), model.getHoursCount()));
        }
        //Диф зачет
        if (model.getIs_pass() == 1 && model.getType() == 1) {
            student.setCountSubject(student.getCountSubject() + 1);
            if (model.getPassrating() > 2) {
                student.setPassCountSubject(student.getPassCountSubject() + 1);
            }
            student.getDifPass().add(new SubjectModel("диф.зач.", model.getSubjectname(),
                    Integer.toString(RatingConst.getDataByRating((model.getPassrating())).getRating()), model.getHoursCount()));
        }
    }
}
