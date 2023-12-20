package org.edec.utility.parser.service;

import org.edec.synchroMine.model.eso.entity.Parent;

public interface ParentService {
    Long getOneStudentCardId(String studentF, String studentN, String studentP, String studentGroup);
    boolean createParent(Parent parent);
}
