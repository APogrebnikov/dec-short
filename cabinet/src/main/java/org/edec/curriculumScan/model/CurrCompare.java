package org.edec.curriculumScan.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс для составления таблицы сравнений учебных планов
 */
@Getter
@Setter
public class CurrCompare {
    private Subject oldModel;
    private Subject newModel;
    private Boolean ident = true;

    // Метод для сравнения УП из файла и из базы данных
    public boolean compareTotal(){

        if (oldModel == null || newModel == null)
        {
            return false;
        }
        // Пошаговая сверка всех полей
        if (!oldModel.getName().equals(newModel.getName())) return false;
        if (!oldModel.getAllHours().equals(newModel.getAllHours())) return false;
        if (!oldModel.getSemesterNumber().equals(newModel.getSemesterNumber()))  return false;
        if (!oldModel.getLabHours().equals(newModel.getLabHours()))  return false;
        if (!oldModel.getLecHours().equals(newModel.getLecHours()))  return false;
        if (!oldModel.getPraHours().equals(newModel.getPraHours()))  return false;
        if (!oldModel.getSrHours().equals(newModel.getSrHours()))  return false;
        if (!oldModel.getExamHours().equals(newModel.getExamHours()))  return false;
        // TODO: Добавить дополнительные проверки для кафедра, компетенций и блоков

        return true;
    }

    public boolean check(Object newVal, Object oldVal) {
        if (newVal == null && oldVal == null) return true;
        if (newVal == null || oldVal == null) return false;
        return newVal.toString().equals(oldVal.toString());
    }

}
