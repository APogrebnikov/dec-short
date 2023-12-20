package org.edec.report.debtorsReport.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DebtorContactModel {
    private Long idStudentMine;
    private String phone, email, fio;
}
