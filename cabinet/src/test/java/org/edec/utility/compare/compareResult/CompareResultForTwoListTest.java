package org.edec.utility.compare.compareResult;

import org.edec.utility.compare.compareResult.helper.HelperWithAndCondition;
import org.edec.utility.compare.compareResult.helper.HelperWithOrCondition;
import org.edec.utility.compare.compareResult.helper.ModelWithAnnotatedFields;
import org.edec.utility.compare.compareResult.helper.ModelWithAnnotatedFieldsWithOrCondition;
import org.edec.utility.compare.compareResult.helper.ModelWithoutAnnotatedFields;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompareResultForTwoListTest {

    @Test
    @DisplayName("Positive check model without annotated fields")
    void check_model_without_annotated_fields() {
        assertThrows(IllegalArgumentException.class, () -> new CompareResultForTwoList<>(ModelWithoutAnnotatedFields.class));
    }

    @Test
    @DisplayName("Positive check model with annotated fields")
    void check_model_with_annotated_fields() {
        new CompareResultForTwoList<>(ModelWithAnnotatedFields.class);
    }

    @Test
    @DisplayName("Positive test on check result from call method with testing AND condition")
    void test_compare_result_with_result_on_call_method() {

        HelperWithAndCondition compareResultHelper = new HelperWithAndCondition();
        CompareResultForTwoList<ModelWithAnnotatedFields> compareResultForTwoList = new CompareResultForTwoList<>(ModelWithAnnotatedFields.class);

        List<CompareResult<ModelWithAnnotatedFields>> compareResults = compareResultForTwoList.compareTwoList(
                compareResultHelper.getCompareList(),
                compareResultHelper.getLinkedList());

        assertIterableEquals(compareResultHelper.getCompareResults(), compareResults);
    }

    @Test
    @DisplayName("Positive test on check result from call method with testing OR condition")
    public void test_compare_result_with_result_on_call_method_with_or_condition() {

        HelperWithOrCondition compareResultHelper = new HelperWithOrCondition();
        CompareResultForTwoList<ModelWithAnnotatedFieldsWithOrCondition> compareResultForTwoList
                = new CompareResultForTwoList<>(ModelWithAnnotatedFieldsWithOrCondition.class);

        List<CompareResult<ModelWithAnnotatedFieldsWithOrCondition>> compareResults = compareResultForTwoList.compareTwoList(
                compareResultHelper.getCompareList(),
                compareResultHelper.getLinkedList()
        );

        assertIterableEquals(compareResultHelper.getCompareResults(), compareResults);
    }
}