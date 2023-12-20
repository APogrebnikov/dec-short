package org.edec.questionnaire.service;

import org.edec.questionnaire.model.GroupModel;

import java.util.List;

public interface QuestionnaireServiceESO {

    List<GroupModel> getGroupBySem(Long idSem);

    List<String> getRecipients(Long idSem, Long idDG);
}
