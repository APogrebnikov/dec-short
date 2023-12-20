package org.edec.curriculumScan.ctrl;

import org.apache.log4j.Logger;
import org.edec.curriculumScan.manager.CurriculumScanDAOManager;
import org.edec.curriculumScan.manager.CurriculumScanManager;
import org.edec.curriculumScan.model.Block;
import org.edec.curriculumScan.model.Competence;
import org.edec.curriculumScan.model.Curriculum;
import org.edec.curriculumScan.model.Subject;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexPageCtrl extends CabinetSelector {

    public final Logger log = Logger.getLogger(IndexPageCtrl.class.getName());

    @Wire
    private Textbox tbLog;

    @Wire
    private Button btStore;

    @Wire
    private Combobox cbYear;

    private ComponentService componentService = new ComponentServiceESOimpl();
    private CurriculumScanManager manager = new CurriculumScanManager();
    private CurriculumScanDAOManager dao = new CurriculumScanDAOManager();
    private List<Curriculum> curriculumList = new ArrayList<>();


    @Override
    protected void fill() {
        componentService.fillCmbYear(cbYear);
    }

    @Listen("onUpload = #btUpload;")
    public void myProcessUpload(UploadEvent event) throws ParserConfigurationException, IOException, SAXException {
        if(cbYear.getSelectedItem() != null){
            curriculumList=new ArrayList<>();
            for (Media med : event.getMedias()) {
                write("File: " + med.getName());
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setValidating(false);
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource isource = new InputSource(new ByteArrayInputStream(med.getStringData().getBytes()));
                Document doc = db.parse(isource);
                doc.getDocumentElement().normalize();

                Curriculum curriculum=manager.parseCurriculum(doc, cbYear.getSelectedItem().getValue(), dao);

                // Проверяем совпадения в уже существующих УП для генерации клона
                Long existId=dao.searchExistCurriculum(curriculum);

                // Если найден уже существующий - дать пользователям выбор Обновить ли существующий
                if (existId!=null){
                    DialogUtil.questionWithYesNoButtons("Загруженный план уже присутствует в базе данных. Обновить?", "Question",
                            (EventListener) evt -> {
                                if (evt.getName().equals("onYes")) {
                                    ///Сопоставление двух учебных планов
                                    Curriculum oldCurriculum = dao.getOneCurriculum(existId);

                                    Map<String, Object> arg = new HashMap<>();
                                    arg.put(WinCompareCurCtrl.OLD_CURRICULUM, oldCurriculum);
                                    arg.put(WinCompareCurCtrl.NEW_CURRICULUM, curriculum);
                                    arg.put(WinCompareCurCtrl.MAIN_PAGE, this);

                                    ComponentHelper.createWindow("winCompareCur.zul", "WinCompareCur", arg).doModal();

                                    return;
                                } else if (evt.getName().equals("onNo")) {
                                    PopupUtil.showWarning("Загрузка плана отменена");
                                    return;
                                }
                            });
                }

                printCurriculum(curriculum);
                curriculumList.add(curriculum);
            }
            btStore.setDisabled(false);
        } else {
            PopupUtil.showWarning("Сначала выберите год загрузки УП");
        }
    }

    @Listen("onClick = #btStore;")
    public void storeCurriculums () {
        for (Curriculum curriculum : curriculumList) {
            storeOneCurriculum(curriculum);
        }
    }

    public void storeOneCurriculum (Curriculum curriculum) {
        write(">> Try to save: " + curriculum.getQualificationCode());
        dao.createCurriculum(curriculum);
        write(">> Finish of Try");
    }

    public void printCurriculum (Curriculum cur) {
        write(">" + cur.getFileName());
        for (Block block : cur.getBlockList()) {
            write("\t" + block.getCode() + "  " +
                  (block.getCode() != block.getName() ? block.getName() : "" + (block.getSelectable() ? " Блок с выбором" : "")));
            for (Subject subject : block.getSubjectList()) {
                String fcStr = "";
                for (FormOfControlConst fc : subject.getFcList()) {
                    fcStr += "-" + fc.getName();
                }
                String compStr = "(";
                for (Competence competence : subject.getCompetenceList()) {
                    compStr += " " + competence.getName();
                }
                compStr += ")";
                write("\t\t" + subject.getName() + " (" + subject.getSemesterNumber() + " семестр) " + subject.getHoursSum() + "ч." + " " +
                      fcStr + "\t" + compStr);
            }
        }
    }

    public void write (String string) {
        tbLog.setValue(tbLog.getValue() + "\n" + string);
    }
}