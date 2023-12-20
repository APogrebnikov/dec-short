package org.edec.reference.ctrl;

import lombok.extern.log4j.Log4j;
import org.edec.reference.model.ReferenceModel;
import org.edec.reference.service.ReferenceService;
import org.edec.reference.service.impl.ReferenceServiceImpl;
import org.edec.utility.constants.ReferenceType;
import org.edec.utility.constants.ReferenceTypeConst;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.*;

@Log4j
public class WinEditReferenceCtrl extends SelectorComposer<Component> {

    public static final String WINDOW_TYPE = "window_type";
    public static final String REFERENCE = "reference";
    public static final String UPDATE_REFERENCES_LIST = "update_references_list";
    public static final String INSTITUTE = "institute";
    public static final String CURRENT_USER = "current_user";
    public static final String STUDENT = "student";

    @Wire
    private Window winEditReference;

    @Wire
    private Label lbFromDate, lbFileNameReference, lbFileNameApplication;

    @Wire
    private Checkbox chbUnlimitedRefType;

    @Wire
    private Button btnRefSave, btnCancel, btnAddReferenceScan, btnAddApplicationScan;

    @Wire
    private Textbox tbBookNumber;

    @Wire
    private Datebox dbDateOfStart, dbDateOfEnd;

    @Wire
    private Hbox boxFiles;

    private Runnable updateReferencesList;
    private ReferenceModel reference;
    private String typeWindow;
    private String currentUser;
    private String student;
    private long idInst;

    private ReferenceService service = new ReferenceServiceImpl();

    Media mediaFileRefScan;
    Media mediaFileApplicationScan;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);

        typeWindow = (String) Executions.getCurrent().getArg().get(WINDOW_TYPE);
        updateReferencesList = (Runnable) Executions.getCurrent().getArg().get(UPDATE_REFERENCES_LIST);
        reference = (ReferenceModel) Executions.getCurrent().getArg().get(REFERENCE);
        idInst = (Long) Executions.getCurrent().getArg().get(INSTITUTE);
        currentUser = (String) Executions.getCurrent().getArg().get(CURRENT_USER);
        student = (String) Executions.getCurrent().getArg().get(STUDENT);

        init();
    }

    private void init () {
        winEditReference.setTitle(typeWindow);

        if (typeWindow.equals(ReferenceTypeConst.REFERENCE_INVALID_CREATE) ||
            typeWindow.equals(ReferenceTypeConst.REFERENCE_INVALID_EDIT)) {
            chbUnlimitedRefType.setVisible(true);
        }

        if (typeWindow.equals(ReferenceTypeConst.REFERENCE_DSPP_EDIT) || typeWindow.equals(ReferenceTypeConst.REFERENCE_INVALID_EDIT)) {
            tbBookNumber.setValue(reference.getBooknumber());
            dbDateOfStart.setValue(reference.getDateStart());
            dbDateOfEnd.setValue(reference.getDateFinish());
        }

        if (typeWindow.equals(ReferenceTypeConst.REFERENCE_INVALID_EDIT) && reference.getDateFinish() == null) {
            chbUnlimitedRefType.setChecked(true);
            dbDateOfEnd.setDisabled(true);
        }

        if (typeWindow.equals(ReferenceTypeConst.REFERENCE_ORPHAN_CREATE) || typeWindow.equals(ReferenceTypeConst.REFERENCE_ORPHAN_EDIT)) {
            tbBookNumber.setValue(reference.getBooknumber());
            dbDateOfStart.setValue(reference.getDateStart());
            chbUnlimitedRefType.setChecked(true);
            dbDateOfEnd.setDisabled(true);
            boxFiles.setVisible(false);
        }

        if (typeWindow.equals(ReferenceTypeConst.REFERENCE_VETERAN_CREATE) || typeWindow.equals(ReferenceTypeConst.REFERENCE_VETERAN_EDIT)) {
            tbBookNumber.setValue(reference.getBooknumber());
            dbDateOfStart.setValue(reference.getDateStart());
            chbUnlimitedRefType.setChecked(true);
            dbDateOfEnd.setDisabled(true);
            boxFiles.setVisible(true);
        }
    }

    @Listen("onClick = #btnRefSave")
    public void saveReference () {
        if (validateRefInformation()) {
            reference.setBooknumber(tbBookNumber.getValue());
            reference.setDateStart(dbDateOfStart.getValue());
            reference.setDateFinish(dbDateOfEnd.getValue());

            if (reference.getIdRef() == -1) {
                reference.setDateGet(new Date());

                if (typeWindow.equals(ReferenceTypeConst.REFERENCE_DSPP_CREATE)) {
                    reference.setRefType(ReferenceType.INDIGENT.getValue());
                } else if (typeWindow.equals(ReferenceTypeConst.REFERENCE_ORPHAN_CREATE)) {
                    reference.setRefType(ReferenceType.ORPHAN.getValue());
                } else if (typeWindow.equals(ReferenceTypeConst.REFERENCE_INVALID_CREATE)) {
                    reference.setRefType(ReferenceType.INVALID.getValue());
                } else if (typeWindow.equals(ReferenceTypeConst.REFERENCE_VETERAN_CREATE)) {
                    reference.setRefType(ReferenceType.VETERAN.getValue());
                }

                reference.setIdRef(service.createReference(reference));

                if (!typeWindow.equals(ReferenceTypeConst.REFERENCE_ORPHAN_CREATE)) {
                    reference.setUrl(service.createFiles(reference, idInst, mediaFileRefScan, mediaFileApplicationScan));
                }

                if (service.updateReference(reference)) {
                    PopupUtil.showInfo("Справка была успешно создана!");

                    String refType  = getRefTypeString(reference);

                    log.info("Пользователь " + currentUser + " создал справку " + refType + " у студента " +
                             student);
                } else {
                    PopupUtil.showError("Создать справку не удалось!");
                }
            } else {
                if (!typeWindow.equals(ReferenceTypeConst.REFERENCE_ORPHAN_CREATE) && !typeWindow.equals(ReferenceTypeConst.REFERENCE_ORPHAN_EDIT)) {
                    reference.setUrl(service.createFiles(reference, idInst, mediaFileRefScan, mediaFileApplicationScan));
                }

                if (service.updateReference(reference)) {
                    PopupUtil.showInfo("Справка была успешно обновлена!");

                    String refType = getRefTypeString(reference);

                    log.info("Пользователь " + currentUser + " изменил справку " + refType +" у студента " +
                             student);
                } else {
                    PopupUtil.showError("Обновление справки не удалось!");
                }
            }

            updateReferencesList.run();

            winEditReference.detach();
        }
    }

    private String getRefTypeString (ReferenceModel reference) {
        if (reference.getRefType() == ReferenceType.INDIGENT.getValue()) {
            return "УСЗН";
        } else if (reference.getRefType() == ReferenceType.ORPHAN.getValue()) {
            return "сироты";
        } else{
            return "об инвалидности";
        }
    }

    private boolean validateRefInformation () {
        if (tbBookNumber.getValue().equals("")) {
            PopupUtil.showWarning("Не указан номер записи из книги!");
            return false;
        }

        Date dateStart = dbDateOfStart.getValue();
        Date dateEnd = dbDateOfEnd.getValue();

        if ((dateStart == null || (!chbUnlimitedRefType.isChecked() && dateEnd == null))) {
            PopupUtil.showWarning("Дата начала справки и/или окончания не установлены!");
            return false;
        }

        if (!chbUnlimitedRefType.isChecked() && (dateStart.after(dateEnd) || dateStart.equals(dateEnd))) {
            PopupUtil.showWarning("Дата начала действия справки больше или равна дате окончания действия!");
            return false;
        }

        if (typeWindow.equals(ReferenceTypeConst.REFERENCE_DSPP_CREATE) || typeWindow.equals(ReferenceTypeConst.REFERENCE_INVALID_CREATE)) {
            if (mediaFileRefScan == null || mediaFileApplicationScan == null) {
                PopupUtil.showWarning("Не загружен скан справки и/или скан заявления!");
                return false;
            }
        }

        return true;
    }

    @Listen("onClick = #btnCancel")
    public void onCancel () {
        winEditReference.detach();
    }

    @Listen("onUpload = #btnAddReferenceScan")
    public void addReferenceScan (UploadEvent event) {
        mediaFileRefScan = event.getMedia();
        lbFileNameReference.setValue(mediaFileRefScan.getName());
    }

    @Listen("onUpload = #btnAddApplicationScan")
    public void addApplicationScan (UploadEvent event) {
        mediaFileApplicationScan = event.getMedia();
        lbFileNameApplication.setValue(mediaFileApplicationScan.getName());
    }

    @Listen("onCheck = #chbUnlimitedRefType")
    public void changeRefDates () {
        if (chbUnlimitedRefType.isChecked()) {
            dbDateOfEnd.setDisabled(true);
        } else {
            dbDateOfEnd.setDisabled(false);
        }
    }
}
