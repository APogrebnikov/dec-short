package org.edec.teacher.ctrl.renderer.register;

import lombok.extern.log4j.Log4j;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.teacher.ctrl.listener.RatingClickListener;
import org.edec.teacher.ctrl.listener.ThemeEditListener;
import org.edec.teacher.model.GroupModel;
import org.edec.teacher.model.register.RegisterModel;
import org.edec.teacher.model.register.RegisterRowModel;
import org.edec.teacher.service.impl.RegisterServiceImpl;
import org.edec.utility.DateUtility;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.constants.RegisterConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.pdfViewer.ctrl.PdfViewerCtrl;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.*;
import java.util.Calendar;
import java.util.function.Consumer;

/**
 * Created by antonskripacev on 25.02.17.
 */
@Log4j
public class IndivRetakeRegisterRenderer implements ListitemRenderer<RegisterModel> {
    private RegisterServiceImpl service = new RegisterServiceImpl();
    private TemplatePageCtrl template = new TemplatePageCtrl();

    private FormOfControlConst formOfCtrl;
    private int type;

    private GroupModel group;

    private Runnable updateRegisterUI;
    private Consumer<RegisterModel> openRegister;

    private List<RatingConst> listRatings;

    public IndivRetakeRegisterRenderer(FormOfControlConst formOfCtrl, int type, GroupModel group, Runnable updateRegisterUI,
                                       Consumer<RegisterModel> openRegister, List<RatingConst> listRatings) {
        this.formOfCtrl = formOfCtrl;
        this.type = type;
        this.group = group;
        this.updateRegisterUI = updateRegisterUI;
        this.openRegister = openRegister;
        this.listRatings = listRatings;
    }

    @Override
    public void render(Listitem item, final RegisterModel data, int index) throws Exception {

        boolean signedStatus = data.isRegisterSigned();
        boolean isOutOfDate = data.isRetakeOutOfDate();
        boolean isSecondSignPeriodAvailable = data.isSecondSignPeriodAvailable();

        Date today = new Date();

        RegisterRowModel rating = data.getListRegisterRow().get(0);

        try {
            // Рендерим фио
            Listcell lc = new Listcell();
            Label lb = new Label();
            if (rating.getDeducted()) {
                lb.setValue(rating.getStudentFullName() + " (отчислен)");
                lb.setStyle("color: red;");
            } else if (rating.getAcademicLeave()) {
                lb.setValue(rating.getStudentFullName() + " (академ. отпуск)");
                lb.setStyle("color: red;");
            } else {
                lb.setValue(rating.getStudentFullName());
                lb.setStyle("color: #000000;");
            }
            lc.appendChild(lb);
            lc.setStyle("border-bottom: solid 1px #bcbcbc; border-right: solid 1px #bcbcbc; background: #ffffff; collor: black;");
            item.appendChild(lc);

            if(data.getStartDate()==null || data.getFinishDate()==null){
                showUnfilledDatesRow(item);
                return;
            }

            // Рендерим поле со сроком действия.
            lc = new Listcell();
            lb = new Label();

            if (!DateUtility.isBeforeDate(today, data.getStartDate()) && !signedStatus) {
                if(!isOutOfDate) {
                    lb.setValue(DateConverter.convertDateToString(data.getStartDate()) + " - " + DateConverter.convertDateToString(data.getFinishDate()));
                    lb.setStyle("color: black;");
                }else if(isSecondSignPeriodAvailable){
                    lb.setValue(DateConverter.convertDateToString(data.getSecondSignDateBegin()) + " - " + DateConverter.convertDateToString(data.getSecondSignDateEnd()));
                    lb.setStyle("color: black;");
                } else {
                    if (data.getSecondSignDateBegin() != null) {
                        lb.setValue("Просрочена (" + DateConverter.convertDateToString(data.getSecondSignDateBegin()) + " - " +
                                DateConverter.convertDateToString(data.getSecondSignDateEnd()) + ")");
                        lb.setStyle("color: red;");
                    } else {
                        lb.setValue("Просрочена (" + DateConverter.convertDateToString(data.getStartDate()) + " - " +
                                DateConverter.convertDateToString(data.getFinishDate()) + ")");
                        lb.setStyle("color: red;");
                    }
                }
            }
            if (data.getSecondSignDateBegin() != null && DateUtility.isBeforeDate(today, data.getSecondSignDateBegin())) {
                lb.setValue("Дата подписания (" + DateConverter.convertDateToString(data.getSecondSignDateBegin()) + " - " +
                            DateConverter.convertDateToString(data.getSecondSignDateEnd()) + ") ещё не наступила");
                lb.setStyle("color: orange;");
            } else if (DateUtility.isBeforeDate(today, data.getStartDate())) {
                lb.setValue("Дата подписания (" + DateConverter.convertDateToString(data.getStartDate()) + " - " +
                        DateConverter.convertDateToString(data.getFinishDate()) + ") ещё не наступила");
                lb.setStyle("color: orange;");
            }
            if (signedStatus){
                lb.setValue("Подписана с ЭП");
                lb.setStyle("color: black;");
            }

            lc.appendChild(lb);
            lc.setStyle(
                    "border-bottom: solid 1px #bcbcbc; border-right: solid 1px #bcbcbc; background: #ffffff; text-align: center; width: 140px;");
            item.appendChild(lc);

            // Рендерим поле с оценкой
            lc = new Listcell();

            lb = new Label();

            if (rating.getMark() != null) {
                if (rating.getMark() == RatingConst.FAILED_TO_APPEAR.getRating() || rating.getMark() == RatingConst.NOT_PASS.getRating() ||
                    rating.getMark() == RatingConst.UNSATISFACTORILY.getRating()) {
                    lb.setStyle("color: red;");
                }
                if (rating.getMark() == 0) {
                    if (DateUtility.isAfterDate(today, data.getFinishDate())) {
                        lb.setValue("");
                    } else {
                        lb.setValue("Введите оценку.");
                    }
                } else {
                    lb.setValue(RatingConst.getDataByRating(rating.getMark()).getShortname());
                }
            } else {
                lb.setValue("");
            }

            lc.appendChild(lb);

            //Ввести оценку можно если:
            // 1 -  ведомость еще не подписана
            // 2.1 - либо крайняя дата подписания не прошла,
            // 2.2 - либо еще действует период повторного подписания
            Calendar threeDayAfterRetake = Calendar.getInstance();
            threeDayAfterRetake.setTime(data.getFinishDate());
            threeDayAfterRetake.add(Calendar.DAY_OF_MONTH, 3);
            if (!signedStatus &&
                (!DateUtility.isAfterDate(today, threeDayAfterRetake.getTime()) || isSecondSignPeriodAvailable)) {
                lc.setStyle(
                        "border-bottom: solid 1px #bcbcbc; border-right: solid 1px #bcbcbc; background: #ffffff; text-align: center; width: 140px;");
                lc.addEventListener(Events.ON_CLICK,
                                    new RatingClickListener(lc, rating, formOfCtrl, RegisterConst.TYPE_RETAKE_INDIV_NOT_SIGNED, type,
                                                            listRatings
                                    )
                );
            } else if (signedStatus && data.getCertNumber().equals("  ")) {
                lc.setStyle(
                        "border-bottom: solid 1px #bcbcbc; border-right: solid 1px #bcbcbc; background: #d7f0d3; text-align: center; width: 140px;");
            } else if (signedStatus) {
                lc.setStyle(
                        "border-bottom: solid 1px #bcbcbc; border-right: solid 1px #bcbcbc; background: #B0FFAD; text-align: center; width: 140px;");
            } else {
                lc.setStyle(
                        "border-bottom: solid 1px #bcbcbc; border-right: solid 1px #bcbcbc; background: #ffffff; text-align: center; width: 140px;");
            }

            item.appendChild(lc);

            lc = new Listcell();
            if ((!signedStatus && !DateUtility.isBeforeDate(today, data.getStartDate()) && (!isOutOfDate || isSecondSignPeriodAvailable))
            || (signedStatus && data.getCertNumber() != null &&  data.getCertNumber().equals("  "))) {
                Button btnPrint = new Button();
                btnPrint.setLabel("Подписать c ЭП");
                btnPrint.setStyle("font-weight: 700; height: 30px;");
                btnPrint.setStyle("background: #B0FFAD; font-weight: 700; height: 30px;");
                btnPrint.addEventListener(Events.ON_CLICK, event -> openRegister.accept(data));
                lc.appendChild(btnPrint);
            } else if (signedStatus) {
                Button btnPrint = new Button();
                btnPrint.setLabel("Печать");
                btnPrint.setStyle("font-weight: 700; height: 30px;");
                btnPrint.setImage("/imgs/pdf.png");
                btnPrint.addEventListener(Events.ON_CLICK, event -> {
                    Map arg = new HashMap();
                    arg.put(PdfViewerCtrl.FILE, new org.edec.register.service.impl.RegisterServiceImpl().getFileRegister(data.getRegisterURL(), data.getIdRegisterESO()));
                    ComponentHelper.createWindow("/utility/pdfViewer/index.zul", "winPdfViewer", arg).doModal();
                });
                lc.appendChild(btnPrint);
            }
            lc.setStyle("border-bottom: solid 1px #bcbcbc; background: #ffffff; text-align: center; width: 140px;");
            item.appendChild(lc);
            // Если форма контроля курсовой/курсовая, выводим колонку для внесения темы
            if ((formOfCtrl == FormOfControlConst.CP || formOfCtrl == FormOfControlConst.CW)) {
                lc = new Listcell();

                if (rating.getTheme() != null) {
                    if (data.getSignDate() == null) {
                        lc.setImage("/imgs/okCLR.png");
                    } else {
                        lc.setImage("/imgs/showCLR.png");
                    }
                } else {
                    if (data.getSignDate() == null) {
                        lc.setImage("/imgs/edit.png");
                    } else {
                        lc.setImage("/imgs/show.png");
                    }
                }

                if (data.getSignDate() == null) {
                    lc.setHoverImage("/imgs/editBLACK.png");
                } else {
                    lc.setHoverImage("/imgs/showBLACK.png");
                }

                lc.addEventListener(Events.ON_CLICK, new ThemeEditListener(rating, formOfCtrl, updateRegisterUI, signedStatus));
                lc.setStyle(
                        "width: 50px; text-align: center; border-left: solid 1px #bcbcbc; border-bottom: solid 1px #bcbcbc; background: #ffffff;");
                item.appendChild(lc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showUnfilledDatesRow(Listitem item){
        Listcell lc = new Listcell();
        Label lb = new Label();

        lb.setValue("Отсутствуют даты пересдачи - обратитесь в УОО!");
        lb.setStyle("color: orange;");
        lc.setStyle(
                "border-bottom: solid 1px #bcbcbc; border-right: solid 1px #bcbcbc; background: #ffffff; text-align: center; width: 140px;");
        lc.appendChild(lb);
        item.appendChild(lc);

        lc = new Listcell();
        lb = new Label();
        lc.appendChild(lb);
        item.appendChild(lc);

        lc = new Listcell();
        lb = new Label();
        lc.appendChild(lb);
        lc.setStyle("border-bottom: solid 1px #bcbcbc; border-right: solid 1px #bcbcbc; background: #ffffff; text-align: center; width: 140px;");
        item.appendChild(lc);

        lc = new Listcell();
        lb = new Label();
        lc.appendChild(lb);
        lc.setStyle("border-bottom: solid 1px #bcbcbc; background: #ffffff; text-align: center; width: 140px;");
        item.appendChild(lc);

    }
}
