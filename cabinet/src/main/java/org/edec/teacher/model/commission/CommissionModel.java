package org.edec.teacher.model.commission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.teacher.model.SemesterModel;
import org.edec.utility.constants.FormOfControlConst;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommissionModel {
    private Date dateOfCommission, dateOfBeginSY, comissionDate;

    private Integer formOfControl, type, semesternumber, hoursCount, formofstudy, season, course;
    private  Boolean curSem;

    private Long idRC, idReg, idSem;

    private Double creditUnits, hourCountDouble;

    private String certnumber, chairman;
    private String focStr, classroom, timeCom;
    private String institute, regNumber, semesterStr;
    private String signatorytutor;
    private String subjectName;

    private SemesterModel semester;
    private List<StudentModel> students = new ArrayList<>();
    private List<EmployeeModel> employees = new ArrayList<>();
    private List<EmployeeModel> commissionStaff = new ArrayList<>();

    private boolean checkStatus;

    private String groups;
    private Date signDate;

    public String getFocStr () {
        return FormOfControlConst.getName(this.formOfControl).getName();
    }
    /**
     * Подписан документ
     **/
    public Boolean isSigned () { return certnumber != null;}

    public String getGroups() {
        return groups.replace("{","").replace("}", "");
    }
    public Date getCompletionDate () {return comissionDate;}
    public void setCourse(String course) {
        this.course = Integer.valueOf(course);
    }
    public void setCourse(Integer course) {
        this.course = course;
    }
}
