package org.edec.secretaryChair.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
public class CommissionModel {
    private boolean isPhysicalCulture;

    private Long id, idRegister, idChair, idSubject;

    private Date dateBegin, dateEnd, commissionDate;

    private Integer formOfControl, status, statusNotification;

    private String subjectName, semesterStr, classroom;

    private boolean checkStatus;
}
