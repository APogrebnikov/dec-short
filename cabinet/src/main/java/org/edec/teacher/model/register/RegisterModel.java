package org.edec.teacher.model.register;

import lombok.Data;
import org.edec.utility.DateUtility;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RegisterType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class RegisterModel implements Serializable {

    private boolean signPermissionAfterCompletionDate = false;
    private FormOfControlConst foc;
    private RegisterType registerType;
    // диф зачет или не диф зачет
    private Integer type;
    private Integer course, synchStatus;
    private Double hoursCount;
    private Long idRegisterESO, idRegisterMine, idSemester;
    private String registerNumber, registerURL, signatoryTutor, certNumber, thumbPrint, subjectName, semesterName, groupName;
    private Date signDate, completionDate;
    // Даты пересдачи
    private Date startDate, finishDate, secondSignDateBegin, secondSignDateEnd;
    private Boolean isCanceled;
    private List<RegisterRowModel> listRegisterRow;

    private Date dateOfBeginSession, dateOfEndSession, dateOfBeginPassweek, dateOfEndPassweek;
    private Date beginDateMainRegister, endDateMainRegister;

    private String semesterStr;

    public RegisterModel() {
        listRegisterRow = new ArrayList<>();
    }

    public boolean isRegisterSigned() {
        if (registerURL != null && !registerURL.equals("")) {
            return true;
        }

        for (RegisterRowModel rating : listRegisterRow) {
            if (rating.getRetakeCount() != null && rating.getRetakeCount() > 0) {
                return true;
            }

            // TODO если количество положительных retakeCount не совпадает с количеством студентов в ведомости - лог об ошибке!
        }

        return false;
    }

    /**
     * Проверка на просроченность по правилу, ведомость доступна три дня начиная с даты конца пересдачи
     * с учетом выходных (воскресенье)
     * @return вышел ли срок ведомости
     */
    public boolean isRetakeOutOfDate() {

        Date today = new Date();

        if (finishDate == null) {
            return false;
        }

        return DateUtility.isAfterDate(today, finishDate) && !DateUtility.isDayBelongsToPeriod(today, finishDate, 4);
    }

    public boolean canBeSigned() {

        Date today = new Date();

        //Если дата ещё не наступила, то подписывать нельзя!
        if (completionDate == null || DateUtility.isBeforeDate(today, completionDate)) {
            return false;
        }

        //Специальное разрешение сотрудников УОО, то что можно подписать после срока проведения ведомости
        if (signPermissionAfterCompletionDate) {
            return true;
        }

        if (registerType == RegisterType.MAIN) {
            //Подписывать можно в день сдачи или на следующий день
                //Если была суббота, то можно подписать в понедельник
            return DateUtility.isBetweenDate(today, completionDate, DateUtility.getNextDateExcludeWeekend(completionDate));
        } else if (registerType == RegisterType.INDIVIDUAL_RETAKE) {
            return DateUtility.isBetweenDate(today, signDate, finishDate);
        }

        return false;
    }

    public String getErrorIfSignRegisterNotAllowed() {

        if (certNumber != null) {
            return "";
        }

        if (completionDate == null) {
            return "Дата проведения не установлена!";
        }

        Date today = new Date();

        if (DateUtility.isBeforeDate(today, completionDate)) {
            return "Срок сдачи предмета ещё не подошёл";
        } else if (beginDateMainRegister != null) {
            if (DateUtility.isBeforeDate(today, completionDate)) {
                return "Срок сдачи предмета ещё не подошёл";
            }
        }

        boolean badDateInMainRegister = registerType == RegisterType.MAIN
                && (!DateUtility.isDayBelongsToPeriod(today, completionDate, 2));

        if (beginDateMainRegister != null && endDateMainRegister != null ){
           if (registerType == RegisterType.MAIN && DateUtility.isNotBetweenDate(today, beginDateMainRegister, endDateMainRegister)) {
               badDateInMainRegister = true;
           } else {
               badDateInMainRegister = false;
           }
        }
        if (badDateInMainRegister) {
            return "Срок подписания ведомости истек!";
        }

        if (registerType == RegisterType.INDIVIDUAL_RETAKE) {
            if (signDate == null || finishDate == null) {
                return "Не заполнена дата начала и дата окончания пересдачи. Обратитесь в УОО";
            }
            if (DateUtility.isNotBetweenDate(today, signDate, finishDate)) {
                return "Срок подписания ведомости истек!";
            }
        }

        return "";
    }

    /**
     * Проверка доступна ли преподавателю возможность подписать просроченную ведомость
     * (механизм открытия просроченной ведомости сотрудником УОО)
     */
    public boolean isSecondSignPeriodAvailable(){
        Date today = new Date();

        if (secondSignDateBegin == null || secondSignDateEnd == null) {
            return false;
        }

        return DateUtility.isBetweenDate(today, secondSignDateBegin, secondSignDateEnd);
    }

    public String getFocStr () {
        return foc.getName();
    }

    public String getRegisterTypeStr() {
        return registerType.getName();
    }


}
