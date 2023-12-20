package org.edec.newOrder.model.editOrder;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class StudentModel {
    private String fio;
    private String recordnumber;
    private String numberPrevOrder;
    private String foundation;
    private String additional = "{}";

    private Date firstDate;
    private Date secondDate;
    private Date thirdDate;
    private Date datePrevOrder;

    private long id;
    private Long idMine;


    private boolean selected;

    public StudentModel (long id) {
        this.id = id;
    }

    public StudentModel () {
    }

    /**
     * используется при создании переводного
     **/
    private boolean isProlongatedManualy;
    private Date dateProlongation;
    private String groupname;

    /**
     * используется при создании академической повышенной
     **/
    private String nomination;

    /**
     * Данные для получения списков студентов на назначении академ стипендии а также соц. повышенной
     */
    private int oldSessionResult;
    private int newSessionResult;
    private int course;

    private long idOrderRule;
}
