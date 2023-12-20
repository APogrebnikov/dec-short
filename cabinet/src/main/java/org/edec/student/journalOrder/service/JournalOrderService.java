package org.edec.student.journalOrder.service;

import org.edec.student.journalOrder.manager.EntityManagerJournalOrder;
import org.edec.student.journalOrder.model.JournalOrderModel;
import org.edec.student.journalOrder.model.Scholarship;
import org.edec.utility.constants.OrderTypeConst;

import java.util.ArrayList;
import java.util.List;


public class JournalOrderService {
    private EntityManagerJournalOrder emJournalOrder = new EntityManagerJournalOrder();

    public List<JournalOrderModel> getJournalByHum(Long idHum) {
        List<JournalOrderModel> listStudentsOrder = emJournalOrder.getJournalOrder(idHum);
        Long idOtherSc = emJournalOrder.getIdOtherSC(idHum);

        List<String> orders = new ArrayList<>();
        for (JournalOrderModel model : listStudentsOrder) {
            orders.add(model.getOrderNumber());
        }

        List<Scholarship> scholarships = emJournalOrder.getMassSummOfScholarship(orders, idOtherSc);

        for (JournalOrderModel model : listStudentsOrder) {
            for (Scholarship scholarship : scholarships) {
                if (scholarship.orderNum.equals(model.getOrderNumber())) {
                    model.setSummScolarship(scholarship.summ);
                    break;
                }
            }
        }

        /*
        for (JournalOrderModel model : listStudentsOrder) {
            if ((model.getIdOrderType() == OrderTypeConst.ACADEMIC.getType() && model.getSectionDescription().toLowerCase().contains("назначить"))
                    || model.getIdOrderType() == OrderTypeConst.MATERIAL_SUPPORT.getType() ||
                    model.getIdOrderType() == OrderTypeConst.SOCIAL.getType() || model.getIdOrderType() == OrderTypeConst.SOCIAL_INCREASED.getType()){
                String year = model.getSemesterStr().substring(0, 9);
                model.setSummScolarship(emJournalOrder.getSummOfScholarship(idOtherSc, model.getOrderNumber(), year));
            }
        }
        */

        return listStudentsOrder;
    }
}
