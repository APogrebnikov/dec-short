package org.edec.curriculumScan.ctrl.renderer;

import com.lowagie.text.ListItem;
import org.edec.curriculumScan.model.CurrCompare;
import org.edec.curriculumScan.model.Subject;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * Created by alex
 */
public class SubjectCompLbRenderer implements ListitemRenderer<CurrCompare> {

    public static final Integer NEW_NAME_NUMBER = 3;
    public static final Integer NEW_COMPETENCE_NUMBER = 10;
    public static final Integer OLD_NAME_NUMBER = 14;
    public static final Integer OLD_COMPETENCE_NUMBER = 21;

    @Override
    public void render (Listitem li, CurrCompare data, int index) throws Exception {
        // Просто потому что
        li.setStyle("background: #acffaf");
        li.setValue(data);

        /// =========================Новая модель
        // Сем
        Listcell lcSem = new Listcell("");
        lcSem.setParent(li);
        if (data.getNewModel() != null) {
            lcSem.setLabel(data.getNewModel().getSemesterNumber() == null ? "" : data.getNewModel().getSemesterNumber().toString());
            if (!data.check(data.getNewModel().getSemesterNumber(), data.getOldModel() == null ? null : data.getOldModel().getSemesterNumber())) {
                lcSem.setStyle("background: #ffacac");
            }
        } else {
            lcSem.setStyle("background: #ffacac");
        }

        // Цикл
        Listcell lcCicle = new Listcell("");
        lcCicle.setParent(li);
        if (data.getNewModel() != null) {
            lcCicle.setLabel(data.getNewModel().getCicleCode());
            if (!data.check(data.getNewModel().getCicleCode(), data.getOldModel() == null ? null : data.getOldModel().getCicleCode())) {
                lcCicle.setStyle("background: #ffacac");
            }
        } else {
            lcCicle.setStyle("background: #ffacac");
        }

        // Код
        Listcell lcCode = new Listcell("");
        lcCode.setParent(li);
        if (data.getNewModel() != null) {
            lcCode.setLabel(data.getNewModel().getCode());
            if (!data.check(data.getNewModel().getCode(), data.getOldModel() == null ? null : data.getOldModel().getCode())) {
                lcCode.setStyle("background: #ffacac");
            }
        } else {
            lcCode.setStyle("background: #ffacac");
        }

        // Название
        Listcell lcName = new Listcell("");
        lcName.setParent(li);
        if (data.getNewModel() != null) {
            //lcName.setLabel("---");
            lcName.setLabel(data.getNewModel().getName());
            lcName.setStyle("white-space: nowrap; overflow: hidden; text-overflow: ellipsis;");
            lcName.setTooltiptext(data.getNewModel().getName());
            if (!data.check(data.getNewModel().getName(), data.getOldModel() == null ? null : data.getOldModel().getName())) {
                lcName.setStyle("white-space: nowrap; overflow: hidden; text-overflow: ellipsis; background: #ffacac;");
            }
        } else {
            lcName.setStyle("background: #ffacac");
        }

        // Часы (все)
        Listcell lcAllHours = new Listcell("");
        lcAllHours.setParent(li);
        if (data.getNewModel() != null) {
            lcAllHours.setLabel(data.getNewModel().getAllHours() == null ? "" : data.getNewModel().getAllHours().toString());
            if (!data.check(data.getNewModel().getAllHours(), data.getOldModel() == null ? null : data.getOldModel().getAllHours())) {
                lcAllHours.setStyle("background: #ffacac");
            }
        } else {
            lcAllHours.setStyle("background: #ffacac");
        }

        // Лек
        Listcell lcLecHours = new Listcell("");
        lcLecHours.setParent(li);
        if (data.getNewModel() != null) {
            lcLecHours.setLabel(data.getNewModel().getLecHours() == null ? "" : data.getNewModel().getLecHours().toString());
            if (!data.check(data.getNewModel().getLecHours(), data.getOldModel() == null ? null : data.getOldModel().getLecHours())) {
                lcLecHours.setStyle("background: #ffacac");
            }
        } else {
            lcLecHours.setStyle("background: #ffacac");
        }

        // Лаб
        Listcell lcLabHours = new Listcell();
        lcLabHours.setParent(li);
        if (data.getNewModel() != null) {
            lcLabHours.setLabel(data.getNewModel().getLabHours() == null ? "" : data.getNewModel().getLabHours().toString());
            if (!data.check(data.getNewModel().getLabHours(), data.getOldModel() == null ? null : data.getOldModel().getLabHours())) {
                lcLabHours.setStyle("background: #ffacac");
            }
        } else {
            lcLabHours.setStyle("background: #ffacac");
        }

        // Практ
        Listcell lcPraHours = new Listcell();
        lcPraHours.setParent(li);
        if (data.getNewModel() != null) {
            lcPraHours.setLabel(data.getNewModel().getPraHours() == null ? "" : data.getNewModel().getPraHours().toString());
            if (!data.check(data.getNewModel().getPraHours(), data.getOldModel() == null ? null : data.getOldModel().getPraHours())) {
                lcPraHours.setStyle("background: #ffacac");
            }
        } else {
            lcPraHours.setStyle("background: #ffacac");
        }

        // СРС
        Listcell lcKsrHours = new Listcell();
        lcKsrHours.setParent(li);
        if (data.getNewModel() != null) {
            lcKsrHours.setLabel(data.getNewModel().getKsrHours() == null ? "" : data.getNewModel().getKsrHours().toString());
            if (!data.check(data.getNewModel().getKsrHours(), data.getOldModel() == null ? null : data.getOldModel().getKsrHours())) {
                lcKsrHours.setStyle("background: #ffacac");
            }
        } else {
            lcKsrHours.setStyle("background: #ffacac");
        }

        // Кафедра
        Listcell lcKaf = new Listcell();
        lcKaf.setParent(li);
        if (data.getNewModel() != null) {
            lcKaf.setLabel(data.getNewModel().getChairCode() == null ? "" : data.getNewModel().getChairCode().toString());
            if (!data.check(data.getNewModel().getChairCode(), data.getOldModel() == null ? null : data.getOldModel().getChairCode())) {
                lcKaf.setStyle("background: #ffacac");
            }
        } else {
            lcKaf.setStyle("background: #ffacac");
        }

        // Компетенци
        Listcell lcComp = new Listcell();
        lcComp.setParent(li);
        if (data.getNewModel() != null) {
            lcComp.setLabel(data.getNewModel().getCompetenceString());
            lcComp.setStyle("border-right: 3px solid red; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;");
            lcComp.setTooltiptext(data.getNewModel().getCompetenceString());
            if (!data.check(data.getNewModel().getCompetenceString(), data.getOldModel() == null ? null : data.getOldModel().getCompetenceString())) {
                lcComp.setStyle("border-right: 3px solid red; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; background: #ffacac;");
            }
        } else {
            lcComp.setStyle("border-right: 3px solid red; background: #ffacac");
        }

        /// =========================Старая модель
        // Сем
        Listcell lcSemOld = new Listcell("");
        lcSemOld.setParent(li);
        if (data.getOldModel() != null) {
            lcSemOld.setLabel(data.getOldModel().getSemesterNumber() == null ? "" : data.getOldModel().getSemesterNumber().toString());
        } else {
            lcSemOld.setStyle("background: #ffacac");
        }

        // Цикл
        Listcell lcCicleOld = new Listcell("");
        lcCicleOld.setParent(li);
        if (data.getOldModel() != null) {
            lcCicleOld.setLabel(data.getOldModel().getCicleCode());
        } else {
            lcCicleOld.setStyle("background: #ffacac");
        }

        // Код
        Listcell lcCodeOld = new Listcell("");
        lcCodeOld.setParent(li);
        if (data.getOldModel() != null) {
            lcCodeOld.setLabel(data.getOldModel().getCode());
        } else {
            lcCodeOld.setStyle("background: #ffacac");
        }

        // Название
        Listcell lcNameOld = new Listcell("");
        lcNameOld.setParent(li);
        if (data.getOldModel() != null) {
            lcNameOld.setLabel(data.getOldModel().getName());
            lcNameOld.setStyle("white-space: nowrap; overflow: hidden; text-overflow: ellipsis;");
            lcNameOld.setTooltiptext(data.getOldModel().getName());
        } else {
            lcNameOld.setStyle("background: #ffacac");
        }

        // Часы (все)
        Listcell lcAllHoursOld = new Listcell("");
        lcAllHoursOld.setParent(li);
        if (data.getOldModel() != null) {
            lcAllHoursOld.setLabel(data.getOldModel().getAllHours() == null ? "" : data.getOldModel().getAllHours().toString());
        } else {
            lcAllHoursOld.setStyle("background: #ffacac");
        }

        // Лек
        Listcell lcLecHoursOld = new Listcell("");
        lcLecHoursOld.setParent(li);
        if (data.getOldModel() != null) {
            lcLecHoursOld.setLabel(data.getOldModel().getLecHours() == null ? "" : data.getOldModel().getLecHours().toString());
        } else {
            lcLecHoursOld.setStyle("background: #ffacac");
        }

        // Лаб
        Listcell lcLabHoursOld = new Listcell();
        lcLabHoursOld.setParent(li);
        if (data.getOldModel() != null) {
            lcLabHoursOld.setLabel(data.getOldModel().getLabHours() == null ? "" : data.getOldModel().getLabHours().toString());
        } else {
            lcLabHoursOld.setStyle("background: #ffacac");
        }

        // Практ
        Listcell lcPraHoursOld = new Listcell();
        lcPraHoursOld.setParent(li);
        if (data.getOldModel() != null) {
            lcPraHoursOld.setLabel(data.getOldModel().getPraHours() == null ? "" : data.getOldModel().getPraHours().toString());
        } else {
            lcPraHoursOld.setStyle("background: #ffacac");
        }

        // СРС
        Listcell lcKsrHoursOld = new Listcell();
        lcKsrHoursOld.setParent(li);
        if (data.getOldModel() != null) {
            lcKsrHoursOld.setLabel(data.getOldModel().getKsrHours() == null ? "" : data.getOldModel().getKsrHours().toString());
        } else {
            lcKsrHoursOld.setStyle("background: #ffacac");
        }

        // Кафедра
        Listcell lcKafOld = new Listcell();
        lcKafOld.setParent(li);
        if (data.getOldModel() != null) {
            lcKafOld.setLabel(data.getOldModel().getChairCode() == null ? "" : data.getOldModel().getChairCode().toString());
        } else {
            lcKafOld.setStyle("background: #ffacac");
        }

        // Компетенци
        Listcell lcCompOld = new Listcell();
        lcCompOld.setParent(li);
        if (data.getOldModel() != null) {
            lcCompOld.setLabel(data.getOldModel().getCompetenceString());
            lcCompOld.setStyle("white-space: nowrap; overflow: hidden; text-overflow: ellipsis;");
            lcCompOld.setTooltiptext(data.getOldModel().getCompetenceString());
        } else {
            lcCompOld.setStyle("background: #ffacac");
        }

    }
}
