package org.edec.order.model.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Модель для переноса приказа в таблицу order_action
 */
@Getter
@Setter
public class OrderImportModel {
    private Long idLOSS, idStudentcard, idDicGroupFromOrder, idDicGroup, idInstitute, idSemester;
    private Date dateAction, dateStart, dateFinish;
    private String orderNumber;
}
