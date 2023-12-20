package org.edec.utility.zk;

import lombok.experimental.UtilityClass;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;

/**
 * Утилити-класс для отображения диалогового окна
 */
@UtilityClass
public class DialogUtil {

    /**
     * Название события, которое сработает на нажатие кнопки "Да"
     */
    public final static String ON_YES = Messagebox.ON_YES;

    /**
     * @param msg - название сообщения
     *
     * @see #info(String, String)
     */
    public static void info(String msg) {
        info(msg, "Информация");
    }

    /**
     * Отображается диалоговое окно с информацией (синий цвет),
     * где можно указывать заголовок и текст сообщения
     *
     * @param msg - название сообщения
     * @param title - название заголовка
     */
    public static void info(String msg, String title) {
        Messagebox.show(msg, title, Messagebox.OK, Messagebox.INFORMATION);
    }

    /**
     * @param msg - название сообщения
     *
     * @see #error(String, String)
     */
    public static void error(String msg) {
        error(msg, "Ошибка");
    }

    /**
     * Отображается диалоговое окно с ошибкой (красный цвет),
     * где можно указывать заголовок и текст сообщения
     *
     * @param msg - название сообщения
     * @param title - название заголовка
     */
    public static void error(String msg, String title) {
        Messagebox.show(msg, title, Messagebox.OK, Messagebox.ERROR);
    }

    /**
     * @param msg - название сообщения
     *
     * @see #exclamation(String, String)
     */
    public static void exclamation(String msg) {
        exclamation(msg, "Предупреждение");
    }

    /**
     * Отображается диалоговое окно с предупреждением (желтыйцвет),
     * где можно указывать заголовок и текст сообщения
     *
     * @param msg - название сообщения
     * @param title - название заголовка
     */
    public static void exclamation(String msg, String title) {
        Messagebox.show(msg, title, Messagebox.OK, Messagebox.EXCLAMATION);
    }

    /**
     * Отображение диалогового окна с вопросом,
     * где можно указать заголовок и текст сообщения, а также
     * события на нажатия кнопок {@link Messagebox.YES} or {@link Messagebox.NO}
     *
     * @param msg
     * @param title
     * @param eventListener
     */
    public static void questionWithYesNoButtons(String msg, String title, EventListener<Event> eventListener) {
        Messagebox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, eventListener);
    }

    /**
     * Отображение диалогового окна с возможность назначить действия по нажатию кнопки ок.
     * Используем для объявлений при авторизации.
     * @param msg
     * @param title
     * @param eventListener
     */
    public static void announcement(String msg, String title, EventListener<Event> eventListener) {
        Messagebox.show(msg, title, Messagebox.OK, Messagebox.INFORMATION, eventListener);
    }
}
