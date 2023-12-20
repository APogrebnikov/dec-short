package org.edec.teacher.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.utility.constants.FormOfControlConst;
import org.hibernate.type.IntegerType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class RetakeModel {
    private Integer formofcontrol;
    private Integer typePass;
    private SubjectModel subject;
    private GroupModel group;
    private Date dateOfRetake;
    private Date begindate, enddate, secondDateBegin, secondDateEnd;
    private Date signDate;
    private Long idReg;
    private Long idSem;
    private Integer retakeCount;
    private String certnumber;
    private String fioStudent;

    public String getFocStr () {
        return FormOfControlConst.getName(this.formofcontrol).getName();
    }
    public Date getCompletionDate () {return dateOfRetake;}
}
