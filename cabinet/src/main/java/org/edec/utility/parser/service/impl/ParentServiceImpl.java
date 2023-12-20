package org.edec.utility.parser.service.impl;

import org.edec.synchroMine.model.eso.entity.Parent;
import org.edec.utility.parser.manager.ParseParentManager;
import org.edec.utility.parser.service.ParentService;

public class ParentServiceImpl implements ParentService {

    ParseParentManager ppm = new ParseParentManager();

    @Override
    public Long getOneStudentCardId(String studentF, String studentN, String studentP, String studentGroup) {
        return ppm.getOneStudentCardId(studentF, studentN, studentP, studentGroup);
    }

    @Override
    public boolean createParent(Parent parent) {
        return ppm.createParent(parent);
    }
}
