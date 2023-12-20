package org.edec.passportGroup.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class RegisterCurProgressGroupModel {
    private String groupname;

    private List<RegisterCurProgressModel> students = new ArrayList<>();

    public RegisterCurProgressGroupModel()
    {
    }

    public String getGroupname()
    {
        return groupname;
    }

    public void setGroupname(String groupname)
    {
        this.groupname = groupname;
    }

    public List<RegisterCurProgressModel> getStudents()
    {
        return students;
    }

    public void setStudents(List<RegisterCurProgressModel> students)
    {
        this.students = students;
    }
}
