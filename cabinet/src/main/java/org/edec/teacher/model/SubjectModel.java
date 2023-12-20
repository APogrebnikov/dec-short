package org.edec.teacher.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class SubjectModel {
    private Integer formofcontrol;
    private Integer typePass;

    private String subjectname;

    private List<GroupModel> groups = new ArrayList<>();

    private SemesterModel semester;
}
