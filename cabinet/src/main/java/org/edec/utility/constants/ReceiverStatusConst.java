package org.edec.utility.constants;

/**
 * Класс констант, которые отображают статус отправки уведомления пользователю
 * NOT_ADDED - дефолтный статус при поиске пользователей
 * ADDED - добавлен в список но не сохранен в БД
 * SAVED - пользователь привязян к уведомлению в БД, но уведомление не было отправлено пользователю
 * NOTIFIED - уведомление отправлено пользователю
 */
public class ReceiverStatusConst {

    public static final int NOT_ADDED = -1;
    public static final int ADDED = 0;
    public static final int SAVED = 1;
    public static final int NOTIFIED = 2;
}
