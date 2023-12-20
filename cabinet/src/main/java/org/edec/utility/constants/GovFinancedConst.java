package org.edec.utility.constants;

/**
 * @author Alex
 */
public enum GovFinancedConst {
    ALL("Все", null), GOV_FINANCED("Бюджет", 1), CONTRACT("Договор", 0);

    private String name;
    private Integer type;

    public String getName () {
        return name;
    }

    public Integer getType () {
        return type;
    }

    GovFinancedConst (String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static String getNameByType (Integer type) {
        for (GovFinancedConst govFinancedConst : GovFinancedConst.values()) {
            if (govFinancedConst.getType() - type == 0) {
                return govFinancedConst.getName();
            }
        }
        return "";
    }

    public static Integer getTypeByName (String name) {
        for (GovFinancedConst govFinancedConst : GovFinancedConst.values()) {
            if (govFinancedConst.getName().equals(name)) {
                return govFinancedConst.getType();
            }
        }
        return null;
    }
}