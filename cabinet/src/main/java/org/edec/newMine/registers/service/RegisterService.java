package org.edec.newMine.registers.service;

import org.edec.newMine.registers.model.RegistersSubjectsModel;
import org.edec.newMine.registers.model.StudentsRatingModel;
import org.edec.utility.component.model.GroupModel;
import org.edec.utility.component.model.SemesterModel;

import java.util.List;

public interface RegisterService {

    List<RegistersSubjectsModel> getSubjectsGroup (GroupModel group, SemesterModel semester);
    List<StudentsRatingModel> getStudentsAndMarks(Long idSubjectsMine, int typeRegister, Long idRegisterMine);
}
