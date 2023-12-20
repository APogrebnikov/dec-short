package org.edec.questionnaire.ctrl;

import org.edec.notification.model.NotificationModel;
import org.edec.notification.service.NotificationService;
import org.edec.notification.service.impl.NotificationServiceImpl;
import org.edec.questionnaire.model.*;
import org.edec.questionnaire.service.QuestionnaireServiceESO;
import org.edec.questionnaire.service.QuestionnaireServiceEnsemble;
import org.edec.questionnaire.service.impl.QuestionnaireServiceESOimpl;
import org.edec.questionnaire.service.impl.QuestionnaireServiceEnsembleImpl;
import org.edec.utility.zk.CabinetSelector;
import org.json.JSONObject;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class IndexPageCtrl extends CabinetSelector {

    private QuestionnaireServiceESO questionnaireServiceESO = new QuestionnaireServiceESOimpl();
    private QuestionnaireServiceEnsemble questionnaireServiceEnsemble = new QuestionnaireServiceEnsembleImpl();

    protected void fill() {
    }

    @Listen("onClick = #btnSendQuestionnaire")
    public void sendQuestionnaire() {


        TopQuestModel topQuest = new TopQuestModel("Опросник для оценки качества преподавания дисциплин в весеннем семестре 2020-2021гг",
                "Анкета студента (Весна 2020-2021)"
        );
        Long idTopQuest = questionnaireServiceEnsemble.sendTopQuest(topQuest);


        //Long idTopQuest = 17L;


        System.out.println("ID TopQuest: " + idTopQuest);

        List<AnswerModel> radioAnswers = generateAnswersRadio(5, 2);
        List<AnswerModel> answerModelsForText = Collections.singletonList(new AnswerModel(0, "Введите свои пожелания", null));

        List<QuestionnaireModel> questionnaires = new ArrayList<>();

        Long syncSemesterId = 74L;
        Long senderId = 214503L;

        List<GroupModel> groups = questionnaireServiceESO.getGroupBySem(syncSemesterId);
        for (GroupModel group : groups) {
            List<BlockModel> blockModels = new ArrayList<>();

            for (SubjectModel subject : group.getSubjects()) {
                List<QuestionModel> subjectQuestions = new ArrayList<>();
                subjectQuestions.add(
                        new QuestionModel(1L, 1, 1, 0, "", "Полнота и качество электронного образовательного ресурса", radioAnswers));
                subjectQuestions.add(
                        new QuestionModel(2L, 1, 1, 0, "", "Доступность изложения теоретического материала на лекционных занятиях",
                                radioAnswers
                        ));
                subjectQuestions.add(new QuestionModel(6L, 1, 1, 0, "", "Качество проведения практических занятий", radioAnswers));
                subjectQuestions.add(
                        new QuestionModel(7L, 0, 0, 2, "",
                                "Замечания и пожелания по преподаванию дисциплины", answerModelsForText));

                blockModels.add(new BlockModel(1L, new JSONObject().put("subject", subject.getIdSubj()).toString(),
                        "Качество подготовки по дисциплине " + subject.getSubjectname() +
                                " (дисциплина за предыдущий семестр)", subject.getSubjectname(), subjectQuestions
                ));
            }

            //Блок по своим пожеланиям
            List<QuestionModel> questionComments = new ArrayList<>();
            questionComments.add(new QuestionModel(19L, 0, 0, 2, "", "Опишите Ваши замечания и пожелания по обучению в ИКИТ",
                    answerModelsForText
            ));
            blockModels.add(new BlockModel(4L, "", "Качество подготовки ИКИТ", "Качество подготовки ИКИТ", questionComments));
            List<String> recipients = questionnaireServiceESO.getRecipients(syncSemesterId, group.getIdDG());
            if (recipients == null || recipients.size() == 0) {
                System.out.println("Не удалось получить получателей!!!!!" + group.getGroupname());
                continue;
            }

            questionnaires.add(new QuestionnaireModel(new Date(), //Дата отправления
                    new JSONObject().put("group", group.getIdDG()).put("semester", syncSemesterId).toString(),
                    //Дополнителньые свойства
                    "Шкала оценок: <br/>" +//Описание опроса
                            "<b>5</b> - высший балл;<br/>" + "<b>1</b> - низший бал;<br/>",
                    "Анкета студента (весна 2020-2021) - " + group.getGroupname(),//Название опроса
                    idTopQuest, senderId,//Кто отправил <= это ид Сомовой
                    recipients, //Получатели
                    blockModels
            ));
        }

        int quirc = 0, bc = 0, qc = 0, ansc = 0;

        for (QuestionnaireModel questionnaire : questionnaires) {
            questionnaireServiceEnsemble.sendQuestionnaire(questionnaire);
            quirc++;
            for (BlockModel block : questionnaire.getBlocks()) {
                bc++;
                for (QuestionModel question : block.getQuestions()) {
                    qc++;
                    ansc += question.getAnswers().size();
                }
            }
        }

        System.out.println("Создано:");
        System.out.println("Опросников:" + quirc);
        System.out.println("Блоков:" + bc);
        System.out.println("Вопросов:" + qc);
        System.out.println("Ответов:" + ansc);
    }

    private List<AnswerModel> generateAnswersRadio(int countAnswers, int countAnswerWithText) {
        if (countAnswerWithText > countAnswers) {
            return null;
        }
        List<AnswerModel> answers = new ArrayList<>();
        for (int i = 0; i < countAnswers; i++) {
            answers.add(new AnswerModel((i < countAnswerWithText) ? 1 : 0, String.valueOf(i + 1), i + 1));
        }
        return answers;
    }

    /**
     * Формирование временного опроса о дистанционном формате
     */
    @Listen("onClick = #btnSendTempQuestionnaire")
    public void generateTempQuestionnaire () {
        /*
        TopQuestModel topQuest = new TopQuestModel("Опросник об организации образовательного процесса в удаленном формате в осеннем семестре 2020/2021 уч. году",
                "Образовательный процесс в удаленном формате (Осень 2020-2021)"
        );
        Long idTopQuest = questionnaireServiceEnsemble.sendTopQuest(topQuest);*/
        Long idTopQuest = 16L;
        List<AnswerModel> radioAnswers = generateAnswersRadio(5, 2);
        List<AnswerModel> answerModelsForText = Collections.singletonList(new AnswerModel(0, "Введите свои пожелания", null));

        List<QuestionnaireModel> questionnaires = new ArrayList<>();

        Long syncSemesterId = 72L;
        Long senderId = 214503L;
        List<GroupModel> groups = questionnaireServiceESO.getGroupBySem(syncSemesterId);
        for (GroupModel group : groups) {
            //if (group.getGroupname().equals("КИ17-16Б")) {
                List<BlockModel> blockModels = new ArrayList<>();

                for (SubjectModel subject : group.getSubjects()) {
                    List<QuestionModel> subjectQuestions = new ArrayList<>();
                    subjectQuestions.add(
                            new QuestionModel(20L, 1, 1, 0, "",
                                    "Качество организации работы в удаленном формате",
                                    radioAnswers));
                    subjectQuestions.add(
                            new QuestionModel(21L, 1, 1, 0, "",
                                    "Трудоемкость дисциплины (1 – крайне низкая трудоемкость; 5 – очень высокая трудоемкость)",
                                    radioAnswers
                            ));
                    subjectQuestions.add(
                            new QuestionModel(22L, 1, 1, 0, "",
                                    "Оперативность организации контактной работы с  преподавателем (1 – преподаватель не реагирует на обращения; 5 – преподаватель реагирует на обращения очень оперативно)",
                                    radioAnswers));
                    subjectQuestions.add(
                            new QuestionModel(23L, 1, 1, 0, "",
                                    "Степень Вашей готовности к обучению в дистанционном формате",
                                    radioAnswers));
                    subjectQuestions.add(
                            new QuestionModel(24L, 1, 1, 0, "",
                                    "Степень готовности преподавателя к организации учебного процесса по дисциплине в дистанционном формате",
                                    radioAnswers));
                    subjectQuestions.add(
                            new QuestionModel(27L, 0, 0, 2, "",
                                    "\tОтзывы и пожелания по организации образовательного процесса по дисциплине",
                                    answerModelsForText));

                    blockModels.add(new BlockModel(1L, new JSONObject().put("subject", subject.getIdSubj()).toString(),
                            "Качество подготовки по дисциплине " + subject.getSubjectname(), subject.getSubjectname(), subjectQuestions
                    ));
                }

                //Блок по своим пожеланиям
                List<QuestionModel> questionComments = new ArrayList<>();
                questionComments.add(new QuestionModel(25L, 0, 0, 2, "", "Отзывы и пожелания по организации учебного процесса в институте (общие вопросы)",
                        answerModelsForText
                ));
                blockModels.add(new BlockModel(4L, "", "Качество подготовки ИКИТ", "Качество подготовки ИКИТ", questionComments));


                List<String> recipients = questionnaireServiceESO.getRecipients(syncSemesterId, group.getIdDG());
                if (recipients == null || recipients.size() == 0) {
                    System.out.println("Не удалось получить получателей!!!!!" + group.getGroupname());
                    continue;
                }
                /*
                List<String> recipients = new ArrayList<>();
                recipients.add("221011");
                */
                questionnaires.add(new QuestionnaireModel(new Date(), //Дата отправления
                        new JSONObject().put("group", group.getIdDG()).put("semester", syncSemesterId).toString(),
                        //Дополнителньые свойства
                        "Шкала оценок: <br/>" +//Описание опроса
                                "<b>5</b> - высший балл;<br/>" + "<b>1</b> - низший бал;<br/>",
                        "Образовательный процесс в удаленном формате (Осень 2020-2021) - " + group.getGroupname(),//Название опроса
                        idTopQuest, senderId,//Кто отправил <= это ид Сомовой
                        recipients, //Получатели
                        blockModels
                ));
           // }
        }

        int quirc = 0, bc = 0, qc = 0, ansc = 0;

        for (QuestionnaireModel questionnaire : questionnaires) {
            questionnaireServiceEnsemble.sendQuestionnaire(questionnaire);
            quirc++;
            for (BlockModel block : questionnaire.getBlocks()) {
                bc++;
                for (QuestionModel question : block.getQuestions()) {
                    qc++;
                    ansc += question.getAnswers().size();
                }
            }
        }

        System.out.println("Создано:");
        System.out.println("Опросников:" + quirc);
        System.out.println("Блоков:" + bc);
        System.out.println("Вопросов:" + qc);
        System.out.println("Ответов:" + ansc);
    }
}
