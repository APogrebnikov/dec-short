package org.edec.student.FAQ.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordModel {
    String oldPassword;
    String newPassword;
    Long idParent;
}
