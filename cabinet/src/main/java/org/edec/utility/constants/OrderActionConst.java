package org.edec.utility.constants;

public enum OrderActionConst {

    NOMINATE_ACADEM(1L, "Назначение академ. стипендии"),
    CANCEL_ACADEM(2L, "Отмена академ. стипендии"),
    NOMINATE_SOC(3L, "Назначение соц. стипендии"),
    NOMINATE_GREAT_SOC(4L, "Назначение соц. повыш. стипендии"),
    SET_DEBT_DATE(5L, "Установление сроков ЛАЗ"),
    UP_DEBT_DATE(6L, "Продление сроков ЛАЗ"),
    RECOVERY(7L, "Востановление");

    private Long value;
    private String name;

    public Long getValue () {
        return value;
    }

    public String getName () {
        return name;
    }

    OrderActionConst (Long value, String name) {
        this.value = value;
        this.name = name;
    }

    public static OrderActionConst getName (Long value) {
        for (OrderActionConst orderAction : OrderActionConst.values()) {
            if (orderAction.getValue().equals(value)) {
                return orderAction;
            }
        }
        return null;
    }

    public static OrderActionConst getValue (String name) {
        for (OrderActionConst orderAction : OrderActionConst.values()) {
            if (orderAction.getName().equals(name)) {
                return orderAction;
            }
        }
        return null;
    }
}
