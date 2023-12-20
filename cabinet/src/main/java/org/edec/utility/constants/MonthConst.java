package org.edec.utility.constants;

/**
 * Перечисление используется для отображания даты при группировке сообщений
 */
public enum MonthConst {

    JANUARY(0, "Января"), FEBRUARY(1, "Февраля"), MARCH(2, "Марта"), APRIL(3, "Апреля"), MAY(4, "Мая"), JUNE(5, "Июня"),
    JULY(6,"Июля"),
    AUGUST(7, "Августа"), SEPTEMBER(8, "Сентября"), OCTOBER(9, "Октября"), NOVEMBER(10, "Ноября"), DECEMBER(11, "Декабря");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    MonthConst(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getNameByValue(int value){
        for(MonthConst monthConst: MonthConst.values()){
            if(monthConst.getValue() - value == 0){
                return monthConst.getName();
            }
        }

        return "";
    }
}
