package org.edec.utility.sign.service.impl;

import org.apache.log4j.Logger;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.teacher.model.register.RegisterModel;
import org.edec.teacher.service.CompletionService;
import org.edec.teacher.service.impl.CompletionServiceImpl;
import org.edec.teacher.service.impl.RegisterServiceImpl;
import org.edec.utility.constants.RegisterConst;
import org.edec.utility.constants.RegisterType;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.fileManager.FileModel;
import org.edec.utility.sign.service.SignService;

/**
 * Created by antonskripacev on 28.02.17.
 */
public class SignRegisterImpl implements SignService {
    private static final Logger log = Logger.getLogger(SignRegisterCommissionImpl.class.getName());

    private FileManager fileManager = new FileManager();
    private CompletionService completionService = new CompletionServiceImpl();
    private TemplatePageCtrl template = new TemplatePageCtrl();
    private RegisterServiceImpl registerService = new RegisterServiceImpl();


    private Long idRegister;
    private Long idInstitute;
    private Long idSemester;
    private RegisterType typeRegister;
    private Integer foc;
    private RegisterModel registerModel;

    private Runnable updateRegisterUI;

    public SignRegisterImpl(Runnable updateRegisterUI, Long idRegister, Long idInstitute, Long idSemester, RegisterType typeRegister, Integer foc) {
        this.updateRegisterUI = updateRegisterUI;
        this.idRegister = idRegister;
        this.idInstitute = idInstitute;
        this.idSemester = idSemester;
        this.typeRegister = typeRegister;
        this.foc = foc;
    }

    @Override
    public RegisterModel getRegisterModel() {
        if (idRegister != null) {
            registerModel =  registerService.getRegisterByIdRegister(idRegister);
            return registerModel;
        } else {
            return  null;
        }
    }

    @Override
    public boolean hasSignRegister() {
        return registerService.isHasSign(template.getCurrentUser().getIdHum());
    }

    @Override
    public boolean createFileAndUpdateUI(byte[] bytesFile, String serialNumber, String thumbPrint) {
        try {
            FileModel fileModel = new FileModel(
                    FileModel.Inst.getInstById(idInstitute),
                    FileModel.TypeDocument.REGISTER,
                    typeRegister.getSubTypeDocument(),
                    idSemester,
                    idRegister.toString()
            );
            fileModel.setFormat("pdf");

            String pathFile = fileManager.createFile(fileModel, bytesFile);
            if (pathFile == null) {
                log.warn("Не удалось создать файл ведомости " + idRegister);
                return false;
            }

            String relativePath = FileManager.getRelativePath(fileModel);

            if (registerModel.getCertNumber() != null && registerModel.getThumbPrint() != null
                    && registerModel.getCertNumber().equals("  ") && registerModel.getThumbPrint().equals("  ")) {
                if (completionService.updateRegisterAfterConfirmWithDigitalSign(idRegister, serialNumber, thumbPrint)) {
                    log.info("В ведомость " + idRegister + " успешно подтверждена с ЭЦП и сохранена в БД ");
                    updateRegisterUI.run();
                    return true;
                }
            } else {
                if (completionService.updateRegisterAfterSign(idRegister, relativePath, serialNumber, thumbPrint, template.getCurrentUser().getFio(), RegisterConst.STATUS_CONFIRMED, RegisterConst.STATUS_NEW)) {
                    log.info("Ведомость " + idRegister + " успешно сохранена в БД");
                    updateRegisterUI.run();
                    return true;
                }
            }

            // TODO подумать о транзакции

            log.warn("Ведомость " + idRegister + " не удалось сохранить в БД");
            return false;
        } catch (Exception e) {
            log.warn("Ведомость " + idRegister + " не удалось сохранить в БД");
            return false;
        }
    }
}
