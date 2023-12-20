package org.edec.utility.constants;

public class RegisterConst {
    /**
     * Основная ведомость
     */
    public static final int TYPE_MAIN = 1;
    /**
     * Ведомость общей пересдачи
     */
    public static final int TYPE_RETAKE_MAIN = 2;
    /**
     * Ведомость комиссии
     */
    public static final int TYPE_COMMISSION = 3;
    /**
     * Ведомость индивидуальной пересдачи
     */
    public static final int TYPE_RETAKE_INDIV = 4;

    /**
     * Основная ведомость (не подписанная)
     */
    public static final int TYPE_MAIN_NOT_SIGNED = -1;
    /**
     * Ведомость общей пересдачи (не подписанная)
     */
    public static final int TYPE_RETAKE_MAIN_NOT_SIGNED = -2;
    /**
     * Ведомость комиссии (не подписанная)
     */
    public static final int TYPE_COMMISSION_NOT_SIGNED = -3;
    /**
     * Ведомость индивидуальной пересдачи (не подписанная)
     */
    public static final int TYPE_RETAKE_INDIV_NOT_SIGNED = -4;

    /**
     * Статус - по умолчанию: 0.0.0
     */
    public static final String STATUS_DEFAULT = "0.0.0";
    /**
     * Статус - новая ведомость: 2.0.0
     */
    public static final String STATUS_NEW = "2.0.0";
    /**
     * Статус - распечатана в деканате: 2.1.0
     */
    public static final String STATUS_PRINTED_BY_DEC = "2.1.0";
    /**
     * Статус - просмотренная: 2.2.0
     */
    public static final String STATUS_VIEWED = "2.2.0";
    /**
     * Статус - просмотренная/распечатанная преподавателем: 2.3.0
     */
    public static final String STATUS_VIEWED_OR_PRINTED_BY_TEACHER = "2.3.0";
    /**
     * Статус - оценка перезасчитана (заполнено системой): 1.5.0.
     * Данный статус записывается при создании истории обучения
     * у переводного/восстановленного студента при условии,
     * что данный предмет он ранее сдал.
     */
    public static final String STATUS_RATING_RESTATED_BY_SYSTEM = "1.5.0";
    /**
     * Статус - оценка перезасчитана: 1.5.1.
     * Данный статус записывается при создании истории обучения
     * у переводного/восстановленного студента при условии,
     * что данный предмет он ранее сдал.
     */
    public static final String STATUS_RATING_RESTATED = "1.5.1";

    /**
     * Статус - оценка утверждена: 1.3.1
     */
    public static final String STATUS_CONFIRMED = "1.3.1";
}
