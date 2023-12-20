package org.edec.notificationCenter.service;

import org.edec.notificationCenter.model.CommentaryModel;
import org.edec.notificationCenter.model.FeedbackModel;
import org.edec.notificationCenter.model.NotificationModel;
import org.edec.notificationCenter.model.ReceiverModel;
import org.edec.utility.constants.FormOfStudyConst;
import org.edec.utility.constants.ReceiverTypeConst;
import org.zkoss.util.media.Media;
import org.zkoss.zul.Div;

import java.util.Date;
import java.util.List;

public interface NotificationService {

    List<NotificationModel> getAllNotifications(Long idHumanface);

    NotificationModel createNotification(Long idSender, ReceiverTypeConst notificationType);

    /**
     * Создание и отправка уведомления, формируемого системой
     * @param header заголовок уведомления
     * @param text текст уведомления
     * @param listIdReceivers список id humanface получателей уведомления
     */
    void createSystemNotification(String header, String text, List<Long> listIdReceivers);

    boolean saveNotification(NotificationModel notification);

    boolean sendNotification(Long idNotification);

    boolean removeNotification(Long idNotification);

    List<String> getAttachedFiles(NotificationModel notification);

    void attachFile(NotificationModel notification, List<Media> medias);

    void deleteFile(NotificationModel notification, String file);

    void downloadFile(Long idNotification, String filePath);

    List<ReceiverModel> getReceivers(ReceiverTypeConst notificationType, Long idNotification);

    /**
     * Поиск получаетелей по заданным параметрам
     *
     * @param fos форма обучения (очная/заочная)
     * @param idInst институт
     * @param notificationType тип уведомления
     * @param addedReceivers список получателей, добавленных ранее
     * @param params параметры для поиска
     * @return
     */
    List<ReceiverModel> searchReceivers(int fos,long idInst, ReceiverTypeConst notificationType, List<ReceiverModel> addedReceivers, List<String> params);

    boolean deleteReceiver(ReceiverTypeConst type, Long idNotification, ReceiverModel receiver);

    /**
     * Получаем список людей, доступных для обратной связи по выбранному уведомлению
     * @param idNotification id выбранного уведомления
     * @return
     */
    List<FeedbackModel> getFeedback(Long idNotification);

    /**
     * Получаем историю сообщений с выбранным человеком
     * @param idSender id текущего пользователя
     * @param idLinkNotificationHumanface id таблицы связи человека и уведомления
     * @return
     */
    List<CommentaryModel> getComments(Long idSender,Long idLinkNotificationHumanface);

    /**
     * Отправка сообщения
     * @param idLinkNotificationHumanface id таблицы связи человека и уведомления
     * @param comment сущность сообщения
     */
    void sendComment(Long idLinkNotificationHumanface, CommentaryModel comment);

    /**
     * Обновление статусов непрочитанных сообщений при прочтении
     * @param unreadComments список непрочитанных коментариев
     */
    void updateCommentStatus(List<CommentaryModel> unreadComments);

    /**
     * Получение уведомлений для человека
     * @param idHumanface
     * @return
     */
    List<NotificationModel> getUsersNotifications(Long idHumanface);

    /**
     * Заполнение окна чата
     * @param divChat чат
     * @param commentaries список сообщений
     * @param idCurrentUser id текущего пользователя
     */
    void fillDivChat(Div divChat, List<CommentaryModel> commentaries,Long idCurrentUser);

    /**
     * Формирование сообщения чата
     * @param divChat чат
     * @param commentaryModel сущность сообщения
     * @param previousCommentaryDate дата предыдущего комментария
     * (используется для визуального разделения сообщений по блокам в зависимости от даты)
     * @param idCurrentUser id текущего пользователя
     */
    void constructDivMessage(Div divChat, CommentaryModel commentaryModel, Date previousCommentaryDate, Long idCurrentUser);

    /**
     * Обновление уведомления как прочитанного у конкретного получателя
     * @param idLinkNotificationHumanface id таблицы связи человека и уведомления
     */
    void updatePersonalNotificationStatus(Long idLinkNotificationHumanface);

    void updateNotificationStatus(Long idNotification);

}
