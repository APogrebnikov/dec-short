package org.edec.subjectsAnalysis.ctrl;

import org.edec.subjectsAnalysis.ctrl.renderer.SubjectInfoRenderer;
import org.edec.subjectsAnalysis.service.SubjectsAnalysisService;
import org.edec.subjectsAnalysis.service.impl.SubjectAnalysisServiceImpl;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

public class WinSubjectsInfoCtrl extends CabinetSelector {
    @Wire
    private Listbox lbSubjectsInfo;
    @Wire
    private Window winSubjectsInfo;

    public static final String SUBJECTNAME = "subjectname";
    public static final String ID_SEMESTER = "id_Sem";
    public static final String COURSE = "course";
    public static final String FORM_OF_CONTROL = "foc";
    public static final String ID_CHAIR = "idChair";

    private Integer course;
    private Integer idChair;
    private int foc;
    private Long idSem;
    private String subjectname;

    private SubjectsAnalysisService service = new SubjectAnalysisServiceImpl();

    @Override
    protected void fill() throws InterruptedException {
        subjectname = (String) Executions.getCurrent().getArg().get(SUBJECTNAME);
        idSem = (Long) Executions.getCurrent().getArg().get(ID_SEMESTER);
        foc = (Integer) Executions.getCurrent().getArg().get(FORM_OF_CONTROL);
        course = (Integer) Executions.getCurrent().getArg().get(COURSE);
        idChair = (Integer) Executions.getCurrent().getArg().get(ID_CHAIR);

        lbSubjectsInfo.setModel(new ListModelList<>(service.getSubjectsInfo(subjectname, idSem, course, foc, idChair)));
        lbSubjectsInfo.setItemRenderer(new SubjectInfoRenderer());

        winSubjectsInfo.setClosable(true);
    }
}
