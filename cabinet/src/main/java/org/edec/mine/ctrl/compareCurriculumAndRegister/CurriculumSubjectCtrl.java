package org.edec.mine.ctrl.compareCurriculumAndRegister;

import org.edec.cloud.sync.mine.api.compareRegisterAndCurriculum.data.filter.CurriculumSubjectFilter;
import org.edec.cloud.sync.mine.api.compareRegisterAndCurriculum.service.ApiSyncMineCompareRegisterAndCurriculumImpl;
import org.edec.cloud.sync.mine.api.compareRegisterAndCurriculum.service.ApiSyncMineCompareRegisterAndCurriculumService;
import org.edec.cloud.sync.mine.api.utility.constants.MineTypeOfRegisterEnum;
import org.edec.commons.component.MultipleSelectComponent;
import org.edec.mine.ctrl.compareCurriculumAndRegister.renderer.CurriculumSubjectRenderer;
import org.edec.utility.zk.IncludeSelector;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CurriculumSubjectCtrl extends IncludeSelector {

    private MultipleSelectComponent<MineTypeOfRegisterEnum, MineTypeOfRegisterEnum> mscTypeOfRegister;

    @Wire
    private Intbox ibSemesterNumber;
    @Wire
    private Listbox lbCurriculumSubject;
    @Wire
    private Textbox tbGroupName, tbSubjectName;
    @Wire
    private Vlayout vlTypeOfRegister;

    private final ApiSyncMineCompareRegisterAndCurriculumService service = new ApiSyncMineCompareRegisterAndCurriculumImpl();

    @Override
    protected void fill() {

        lbCurriculumSubject.setItemRenderer(new CurriculumSubjectRenderer(
                true, getSelectedInstitute().getIdInst(), getSelectedFormOfStudy().getType()
        ));
        createSemMultiSelectComponent();
    }

    private void createSemMultiSelectComponent() {

        List<MineTypeOfRegisterEnum> typeOfRegisters = Arrays.stream(MineTypeOfRegisterEnum.values())
                .collect(Collectors.toList());
        mscTypeOfRegister = new MultipleSelectComponent<>(typeOfRegisters, typeOfRegister -> typeOfRegister);
        mscTypeOfRegister.setParent(vlTypeOfRegister);
        mscTypeOfRegister.addEventListener(Events.ON_CHANGING, event -> searchCurriculumSubjects());
        mscTypeOfRegister.setHflex("1");
        mscTypeOfRegister.selectAllItems();
    }

    @Listen("onClick = #btnSearch")
    public void searchCurriculumSubjects() {

        Clients.showBusy(lbCurriculumSubject, "Загрузка данных");
        Events.echoEvent("onLater", lbCurriculumSubject, null);
    }

    @Listen("onLater = #lbCurriculumSubject")
    public void lazyLoadData() {

        CurriculumSubjectFilter filter = CurriculumSubjectFilter
                .builder(getSelectedInstitute().getIdInst(), getSelectedFormOfStudy().getType())

                .semesterNumber(ibSemesterNumber.getValue()).groupName(tbGroupName.getValue())
                .subjectName(tbSubjectName.getValue()).selectedTypeOfRegister(mscTypeOfRegister.getSelectedValues())
                .build();
        lbCurriculumSubject.setModel(new ListModelList<>(service.findCurriculumSubjectsByFilter(filter)));
        lbCurriculumSubject.renderAll();

        Clients.clearBusy(lbCurriculumSubject);
    }
}
