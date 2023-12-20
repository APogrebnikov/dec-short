package org.edec.student.questionnaire.ctrl.renderer;

import org.edec.manager.HumanfaceManager;
import org.edec.model.HumanfaceModel;
import org.edec.student.questionnaire.ctrl.QuestionnaireCtrl;
import org.edec.student.questionnaire.model.QuestionnaireModel;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmmax work on 03.11.2017.
 */
public class QuestionnaireRenderer implements ListitemRenderer<QuestionnaireModel> {
    private HumanfaceManager humanfaceManager = new HumanfaceManager();

    @Override
    public void render (Listitem li, QuestionnaireModel data, int index) throws Exception {
        li.setValue(data);
        new Listcell(String.valueOf(index + 1)).setParent(li);
        new Listcell(data.getSubject()).setParent(li);

        HumanfaceModel human = humanfaceManager.getHumanById(data.getSenderId());
        new Listcell(human == null ? "" : human.getShortFio()).setParent(li);
        new Listcell(DateConverter.convertDateToString(data.getPosted())).setParent(li);

        li.addEventListener(Events.ON_CLICK, event -> {
            Map arg = new HashMap();
            arg.put(QuestionnaireCtrl.QUESTIONNAIRE_ID, data.getId());
            arg.put(QuestionnaireCtrl.QUESTIONNAIRE_SUBJECT, data.getSubject());
            arg.put(QuestionnaireCtrl.QUESTIONNAIRE_DESCRIPTION, data.getDescription());

            ComponentHelper.createWindow("/student/questionnaire/questionnaire.zul", "winQuestionnaire", arg).doModal();
        });
    }
}
