package org.edec.contingentMovement.service;

import org.edec.model.ActionModel;
import org.edec.model.GroupModel;
import org.edec.studentPassport.model.StudentStatusModel;

import java.util.List;

public interface ContingentMovementService {

    Long getCurrentSem(Long idInst, Integer formOfStudy);

    List<GroupModel> getGroupsBySem(Long idSem);

    List<StudentStatusModel> getStudentsByFilter(String fio, String recordbook, String groupname);

    List<StudentStatusModel> getStudentsByFilterWithNull(String fio, String recordbook, String groupname);

    List<StudentStatusModel> getStudentsByFilterDetail(String fio, String recordbook, String groupname);

    List<StudentStatusModel> getStudentsByFilterInDBO(String fio, String recordbook, String groupname);

    Long createSSSandSRinNewGroup(long idStudentCard, int trustAgreement, int governmentFinanced, long idDicGroup);

    boolean deleteWasteSSS(Long idSSS, Long idSem);

    boolean setNotActualSR(Long oldSSS);

    boolean setAcademToPrevSSS(Long oldSSS);

    boolean updateCurrentGroup(Long currentSSS);

    boolean setListenetFlag(long idSSS, boolean listener);

    Long createNewStudent(StudentStatusModel studentStatusModel);

    StudentStatusModel getStudentSCid(Long stId);

    boolean insertAction(ActionModel action);

    org.edec.contingentMovement.model.StudentStatusModel getStasuses(Long idSSS);

    org.edec.contingentMovement.model.StudentStatusModel updateStatus(org.edec.contingentMovement.model.StudentStatusModel sss);
}
