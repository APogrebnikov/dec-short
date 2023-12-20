package org.edec.factSheet.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum FactSheetStatusEnum {

    CREATED("Создан", 0),
    APPROVED("Одобрен", 1),
    CANCELED("Отменен", 2);

    private String statusName;
    private int statusId;

    @Override
    public String toString() {
        return this.statusName;
    }

    public static FactSheetStatusEnum getEnumById(int statusId) {
        return Stream.of(FactSheetStatusEnum.values())
                .filter(statusEnum -> statusEnum.getStatusId() == statusId)
                .findFirst().orElse(null);
    }

    public static FactSheetStatusEnum getEnumByStatusName(String name) {
        return Stream.of(FactSheetStatusEnum.values())
                .filter(statusEnum -> statusEnum.getStatusName().equals(name))
                .findFirst().orElse(null);
    }

    public static List<FactSheetStatusEnum> getEnumListByContainsTextInStatusName(String text) {
        return Stream.of(FactSheetStatusEnum.values())
                .filter(statusEnum -> statusEnum.getStatusName().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

}
