package org.edec.kcp.service.impl;

import org.edec.kcp.manager.KCPManager;
import org.edec.kcp.model.KCPFullModel;
import org.edec.kcp.model.StudentShortModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class KCPService {
    KCPManager manager = new KCPManager();
    public List<KCPFullModel> getFullModel(List<Integer> qualification, List<Integer> formofstudy, Integer debt, String conditionDebt) {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        String yearInString = String.valueOf(year);
        List<KCPFullModel> listKCP = manager.getFullModel(yearInString, qualification, formofstudy);
        List<KCPFullModel> listForRemove = new ArrayList<>();
        for (KCPFullModel kcpFullModel : listKCP) {
            kcpFullModel.setListContingetBudget(new ArrayList<>());
            kcpFullModel.setListContingetDogovor(new ArrayList<>());
            kcpFullModel.setListProblemsBudget(new ArrayList<>());
            kcpFullModel.setListProblemsDogovor(new ArrayList<>());
            kcpFullModel.setContingentBudget(0);
            kcpFullModel.setContingentDogovor(0);

            // Определяем текущее кол-во студентов по направлениям
            List<StudentShortModel> listStudent = manager.getStudentsForDir(kcpFullModel.getDirectioncode(), kcpFullModel.getStartyear(), qualification, kcpFullModel.getListFos());
            for (StudentShortModel studentShortModel : listStudent) {
                if (studentShortModel.isFinanced()) {
                    kcpFullModel.getListContingetBudget().add(studentShortModel);
                    kcpFullModel.setContingentBudget(kcpFullModel.getContingentBudget() + 1);
                } else {
                    kcpFullModel.getListContingetDogovor().add(studentShortModel);
                    kcpFullModel.setContingentDogovor(kcpFullModel.getContingentDogovor() + 1);
                }
            }
            kcpFullModel.setContingentTotal(kcpFullModel.getContingentDogovor() + kcpFullModel.getContingentBudget());

            // Определяем кол-во потенциально проблемных студентов через поиск долгов
            kcpFullModel.setAfterCommBudget(kcpFullModel.getContingentBudget());
            kcpFullModel.setAfterCommDogovor(kcpFullModel.getContingentDogovor());

            List<StudentShortModel> listProblemStudent = manager.getProblemStudents(kcpFullModel.getCode(), kcpFullModel.getStartyear(), debt, conditionDebt,  qualification, kcpFullModel.getListFos());
            for (StudentShortModel studentShortModel : listProblemStudent) {
                if (studentShortModel.isFinanced()) {
                    kcpFullModel.setAfterCommBudget(kcpFullModel.getAfterCommBudget() - 1);
                    kcpFullModel.getListProblemsBudget().add(studentShortModel);
                } else {
                    kcpFullModel.setAfterCommDogovor(kcpFullModel.getAfterCommDogovor() - 1);
                    kcpFullModel.getListProblemsDogovor().add(studentShortModel);
                }
            }
            kcpFullModel.setAfterCommTotal(kcpFullModel.getAfterCommDogovor() + kcpFullModel.getAfterCommBudget());
            // Если нет ни одного студента, то не нужно выводить
            if (listStudent.size() == 0 && listProblemStudent.size() == 0) {
                listForRemove.add(kcpFullModel);
            }
        }
        listKCP.removeAll(listForRemove);
        return listKCP;
    }

    public int updateKCPModel(KCPFullModel kcpFullModel) {
        return manager.updateKCPModel(kcpFullModel);
    }
}
