package org.edec.contingentMovement.ctrl;

import org.edec.contingentMovement.service.impl.ContingentMovementImpl;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.utility.zk.DialogUtil;
import org.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.Date;

/**
 * Created by dmmax / Modify by Alex
 */
public class WinEditStatusCtrl extends SelectorComposer<Component> {
    public static final String LIST_ITEM = "listItem";
    public static final String SELECTED_STUDENT = "selected_student";
    public static final String MAIN_PAGE = "main_page";

    public static final String ACADEM = "Академ";
    public static final String COMPLETED_EDUCATION = "Завершил обучение";
    public static final String DEDUCTED = "Отчислен";
    public static final String GET_SOCIAL = "Получение соц. стип.";
    public static final String GOVERNMENT = "Бюджет";
    public static final String GROUP_LEADER = "Староста";
    public static final String INVALID = "Инвалид";
    public static final String PROLONGATION = "Продление сессии";
    public static final String PUT_SOCIAL = "Подача на соц. стип.";
    public static final String SIROTA = "Сирота";
    public static final String TRANSFER_STUDENT = "Переводник";
    public static final String LISTENER = "Слушатель";
    public static final String TRUST_AGREEMENT = "Целевой договор";

    public static final String NAME_STATUS = "nameStatus";
    public static final String FLAG_ACTION = "flagAction";
    public static final String DATE_BEGIN = "dateBegin";
    public static final String DATE_END = "dateEnd";

    @Wire
    private Combobox cmbStatus;

    @Wire
    private Listbox lbStatus;

    @Wire
    private Label lFIO, lGroup, lSem, lCourse;

    @Wire
    private Window winEditStatus;

    private Listitem listitem;
    private StudentStatusModel studentStatusModel;
    private org.edec.contingentMovement.model.StudentStatusModel sss;

    private ContingentMovementImpl contingentMovement = new ContingentMovementImpl();

    private IndexPageCtrl mainPage;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        mainPage = (IndexPageCtrl) Executions.getCurrent().getArg().get(MAIN_PAGE);
        studentStatusModel = (StudentStatusModel) Executions.getCurrent().getArg().get(SELECTED_STUDENT);

        lFIO.setValue(studentStatusModel.getFio());
        lGroup.setValue(studentStatusModel.getGroupname());
        //lSem.setValue(convert2dateToString(studentStatusModel.getDateofbegin(), studentStatusModel.getDateofend())
        //               +" (" + (studentStatusModel.getSeason()==0?"осенний":"весенний") +")");
        lCourse.setValue(studentStatusModel.getCourse() + " курс");

        sss = contingentMovement.getStasuses(studentStatusModel.getIdSSS());
        refreshStatusGrid();
    }

    private void refreshStatusGrid () {
        createListItem(DEDUCTED, sss.getDeducted(), null, null, false, false);
        createListItem(ACADEM, sss.getAcademicLeave(), null, null, false, false);
        createListItem(GROUP_LEADER, sss.getGroupLeader(), null, null, false, false);
        createListItem(SIROTA, sss.getSirota(), null, null, false, false);
        createListItem(GOVERNMENT, sss.getGovernmentFinanced(), null, null, false, false);
        createListItem(PUT_SOCIAL, sss.getPutAppForSocialGrant(), null, null, false, false);
        createListItem(GET_SOCIAL, sss.getGetSocialGrant(), null, null, false, false);
        createListItem(INVALID, sss.getInvalid(), null, null, false, false);
        createListItem(PROLONGATION, sss.getSessionProlongation(), sss.getProlongationBeginDate(), sss.getProlongationEndDate(), true,
                       true
        );
        createListItem(TRUST_AGREEMENT, sss.getTrustAGreement(), null, null, false, false);
        createListItem(COMPLETED_EDUCATION, sss.getEducationComplete(), null, null, false, false);
        createListItem(TRANSFER_STUDENT, sss.getTransferStudent(), null, null, false, false);
        createListItem(LISTENER, sss.getListener(), null, null, false, false);
    }

    private void createListItem (String nameStatus, final boolean flagAction, Date dateBegin, Date dateEnd, boolean dateBeginExist,
                                 boolean dateEndExist) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(NAME_STATUS, nameStatus);
        jsonObject.put(FLAG_ACTION, flagAction);
        jsonObject.put(DATE_BEGIN, dateBegin == null ? "" : dateBegin);
        jsonObject.put(DATE_END, dateEnd == null ? "" : dateEnd);
        Listitem li = new Listitem();
        li.setValue(jsonObject);
        li.setParent(lbStatus);
        new Listcell(nameStatus).setParent(li);
        final Listcell lcFlag = new Listcell();
        lcFlag.setImage(flagAction ? "/imgs/okCLR.png" : null);
        lcFlag.setStyle("-webkit-user-select: none; -khtml-user-select: none; -moz-user-select: none; -ms-user-select: none; ");
        lcFlag.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent (Event event) throws Exception {
                if (jsonObject.getBoolean(FLAG_ACTION)) {
                    lcFlag.setImage(null);
                    jsonObject.put(FLAG_ACTION, false);
                } else {
                    lcFlag.setImage("/imgs/okCLR.png");
                    jsonObject.put(FLAG_ACTION, true);
                }
            }
        });
        lcFlag.setParent(li);

        if (dateBeginExist) {
            createDatebox(li, dateBegin, jsonObject, true);
        } else {
            new Listcell().setParent(li);
        }

        if (dateEndExist) {
            createDatebox(li, dateEnd, jsonObject, false);
        } else {
            new Listcell().setParent(li);
        }
    }

    private void createDatebox (Listitem li, Date date, final JSONObject jsonObject, final boolean begin) {
        Listcell lc = new Listcell();
        final Datebox db = new Datebox(date);
        db.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
            @Override
            public void onEvent (Event event) throws Exception {
                jsonObject.put(begin ? DATE_BEGIN : DATE_END, db.getValue() == null ? "" : db.getValue());
            }
        });
        db.setParent(lc);
        lc.setParent(li);
    }

    @Listen("onClick = #btnCancel")
    public void closeWindow () {
        winEditStatus.detach();
    }

    @Listen("onClick = #btnSave")
    public void saveStatus () {
        DialogUtil.questionWithYesNoButtons("Вы уверены, что хотите сохранить изменения?", "Внимание!", e -> {
            if (e.getName().equals(DialogUtil.ON_YES)) {
                boolean removeOpenRetakes = false;
                for (Listitem listitem : lbStatus.getItems()) {
                    JSONObject jsonObject = listitem.getValue();
                    sss = contingentMovement.setSSSflags(jsonObject, sss);
                }
                try {
                    if (sss.getGroupLeader()) {
                        //Меняем старосту группы

                                /*
                                ArrayList<StudentSemesterStatus> oldGroupLeaderList = new ArrayList<StudentSemesterStatus>(sssDAO.getCustomQuery("SELECT * FROM student_semester_status WHERE is_group_leader = 1 AND id_link_group_semester = " + sss.getLinkGroupSemester().getId()));
                                if(oldGroupLeaderList != null && oldGroupLeaderList.size() > 0)
                                {
                                    oldGroupLeaderList.get(0).setGroupLeader(false);
                                    sssDAO.update(oldGroupLeaderList.get(0));
                                }*/
                    }
                    sss = contingentMovement.updateStatus(sss);
                    mainPage.searchStudents();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                //Еслли статус стал "Отчислен", а предыдущий статус был "Не отчислен", то удаляем открытые пересдачи.
                        /*
                        if (sss.getDeducted()==(studentStatusModel.getDeducted()==0))
                            removeOpenRetakes = true;
                        if (removeOpenRetakes) emMovement.removeOpenRetakes(sss.getId());
                        studentStatusModel.setAcadem(sss.getAcademicLeave()?1:0);
                        studentStatusModel.setDeducted(sss.getDeducted()?1:0);
                        listitem.setValue(studentStatusModel);

                        listitem.setStyle("background: #"+((studentStatusModel.getAcadem()==1)?"8BC0FC;":"fff;"));
                        listitem.setStyle("background: #"+((studentStatusModel.getDeducted()==1)?"FA9D9D;":"fff;"));

                        listitem.getListbox().renderItem(listitem);

                        mainPage.refreshLB();
                        winEditStatus.detach();*/
            }
        });
    }
}
