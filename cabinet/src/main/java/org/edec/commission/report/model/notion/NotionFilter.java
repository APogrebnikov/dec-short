package org.edec.commission.report.model.notion;

import lombok.Builder;
import lombok.Getter;
import org.edec.commission.model.PeriodCommissionModel;

@Builder @Getter
public class NotionFilter {

    private Integer formOfStudy;
    private Long idInst, idSemester;
    /**
     * Может быть название предмета, фио, группа
     */
    private String commonFilter;

    private PeriodCommissionModel period;
}
