package org.edec.utility.compare.compareResult;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value= ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface CompareField {
    CompareType compareType() default CompareType.EQUAL_TO;

    boolean required() default true;

    /**
     * @link #LogicalCondition.OR может быть установлено только у обязательных полей
     */
    LogicalCondition logicalCondition() default LogicalCondition.AND;
}
