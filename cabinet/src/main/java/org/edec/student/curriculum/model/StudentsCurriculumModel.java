package org.edec.student.curriculum.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StudentsCurriculumModel {
    private String filename, groupnmae, speciallity, codeSpeciality, year;
    private List<SubjectCurriculumModel> subjects = new ArrayList<>();

}
