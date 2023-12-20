package org.edec.newMine.students.service;

import org.edec.newMine.students.model.StudentsModel;

import java.util.Date;
import java.util.List;

public interface StudentsService {
    List<StudentsModel> getStudentsFromESO (Long idLgs);
    List<StudentsModel> getStudentsFromDBO (String groupname);
    boolean updateStudentCardFromMine (Long idStudentcard, Long idStudentCardMine, String recordbook);
    boolean updateStudentStatus (StudentsModel studentMine, Long idSSS);
    boolean deleteSSS (Long idSSS);
    void refreshStudentEsoByGroup(List<StudentsModel> studentsMine, String groupname);
    boolean updateStudentsHashByGroup(List<StudentsModel> studentsMine, String groupname);


}
