package org.edec.newMine.students.service.impl;

import org.edec.newMine.students.manager.StudentsManagerESO;
import org.edec.newMine.students.manager.StudentsManagerMine;
import org.edec.newMine.students.model.StudentsModel;
import org.edec.newMine.students.service.StudentsService;
import org.edec.utility.component.model.StudentModel;

import java.util.Date;
import java.util.List;

public class StudentsServiceImpl implements StudentsService {

    private StudentsManagerMine managerMine = new StudentsManagerMine();
    private StudentsManagerESO managerEso = new StudentsManagerESO();

    @Override
    public boolean updateStudentCardFromMine(Long idStudentcard, Long idStudentCardMine, String recordbook) {
        return managerEso.updateStudentFromMine(idStudentcard, idStudentCardMine, recordbook);
    }

    @Override
    public List<StudentsModel> getStudentsFromDBO(String groupname) {
        return managerMine.getStudentsByGroupName(groupname);
    }

    @Override
    public List<StudentsModel> getStudentsFromESO(Long idLgs) {
        return managerEso.getStudentsCurrent(idLgs);
    }

    @Override
    public boolean deleteSSS(Long idSSS) {
        return managerEso.deleteStudent(idSSS);
    }

    @Override
    public boolean updateStudentStatus(StudentsModel studentMine, Long idSSS) {
        int isDeducted = (studentMine.getStatus() == 3 ? 1 : 0);
        int isAcadem = (studentMine.getStatus() == -1 ? 1 : 0);
        int isComleteEducation = (studentMine.getStatus() == 4 ? 1 : 0);
        return managerEso.updateStudentStatus(idSSS, isDeducted, isAcadem, isComleteEducation);
    }

    @Override
    public void refreshStudentEsoByGroup(List<StudentsModel> studentsMine, String groupname) {
        for (StudentsModel student : studentsMine) {
            if (student.getStatus() == 3 || student.getOtherStudentModel() != null) {
                continue;
            }
            Long idStudentCard = null;
            List<StudentsModel> studentsByFilter = getStudentsByFilter(student.getFio(), student.getRecordbook(), student.getIdStudCardMine());
            for (StudentsModel studentModel : studentsByFilter) {
                if (student.getRecordbook().equals(studentModel.getRecordbook())) {
                    idStudentCard = studentModel.getIdStudCard();
                    break;
                } else if (student.getFio().equals(studentModel.getFio())) {
                    student.setIdHum(studentModel.getIdHum());
                }
            }
//            if (idStudentCard == null && studentsByFilter.size() != 0) {
//                continue;
//            }

            if (idStudentCard == null) {
                try {
                    idStudentCard = createStudent(groupname, student.getFamily(), student.getName(),
                            (student.getIdHum() == null ? student.getPatronymic() : null), student.getBirthday(), student.getRecordbook(),
                            student.getSex(), student.getIdStudCardMine(), student.getIdHum());

                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
            student.setIdStudCard(idStudentCard);
            int trustAgreement = student.getCondOfEducation() == 2 ? 1 : 0;
            int governmentFinanced = (student.getCondOfEducation() == 1 || student.getCondOfEducation() == 2) ? 1 : 0;
            createSSSforStudent(student.getIdStudCard(), trustAgreement, governmentFinanced, student.getStatus() == -1 ? 1 : 0, null, groupname);
            createSRforStudent(student.getIdStudCard(), null, groupname);
            /// Обновляем новый параметр - hash для синхронизации
            updateHash(student.getIdStudCard(), student.getHash());
        }
    }

    public boolean updateStudentsHashByGroup(List<StudentsModel> studentsMine, String groupname) {
        int count = 0;
        for (StudentsModel student : studentsMine) {
            Long idStudentCard = null;
            List<StudentsModel> studentsByFilter = getStudentsByFilter(student.getFio(), student.getRecordbook(), student.getIdStudCardMine());
            for (StudentsModel studentModel : studentsByFilter) {
                if (student.getRecordbook().equals(studentModel.getRecordbook())) {
                    idStudentCard = studentModel.getIdStudCard();
                    break;
                } else if (student.getFio().equals(studentModel.getFio())) {
                    student.setIdHum(studentModel.getIdHum());
                }
            }

            if (idStudentCard == null) {
                try {
                    idStudentCard = createStudent(groupname, student.getFamily(), student.getName(),
                            (student.getIdHum() == null ? student.getPatronymic() : null), student.getBirthday(), student.getRecordbook(),
                            student.getSex(), student.getIdStudCardMine(), student.getIdHum());

                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
            student.setIdStudCard(idStudentCard);
            updateHash(student.getIdStudCard(), student.getHash());
            count++;
        }
        return count>0;
    }

    private List<StudentsModel> getStudentsByFilter(String fio, String recordbook, Long idStudCardMine) {
        return managerEso.getStudentsByFioOrStudCard(fio, recordbook, idStudCardMine);
    }

    public Long createStudent(String groupname, String family, String name, String patronymic, Date birthday, String recordbook, Integer sex, Long idStudentMine, Long idHum) {
        return managerEso.createStudent(groupname, family, name, patronymic, birthday, recordbook, sex, idStudentMine, idHum);
    }

    public void createSSSforStudent(Long idStudent, Integer trustAgreement, Integer governmentFinanced, Integer academicLeave, Long idGroup, String groupname) {
        managerEso.createSSSforStudentByGroup(idStudent, trustAgreement, governmentFinanced, academicLeave, idGroup, groupname);
    }

    private void createSRforStudent(Long idStudent, Long idGroup, String groupname) {
        managerEso.createSRforStudentByGroup(idStudent, idGroup, groupname);
    }

    private void updateHash(Long idStudent, String hash) {
       managerEso.updateHash(idStudent, hash);
    }
}
