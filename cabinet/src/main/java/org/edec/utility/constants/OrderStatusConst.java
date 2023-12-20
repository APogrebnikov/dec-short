package org.edec.utility.constants;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum OrderStatusConst {

    CREATED("Создан", 1L),
    APPROVAL("На согласовании", 2L),
    AGREED("Утвержден", 3L),
    REVISION("Отправлен на доработку", 4L),
    CANCELED("Отменен", 5L),
    SYNCHED("Синхронизирован", 6L),
    EXPORTED("Экспортирован в Шахты", 7L);

    private String name;
    private Long id;

    public static OrderStatusConst getOrderStatusConstById(Long id) {
        return Stream.of(OrderStatusConst.values())
                .filter(orderStatusConst -> orderStatusConst.getId().equals(id))
                .findFirst().orElse(null);
    }

    public static OrderStatusConst getOrderStatusConstByName(String name) {
        return Stream.of(OrderStatusConst.values())
                .filter(orderStatusConst -> orderStatusConst.getName().equals(name))
                .findFirst().orElse(null);
    }
}
