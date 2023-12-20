package org.edec.questionnaire.service.impl;

import org.edec.questionnaire.manager.QuestionnaireManager;
import org.edec.questionnaire.model.GroupModel;
import org.edec.questionnaire.model.SubjectModel;
import org.edec.questionnaire.model.dao.QuestionnaireEsoModel;
import org.edec.questionnaire.service.QuestionnaireServiceESO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestionnaireServiceESOimpl implements QuestionnaireServiceESO {
    private QuestionnaireManager questionnaireManager = new QuestionnaireManager();

    @Override
    public List<GroupModel> getGroupBySem(Long idSem) {
        return getGroupsByModel(questionnaireManager.getGroupsWithSubjectsBySemester(idSem));
    }

    @Override
    public List<String> getRecipients(Long idSem, Long idDG) {

        List<String> recipients = questionnaireManager.getRecipientsBySemesterAndGroup(idSem, idDG);
        if (recipients == null) {
            return null;
        }
        Set<String> tempList = new HashSet<>(recipients);
        return new ArrayList<>(tempList);
    }

    private List<GroupModel> getGroupsByModel(List<QuestionnaireEsoModel> models) {

        List<GroupModel> groups = new ArrayList<>();

        for (QuestionnaireEsoModel model : models) {

            GroupModel foundGroup = groups.stream()
                    .filter(group -> group.getGroupname().equals(model.getGroupname()))
                    .findFirst()
                    .orElse(null);

            if (foundGroup == null) {
                foundGroup = new GroupModel(model.getIdChair(), model.getIdDG(), model.getGroupname());
                groups.add(foundGroup);
            }

            foundGroup.getSubjects().add(new SubjectModel(model.getIdSubj(), model.getSubjectname()));
        }

        return groups;
    }
}
