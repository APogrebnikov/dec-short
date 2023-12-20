package org.edec.teacher.model.register;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
public class DateModel {

    private Date dateOfBeginSession;
    private Date dateOfEndSession;
    private Date dateOfBeginPassweek;
    private Date dateOfEndPassweek;
}
