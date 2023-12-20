package org.edec.register.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.model.SubjectGroupModel;

/**
 * @author Max Dimukhametov
 */
@Getter
@Setter
public class RetakeSubjectModel extends SubjectGroupModel {
    private String teachers;
    private int typeRetake;
}
