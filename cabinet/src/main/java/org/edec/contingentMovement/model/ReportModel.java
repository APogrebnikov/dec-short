package org.edec.contingentMovement.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReportModel {
    private GroupModel group;
    private String groupName;
    private Long groupId;
    private List<StudentMoveModel> budgetStudents;
    private Integer countOfBudget = 0;
    private List<StudentMoveModel> unBudgetStudents;
    private Integer countOfUnBudget = 0;
    private List<StudentMoveModel> budgetAcademicStudents;
    private Integer countOfBudgetAcademic = 0;
    private List<StudentMoveModel> unBudgetAcademicStudents;
    private Integer countOfUnBudgetAcademic = 0;
    private List<StudentMoveModel> budgetMoveInStudents;
    private Integer countOfBudgetMoveIn = 0;
    private List<StudentMoveModel> unBudgetMoveInStudents;
    private Integer countOfUnBudgetMoveIn = 0;
    private List<StudentMoveModel> budgetMoveOutStudents;
    private Integer countOfBudgetMoveOut = 0;
    private List<StudentMoveModel> unBudgetMoveOutStudents;
    private Integer countOfUnBudgetMoveOut = 0;
    private List<StudentMoveModel> unBudgetAcademicMoveOutStudents;
    private Integer countOfUnBudgetAcademicMoveOut = 0;
    private List<StudentMoveModel> budgetAcademicMoveOutStudents;
    private Integer countOfBudgetAcademicMoveOut = 0;

    private List<StudentMoveModel> budgetAcademicMoveInStudents;
    private Integer countOfBudgetAcademicMoveIn = 0;
    private List<StudentMoveModel> unBudgetAcademicMoveInStudents;
    private Integer countOfUnBudgetAcademicMoveIn = 0;

    private Integer countOfBudgetOld = 0;
    private Integer countOfUnBudgetOld = 0;
    private Integer countOfBudgetAcademicOld = 0;
    private Integer countOfUnBudgetAcademicOld = 0;

    public List<StudentMoveModel> getBudgetWithMove ()
    {
        List<StudentMoveModel> result = new ArrayList<>();
        result.addAll(budgetStudents);
        result.removeAll(budgetMoveInStudents);
        result.addAll(budgetMoveOutStudents);
        result.addAll(budgetAcademicMoveOutStudents);
        return result;
    }

    public List<StudentMoveModel> getUnBudgetWithMove ()
    {
        List<StudentMoveModel> result = new ArrayList<>();
        result.addAll(unBudgetStudents);
        result.removeAll(unBudgetMoveInStudents);
        result.addAll(unBudgetMoveOutStudents);
        result.addAll(unBudgetAcademicMoveOutStudents);
        return result;
    }

    public Integer getCountOfBudget() {
        if (getBudgetStudents() != null)
            return getBudgetStudents().size();
        return countOfBudget;
    }

    public Integer getCountOfUnBudget() {
        if (getUnBudgetStudents() != null)
            return getUnBudgetStudents().size();
        return countOfUnBudget;
    }

    public Integer getCountOfBudgetAcademic() {
        if (getBudgetAcademicStudents() != null)
            return getBudgetAcademicStudents().size();
        return countOfBudgetAcademic;
    }

    public Integer getCountOfUnBudgetAcademic() {
        if (getUnBudgetAcademicStudents() != null)
            return getUnBudgetAcademicStudents().size();
        return countOfUnBudgetAcademic;
    }

    public Integer getCountOfBudgetMoveIn() {
        if (getBudgetMoveInStudents() != null)
            return getBudgetMoveInStudents().size();
        return countOfBudgetMoveIn;
    }

    public Integer getCountOfUnBudgetMoveIn() {
        if (getUnBudgetMoveInStudents() != null)
            return getUnBudgetMoveInStudents().size();
        return countOfUnBudgetMoveIn;
    }

    public Integer getCountOfBudgetMoveOut() {
        if (getBudgetMoveOutStudents() != null)
            return getBudgetMoveOutStudents().size();
        return countOfBudgetMoveOut;
    }


    public Integer getCountOfUnBudgetMoveOut() {
        if (getUnBudgetMoveOutStudents() != null)
            return getUnBudgetMoveOutStudents().size();
        return countOfUnBudgetMoveOut;
    }

    public Integer getCountOfUnBudgetAcademicMoveOut() {
        if (getBudgetAcademicMoveOutStudents() != null)
            return getBudgetAcademicMoveOutStudents().size();
        return countOfUnBudgetAcademicMoveOut;
    }

    public Integer getCountOfBudgetAcademicMoveOut() {
        if (getUnBudgetAcademicMoveOutStudents() != null)
            return getUnBudgetAcademicMoveOutStudents().size();
        return countOfBudgetAcademicMoveOut;
    }

    public Integer getCountOfBudgetAcademicMoveIn() {
        if (getBudgetAcademicMoveInStudents() != null)
            return getBudgetAcademicMoveInStudents().size();
        return countOfBudgetAcademicMoveIn;
    }

    public Integer getCountOfUnBudgetAcademicMoveIn() {
        if (getUnBudgetAcademicMoveInStudents() != null)
            return getUnBudgetAcademicMoveInStudents().size();
        return countOfUnBudgetAcademicMoveIn;
    }

}
