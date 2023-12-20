package org.edec.rest.model.student;

import lombok.Data;

import java.util.Date;

@Data
public class ProgressDetail {
        private String subjectName;
        private Long avg, max, min;
        private Long idLGSS;
        private Date dateSync;
        private Integer week;
}
