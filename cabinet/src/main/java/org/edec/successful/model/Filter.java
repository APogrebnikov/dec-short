package org.edec.successful.model;

import lombok.Data;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.GovFinancedConst;

import java.util.Date;

@Data
public class Filter {
    private Long idInstitute;
    private Long idSemester;
    private Long idDepartment;
    private FormOfStudy fos;
    private GovFinancedConst govFin;
    private String levels;
    private String groupName;
    private Date lastDate;
    private String courses;
    private Long idChair;
    private Boolean usePractic = false;
    private Boolean useFiz = false;
    private Boolean useAcadem = false;
    private Boolean onlyPassWeek = false;
}
