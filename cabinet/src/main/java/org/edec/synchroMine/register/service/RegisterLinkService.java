package org.edec.synchroMine.register.service;

import org.edec.commons.entity.dec.mine.LinkRegister;
import org.edec.synchroMine.register.dao.request.LinkRegisterDaoRequest;

import java.util.List;

public interface RegisterLinkService {

    List<LinkRegister> findLinkRegisterByRequest(LinkRegisterDaoRequest request);

    void saveSubjectnameMineForLinkRegister(LinkRegister linkRegister);
}
