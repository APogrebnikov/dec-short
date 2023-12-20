package org.edec.synchroMine.service;

import org.edec.model.SemesterModel;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.synchroMine.model.mine.Student;
import org.edec.synchroMine.model.eso.StudentModel;

import java.util.Date;
import java.util.List;


public interface StudentMineService {
    List<Student> getStudentsByInst (Long idInst);

    List<StudentModel> getStudentsCurrent ();

    List<org.edec.utility.component.model.StudentModel> getStudentsMineByGroupname (String groupname);

    List<StudentStatusModel> getStudentsDecByGroupname (String groupname);

    List<org.edec.utility.component.model.StudentModel> getStudentsEsoByGroupInSem (Long idLGS);

    List<org.edec.utility.component.model.StudentModel> getStudentsByFilter (String fio, String recordbook, Long idStudCardMine);

    List<org.edec.utility.component.model.StudentModel> getStudentsByFilter (String fio);

    List<String> getGroupNameByInst (Long idInst);

    boolean synchroStudentId (Long idInstMine, Long idInst);

    Long createStudent (String groupname, String family, String name, String patronymic, Date birthday, String recordbook, Integer sex, Long idStudentMine,
                        Long idHum);

    void createSSSforStudent (Long idStudent, Integer trustAgreement, Integer governmentFinanced, Integer academicLeave, Long idGroup,
                              String groupname);

    void createSRforStudent (Long idStudent, Long idGroup, String groupname);

    boolean deleteSSS (Long idSSS);

    boolean updateStudentCardFromMine (Long idStudentcard, Long idStudentCardMine, String recordbook);

    List<org.edec.synchroMine.model.StudentStatusModel> getStudentStatusEso (Long idInst, Long idSem);

    List<org.edec.synchroMine.model.StudentStatusModel> getStudentStatusMine (Long idInst,SemesterModel selectedSemester);
}
