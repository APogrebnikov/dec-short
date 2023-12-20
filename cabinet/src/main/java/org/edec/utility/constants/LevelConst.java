package org.edec.utility.constants;


public enum LevelConst {
    ALL("Все", null), SPEC("Специалисты", 1), BACH("Бакалавры", 2), MAGISTR("Магистры", 3);

    private String name;
    private Integer type;

    public String getName () {
        return name;
    }

    public Integer getType () {
        return type;
    }

    LevelConst (String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static String getNameByType (Integer type) {
        for (LevelConst govFinancedConst : LevelConst.values()) {
            if (govFinancedConst.getType() - type == 0) {
                return govFinancedConst.getName();
            }
        }
        return "";
    }

    public static Integer getTypeByName (String name) {
        for (LevelConst govFinancedConst : LevelConst.values()) {
            if (govFinancedConst.getName().equals(name)) {
                return govFinancedConst.getType();
            }
        }
        return null;
    }

    public static LevelConst getConstByVal (Integer val) {
        if (val == null) {
            return LevelConst.ALL;
        }
        switch (val) {
            case 1:
                return LevelConst.SPEC;
            case 2:
                return LevelConst.BACH;
            case 3:
                return LevelConst.MAGISTR;
            default:
                return LevelConst.ALL;
        }
    }
}