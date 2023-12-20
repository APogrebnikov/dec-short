package org.edec.utility.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum OrderTypeConst {

    ACADEMIC("Академическая стипендия", 1L),
    TRANSFER("Переводной", 2L),
    DEDUCTION("Отчисление", 3L),
    ACADEMIC_INCREASED("Повышенная академ. стипендия", 4L),
    SOCIAL("Соц. стипендия", 5L),
    SOCIAL_INCREASED("Соц. повышенная стипендия", 6L),
    SET_ELIMINATION_DEBTS("Установление сроков ЛАЗ", 7L),
    MATERIAL_SUPPORT("Материальная поддержка", 8L);

    private String name;
    private Long type;

    public static OrderTypeConst getByType(Long type) {
        return Arrays.stream(OrderTypeConst.values())
                .filter(orderType -> orderType.getType().equals(type))
                .findFirst().orElse(null);
    }

}
