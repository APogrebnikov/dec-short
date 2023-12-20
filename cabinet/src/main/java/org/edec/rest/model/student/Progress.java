package org.edec.rest.model.student;

import lombok.Data;

import java.util.Date;

@Data
public class Progress {
    private String login;
    private String subjectName;
    private Long esogradecurrent, esogrademax, progress;
    private Long idLGSS;
}
