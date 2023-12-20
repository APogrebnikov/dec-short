package org.edec.workflow.service;

import org.edec.workflow.model.WorkflowModel;

import java.util.List;

/**
 * Сервис доступа к данным по задачам.
 *
 * @author Max Dimukhametov
 */
public interface WorkflowService {
    /**
     * Получает список всех задач по приказам.
     *
     * @return список задач.
     */
    List<WorkflowModel> getAllNewTask ();

    /**
     * Получение списка задач-операций
     *
     * @return список задач
     */
    List<WorkflowModel> getAllNewOperationTasks ();

    /**
     * Получает список всех задач по приказам из архива.
     *
     * @return список задач.
     */
    List<WorkflowModel> getAllArchiveTask ();

    /**
     * Получает список задач по приказам по id пользователя.
     *
     * @param idHumanface
     * @return список задач.
     */
    List<WorkflowModel> getAllNewTasksByIdHum (Long idHumanface);

    /**
     * Получает список архива задач по приказам по id пользователя.
     *
     * @param idHumanface
     * @return список задач.
     */
    List<WorkflowModel> getArchiveTaskListByIdHum (Long idHumanface);

    /**
     * Получение процесса подписания у БП
     *
     * @param idTask - ИД таска
     * @return список процесса подписания
     */
    List<WorkflowModel> getArchiveTasksConfirmingByIdBP (Long idTask);

    /**
     * Обновляет данные задачи по приказу по id задачи.
     *
     * @return Статус записи.
     */
    String updateOrderTask (WorkflowModel data);

    List<Long> getOrdersByStudentFio(String fio);

    List<WorkflowModel> getArchiveTasksConfirmingByOrderId(Long idOrder);
}
