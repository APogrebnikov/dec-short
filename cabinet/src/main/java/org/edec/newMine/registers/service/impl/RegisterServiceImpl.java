package org.edec.newMine.registers.service.impl;

import org.edec.newMine.registers.manager.RegisterManagerASU;
import org.edec.newMine.registers.manager.RegisterManagerMine;
import org.edec.newMine.registers.model.RegistersSubjectsModel;
import org.edec.newMine.registers.model.StudentsRatingModel;
import org.edec.newMine.registers.service.RegisterService;
import org.edec.utility.component.model.GroupModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.converter.DateConverter;

import java.util.ArrayList;
import java.util.List;

public class RegisterServiceImpl  implements RegisterService {

    RegisterManagerASU managerASU = new RegisterManagerASU();
    RegisterManagerMine managerMine = new RegisterManagerMine();

    @Override
    public List<RegistersSubjectsModel> getSubjectsGroup(GroupModel group, SemesterModel semester) {
        int semesternumber =managerASU.getSemesternumber(group.getIdLGS());
        String year = DateConverter.convertDateToYearString(semester.getDateOfBegin()) + "-" + DateConverter.convertDateToYearString(semester.getDateOfEnd());
//        List<RegistersSubjectsModel> list = new ArrayList<>();
//        RegistersSubjectsModel model = new RegistersSubjectsModel();
//        model.setCodeSubject("codeSubj");
//        model.setFoc("foc");
//        model.setGroupname("group");
//        model.setIdCurriculumMine(12345L);
//        model.setIdMineRegister(54321L);
//        model.setOtherIdGroup(123L);
//        model.setOtherIdSubject(321L);
//        model.setSubjectname("subject");
//        model.setTypeMineRegister(2);
//        list.add(model);
        return managerMine.getGroupsSubject(group.getGroupname(), semesternumber, year);
       // return  list;
    }

    @Override
    public List<StudentsRatingModel> getStudentsAndMarks(Long idSubjectsMine, int typeRegister, Long idRegisterMine) {
        return managerMine.getStudentsAndMarks(idSubjectsMine, typeRegister, idRegisterMine);
    }
}
