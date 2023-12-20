package org.edec.efficiency.model;

/**
 * @author Max Dimukhametov
 */
public enum StatusEfficiency {
    CREATED(0, "Не в работе"), CONFIRM(1, "Принят в работу"), COMPLETED(2, "Завершен");

    private Integer status;
    private String name;

    public String getName () {
        return name;
    }

    public Integer getStatus () {
        return status;
    }

    StatusEfficiency (Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public static StatusEfficiency getStatusEfficiencyByInt (Integer intValue) {
        for (StatusEfficiency statusEfficiency : StatusEfficiency.values()) {
            if (statusEfficiency.status == intValue) {
                return statusEfficiency;
            }
        }
        return null;
    }
}
