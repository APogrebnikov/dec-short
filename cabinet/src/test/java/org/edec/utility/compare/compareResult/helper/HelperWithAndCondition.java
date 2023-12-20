package org.edec.utility.compare.compareResult.helper;

import lombok.Getter;
import org.edec.utility.compare.compareResult.CompareResult;
import org.edec.utility.compare.compareResult.CompareResultStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class HelperWithAndCondition {
    private List<CompareResult<ModelWithAnnotatedFields>> compareResults;
    private List<ModelWithAnnotatedFields> compareList, linkedList;

    public HelperWithAndCondition() {
        fillCompareList();
        fillLinkedList();
        fillCompareResults();
    }

    private void fillCompareList() {

        compareList = new ArrayList<>();
        compareList.addAll(Arrays.asList(
                createModelWithAnnotated("КИ12-18Б", 1L, 2),
                createModelWithAnnotated("КИ13-17Б", 2L, 1),
                createModelWithAnnotated("КИ12-17Б", 3L, 2),
                createModelWithAnnotated("КИ13-18Б", 4L, 1),
                createModelWithAnnotated("КИ12-16Б", 5L, 2),
                createModelWithAnnotated("КИ13-16Б", 6L, 1)
        ));
    }

    private void fillLinkedList() {

        linkedList = new ArrayList<>();
        linkedList.addAll(Arrays.asList(
                createModelWithAnnotated("КИ12-18Б", 1L, 2),
                createModelWithAnnotated("КИ13-17Б", 2L, 1),
                createModelWithAnnotated("КИ12-17Б", 33L, 3),
                createModelWithAnnotated("КИ13-18Б", 44L, 4),
                createModelWithAnnotated("КИ12-16Б", 5L, 3),
                createModelWithAnnotated("КИ13-16Б", 6L, 6)
        ));
    }

    private void fillCompareResults() {

        compareResults = new ArrayList<>();
        compareResults.addAll(Arrays.asList(
                generateFullyEqualsObjects("КИ12-18Б", 1L, 2),
                generateFullyEqualsObjects("КИ13-17Б", 2L, 1),
                generatePartlyEqualsObjects("КИ12-16Б", 5L, 2, 3),
                generatePartlyEqualsObjects("КИ13-16Б", 6L, 1, 6),
                generateNoneEqualsObjectsWithNullLinkedObject("КИ12-17Б", 3L, 2),
                generateNoneEqualsObjectsWithNullLinkedObject("КИ13-18Б", 4L, 1),
                generateNoneEqualsObjectsWithNullCompareObject("КИ12-17Б", 33L, 3),
                generateNoneEqualsObjectsWithNullCompareObject("КИ13-18Б", 44L, 4)
        ));
    }

    private CompareResult<ModelWithAnnotatedFields> generateFullyEqualsObjects(String groupname, Long idGroup, Integer course) {
        return new CompareResult<>(
                createModelWithAnnotated(groupname, idGroup, course),
                createModelWithAnnotated(groupname, idGroup, course),
                CompareResultStatus.FULL_EQUAL);
    }

    private CompareResult<ModelWithAnnotatedFields> generatePartlyEqualsObjects(String groupname, Long idGroup, Integer courseCompareObj, Integer courseLinkedObj) {
        return new CompareResult<>(
                createModelWithAnnotated(groupname, idGroup, courseCompareObj),
                createModelWithAnnotated(groupname, idGroup, courseLinkedObj),
                CompareResultStatus.PARTLY_EQUAL);
    }

    private CompareResult<ModelWithAnnotatedFields> generateNoneEqualsObjectsWithNullLinkedObject(String groupname, Long idGroup, Integer course) {
        return new CompareResult<>(
                createModelWithAnnotated(groupname, idGroup, course),
                null,
                CompareResultStatus.NOT_EQUAL);
    }

    private CompareResult<ModelWithAnnotatedFields> generateNoneEqualsObjectsWithNullCompareObject(String groupname, Long idGroup, Integer course) {
        return new CompareResult<>(
                null,
                createModelWithAnnotated(groupname, idGroup, course),
                CompareResultStatus.NOT_EQUAL);
    }

    private ModelWithAnnotatedFields createModelWithAnnotated(String groupname, Long idGroup, Integer course) {
        return ModelWithAnnotatedFields.builder()
                .groupname(groupname).idGroup(idGroup).course(course)
                .build();
    }
}
