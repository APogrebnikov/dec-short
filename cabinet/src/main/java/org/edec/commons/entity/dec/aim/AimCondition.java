package org.edec.commons.entity.dec.aim;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

/**
 * @author Max Dimukhametov
 */
@Getter
@Setter
@NoArgsConstructor
/*@Entity
@Table(name = "aim_condition")*/ public class AimCondition {
    @Id
    @Column(name = "id_aim_condition")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "value_condition")
    private String valueCondition;

    @ManyToOne
    @JoinColumn(name = "id_aim")
    private Aim aim;
    @ManyToOne
    @JoinColumn(name = "id_aim_condition_type")
    private AimConditionType aimConditionType;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type_condition")
    private TypeCondition typeCondition;

    @Getter
    @AllArgsConstructor
    public enum TypeCondition {
        EQ("=", "равно"), EQ_MORE(">=", "больше либо равно"), EQ_LESS("<=", "меньше либо равно"), MORE(">", "больше"), LESS(
                "<", "меньше"), IN("in (?)", "входит в множество"), NOT_IN("not in (?)", "не входит в множество");

        private String condition;
        private String description;
    }

    public boolean checkCondition (String argument) {
        String tmpValueCondition = valueCondition;
        if (argument == null || valueCondition == null || typeCondition == null || aimConditionType == null ||
            aimConditionType.getTypeValue() == null) {
            return false;
        }
        if (aimConditionType.getTypeValue() == AimConditionType.TypeValue.BOOLEAN) {
            if (tmpValueCondition.toLowerCase().equals("да")) {
                tmpValueCondition = "true";
            } else if (tmpValueCondition.toLowerCase().equals("нет")) {
                tmpValueCondition = "false";
            }
        }
        try {
            switch (typeCondition) {
                case EQ:
                    return tmpValueCondition.equals(argument);
                case EQ_MORE:
                    switch (aimConditionType.getTypeValue()) {
                        case INT:
                            return Integer.valueOf(argument) >= Integer.valueOf(tmpValueCondition);
                        case FLOAT:
                            return Float.valueOf(argument) >= Integer.valueOf(tmpValueCondition);
                        default:
                            throw new IllegalArgumentException("Неверный тип для сравнения!");
                    }
                case EQ_LESS:
                    switch (aimConditionType.getTypeValue()) {
                        case INT:
                            return Integer.valueOf(argument) <= Integer.valueOf(tmpValueCondition);
                        case FLOAT:
                            return Float.valueOf(argument) <= Integer.valueOf(tmpValueCondition);
                        default:
                            throw new IllegalArgumentException("Неверный тип для сравнения!");
                    }
                case MORE:
                    switch (aimConditionType.getTypeValue()) {
                        case INT:
                            return Integer.valueOf(argument) > Integer.valueOf(tmpValueCondition);
                        case FLOAT:
                            return Float.valueOf(argument) > Integer.valueOf(tmpValueCondition);
                        default:
                            throw new IllegalArgumentException("Неверный тип для сравнения!");
                    }
                case LESS:
                    switch (aimConditionType.getTypeValue()) {
                        case INT:
                            return Integer.valueOf(argument) < Integer.valueOf(tmpValueCondition);
                        case FLOAT:
                            return Float.valueOf(argument) < Integer.valueOf(tmpValueCondition);
                        default:
                            throw new IllegalArgumentException("Неверный тип для сравнения!");
                    }
                case IN:
                    throw new IllegalArgumentException("Не реализовано!");
                case NOT_IN:
                    throw new IllegalArgumentException("Не реализовано!");
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
