package org.edec.newOrder.model.dao;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by antonskripacev on 03.01.17.
 */
@Getter
@Setter
public class SearchStudentModelESO {
    private String surname, name, patronymic, nameGroup;
    private Integer numberSemester;
    private Long idSSS;
    private Long idMine;
}
