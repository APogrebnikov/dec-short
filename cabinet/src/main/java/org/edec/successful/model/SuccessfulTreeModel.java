package org.edec.successful.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.successful.ReportMarksEnum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class SuccessfulTreeModel {
    private SuccessfulTreeModel parent;
    private List<SuccessfulTreeModel> childs;

    private String value;
    private StudentReportModel studentReportModel;

    public SuccessfulTreeModel(SuccessfulTreeModel parent) {
        this.parent = parent;
        this.childs = new ArrayList<>();
    }

    public void addChild(SuccessfulTreeModel child) {
        childs.add(child);
    }

    /**
     *
     * ПОДСЧЕТЫ СТАТИСТИКИ
     *
     */

    // Количество всех студентов
    public int getCountAllStudents() {
        return getAllStudentsSet().size();
    }

    private Set<String> getAllStudentsSet() {
        Set<String> setStudents = new HashSet<>();

        for(SuccessfulTreeModel successfulTreeModel : childs) {
            if(successfulTreeModel.childs.size() == 0) {
                setStudents.add(successfulTreeModel.getStudentReportModel().getRecordbook());
            } else {
                setStudents.addAll(successfulTreeModel.getAllStudentsSet());
            }
        }

        return setStudents;
    }

    // Количество сдавших студентов
    public int getCountSuccessfulStudents() {
        return getSuccessfulStudentsSet().size();
    }

    private Set<String> getSuccessfulStudentsSet() {
        Set<String> setStudents = new HashSet<>();

        for(SuccessfulTreeModel successfulTreeModel : childs) {
            if(successfulTreeModel.childs.size() == 0 && successfulTreeModel.studentReportModel.getCountDebts() == 0) {
                setStudents.add(successfulTreeModel.getStudentReportModel().getRecordbook());
            } else {
                setStudents.addAll(successfulTreeModel.getSuccessfulStudentsSet());
            }
        }

        return setStudents;
    }

    // Количество студентов с долгами
    public int getCountUnsuccessfulStudents() {
        return getUnsuccessfulStudentsSet().size();
    }

    private Set<String> getUnsuccessfulStudentsSet() {
        Set<String> setStudents = new HashSet<>();

        for(SuccessfulTreeModel successfulTreeModel : childs) {
            if(successfulTreeModel.childs.size() == 0 && successfulTreeModel.studentReportModel.getCountDebts() > 0) {
                setStudents.add(successfulTreeModel.getStudentReportModel().getRecordbook());
            } else {
                setStudents.addAll(successfulTreeModel.getUnsuccessfulStudentsSet());
            }
        }

        return setStudents;
    }

    // Количество студентов с n количеством долгов
    public int getCountStudentsWithNDebts(int n) {
        return getStudentsWithNDebtsSet(n).size();
    }

    private Set<String> getStudentsWithNDebtsSet(int n) {
        Set<String> setStudents = new HashSet<>();

        for(SuccessfulTreeModel successfulTreeModel : childs) {
            if(successfulTreeModel.childs.size() == 0 && successfulTreeModel.studentReportModel.getCountDebts() == n) {
                setStudents.add(successfulTreeModel.getStudentReportModel().getRecordbook());
            } else {
                setStudents.addAll(successfulTreeModel.getStudentsWithNDebtsSet(n));
            }
        }

        return setStudents;
    }

    // Количество студентов с большим чем n количеством долгов
    public int getCountStudentsWithMoreThanNDebts(int n) {
        return getStudentsWithMoreThanNDebtsSet(n).size();
    }

    private Set<String> getStudentsWithMoreThanNDebtsSet(int n) {
        Set<String> setStudents = new HashSet<>();

        for(SuccessfulTreeModel successfulTreeModel : childs) {
            if(successfulTreeModel.childs.size() == 0 && successfulTreeModel.studentReportModel.getCountDebts() > n) {
                setStudents.add(successfulTreeModel.getStudentReportModel().getRecordbook());
            } else {
                setStudents.addAll(successfulTreeModel.getStudentsWithMoreThanNDebtsSet(n));
            }
        }

        return setStudents;
    }

    public int getCountStudentsWithMarksCriteria(ReportMarksEnum criteria) {
        return getStudentsWithMarksCriteriaSet(criteria).size();
    }

    private Set<String> getStudentsWithMarksCriteriaSet(ReportMarksEnum criteria) {
        Set<String> setStudents = new HashSet<>();

        for(SuccessfulTreeModel successfulTreeModel : childs) {
            if(successfulTreeModel.childs.size() == 0 && successfulTreeModel.studentReportModel.getMarksType() == criteria) {
                setStudents.add(successfulTreeModel.getStudentReportModel().getRecordbook());
            } else {
                setStudents.addAll(successfulTreeModel.getStudentsWithMarksCriteriaSet(criteria));
            }
        }

        return setStudents;
    }
}
