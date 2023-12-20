package org.edec.signEditor.model;

public class RuleModel {
    private Long idRule;
    private String rule;

    public Long getIdRule () {
        return idRule;
    }

    public void setIdRule (Long idRule) {
        this.idRule = idRule;
    }

    public String getRule () {
        return rule;
    }

    public void setRule (String rule) {
        this.rule = rule;
    }

    @Override
    public boolean equals (Object object) {
        boolean isEqual = false;

        if (object != null && object instanceof RuleModel) {
            if ((this.idRule == ((RuleModel) object).idRule) && (this.rule.equals(((RuleModel) object).rule))) {
                isEqual = true;
            }
        }

        return isEqual;
    }
}
