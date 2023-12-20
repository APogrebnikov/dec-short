package org.edec.mine.ctrl.curriculumLine.renderer;

import org.edec.cloud.sync.mine.api.curriculumLine.data.response.CurriculumLineResponse;
import org.edec.mine.ctrl.curriculumLine.WinCurriculumLineAdditionalInfo;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class SearchCurriculumLineRenderer implements ListitemRenderer<CurriculumLineResponse> {

    private SelectSubjectListener listener;

    public SearchCurriculumLineRenderer(SelectSubjectListener listener) {
        this.listener = listener;
    }

    @Override
    public void render(Listitem li, CurriculumLineResponse data, int index) throws Exception {

        li.setValue(data);

        new Listcell(data.getGroupname()).setParent(li);
        new Listcell(data.getSubjectName()).setParent(li);
        new Listcell(getStringFromInteger(data.getCourse())).setParent(li);
        new Listcell(getStringFromInteger(data.getSemesterNumber())).setParent(li);

        Listcell lcAction = new Listcell();
        lcAction.setParent(li);

        Hlayout hlActionButtons = new Hlayout();
        hlActionButtons.setParent(lcAction);

        Button btnLinkSubjects = new Button("Сопоставить");
        btnLinkSubjects.setParent(hlActionButtons);
        btnLinkSubjects.addEventListener(Events.ON_CLICK, event -> linkSubjects(data));

        Button btnShowAdditionalInfo = new Button("Подробнее");
        btnShowAdditionalInfo.setParent(hlActionButtons);
        btnShowAdditionalInfo.addEventListener(Events.ON_CLICK, event -> showAdditionalWindow(li.getListbox().getParent().getParent(), data));
    }

    private String getStringFromInteger(Integer value) {
        return value == null ? "" : String.valueOf(value);
    }

    private void linkSubjects(CurriculumLineResponse selectedCurriculumLine) {
        listener.selectCurriculumLine(selectedCurriculumLine);
    }

    private void showAdditionalWindow(Component parentComponent, CurriculumLineResponse curriculumLine) {

        WinCurriculumLineAdditionalInfo winAdditionalInfo = new WinCurriculumLineAdditionalInfo(curriculumLine);
        winAdditionalInfo.setParent(parentComponent);
        winAdditionalInfo.doModal();
    }

    public interface SelectSubjectListener {

        void selectCurriculumLine(CurriculumLineResponse selectedCurriculumLine);
    }
}
