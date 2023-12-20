package org.edec.newOrder.model.addStudent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anton Skripachev
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchStudentModel {
    private String surname, name, patronymic, groupname;
    private Integer semesterNumber;
    private Boolean governmentFinanced;
    private List<Object> studentParams = new ArrayList<>();
    private Long id, idMine;

    public SearchStudentModel (String surname, String name, String patronymic, String groupname, long id, Integer semesterNumber, Long idMine) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.groupname = groupname;
        this.id = id;
        this.semesterNumber = semesterNumber;
        this.idMine = idMine;
    }

    public String getFio() {
        return surname + " " + name + " " + patronymic;
    }
}
