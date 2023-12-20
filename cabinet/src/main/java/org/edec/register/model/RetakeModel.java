package org.edec.register.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Anton Skripachev
 */
@Getter
@Setter
@NoArgsConstructor
public class RetakeModel {
    private String fio;
    private Long idSSS, idSR, idCurDicGroup, idSemester;
    private Boolean deductedCurSem, academicLeaveCurSem, transferedStudent;
    private Boolean listenerCurSem, transferedStudentCurSem;
    private Integer passRating, examRating, cpRating, cwRating, practicRating, type, newRating, typeRetake; //type 0 - зачет, 1 - экзамен
    private Integer esoPassRating, esoExamRating, esoCpRating, esoCwRating;
    private Date beginDate, finishDate;
    private List<StudentModel> students = new ArrayList<>();

    private List<SessionRatingHistoryModel> listSRH = new ArrayList<>();

    public boolean isRetakeOutOfDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);

        if(finishDate != null && finishDate.before(cal.getTime())) {
            return true;
        }

        return false;
    }
}
