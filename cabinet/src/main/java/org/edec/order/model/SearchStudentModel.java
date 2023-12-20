package org.edec.order.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SearchStudentModel {
    private String surname;
    private String name;
    private String patronymic;
    private String groupname;

    long id;

    private Boolean governmentFinanced;
}
