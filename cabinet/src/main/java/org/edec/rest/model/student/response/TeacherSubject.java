package org.edec.rest.model.student.response;

import lombok.Data;
import org.edec.rest.model.BaseUserMsg;
import org.edec.utility.constants.FormOfControlConst;
import org.json.JSONObject;

import java.util.Date;

@Data
public class TeacherSubject extends BaseUserMsg implements Cloneable {
    private Long idLGSS;
    private Long idSub;
    private String groupname;
    private String subjectname;
    private Boolean isExam;
    private Boolean isPass;
    private Boolean isCP;
    private Boolean isCW;
    private Boolean isPractic;
    private String certNumber;
    private Date signDate;
    private Long idRegister;
    /**
     * -1 - не подписана
     * 1 - подписана нормально
     * 2 - подписана без сертификата
     */
    private int isSign;

    private Date completionDate;
    private Integer passType;

    private String semesterStr;
    private Integer foc;
    private String focStr;

    public int getIsSign() {
        if (this.certNumber != null && this.signDate != null)
        {
            return 1;
        } else if (this.certNumber == null && this.signDate != null){
            return 2;
        } else {
            return -1;
        }

    }

    public Object getCompletionDate() {
        if (completionDate == null) {
            return JSONObject.NULL;
        }
        return completionDate;
    }

    @Override
    public TeacherSubject clone() throws CloneNotSupportedException {
        return (TeacherSubject) super.clone();
    }

    public String getFocStr () {
        return FormOfControlConst.getName(this.foc).getName();
    }
}
