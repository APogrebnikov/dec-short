package org.edec.register.model;

import lombok.Data;

import java.util.Date;

@Data
public class SimpleRatingModel {
    private Long idSRH;
    private Integer newRating;
    private Date signDate;
    private Integer retakeCount;
}
