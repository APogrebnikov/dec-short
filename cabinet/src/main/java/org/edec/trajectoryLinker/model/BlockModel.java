package org.edec.trajectoryLinker.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BlockModel {
    private String blockName;
    private Boolean subjectWasLinked = false;
    private List<SubjectModel> subjects = new ArrayList<>();
}
