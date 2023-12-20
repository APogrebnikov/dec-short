package org.edec.teacher.model;

import java.util.List;

public class CourseHistoryModel {
    private Long idEmp;
    private String subjectname;
    private List<EsoCourseModel> usedCourses;
    private List<GroupModel> groupList;

    public String getSubjectname () {
        return subjectname;
    }

    public void setSubjectname (String subjectname) {
        this.subjectname = subjectname;
    }

    public Long getIdEmp () {
        return idEmp;
    }

    public void setIdEmp (Long idEmp) {
        this.idEmp = idEmp;
    }

    public List<EsoCourseModel> getUsedCourses () {
        return usedCourses;
    }

    public void setUsedCourses (List<EsoCourseModel> usedCourses) {
        this.usedCourses = usedCourses;
    }

    public List<GroupModel> getGroupList () {
        return groupList;
    }

    public void setGroupList (List<GroupModel> groupList) {
        this.groupList = groupList;
    }
}
