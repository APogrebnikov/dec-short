package org.edec.utility.compare.compareResult;

import lombok.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CompareResultForTwoList<T> {
    private Class<T> compareClass;
    private List<Field> requiredFieldsWithOrCondition;
    private List<Field> requiredFieldsWithAndCondition;
    private List<Field> optionalFields;

    public CompareResultForTwoList(@NonNull Class<T> compareClass) {

        this.compareClass = compareClass;
        requiredFieldsWithOrCondition = new ArrayList<>();
        requiredFieldsWithAndCondition = new ArrayList<>();
        optionalFields = new ArrayList<>();
        findAnnotatedFieldsByCompareFieldClass();
        if (requiredFieldsWithOrCondition.size() == 0 && requiredFieldsWithAndCondition.size() == 0) {
            throw new IllegalArgumentException("Должно быть хотя бы одно поле обязательным у класса " + compareClass.getSimpleName());
        }
    }

    private void findAnnotatedFieldsByCompareFieldClass() {

        Stream.of(compareClass.getDeclaredFields())
                .forEach(this::checkFieldOnPresentAnnotation);
    }

    private void checkFieldOnPresentAnnotation(Field field) {

        if (!field.isAnnotationPresent(CompareField.class)) {
            return;
        }
        field.setAccessible(true);
        CompareField compareField = field.getAnnotation(CompareField.class);
        if (compareField.required()) {
            if (compareField.logicalCondition() == LogicalCondition.AND) {
                requiredFieldsWithAndCondition.add(field);
            } else {
                requiredFieldsWithOrCondition.add(field);
            }
        } else {
            optionalFields.add(field);
        }
    }

    public List<CompareResult<T>> compareTwoList(@NonNull List<T> compareList, @NonNull List<T> linkedList) {

        List<CompareResult<T>> compareResultList = new ArrayList<>();
        List<T> usedCompareObjects = new ArrayList<>();
        List<T> usedLinkedObjects = new ArrayList<>();
        for (T compareObj : compareList) {
            for (T linkedObj : linkedList) {
                CompareResultStatus compareResultStatus = getResultByComparingTwoObjects(compareObj, linkedObj);
                if (compareResultStatus == CompareResultStatus.NOT_EQUAL) {
                    continue;
                }
                usedCompareObjects.add(compareObj);
                usedLinkedObjects.add(linkedObj);
                compareResultList.add(new CompareResult<>(compareObj, linkedObj, compareResultStatus));
            }
        }
        compareList.removeAll(usedCompareObjects);
        for (T compareObj : compareList) {
            compareResultList.add(new CompareResult<>(compareObj, null, CompareResultStatus.NOT_EQUAL));
        }
        linkedList.removeAll(usedLinkedObjects);
        for (T linkedObj : linkedList) {
            compareResultList.add(new CompareResult<>(null, linkedObj, CompareResultStatus.NOT_EQUAL));
        }
        return compareResultList;
    }

    private CompareResultStatus getResultByComparingTwoObjects(T compareObj, T linkedObj) {

        CompareResultStatus checkByRequiredFields = checkByRequiredFields(compareObj, linkedObj);
        if (checkByRequiredFields == CompareResultStatus.NOT_EQUAL) {
            return CompareResultStatus.NOT_EQUAL;
        }
        if (!compareByOptionalFields(compareObj, linkedObj)) {
            return CompareResultStatus.PARTLY_EQUAL;
        }
        if (checkByRequiredFields == CompareResultStatus.PARTLY_EQUAL) {
            return CompareResultStatus.PARTLY_EQUAL;
        }
        return CompareResultStatus.FULL_EQUAL;
    }

    private CompareResultStatus checkByRequiredFields(T compareObj, T linkedObj) {

        if (requiredFieldsWithOrCondition.size() == 0) {
            return compareByRequiredFieldsWithAndCondition(compareObj, linkedObj) ? CompareResultStatus.FULL_EQUAL : CompareResultStatus.NOT_EQUAL;
        }
        if (requiredFieldsWithAndCondition.size() == 0) {
            return compareByFieldsWithOrCondition(compareObj, linkedObj);
        }
        boolean checkAllAndCondition = compareByRequiredFieldsWithAndCondition(compareObj, linkedObj);
        return checkAllAndCondition
                ? compareByFieldsWithOrCondition(compareObj, linkedObj)
                : CompareResultStatus.NOT_EQUAL;
    }

    private boolean compareByRequiredFieldsWithAndCondition(T compareObj, T linkedObj) {
        return compareByClassFields(compareObj, linkedObj, requiredFieldsWithAndCondition);
    }

    private boolean compareByOptionalFields(T compareObj, T linkedObj) {
        return compareByClassFields(compareObj, linkedObj, optionalFields);
    }

    private boolean compareByClassFields(T compareObj, T linkedObj, List<Field> compareFields) {
        return compareFields.stream()
                .allMatch(field -> {
                    try {
                        Object compareField = field.get(compareObj);
                        Object linkedField = field.get(linkedObj);
                        return checkByNotNullAndEquals(compareField, linkedField);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return false;
                    }
                });
    }

    private CompareResultStatus compareByFieldsWithOrCondition(T compareObj, T linkedObj) {

        boolean allFieldsNotEquals = true;
        boolean allFieldsEquals = true;
        for (Field requiredField : requiredFieldsWithOrCondition) {
            try {
                Object compareField = requiredField.get(compareObj);
                Object linkedField = requiredField.get(linkedObj);
                if (checkByNotNullAndEquals(compareField, linkedField)) {
                    allFieldsNotEquals = false;
                } else {
                    allFieldsEquals = false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return getCompareResultStatusByFieldsWithOrCondition(allFieldsNotEquals, allFieldsEquals);
    }

    private CompareResultStatus getCompareResultStatusByFieldsWithOrCondition(boolean allFieldsNotEquals, boolean allFieldsEquals) {
        if (allFieldsEquals) {
            return CompareResultStatus.FULL_EQUAL;
        } else if (allFieldsNotEquals) {
            return CompareResultStatus.NOT_EQUAL;
        } else {
            return CompareResultStatus.PARTLY_EQUAL;
        }
    }

    private boolean checkByNotNullAndEquals(Object compareField, Object linkedField) {
        return compareField != null && compareField.equals(linkedField);
    }
}
