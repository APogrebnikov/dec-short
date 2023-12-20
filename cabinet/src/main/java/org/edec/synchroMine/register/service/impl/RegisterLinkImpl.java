package org.edec.synchroMine.register.service.impl;

import org.edec.commons.entity.dec.mine.LinkRegister;
import org.edec.synchroMine.register.dao.LinkRegisterDao;
import org.edec.synchroMine.register.dao.request.LinkRegisterDaoRequest;
import org.edec.synchroMine.register.service.RegisterLinkService;

import java.util.List;

public class RegisterLinkImpl implements RegisterLinkService {

    private LinkRegisterDao linkRegisterDao = new LinkRegisterDao();

    @Override
    public List<LinkRegister> findLinkRegisterByRequest(LinkRegisterDaoRequest request) {
        return linkRegisterDao.findLinkRegistersByRequest(request);
    }

    @Override
    public void saveSubjectnameMineForLinkRegister(LinkRegister linkRegister) {
        linkRegisterDao.saveOrUpdate(linkRegister);
    }
}
