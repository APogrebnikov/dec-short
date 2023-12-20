package org.edec.teacher.ctrl.renderer.register;

import org.edec.teacher.ctrl.WinRegisterCtrl;
import org.edec.teacher.ctrl.listener.RatingClickListener;
import org.edec.teacher.ctrl.listener.ThemeEditListener;
import org.edec.teacher.model.register.RatingModel;
import org.edec.teacher.model.register.RegisterRowModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.constants.RegisterConst;
import org.edec.utility.constants.RegisterType;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.List;

/**
 * Created by antonskripacev on 25.02.17.
 */
public class MainRetakeRegisterRenderer implements ListitemRenderer<RegisterRowModel> {
    private FormOfControlConst foc;
    private boolean isSigned;
    private Runnable updateRegisterUI;
    private List<RatingConst> ratingConstList;

    private int type;

    public MainRetakeRegisterRenderer(FormOfControlConst foc, boolean isSigned, Runnable updateRegisterUI, int type,
                                      List<RatingConst> ratingConstList) {
        this.foc = foc;
        this.isSigned = isSigned;
        this.updateRegisterUI = updateRegisterUI;
        this.type = type;
        this.ratingConstList = ratingConstList;
    }

    @Override
    public void render(Listitem item, RegisterRowModel data, int index) throws Exception {

        item.setValue(data);

        Listcell lc = new Listcell();
        lc.setStyle("width: 30px; border-bottom: solid 1px #bcbcbc; border-right: solid 1px #bcbcbc; background: #ffffff;");
        item.appendChild(lc);
        // Рендерим фио
        lc = new Listcell();
        Label lb = new Label();
        Integer n = index + 1;
        if (data.getDeducted()) {
            lb.setValue(n + ") " + data.getStudentFullName() + " (отчислен)");
            lb.setStyle("color: red;");
        } else if (data.getAcademicLeave()) {
            lb.setValue(n + ") " + data.getStudentFullName() + " (академ. отпуск)");
            lb.setStyle("color: red;");
        } else {
            lb.setValue(n + ") " + data.getStudentFullName());
            lb.setStyle("color: #000000;");
        }
        lc.appendChild(lb);
        lc.setStyle("border-bottom: solid 1px #bcbcbc; border-right: solid 1px #bcbcbc; background: #ffffff;");
        item.appendChild(lc);
        // Рендерим оценку
        lc = new Listcell();
        lb = new Label();

        if (foc == FormOfControlConst.PASS && type == 0) {
            if (data.getMark() != null && data.getMark() != 0) {
                if (data.getMark() == RatingConst.FAILED_TO_APPEAR.getRating()) {
                    lb.setValue("Н.Я.");
                    lb.setStyle("color: red;");
                } else if (data.getMark() == RatingConst.NOT_PASS.getRating()) {
                    lb.setValue("Не зачтено");
                    lb.setStyle("color: red;");
                } else if (data.getMark() == RatingConst.PASS.getRating()) {
                    lb.setValue("Зачтено");
                    lb.setStyle("color: #000000;");
                }

                lc.appendChild(lb);
            } else {
                if (isSigned) {
                    lb.setValue("");
                } else {
                    lb.setValue("Введите оценку.");
                }
                lc.appendChild(lb);
            }
        } else {
            if (data.getMark() != 0) {
                if (data.getMark() == RatingConst.FAILED_TO_APPEAR.getRating()) {
                    lb.setValue("Н.Я.");
                    lb.setStyle("color: red;");
                } else if (data.getMark() == RatingConst.UNSATISFACTORILY.getRating()) {
                    lb.setValue("2");
                    lb.setStyle("color: red;");
                } else if (data.getMark() > RatingConst.UNSATISFACTORILY.getRating()) {
                    lb.setValue(String.valueOf(data.getMark()));
                    lb.setStyle("color: #000000;");
                } else {
                    lb.setValue("Ошибка! Обратитесь к администратору.");
                }

                lc.appendChild(lb);
            } else {
                if (isSigned) {
                    lb.setValue("");
                } else {
                    lb.setValue("Введите оценку.");
                }
                lc.appendChild(lb);
            }
        }

        //Ввести оценку можно если:
        // 1 -  ведомость еще не подписана
        // 2.1 - либо крайняя дата подписания не прошла,
        // 2.2 - либо еще действует период повторного подписания
        if (!isSigned && (!data.getIsOutOfDate() || data.getIsSecondSignPeriodAvailable())) {
            lc.setTooltip("Для ввода оценки кликнете по данному полю.");
            lc.addEventListener(Events.ON_CLICK,
                                new RatingClickListener(lc, data, foc, RegisterConst.TYPE_RETAKE_MAIN_NOT_SIGNED, type, ratingConstList)
            );
        }

        if (isSigned) {
            lc.setStyle("text-align: center; border-bottom: solid 1px #bcbcbc; background: #B0FFAD;");
        } else {
            lc.setStyle("text-align: center; border-bottom: solid 1px #bcbcbc; background: #ffffff;");
        }

        item.appendChild(lc);

        if (foc == FormOfControlConst.CP || foc == FormOfControlConst.CW) {
            lc = new Listcell();
            if ((foc == FormOfControlConst.CP || foc == FormOfControlConst.CW) && data.getTheme() != null) {
                if(isSigned){
                    lc.setImage("/imgs/showCLR.png");
                }else{
                    lc.setImage("/imgs/okCLR.png");
                }
            } else {
                if(isSigned){
                    lc.setImage("/imgs/show.png");
                }else {
                    lc.setImage("/imgs/edit.png");
                }
            }

            if(isSigned){
                lc.setHoverImage("/imgs/showBLACK.png");
            }else {
                lc.setHoverImage("/imgs/editBLACK.png");
            }

            lc.addEventListener(Events.ON_CLICK, new ThemeEditListener(data, foc, updateRegisterUI, isSigned));
            lc.setStyle(
                    "width: 50px; text-align: center; border-left: solid 1px #bcbcbc; border-bottom: solid 1px #bcbcbc; background: #ffffff;");
            item.appendChild(lc);
        } else {
            lc = new Listcell();
            item.appendChild(lc);
        }
        // Рендерим последний столбец со служебной запиской

        lc = new Listcell();
        if(data.getCurCorrectRequest() != null) {
            Button button = new Button();
            button.setImage("/imgs/pdf.png");

            button.addEventListener(Events.ON_CLICK, e -> new JasperReportService().getJasperReportCorrectRating(data.getCurCorrectRequest()).showPdf());

            lc.appendChild(button);
        }

        item.appendChild(lc);
    }
}

