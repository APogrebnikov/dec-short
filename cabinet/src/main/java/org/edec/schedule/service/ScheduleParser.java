package org.edec.schedule.service;

import org.edec.schedule.model.dao.GroupSubjectLesson;
import org.edec.utility.constants.QualificationConst;

import java.util.List;

public interface ScheduleParser {
    List<GroupSubjectLesson> parseScheduleByParams(String groupname, Integer course, QualificationConst qualification);
}
