package org.edec.kcp.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class KCPFullModel {
    Long id;
    Long id_curriculum;
    Integer formofstudy;
    Integer qualification;
    String direction;
    String directioncode;
    Integer fos;
    String code;
    String startyear;
    String endyear;
    Integer kcpBudget;
    Integer kcpDogovor;
    Integer kcpTotal;
    Integer contingentBudget;
    Integer contingentDogovor;
    Integer contingentTotal;
    List<StudentShortModel> listContingetBudget;
    List<StudentShortModel> listContingetDogovor;
    Integer afterCommBudget;
    Integer afterCommDogovor;
    Integer afterCommTotal;
    List<StudentShortModel> listProblemsBudget;
    List<StudentShortModel> listProblemsDogovor;

    public List<Integer> getListFos() {
        List<Integer> list = new ArrayList<>();
        list.add(this.fos);
        return list;
    }
}
