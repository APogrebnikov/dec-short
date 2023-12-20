package org.edec.newOrder.service.orderCreator;

import lombok.Getter;
import lombok.Setter;
import org.edec.newOrder.manager.CreateOrderManagerESO;
import org.edec.newOrder.manager.EditOrderManagerESO;
import org.edec.newOrder.manager.StudentsGetterForCreationManager;
import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.model.createOrder.OrderCreateDocumentModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.report.OrderLineService;
import org.edec.newOrder.report.OrderReportDAO;
import org.edec.newOrder.report.service.OrderReportFillService;
import org.edec.newOrder.service.DocumentService;
import org.edec.newOrder.service.esoImpl.EditOrderService;
import org.edec.newOrder.service.orderCreator.concept.EditCreatedOrderService;
import org.edec.newOrder.service.orderCreator.concept.OrderCreateService;
import org.edec.newOrder.service.orderCreator.concept.ParamsGetter;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.OrderRuleConst;

import java.util.*;

@Getter
@Setter
public abstract class OrderService {
    // SERVICE
    protected DocumentService documentService = new DocumentService();
    private EditOrderService editOrderService = new EditOrderService();
    // MANAGERS
    protected CreateOrderManagerESO managerESO = new CreateOrderManagerESO();
    private EditCreatedOrderService editCreatedOrderService;
    private ParamsGetter paramsGetter;
    protected OrderReportDAO orderReportDAO = new OrderReportDAO();
    // DATA
    protected boolean isFilesNeeded;
    protected StudentsGetterForCreationManager studentsOrderManager = new StudentsGetterForCreationManager();
    protected List<OrderCreateParamModel> orderParams = new ArrayList<>();
    protected OrderRuleConst orderRuleConst;
    protected GroupingInEditEnum groupingInEditEnum;
    protected FormOfStudy formOfStudy;
    protected Long idInstitute;
    protected List<OrderCreateDocumentModel> orderDocuments = new ArrayList<>();
    protected List<OrderVisualParamModel> orderVisualGeneralParams = new ArrayList<>();
    protected Map<Long, List<OrderVisualParamModel>> orderSectionVisualParamsMap = new HashMap<>();
    private OrderCreateService orderCreateService;

    public OrderService() {
        this(null, null);
    }

    public OrderService(FormOfStudy fos, Long idInstitute) {
        this.formOfStudy = fos;
        this.idInstitute = idInstitute;

        generateParamModel();
        generateDocumentModel();

        editCreatedOrderService = new EditCreatedOrderService(this);
        paramsGetter = new ParamsGetter();
        orderCreateService = new OrderCreateService(this);
    }

    protected abstract void generateParamModel();
    protected abstract void generateDocumentModel();

    /** CreateOrder */
    public abstract boolean createAndAttachOrderDocuments(List<Object> documentParams, List<Object> orderParams, OrderEditModel order);
    public abstract Long createOrderInDatabase (List<Object> orderParams, List<OrderCreateStudentModel> students);
    /** CreateOrder */


    /** EditOrder */
    public abstract void setParamForStudent (Integer i, Object value, StudentModel studentModel, Long idOS);
    public void removeStudentFromOrder (Long idLoss, OrderEditModel order) {
        long idOrder = new EditOrderManagerESO().getIdOrder(idLoss);
        editCreatedOrderService.removeStudentFromOrder(idLoss);
        new OrderLineService().updateOrderLines(idOrder, new OrderReportFillService().getLines(idOrder));
    }
    /** EditOrder */

    /** VisualiseOrder */
    public abstract Object getParamForStudent (Integer i, StudentModel studentModel, Long idOS);
    public abstract String getStringParamForStudent (Integer i, StudentModel studentModel, Long idOS);
    public GroupingInEditEnum getGroupingInEditEnum () {
        return groupingInEditEnum;
    }
    /** VisualiseOrder */

    public List<OrderCreateDocumentModel> getOrderDocuments () {
        return orderDocuments;
    }
    public boolean isFilesNeeded () {
        return isFilesNeeded;
    }
}
