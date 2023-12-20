package org.edec.order.service;

import org.edec.order.model.*;

import java.util.List;


public interface AddStudentService {
    public List<SearchGroupModel> getGroupBySemester (long idSemester);
    public List<SearchStudentModel> getStudentsForSearch (String surname, String name, String patronymic, String groupname, long idSemester,
                                                          long idOrder);
    public void addStudentInOrder (OrderModel orderModel, SectionModel section, List<StudentToAddModel> listToAdd);
    public List<SectionModel> getSectionsForSearch (long idOrder);
}
