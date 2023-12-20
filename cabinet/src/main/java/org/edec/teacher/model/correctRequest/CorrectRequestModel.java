package org.edec.teacher.model.correctRequest;

import lombok.Data;
import org.edec.utility.constants.FormOfControlConst;

import java.util.Date;

/**
 * Модель для корректировки оценок
 */
@Data
public class CorrectRequestModel {
    private Long id;
    /**
     * ID старой оценки
     */
    private Long idSRH;
    /**
     * ID создателя запроса
     */
    private Long idHumanface;
    /**
     * Новая оценка
     */
    private Integer newRating;

    /**
     * Старая оценка
     */
    private Integer oldRating;
    /**
     * Использует константы из RegisterRequestStatusConst
     */
    private Integer status;
    // Не знаю, есть ли смысл это тут держать
    private Integer foc;
    /**
     * Дата запроса
     */
    private Date dateOfApplying;
    /**
     * Дата согласования
     */
    private Date dateOfAnswering;

    /**
     * Доп. комментарии
     */
    private String additionalInformation;

    /**
     * Отпечаток ключа подписи
     */
    private String thumbprint;

    /**
     * Сертификат
     */
    private String certnumber;

    /**
     * Путь подписанного файла
     */
    private String filePath;

    ///// Информация для отображения
    /**
     * ФИО преподавателя
     */
    private String teacherFIO;
    /**
     * ФИО Студента
     */
    private String studentFIO;
    /**
     * Название дисциплины
     */
    private String subjectName;
    /**
     * Номер ведомости
     */
    private String regNumber;
    /**
     * Статус для отправки уведомления
     * 0 - заявка на корректировку создана, 1 - отправить уведомление об одобрении заявки, 2 - отправить уведомление об отклонении заявки
     */
    private Integer notificationStatus;

    // Дополнительные поля для отображения
    private String groupName;
    private Integer FC;
    private String season;
    private Integer type;

    public String getFormControl(){
        return FormOfControlConst.getName(this.FC).getName();
    }
}
