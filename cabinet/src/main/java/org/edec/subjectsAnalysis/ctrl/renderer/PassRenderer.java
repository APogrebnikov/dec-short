package org.edec.subjectsAnalysis.ctrl.renderer;

import org.edec.subjectsAnalysis.ctrl.WinSubjectsInfoCtrl;
import org.edec.subjectsAnalysis.model.SubjectsAnalysisModel;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.HashMap;
import java.util.Map;

public class PassRenderer implements ListitemRenderer<SubjectsAnalysisModel> {

    private Integer course;
    private Long idSem;
    private int foc;


    private double oneRetake;
    private double hqMoreOneRetake;
    private double pass;
    private double hqNotPass;

    public PassRenderer (Integer course, int foc, Long idSem, double oneRetake, double hqMoreOneRetake,double pass, double hqNotPass){
        this.course = course;
        this.foc = foc;
        this.idSem = idSem;

        this.oneRetake = oneRetake;
        this.hqMoreOneRetake = hqMoreOneRetake;
        this.pass = pass;
        this.hqNotPass = hqNotPass;
    }

    @Override
    public void render(Listitem li, SubjectsAnalysisModel data, int i) throws Exception {
        li.appendChild(new Listcell(data.getSubjectname()));


        Listcell lcOneRetake = new Listcell(data.getOneRetake() != null ? String.valueOf(Math.round(data.getOneRetake())) : "-");
        if (data.getOneRetake() != null && data.getOneRetake() > this.oneRetake) {
            lcOneRetake.setStyle("background: #ff0000");
        }
        li.appendChild(lcOneRetake);

        Listcell lcMoreThenOne = new Listcell(data.getMoreThenOneRetake() != null ? String.valueOf(Math.round(data.getMoreThenOneRetake())) : "-");
        if (data.getMoreThenOneRetake() != null && data.getMoreThenOneRetake() > this.hqMoreOneRetake) {
            lcMoreThenOne.setStyle("background: #ff0000");
        }
        li.appendChild(lcMoreThenOne);


        Listcell lcPartPass = new Listcell(String.valueOf(data.getPartPass()));
        if (data.getPartPass() > this.pass) {
            lcPartPass.setStyle("background: #ff0000");
        }
        li.appendChild(lcPartPass);

        Listcell lcPartNoPass = new Listcell(String.valueOf(data.getPartNoPass()));
        if (data.getPartNoPass() > this.hqNotPass) {
            lcPartNoPass.setStyle("background: #ff0000");
        }
        li.appendChild(lcPartNoPass);

        Listcell lcTotalRiskScore = new Listcell(String.valueOf(data.getTotalRiskScore()));
        li.appendChild(lcTotalRiskScore);

        li.addEventListener(Events.ON_CLICK, event -> {
            Map<String, Object> arg = new HashMap<>();
            arg.put(WinSubjectsInfoCtrl.SUBJECTNAME, data.getSubjectname());
            arg.put(WinSubjectsInfoCtrl.ID_SEMESTER, idSem);
            arg.put(WinSubjectsInfoCtrl.COURSE, course);
            arg.put(WinSubjectsInfoCtrl.FORM_OF_CONTROL, foc);
            arg.put(WinSubjectsInfoCtrl.ID_CHAIR, data.getIdChair());
            ComponentHelper.createWindow("/subjectsAnalysis/winSubjectInfo.zul", "winSubjectInfo", arg).doModal();
        });
    }
}
