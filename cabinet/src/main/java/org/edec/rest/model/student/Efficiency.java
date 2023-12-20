package org.edec.rest.model.student;

import lombok.Data;

@Data
public class Efficiency {
    private Long idSSS;
    private Long progress;
    private Integer week;
    private Long res;
    private Integer retake;
    private Integer rating;
    private Boolean pass;
    private Boolean exam;
    // Вспомогательное поле для поиска
    public boolean isFind = false;
}
