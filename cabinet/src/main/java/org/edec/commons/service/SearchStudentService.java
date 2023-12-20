package org.edec.commons.service;

import org.edec.commons.model.StudentGroupModel;

import java.util.List;


public interface SearchStudentService {
    /**
     * Получения списка студентов, соответствующие фильтру, институту и форме обучения
     *
     * @param filter - фильтр: ФИО/зачетка/группа <= всё зависит от вашей реализации
     * @param idInst - институт
     * @param fos    - форма обучения
     * @return список студентов
     */
    List<StudentGroupModel> getStudentByFilter (String filter, Long idInst, Integer fos);
}
