package org.edec.utility.constants;

public enum QualificationConst {

    SPECIALIST("Инженер", 1),
    BACHELOR("Бакалавр", 2),
    MASTER("Магистр", 3);

    private String name;
    private Integer value;

    public String getName () {
        return name;
    }

    public Integer getValue () {
        return value;
    }

    QualificationConst (String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static QualificationConst getByType (Integer value) {
        for (QualificationConst qualificationConst : QualificationConst.values()) {
            if (qualificationConst.getValue().equals(value)) {
                return qualificationConst;
            }
        }
        return null;
    }

    public static QualificationConst getQualificationByName (String name) {
        for (QualificationConst qualificationConst : QualificationConst.values()) {
            if (qualificationConst.getName().equals(name)) {
                return qualificationConst;
            }
        }
        return null;
    }

    public static QualificationConst getQualificationByValue (Integer value) {
        for (QualificationConst qualificationConst : QualificationConst.values()) {
            if (qualificationConst.getValue() == value) {
                return qualificationConst;
            }
        }
        return null;
    }
}
