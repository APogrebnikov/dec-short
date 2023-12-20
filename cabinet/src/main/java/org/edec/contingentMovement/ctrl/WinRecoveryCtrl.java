package org.edec.contingentMovement.ctrl;

import org.edec.contingentMovement.ctrl.renderer.GroupLbRenderer;
import org.edec.contingentMovement.service.ContingentMovementService;
import org.edec.contingentMovement.service.impl.ContingentMovementImpl;
import org.edec.model.ActionModel;
import org.edec.model.GroupModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.OrderActionConst;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.*;

/**
 * Created by APogrebnikov
 */
public class WinRecoveryCtrl extends SelectorComposer<Component> {
    public static final String SELECTED_STUDENT = "selected_student";
    public static final String MAIN_PAGE = "main_page";
    public static final String ACTION_PAGE = "action";

    enum Actions {RECOVERY, TRANSFER, NEW}

    @Wire
    private Checkbox chGovernment, chTrustagreement;
    @Wire
    private Combobox cmbSem, cmbFormOfStudyRecovery;
    @Wire
    private Label lFIO, lSem, lDateStart, lGroup, lCaption;
    @Wire
    private Listbox lbGroup;
    @Wire
    private Window winRecovery;
    @Wire
    private Textbox tbGroupFilter, tbOrderNumber;
    @Wire
    private Datebox dpFrom, dpLimit;
    @Wire
    private Button btnRecovery;
    @Wire
    private Button btnCreateNewStudent;

    private ComponentService componentService = new ComponentServiceESOimpl();
    private ContingentMovementService contingentMovementService = new ContingentMovementImpl();

    private StudentStatusModel selectedStudentStatusModel;
    private List<GroupModel> currentGroupModel;
    private IndexPageCtrl mainPage;
    private Actions currentAction;
    private String msgAlert, msgQuest;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        selectedStudentStatusModel = (StudentStatusModel) Executions.getCurrent().getArg().get(SELECTED_STUDENT);
        mainPage = (IndexPageCtrl) Executions.getCurrent().getArg().get(MAIN_PAGE);
        currentAction = (Actions) Executions.getCurrent().getArg().get(ACTION_PAGE);

        renameFieldsByAction();

        //curProxyUser = (ProxyUser) Executions.getCurrent().getDesktop().getSession().getAttribute("curProxyUser");

        lbGroup.setItemRenderer(new GroupLbRenderer());

        if (currentAction == Actions.NEW) {
            btnCreateNewStudent.setVisible(true);
        } else {
            if (selectedStudentStatusModel.getIdSSS() != null) {
                chGovernment.setChecked(selectedStudentStatusModel.getGovernmentFinanced());
                chTrustagreement.setChecked(selectedStudentStatusModel.getTrustAgreement());
            }
            lFIO.setValue(selectedStudentStatusModel.getFio());
        }

        //TODO: Прописать для ШАХТ действий
        /*
        lSem.setValue(ConverterDate.convert2dateToString(selectedStudentStatusModel.getDateofbegin(), selectedStudentStatusModel.getDateofend())
                + "("+(selectedStudentStatusModel.getSeason()==0?"осень":"весна") +")");
        */
    }

    /**
     * Переименовываем поля в зависимости от действия
     */
    public void renameFieldsByAction() {

        String action = (currentAction == Actions.RECOVERY) ? "востановить" : "перевести";

        String lTextSem = "На какой семестр " + action + ":";
        lSem.setValue(lTextSem);

        String caption = (currentAction == Actions.RECOVERY) ? "Востановление" : "Перевод";
        lCaption.setValue(caption);

        String lTextDateStart = "Выберите с какого числа " + action + ":";
        lDateStart.setValue(lTextDateStart);

        String lTextGroup = "В какую группу " + action + ":";
        lGroup.setValue(lTextGroup);

        msgAlert = "Сначала выберите группу, в которую " + action + " студента!";
        msgQuest = "Вы уверены, что хотите " + action + " его в выбранную группу?";

        btnRecovery.setLabel((currentAction == Actions.RECOVERY) ? "Востановить" : "Перевести");
    }

    @Listen("onCreate = #cmbFormOfStudyRecovery")
    public void afterCreateCmb() {
        cmbFormOfStudyRecovery.setSelectedIndex(0);
        int formOfStudy = cmbFormOfStudyRecovery.getSelectedIndex() + 1;
        //TODO: Изменить институт
        componentService.fillCmbSem(cmbSem, ((Integer) (1)).longValue(), formOfStudy, null);
        Events.echoEvent("onFill", cmbSem, null);
    }

    @Listen("onFill = #cmbSem")
    public void afterFillCmbSem() {
        cmbSem.setSelectedIndex(0);
        currentGroupModel = contingentMovementService.getGroupsBySem(((SemesterModel) cmbSem.getSelectedItem().getValue()).getIdSem());
        lbGroup.setModel(new ListModelList<>(currentGroupModel));
    }

    @Listen("onChange = #cmbFormOfStudyRecovery")
    public void changeCmbFormOfStudyRecovery() {
        int formOfStudy = cmbFormOfStudyRecovery.getSelectedIndex() + 1;
        componentService.fillCmbSem(cmbSem, ((Integer) (1)).longValue(), formOfStudy, null);
        Events.echoEvent("onFill", cmbSem, null);
    }

    @Listen("onChange = #cmbSem")
    public void refreshLbGroup() {
        currentGroupModel = contingentMovementService.getGroupsBySem(((SemesterModel) cmbSem.getSelectedItem().getValue()).getIdSem());
        lbGroup.setModel(new ListModelList<>(currentGroupModel));
        lbGroup.renderAll();
    }

    @Listen("onChanging = #tbGroupFilter")
    public void filterLBGroup(InputEvent event) {
        List<GroupModel> filteredGroup = new ArrayList<>();
        for (GroupModel groupModel : currentGroupModel) {
            if (groupModel.getGroupname().toLowerCase().contains(event.getValue().toLowerCase())) {
                filteredGroup.add(groupModel);
            }
        }

        lbGroup.setModel(new ListModelList<>(filteredGroup));
        lbGroup.renderAll();
    }

    @Listen("onClick = #btnRecovery")
    public void clickRecovery() {
        if (lbGroup.getSelectedItem() == null) {
            PopupUtil.showWarning(msgAlert);
            return;
        }

        Messagebox.show(msgQuest, "Внимание!", org.zkoss.zhtml.Messagebox.YES | org.zkoss.zhtml.Messagebox.NO,
                        org.zkoss.zhtml.Messagebox.QUESTION, e -> {
                            if (Messagebox.ON_YES.equals(e.getName())) {

                                GroupModel group = lbGroup.getSelectedItem().getValue();

                                // Видимо это новый студент из Шахт?
                                // TODO: сначала проверить, может он уже есть у нас в системе?
                                if (selectedStudentStatusModel.getIdStudentCard() == null) {
                                    Long scId = contingentMovementService.createNewStudent(selectedStudentStatusModel);
                                    selectedStudentStatusModel = contingentMovementService.getStudentSCid(scId);
                                }

                                // Если студент выходит из академического отпуска, то сначала установить флаг академического отпуска
                                if(selectedStudentStatusModel.getAcademicLeave()!=null && selectedStudentStatusModel.getAcademicLeave()) {
                                    contingentMovementService.setAcademToPrevSSS(selectedStudentStatusModel.getIdSSS());
                                }

                                // Породить SSS и SR в новой группе
                                Long maxSSS = contingentMovementService.createSSSandSRinNewGroup(selectedStudentStatusModel.getIdStudentCard(),
                                                                                                 chTrustagreement.isChecked() ? 1 : 0,
                                                                                                 chGovernment.isChecked() ? 1 : 0,
                                                                                                 group.getIdDG()
                                );

                                if (maxSSS == null) {
                                    PopupUtil.showError("Не удалось перевести студента, обратитесь к администратору!");
                                    return;
                                } else {
                                    // Если проводят до приказа - указать флаг слушателя
                                    if (tbOrderNumber.getValue() == null || tbOrderNumber.getValue() == "") {
                                        contingentMovementService.setListenetFlag(maxSSS, true);
                                    }
                                }

                                // Если перевод был не из группы в нее же
                                if (selectedStudentStatusModel.getCurrentGroupId()!=null && !selectedStudentStatusModel.getCurrentGroupId().equals(group.getIdDG())) {
                                    // Удалить все лишние SSS если они созданы наперед
                                    contingentMovementService.deleteWasteSSS(selectedStudentStatusModel.getIdSSS(),
                                            ((SemesterModel) cmbSem.getSelectedItem().getValue()).getIdSem()
                                    );
                                }

                                // Установить в старые SR флаг notActual
                                contingentMovementService.setNotActualSR(selectedStudentStatusModel.getIdSSS());

                                // Установить новую группу студенту в CurrentGroup
                                contingentMovementService.updateCurrentGroup(maxSSS);

                                // Проапдейтить Таблицу перемещений
                                ActionModel newAction = new ActionModel();
                                newAction.setDateAction(new Date());
                                newAction.setDateFinish(dpLimit.getValue());
                                newAction.setDateStart(dpFrom.getValue());
                                newAction.setIdDicAction(OrderActionConst.RECOVERY.getValue());
                                newAction.setIdDicGroupFrom(selectedStudentStatusModel.getIdDG());
                                newAction.setIdDicGroupTo(group.getIdDG());
                                newAction.setIdDicScholarship(null);
                                newAction.setIdInstituteFrom(selectedStudentStatusModel.getIdInstitute());
                                newAction.setIdInstituteTo(group.getIdInstitute());
                                newAction.setIdLinkOrderStudentStatus(null);
                                newAction.setIdSemester(mainPage.getIdSem());
                                newAction.setIdStudentcard(selectedStudentStatusModel.getIdStudentCard());
                                newAction.setIdUser(mainPage.template.getCurrentUser().getIdHum());
                                newAction.setOrderNumber(tbOrderNumber.getValue());
                                newAction.setSeq(0);
                                // Для тестирования возможности
                                contingentMovementService.insertAction(newAction);

                                // Обновить отображение в таблице
                                mainPage.searchStudents();

                                // Оповестить об успешном переводе
                                PopupUtil.showInfo("Студент востановлен в группу " + group.getGroupname());

                                // Предложить перейти к перезачету оценок
                                String msg = "Перейти к перезачету оценок?";
                                Messagebox.show(msg, "Внимание!", org.zkoss.zhtml.Messagebox.YES | org.zkoss.zhtml.Messagebox.NO,
                                                org.zkoss.zhtml.Messagebox.QUESTION, e1 -> {
                                                    if (Messagebox.ON_YES.equals(e1.getName())) {
                                                        PopupUtil.showInfo("Добро пожаловать в перезачет оценок");
                                                    }
                                                }
                                );

                                // Убить окно
                                winRecovery.detach();
                            }
                        }
        );
    }

    @Listen("onClick=#btnCreateNewStudent")
    public void openCreateNewStudentWindow() {
        Map arg = new HashMap();
        arg.put(WinCreateNewStudentCtrl.MAIN_PAGE, this);
        ComponentHelper.createWindow("winCreateNewStudent.zul", "winCreateNewStudent", arg).doModal();
    }

    @Listen("onClick=#btnCancel")
    public void closeWindow() {
        winRecovery.detach();
    }

    public void setSelectedStudent(StudentStatusModel studentStatusModel) {
        selectedStudentStatusModel = studentStatusModel;
        lFIO.setValue(selectedStudentStatusModel.getFio());
        btnCreateNewStudent.setLabel("Создать другого студента");
    }
}
