package org.edec.contingentMovement.report.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ResitModel {
    private List<ResitMarkModel> marks = new ArrayList<>();
    private String fio;
    private String directioncode;
    private String groupname;
    private String prevgroupname;
    private String shortfio;
}
