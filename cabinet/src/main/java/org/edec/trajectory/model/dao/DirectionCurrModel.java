package org.edec.trajectory.model.dao;

import lombok.Data;


@Data
public class DirectionCurrModel {
    private Boolean currentYear;

    private Long idDirection;
    private String codeDirection;
    private String titleDirection;

    //Учебный план
    private Long idCurriculum;

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
