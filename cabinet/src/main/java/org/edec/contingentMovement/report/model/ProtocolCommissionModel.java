package org.edec.contingentMovement.report.model;

import lombok.Data;
import org.edec.contingentMovement.model.ResitRatingModel;

import java.util.Date;
import java.util.List;

@Data
public class ProtocolCommissionModel {
    private Date dateCommission, dateResit;

    private Integer numberProtocol, selectedQualification, oldQualification, typeProtocol;

    private String chairman, fioStudent, typeStudentMove, templateFioStudent, institute, recordbook, direction;
    private String recordbookDate, typeMovement, directionRestore;
    private String agenda;

    private List<ResitRatingModel> resitSubjects, resitSubjectSecondParagraph;
    private List<String> сommissionMembers;
}
