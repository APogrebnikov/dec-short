package org.edec.mine.ctrl.curriculumLine;

import org.edec.cloud.sync.mine.api.curriculumLine.data.filter.CurriculumLineFilter;
import org.edec.cloud.sync.mine.api.curriculumLine.data.response.CurriculumLineResponse;
import org.edec.cloud.sync.mine.api.curriculumLine.service.ApiSyncMineCurriculumLineImpl;
import org.edec.cloud.sync.mine.api.curriculumLine.service.ApiSyncMineCurriculumLineService;
import org.edec.cloud.sync.mine.api.linkCurriculumLine.data.request.LinkCurriculumLineRequest;
import org.edec.cloud.sync.mine.api.linkCurriculumLine.data.response.LinkCurriculumLineResponse;
import org.edec.cloud.sync.mine.api.linkCurriculumLine.service.ApiSyncMineLinkCurriculumLineImpl;
import org.edec.cloud.sync.mine.api.linkCurriculumLine.service.ApiSyncMineLinkCurriculumLineService;
import org.edec.cloud.sync.mine.api.utility.constants.FormOfControlConst;
import org.edec.mine.ctrl.curriculumLine.renderer.SearchCurriculumLineRenderer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import java.util.Arrays;

public class WinSearchCurriculumLineForLinkCtrl extends SelectorComposer<Component> {

    public static final String SELECTED_INST = "selectedInst";
    public static final String SELECTED_FOS = "selectedFos";
    public static final String SELECTED_LINK_CURRICULUM_LINE = "selectedLinkCurriculumLine";
    public static final String ACTION_ON_SELECTED_CURRICULUM_LINE = "actionOnSelectedCurriculumLine";

    @Wire
    private Intbox ibCourse, ibSemesterNumber;
    @Wire
    private Label lSubjectName, lGroupName, lCourse, lSemesterNumber;
    @Wire
    private Listbox lbCurriculumLine;
    @Wire
    private Textbox tbGroupName, tbSubjectName;

    private ApiSyncMineCurriculumLineService apiSyncMineCurriculumLineService = new ApiSyncMineCurriculumLineImpl();
    private ApiSyncMineLinkCurriculumLineService apiSyncMineLinkCurriculumLineService = new ApiSyncMineLinkCurriculumLineImpl();

    private Integer selectedFos;
    private Long selectedIdInst;
    private LinkCurriculumLineResponse selectedLine;
    private SelectSubjectListener listenerOnSelectAction;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        selectedLine = (LinkCurriculumLineResponse) Executions.getCurrent().getArg().get(SELECTED_LINK_CURRICULUM_LINE);
        listenerOnSelectAction = (SelectSubjectListener) Executions.getCurrent().getArg().get(ACTION_ON_SELECTED_CURRICULUM_LINE);
        selectedFos = (Integer) Executions.getCurrent().getArg().get(SELECTED_FOS);
        selectedIdInst = (Long) Executions.getCurrent().getArg().get(SELECTED_INST);

        fillFields();
        fillListbox();
    }

    private void fillFields() {

        lSubjectName.setValue("Предмет: " + selectedLine.getSubjectNameCabinet());
        lGroupName.setValue("Группа: " + selectedLine.getGroupName());
        lCourse.setValue("Курс: " + selectedLine.getCourseCabinet());
        lSemesterNumber.setValue("Семестр: " + selectedLine.getSemesterNumberCabinet());
        tbGroupName.setValue(selectedLine.getGroupName());
    }

    private void fillListbox() {

        lbCurriculumLine.setItemRenderer(new SearchCurriculumLineRenderer(this::selectCurriculumLine));
        search();
    }

    private void selectCurriculumLine(CurriculumLineResponse selectedCurriculumLine) {

        LinkCurriculumLineRequest request = LinkCurriculumLineRequest.builder()
                .groupName(selectedLine.getGroupName())
                .courseCabinet(selectedLine.getCourseCabinet())
                .semesterNumberCabinet(selectedLine.getSemesterNumberCabinet())
                .subjectNameCabinet(selectedLine.getSubjectNameCabinet())
                .courseMine(selectedCurriculumLine.getCourse())
                .semesterNumberMine(selectedCurriculumLine.getSemesterNumber())
                .subjectNameMine(selectedCurriculumLine.getSubjectName())
                .idCurriculumLine(selectedCurriculumLine.getId())
                .build();
        
        apiSyncMineLinkCurriculumLineService.linkSubjects(request);
        listenerOnSelectAction.selectCurriculumLine(selectedCurriculumLine);
        getSelf().detach();
    }

    @Listen("onOK = #ibCourse; onOK = #ibSemesterNumber; onOK = #tbSubjectName;")
    public void search() {

        CurriculumLineFilter filter = CurriculumLineFilter
                .builder(selectedIdInst, selectedFos)

                .onlyUnlinked(true)
                .groupName(tbGroupName.getValue()).subjectNameMine(tbSubjectName.getValue())
                .courseMine(ibCourse.getValue()).semesterNumberMine(ibSemesterNumber.getValue())
                .selectedFocMine(Arrays.asList(FormOfControlConst.values()))
                .selectedFocCabinet(Arrays.asList(FormOfControlConst.values()))
                .build();

        lbCurriculumLine.setModel(new ListModelList<>(apiSyncMineCurriculumLineService.findCurriculumLinesByFilter(filter)));
        lbCurriculumLine.renderAll();
    }

    public interface SelectSubjectListener {

        void selectCurriculumLine(CurriculumLineResponse selectedCurriculumLine);
    }
}
