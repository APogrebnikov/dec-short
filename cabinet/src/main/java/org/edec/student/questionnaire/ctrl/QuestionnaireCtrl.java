package org.edec.student.questionnaire.ctrl;

import org.apache.log4j.Logger;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.student.questionnaire.ctrl.event.AnswerEvent;
import org.edec.student.questionnaire.model.AnswerModel;
import org.edec.student.questionnaire.model.BlockModel;
import org.edec.student.questionnaire.model.QuestionModel;
import org.edec.student.questionnaire.model.QuestionnaireModel;
import org.edec.student.questionnaire.service.QuestionnaireEnsembleService;
import org.edec.student.questionnaire.service.impl.QuestionnaireEnsembleImpl;
import org.edec.utility.constants.CabinetStyleClassConst;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireCtrl extends SelectorComposer<Component> {
    private static final Logger log = Logger.getLogger(QuestionnaireCtrl.class.getName());

    public static final String QUESTIONNAIRE_ID = "questionnaire_id";
    public static final String QUESTIONNAIRE_SUBJECT = "questionnaire_subject";
    public static final String QUESTIONNAIRE_DESCRIPTION = "questionnaire_description";
    public static final String COUNT_QUESTION = "count_question";
    public static final String COUNT_ANSWERED_QUESTION = "count_answered_question";
    public static final String ANSWERED_QUESTION = "answered_question";

    static final String INDEX_PAGE_QUESTIONNAIRE = "indexPageCtrlQuestionnaire";

    @Wire
    private Button btnSend, btnDescription;
    @Wire
    private Div divListBlock, divContainer;
    @Wire
    private Label lSubject;
    @Wire
    private Window winQuestionnaire;

    private QuestionnaireEnsembleService questionnaireEnsembleService = new QuestionnaireEnsembleImpl();
    private TemplatePageCtrl template = new TemplatePageCtrl();

    private IndexPageCtrl indexPageCtrl;
    private QuestionnaireModel questionnaire;
    private String subject;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        indexPageCtrl = (IndexPageCtrl) Executions.getCurrent().getSession().getAttribute(INDEX_PAGE_QUESTIONNAIRE);

        questionnaire = questionnaireEnsembleService.getQuestionnaire((Long) Executions.getCurrent().getArg().get(QUESTIONNAIRE_ID));
        subject = (String) Executions.getCurrent().getArg().get(QUESTIONNAIRE_SUBJECT);
        if (subject != null && !subject.equals("")) {
            lSubject.setValue(subject);
        }

        btnSend.setAttribute(COUNT_QUESTION, 0);
        btnSend.setAttribute(COUNT_ANSWERED_QUESTION, 0);
        btnSend.setAttribute(ANSWERED_QUESTION, new ArrayList<QuestionModel>());

        renderNavigation(questionnaire.getBlocks());
    }

    private void renderNavigation(List<BlockModel> blocks) {
        for (final BlockModel block : blocks) {
            Div divBlock = new Div();
            divBlock.setParent(divListBlock);
            divBlock.setSclass("list-group");

            A aBlock = new A(block.getName());
            aBlock.setParent(divBlock);
            aBlock.setSclass("list-group-item");

            for (final QuestionModel question : block.getQuestions()) {
                if (question.getRequired() == 1) {
                    btnSend.setAttribute(COUNT_QUESTION, (int) btnSend.getAttribute(COUNT_QUESTION) + 1);
                }
                final A aQuestion = new A(question.getName());
                aQuestion.setParent(divBlock);
                aQuestion.setId("aQuestion" + question.getId());
                aQuestion.setTooltiptext(question.getName());
                aQuestion.setSclass("list-group-item");
                aQuestion.addEventListener(Events.ON_CLICK, event -> renderContentDiv(block, question));
                if (block.getQuestions().indexOf(question) == 0) {
                    aBlock.addEventListener(Events.ON_CLICK, event -> Events.echoEvent(Events.ON_CLICK, aQuestion, null));
                }
            }
        }
    }

    private void renderContentDiv(BlockModel block, QuestionModel questionScroll) {
        divContainer.getChildren().clear();

        Div divBlockName = new Div();
        divBlockName.setParent(divContainer);
        divBlockName.setSclass("block");

        Label lBlockName = new Label(block.getName());
        lBlockName.setParent(divBlockName);

        for (QuestionModel question : block.getQuestions()) {
            Div divQuestion = new Div();
            divQuestion.setParent(divContainer);
            divQuestion.setId("divQuestion" + question.getId());
            divQuestion.setSclass("question");
            divQuestion.addEventListener(Events.ON_CLICK, new AnswerEvent(question));

            Label lQuestionName = new Label(question.getName());
            lQuestionName.setParent(divQuestion);
            if (question.getRequired() == 0) {
                Html html = new Html();
                html.setParent(divQuestion);
                html.setContent("<br>");

                Label lNotRequired = new Label("*необязательный");
                lNotRequired.setParent(divQuestion);
                lNotRequired.setStyle("font-size: 10pt;");
            }

            createAnswerDiv(divQuestion, question);
        }

        final BlockModel prevBlock = questionnaire.getBlocks().indexOf(block) > 0 ? questionnaire.getBlocks()
                .get(questionnaire.getBlocks()
                        .indexOf(
                                block) -
                        1) : null;

        final BlockModel nextBlock = questionnaire.getBlocks().indexOf(block) != questionnaire.getBlocks().size() - 1
                ? questionnaire.getBlocks()
                .get(questionnaire.getBlocks()
                        .indexOf(block) + 1)
                : null;

        Div divPager = new Div();
        divPager.setParent(divContainer);
        divPager.setSclass("pager");
        divPager.setStyle("margin-bottom: 20px;");

        A aPrevBlock = new A();
        aPrevBlock.setParent(divPager);
        aPrevBlock.setDisabled(prevBlock == null);
        aPrevBlock.setSclass("previous");
        aPrevBlock.addEventListener(Events.ON_CLICK, event -> renderContentDiv(prevBlock, prevBlock.getQuestions().get(0)));
        Label lPrevBlock = new Label("Предыдущий блок");
        lPrevBlock.setStyle("font-size: 16pt;");
        lPrevBlock.setParent(aPrevBlock);

        A aNextBlock = new A();
        aNextBlock.setParent(divPager);
        aNextBlock.setDisabled(nextBlock == null);
        aNextBlock.setSclass("next");
        aNextBlock.addEventListener(Events.ON_CLICK, event -> renderContentDiv(nextBlock, nextBlock.getQuestions().get(0)));
        Label lNextBlock = new Label("Следующий блок");
        lNextBlock.setStyle("font-size: 16pt;");
        lNextBlock.setParent(aNextBlock);

        Events.echoEvent("onScroll", divContainer, questionScroll);
    }

    @Listen("onScroll = #divContainer")
    public void scrollTo(Event e) {
        QuestionModel question = (QuestionModel) e.getData();
        Div divQuestion = (Div) ComponentHelper.find(winQuestionnaire, "divQuestion" + question.getId());
        divQuestion.focus();
    }

    private void createAnswerDiv(final Div divQuestion, final QuestionModel question) {
        Radiogroup radGr = null;
        if (question.getType() == 0 || question.getType() == 3) {
            radGr = new Radiogroup();
            radGr.setParent(divQuestion);
        }
        for (AnswerModel answer : question.getAnswers()) {
            Div divAnswer = new Div();
            divAnswer.setParent(divQuestion);
            divAnswer.setAttribute("data", answer);
            divAnswer.setSclass("input-group answer");

            if (question.getType() == 0) {
                final Radio radio = new Radio(answer.getName());
                radio.setParent(divAnswer);
                radio.setRadiogroup(radGr);
                radio.setSclass("input-group-addon");
                radio.setSelected(question.getChoosenAnswer().contains(answer));
                radio.addEventListener(Events.ON_CLICK, new AnswerEvent(question));
                if (answer.getCustom() == 0) {
                    divAnswer.addEventListener(Events.ON_CLICK, event -> {
                        radio.setSelected(true);
                        Events.echoEvent(Events.ON_CLICK, radio, null);
                    });
                } else {
                    divAnswer.addEventListener(Events.ON_CLICK, event -> {
                        radio.setSelected(true);
                        Events.echoEvent(Events.ON_CLICK, radio, false);
                    });
                }
                createModificationAnswer(divAnswer, answer.getCustom(), answer.getName(), answer.getValue(), question);
            } else if (question.getType() == 1) {
                final Checkbox checkbox = new Checkbox();
                checkbox.setSclass("input-group-addon");
                checkbox.setParent(divAnswer);
                checkbox.setChecked(question.getChoosenAnswer().contains(answer));
                checkbox.addEventListener(Events.ON_CHECK, new AnswerEvent(question));
                if (answer.getCustom() == 0) {
                    divAnswer.addEventListener(Events.ON_CLICK, event -> {
                        checkbox.setChecked(checkbox.isChecked());
                        Events.echoEvent(Events.ON_CHECK, checkbox, null);
                    });
                }
                createModificationAnswer(divAnswer, answer.getCustom(), answer.getName(), answer.getValue(), question);
            } else if (question.getType() == 2) {
                Textbox textbox = new Textbox(answer.getValue());
                textbox.setParent(divAnswer);
                textbox.setMultiline(true);
                textbox.setRows(3);
                textbox.setCols(50);
                textbox.setSclass("form-control");
                textbox.setPlaceholder("Напишите комментарий");

                Button btnSend = new Button("Сохранить");
                btnSend.setParent(divAnswer);
                btnSend.setStyle("input-group-btn");
                btnSend.addEventListener(Events.ON_CLICK, new AnswerEvent(question));
            } else if (question.getType() == 3) {
                Hbox hbox = new Hbox();
                hbox.setParent(divAnswer);
                for (int i = 0; i < question.getMaxVal(); ++i) {
                    AnswerModel tempAnswer = new AnswerModel();
                    tempAnswer.setId(answer.getId());
                    tempAnswer.setScore(i + 1);

                    Vbox vbLocal = new Vbox();
                    vbLocal.setParent(hbox);
                    vbLocal.setPack("center");
                    vbLocal.setAlign("center");
                    vbLocal.setStyle("cursor: pointer");
                    vbLocal.setSclass("form-control");

                    final Radio radio = new Radio();
                    radio.setParent(vbLocal);
                    radio.setRadiogroup(radGr);
                    radio.setValue(tempAnswer);
                    radio.setWidth("100px");
                    radio.setHeight("100px");

                    Label lAnswer = new Label(String.valueOf(i + 1));
                    lAnswer.setParent(vbLocal);

                    vbLocal.addEventListener(Events.ON_CLICK, event -> {
                        radio.setSelected(true);
                        Events.echoEvent(Events.ON_CLICK, divQuestion, null);
                    });
                    for (AnswerModel answerSelected : question.getChoosenAnswer()) {
                        if (tempAnswer.getScore().equals(answerSelected.getScore())) {
                            radio.setSelected(true);
                        }
                    }
                }
            }
        }
        if (question.getOptional() == 1) {
            final Div divOptional = new Div();
            divOptional.setId("optional" + question.getId());
            divOptional.setParent(divQuestion);
            divOptional.setStyle("cursor: pointer;");
            divOptional.setSclass("input-group");

            final Checkbox chOptional = new Checkbox();
            chOptional.setParent(divOptional);
            chOptional.setSclass("input-group-addon");
            chOptional.setChecked(question.isOptional());

            Label lOptional = new Label("Затрудняюсь ответить");
            lOptional.setSclass("form-control");
            lOptional.setParent(divOptional);

            chOptional.addEventListener(Events.ON_CHECK, event -> {
                int countQuestion = (int) btnSend.getAttribute(COUNT_QUESTION);
                int countAnsweredQuestion = (int) btnSend.getAttribute(COUNT_ANSWERED_QUESTION);
                if (!question.isOptional()) {
                    btnSend.setAttribute(COUNT_QUESTION, countQuestion - 1);
                    if (question.getChoosenAnswer().size() > 0) {
                        btnSend.setAttribute(COUNT_ANSWERED_QUESTION, countAnsweredQuestion - 1);
                    }
                    question.setOptional(true);
                } else {
                    btnSend.setAttribute(COUNT_QUESTION, countQuestion + 1);
                    if (question.getChoosenAnswer().size() > 0) {
                        btnSend.setAttribute(COUNT_ANSWERED_QUESTION, countAnsweredQuestion + 1);
                    }
                    question.setOptional(false);
                }
                Events.echoEvent(Events.ON_CLICK, divQuestion, null);
            });
            divOptional.addEventListener(Events.ON_CLICK, event -> {
                chOptional.setChecked(!chOptional.isChecked());
                Events.echoEvent(Events.ON_CHECK, chOptional, null);
            });
        }
        Events.echoEvent(Events.ON_CLICK, divQuestion, null);
    }

    private void createModificationAnswer(final Div divAnswer, int custom, String answerName, String answerValue, QuestionModel question) {
        if (custom == 0) {
            Label lAnswer = new Label(answerName);
            lAnswer.setParent(divAnswer);
            lAnswer.setSclass("form-control");
        } else {
            Textbox textbox = new Textbox(answerValue);
            textbox.setParent(divAnswer);
            textbox.setPlaceholder("Добавьте комментарий");
            textbox.setSclass("form-control");

            Hbox hbAction = new Hbox();
            hbAction.setParent(divAnswer);

            Button btnSend = new Button("Сохранить");
            btnSend.setParent(hbAction);
            btnSend.setStyle("input-group-btn");
            btnSend.addEventListener(Events.ON_CLICK, new AnswerEvent(question));

            Label lblError = new Label("Введите комментарий и нажмите сохранить");
            lblError.setParent(hbAction);
            lblError.setSclass(CabinetStyleClassConst.BACKGROUND_ORANGE);
            lblError.setVisible(false);
        }
    }

    @Listen("onClick = #btnSend")
    public void sendToServer() {
        int countQuestions = (int) btnSend.getAttribute(COUNT_QUESTION);
        int countAnsweredQuestions = (int) btnSend.getAttribute(COUNT_ANSWERED_QUESTION);
        if (countQuestions != countAnsweredQuestions) {
            List<QuestionModel> answeredQuestions = (ArrayList<QuestionModel>) btnSend.getAttribute(QuestionnaireCtrl.ANSWERED_QUESTION);
            for (BlockModel block : questionnaire.getBlocks()) {
                for (QuestionModel question : block.getQuestions()) {
                    if (question.getChoosenAnswer().isEmpty() && !answeredQuestions.contains(question)) {
                        renderContentDiv(block, question);
                        String message = "Осталось ответить на ";
                        int difference = countQuestions - countAnsweredQuestions;
                        if (difference == 1) {
                            message += "1 вопрос ";
                        } else if (difference > 1 && difference < 5) {
                            message += difference + " вопроса ";
                        } else {
                            message += difference + " вопросов ";
                        }
                        message += "из " + countQuestions;
                        DialogUtil.info(message);
                        return;
                    }
                }
            }
            return;
        }
        Clients.showBusy("Отправка ответа на сервер");
        List<AnswerModel> tempAnswerList = new ArrayList<>();
        for (BlockModel block : questionnaire.getBlocks()) {
            for (QuestionModel question : block.getQuestions()) {
                if (!question.isOptional()) {
                    tempAnswerList.addAll(question.getChoosenAnswer());
                }
            }
        }
        try {
            Long idHuman = template.getCurrentUser().getIdHum();
            /* Для отладки */
            //Long idHuman = 221011L;
            if (questionnaireEnsembleService.sendHumAnswer(
                    idHuman, questionnaire.getId(), tempAnswerList)) {
                log.info(template + " успешно завершил опросник \"" + subject + "\"");
            } else {
                log.warn(template.getCurrentUser().getFio() + " не удалось завершить опросник \"" + subject + "\"");
            }
            indexPageCtrl.rendererListbox();
            Clients.clearBusy();
            PopupUtil.showInfo("Опрос успешно завершен.");
            winQuestionnaire.detach();
        } catch (Exception e) {
            PopupUtil.showError("Не удалось отправить ответы на сервер! Обратитесь к администратору.");
            e.printStackTrace();
            log.warn(template.getCurrentUser().getFio() + " не удалось завершить опросник \"" + subject + "\"");
            Clients.clearBusy();
        }
    }

    @Listen("onClick = #btnDescription")
    public void showPopup() {
        if (Selectors.find(winQuestionnaire, "#winPopup").size() > 0) {
            Selectors.find(winQuestionnaire, "#winPopup").get(0).detach();
        } else {
            Window winPopup = new Window();
            winPopup.setId("winPopup");
            winPopup.setPosition("right, center");
            winPopup.setClosable(true);
            winPopup.setParent(winQuestionnaire);

            Caption capDescription = new Caption("Описание");
            capDescription.setParent(winPopup);

            Html htmlDescription = new Html();
            htmlDescription.setParent(winPopup);
            htmlDescription.setContent(questionnaire.getDescription());
            winPopup.doOverlapped();
        }
    }
}
