package org.edec.newMine.registers.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegistersSubjectsModel {
    private String  groupname,  subjectname, codeSubject;
    private Integer semesterNumber;
    private Long otherIdSubject, otherIdGroup, idCurriculumMine, idMineRegister;
    private int typeMineRegister;

    public String getFocByRegisterType() {
        String foc = "";
        if (typeMineRegister != 0) {
            switch (typeMineRegister) {
                case 1 : return foc = "Экзамен";
                case 2 : return foc = "Зачет";
                case 3 : return foc = "КР";
                case 4 : return foc = "КП";
                case 6 : return foc = "Практика";
            }
        }
        return foc;
    }
}
