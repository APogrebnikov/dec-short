package org.edec.mine.ctrl.curriculumLine.renderer;

import org.edec.cloud.sync.mine.api.linkCurriculumLine.data.response.LinkCurriculumLineResponse;
import org.edec.cloud.sync.mine.api.linkCurriculumLine.service.ApiSyncMineLinkCurriculumLineImpl;
import org.edec.cloud.sync.mine.api.linkCurriculumLine.service.ApiSyncMineLinkCurriculumLineService;
import org.edec.mine.ctrl.curriculumLine.WinSearchCurriculumLineForLinkCtrl;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

import java.util.HashMap;
import java.util.Map;

public class LinkCurriculumLineRenderer implements ListitemRenderer<LinkCurriculumLineResponse> {

    private ApiSyncMineLinkCurriculumLineService apiSyncMineLinkCurriculumLineService = new ApiSyncMineLinkCurriculumLineImpl();

    private Integer selectedFos;
    private Long selectedIdInst;
    private Runnable callSearch;

    public LinkCurriculumLineRenderer(Runnable callSearch, Long idInst, Integer fos) {
        this.callSearch = callSearch;
        this.selectedFos = fos;
        this.selectedIdInst = idInst;
    }

    @Override
    public void render(Listitem li, LinkCurriculumLineResponse data, int index) throws Exception {

        li.setValue(data);

        new Listcell(data.getGroupName()).setParent(li);
        new Listcell(data.getSubjectNameCabinet()).setParent(li);
        new Listcell(getStringFromInteger(data.getCourseCabinet())).setParent(li);
        new Listcell(getStringFromInteger(data.getSemesterNumberCabinet())).setParent(li);

        Listcell lcSubjectNameMine = new Listcell();
        lcSubjectNameMine.setParent(li);
        Textbox tbSubjectNameMine = new Textbox(data.getSubjectNameMine());
        tbSubjectNameMine.setParent(lcSubjectNameMine);
        tbSubjectNameMine.setHflex("1");
        tbSubjectNameMine.addEventListener(Events.ON_OK, event -> {
            data.setSubjectNameMine(tbSubjectNameMine.getValue());
            saveLinkCurriculumLine(data);
        });

        Listcell lcCourseMine = new Listcell();
        lcCourseMine.setParent(li);

        Intbox ibCourseMine = new Intbox();
        ibCourseMine.setParent(lcCourseMine);
        ibCourseMine.setHflex("1");
        if (data.getCourseMine() != null) {
            ibCourseMine.setValue(data.getCourseMine());
        }
        ibCourseMine.addEventListener(Events.ON_OK, event -> {
           data.setCourseMine(ibCourseMine.getValue());
            saveLinkCurriculumLine(data);

        });

        Listcell lcSemesterNumberMine = new Listcell();
        lcSemesterNumberMine.setParent(li);

        Intbox ibSemesterNumberMine = new Intbox();
        ibSemesterNumberMine.setParent(lcSemesterNumberMine);
        ibSemesterNumberMine.setHflex("1");
        if (data.getSemesterNumberMine() != null) {
            ibSemesterNumberMine.setValue(data.getSemesterNumberMine());
        }
        ibSemesterNumberMine.addEventListener(Events.ON_OK, event -> {
           data.setSemesterNumberMine(ibSemesterNumberMine.getValue());
            saveLinkCurriculumLine(data);
        });

        if (data.getSubjectNameMine() == null) {
            li.setStyle("background: #FF7373;");
        }

        li.addEventListener(Events.ON_RIGHT_CLICK, event -> {
            Map<String, Object> arg = new HashMap<>();

            arg.put(WinSearchCurriculumLineForLinkCtrl.SELECTED_LINK_CURRICULUM_LINE, data);
            arg.put(WinSearchCurriculumLineForLinkCtrl.SELECTED_INST, selectedIdInst);
            arg.put(WinSearchCurriculumLineForLinkCtrl.SELECTED_FOS, selectedFos);
            arg.put(WinSearchCurriculumLineForLinkCtrl.ACTION_ON_SELECTED_CURRICULUM_LINE, (WinSearchCurriculumLineForLinkCtrl.SelectSubjectListener) selectedCurriculumLine -> {

                ibCourseMine.setValue(selectedCurriculumLine.getCourse());
                data.setCourseMine(selectedCurriculumLine.getCourse());
                ibSemesterNumberMine.setValue(selectedCurriculumLine.getSemesterNumber());
                data.setSemesterNumberMine(selectedCurriculumLine.getSemesterNumber());
                tbSubjectNameMine.setValue(selectedCurriculumLine.getSubjectName());
                data.setSubjectNameMine(selectedCurriculumLine.getSubjectName());

                callSearch.run();
            });

            ComponentHelper.createWindow("/mine/curriculumLine/winSearchCurriculumLineForLink.zul", "winSearchCurriculumLineForLink", arg).doModal();
        });
    }

    private String getStringFromInteger(Integer value) {
        return value == null ? "" : String.valueOf(value);
    }

    private void saveLinkCurriculumLine(LinkCurriculumLineResponse selectedLine) {

        apiSyncMineLinkCurriculumLineService.linkSubjects(selectedLine.toRequest());
    }
}
