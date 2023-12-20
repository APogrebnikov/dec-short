package org.edec.contingentMovement.service.impl;

import org.edec.contingentMovement.manager.ResitDAO;
import org.edec.contingentMovement.model.ResitRatingModel;
import org.edec.contingentMovement.service.ResitService;
import org.edec.order.manager.CreateOrderManagerESO;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.utility.zk.PopupUtil;

import java.util.ArrayList;
import java.util.List;

public class ResitImpl implements ResitService {
    private ResitDAO resitDAO = new ResitDAO();
    private CreateOrderManagerESO orderManagerESO = new CreateOrderManagerESO();

    @Override
    public List<ResitRatingModel> getListResitRatingModel(Long idStudent, Long idGroup, Boolean currentGroup) {
        List<ResitRatingModel> result = new ArrayList<>();
        for (ResitRatingModel rating : resitDAO.getListResitRating(idStudent, idGroup, currentGroup)) {
            ResitRatingModel.getRatingByDoubleFoc(rating, result);
        }
        return result;
    }

    @Override
    public void autoResit(List<ResitRatingModel> listRatingOldGroup, List<ResitRatingModel> listRatingCurrentGroup) {
        for (ResitRatingModel curRating : listRatingCurrentGroup) {

            // TODO
            // 1) Если совпадает форма контроля, название предмета и семестр - на часы не смотрим
            // 2) .Если совпадает форма контроля, название предмета,но семестр разный ,
            //    то разница в часах- должна быть не более 30% в меньшую сторону т.е
            //    (ПРИМЕР)
            //          было(в старой группе) 216, а стало 72 (в новой группе) - норм(перезачитываем),
            //          было 72(в старой группе),а стало 216(в новой группе) не норм(не перезачитываем)
            // 3) Если совпадает название, но форма контроля разная (был экзамен,а стал зачет), то перезачитываем как в пункте !2!.
            // 4) Если совпадает название , но форма контроля разная (был зачет,а стал экзамен), то НЕ перезачитываем.

            // 1
            ResitRatingModel foundOldRating = listRatingOldGroup.stream()
                    .filter(rating -> compareBySubjectName(rating, curRating)
                            && rating.getSemesternumber().equals(curRating.getSemesternumber())
                            && rating.getFocInt().equals(curRating.getFocInt()))
                    .findFirst().orElse(null);

            // 2
            if (foundOldRating == null) {
                foundOldRating = listRatingOldGroup.stream()
                        .filter(rating -> compareBySubjectName(rating, curRating)
                                && rating.getFocInt().equals(curRating.getFocInt())
                                && (rating.getHoursCount() / curRating.getHoursCount()) >= 0.7)
                        .findFirst().orElse(null);
            }

            // 3 и 4
            if (foundOldRating == null) {
                foundOldRating = listRatingOldGroup.stream()
                        .filter(rating -> compareBySubjectName(rating, curRating)
                                && (rating.getHoursCount() / curRating.getHoursCount()) >= 0.7)
                        .findFirst().orElse(null);
            }

            if (foundOldRating != null) {
                manualResit(foundOldRating, curRating);
            }
        }
    }

    private boolean compareBySubjectName(ResitRatingModel rating, ResitRatingModel currentRating) {
        return convertObjToSubjName(rating).equals(convertObjToSubjName(currentRating))
                || (convertObjToSubjName(rating) + " и спорт").equals(convertObjToSubjName(currentRating))
                || (convertObjToSubjName(rating).equals(convertObjToSubjName(currentRating) + " и спорт"));
    }

    private String convertObjToSubjName(ResitRatingModel rating) {
        return rating.getSubjectname().toLowerCase().trim().replaceAll(" +", " ");
    }

    @Override
    public boolean deleteAllResit(StudentStatusModel student) {
        return resitDAO.deleteAllResit(student.getIdStudentCard(), student.getIdDG());
    }

    @Override
    public boolean manualResit(ResitRatingModel oldRating, ResitRatingModel currentRating) {
        // если оценка отрицательная - не перезачитываем
        if ((oldRating.getRating() < 3 && oldRating.getRating() != 1)
                || (currentRating.getRating() != null && (currentRating.getRating() == 1 || currentRating.getRating() >= 3))
                || currentRating.getResitRating() != null || oldRating.getResitRating() != null) {
            return false;
        }

        boolean sameFoc = oldRating.getFoc().equals(currentRating.getFoc());

        if (sameFoc || (oldRating.getFoc().equals("Экзамен") && currentRating.getFocInt().equals(2)) ||
                (oldRating.getFoc().equals("КП") && currentRating.getFoc().equals("КР")) ||
                (oldRating.getFoc().equals("КР") && currentRating.getFoc().equals("КП")) ||
                (oldRating.getFoc().equals("Диф. зачет") && currentRating.getFoc().equals("Практика")) ||
                (oldRating.getFoc().equals("Диф. зачет") && currentRating.getFoc().equals("Зачет")) ||
                (oldRating.getFoc().equals("Практика") && currentRating.getFocInt().equals(2))) {

            currentRating.setRating(oldRating.getRating());
            if (currentRating.getFoc().equals("Зачет") && !oldRating.getFoc().equals("Зачет")) {
                currentRating.setRating(1);
            }
            currentRating.setResitRating(oldRating);
            oldRating.setResitRating(currentRating);
            return true;
        }
        return false;
    }

    @Override
    public boolean saveResit(List<ResitRatingModel> listOfResitRating, Long idCurrentUser) {
        boolean successfullySaveResit = true;
        for (ResitRatingModel resit : listOfResitRating) {
            if (!resitDAO.saveResitRating(resit, idCurrentUser)) {
                successfullySaveResit = false;
            }
        }
        return successfullySaveResit;
    }

    @Override
    public void fillDirectionCodeAndPreviousGroup(StudentStatusModel student) {
        String direction = resitDAO.getDirectionCode(student.getIdDG());

        String prevGroupName = resitDAO.getPreviousGroupName(student.getIdStudentCard());

        if (prevGroupName == null) {
            PopupUtil.showError("Не удалось определить предыдущую группу студента");
        } else {
            student.setPrevGroupName(prevGroupName);
        }

        student.setDirection(direction);
    }
}
