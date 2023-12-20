package org.edec.utility.component.service.impl;

import org.edec.utility.component.manager.StudentComponentManager;
import org.edec.utility.component.model.GroupModel;
import org.edec.utility.component.model.StudentModel;
import org.edec.utility.component.model.SubjectModel;
import org.edec.utility.component.model.dao.GroupSubjectDAOmodel;
import org.edec.utility.component.model.dao.StudentSubjectDAOmodel;
import org.edec.utility.component.service.StudentComponentService;

import java.util.ArrayList;
import java.util.List;


public class StudentComponentImpl implements StudentComponentService {
    private StudentComponentManager studentComponentManager = new StudentComponentManager();

    @Override
    public List<GroupModel> getGroupSemByGroupname (String groupname) {
        return divisionModelByGroup(studentComponentManager.getGroupSubjectModel(groupname));
    }

    @Override
    public List<StudentModel> getStudentByIdHumAndGroupname (Long idHum, String groupname) {
        return divisionModelByStudent(studentComponentManager.getStudentsModel(groupname, idHum));
    }

    private List<GroupModel> divisionModelByGroup (List<GroupSubjectDAOmodel> models) {
        List<GroupModel> result = new ArrayList<>();

        for (GroupSubjectDAOmodel model : models) {
            boolean addGroup = true;
            for (GroupModel group : result) {
                if (group.getIdLGS().equals(model.getIdLGS())) {
                    setSubjectForGroup(model, group);
                    addGroup = false;
                    break;
                }
            }
            if (addGroup) {
                GroupModel group = new GroupModel();
                group.setCourse(model.getCourse());
                group.setGroupname(model.getGroupname());
                group.setIdDG(model.getIdDG());
                group.setIdLGS(model.getIdLGS());
                group.setSemester(model.getSemester());
                setSubjectForGroup(model, group);
                result.add(group);
            }
        }

        return result;
    }

    private void setSubjectForGroup (GroupSubjectDAOmodel model, GroupModel group) {
        SubjectModel subject = new SubjectModel();
        subject.setExam(model.getExam());
        subject.setPass(model.getPass());
        subject.setCp(model.getCp());
        subject.setCw(model.getCw());
        subject.setPractic(model.getPractic());
        subject.setHoursCount(model.getHoursCount());
        subject.setType(model.getType());
        subject.setIdSubj(model.getIdSubj());
        subject.setSubjectname(model.getSubjectname());
        group.getSubjects().add(subject);
    }

    private List<StudentModel> divisionModelByStudent (List<StudentSubjectDAOmodel> models) {
        List<StudentModel> result = new ArrayList<>();
        for (StudentSubjectDAOmodel model : models) {
            boolean addStudent = true;
            for (StudentModel student : result) {
                if (student.getIdSSS().equals(model.getIdSSS())) {
                    setSubjectForStudent(model, student);
                    addStudent = false;
                    break;
                }
            }
            if (addStudent) {
                StudentModel student = new StudentModel();
                student.setIdSSS(model.getIdSSS());
                student.setSemester(model.getSemester());
                setSubjectForStudent(model, student);
                result.add(student);
            }
        }
        return result;
    }

    private void setSubjectForStudent (StudentSubjectDAOmodel model, StudentModel student) {
        SubjectModel subject = new SubjectModel();
        subject.setExam(model.getExam());
        subject.setPass(model.getPass());
        subject.setCp(model.getCp());
        subject.setCw(model.getCw());
        subject.setPractic(model.getPractic());
        subject.setHoursCount(model.getHoursCount());
        subject.setType(model.getType());
        subject.setSubjectname(model.getSubjectname());
        student.getSubjects().add(subject);
    }
}
