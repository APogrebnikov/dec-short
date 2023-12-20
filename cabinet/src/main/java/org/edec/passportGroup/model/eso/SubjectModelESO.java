package org.edec.passportGroup.model.eso;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by antonskripacev on 09.04.17.
 */
@Getter
@Setter
public class SubjectModelESO {
    private Long idSubject, idLgss;
    private Double hoursCount;
    private Boolean exam, pass, cp, cw, practic;
    private String subjectName;
}
