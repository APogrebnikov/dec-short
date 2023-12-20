package org.edec.utility.constants;

public enum ReferenceType {

    INDIGENT(1, "Малоимущий"), ORPHAN(2, "Сирота"), INVALID(3, "Инвалид"), VETERAN(4,"Ветеран");

    private Integer value;
    private String name;

    public Integer getValue () {
        return value;
    }

    public String getName () {
        return name;
    }

    ReferenceType (Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static ReferenceType getName (Integer value) {
        for (ReferenceType refType : ReferenceType.values()) {
            if (refType.getValue().equals(value)) {
                return refType;
            }
        }
        return null;
    }

    public static ReferenceType getValue (String name) {
        for (ReferenceType refType : ReferenceType.values()) {
            if (refType.getName().equals(name)) {
                return refType;
            }
        }
        return null;
    }
}
