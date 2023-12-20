package org.edec.profile.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
public class ProfileModel {
    private boolean getNotification;

    private Date birthDay;

    private String email;
    private String fio;

    private String referenceDateFinish;
}
