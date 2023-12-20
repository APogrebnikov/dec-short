package org.edec.utility.report.model.commission.dao;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class CommissionScheduleEso {
    private Date datecommission;

    private String classroom;
    private String fulltitle;
    private String subjectname;
    private String groupname;
    private String teachers;
    private Boolean isIndividual;

    private Long idRegisterComission;
    // Заглушка для этапа группировки
    private List<String> groups = new ArrayList<>();

    public CommissionScheduleEso () {
    }
}
