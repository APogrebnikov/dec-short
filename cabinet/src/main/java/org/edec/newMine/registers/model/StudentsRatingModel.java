package org.edec.newMine.registers.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class StudentsRatingModel {
    private String family, name, patronymic, schoolyear, subjectName;
    private Date mainDateOfPass, dateRetake1, dateRetake2, dateRetake3;
    private Long idStudentMine, idMineRegister;
    private int semesternumber, course, hoursCount, typeRegister;
    private String typePractic;
    private int mainRating, total, ratingRetake1, ratingRetake2, ratingRetake3;
    private Double creditUnit;

    public String getFioStudent() {
        String fio = "";
        if (family != null &&  name != null && patronymic != null) {
            fio = family + " " + name + " " + patronymic;
        }
        return fio;
    }

}
