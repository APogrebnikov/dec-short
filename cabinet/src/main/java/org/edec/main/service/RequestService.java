package org.edec.main.service;

import org.apache.commons.lang3.tuple.Triple;
import org.edec.main.model.ModuleModel;
import org.edec.main.model.RoleModel;
import org.edec.main.model.UserModel;
import org.javatuples.Triplet;

import java.util.Set;


public interface RequestService {
    Integer getRequestCount();
    Set<Triplet> getAllOpenRequestCount();

    /**
     * Получаем количество новых сообщений для центра уведомлений
     * @param idHumanface создатель уведомления
     * @return количество непрочитанных сообщений по всем доступным уведомлениям
     */
    int getAllNotificationCount(Long idHumanface);

    /**
     * Получаем количество новых личных сообщений
     * @param idHumanface id пользователя
     * @return количество непрочитанных личных сообщений
     */
    int getPersonalNotificationCount(Long idHumanface);
}
