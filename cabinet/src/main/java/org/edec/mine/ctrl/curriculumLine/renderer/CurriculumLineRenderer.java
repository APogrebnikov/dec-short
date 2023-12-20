package org.edec.mine.ctrl.curriculumLine.renderer;

import org.edec.cloud.sync.mine.api.curriculumLine.data.response.CurriculumLineResponse;
import org.edec.cloud.sync.mine.api.curriculumLine.service.ApiSyncMineCurriculumLineImpl;
import org.edec.cloud.sync.mine.api.curriculumLine.service.ApiSyncMineCurriculumLineService;
import org.edec.mine.ctrl.curriculumLine.WinCompareSubjectsCtrl;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;


public class CurriculumLineRenderer implements ListitemRenderer<CurriculumLineResponse> {

    private ApiSyncMineCurriculumLineService apiSyncMineCurriculumLineService = new ApiSyncMineCurriculumLineImpl();

    private Runnable searchSubjectAction;

    public CurriculumLineRenderer(Runnable searchSubjectAction) {
        this.searchSubjectAction = searchSubjectAction;
    }

    @Override
    public void render(Listitem li, CurriculumLineResponse data, int index) throws Exception {

        li.setValue(data);

        new Listcell(data.getGroupname()).setParent(li);

        //subject
        Listcell lcSubjectNameCabinet = new Listcell();
        lcSubjectNameCabinet.setParent(li);

        Textbox tbSubjectNameMine = new Textbox(data.getSubjectNameCabinet());
        tbSubjectNameMine.setParent(lcSubjectNameCabinet);
        tbSubjectNameMine.setHflex("1");
        tbSubjectNameMine.addEventListener(Events.ON_OK, event -> {
            data.setSubjectNameCabinet(tbSubjectNameMine.getValue());
            saveCurriculumLine(data);
        });

        //course
        Listcell lcCourseMine = new Listcell();
        lcCourseMine.setParent(li);

        Intbox ibCourseMine = new Intbox();
        ibCourseMine.setParent(lcCourseMine);
        if (data.getCourseCabinet() != null) {
            ibCourseMine.setValue(data.getCourseCabinet());
        }
        ibCourseMine.setHflex("1");
        ibCourseMine.addEventListener(Events.ON_OK, event -> {
            data.setCourseCabinet(ibCourseMine.getValue());
            saveCurriculumLine(data);

        });

        //semester
        Listcell lcSemesterNumberMine = new Listcell();
        lcSemesterNumberMine.setParent(li);

        Intbox ibSemesterNumberMine = new Intbox();
        ibSemesterNumberMine.setParent(lcSemesterNumberMine);
        if (data.getSemesterNumberCabinet() != null) {
            ibSemesterNumberMine.setValue(data.getSemesterNumberCabinet());
        }
        ibSemesterNumberMine.setHflex("1");
        ibSemesterNumberMine.addEventListener(Events.ON_OK, event -> {
            data.setSemesterNumberCabinet(ibSemesterNumberMine.getValue());
            saveCurriculumLine(data);
        });


        new Listcell(data.getSubjectName()).setParent(li);
        new Listcell(getStringFromInteger(data.getCourse())).setParent(li);
        new Listcell(getStringFromInteger(data.getSemesterNumber())).setParent(li);

        if (data.getSubjectNameCabinet() == null) {
            li.setStyle("background: #FF7373;");
        }

        li.addEventListener(Events.ON_RIGHT_CLICK, event -> {

            if (data.isUnlinked()) {
                DialogUtil.questionWithYesNoButtons("Создать новый предмет?", "Новый предмет", eventDialog -> PopupUtil.showWarning("Функция еще не реализована"));
            } else {
                showCompareWindow(li.getListbox().getParent().getParent(), data);
            }
        });
    }

    private void saveCurriculumLine(CurriculumLineResponse data) {

        apiSyncMineCurriculumLineService.updateCurriculumLine(data.getId(), data);
        searchSubjectAction.run();
    }

    private String getStringFromInteger(Integer value) {
        return value == null ? "" : String.valueOf(value);
    }

    private void showCompareWindow(Component parentComponent, CurriculumLineResponse data) {

        WinCompareSubjectsCtrl winCompareSubjectsCtrl = new WinCompareSubjectsCtrl(data);
        winCompareSubjectsCtrl.setParent(parentComponent);
        winCompareSubjectsCtrl.addEventListener("onFinishSync", event -> searchSubjectAction.run());
        winCompareSubjectsCtrl.doModal();
    }
}
