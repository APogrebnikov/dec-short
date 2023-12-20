package org.edec.passportGroup.service.impl;

import org.edec.passportGroup.manager.CopyGroupManager;
import org.edec.passportGroup.model.LinkGroupSemesterModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 03.07.19.
 * Сервис для создания и заполнения ГАК групп
 */
public class CopyGroupService {
    private CopyGroupManager copyGroupManager = new CopyGroupManager();

    /**
     * Метод для создания сущностей в БД для группы ГАК -
     * чтобы можно было перезачесть оценки востанавливающихся на защиту стедентов
     * @param idGroupFrom
     * @param idGroupTo
     * @return
     */
    public boolean copySubject(Long idGroupFrom, Long idGroupTo){
        // Получаем список LGS для From
        List<LinkGroupSemesterModel> listLGSold = copyGroupManager.getLgsForGroup(idGroupFrom);
        // Получаем список LGS для To
        List<LinkGroupSemesterModel> listLGSnew = copyGroupManager.getLgsForGroup(idGroupTo);
        // TODO: тут поидее должна быть проверка, чтобы не генерировать все подряд - цикл по двум листам с поиском последнего
        List<Long> listOldLGSid = new ArrayList<>();
        for (int i=0; i<listLGSold.size()-1; i++) {
            listOldLGSid.add(listLGSold.get(i).getIdLgs());
        }
        // Генерируем в To недостающие LGS
        copyGroupManager.createLgsForGroup(listOldLGSid, idGroupTo);
        // Генерируем в To недостающие LGSS из FROM
        copyGroupManager.createLgssForGroup(idGroupFrom, idGroupTo);

        return true;
    }
}
