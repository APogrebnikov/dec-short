package org.edec.commission.report.model.notion;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class NotionStudentModel {

    private Integer course;
    private String family, name, patronymic;
    private String groupName;
    private String speciality, profile;

    private List<NotionStudentDebtModel> debts = new ArrayList<>();

    public String getStudentFio() {
        return family + " " + name + " " + patronymic;
    }
}
