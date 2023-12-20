package org.edec.student.questionnaire.service;

import org.edec.student.questionnaire.model.AnswerModel;
import org.edec.student.questionnaire.model.QuestionnaireModel;

import java.util.List;


public interface QuestionnaireEnsembleService {
    boolean sendHumAnswer (Long id_humanface, Long id_questionnaire, List<AnswerModel> answers);

    List<QuestionnaireModel> getQuestionnairesByIdHum (Long id_hum);
    List<QuestionnaireModel> getAllQuestionnairesByIdHum (Long id_hum);

    QuestionnaireModel getQuestionnaire (Long id);
}
