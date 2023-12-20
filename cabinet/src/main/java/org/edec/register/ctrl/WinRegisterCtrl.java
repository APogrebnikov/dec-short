package org.edec.register.ctrl;

import lombok.extern.log4j.Log4j;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.ModuleModel;
import org.edec.register.model.RegisterModel;
import org.edec.register.model.StudentModel;
import org.edec.register.model.mine.StudentMineModel;
import org.edec.register.service.RegisterService;
import org.edec.register.service.impl.RegisterServiceImpl;
import org.edec.utility.DateUtility;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.constants.RegisterConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.Calendar;
import java.util.Date;

import static org.zkoss.zk.ui.Executions.getCurrent;

@Log4j
public class WinRegisterCtrl extends SelectorComposer<Component> {
    public static String REGISTER = "register";
    public static String UPDATE_INTERFACE = "update";

    @Wire
    private Label lbName;
    @Wire
    private Window winLookRegister;
    @Wire
    private Listbox lbStudents, lbAsuIkit, lbMine;
    @Wire
    private Groupbox gbDates, gbSecondSignDates;
    @Wire
    private Datebox dateOfBegin, dateOfEnd, dateOfBeginSecondSign, dateOfEndSecondSign;
    @Wire
    private Button saveDates, btnCancelSign, btnSaveSecondSignDate;
    @Wire
    private Tab tabMine;
    @Wire
    private Label lbSecondSignPeriod;

    private TemplatePageCtrl template = new TemplatePageCtrl();
    private ModuleModel currentModule;
    private Runnable updateIndex;
    private RegisterModel model;

    private RegisterService service = new RegisterServiceImpl();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        template.checkModuleByRole(getCurrent().getDesktop().getRequestPath(), getPage());
        currentModule = template.getCurrentModule();

        model = (RegisterModel) getCurrent().getArg().get(REGISTER);
        updateIndex = (Runnable) getCurrent().getArg().get(UPDATE_INTERFACE);

        dateOfBegin.setValue(model.getDateOfBegin());
        dateOfEnd.setValue(model.getDateOfEnd());

        if (model.getRetakeCount() == RegisterConst.TYPE_MAIN || model.getRetakeCount() == RegisterConst.TYPE_COMMISSION) {
            gbDates.setVisible(false);
        }

        if (currentModule.isReadonly()) {
            gbDates.setVisible(false);
            dateOfBegin.setDisabled(true);
            dateOfEnd.setDisabled(true);
            saveDates.setDisabled(true);
        } else if (model.getRetakeCount() == -2 || model.getRetakeCount() == -4) {
            saveDates.addEventListener(Events.ON_CLICK, event -> {
                if (dateOfBegin.getValue() == null || dateOfEnd.getValue() == null) {
                    PopupUtil.showWarning("Заполните даты");
                } else {
                    for (StudentModel student : model.getStudents()) {
                        service.updateDatesRegister(dateOfBegin.getValue(), dateOfEnd.getValue(), student.getIdSRH());
                    }

                    PopupUtil.showInfo("Даты были успешно изменены!");
                    updateIndex.run();
                }
            });
        } else {
            dateOfBegin.setDisabled(true);
            dateOfEnd.setDisabled(true);
            saveDates.setDisabled(true);
        }

        if (!currentModule.isReadonly() && model.getCertNumber() != null) {
            btnCancelSign.setDisabled(false);
            btnCancelSign.setVisible(true);

            btnCancelSign.addEventListener(Events.ON_CLICK, event -> {
                if (service.cancelRegister(model)) {
                    PopupUtil.showInfo("Отмена подписи прошла успешно");
                    getSelf().detach();
                } else {
                    PopupUtil.showError("Не удалось отменить подпись ведомости, обратитесь в поддержку, код - " + model.getIdRegister());
                }

                updateIndex.run();
            });
        }

        //Проверяем открывать ли пользователю доступ к назначению даты для подписания просроченной ведомости
        /*
        0 - должны быть полные права к модулю
        1 - Это должна быть неподписанная индивидуальная ведомость
        3 - Ведомость должна быть просроченной
         */

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);

        if (!currentModule.isReadonly() && (model.getRetakeCount().equals(RegisterConst.TYPE_RETAKE_INDIV_NOT_SIGNED)
                || model.getRetakeCount().equals(RegisterConst.TYPE_RETAKE_MAIN_NOT_SIGNED) || model.getRetakeCount().equals(RegisterConst.TYPE_COMMISSION_NOT_SIGNED))
                && (model.getCertNumber() == null && model.getDateOfEnd() != null && model.getDateOfEnd().before(cal.getTime()))) {

            btnSaveSecondSignDate.addEventListener(Events.ON_CLICK, event -> {
                //проверка валидности дат
                if (dateOfBeginSecondSign.getValue() == null || dateOfEndSecondSign.getValue() == null) {
                    PopupUtil.showWarning("Заполните даты");
                } else {

                    if (service.updateSecondSignDate(model.getIdRegister(), dateOfBeginSecondSign.getValue(),
                            dateOfEndSecondSign.getValue()
                    )) {
                        log.info(String.format("Сроки для подписи просроченной индивидуальной ведомости" +
                                        " по предмету %s за семестр %s были выставлены с %s по %s", model.getSubjectName(),
                                model.getSemester(), DateConverter.convertDateToString(dateOfBeginSecondSign.getValue()),
                                DateConverter.convertDateToString(dateOfEndSecondSign.getValue())
                        ));

                        PopupUtil.showInfo("Сроки для подписи ведомости были успешно изменены!");

                        lbSecondSignPeriod.setValue(String.format("Открыт период для подписания \nс %s" + " до %s",
                                DateConverter.convertDateToString(dateOfBeginSecondSign.getValue()),
                                DateConverter.convertDateToString(dateOfEndSecondSign.getValue())
                        ));
                    } else {
                        PopupUtil.showError("Изменить сроки для подписи ведомости не удалось!");
                    }

                    updateIndex.run();
                }
            });

            if (model.getDateOfSecondSignBegin() != null && DateUtility.isSameDayOrAfter(model.getDateOfSecondSignEnd(), new Date())) {
                lbSecondSignPeriod.setValue(String.format("Открыт период для подписания \nс %s" + " до %s",
                        DateConverter.convertDateToString(model.getDateOfSecondSignBegin()),
                        DateConverter.convertDateToString(model.getDateOfSecondSignEnd())
                ));
            }

            dateOfBegin.setDisabled(true);
            dateOfEnd.setDisabled(true);
            saveDates.setDisabled(true);

            dateOfBeginSecondSign.setValue(new Date());
            dateOfEndSecondSign.setValue(new Date());
            gbSecondSignDates.setVisible(true);
        }

        if ((model.getRetakeCount() == RegisterConst.TYPE_MAIN_NOT_SIGNED)
                && ((model.getExamDate() != null && new Date().after(DateUtility.getNextDateExcludeWeekend(model.getExamDate())))
                || (model.getPassDate() != null && new Date().after(DateUtility.getNextDateExcludeWeekend(model.getPassDate())))
                || (model.getCpDate() != null && new Date().after(DateUtility.getNextDateExcludeWeekend(model.getCpDate())))
                || (model.getCwDate() != null && new Date().after(DateUtility.getNextDateExcludeWeekend(model.getCwDate())))
                || (model.getPracticDate() != null && new Date().after(DateUtility.getNextDateExcludeWeekend(model.getPracticDate()))))) {
            dateOfBegin.setDisabled(false);
            dateOfEnd.setDisabled(false);
            saveDates.setDisabled(false);
            saveDates.addEventListener(Events.ON_CLICK, event -> {
                if (dateOfBegin.getValue() == null || dateOfEnd.getValue() == null) {
                    PopupUtil.showWarning("Заполните обе даты!");
                } else {
                    service.updateDateMainRegister(dateOfBegin.getValue(), dateOfEnd.getValue(), model.getIdLgss());
                    PopupUtil.showInfo("Даты были успешно изменены!");
                    updateIndex.run();
                }
            });
            if (model.getDateBeginMainRegister() != null && model.getDateEndMainRegister() != null) {
                dateOfBegin.setValue(model.getDateBeginMainRegister());
                dateOfEnd.setValue(model.getDateEndMainRegister());
            }
        }

        lbName.setValue(model.getSubjectName() + "/" + FormOfControlConst.getName(model.getFoc()).getName());

        lbStudents.setModel(new ListModelList<>(model.getStudents()));
        lbStudents.setHeight("400px");
        lbStudents.setItemRenderer((Listitem li, StudentModel data, int i) -> {
            new Listcell(data.getFio()).setParent(li);

            String rating = "";
            switch (data.getRating()) {
                case 5:
                case 4:
                case 3:
                case 2:
                    rating = Integer.toString(data.getRating());
                    break;
                case 1:
                    rating = "Зачтено";
                    break;
                case 0:
                    rating = (model.getFoc() == FormOfControlConst.PASS.getValue() && data.getType() == 0)
                            ? "Не зачтено"
                            : "Оценка не выставлена";
                    break;
                case -2:
                    rating = "Не зачтено";
                    break;
                case -3:
                    rating = "Неявка";
                    break;
            }

            new Listcell(rating).setParent(li);
        });

        lbStudents.renderAll();

        if (model.getSignDate() != null && model.getCertNumber() != null && model.getCertNumber() != "") {
            tabMine.setVisible(true);
            if (configureListBoxTabCompareRegister()) {
                lbAsuIkit.setModel(new ListModelList<>(model.getStudents()));
                lbMine.setModel(new ListModelList<>(service.getFilteredStudentsForRegisterFromMine(model)));
            }
        }
    }

    private boolean configureListBoxTabCompareRegister() {
        if (model.getRetakeCount() == null || model.getRetakeCount() <= 0) {
            DialogUtil.error("Произошла ошибка при получении данных, обратитесь к администратору.");
            return false;
        }

        lbAsuIkit.setItemRenderer((ListitemRenderer<StudentModel>) (li, data, i) -> {
            new Listcell(data.getFio()).setParent(li);

            RatingConst rc = RatingConst.getDataByRating(data.getRating());
            new Listcell(rc != null ? rc.getName() : "").setParent(li);
        });

        lbMine.getListhead().appendChild(createListheader("ФИО", "3"));

        if (model.getRetakeCount() == 1) {
            lbMine.getListhead().appendChild(createListheader("Оценка", "1"));

            lbMine.setItemRenderer((ListitemRenderer<StudentMineModel>) (li, data, i) -> {
                new Listcell(data.getFio()).setParent(li);
                new Listcell(data.getMainMark() != null ? data.getMainMark().getName() : "").setParent(li);
            });
        } else {
            lbMine.getListhead().appendChild(createListheader("Оценка 1", "1"));
            lbMine.getListhead().appendChild(createListheader("Оценка 2", "1"));

            lbMine.setItemRenderer((ListitemRenderer<StudentMineModel>) (li, data, i) -> {
                new Listcell(data.getFio()).setParent(li);
                new Listcell(data.getMark1() != null ? data.getMark1().getName() : "").setParent(li);
                new Listcell(data.getMark2() != null ? data.getMark2().getName() : "").setParent(li);
            });
        }

        return true;
    }

    private Listheader createListheader(String label, String hflex) {
        Listheader lhr = new Listheader();
        lhr.setHflex(hflex);
        Label lbl = new Label(label);
        lbl.setSclass("cwf-listheader-label");
        lhr.appendChild(lbl);
        return lhr;
    }
}
