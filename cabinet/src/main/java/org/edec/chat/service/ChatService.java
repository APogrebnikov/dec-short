package org.edec.chat.service;

import org.edec.chat.ChatClient;
import org.edec.chat.model.Message;
import org.edec.main.model.ModuleModel;
import org.edec.main.model.UserModel;
import org.edec.profile.model.ProfileModel;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public interface ChatService {
    List<Message> findMessageByInformation (Long userDialog, String information); //Найти сообщение по содежимому

    Long getUserDialog (Long humanfaceId, String groupName); //Получить UserDialog

    List<Message> getUnreadMessages (Long userDialog); //Получить непрочитанные сообщений

    List<Message> getDownMessages (Long userDialog, Long messageId); //Подгрузить вниз

    List<Message> getUpMessages (Long userDialog, Long messageId); //Подгрузить вверх

    List<Message> getImportantMessages (Long userDialog); //Получить 20 важных сообщений

    Long sendMessage (Message message, Long userDialog); //Отправить сообщение на сервер

    Boolean noteImportantMessage (Long messageId, int status); //Отметить сообщение как важное на сервере и наоборот

    Boolean updateLastMessage (Long userDialog, Long messageId); //Обновить последнее прочитанное сообщение

    Boolean addMessage (Long idHum, String message, Long idChat, int userType, Long replyTo, List<String> paths); //Добавить сообщение

    List<Message> downloadMessages (Long last); // Подгрузка сообщений
}
