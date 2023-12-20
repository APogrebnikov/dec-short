package org.edec.register.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RegisterReportModelEso {
    private String registerNumber, groupName, subjectName, signatoryTutor, fullTitle, foc;
    private Date signDate, examinationDate;
}
