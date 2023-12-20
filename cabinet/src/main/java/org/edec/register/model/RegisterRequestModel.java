package org.edec.register.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @author Anton Skripachev
 */
@Getter
@Setter
@NoArgsConstructor
public class RegisterRequestModel {

    private Long idRegisterRequest;
    private Long idLgss;
    private Long idGroup;
    private Long idSss;

    private Integer status;
    private Integer seasonSemester;

    private Integer foc;
    private String studentFullName;
    private String teacherFullName;
    private String subjectName;
    private String groupName;
    private String additionalInformation;
    private String email;

    private boolean getNotification;

    private Date dateOfApplying;
    private Date dateOfAnswering;

    private Date dateOfBeginSemester;
    private Date dateOfEndSemester;
}
