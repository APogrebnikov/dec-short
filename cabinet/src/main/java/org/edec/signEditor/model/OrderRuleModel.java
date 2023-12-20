package org.edec.signEditor.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class OrderRuleModel {

private String name;
private String description;
private String headDescription;
private Long idOrderType;
private Long idInstitute;
private Long formOfControl;
private boolean isAutomatic;
private Long idOrderRule;
}
