package org.edec.synchroMine.model;

import org.edec.synchroMine.model.dao.SubjectGroupMineModel;
import org.edec.synchroMine.model.dao.SubjectGroupModel;

public class CurriculumCompareResult {
    private SubjectGroupModel esoModel;
    private SubjectGroupMineModel mineModel;
    private boolean result;

    public boolean compareCurriculum () {
        return false;
    }
}
