package org.edec.order.service;

import org.edec.model.*;
import org.edec.order.model.*;
import org.edec.order.model.SemesterModel;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.fileManager.FileModel;
import org.zkoss.util.media.Media;

import java.util.Date;
import java.util.List;


public interface CreateOrderService {
    List<OrderRuleModel> getOrderRulesByInstitute (long idInstitute);
    Long getCurrentSemester (long institute, int formOfControl);
    OrderModel getCreatedOrderById (Long id);
    String createOrderByParams (InstituteModel inst, FileModel.SubTypeDocument subTypeDocument, Long sem, String name,
                                List<Media> attached);
    List<StudentWithReference> getStudentsWithReferenceFromSocialOrder (Long idOrder);
    List<StudentWithReference> getStudentsWithReferenceFromSocialOrder (Long idOrder, List<StudentToAddModel> studentToAddModels);
    List<StudentWithReference> getStudentsWithReferenceFromSocialIncreasedOrder (Long idOrder);
    List<StudentWithReference> getStudentsWithReferenceFromSocialIncreasedOrder (Long idOrder, List<StudentToAddModel> studentToAddModels);
    OrderModel updateOrder (OrderModel orderModel);
    List<SemesterModel> getSemesterByInstitute (Long idInsitute, Integer formOfStudy);
    List<SearchStudentModel> getStudentsBySurnameAndSemester (String surname, Long idSemester);
    /*
    * в этой функции выясняется: брать для приказа текущий семестр, или нужен предыдущий для стипендиальных приказов
     */
    Long getNeededSemester (Long curSemester);
    List<org.edec.model.GroupModel> getGroupsBySemester (Long idSemester);
    Long getNeededSemester (Long curSemester, Boolean inSession);
    /**
     * Переводим студента в соответствующий пункт по уважительной причине
     **/
    void changeSectionForStudentInTransfer (Long idLgss, Date dateProlongation);
    void updateFirstDateStudent (Long idLgss, Date firstDate);
}
