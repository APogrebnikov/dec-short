package org.edec.teacher.model.register;

import lombok.Data;
import org.edec.teacher.model.correctRequest.CorrectRequestModel;
import org.edec.utility.DateUtility;

import java.util.Date;

@Data
public class RegisterRowModel {
    private Long idSSS, idSRH, idSR, idCurrentDicGroup, idDicGroup;
    private Integer mark, currentMark, retakeCount;
    private Date changeDateTime, secondSignDateBegin, secondSignDateEnd;
    // Тема курсовой работы или курсового проекта
    private String theme;
    private String studentFullName, recordbookNumber;
    private Boolean deducted, academicLeave, notActual, isOutOfDate, isSecondSignPeriodAvailable;
    private CorrectRequestModel curCorrectRequest = null;
    private Integer visitcount,skipcount;
    private Long esogradecurrent, esogrademax;
}
