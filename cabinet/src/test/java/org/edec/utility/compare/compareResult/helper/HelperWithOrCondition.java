package org.edec.utility.compare.compareResult.helper;

import lombok.Getter;
import org.edec.utility.compare.compareResult.CompareResult;
import org.edec.utility.compare.compareResult.CompareResultStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class HelperWithOrCondition {

    private List<CompareResult<ModelWithAnnotatedFieldsWithOrCondition>> compareResults;
    private List<ModelWithAnnotatedFieldsWithOrCondition> compareList, linkedList;

    public HelperWithOrCondition() {
        fillCompareList();
        fillLinkedList();
        fillCompareResults();
    }

    private void fillCompareList() {

        compareList = new ArrayList<>();
        compareList.addAll(Arrays.asList(
                createModel("КИ12-18Б", 1L, 2),
                createModel("КИ12-17Б", 2L, 2),
                createModel("КИ13-17Б", 3L, 1)
        ));
    }

    private void fillLinkedList() {
        linkedList = new ArrayList<>();
        linkedList.addAll(Arrays.asList(
                createModel("КИ12-18Б", 1L, 2),
                createModel("КИ12-17Б", 22L, 2),
                createModel("КИ14-17Б", 4L, 0)
        ));
    }

    private void fillCompareResults() {
        compareResults = new ArrayList<>();
        compareResults.addAll(Arrays.asList(
                generateFullEqualsObjects("КИ12-18Б", 1L, 2),
                new CompareResult<>(
                        createModel("КИ12-17Б", 2L, 2),
                        createModel("КИ12-17Б", 22L, 2),
                        CompareResultStatus.PARTLY_EQUAL
                ),
                new CompareResult<>(
                        createModel("КИ13-17Б", 3L, 1),
                        null,
                        CompareResultStatus.NOT_EQUAL
                ),
                new CompareResult<>(
                        null,
                        createModel("КИ14-17Б", 4L, 0),
                        CompareResultStatus.NOT_EQUAL
                )
        ));
    }

    private CompareResult<ModelWithAnnotatedFieldsWithOrCondition> generateFullEqualsObjects(String groupname, Long idGroup, Integer course) {
        return new CompareResult<>(
                createModel(groupname, idGroup, course),
                createModel(groupname, idGroup, course),
                CompareResultStatus.FULL_EQUAL);
    }

    private ModelWithAnnotatedFieldsWithOrCondition createModel(String groupname, Long idGroup, Integer course) {
        return ModelWithAnnotatedFieldsWithOrCondition.builder()
                .groupname(groupname).idGroup(idGroup).course(course)
                .build();
    }
}
