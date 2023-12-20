package org.edec.scholarship.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.edec.utility.constants.ScholarshipTypeConst;

import java.util.Date;

@Data
@NoArgsConstructor
public class ScholarshipModel {
    private ScholarshipTypeConst type;
    private Date dateFrom;
    private Date dateTo;
    private Long idStudentCard;

    public ScholarshipModel(ScholarshipTypeConst scholarshipTypeConst){
        this.type = scholarshipTypeConst;
    }
}
