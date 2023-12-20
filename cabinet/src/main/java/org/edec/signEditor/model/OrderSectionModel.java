package org.edec.signEditor.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OrderSectionModel {

    private String description;
    private Integer layout;
    private  String name;
    private String foundation;
    private Long otherdbid;
    private Long idOrderRule;
    private Long idOrderSection;
}
