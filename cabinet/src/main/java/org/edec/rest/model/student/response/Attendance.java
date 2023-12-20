package org.edec.rest.model.student.response;

import lombok.Data;

@Data
public class Attendance {
    private String family;
    private String patronymic;
    private String name;
    private Long idSSS;
    private Integer attend = 0;
    private Long idAttendance;
}
