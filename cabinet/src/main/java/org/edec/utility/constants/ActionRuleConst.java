package org.edec.utility.constants;

public enum ActionRuleConst {

    EXECUTOR(0, "Исполнитель"), APPROVER(1, "Утверждающее лицо"), CONCORDANT(2, "Согласующее лицо"), NOTIFICATION(3, "Рассылка");

    private Integer value;
    private String name;

    public Integer getValue () {
        return value;
    }

    public String getName () {
        return name;
    }

    ActionRuleConst (Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static ActionRuleConst getName (Integer value) {
        for (ActionRuleConst actionRule : ActionRuleConst.values()) {
            if (actionRule.getValue().equals(value)) {
                return actionRule;
            }
        }
        return null;
    }

    public static ActionRuleConst getValue (String name) {
        for (ActionRuleConst actionRule : ActionRuleConst.values()) {
            if (actionRule.getName().equals(name)) {
                return actionRule;
            }
        }
        return null;
    }
}
