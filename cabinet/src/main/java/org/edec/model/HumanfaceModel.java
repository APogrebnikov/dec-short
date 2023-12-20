package org.edec.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter @NoArgsConstructor
public class HumanfaceModel {

    private Long idHum;
    private String family, name, patronymic;
    private String email;
    private Integer sex;

    public String getShortFio() {
        if (family == null) {
            return getFio();
        }

        return String.format(
                "%s %s%s",
                family,
                isNonEmptyText(name) ? name.substring(0, 1) + ". " : "",
                isNonEmptyText(patronymic) ? patronymic.substring(0, 1) + "." : ""
        );
    }

    public String getFio() {
        return (family == null ? "" : family) + " "
                + (name == null ? "" : name) + " "
                + (patronymic == null ? "" : patronymic);
    }

    private Boolean isNonEmptyText(String text) {
        return text != null && !text.equals("");
    }

    public String getShortFioInverse() {
        return String.format(
                "%s%s %s",
                isNonEmptyText(name) ? name.substring(0, 1) + ". " : "",
                isNonEmptyText(patronymic) ? (patronymic.substring(0, 1) + ".") : "",
                family);
    }
}