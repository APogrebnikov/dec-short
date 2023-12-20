package org.edec.utility.sign.service;


import org.edec.teacher.model.register.RegisterModel;

public interface SignService {
    boolean createFileAndUpdateUI (byte[] bytesFile, String serialNumber, String thumbPrint);
    boolean hasSignRegister();
    RegisterModel getRegisterModel();
}
