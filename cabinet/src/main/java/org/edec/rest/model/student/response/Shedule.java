package org.edec.rest.model.student.response;

import lombok.Data;

@Data
public class Shedule {
    Integer pos;
    Long idLGSS;
    String subjectName;
    Boolean lesson;
}
