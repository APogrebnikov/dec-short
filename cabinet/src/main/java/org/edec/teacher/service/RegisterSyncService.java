package org.edec.teacher.service;

public interface RegisterSyncService {
    boolean syncRegister(Long idRegister);
    boolean isRegisterAlreadySync(Long idRegister);
}
