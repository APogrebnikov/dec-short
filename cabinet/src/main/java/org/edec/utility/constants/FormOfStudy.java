package org.edec.utility.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Getter
@AllArgsConstructor
public enum FormOfStudy {
    ALL("Все", "Все", 3, Arrays.asList(1, 2, 3)),
    FULL_TIME("Очное отделение", "Очное", 1, Collections.singletonList(1)),
    EXTRAMURAL("Заочное(очно-заочное) отделение", "Заочное", 2, Arrays.asList(2,3));

    private String name;
    private String shortName;
    private int type;
    private List<Integer> mineTypes;

    public static FormOfStudy getFormOfStudyByType (int type) {
        return Arrays.stream(FormOfStudy.values()).filter(formOfStudy -> formOfStudy.getType() == type).findFirst().orElse(null);
    }

    public static String getNameByType (int type) {
        FormOfStudy result = Arrays.stream(FormOfStudy.values())
                                   .filter(formOfStudy -> formOfStudy.getType() == type)
                                   .findFirst()
                                   .orElse(null);
        return result == null ? null : result.getName();
    }

    public static int getTypeByName (String name) {
        FormOfStudy result = Arrays.stream(FormOfStudy.values())
                                   .filter(formOfStudy -> formOfStudy.getName().equals(name))
                                   .findFirst()
                                   .orElse(null);
        return result == null ? 0 : result.getType();
    }

    public static FormOfStudy getFormOfStudyByName (String name) {
        for (FormOfStudy formOfStudy : FormOfStudy.values()) {
            if (formOfStudy.getName().equals(name)) {
                return formOfStudy;
            }
        }
        return null;
    }

}