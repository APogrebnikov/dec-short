package org.edec.factSheet.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.model.HumanfaceModel;

@Getter
@Setter
@NoArgsConstructor
public class FactSheetAddModel extends HumanfaceModel {

    private Boolean getNotification, current, deducted, academicleave;
    private String fullName, groupName, recordBook;
    private Long idHumanface;
}
