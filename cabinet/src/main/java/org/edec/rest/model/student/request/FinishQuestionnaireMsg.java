package org.edec.rest.model.student.request;

import lombok.Data;
import org.edec.rest.model.BaseUserMsg;
import org.edec.student.questionnaire.model.AnswerModel;

import java.util.List;

@Data
public class FinishQuestionnaireMsg extends BaseUserMsg {
    Long id;
    List<AnswerModel> answers;
}
