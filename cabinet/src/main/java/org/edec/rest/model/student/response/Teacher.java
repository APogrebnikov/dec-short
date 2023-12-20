package org.edec.rest.model.student.response;

import lombok.Data;

import java.util.Date;

@Data
public class Teacher {
    private Long idEmp;
    private Long idHum;
    private String surname;
    private String name;
    private String patronymic;
    private String email;
    private String department;
    private String rolename;
}
