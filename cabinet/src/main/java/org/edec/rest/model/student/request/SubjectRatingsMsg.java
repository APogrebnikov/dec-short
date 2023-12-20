package org.edec.rest.model.student.request;

import lombok.Data;
import org.edec.rest.model.BaseUserMsg;

@Data
public class SubjectRatingsMsg extends BaseUserMsg {
    private Long idLGSS;
    /**
     * @see org.edec.utility.constants.FormOfControlConst
     */
    private int foc;
    /**
     * @see org.edec.utility.constants.RegisterType
     */
    private int type;
}
