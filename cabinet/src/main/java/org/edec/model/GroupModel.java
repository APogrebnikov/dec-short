package org.edec.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class GroupModel {
    private Integer course, semester;

    private Long idDG, idLGS, idInstitute;

    private String groupname;

    @Override
    public String toString () {
        return getGroupname();
    }
}
