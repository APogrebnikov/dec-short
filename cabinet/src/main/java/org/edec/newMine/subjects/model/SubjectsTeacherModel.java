package org.edec.newMine.subjects.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubjectsTeacherModel {
    private String family, name, patronymic;
    private Long idWorkload, idTeacherMine, idEmployee;

    public String getFio(){
        return  family + ' ' + name + ' ' + patronymic;
    }
}
