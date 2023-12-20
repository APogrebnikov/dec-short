package org.edec.teacher.model.commission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentModel {
    private Integer rating;

    private Long idSRH;

    private String fio;
    private String groupname;
    private String ratingStr;
    private Boolean checkCommision;

    public StudentModel () {
    }

    public String getShortFio(){
        String[] fio = getFio().split(" ");
        return fio[0] + " " + fio[1].substring(0, 1) + "." + (fio.length > 2 ? " "+fio[2].substring(0, 1) + ".": "");
    }
}
