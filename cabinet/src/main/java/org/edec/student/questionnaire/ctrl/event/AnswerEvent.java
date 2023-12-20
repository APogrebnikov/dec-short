package org.edec.student.questionnaire.ctrl.event;

import org.edec.student.questionnaire.ctrl.QuestionnaireCtrl;
import org.edec.student.questionnaire.model.AnswerModel;
import org.edec.student.questionnaire.model.QuestionModel;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;


public class AnswerEvent implements EventListener<Event> {
    private QuestionModel question;

    public AnswerEvent(QuestionModel question) {
        this.question = question;
    }

    @Override
    public void onEvent(Event event) throws Exception {
        boolean isFocus = true;
        if (event.getData() != null && event.getData().equals(false)) {
            isFocus = false;
        }

        Component mainWin = Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winQuestionnaire");
        A aQuestion = (A) ComponentHelper.find(mainWin, "aQuestion" + question.getId());
        Div divQuestion = (Div) ComponentHelper.find(mainWin, "divQuestion" + question.getId());
        Button btnSend = (Button) ComponentHelper.find(mainWin, "btnSend");
        assert aQuestion != null && divQuestion != null && btnSend != null;
        if (question.isOptional()) {
            for (Component comp : divQuestion.getChildren()) {
                if (comp instanceof Div && !comp.getId().contains("optional")) {
                    ((Div) comp).setStyle("pointer-events: none; opacity: 0.4;");
                }
            }
            aQuestion.setSclass("list-group-item selected-group-item");
        } else {
            for (Component comp : divQuestion.getChildren()) {
                if (comp instanceof Div && !comp.getId().contains("optional")) {
                    ((Div) comp).setStyle("");
                }
            }
            checkQuestion(divQuestion, question);
            List<QuestionModel> answeredQuestion = (ArrayList<QuestionModel>) btnSend.getAttribute(QuestionnaireCtrl.ANSWERED_QUESTION);
            int countQuestion = (int) btnSend.getAttribute(QuestionnaireCtrl.COUNT_QUESTION);
            int countAnsweredQuestion = (int) btnSend.getAttribute(QuestionnaireCtrl.COUNT_ANSWERED_QUESTION);
            if (question.getChoosenAnswer().size() == 0) {
                if (answeredQuestion.contains(question)) {
                    answeredQuestion.remove(question);
                    if (question.getRequired() == 1) {
                        countAnsweredQuestion = countAnsweredQuestion - 1;
                    }
                }
                aQuestion.setSclass("list-group-item");
            } else {
                if (!answeredQuestion.contains(question)) {
                    answeredQuestion.add(question);
                    if (question.getRequired() == 1) {
                        countAnsweredQuestion = countAnsweredQuestion + 1;
                    }
                }
                aQuestion.setSclass("list-group-item selected-group-item");
            }
            btnSend.setAttribute(QuestionnaireCtrl.COUNT_QUESTION, countQuestion);
            btnSend.setAttribute(QuestionnaireCtrl.COUNT_ANSWERED_QUESTION, countAnsweredQuestion);
            if (countAnsweredQuestion >= countQuestion) {
                btnSend.setDisabled(false);
                btnSend.setSclass("completed-btn");
                btnSend.focus();
                Clients.showNotification("Завершение опроса", "info", btnSend, "before_end", 5000);
            }
            if (isFocus) {
                aQuestion.focus();
                divQuestion.focus();
            }
        }
    }

    private void checkQuestion(Div divQuestion, QuestionModel question) {
        for (Component compAns : divQuestion.getChildren()) {
            if (!(compAns instanceof Div) || compAns.getAttribute("data") == null) {
                continue;
            }
            Div divAnswer = (Div) compAns;

            AnswerModel answer = (AnswerModel) divAnswer.getAttribute("data");

            //Если тип радио
            if (question.getType() == 0) {
                Label lblError = null;
                Textbox tbForRadio = null;
                Radio radio = null;
                for (Component comp : divAnswer.getChildren()) {
                    if (comp instanceof Hbox) {
                        Hbox hbAction = (Hbox) comp;
                        for (Component child : hbAction.getChildren()) {
                            if (child instanceof Label) {
                                lblError = (Label) child;
                            }
                        }
                    }
                    if (comp instanceof Textbox) {
                        tbForRadio = (Textbox) comp;
                    }
                    if (comp instanceof Radio) {
                        radio = (Radio) comp;
                    }
                }
                if (radio.isSelected()) { //Если кнопка нажата
                    checkCustomElement(tbForRadio, lblError, answer, question, divAnswer);
                } else { //Если кнопка отжата
                    if (lblError != null) {
                        lblError.setVisible(false);
                    }
                    question.getChoosenAnswer().remove(answer);
                    answer.setValue(null);
                    divAnswer.setSclass("input-group answer");
                }
            } else if (question.getType() == 1) { //Чекбоксы
                Textbox tbForCheckbox = null;
                Label lblError = null;
                Checkbox checkbox = null;
                for (Component comp : divAnswer.getChildren()) {
                    if (comp instanceof Hbox) {
                        Hbox hbAction = (Hbox) comp;
                        for (Component child : hbAction.getChildren()) {
                            if (child instanceof Label) {
                                lblError = (Label) child;
                            }
                        }
                    }
                    if (comp instanceof Textbox) {
                        tbForCheckbox = (Textbox) comp;
                    }
                    if (comp instanceof Checkbox) {
                        checkbox = (Checkbox) comp;
                    }
                }
                if (checkbox.isChecked()) {
                    checkCustomElement(tbForCheckbox, lblError, answer, question, divAnswer);
                } else {
                    lblError.setVisible(false);
                    question.getChoosenAnswer().remove(answer);
                    answer.setValue(null);
                    divAnswer.setSclass("input-group answer");
                }
            } else if (question.getType() == 2) {  //Текст
                Textbox textbox = null;
                for (Component comp : divAnswer.getChildren()) {
                    if (comp instanceof Textbox) {
                        textbox = (Textbox) comp;
                    }
                }
                answer.setValue(textbox.getValue());
                if (textbox.getValue().equals("")) {
                    divAnswer.setSclass("input-group answer");
                    if (question.getChoosenAnswer().contains(answer)) {
                        question.getChoosenAnswer().remove(answer);
                    }
                } else {
                    divAnswer.setSclass("input-group answer selected");
                    if (!question.getChoosenAnswer().contains(answer)) {
                        question.getChoosenAnswer().add(answer);
                    }
                }
            } else if (question.getType() == 3) {  //Шкала
                Hbox hbox = null;
                for (Component compHbox : divAnswer.getChildren()) {
                    if (compHbox instanceof Hbox) {
                        hbox = (Hbox) compHbox;
                    }
                }
                for (Component componentVbox : hbox.getChildren()) {
                    Vbox vbox = (Vbox) componentVbox;
                    Radio radioScale = null;
                    for (Component compRadio : vbox.getChildren()) {
                        if (compRadio instanceof Radio) {
                            radioScale = (Radio) compRadio;
                        }
                    }
                    if (radioScale.isSelected()) {
                        while (question.getChoosenAnswer().size() > 0) {
                            question.getChoosenAnswer().remove(0);
                        }
                        question.getChoosenAnswer().add((AnswerModel) radioScale.getValue());
                        vbox.setSclass("form-control selected");
                    } else {
                        if (question.getChoosenAnswer().contains(radioScale.getValue())) {
                            question.getChoosenAnswer().remove(radioScale.getValue());
                        }
                        vbox.setSclass("form-control");
                    }
                }
            }
        }
    }

    private void checkCustomElement(Textbox textbox, Label lblError, AnswerModel answer, QuestionModel question, Div divAnswer) {

        //Если элемент кастоматизирован (с элементом текстбокс), то проверяем еще на заполняемость текстбокса
        if (textbox != null) {
            if (!textbox.getValue().equals("")) {
                answer.setValue(textbox.getValue());
                divAnswer.setSclass("input-group answer selected");
                lblError.setVisible(false);
                if (!question.getChoosenAnswer().contains(answer)) {
                    question.getChoosenAnswer().add(answer);
                }
            } else {
                lblError.setVisible(true);
                divAnswer.setSclass("input-group answer");
            }
        } else {
            if (!question.getChoosenAnswer().contains(answer)) {
                question.getChoosenAnswer().add(answer);
            }
            answer.setValue(answer.getName());
            divAnswer.setSclass("input-group answer selected");
        }
    }
}
