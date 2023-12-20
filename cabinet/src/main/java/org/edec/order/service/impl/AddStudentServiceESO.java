package org.edec.order.service.impl;

import org.edec.newOrder.manager.EditOrderManagerESO;
import org.edec.order.manager.EntityManagerOrderESO;
import org.edec.order.model.*;
import org.edec.order.model.dao.SearchGroupModelESO;
import org.edec.order.model.dao.SearchStudentModelESO;
import org.edec.order.model.dao.SectionModelEso;
import org.edec.order.service.AddStudentService;
import org.edec.utility.constants.OrderTypeConst;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.fileManager.FileModel;

import java.util.ArrayList;
import java.util.List;


public class AddStudentServiceESO implements AddStudentService {
    EntityManagerOrderESO manager = new EntityManagerOrderESO();
    EditOrderManagerESO editManager = new EditOrderManagerESO();

    @Override
    public List<SearchGroupModel> getGroupBySemester (long idSemester) {
        return transformGroupESOToModel(manager.getGroupsBySemester(idSemester));
    }

    @Override
    public List<SearchStudentModel> getStudentsForSearch (String surname, String name, String patronymic, String groupname, long idSemester,
                                                          long idOrder) {
        return transformStudentESOToModel(manager.getStudentsByFilterNotInOrder(surname, name, patronymic, groupname, idSemester, idOrder));
    }

    @Override
    public List<SectionModel> getSectionsForSearch (long idOrder) {
        return transformSectionESOToModel(manager.getSectionsByIdOrder(idOrder));
    }

    @Override
    public void addStudentInOrder (OrderModel orderModel, SectionModel section, List<StudentToAddModel> listToAdd) {
        manager.insertLinkOrderStudentStatus(section, listToAdd);

        if (orderModel.getOrderType().equals(OrderTypeConst.SOCIAL.getType()) ||
            orderModel.getOrderType().equals(OrderTypeConst.SOCIAL_INCREASED.getType())) {
            FileModel fileModel = new FileModel(FileModel.Inst.getInstById(1L), FileModel.TypeDocument.ORDER,
                                                orderModel.getOrderType().equals(OrderTypeConst.SOCIAL.getType())
                                                ? FileModel.SubTypeDocument.SOCIAL
                                                : FileModel.SubTypeDocument.SOCIAL_INCREASE, orderModel.getIdSemester(),
                                                Long.toString(orderModel.getIdOrder())
            );

            FileManager fileHelper = new FileManager();

            List<StudentWithReference> listStudents = orderModel.getOrderType().equals(OrderTypeConst.SOCIAL.getType())
                                                      ? new CreateOrderServiceESO().getStudentsWithReferenceFromSocialOrder(
                    orderModel.getIdOrder(), listToAdd)
                                                      : new CreateOrderServiceESO().getStudentsWithReferenceFromSocialIncreasedOrder(
                                                              orderModel.getIdOrder(), listToAdd);

            for (StudentWithReference student : listStudents) {
                if (student.getIdReference() == null) {
                    continue;
                }

                boolean bResult = fileHelper.transferFilesFromReferenceToAttached(fileModel,
                                                                                  student.getFamily() + " " + student.getName() + " " +
                                                                                  student.getPatronymic(), student.getUrl()
                );
            }
        }

        // Очищаем предпросмотр от предзаготовленных строк
        if (orderModel.getIdOrder() != null) {
            editManager.deleteOrderLines(orderModel.getIdOrder());
        }
    }

    public List<SearchGroupModel> transformGroupESOToModel (List<SearchGroupModelESO> list) {
        List<SearchGroupModel> newList = new ArrayList<>();

        for (SearchGroupModelESO item : list) {
            SearchGroupModel newItem = new SearchGroupModel();

            newItem.setId(item.getId());
            newItem.setName(item.getName());

            newList.add(newItem);
        }

        return newList;
    }

    public List<SearchStudentModel> transformStudentESOToModel (List<SearchStudentModelESO> list) {
        List<SearchStudentModel> newList = new ArrayList<>();

        for (SearchStudentModelESO item : list) {
            SearchStudentModel newItem = new SearchStudentModel();

            newItem.setId(item.getIdSSS());
            newItem.setName(item.getName());
            newItem.setSurname(item.getSurname());
            newItem.setPatronymic(item.getPatronymic());
            newItem.setGroupname(item.getGroupname());

            newList.add(newItem);
        }

        return newList;
    }

    public List<SectionModel> transformSectionESOToModel (List<SectionModelEso> list) {
        List<SectionModel> newList = new ArrayList<>();

        for (SectionModelEso item : list) {
            SectionModel newItem = new SectionModel();

            newItem.setId(item.getIdLOS());
            newItem.setName(item.getName());
            newItem.setFirstDate(item.getFirstDate());
            newItem.setSecondDate(item.getSecondDate());

            newList.add(newItem);
        }

        return newList;
    }
}
