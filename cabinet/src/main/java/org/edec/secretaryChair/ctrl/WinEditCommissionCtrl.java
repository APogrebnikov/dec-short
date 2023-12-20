package org.edec.secretaryChair.ctrl;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.time.DateUtils;
import org.edec.secretaryChair.ctrl.renderer.EmployeeRenderer;
import org.edec.secretaryChair.model.CommissionModel;
import org.edec.secretaryChair.model.EmployeeModel;
import org.edec.secretaryChair.model.StudentCommissionModel;
import org.edec.secretaryChair.service.SecretaryChairService;
import org.edec.secretaryChair.service.impl.SecretaryChairImpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.*;
import java.util.Calendar;

import static org.edec.secretaryChair.ctrl.WinChoseCommissionTime.COMMISSION;

/**
 * Окно по созданию и изменению состава/даты/аудитории комиссии
 */
@Log4j
public class WinEditCommissionCtrl extends SelectorComposer<Component> {
    //константы
    public static final String COMMISSION_PAGE_CTRL = "commission_page_ctrl";
    public static final String COMMISSION_OBJ = "commission_obj";

    //компоненты
    @Wire
    private Datebox dbCommission;
    @Wire
    private Label lInfoCommission, lDateOfBeginCommission, lDateOfEndCommission;
    @Wire
    private Listbox lbStaff, lbCommissionStaff;
    @Wire
    private Textbox tbClassroom;
    @Wire
    private Timebox timebCommission;

    //сервисы
    private SecretaryChairService chairService = new SecretaryChairImpl();

    //переменные
    private IndexPageCtrl commissionPageCtrl;
    private CommissionModel commission;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        commissionPageCtrl = (IndexPageCtrl) Executions.getCurrent().getArg().get(COMMISSION_PAGE_CTRL);
        commission = (CommissionModel) Executions.getCurrent().getArg().get(COMMISSION_OBJ);
        fill();
    }

    private void fill() {
        if (commission.getCommissionDate() != null) {
            dbCommission.setValue(commission.getCommissionDate());
            timebCommission.setValue(commission.getCommissionDate());
        }

        if (commission.getClassroom() != null) {
            tbClassroom.setValue(commission.getClassroom());
        }
        lDateOfBeginCommission.setValue(DateConverter.convertDateToString(commission.getDateBegin()));
        lDateOfEndCommission.setValue(DateConverter.convertDateToString(commission.getDateEnd()));
        Long idChair = commission.getIdChair();
        lInfoCommission.setValue("Комиссия по предмету: " + commission.getSubjectName() + "(" +
                FormOfControlConst.getName(commission.getFormOfControl()).getName() + ") " + commission.getSemesterStr());
        lbStaff.setModel(new ListModelList<>(chairService.getEmployeeByChair(idChair)));
        lbStaff.setItemRenderer(new EmployeeRenderer());
        for (EmployeeModel employee : chairService.getEmployeeByCommission(commission.getId())) {
            addEmployeeIntoCommissionStuff(employee);
        }
        lbCommissionStaff.setCheckmark(true);

        dbCommission.setConstraint((component, o) -> {
            if (o == null) {
                throw new WrongValueException(component, "Дата не может быть пустой");
            }
            Date date = (Date) o;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                throw new WrongValueException(component, "Комиссия не может проводиться в воскресенье");
            }
            if (!DateUtils.isSameDay(date, commission.getDateBegin()) && date.before(commission.getDateBegin())) {
                throw new WrongValueException(component, "Дата не может быть раньше начала сроков комиссии");
            }
            if (!DateUtils.isSameDay(date, commission.getDateEnd()) && date.after(commission.getDateEnd())) {
                throw new WrongValueException(component, "Дата не может быть позже окончания сроков комиссии");
            }
        });
        timebCommission.setConstraint((component, o) -> {
            if (o == null) {
                throw new WrongValueException(component, "Время не может быть пустым");
            }
            Date date = (Date) o;
            int minute = DateConverter.getMinuteOfDay(date);
            int time8_30 = 8 * 60 + 30;
            int time21_00 = 21 * 60;
            if (minute < time8_30 || minute > time21_00) {
                throw new WrongValueException(component,
                        "Комиссия должна состоять в период с 8:30 до 21:00. Промежуток между предметами 2 часа!"
                );
            }
        });
    }

    @Listen("onClick = #btnShowStudent")
    public void showStudent() {
        Map<String, Object> arg = new HashMap<>();
        arg.put(WinStudentCommissionCtrl.REGISTER_COMMISSION, commission);
        ComponentHelper.createWindow("/secretaryChair/winStudent.zul", "winStudent", arg).doModal();
    }

    @Listen("onClick = #btnSearchFreeTime")
    public void searchFreeTime() {

        Map<String, Object> arg = new HashMap<>();
        arg.put(COMMISSION, commission);
        Window win = ComponentHelper.createWindow("winChooseDateCommission.zul", "winChooseDateCommission", arg);

        win.addEventListener("onSelectDate", event -> {
            Date date = (Date) event.getData();
            dbCommission.setValue(date);
            timebCommission.setValue(date);
            win.detach();
        });

        win.doModal();
    }

    @Listen("onClick = #lbStaff")
    public void onClickLbStaff() {
        if (lbStaff.getSelectedItem() == null) {
            return;
        }
        EmployeeModel selectedEmp = lbStaff.getSelectedItem().getValue();
        for (Listitem li : lbCommissionStaff.getItems()) {
            EmployeeModel empTmp = li.getValue();
            if (empTmp.getIdEmployee().equals(selectedEmp.getIdEmployee())) {
                return;
            }
        }
        addEmployeeIntoCommissionStuff(selectedEmp);
    }

    @Listen("onClick = #btnSaveCommission")
    public void saveCommission() {
        if (commission.getStatus() == 2) {
            Messagebox.show("Комиссионная ведомость подписана, изменять данные запрещено", "Внимание!", Messagebox.OK,
                    Messagebox.EXCLAMATION
            );
            return;
        }
        if (dbCommission.getValue() == null || tbClassroom.getValue().equals("") || timebCommission.getValue() == null) {
            Messagebox.show("Заполните дату, время, аудиторию и состав комиссии.", "Внимание!", Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }

        boolean chairmanExists = lbCommissionStaff.getItems().stream()
                .map(listitem -> ((EmployeeModel) listitem.getValue()))
                .anyMatch(employee -> employee.getLeader() == 1);
        if (!chairmanExists) {
            Messagebox.show("Вы не указали председателя комиссии.", "Внимание!", Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dbCommission.getValue());

        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTime(timebCommission.getValue());

        calendar.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));

        List<StudentCommissionModel> searchStudentsForCheckByTime = chairService.getStudentsForCheckFreeTime(commission, calendar.getTime());

        if (searchStudentsForCheckByTime.size() != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Время занято, выберите другой день. У студентов комиссии по предметам:\n");
            for (int i = 0; i < searchStudentsForCheckByTime.size(); i++) {
                StudentCommissionModel student = searchStudentsForCheckByTime.get(i);
                stringBuilder.append(i + 1).append(". ").append(student.getShortFio()).append(" (").append(student.getGroupname())
                        .append("), предмет '").append(student.getSubjectname()).append("(").append(student.getFocStr())
                        .append(")' в ").append(DateConverter.convertTimestampToString(student.getDateCommission())).append("\n");
            }
            Messagebox.show(stringBuilder.toString(), "Внимание!", Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }
        boolean flag = true;
        chairService.deleteCommissionStaff(commission.getId());
        for (Listitem li : lbCommissionStaff.getItems()) {
            EmployeeModel tmpEmployee = li.getValue();
            if (!chairService.addCommissionStaff(tmpEmployee, commission.getId())) {
                flag = false;
                break;
            }
        }
        if (!flag) {
            PopupUtil.showError("Не удалось сохранить состав комиссии, обратитесь к администратору!");
            return;
        }
        chairService.updateComissionStatusNotification(commission.getId(), commission.getStatusNotification());
        chairService.updateCommissionInfo(calendar.getTime(), tbClassroom.getValue(), commission.getId());

        log.info("Комиссия по предмету \"" + commission.getSubjectName() + "\" за семестр \"" + commission.getSemesterStr() +
                 "\" была назначена на " + DateConverter.convertDateToStringByFormat(calendar.getTime(),"dd-MM-yyyy")
                + " в " + tbClassroom.getValue());

        commission.setCommissionDate(calendar.getTime());
        commission.setClassroom(tbClassroom.getValue());

        PopupUtil.showInfo("Комиссия успешно сохранена!");
        commissionPageCtrl.refreshLb("Загрузка данных", commission);
    }

    private void addEmployeeIntoCommissionStuff(final EmployeeModel employee) {
        final Listitem li = new Listitem();
        li.setParent(lbCommissionStaff);
        li.setSelected(employee.getLeader() == 1);
        li.setValue(employee);

        new Listcell().setParent(li);
        new Listcell(employee.getFio()).setParent(li);
        new Listcell(employee.getRole()).setParent(li);

        Listcell lcDel = new Listcell("", "/imgs/del.png");
        lcDel.setParent(li);
        lcDel.addEventListener(Events.ON_CLICK, event -> li.detach());

        lbCommissionStaff.addEventListener(Events.ON_SELECT, event -> employee.setLeader(li.isSelected() ? 1 : 0));
    }
}