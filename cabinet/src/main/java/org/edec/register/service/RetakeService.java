package org.edec.register.service;

import org.edec.model.GroupModel;
import org.edec.register.model.RetakeModel;
import org.edec.register.model.RetakeSubjectModel;
import org.edec.register.model.StudentModel;
import org.edec.register.model.SubjectModel;
import org.edec.utility.constants.FormOfControlConst;

import java.util.Date;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public interface RetakeService {
    /**
     * Поиск групп по фильтрам
     *
     * @param qualification - перечисления через запятую квалификация, где
     *                      1 - инженер, 2 - бакалавр, 3 -магистр
     * @param course - курс
     * @param idSem - семестр
     * @param groupname - поиск по группе (может содержать не полное название группы)
     * @return - список групп
     */
    List<GroupModel> getGroupsByFilter(String qualification, Integer course, Long idSem, String groupname);
    /**
     *
     * @param idSem - семестр
     * @param listOfIdGroup - список групп, по которым производится поиск,
     *                      если строка пустая, то фильтр не учитывается
     * @param filter - поиск по вхождениям в строках: ФИО преподавателя, предмет
     * @return - список предметов
     */
    List<RetakeSubjectModel> getRetakeSubjects(Long idSem, String listOfIdGroup, String filter);

    //boolean openRetake(Long idLGSS, Integer foc, Date dateOfBegin, Date dateOfEnd, Integer vedomType);

    List<StudentModel> getCheckedStudentsForOpenRetake(RetakeSubjectModel subject, List<StudentModel> students);
    List<StudentModel> getCheckedStudentsForOpenRetake(RetakeSubjectModel subject, List<StudentModel> students, List<StudentModel> canceledStudents);

    boolean openRetake(RetakeSubjectModel subject, Integer foc, List<StudentModel> studentsForRetake, Date dateOfBegin, Date dateOfEnd, Integer typeRetake);
}
