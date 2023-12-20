package org.edec.passportGroup.model.eso;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by antonskripacev on 09.04.17.
 */
@Getter
@Setter
public class StudentModelESO {

    private Long idSR, idSubject, idSSS, idDicGroup, idCurrentDicGroup, idStudentcard, idSem;
    private String family, name, patronymic, status;
    private Boolean pass, exam, cp, cw, practic;
    private Boolean deducted, academicLeave, listener, governmentFinanced;
    private Integer passRating, esoPassRating, esoExamRating, examRating, CPRating, esoCPRating, CWRating, esoCWRating, practicRating, type;
}
