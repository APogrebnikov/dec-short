package org.edec.utility.report.model.order.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class SocialEsoModel {
    private Integer course;

    private String description;
    private String fio;
    private String foundation;
    private String groupname;
    private String recordbook;
    private boolean sirota, indigent, invalid;
}
