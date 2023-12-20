package org.edec.register.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
public class SubjectModel {

    private Long idSubject, idLgss, idGroup;
    private String groupName, subjectName;
    private HashSet<String> teachers;
    private Integer foc, type, course;

}
