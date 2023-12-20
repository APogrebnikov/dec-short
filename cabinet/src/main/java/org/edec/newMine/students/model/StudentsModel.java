package org.edec.newMine.students.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class StudentsModel {
    private Date birthday;

    private Long idHum;
    private Long idStudCard;
    private Long idStudCardMine;
    private Long idSSS;

    /**
     * Условия обучения
     */
    private Integer condOfEducation;
    private Integer semester;
    private Integer sex;
    private Integer status;

    private String family;
    private String groupname;
    private String name;
    private String patronymic;
    private String recordbook;
    private String hash;

    private StudentsModel otherStudentModel;

    public String getFio () {
        return family + " " + name + " " + patronymic;
    }

    public String getCondOfEducationStr () {
        String condOfEducationStr = "";
        if (condOfEducation == 1) {
            condOfEducationStr = "Бюджетник";
        } else if (condOfEducation == 2) {
            condOfEducationStr = "Целевик";
        } else if (condOfEducation == 3) {
            condOfEducationStr = "Договорник";
        }
        return condOfEducationStr;
    }

}
