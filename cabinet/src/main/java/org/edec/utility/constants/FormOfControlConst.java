package org.edec.utility.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter @AllArgsConstructor
public enum FormOfControlConst {

    EXAM(1, "Экзамен"), PASS(2, "Зачет"),
    CP(3, "КП"), CW(4, "КР"),
    PRACTIC(5, "Практика");

    private Integer value;
    private String name;

    public static FormOfControlConst getName(Integer value) {
        return Stream.of(FormOfControlConst.values())
                .filter(foc -> foc.getValue().equals(value))
                .findFirst().orElse(null);
    }

    public static FormOfControlConst getValue(String name) {
        return Stream.of(FormOfControlConst.values())
                .filter(foc -> foc.getName().equals(name))
                .findFirst().orElse(null);
    }

    @Override
    @JsonValue
    public String toString() {
        return this.name;
    }

    private final static Map<String, FormOfControlConst> enums = Arrays.stream(FormOfControlConst.values())
            .collect(Collectors.toMap(
                    Enum::toString,
                    Function.identity()));

    @JsonCreator
    public static FormOfControlConst fromJson(String value) {
        return enums.get(value);
    }
}
