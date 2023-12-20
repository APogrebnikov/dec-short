package org.edec.mine.service.curriculumLine;

import org.edec.utility.component.model.SemesterModel;

import java.util.List;

public interface JdbcCurriculumLineService {

    List<SemesterModel> getSemesters(Long idInst, Integer formOfStudy);
}
