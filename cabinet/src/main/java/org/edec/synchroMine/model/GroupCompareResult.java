package org.edec.synchroMine.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.utility.compare.compareResult.CompareField;
import org.edec.utility.compare.compareResult.CompareLevel;

@Getter
@Setter
public class GroupCompareResult {

    @CompareField
    private String groupname;
    @CompareField
    private Integer idGroupMine;
    @CompareField(required = false)
    private Integer course;
    private Integer semester;

    private Long idLGS;
    private Long idChair;
}
