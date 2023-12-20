package org.edec.successful.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.successful.ReportMarksEnum;

@Getter
@Setter
public class StudentReportModel {
    private String fio;
    private String recordbook;

    private ReportMarksEnum marksType;
    private int countDebts;
}
