package org.edec.student.recordBook.ctrl.renderer;

import org.edec.efficiency.model.ConfigurationEfficiency;
import org.edec.student.recordBook.model.GradeBookModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.converter.DateConverter;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.Date;


public class RecordBookRenderer implements ListitemRenderer<GradeBookModel> {
    private ComponentService componentService = new ComponentServiceESOimpl();

    @Override
    public void render (final Listitem li, GradeBookModel data, int index) throws Exception {
        li.setValue(data);

        if (index == 0) {
            if (li.getListbox().getListhead() != null) {
                li.getListbox().getListhead().detach();
            }
            Listhead lh = new Listhead();
            lh.setParent(li.getListbox());
            componentService.getListheader("#", "40px", "", "center").setParent(lh);
            componentService.getListheader("Предмет", "", "1", "").setParent(lh);
            componentService.getListheader("Кол-во часов", "110px", "", "center").setParent(lh);
            componentService.getListheader("Оценка", "80px", "", "center").setParent(lh);
            componentService.getListheader("Дата", "90px", "", "center").setParent(lh);
            componentService.getListheader("Преподаватель", "260px", "", "").setParent(lh);
            componentService.getListheader("Доп. информация", "240px", "", "").setParent(lh);
            if (data.getConfiguration() != null) {
                componentService.getListheader("Успеваемость в эл. курсах", "", "2", "center").setParent(lh);
            }
        }

        final String style = data.getStyle(false);
        final String chooseStyle = data.getStyle(true);

        li.addEventListener(Events.ON_MOUSE_OVER, event -> {
            for (int j = 0; j < li.getChildren().size(); ++j) {
                if (j == 1) {
                    ((Listcell) li.getChildren().get(j)).setStyle(chooseStyle);
                }
            }
        });
        li.addEventListener(Events.ON_MOUSE_OUT, event -> {
            for (int j = 0; j < li.getChildren().size(); ++j) {
                if (j == 1) {
                    ((Listcell) li.getChildren().get(j)).setStyle(style);
                }
            }
        });

        componentService.createListcell(li, String.valueOf(index + 1), "color: #000;", "", "background: #fff;");
        componentService.createListcell(li, data.getSubjectname(), "color: #000;", "", style);
        String totalHours = "";
        if (data.getHoursCount() > 0) {
            Integer hoursCount = data.getHoursCount().intValue();

            double tmpCreditUnits = hoursCount / 36;
            int creditUnits = (int) tmpCreditUnits;
            tmpCreditUnits = tmpCreditUnits - creditUnits;
            if (tmpCreditUnits >= 0.5) {
                creditUnits++;
            }

            if (data.getFoc().equals("Практика")) {
                totalHours = "-";
            } else {
                totalHours = hoursCount + " (" + creditUnits + " з.е.)";
            }
        }
        String dateOfPass = "";
        if (data.getDateOfPass() != null && data.getRating() > 0 && data.getRating() != 2) {
            dateOfPass = DateConverter.convertDateToString(data.getDateOfPass());
        }
        componentService.createListcell(li, totalHours, "color: #000;", "", "background: #fff;");
        componentService.createListcell(li, RatingConst.getNameByRating(data.getRating()), "color: #000;", "", "background: #fff;");
        componentService.createListcell(li, dateOfPass, "color: #000;", "", "background: #fff;");
        componentService.createListcell(li, data.getTeacher(), "color: #000; word-wrap: break-word;", "", "background: #fff;");

        Listcell lcInfo = new Listcell();
        lcInfo.setParent(li);
        lcInfo.setStyle("background: #fff;");
        Vbox vbInfo = getMarkVbox(data);
        vbInfo.setParent(lcInfo);

        if (data.getConfiguration() != null) {
          //  Float efficiency = getEfficiency(data);
            if (data.getEsoGradeCurrent() == null) {
                componentService.createListcell(li, "", "", "", "");
            } else {
                ConfigurationEfficiency configuration = data.getConfiguration();
                String lcStyle = "";
                if (configuration.getMaxRedLevel() >= data.getEsoGradeCurrent()) {
                    lcStyle = "background: #FF7373;";
                } else if (configuration.getMinGreenLevel() <= data.getEsoGradeCurrent()) {
                    lcStyle = "background: #95FF82;";
                } else {
                    lcStyle = "background: #FFFE7E;";
                }
                Listcell lcEfficiency = componentService.createListcell(
                        li, String.valueOf(data.getEsoGradeCurrent().intValue() + "%"), "color: #000;", "", lcStyle);

                Popup popup = new Popup();
                popup.setParent(lcEfficiency);
                popup.setId("popup" + li.getUuid());

                Vbox vb = new Vbox();
                vb.setParent(popup);

                new Label("Учитывается физ-ра: " + (configuration.getPhyscul() ? "Да" : "Нет")).setParent(vb);
                new Label("Учитывается посещаемость: " + (configuration.getAttendance() ? "Да" : "Нет")).setParent(vb);
                new Label("Учитывается успеваемость: " + (configuration.getPerformance() ? "Да" : "Нет")).setParent(vb);
                new Label("Красный уровень с 0 до " + configuration.getMaxRedLevel()).setParent(vb);
                new Label("Зеленый уровень с " + configuration.getMinGreenLevel() + " до 100").setParent(vb);
                new Label("Ваша посещаемость: " + data.getVisitCount() + " из " + data.getLessonCount()).setParent(vb);
                new Label("Ваша успеваемость: " + data.getEsoGradeCurrent()).setParent(vb);

                li.setPopup("popup" + li.getUuid());
            }
        }
    }
// расчет успеваемости по формуле марины валерьевны
//    private Float getEfficiency (GradeBookModel data) {
//        Float result = null;
//        Float maxStage = 100.0F;
//        if (data.getEsoGradeMax() != null) {
//            maxStage = data.getEsoGradeMax().floatValue();
//        }
//        ConfigurationEfficiency configuration = data.getConfiguration();
//        float countCoefficient = 0;
//        float maxResult = 0;
//        if (!configuration.getPhyscul() && (data.getSubjectname().equals("Физическая культура") ||
//                                            data.getSubjectname().equals("Элективные курсы по физической культуре") ||
//                                            data.getSubjectname().equals("Прикладная физическая культура") ||
//                                            data.getSubjectname().equals("Прикладная физическая культура (элективная)") ||
//                                            data.getSubjectname().equals("Прикладная физическая культура (элективный)") ||
//                                            data.getSubjectname().equals("Физическая культура/Прикладная физическая культура") ||
//                                            data.getSubjectname().equals("Физическая культура/Элективные курсы по физической культуре") ||
//                                            data.getSubjectname().equals("Элективные курсы по физической культуре и спорту") ||
//                                            data.getSubjectname().equals("Физическая культура"))) {
//            return null;
//        }
//        if (configuration.getAttendance()) {
//            ++countCoefficient;
//            maxResult = data.getVisitCount().floatValue() / data.getLessonCount().floatValue();
//        }
//
//        if (configuration.getPerformance()) {
//            ++countCoefficient;
//            maxResult = maxResult + (data.getEsoGradeCurrent().floatValue() / maxStage);
//        }
//
//        if (countCoefficient != 0) {
//            result = maxResult * 100F / countCoefficient;
//        }
//
//        return result;
//    }

    private Vbox getMarkVbox (GradeBookModel data) {
        Vbox vbox = new Vbox();
        if (data.getStatus().equals("0.1.1")) {
            String peresdachaDate = "";
            Date date1 = data.getStatusBeginDate();
            Date date2 = data.getStatusFinishDate();
            if ((date1 == null) && (date2 != null)) {
                peresdachaDate = "до " + date2;
            } else if ((date1 != null) && (date2 == null)) {
                peresdachaDate = "c " + date1;
            } else if ((date1 != null) && (date2 != null)) {
                if (date1.equals(date2)) {
                    peresdachaDate = "до " + date2;
                } else {
                    peresdachaDate = "c " + date1 + " по " + date2;
                }
            } else if ((date1 == null) && (date2 == null)) {
                peresdachaDate = "нет даты";
            }
            Hbox peresdachaWithDate = getHboxWith2labels("Пересдача:", peresdachaDate);
            peresdachaWithDate.setParent(vbox);
        } else if (data.getStatus().equals("1.0.0")) {
            Date date = new Date();
            if (data.getDateOfPass() != null) {
                if (date.getTime() > data.getDateOfPass().getTime()) {
                    getHboxWith1labels("Ведомость не сдана в деканат", "color: red").setParent(vbox);
                } else {
                    //Консультация
                    Hbox consultation;
                    if (data.getConsultationDate() != null) {
                        consultation = getHboxWith2labels("Консультация: ", DateConverter.convertDateToString(data.getConsultationDate()));
                    } else {
                        consultation = getHboxWith2labels("Консультация: ", " ");
                    }

                    consultation.setParent(vbox);

                    //Форма контроля
                    String datePass = "";
                    if (data.getDateOfPass() != null) {
                        datePass = DateConverter.convertDateToString(data.getDateOfPass());
                    }
                    if(data.getCpDate() != null && data.getFoc() == FormOfControlConst.CP.getName()){
                        datePass = DateConverter.convertDateToString(data.getCpDate());
                    }

                    Hbox foc = getHboxWith2labels(data.getFoc() + ": ", datePass);
                    foc.setParent(vbox);
                }
            }
        }
        return vbox;
    }

    /**
     * @param label1value - значение первого label
     * @param label2value - значение второго label
     * @return - Hbox, в котором есть два label'a
     */
    private Hbox getHboxWith2labels (String label1value, String label2value) {
        Hbox tempHbox = new Hbox();
        Vbox vbox1 = new Vbox();
        Label label = new Label();
        label.setValue(label1value);
        label.setSclass("b");
        label.setParent(vbox1);
        vbox1.setParent(tempHbox);

        Vbox vbox2 = new Vbox();
        Label label2 = new Label();
        label2.setValue(label2value);
        label2.setStyle("text-decoration: underline");
        label2.setParent(vbox2);
        vbox2.setParent(tempHbox);

        return tempHbox;
    }

    /**
     * @param labelValue - значение первого label
     * @return - Hbox, в котором есть один label
     */
    private Hbox getHboxWith1labels (String labelValue, String style) {
        Hbox tempHbox = new Hbox();
        Label label = new Label();
        label.setValue(labelValue);
        label.setStyle(style);
        tempHbox.appendChild(label);

        return tempHbox;
    }
}
