package org.edec.register.model.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RegisterModel {
    private String registerNumber, examinationDate, signDate, group, subject, formOfCtrl, tutor, chair;
}
