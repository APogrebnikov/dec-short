package org.edec.utility.report.model.register;

import lombok.Data;
import org.edec.utility.constants.RatingConst;

import java.util.Date;

@Data
public class CorrectRatingJasperModel
{
    private String certnumber;
    private String signatorytutor;
    private String signdate;
    private Date signdateVal;
    private String department;
    private String mainstring1;
    private String mainstring2;
    private String registerNumber;
    private String registerDate;
    private Date registerDateVal;
    private String group;
    private String fio;
    private String subject;
    private String oldRating;
    private String newRating;
    private int oldRatingVal;

    public CorrectRatingJasperModel()
    {
    }

    public String getMainstring1() {
        StringBuilder builder = new StringBuilder();
        builder.append("\tПрошу разрешить внести изменения в ведомость ");
        builder.append(registerNumber);
        builder.append(" от ");
        builder.append(registerDate);
        builder.append(" в отношении студента группы ");
        builder.append(group);
        builder.append(" ");
        builder.append(fio);
        builder.append(".");
        mainstring1 = builder.toString();
        return mainstring1;
    }

    public String getMainstring2() {
        StringBuilder builder = new StringBuilder();
        builder.append("\tИ исправить оценку по курсу ");
        builder.append(subject);
        builder.append(" с «");
        builder.append(oldRating);
        builder.append("» на «");
        builder.append(newRating);
        builder.append("» в связи с допущенной мной ошибкой по невнимательности.");
        mainstring2 = builder.toString();
        return mainstring2;
    }
}