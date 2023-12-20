package org.edec.eok.ctrl.renderer;

import org.edec.eok.model.SubjectModel;
import org.edec.eok.model.TeacherModel;
import org.edec.teacher.ctrl.renderer.EsoCourseRenderer;
import org.edec.teacher.model.EsoCourseModel;
import org.edec.teacher.service.CompletionService;
import org.edec.teacher.service.impl.CompletionServiceImpl;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.List;

public class SubjectRenderer implements ListitemRenderer<SubjectModel> {
    private ComponentService componentService = new ComponentServiceESOimpl();
    private CompletionService completionService = new CompletionServiceImpl();

    private List<EsoCourseModel> esoCourses;

    public SubjectRenderer (List<EsoCourseModel> esoCourses) {
        this.esoCourses = esoCourses;
    }

    @Override
    public void render (Listitem li, SubjectModel data, int index) throws Exception {
        li.setValue(data);
        componentService.createListcell(li, String.valueOf(index + 1), "", "", "");
        componentService.createListcell(li, data.getDepartment(), "", "", "");
        componentService.createListcell(li, data.getSubjectname(), "", "", "");
        componentService.createListcell(li, data.getGroupname(), "", "", "");
        componentService.createListcell(li, data.getFormOfControl(), "", "", "");
        componentService.createListcell(li, String.valueOf(data.getHourscount()), "", "", "");

        Listcell lcESOcourse = new Listcell();
        lcESOcourse.setParent(li);

        Bandbox bdEsoCourse = new Bandbox();
        bdEsoCourse.setParent(lcESOcourse);
        bdEsoCourse.setMold("rounded");
        bdEsoCourse.setAutodrop(true);
        bdEsoCourse.setWidth("80px");
        bdEsoCourse.setReadonly(true);
        bdEsoCourse.setValue(data.getIdEsoCourse() != null ? String.valueOf(data.getIdEsoCourse()) : "");

        Bandpopup bpop = new Bandpopup();
        bpop.setParent(bdEsoCourse);

        Hbox hbEsoCourse = new Hbox();
        hbEsoCourse.setParent(bpop);

        Button btnDelEsoCourse = new Button("", "/imgs/crossCLR.png");
        btnDelEsoCourse.setParent(hbEsoCourse);
        btnDelEsoCourse.setTooltiptext("Удалить привязку курса");

        Label lEsoCourse = new Label();
        lEsoCourse.setParent(hbEsoCourse);

        Listbox lbEsoCourse = new Listbox();
        lbEsoCourse.setParent(bpop);
        lbEsoCourse.setWidth("500px;");
        lbEsoCourse.setVflex("1");
        lbEsoCourse.setMold("paging");
        lbEsoCourse.setItemRenderer(new EsoCourseRenderer());
        lbEsoCourse.setPageSize(5);
        Listhead lhEsoCourse = new Listhead();
        lhEsoCourse.setParent(lbEsoCourse);

        Listheader lhrIdCourse = new Listheader();
        lhrIdCourse.setParent(lhEsoCourse);
        lhrIdCourse.setWidth("80px");
        Intbox intIdCourse = new Intbox();
        intIdCourse.setParent(lhrIdCourse);
        intIdCourse.setPlaceholder("Введите код");

        Listheader lhrNameCourse = new Listheader();
        lhrNameCourse.setParent(lhEsoCourse);
        lhrNameCourse.setWidth("400px");
        Textbox tbCourseName = new Textbox();
        tbCourseName.setPlaceholder("Введите название");
        tbCourseName.setParent(lhrNameCourse);

        Listcell lcTeacher = new Listcell();
        lcTeacher.setParent(li);
        Vbox vbTeacher = new Vbox();
        vbTeacher.setParent(lcTeacher);
        for (TeacherModel teacher : data.getTeachers()) {
            new Label(teacher.getShortFio()).setParent(vbTeacher);
        }

        bdEsoCourse.addEventListener(Events.ON_OPEN, onOpenBandbox(data, intIdCourse, tbCourseName, lbEsoCourse, lEsoCourse));
        intIdCourse.addEventListener(Events.ON_OK, onOKtextboxes(intIdCourse, tbCourseName, lbEsoCourse));
        tbCourseName.addEventListener(Events.ON_OK, onOKtextboxes(intIdCourse, tbCourseName, lbEsoCourse));
        lbEsoCourse.addEventListener(Events.ON_SELECT, onSelectEsoCourse(lbEsoCourse, data, bdEsoCourse));
        btnDelEsoCourse.addEventListener(Events.ON_CLICK, onClickDelEsoCourse(data, bdEsoCourse));
    }

    private EventListener<? extends Event> onClickDelEsoCourse (final SubjectModel data, final Bandbox bdEsoCourse) {
        return (EventListener<Event>) event -> {
            completionService.updateESOcourse(data.getIdLGSS(), null);
            data.setIdEsoCourse(null);
            bdEsoCourse.setValue("");
            bdEsoCourse.close();
        };
    }

    private EventListener<Event> onOpenBandbox (final SubjectModel subject, final Intbox intIdCourse, final Textbox tbNameCourse,
                                                final Listbox lbEsoCourse, final Label lEsoCourse) {
        return event -> {
            intIdCourse.setValue(subject.getIdEsoCourse() == null ? null : subject.getIdEsoCourse().intValue());
            tbNameCourse.setValue("");
            lEsoCourse.setValue(subject.getIdEsoCourse() == null
                                ? "Курс не выбран"
                                : "Выбранный курс: " +
                                  completionService.getFilteredEsoCourses(null, subject.getIdEsoCourse(), esoCourses).get(0).getFullname());
            lbEsoCourse.setModel(new ListModelList<>(completionService.getFilteredEsoCourses(null, intIdCourse.getValue() != null
                                                                                                   ? intIdCourse.getValue().longValue()
                                                                                                   : null, esoCourses)));
            lbEsoCourse.renderAll();
        };
    }

    private EventListener<Event> onOKtextboxes (final Intbox intIdCourse, final Textbox tbNameCourse, final Listbox lbEsoCourse) {
        return event -> {
            lbEsoCourse.setModel(new ListModelList<>(
                    completionService.getFilteredEsoCourses(tbNameCourse.getValue().equals("") ? null : tbNameCourse.getValue(),
                                                            intIdCourse.getValue() != null ? intIdCourse.getValue().longValue() : null,
                                                            esoCourses
                    )));
            lbEsoCourse.renderAll();
        };
    }

    private EventListener<Event> onSelectEsoCourse (final Listbox lbEsoCourse, final SubjectModel subject, final Bandbox bdEsoCourse) {
        return event -> {
            EsoCourseModel selectedCourse = lbEsoCourse.getSelectedItem().getValue();
            completionService.updateESOcourse(subject.getIdLGSS(), selectedCourse.getIdEsoCourse());
            subject.setIdEsoCourse(selectedCourse.getIdEsoCourse());
            bdEsoCourse.setValue(String.valueOf(selectedCourse.getIdEsoCourse()));
            bdEsoCourse.close();
        };
    }
}
