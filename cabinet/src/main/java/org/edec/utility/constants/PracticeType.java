package org.edec.utility.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum PracticeType {

    STUDY(1, "Учебная практика", "Учебная", "Учебной практике"),
    MANUFACTURE(2, "Производственная практика", "Производственная", "Производственной практике");

    private Integer value;
    private String name;
    private String shortname;
    private String dativeName;

    public static PracticeType getPracticeTypeByValue(Integer value) {
        return Stream.of(PracticeType.values())
                .filter(practiceType -> practiceType.getValue().equals(value))
                .findFirst().orElse(null);
    }
}
