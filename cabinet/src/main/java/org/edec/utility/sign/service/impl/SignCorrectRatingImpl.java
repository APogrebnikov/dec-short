package org.edec.utility.sign.service.impl;

import org.apache.log4j.Logger;
import org.edec.teacher.model.correctRequest.CorrectRequestModel;
import org.edec.teacher.model.register.RegisterModel;
import org.edec.teacher.service.RegisterRequestService;
import org.edec.teacher.service.impl.RegisterRequestServiceImpl;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.fileManager.FileModel;
import org.edec.utility.sign.service.SignService;

import java.util.Date;

/**
 * Created by apogrebnikov.
 */
public class SignCorrectRatingImpl implements SignService {
    private static final Logger log = Logger.getLogger(SignCorrectRatingImpl.class.getName());

    private FileManager fileManager = new FileManager();

    private RegisterRequestService registerRequestService = new RegisterRequestServiceImpl();

    private Runnable updateRegisterUI;
    private CorrectRequestModel correctRequestModel;
    private Long idInstitute;
    private Long idSemester;

    public SignCorrectRatingImpl(CorrectRequestModel correctRequestModel, Long idInstitute, Long idSemester) {
        this.correctRequestModel = correctRequestModel;
        this.idInstitute = idInstitute;
        this.idSemester = idSemester;
    }

    @Override
    public boolean createFileAndUpdateUI(byte[] bytesFile, String serialNumber, String thumbPrint) {
        try {
            FileModel fileModel = new FileModel(
                    FileModel.Inst.getInstById(idInstitute),
                    FileModel.TypeDocument.MEMORANDUM,
                    FileModel.SubTypeDocument.CORRECT_RATING,
                    idSemester,
                    correctRequestModel.getId().toString()
            );
            fileModel.setFormat("pdf");

            String pathFile = fileManager.createFile(fileModel, bytesFile);
            if (pathFile == null) {
                log.warn("Не удалось создать файл служебной " + correctRequestModel.getId());
                return false;
            }

            String relativePath = FileManager.getRelativePath(fileModel);

            correctRequestModel.setFilePath(relativePath);
            correctRequestModel.setThumbprint(thumbPrint);
            correctRequestModel.setCertnumber(serialNumber);
            correctRequestModel.setDateOfApplying(new Date());

            if (registerRequestService.updateCorrectAfterSign(correctRequestModel)) {
                log.info("Служебная записка " + correctRequestModel.getId() + " успешно сохранена в БД");
                // updateRegisterUI.run();
                return true;
            }


            log.warn("Служебку " + correctRequestModel.getId() + " не удалось сохранить в БД");
            return false;
        } catch (Exception e) {
            log.warn("Ошибочкос");
            return false;
        }
    }

    @Override
    public boolean hasSignRegister() {
        return false;
    }

    @Override
    public RegisterModel getRegisterModel() {
        return null;
    }
}
