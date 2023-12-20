package org.edec.synchroMine.register.dao.request;

import lombok.Data;

@Data
public class LinkRegisterDaoRequest {

    private boolean onlyUnlinked;
    private Integer course, semesterNumber;
    private Integer courseMine, semesterNumberMine;
    private String groupname = "", subjectnameCabinet = "", subjectnameMine = "";
}
