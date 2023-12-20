package org.edec.studentOrder.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentOrderModel {
    private long idStudentSemesterStatus;
    private String family;
    private String name;
    private String patronymic;
    private String groupname;
}
