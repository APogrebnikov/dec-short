package org.edec.synchroMine.model;

import lombok.Data;
import org.edec.curriculumScan.model.CompareErrorConst;
import org.edec.curriculumScan.model.Subject;
import org.edec.synchroMine.model.dao.SubjectGroupMineModel;
import org.edec.synchroMine.model.dao.SubjectGroupModel;

import java.util.ArrayList;
import java.util.List;

@Data
public class CurriculumCompareScanResult {
    private Subject esoModel;
    private Subject mineModel;
    private List<CompareErrorConst> notes = new ArrayList<>();
    private boolean result;
    private String groupName;

    public boolean compareCurriculum () {
        return false;
    }
}
