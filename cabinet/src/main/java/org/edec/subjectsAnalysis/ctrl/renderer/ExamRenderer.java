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

public class ExamRenderer implements ListitemRenderer<SubjectsAnalysisModel> {
    private Integer course;
    private Long idSem;
    private int foc;

    private double avgRating;
    private double oneRetake;
    private double three;
    private double five;
    private double hqMoreOneRetake;
    private double hqTwo;

    public ExamRenderer (Integer course, int foc, Long idSem, double avgRating, double oneRetake, double three, double five, double hqMoreOneRetake, double hqTwo) {
        this.course = course;
        this.foc = foc;
        this.idSem = idSem;
        this.avgRating = avgRating;
        this.oneRetake = oneRetake;
        this.three = three;
        this.five = five;
        this.hqMoreOneRetake = hqMoreOneRetake;
        this.hqTwo = hqTwo;
    }
    @Override
    public void render(Listitem li, SubjectsAnalysisModel data, int i) throws Exception {
        li.setValue(data);
        li.appendChild(new Listcell(data.getSubjectname()));

        Listcell lcAvgRating = new Listcell(data.getAvgRating().toString());
        if (data.getAvgRating() < this.avgRating) {
            lcAvgRating.setStyle("background: #ff0000");
        }
        li.appendChild(lcAvgRating);

        li.appendChild(new Listcell(data.getModa().toString()));

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

        Listcell lcPartThree = new Listcell(String.valueOf(data.getPartThree()));
        if (data.getPartThree() > this.three) {
            lcPartThree.setStyle("background: #ff0000");
        }
        li.appendChild(lcPartThree);

        li.appendChild(new Listcell(String.valueOf(data.getPartFour())));

        Listcell lcPartFive = new Listcell(String.valueOf(data.getPartFive()));
        if (data.getPartFive() > this.five) {
            lcPartFive.setStyle("background: #ff0000");
        }
        li.appendChild(lcPartFive);

        Listcell lcPartTwo = new Listcell(String.valueOf(data.getPartTwo()));
        if (data.getPartTwo() > this.hqTwo) {
            lcPartTwo.setStyle("background: #ff0000");
        }
        li.appendChild(lcPartTwo);
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
