package org.edec.utility.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum ConditionOfEducation {

    GOVERNMENT_FINANCED("ОО", "Общие основания"),
    TRUST_AGREEMENT("ЦН", "Целевой прием"),
    PAID_EDUCATION("СН", "Сверхплановый набор");

    private String shortName;
    private String fullName;

    @Override
    public String toString() {
        return fullName;
    }
}
