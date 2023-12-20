package org.edec.rest.model.student.request;

import lombok.Data;
import org.edec.rest.model.BaseUserMsg;

@Data
public class TeacherRetakeMsg extends BaseUserMsg {
    /**
     * 0 - все
     * 1 - только подписанные
     * 2 - только не подписанные
     * 3 - только не подписанные ключем
     */
    private Integer filter;
}
