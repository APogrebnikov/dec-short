package org.edec.mine.service.curriculumLine.impl;

import org.edec.mine.service.curriculumLine.JdbcLinkCurriculumLineService;
import org.edec.utility.component.manager.ComponentManager;
import org.edec.utility.component.model.SemesterModel;

import java.util.List;

public class JdbcLinkCurriculumLineImpl implements JdbcLinkCurriculumLineService {

    private ComponentManager componentManager = new ComponentManager();

    @Override
    public List<SemesterModel> getSemesters(Long idInst, Integer formOfStudy) {
        return componentManager.getSemester(idInst, formOfStudy, null);
    }
}