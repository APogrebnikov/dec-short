package org.edec.kcp.ctrl;

import org.edec.kcp.ctrl.renderer.KCPFullRender;
import org.edec.kcp.model.KCPFullModel;
import org.edec.kcp.service.impl.KCPService;
import org.edec.kcp.service.impl.PrintService;
import org.edec.main.auth.AuthInit;
import org.edec.subjectsAnalysis.service.SubjectsAnalysisService;
import org.edec.subjectsAnalysis.service.impl.SubjectAnalysisServiceImpl;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.QualificationConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.report.POIUtility;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Контроллер страницы анализа КЦП
 */
public class IndexPageCtrl extends CabinetSelector {
    @Wire
    private Combobox cmbTypeDebt;
    @Wire
    private Spinner spinDebt;
    @Wire
    private Listbox lbMain;
    @Wire
    private Checkbox chEngineer, chBachelor, chMaster, chOch, chZaoch;
    @Wire
    private Button btnPrint;

    private ComponentService componentService = new ComponentServiceESOimpl();
    private SubjectsAnalysisService service = new SubjectAnalysisServiceImpl();

    private KCPService kcpService = new KCPService();
    private PrintService printService = new PrintService();

    private FormOfStudy fos;
    private SemesterModel selectedSem;
    private Integer course = 0;
    private Integer score = null;

    List<KCPFullModel> currentListKCP = new ArrayList<>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        fillKCPFullList();
    }

    @Override
    protected void fill() {

    }

    @Listen("onClick = #btnSearch")
    public void search() {
        fillKCPFullList();
    }

    @Listen("onClick = #btnPrint")
    public void print() {
        AMedia aMedia = new AMedia("Контроль КЦП  (" + DateConverter.convertDateToString(new Date()) + ").xls",
                "xls", "application/xls", printService.generateReportXlsx(currentListKCP)
        );
        Filedownload.save(aMedia);
    }

    public void fillKCPFullList() {
        String conditionDebt = ">=";
        if (cmbTypeDebt.getSelectedItem() != null) conditionDebt = cmbTypeDebt.getSelectedItem().getValue();
        Integer debt = spinDebt.getValue();
        List<Integer> qualification = getQualification();
        List<Integer> formofstudy = getFormOfStudy();

        currentListKCP = kcpService.getFullModel(qualification, formofstudy, debt, conditionDebt);
        lbMain.setModel(new ListModelList<>(currentListKCP));
        lbMain.setItemRenderer(new KCPFullRender(this));
    }

    public List<Integer> getQualification() {
        List<Integer> res = new ArrayList<>();
        if (chBachelor.isChecked()) res.add(QualificationConst.BACHELOR.getValue());
        if (chMaster.isChecked()) res.add(QualificationConst.MASTER.getValue());
        if (chEngineer.isChecked()) res.add(QualificationConst.SPECIALIST.getValue());
        return res;
    }

    public List<Integer> getFormOfStudy() {
        List<Integer> res = new ArrayList<>();
        if (chOch.isChecked()) res.add(FormOfStudy.FULL_TIME.getType());
        if (chZaoch.isChecked()) res.add(FormOfStudy.EXTRAMURAL.getType());
        return res;
    }

    public void updateKCP(KCPFullModel kcpFullModel) {
        int res = kcpService.updateKCPModel(kcpFullModel);
        fillKCPFullList();
    }

}
