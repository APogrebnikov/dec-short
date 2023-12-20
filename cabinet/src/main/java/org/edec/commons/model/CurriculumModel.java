package org.edec.commons.model;

import lombok.Data;

/**
 * @author Max Dimukhametov
 */
@Data
public class CurriculumModel {
    private Long id;

    private Boolean currentYear;

    private Double periodOfStudy;

    private Integer distanceType;
    private Integer formOfStudy;
    private Integer generation;
    private Integer qualification;

    private String createdYear;
    private String directionCode;
    private String specialityTitle;
    private String qualificationCode;
    private String planFileName;
    private String programCode;
}
