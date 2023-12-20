package org.edec.eok.service.impl;

import org.edec.eok.manager.EokManager;
import org.edec.eok.model.SubjectModel;
import org.edec.eok.model.TeacherModel;
import org.edec.eok.model.dao.SubjectEsoModel;
import org.edec.eok.service.EokService;

import java.util.ArrayList;
import java.util.List;


public class EokESOimpl implements EokService {
    private EokManager eokManager = new EokManager();

    @Override
    public List<SubjectModel> getSubjects (String idLGS) {
        return dividedSubjectsByTeacher(eokManager.getSubjectModel(idLGS));
    }

    @Override
    public List<SubjectModel> getSubjectsByFilter (List<SubjectModel> subjects, String department, String subject, boolean isNullEok,
                                                   boolean isNullTeacher) {
        if (department.equals("") && subject.equals("") && !isNullEok && !isNullTeacher) {
            return subjects;
        }
        List<SubjectModel> result = new ArrayList<>();
        for (SubjectModel subjectModel : subjects) {
            if ((subject.equals("") || subjectModel.getSubjectname().toLowerCase().contains(subject.toLowerCase())) &&
                (department.equals("") || subjectModel.getDepartment().toLowerCase().contains(department.toLowerCase())) &&
                ((isNullEok == false) || (isNullEok && subjectModel.getIdEsoCourse() == null)) &&
                ((isNullTeacher == false) || (isNullTeacher && subjectModel.getTeachers().size() == 0))) {
                result.add(subjectModel);
            }
        }
        return result;
    }

    private List<SubjectModel> dividedSubjectsByTeacher (List<SubjectEsoModel> models) {
        List<SubjectModel> result = new ArrayList<>();
        for (SubjectEsoModel model : models) {
            boolean addTeacher = true;
            for (SubjectModel subject : result) {
                if (subject.getIdLGSS().equals(model.getIdLGSS()) && subject.getSubjectname().equals(model.getSubjectname())) {
                    boolean addTeacher2 = true;
                    for (TeacherModel teacher : subject.getTeachers()) {
                        if (model.getFio() != null && teacher.getFio().equals(model.getFio())) {
                            addTeacher2 = false;
                            break;
                        }
                    }
                    if (model.getFio() != null && addTeacher2) {
                        subject.getTeachers().add(new TeacherModel(model.getFio()));
                    }
                    addTeacher = false;
                    break;
                }
            }
            if (addTeacher) {
                SubjectModel subject = new SubjectModel();
                subject.setHourscount(model.getHourscount());
                subject.setIdEsoCourse(model.getIdEsoCourse());
                subject.setIdLGSS(model.getIdLGSS());
                subject.setDepartment(model.getDepartment());
                subject.setFormOfControl(model.getFormOfControl());
                subject.setGroupname(model.getGroupname());
                subject.setSubjectname(model.getSubjectname());
                if (model.getFio() != null) {
                    subject.getTeachers().add(new TeacherModel(model.getFio()));
                }
                result.add(subject);
            }
        }

        return result;
    }
}
