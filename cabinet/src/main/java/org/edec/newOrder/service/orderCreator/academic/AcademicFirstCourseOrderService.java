package org.edec.newOrder.service.orderCreator.academic;

import org.edec.newOrder.model.ScholarshipModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.service.esoImpl.CreateOrderServiceESO;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.zkoss.util.media.Media;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AcademicFirstCourseOrderService extends OrderService {

    @Override
    protected void generateParamModel() {
        groupingInEditEnum = GroupingInEditEnum.BY_GROUP;

        orderParams.add(new OrderCreateParamModel("Назначить с", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Назначить по", ComponentEnum.DATEBOX, true));

        orderParams.add(new OrderCreateParamModel("Список студентов", ComponentEnum.FILE, true));

        isFilesNeeded = true;
    }

    @Override
    protected void generateDocumentModel() {

    }

    @Override
    public boolean createAndAttachOrderDocuments(List<Object> documentParams, List<Object> orderParams, OrderEditModel order) {
        return false;
    }

    @Override
    public Long createOrderInDatabase(List<Object> orderParams, List<OrderCreateStudentModel> students) {

        Long idOrder = (orderParams.size() == 8
                        ? getOrderCreateService().createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams),
                                                                   getParamsGetter().getDescFromParams(orderParams),
                                                                   getParamsGetter().getScholarshipInfoFromParams(orderParams))

                        : getOrderCreateService().createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams),
                                                                   getParamsGetter().getDescFromParams(orderParams)
                        ));

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        for (OrderCreateOrderSectionModel sectionModel : listSections) {

            if (sectionModel.getId().equals(117L) && orderParams.size() == 8 ||
                (sectionModel.getId().equals(115L) && orderParams.size() == 7)) {

                Long idLOS = getOrderCreateService()
                        .createEmptySection(idOrder, sectionModel.getId(), sectionModel.getFoundation(), (Date) orderParams.get(0),
                                            (Date) orderParams.get(1)
                        );

                for (OrderCreateStudentModel student : students) {
                    getOrderCreateService().createStudentInSection(idLOS, student.getId(), student.getFirstDate(), student.getSecondDate(),
                                                                   student.getGroupname(), ""
                    );
                }
            }
        }

        return idOrder;
    }

    @Override
    public void setParamForStudent(Integer i, Object value, StudentModel studentModel, Long idOS) {

    }

    @Override
    public Object getParamForStudent(Integer i, StudentModel studentModel, Long idOS) {
        return null;
    }

    @Override
    public String getStringParamForStudent(Integer i, StudentModel studentModel, Long idOS) {
        return null;
    }
}
