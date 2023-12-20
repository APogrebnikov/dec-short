package org.edec.contingentMovement.service.impl;

import org.edec.contingentMovement.ctrl.WinEditStatusCtrl;
import org.edec.contingentMovement.manager.ContingentMovementManager;
import org.edec.contingentMovement.manager.StudentStatusMineDAO;
import org.edec.contingentMovement.manager.StudentStatusModelDAO;
import org.edec.contingentMovement.service.ContingentMovementService;
import org.edec.dao.DAO;
import org.edec.manager.EntityManagerGroupsESO;
import org.edec.manager.EntityManagerSemesterESO;
import org.edec.model.ActionModel;
import org.edec.model.GroupModel;
import org.edec.model.SemesterModel;
import org.edec.studentPassport.manager.StudentPassportEsoDAO;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.studentPassport.model.filter.StudentPassportFilter;
import org.edec.synchroMine.manager.studentSynchro.EntityManagerStudentESO;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class ContingentMovementImpl extends DAO implements ContingentMovementService {

    private EntityManagerGroupsESO emGroup = new EntityManagerGroupsESO();
    private EntityManagerSemesterESO emSemester = new EntityManagerSemesterESO();
    private StudentPassportEsoDAO studentPassportEsoDAO = new StudentPassportEsoDAO();
    private StudentStatusMineDAO studentStatusMineDAO = new StudentStatusMineDAO();
    private StudentStatusModelDAO sssDAO = new StudentStatusModelDAO();
    private EntityManagerStudentESO emStudentESO = new EntityManagerStudentESO();
    private ContingentMovementManager contingentMovementManager = new ContingentMovementManager();

    @Override
    public Long getCurrentSem(Long idInst, Integer formOfStudy) {
        List<SemesterModel> semesters = emSemester.getSemesters(idInst, formOfStudy, 1);
        return semesters.size() == 0 ? null : semesters.get(0).getIdSem();
    }

    @Override
    public List<GroupModel> getGroupsBySem(Long idSem) {
        return emGroup.getGroupsBySemester(idSem);
    }

    @Override
    public List<StudentStatusModel> getStudentsByFilter(String fio, String recordbook, String groupname) {

        StudentPassportFilter filter = StudentPassportFilter.builder()
                .fio(fio).recordBook(recordbook).groupName(groupname)
                .build();
        return studentPassportEsoDAO.getStudentByFilter(filter);
    }

    @Override
    public List<StudentStatusModel> getStudentsByFilterWithNull(String fio, String recordbook, String groupname) {
        return studentPassportEsoDAO.getStudentByFilterWithNull(fio, recordbook, groupname);
    }

    @Override
    public List<StudentStatusModel> getStudentsByFilterDetail(String fio, String recordbook, String groupname) {
        return studentPassportEsoDAO.getStudentsByFilterDetail(fio, recordbook, groupname);
    }

    @Override
    public StudentStatusModel getStudentSCid(Long stId) {
        return studentPassportEsoDAO.getStudentByScId(stId);
    }

    /**
     * Пытаемся отыскать студента в Шахтах
     *
     * @param fio
     * @param recordbook
     * @param groupname
     * @return
     */
    @Override
    public List<StudentStatusModel> getStudentsByFilterInDBO(String fio, String recordbook, String groupname) {
        return studentStatusMineDAO.getStudentByFilter(fio, recordbook, groupname);
    }


    @Override
    public Long createSSSandSRinNewGroup(long idStudentCard, int trustAgreement, int governmentFinanced, long idDicGroup) {
        return contingentMovementManager.createSSSandSRinNewGroup(idStudentCard, trustAgreement, governmentFinanced, idDicGroup);
    }

    /**
     * Удаляет все старшие SSS, которые найдет выше idSSS
     *
     * @param idSSS - выше этого SSS
     * @param idSem - выше этого семестра
     */
    @Override
    public boolean deleteWasteSSS(Long idSSS, Long idSem) {
        return contingentMovementManager.deleteWasteSSS(idSSS, idSem);
    }

    /**
     * Set not actual for SR from old semester
     *
     * @param oldSSS
     */
    @Override
    public boolean setNotActualSR(Long oldSSS) {
        return contingentMovementManager.setNotActualSR(oldSSS);
    }

    /**
     * Обновляем статус академического отпуска у предыдущих семестров
     *
     * @param oldSSS
     * @return
     */
    @Override
    public boolean setAcademToPrevSSS(Long oldSSS) {
        return contingentMovementManager.setAcademToPrevSSS(oldSSS);
    }

    /**
     * Устанавливаем новую группу в SC для текущего студента
     *
     * @param currentSSS
     * @return
     */
    @Override
    public boolean updateCurrentGroup(Long currentSSS) {
        return contingentMovementManager.updateCurrentGroup(currentSSS);
    }

    /**
     * Устанавливает флаг Слушатель, для студентов в подвешанном состоянии
     * TODO: Добавить данный флаг студенту во все семестры в новой группе
     *
     * @param idSSS
     * @param listener
     * @return
     */
    @Override
    public boolean setListenetFlag(long idSSS, boolean listener) {
        return contingentMovementManager.setListenetFlag(idSSS, listener);
    }

    @Override
    public Long createNewStudent(StudentStatusModel studentStatusModel) {
        return emStudentESO.createStudent(studentStatusModel.getGroupname(), studentStatusModel.getFamily(), studentStatusModel.getName(), studentStatusModel.getPatronymic(),
                studentStatusModel.getBirthday(), studentStatusModel.getRecordBook(), studentStatusModel.getSex(),
                studentStatusModel.getMineId(), studentStatusModel.getIdHum()
        );
    }

    @Override
    public boolean insertAction(ActionModel action) {
        return contingentMovementManager.insertAction(action);
    }

    @Override
    public org.edec.contingentMovement.model.StudentStatusModel getStasuses(Long idSSS) {
        try {
            return sssDAO.getStasuses(idSSS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Обновление стаусов студента
     *
     * @param sss
     * @return
     */
    @Override
    public org.edec.contingentMovement.model.StudentStatusModel updateStatus(org.edec.contingentMovement.model.StudentStatusModel sss) {
        try {
            return sssDAO.update(sss);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Установка флагов-статусов студента из полученного из окна JSON-объекта
     *
     * @param jsonObject
     * @param sss
     * @return
     */
    public org.edec.contingentMovement.model.StudentStatusModel setSSSflags(JSONObject jsonObject,
                                                                            org.edec.contingentMovement.model.StudentStatusModel sss) {
        String currentStatus = jsonObject.getString(WinEditStatusCtrl.NAME_STATUS);
        Boolean flagAction = jsonObject.getBoolean(WinEditStatusCtrl.FLAG_ACTION);

        switch (currentStatus) {
            case WinEditStatusCtrl.DEDUCTED:
                sss.setDeducted(flagAction);
                break;

            case WinEditStatusCtrl.ACADEM:
                sss.setAcademicLeave(flagAction);
                break;

            case WinEditStatusCtrl.COMPLETED_EDUCATION:
                sss.setEducationComplete(flagAction);
                break;

            case WinEditStatusCtrl.GET_SOCIAL:
                sss.setGetSocialGrant(flagAction);
                break;

            case WinEditStatusCtrl.GOVERNMENT:
                sss.setGovernmentFinanced(flagAction);
                break;

            case WinEditStatusCtrl.GROUP_LEADER:
                sss.setGroupLeader(flagAction);
                break;

            case WinEditStatusCtrl.INVALID:
                sss.setInvalid(flagAction);
                break;

            case WinEditStatusCtrl.SIROTA:
                sss.setSirota(flagAction);
                break;

            case WinEditStatusCtrl.PROLONGATION:
                sss.setSessionProlongation(flagAction);
                sss.setProlongationBeginDate(
                        jsonObject.get(WinEditStatusCtrl.DATE_BEGIN) instanceof Date
                                ? (Date) jsonObject.get(WinEditStatusCtrl.DATE_BEGIN)
                                : null);
                sss.setProlongationEndDate(
                        jsonObject.get(WinEditStatusCtrl.DATE_END) instanceof Date
                                ? (Date) jsonObject.get(WinEditStatusCtrl.DATE_END)
                                : null);
                break;

            case WinEditStatusCtrl.PUT_SOCIAL:
                sss.setPutAppForSocialGrant(flagAction);
                break;

            case WinEditStatusCtrl.TRANSFER_STUDENT:
                sss.setTransferStudent(flagAction);
                break;

            case WinEditStatusCtrl.TRUST_AGREEMENT:
                sss.setTrustAGreement(flagAction);
                break;

            case WinEditStatusCtrl.LISTENER:
                sss.setListener(flagAction);
                break;

            default:
                break;
        }

        return sss;
    }
}
