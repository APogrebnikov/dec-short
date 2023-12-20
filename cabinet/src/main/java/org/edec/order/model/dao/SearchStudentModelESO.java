package org.edec.order.model.dao;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SearchStudentModelESO {
    String surname;
    String name;
    String patronymic;
    String groupname;

    Long idSSS;
}
