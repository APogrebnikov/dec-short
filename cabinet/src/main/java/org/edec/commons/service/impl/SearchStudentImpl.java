package org.edec.commons.service.impl;

import org.edec.commons.manager.StudentManager;
import org.edec.commons.model.StudentGroupModel;
import org.edec.commons.service.SearchStudentService;

import java.util.List;


public class SearchStudentImpl implements SearchStudentService {

    private StudentManager studentManager = new StudentManager();

    /**
     * @param filter - фильтр: ФИО/зачетка/группа <= всё зависит от вашей реализации
     * @param idInst - институт (может быть null, тогда институт не учитывается)
     * @param fos    - форма обучения (может быть null, тогда форма обучения не учитывается)
     * @return список студентов
     */
    @Override
    public List<StudentGroupModel> getStudentByFilter (String filter, Long idInst, Integer fos) {
        return studentManager.getStudentGroupByFilter(filter, idInst, fos);
    }
}
