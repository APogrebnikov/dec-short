package org.edec.utility.constants;

/**
 * @author Max Dimukhametov
 */
public enum FormOfStudyConst {
    ALL("все", 3), FULL_TIME("очная", 1), EXTRAMURAL("заочная", 2);

    private String name;
    private int type;

    public String getName () {
        return name;
    }

    public int getType () {
        return type;
    }

    FormOfStudyConst (String name, int type) {
        this.name = name;
        this.type = type;
    }

    public static FormOfStudyConst getFormOfStudyByType (int type) {
        for (FormOfStudyConst formOfStudy : FormOfStudyConst.values()) {
            if (formOfStudy.getType() - type == 0) {
                return formOfStudy;
            }
        }
        return null;
    }

    public static FormOfStudyConst getFormOfStudyByName (String name) {
        for (FormOfStudyConst formOfStudy : FormOfStudyConst.values()) {
            if (formOfStudy.getName().equals(name)) {
                return formOfStudy;
            }
        }
        return null;
    }

    public static String getNameByType (int type) {
        for (FormOfStudyConst formOfStudy : FormOfStudyConst.values()) {
            if (formOfStudy.getType() - type == 0) {
                return formOfStudy.getName();
            }
        }
        return "";
    }

    public static int getTypeByName (String name) {
        for (FormOfStudyConst formOfStudy : FormOfStudyConst.values()) {
            if (formOfStudy.getName().equals(name)) {
                return formOfStudy.getType();
            }
        }
        return 0;
    }
}