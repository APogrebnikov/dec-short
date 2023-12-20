package org.edec.studentPassport.model.filter;

import lombok.Builder;
import lombok.Getter;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.constants.FormOfStudy;

@Builder
@Getter
public class StudentPassportFilter {

    private boolean onlyDebtors;

    private String fio, recordBook, groupName;

    private FormOfStudy formOfStudy;
    private InstituteModel inst;
}
