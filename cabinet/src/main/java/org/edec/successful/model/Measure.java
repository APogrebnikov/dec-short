package org.edec.successful.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Measure {
    COURSE("Курс"),
    GROUP("Группа"),
    TEACH_CHAIR("Кафедра (преп.)"),
    OUT_CHAIR("Кафедра (вып.)"),
    SUBJECT("Предмет");

    String name;
}
