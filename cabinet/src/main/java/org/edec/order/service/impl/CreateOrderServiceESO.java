package org.edec.order.service.impl;

import org.edec.manager.EntityManagerGroupsESO;
import org.edec.model.GroupModel;
import org.edec.order.manager.CreateOrderManagerESO;
import org.edec.order.manager.EntityManagerOrderESO;
import org.edec.order.model.*;
import org.edec.order.model.SemesterModel;
import org.edec.order.service.CreateOrderService;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.fileManager.FileModel;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.util.media.Media;

import java.util.Date;
import java.util.List;


public class CreateOrderServiceESO implements CreateOrderService{

    private EntityManagerOrderESO namagerEntity = new EntityManagerOrderESO();
    private EntityManagerGroupsESO managerGroupsESO = new EntityManagerGroupsESO();
    private CreateOrderManagerESO managerCreate = new CreateOrderManagerESO();

    public List<OrderRuleModel> getOrderRulesByInstitute(long idInstitute) {
        return managerCreate.getListOrderRule(idInstitute);
    }

    @Override
    public OrderModel getCreatedOrderById(Long id) {
        return namagerEntity.getOrderById(id);
    }

    @Override
    public String createOrderByParams(InstituteModel inst, FileModel.SubTypeDocument subTypeDocument, Long sem, String name, List<Media> attached) {
        FileModel fileModel = new FileModel(
                FileModel.Inst.getInstById(inst.getIdInst()),
                FileModel.TypeDocument.ORDER,
                subTypeDocument,
                sem, name);

        FileManager fileHelper = new FileManager();
        String result = fileHelper.createFolder(fileModel, true, true, attached);

        boolean isSuccesTransfer = true;
        if (result != null && !result.equals("")) {
            if (subTypeDocument.equals(FileModel.SubTypeDocument.SOCIAL)) {
                List<StudentWithReference> listStudents = this.getStudentsWithReferenceFromSocialOrder(Long.parseLong(name));

                for (StudentWithReference student : listStudents)
                {
                    if (student.getIdReference() == null)
                        continue;

                    boolean bResult = fileHelper.transferFilesFromReferenceToAttached(fileModel,
                            student.getFamily() + " " + student.getName() + " " + student.getPatronymic(),
                            student.getUrl());

                    if(isSuccesTransfer && !bResult) {
                        isSuccesTransfer = false;
                    }
                }
            }

            if (subTypeDocument.equals(FileModel.SubTypeDocument.SOCIAL_INCREASE))
            {
                List<StudentWithReference> listStudents = this.getStudentsWithReferenceFromSocialIncreasedOrder(Long.parseLong(name));

                for (StudentWithReference student : listStudents)
                {
                    if (student.getIdReference() == null)
                        continue;

                    boolean bResult = fileHelper.transferFilesFromReferenceToAttached(fileModel,
                            student.getFamily() + " " + student.getName() + " " + student.getPatronymic(),
                            student.getUrl());

                    if(isSuccesTransfer && !bResult) {
                        isSuccesTransfer = false;
                    }
                }
            }

            if(!isSuccesTransfer) {
                PopupUtil.showError("Возникли проблемы с переносом файлов справок. Обратитесь к администратору");
            }

            return FileManager.getRelativePath(fileModel);
        }

        return null;
    }

    @Override
    public Long getNeededSemester(Long curSemester) {
        int month = new Date().getMonth() + 1;

        //В феврале, июле делается приказ в сессию, поэтому нужен именно текущий семестр
        if(month == 2 || month == 7) {
            return curSemester;
        }

        //в остальные месяцы берем предыдущий месяц
        return managerCreate.getPrevSemester(curSemester);
    }

    @Override
    public Long getNeededSemester(Long curSemester, Boolean inSession) {
        return inSession ? curSemester : managerCreate.getPrevSemester(curSemester);
    }

    @Override
    public OrderModel updateOrder(OrderModel orderModel)
    {
        if (managerCreate.updateOrderModel(orderModel))
            return namagerEntity.getOrderById(orderModel.getIdOrder());
        return null;
    }

    @Override
    public List<SearchStudentModel> getStudentsBySurnameAndSemester(String surname, Long idSemester) {
        return new EntityManagerOrderESO().getStudentsByFilter(surname, idSemester);
    }

    @Override
    public List<SemesterModel> getSemesterByInstitute(Long idInsitute, Integer formOfStudy) {
        return managerCreate.getSemesterByInstitute(idInsitute, formOfStudy);
    }

    @Override
    public Long getCurrentSemester(long institute, int formOfControl) {
        return managerCreate.getCurrentSemester(institute, formOfControl);
    }

    @Override
    public List<StudentWithReference> getStudentsWithReferenceFromSocialOrder(Long idOrder) {
        return managerCreate.getStudentsWithReferenceFromSocialOrder(idOrder);
    }

    @Override
    public List<StudentWithReference> getStudentsWithReferenceFromSocialOrder(Long idOrder, List<StudentToAddModel> listToAdd) {
        return managerCreate.getStudentsWithReferenceFromSocialOrder(idOrder, listToAdd);
    }

    @Override
    public List<StudentWithReference> getStudentsWithReferenceFromSocialIncreasedOrder(Long idOrder) {
        return managerCreate.getStudentsWithReferenceFromSocialIncreasedOrder(idOrder);
    }

    @Override
    public List<StudentWithReference> getStudentsWithReferenceFromSocialIncreasedOrder(Long idOrder, List<StudentToAddModel> listToAdd) {
        return managerCreate.getStudentsWithReferenceFromSocialIncreasedOrder(idOrder, listToAdd);
    }

    @Override
    public void changeSectionForStudentInTransfer(Long idLgss, Date dateProlongation) {
        managerCreate.changeSectionForStudentInTransfer(idLgss, dateProlongation);
    }

    @Override
    public void updateFirstDateStudent(Long idLgss, Date dateProlongation) {
        managerCreate.updateFirstDateStudent(idLgss, dateProlongation);
    }

    @Override
    public List<GroupModel> getGroupsBySemester(Long idSemester) {
        return managerGroupsESO.getGroupsBySemester(idSemester);
    }
}
