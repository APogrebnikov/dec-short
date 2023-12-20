package org.edec.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Max Dimukhametov
 */
@Getter
@Setter
public class SubjectGroupModel extends SubjectModel {
    private Long idLGSS;

    private String groupname;
}
