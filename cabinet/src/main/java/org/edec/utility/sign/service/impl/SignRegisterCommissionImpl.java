package org.edec.utility.sign.service.impl;

import org.apache.log4j.Logger;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.teacher.ctrl.WinCommissionCtrl;
import org.edec.teacher.model.commission.CommissionModel;
import org.edec.teacher.model.register.RegisterModel;
import org.edec.teacher.service.CompletionService;
import org.edec.teacher.service.impl.CompletionServiceImpl;
import org.edec.teacher.service.impl.RegisterServiceImpl;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.fileManager.FileModel;
import org.edec.utility.sign.service.SignService;


public class SignRegisterCommissionImpl implements SignService {
    private static final Logger log = Logger.getLogger(SignRegisterCommissionImpl.class.getName());

    private FileManager fileManager = new FileManager();
    private CompletionService completionService = new CompletionServiceImpl();
    private TemplatePageCtrl template = new TemplatePageCtrl();

    private CommissionModel commission;
    private FileModel fileModel;
    private WinCommissionCtrl winCommissionCtrl;
    private RegisterServiceImpl registerService = new RegisterServiceImpl();

    public SignRegisterCommissionImpl (FileModel fileModel, WinCommissionCtrl winCommissionCtrl, CommissionModel commission) {
        this.fileModel = fileModel;
        this.winCommissionCtrl = winCommissionCtrl;
        this.commission = commission;
    }

    @Override
    public boolean createFileAndUpdateUI (byte[] bytesFile, String serialNumber, String thumbPrint) {
        try {
            String pathFile = fileManager.createFile(fileModel, bytesFile);
            if (pathFile == null) {
                log.warn("Не удалось создать файл комиссионной ведомости " + commission.getIdReg());
                return false;
            }

            String relativePath = FileManager.getRelativePath(fileModel);

            if (relativePath.equals("")) {
                log.warn("Комиссионную ведомость " + commission.getIdReg() + " не удалось сохранить в БД");
                return false;
            }
            if (commission.getCertnumber() != null && commission.getCertnumber().equals("  ")) {
                if (completionService.updateRegisterAfterConfirmWithDigitalSign(
                        commission.getIdReg(),  serialNumber, thumbPrint)) {
                    log.info("Комиссионная ведомость " + commission.getIdReg() + " успешно подтверждена с ЭЦП и сохранена в БД");
                    winCommissionCtrl.init();
                    winCommissionCtrl.fill();
                    winCommissionCtrl.getUpdateIndex().run();
                    return true;
                }
            } else {
                if (completionService.updateRegisterAfterSign(
                        commission.getIdReg(), relativePath, serialNumber, thumbPrint, template.getCurrentUser().getFio(), null, null)) {
                    log.info("Комиссионная ведомость " + commission.getIdReg() + " успешно сохранена в БД");
                    winCommissionCtrl.init();
                    winCommissionCtrl.fill();
                    winCommissionCtrl.getUpdateIndex().run();
                    return true;
                }
            }
            log.warn("Комиссионную ведомость " + commission.getIdReg() + " не удалось сохранить в БД");
            return false;
        } catch (Exception e) {
            log.warn("Комиссионную ведомость " + commission.getIdReg() + " не удалось сохранить в БД");
            return false;
        }
    }

    @Override
    public RegisterModel getRegisterModel() {
        if (commission.getIdReg() != null) {
            return  registerService.getRegisterByIdRegister(commission.getIdReg());
        } else {
            return null;
        }
    }

    @Override
    public boolean hasSignRegister() {
        return registerService.isHasSign(template.getCurrentUser().getIdHum());
    }
}
