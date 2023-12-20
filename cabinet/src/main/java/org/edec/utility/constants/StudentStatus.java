package org.edec.utility.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter @AllArgsConstructor
public enum StudentStatus {

    ACADEMIC_LEAVE(-1, "Академ. отпуск", "академ."),
    STUDENT(1, "Учащийся", "учащ."),
    DEDUCTED(3, "Отчислен", "отч."),
    EDUCATION_COMPLETED(4, "Завершил обучение", "завершил");

    private Integer mineValue;
    private String name;
    private String shortName;

    public static StudentStatus getStatusByValue(Integer value) {
        return Arrays.stream(StudentStatus.values())
                .filter(status -> status.getMineValue().equals(value))
                .findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return name;
    }
}
