package org.edec.contingentMovement.ctrl.individualCurriculum;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.edec.commons.model.GroupDirectionModel;
import org.edec.commons.model.StudentGroupModel;
import org.edec.contingentMovement.model.ResitRatingModel;
import org.edec.contingentMovement.report.model.ProtocolCommissionModel;
import org.edec.report.protocolResit.service.ProtocolResitService;
import org.edec.utility.constants.FormOfStudyConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.pdfViewer.ctrl.PdfViewerCtrl;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WinIndCurrProtocolCtrl extends SelectorComposer<Window> {

    private static final String TEMPLATE_FIO_ST = "$ФИО студента$";
    private static final String TEMPLATE_UNIVERSITY_NEW = "$Название университета новый$";
    private static final String TEMPLATE_UNIVERSITY_OLD = "$Название университета старый$";

    private static final String TEMPLATE_DIRECTION_NEW = "$направление восстановления$";
    public static final String TYPE_STUDENT_MOVE = "$вост/из академ$";
    public static final String TEMPLATE_DIRECTION_QUALIFICATION = "$направ/спец$";
    private static final String TEMPLATE_DOCUMENT_DATE = "$дата получения документа";
    private static final String TEMPLATE_DOCUMENT_NUMBER = "$номер документа$";

    private static final String TEMPLATE_GROUP_NAME = "$Название группы$";
    private static final String TEMPLATE_GROUP_COURSE = "$Курс$";
    private static final String TEMPLATE_GROUP_FOS = "$Форма обучения$";
    private static final String TEMPLATE_BASED_ON_DOCUMENT = "учетной карточки";
    private static final String TEMPLATE_REFERENCE = "справки о периоде обучения";
    private static final String TEMPLATE_GET = "$Дата и номер$";
    private static final String TEMPLATE_AGENDA = "Перезачет учебных дисциплин, практики и переаттестация " + TYPE_STUDENT_MOVE +
            " в " + TEMPLATE_UNIVERSITY_NEW + " на " + TEMPLATE_GROUP_COURSE + " курс, " +
            "в " + TEMPLATE_GROUP_NAME + " группу, на " + TEMPLATE_GROUP_FOS + " форму обучения " +
            "на " + TEMPLATE_DIRECTION_QUALIFICATION + " " + TEMPLATE_DIRECTION_NEW + " " +
            TEMPLATE_FIO_ST + " на основании " + TEMPLATE_BASED_ON_DOCUMENT + ", выданной " +
            TEMPLATE_UNIVERSITY_OLD + TEMPLATE_GET;


    static final String ARG_SELECTED_FIO_STUDENT = "selected_fio_student";
    static final String ARG_SELECTED_STUDENT = "selected_student";
    static final String ARG_SELECTED_GROUP = "selected_group";
    static final String ARG_SELECTED_RESIST_SUBJECTS = "selected_subjects";
    static final String ARG_SELECTED_RESIT_SUBJECT_SECOND_TABLE = "selected_subjects_second_table";

    @Wire
    private Datebox dbIndCurrProtocolDateCommission, dbIndCurrProtocolRecordbook, dbDateResit;
    @Wire
    private Intbox ibIndCurrProtocolNumber;
    @Wire
    private Label lIndCurrProtocolAgenda, lRecordBook, lDateOfIssue;
    @Wire
    private Textbox tbIndCurrProtocolFioStudent, tbIndCurrChairman, tbIndCurrProtocolCommission, tbIndCurrProtocolDirectionOld;
    @Wire
    private Combobox cmbStatus, cmbType;
    @Wire
    private Radio rbIndexCard, rbOtherInst;

    @Wire
    private Textbox tbIndCurrProtocolDirectionNew, tbIndCurrProtocolRecordbook, tbIndCurrProtocolUniversityNew,
            tbIndCurrProtocolUniversityOld, tbIndCurrProtocolGroupNew, tbIndCurrProtocolGroupCourse,
            tbIndCurrProtocolGroupFos, tbIndCurrProtocolBasedDocument;
    @Wire
    private Textbox tbIndCurrProtocolFioStudentFirstSection;

    @Wire
    private Vbox vbIndCurrProtocolCommission;

    private JasperReportService jasperReportService = new JasperReportService();

    private List<ResitRatingModel> resistSubjects;
    private List<ResitRatingModel> resitSubjectsSecondTable;
    private List<String> commissionMembers;

    private GroupDirectionModel selectedNewGroup;
    private Integer oldQualification;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        selectedNewGroup = (GroupDirectionModel) Executions.getCurrent().getArg().get(ARG_SELECTED_GROUP);
        StudentGroupModel selectedStudent = (StudentGroupModel) Executions.getCurrent().getArg().get(ARG_SELECTED_STUDENT);
        resistSubjects = (List<ResitRatingModel>) Executions.getCurrent().getArg().get(ARG_SELECTED_RESIST_SUBJECTS);
        resitSubjectsSecondTable = (List<ResitRatingModel>) Executions.getCurrent().getArg().get(ARG_SELECTED_RESIT_SUBJECT_SECOND_TABLE);

        tbIndCurrProtocolDirectionNew.setValue(selectedNewGroup.getCode() + " \"" + selectedNewGroup.getDirectiontitle() + "\"");
        tbIndCurrProtocolGroupNew.setValue(selectedNewGroup.getGroupname());
        tbIndCurrProtocolGroupCourse.setValue(String.valueOf(selectedNewGroup.getCourse()));
        tbIndCurrProtocolGroupFos.setValue(selectedNewGroup.getFormOfStudy() == FormOfStudyConst.FULL_TIME.getType() ? "очную" :
                                           (selectedNewGroup.getDistanceType() == 1 ? "очно-заочную" : "заочную"));

        if (selectedStudent != null) {
            tbIndCurrProtocolDirectionOld.setValue(selectedStudent.getDirectionCode() + " \"" + selectedStudent.getDirectionName() + "\"");
            oldQualification = selectedStudent.getQualification();
            tbIndCurrProtocolFioStudent.setValue(selectedStudent.getFio());
            tbIndCurrProtocolFioStudentFirstSection.setValue(selectedStudent.getFio());
        } else {
            tbIndCurrProtocolFioStudent.setValue((String) Executions.getCurrent().getArg().get(ARG_SELECTED_FIO_STUDENT));
            tbIndCurrProtocolFioStudentFirstSection.setValue((String) Executions.getCurrent().getArg().get(ARG_SELECTED_FIO_STUDENT));
        }

        if(!resitSubjectsSecondTable.isEmpty()){
            dbDateResit.setDisabled(false);
        }

        commissionMembers = new ArrayList<>();

        if (oldQualification == null) {
            oldQualification = 2;
        } else if (oldQualification == 1) {
            cmbType.setSelectedIndex(1);
        }
        fillAgenda();
    }

    @Listen("onChange = #tbIndCurrProtocolUniversityNew; onChange = #tbIndCurrProtocolGroupCourse;" +
            "onChange = #tbIndCurrProtocolGroupNew; onChange = #tbIndCurrProtocolGroupFos;" +
            "onChange = #tbIndCurrProtocolDirectionNew; onChange = #tbIndCurrProtocolFioStudent;" +
            "onChange = #tbIndCurrProtocolFioStudent; onChange = #cmbStatus;" +
            "onChange = #tbIndCurrProtocolUniversityOld; onClick = #tbIndCurrProtocolRecordbook;" +
            "onChange = #tbIndCurrProtocolGroupCourse;" +
            "onChange = #dbIndCurrProtocolRecordbook; onClick = #rbIndexCard; onClick = #rbOtherInst")
    public void fillAgenda() {
        lIndCurrProtocolAgenda.setValue(getAgendaWithoutHtmlTags());
    }

    private String getAgendaWithoutHtmlTags() {
        return getAgenda().replaceAll("<b>", "")
                .replaceAll("</b>", "");
    }

    private JasperReport getJasperReportProtocol() {

        ProtocolCommissionModel protocol = new ProtocolCommissionModel();
        protocol.setAgenda(getAgenda());
        protocol.setChairman(tbIndCurrChairman.getValue());
        protocol.setDateCommission(dbIndCurrProtocolDateCommission.getValue());
        protocol.setFioStudent(tbIndCurrProtocolFioStudentFirstSection.getValue());
        protocol.setResitSubjects(resistSubjects);
        protocol.setNumberProtocol(ibIndCurrProtocolNumber.getValue());
        protocol.setСommissionMembers(commissionMembers);
        protocol.setDirection(tbIndCurrProtocolDirectionOld.getValue());
        protocol.setInstitute(tbIndCurrProtocolUniversityNew.getValue());
        protocol.setRecordbook(tbIndCurrProtocolRecordbook.getValue());
        protocol.setDirectionRestore(tbIndCurrProtocolDirectionNew.getValue());
        protocol.setRecordbookDate(DateConverter.convertDateToString(dbIndCurrProtocolRecordbook.getValue()));
        protocol.setTemplateFioStudent(tbIndCurrProtocolFioStudent.getValue());
        protocol.setSelectedQualification(selectedNewGroup.getQualification());
        protocol.setOldQualification(oldQualification);
        protocol.setDateResit(dbDateResit.getValue());

        if (cmbStatus.getSelectedIndex() == 0) {
            protocol.setTypeStudentMove("восстанавливающегося");
            protocol.setTypeMovement("восстанавливается");
        } else if (cmbStatus.getSelectedIndex() == 1) {
            protocol.setTypeStudentMove("переводящегося");
            protocol.setTypeMovement("переводится");
        } else {
            protocol.setTypeStudentMove("выходящего из академического отпуска");
            protocol.setTypeMovement("выходит из академического отпуска");
        }

        if (rbOtherInst.isSelected()) {
            protocol.setTypeProtocol(2); //справка и название университета
        } else {
            protocol.setTypeProtocol(1); //зачетка
        }
        return jasperReportService.getIndCurrProtocol(protocol);
    }

    private void getApachePoiReportProtocol(Boolean isPdf) throws IOException {
        ProtocolCommissionModel protocol = new ProtocolCommissionModel();

        protocol.setAgenda(getAgenda());
        protocol.setChairman(tbIndCurrChairman.getValue());
        protocol.setDateCommission(dbIndCurrProtocolDateCommission.getValue());
        protocol.setFioStudent(tbIndCurrProtocolFioStudentFirstSection.getValue());
        protocol.setResitSubjects(resistSubjects);
        protocol.setResitSubjectSecondParagraph(resitSubjectsSecondTable);
        protocol.setNumberProtocol(ibIndCurrProtocolNumber.getValue());
        protocol.setСommissionMembers(commissionMembers);
        protocol.setDirection(tbIndCurrProtocolDirectionOld.getValue());
        protocol.setInstitute(tbIndCurrProtocolUniversityNew.getValue());
        protocol.setRecordbook(tbIndCurrProtocolRecordbook.getValue());
        protocol.setDirectionRestore(tbIndCurrProtocolDirectionNew.getValue());
        protocol.setRecordbookDate(DateConverter.convertDateToString(dbIndCurrProtocolRecordbook.getValue()));
        protocol.setTemplateFioStudent(tbIndCurrProtocolFioStudent.getValue());
        protocol.setSelectedQualification(selectedNewGroup.getQualification());
        protocol.setOldQualification(oldQualification);
        protocol.setDateResit(dbDateResit.getValue());

        if (cmbStatus.getSelectedIndex() == 0) {
            protocol.setTypeStudentMove("восстанавливающегося");
            protocol.setTypeMovement("восстанавливается");
        } else if (cmbStatus.getSelectedIndex() == 1) {
            protocol.setTypeStudentMove("переводящегося");
            protocol.setTypeMovement("переводится");
        } else {
            protocol.setTypeStudentMove("выходящего из академического отпуска");
            protocol.setTypeMovement("выходит из академического отпуска");
        }

        if (rbOtherInst.isSelected()) {
            protocol.setTypeProtocol(2); //справка и название университета
        } else {
            protocol.setTypeProtocol(1); //зачетка
        }

        XWPFDocument protocolResit = new ProtocolResitService().createProtocol(protocol);

        String fileName = "Протокол заседания аттестационной комиссии (" + protocol.getFioStudent() + ")";

        if(isPdf) {
            //TODO: добавить формирование PDF
            //document must be written so underlaaying objects will be committed
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            protocolResit.write(out);
            protocolResit.close();

            protocolResit = new XWPFDocument(new ByteArrayInputStream(out.toByteArray()));
            PdfOptions options = PdfOptions.create();
            PdfConverter converter = (PdfConverter)PdfConverter.getInstance();
            converter.convert(protocolResit, new FileOutputStream(fileName + ".pdf"), options);

            Map<String, Object> arg = new HashMap<>();
            arg.put(PdfViewerCtrl.FILE, new File(fileName + ".pdf"));
            ComponentHelper.createWindow("/utility/pdfViewer/index.zul", "winPdfViewer", arg).doModal();
        }else {
            File file = new File(fileName + ".docx");
            FileOutputStream outFile = new FileOutputStream(file);
            protocolResit.write(outFile);
            Filedownload.save(file, null);
        }
    }


    @Listen("onClick = #btnIndCurrProtocolAddCommission; onOK = #tbIndCurrProtocolCommission;")
    public void addCommissionInProcol() {
        if (tbIndCurrProtocolCommission.getValue().equals("")) {
            return;
        }
        String fioCommission = tbIndCurrProtocolCommission.getValue();
        Hbox hboxComm = new Hbox();
        hboxComm.setParent(vbIndCurrProtocolCommission);

        Label lComm = new Label(fioCommission);
        Button btnDelCommission = new Button("Удалить");
        lComm.setParent(hboxComm);
        btnDelCommission.setParent(hboxComm);
        commissionMembers.add(fioCommission);
        btnDelCommission.addEventListener(Events.ON_CLICK, event -> {
            commissionMembers.remove(fioCommission);
            vbIndCurrProtocolCommission.removeChild(hboxComm);
        });
    }

    @Listen("onClick = #btnIndCurrProtocolPrint;")
    public void showPdf() throws IOException {
        getApachePoiReportProtocol(true);
        getJasperReportProtocol().showPdf();

    }

    @Listen("onClick = #btnIndCurrProtocolDocx;")
    public void downloadProtocolDocx() throws IOException {
        getApachePoiReportProtocol(false);
        //getJasperReportProtocol().downloadDocx();
    }

    private String getAgenda() {

        String result = TEMPLATE_AGENDA;

        result = fillStringByTemplateAndTextbox(result, tbIndCurrProtocolUniversityNew, TEMPLATE_UNIVERSITY_NEW);
        result = fillStringByTemplateAndTextbox(result, tbIndCurrProtocolGroupCourse, TEMPLATE_GROUP_COURSE);
        result = fillStringByTemplateAndTextbox(result, tbIndCurrProtocolGroupNew, TEMPLATE_GROUP_NAME);
        result = fillStringByTemplateAndTextbox(result, tbIndCurrProtocolGroupFos, TEMPLATE_GROUP_FOS);
        result = fillStringByTemplateAndTextbox(result, tbIndCurrProtocolDirectionNew, TEMPLATE_DIRECTION_NEW);
        result = fillStringByTemplateAndTextbox(result, tbIndCurrProtocolFioStudent, TEMPLATE_FIO_ST);
        result = fillStringByTemplateAndTextbox(result, tbIndCurrProtocolUniversityOld, TEMPLATE_UNIVERSITY_OLD);

        // студент восстанавливается
        if (cmbStatus.getSelectedIndex() == 0) {
            result = result.replace(TYPE_STUDENT_MOVE, "востанавливающегося");
        } else if (cmbStatus.getSelectedIndex() == 1) { // студент переводится
            result = result.replace(TYPE_STUDENT_MOVE, "переводящегося");
        } else { // выходит из академ. отпуска
            result = result.replace(TYPE_STUDENT_MOVE, "выходящего из академического отпуска");
        }

        if (cmbType.getSelectedIndex() == 0) {
            oldQualification = 2;
        } else {
            oldQualification = 1;
        }

        // специальность
        if (oldQualification == 1) {
            result = result.replace(TEMPLATE_DIRECTION_QUALIFICATION, "специальности");
        } else {
            result = result.replace(TEMPLATE_DIRECTION_QUALIFICATION, "направление подготовки");
        }


        if(rbOtherInst.isSelected()) {
            result = result.replace(TEMPLATE_BASED_ON_DOCUMENT, TEMPLATE_REFERENCE);
            result = result.replace(TEMPLATE_GET, " от " + TEMPLATE_DOCUMENT_DATE + " № " + TEMPLATE_DOCUMENT_NUMBER);
            result = fillStringByTemplateAndTextbox(result, tbIndCurrProtocolRecordbook, TEMPLATE_DOCUMENT_NUMBER);

        }else{
            result = fillStringByTemplateAndTextbox(result, tbIndCurrProtocolRecordbook, TEMPLATE_DOCUMENT_NUMBER);
            result = result.replace(TEMPLATE_GET, ".");
        }

        if (dbIndCurrProtocolRecordbook.getValue() != null) {
            result = result.replace(TEMPLATE_DOCUMENT_DATE, DateConverter.convertDateToString(dbIndCurrProtocolRecordbook.getValue()));
        }

        return result;
    }

    private String fillStringByTemplateAndTextbox(String result, @NonNull Textbox textbox, String template) {
        if (!StringUtils.isBlank(textbox.getValue())) {
            return result.replace(template, textbox.getValue());
        }
        return result;
    }

    @Listen("onClick = #rbIndexCard;")
    public void disableOtherProtocolResit(){
        if (rbIndexCard.isSelected()) {
            dbIndCurrProtocolRecordbook.setDisabled(true);
            tbIndCurrProtocolRecordbook.setDisabled(true);
        }
    }

    @Listen("onClick = #rbOtherInst;")
    public void getOtherProtocolResit() {
        if (rbOtherInst.isSelected()) {
            dbIndCurrProtocolRecordbook.setDisabled(false);
            tbIndCurrProtocolRecordbook.setDisabled(false);
        }
    }
}
