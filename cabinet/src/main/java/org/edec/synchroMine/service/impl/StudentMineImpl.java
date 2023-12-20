package org.edec.synchroMine.service.impl;

import com.google.protobuf.StringValue;
import org.apache.commons.lang3.StringUtils;
import org.edec.model.SemesterModel;
import org.edec.studentPassport.manager.StudentPassportEsoDAO;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.studentPassport.model.filter.StudentPassportFilter;
import org.edec.synchroMine.manager.studentSynchro.EntityManagerStudentDBO;
import org.edec.synchroMine.manager.studentSynchro.EntityManagerStudentESO;
import org.edec.synchroMine.model.mine.Group;
import org.edec.synchroMine.model.mine.Student;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.edec.synchroMine.model.eso.StudentModel;
import org.edec.synchroMine.service.StudentMineService;

import java.util.*;


public class StudentMineImpl implements StudentMineService {
    private StudentPassportEsoDAO studentPassportEsoDAO = new StudentPassportEsoDAO();
    private EntityManagerStudentDBO emStudentDBO = new EntityManagerStudentDBO();
    private EntityManagerStudentESO emStudentESO = new EntityManagerStudentESO();

    @Override
    public List<Student> getStudentsByInst(Long idInst) {
        return emStudentDBO.getStudentsByInst(idInst);
    }

    @Override
    public List<StudentModel> getStudentsCurrent() {
        return emStudentESO.getStudentsCurrent(1L, null);
    }

    @Override
    public List<org.edec.utility.component.model.StudentModel> getStudentsMineByGroupname(String groupname) {
        return emStudentDBO.getStudentsByGroupName(groupname);
    }

    @Override
    public List<org.edec.studentPassport.model.StudentStatusModel> getStudentsDecByGroupname(String groupname) {

        StudentPassportFilter filter = StudentPassportFilter.builder()
                .groupName(groupname)
                .build();
        return studentPassportEsoDAO.getStudentByFilter(filter);
    }

    @Override
    public List<org.edec.utility.component.model.StudentModel> getStudentsEsoByGroupInSem(Long idLGS) {
        return emStudentESO.getStudentsByGroupName(idLGS);
    }

    @Override
    public List<org.edec.utility.component.model.StudentModel> getStudentsByFilter(String fio, String recordbook, Long idStudCardMine) {
        return emStudentESO.getStudentsByFioOrStudCard(fio, recordbook, idStudCardMine);
    }

    @Override
    public List<org.edec.utility.component.model.StudentModel> getStudentsByFilter(String fio) {
        return emStudentESO.getHumansByFio(fio);
    }

    @Override
    public List<String> getGroupNameByInst(Long idInst) {
        return emStudentDBO.getGroupNameByInstFor6years(idInst);
    }

    @Override
    public boolean synchroStudentId(Long idInstMine, Long idInst) {

        List<Group> groups = emStudentDBO.getGroupByInst(idInstMine);
        List<GroupMineModel> groupMineModels = emStudentESO.getCurrentGroups(idInst);
        List<GroupMineModel> result = new ArrayList<>();
        List<Group> result2 = new ArrayList<>();

        for (GroupMineModel groupMineModel : groupMineModels) {
            for (Group group : groups) {
                if (StringUtils.equalsIgnoreCase(group.getName(), groupMineModel.getGroupname()) 
                        && group.getCourse().equals(groupMineModel.getCourse())) {
                    
                    List<Student> students = emStudentDBO.getStudentsByGroup(group.getId());
                    List<StudentModel> studentModels = emStudentESO.getStudentsCurrent(idInst, groupMineModel.getIdLGS());

                    List<StudentModel> completed = new ArrayList<>();
                    List<Student> completed2 = new ArrayList<>();

                    for (StudentModel studentModel : studentModels) {
                        for (Student student : students) {
                            if (studentModel.getIdMineStudentcard() != null &&
                                    student.getId().equals(studentModel.getIdMineStudentcard())) {
                                /*if (student.getIdStatus()==1)
                                    emStudentESO.updateStudentRecordbook(studentModel.getIdStudentcard(), student.getRecordBook());*/
                                if (student.getIdStatus().intValue() != studentModel.getIdStatus()) {
                                    System.out.println("Не совпадают данные статусы у студента: " + studentModel.getFio() + "(" +
                                            studentModel.getIdSSS() + ")" + ", у нас: " + studentModel.getIdStatus() +
                                            ", в шахтах: " + student.getIdStatus());
                                }
                                if (!student.getReasonStudy().equals(studentModel.getReasonStudy())) {
                                    System.out.println("Не совпадают основания обучения у студента: " + studentModel.getFio() + "(" +
                                            studentModel.getIdSSS() + ") " + ", у нас: " + studentModel.getReasonStudy() +
                                            ", в шахтах:" + student.getReasonStudy());
                                    /*emStudentESO.updateSSSstudyReason(
                                            studentModel.getIdSSS(),
                                            (student.getReasonStudy().equals("ЦН") || student.getReasonStudy().equals("ОО")) ? 1 : 0,
                                            student.getReasonStudy().equals("ЦН") ? 1 : 0
                                    );*/
                                }
                                completed.add(studentModel);
                                completed2.add(student);
                                break;
                            }
                        }
                    }

                    studentModels.removeAll(completed);
                    groupMineModel.getStudents().addAll(studentModels);
                    result.add(groupMineModel);
                    students.removeAll(completed2);
                    group.getStudents().addAll(students);
                    result2.add(group);
                    break;
                }
            }
        }

        System.out.println("====ESO====");
        for (GroupMineModel groupMineModel : result) {
            if (groupMineModel.getStudents().size() == 0) {
                continue;
            }
            System.out.println("Group: " + groupMineModel.getGroupname());
            for (StudentModel studentModel : groupMineModel.getStudents()) {
                System.out.println(
                        "\t" + studentModel.getFio() + " (" + studentModel.getIdStatus() + "; " + studentModel.getReasonStudy() + ")");
            }
        }
        System.out.println("====DBO====");
        for (Group group : result2) {
            if (group.getStudents().size() == 0) {
                continue;
            }
            System.out.println("Group: " + group.getName());
            for (Student student : group.getStudents()) {
                System.out.println("\t" + student.getFamily() + " " + " " + student.getName() + " " + student.getPatronymic() + "(" +
                        student.getIdStatus() + ";" + student.getReasonStudy() + ")");
            }
        }

        return false;
    }

    @Override
    public Long createStudent(String groupname, String family, String name, String patronymic, Date birthday, String recordbook, Integer sex,
                              Long idStudentMine, Long idHum) {
        return emStudentESO.createStudent(groupname, family, name, patronymic, birthday, recordbook, sex, idStudentMine, idHum);
    }

    @Override
    public void createSSSforStudent(Long idStudent, Integer trustAgreement, Integer governmentFinanced, Integer academicLeave,
                                    Long idGroup, String groupname) {
        emStudentESO.createSSSforStudentByGroup(idStudent, trustAgreement, governmentFinanced, academicLeave, idGroup, groupname);
    }

    @Override
    public void createSRforStudent(Long idStudent, Long idGroup, String groupname) {
        emStudentESO.createSRforStudentByGroup(idStudent, idGroup, groupname);
    }

    @Override
    public boolean deleteSSS(Long idSSS) {
        return emStudentESO.deleteSSS(idSSS);
    }

    @Override
    public boolean updateStudentCardFromMine(Long idStudentcard, Long idStudentCardMine, String recordbook) {
        return emStudentESO.updateStudentFromMine(idStudentcard, idStudentCardMine, recordbook);
    }

    @Override
    public List<org.edec.synchroMine.model.StudentStatusModel> getStudentStatusEso(Long idInst, Long idSem) {
        return emStudentESO.getStudentStatusESO(idInst, idSem);

    }

    @Override
    public List<org.edec.synchroMine.model.StudentStatusModel> getStudentStatusMine(Long idInst, SemesterModel selectedSemester) {
        Calendar cal = new GregorianCalendar();
        String firstYear, lastYear;
        cal.setTime(selectedSemester.getDateOfBegin());
        if (cal.get(Calendar.MONTH) < 7){
            firstYear = String.valueOf(cal.get(Calendar.YEAR)-1);
            lastYear = String.valueOf(cal.get(Calendar.YEAR));
        } else {
            firstYear = String.valueOf(cal.get(Calendar.YEAR));
            lastYear = String.valueOf(cal.get(Calendar.YEAR) +1);
        }
        String year = firstYear + "-" + lastYear;

        return emStudentDBO.getStudentStatusMine(idInst, year);
    }
}