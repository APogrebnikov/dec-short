package org.edec.register.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Created by antonskripacev on 10.06.17.
 */

@Getter
@Setter
@NoArgsConstructor
public class StudentModel extends org.edec.model.StudentModel {
    private int rating;
    private Date changeDateTime;
    private Long idSRH, idSR, idSemester, idSSS;
    private Integer type;
    private String reason;

}
