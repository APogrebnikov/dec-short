package org.edec.teacher.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CompletionCommESOmodel {
    private Boolean curSem;

    private Date dateOfBeginSY, dateOfCommission;

    private Integer formOfControl, formofstudy, season, type, semesternumber;

    private Long idRC, idReg, idSem;

    private Double hoursCount;

    private String certnumber, course, institute, regNumber, semesterStr, signatorytutor, subjectName, classroom, timeCom;
}
