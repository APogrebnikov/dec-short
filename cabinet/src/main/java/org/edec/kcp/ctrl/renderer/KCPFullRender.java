package org.edec.kcp.ctrl.renderer;

import org.edec.admin.ctrl.WinAddRoleEmpCtrl;
import org.edec.kcp.ctrl.IndexPageCtrl;
import org.edec.kcp.ctrl.WinKCPInfoCtrl;
import org.edec.kcp.model.KCPFullModel;
import org.edec.kcp.model.StudentShortModel;
import org.edec.successful.model.StudentModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.FormOfStudyConst;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KCPFullRender implements ListitemRenderer<KCPFullModel> {
    IndexPageCtrl indexPageCtrl;

    public KCPFullRender(IndexPageCtrl indexPageCtrl) {
        this.indexPageCtrl = indexPageCtrl;
    }

    @Override
    public void render(Listitem li, KCPFullModel data, int i) throws Exception {
        li.appendChild(new Listcell(data.getDirectioncode() + " "
                + data.getDirection()
                + " (" + FormOfStudyConst.getNameByType(data.getFos()) + ")"));
        li.appendChild(new Listcell(data.getStartyear()));

        //
        // KCP
        //
        Integer kcpBudget = data.getKcpBudget() == null ? 0 : data.getKcpBudget();
        Integer kcpDogovor = data.getKcpDogovor() == null ? 0 : data.getKcpDogovor();
        Integer kcpTotal = kcpBudget + kcpDogovor;
        Listcell lcKcpTotal = new Listcell(kcpTotal.toString());
        Listcell lcKcpBudget = new Listcell(kcpBudget.toString());
        Listcell lcKcpDogovor = new Listcell(kcpDogovor.toString());

        lcKcpTotal.addEventListener(Events.ON_CLICK, event -> openWindowEditKCP(data));
        lcKcpBudget.addEventListener(Events.ON_CLICK, event -> openWindowEditKCP(data));
        lcKcpDogovor.addEventListener(Events.ON_CLICK, event -> openWindowEditKCP(data));

        lcKcpTotal.setStyle("border-left: 2px solid #767676;");
        lcKcpDogovor.setStyle("border-right: 2px solid #767676;");

        li.appendChild(lcKcpTotal);
        li.appendChild(lcKcpBudget);
        li.appendChild(lcKcpDogovor);

        //
        // Контингент
        //
        Integer contingetTotal = data.getContingentTotal() == null ? 0 : data.getContingentTotal();
        Listcell lcContingentTotal = new Listcell(contingetTotal.toString());
        li.appendChild(lcContingentTotal);
        List<StudentShortModel> totalContingentList = new ArrayList<>();
        totalContingentList.addAll(data.getListContingetBudget());
        totalContingentList.addAll(data.getListContingetDogovor());
        lcContingentTotal.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(totalContingentList, "Текущий контингент");
        });

        Double contingentTotalPerc = calculatePercentage(contingetTotal, kcpTotal);
        Listcell lcContingentTotalPerc = new Listcell(contingentTotalPerc.toString());
        setWarn(lcContingentTotalPerc, contingentTotalPerc);
        li.appendChild(lcContingentTotalPerc);

        Integer contingetBudget = data.getContingentBudget() == null ? 0 : data.getContingentBudget();
        Listcell lcContingentBudget = new Listcell(contingetBudget.toString());
        li.appendChild(lcContingentBudget);
        lcContingentBudget.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(data.getListContingetBudget(), "Контингент - бюджет");
        });

        Double contingentBudgetPerc = calculatePercentage(contingetBudget, kcpBudget);
        Listcell lcContingentBudgetPerc = new Listcell(contingentBudgetPerc.toString());
        setWarn(lcContingentBudgetPerc, contingentBudgetPerc);
        li.appendChild(lcContingentBudgetPerc);

        Integer contingetDogovor = data.getContingentDogovor() == null ? 0 : data.getContingentDogovor();
        Listcell lcContingentDogovor = new Listcell(contingetDogovor.toString());
        li.appendChild(lcContingentDogovor);
        lcContingentDogovor.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(data.getListContingetDogovor(), "Контингент - договор");
        });

        Double contingentDogovorPerc = calculatePercentage(contingetDogovor, kcpDogovor);
        Listcell lcContingentDogovorPerc = new Listcell(contingentDogovorPerc.toString());
        lcContingentDogovorPerc.setStyle("border-right: 2px solid #767676;");
        setWarn(lcContingentDogovorPerc, contingentDogovorPerc);
        li.appendChild(lcContingentDogovorPerc);

        //
        // С учетом проблемных
        //
        Integer contingetAfterTotal = data.getAfterCommTotal() == null ? 0 : data.getAfterCommTotal();
        Listcell lcContingentAfterTotal = new Listcell(contingetAfterTotal.toString());
        li.appendChild(lcContingentAfterTotal);
        List<StudentShortModel> totalAfterContingentList = new ArrayList<>();
        totalAfterContingentList.addAll(data.getListProblemsBudget());
        totalAfterContingentList.addAll(data.getListProblemsDogovor());
        lcContingentAfterTotal.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(totalAfterContingentList, "Должники");
        });

        Double contingentTotalAfterPerc = calculatePercentage(contingetAfterTotal, kcpTotal);
        Listcell lcContingentTotalAfterPerc = new Listcell(contingentTotalAfterPerc.toString());
        setWarn(lcContingentTotalAfterPerc, contingentTotalAfterPerc);
        li.appendChild(new Listcell(contingentTotalAfterPerc.toString()));

        Integer contingetAfterBudget = data.getAfterCommBudget() == null ? 0 : data.getAfterCommBudget();
        Listcell lcContingentAfterBudget = new Listcell(contingetAfterBudget.toString());
        li.appendChild(lcContingentAfterBudget);
        lcContingentAfterBudget.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(data.getListProblemsBudget(), "Должники - бюджет");
        });

        Double contingentBudgetAfterPerc = calculatePercentage(contingetAfterBudget, kcpBudget);
        Listcell lcContingentBudgetAfterPerc = new Listcell(contingentBudgetAfterPerc.toString());
        setWarn(lcContingentBudgetAfterPerc, contingentBudgetAfterPerc);
        li.appendChild(lcContingentBudgetAfterPerc);

        Integer contingetAfterDogovor = data.getAfterCommDogovor() == null ? 0 : data.getAfterCommDogovor();
        Listcell lcContingentAfterDogovor = new Listcell(contingetAfterDogovor.toString());
        li.appendChild(lcContingentAfterDogovor);
        lcContingentAfterDogovor.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(data.getListProblemsDogovor(), "Должники - договор");
        });

        Double contingentDogovorAfterPerc = calculatePercentage(contingetAfterDogovor, kcpDogovor);
        Listcell lcContingentDogovorAfterPerc = new Listcell(contingentDogovorAfterPerc.toString());
        lcContingentDogovorAfterPerc.setStyle("border-right: 2px solid #767676;");
        setWarn(lcContingentDogovorAfterPerc, contingentDogovorAfterPerc);
        li.appendChild(lcContingentDogovorAfterPerc);
    }

    public void setWarn(Listcell lc, Double perc) {
        if (perc < 90) {
            String style = (lc.getStyle() == null) ? "" : lc.getStyle();
            lc.setStyle(style + "font-weight: 600; color: red;");
        }
    }

    public Double calculatePercentage(int obtained, int total) {
        if (total == 0) {
            return Double.valueOf(0);
        }
        return Double.valueOf(obtained * 100 / total);
    }

    /**
     * обработчик клика на колонки КЦП
     *
     * @param data
     */
    public void openWindowEditKCP(KCPFullModel data) {
        Map arg = new HashMap();
        arg.put(WinKCPInfoCtrl.MAIN_PAGE, this.indexPageCtrl);
        arg.put(WinKCPInfoCtrl.KCP_INFO, data);

        ComponentHelper.createWindow("/kcp/winKCPInfo.zul", "winKCPInfo", arg).doModal();
    }

    public void showStudents(List<StudentShortModel> students) {
        showStudents(students, null);
    }

    public void showStudents(List<StudentShortModel> students, String title) {
        if (title == null) {
            title = "Студенты";
        }
        DialogUtil.info(concat(students), title);
    }

    public String concat(List<StudentShortModel> students) {
        String res = "";
        for (int i = 0; i < students.size(); i++) {
            String debt = (students.get(i).getDebt() == null) ? "" : " Долги: " + students.get(i).getDebt();
            res = res + (i + 1) + ". " + students.get(i).getGroupName() + " " + students.get(i).getFio() + debt + "\n";
        }
        return res;
    }
}
