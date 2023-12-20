package org.edec.newOrder.service.orderCreator.concept;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.newOrder.manager.CreateOrderManagerESO;
import org.edec.newOrder.manager.EditOrderManagerESO;
import org.edec.newOrder.manager.OrderMainManagerESO;
import org.edec.newOrder.model.ScholarshipModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.createOrder.StudentWithReference;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.report.OrderLineService;
import org.edec.newOrder.report.model.OrderLineModel;
import org.edec.newOrder.report.service.OrderReportFillService;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.fileManager.FileModel;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;

import java.util.Date;
import java.util.List;

public class OrderCreateService {
    private OrderService orderService;
    private CreateOrderManagerESO managerESO = new CreateOrderManagerESO();
    private FileManager fileHelper = new FileManager();
    private OrderMainManagerESO orderMainManagerESO = new OrderMainManagerESO();
    private OrderLineService orderLineService = new OrderLineService();
    private OrderReportFillService orderReportFillService = new OrderReportFillService();

    public OrderCreateService(OrderService orderService) {
        this.orderService = orderService;
    }

    public OrderEditModel createOrder(List<Object> orderParams, List<Object> documentParams, List<Media> attachedDocuments,
                                      List<OrderCreateStudentModel> students, List<String> groups) {
        Long idInst = orderService.getParamsGetter().getIdInstFromParams(orderParams);
        Long idSemester = orderService.getParamsGetter().getIdSemesterFromParams(orderParams);
        // TODO транзакция
        Long idOrder = orderService.createOrderInDatabase(orderParams, students);

        if (idOrder == null) {
            return null;
        }

        createFolderForOrder(
                idInst, idSemester, idOrder, attachedDocuments
        );

        OrderEditModel order = orderMainManagerESO.getOrderById(idOrder);

        List<OrderLineModel> lines = orderReportFillService.getLines(idOrder);

        if (!lines.isEmpty()) {
            orderLineService.saveOrderLines(idOrder, orderReportFillService.getLines(idOrder));
        }

        orderService.createAndAttachOrderDocuments(documentParams, orderParams, order);

        return order;
    }

    // TODO REFACTOR
    private void createFolderForOrder(Long idInstitute, Long idSemester, Long idOrder, List<Media> attached) {
        FileModel fileModel = new FileModel(FileModel.Inst.getInstById(idInstitute), FileModel.TypeDocument.ORDER,
                                            getTypeDocumentByRule(orderService.getOrderRuleConst()), idSemester, idOrder.toString()
        );

        fileHelper.createFolder(fileModel, true, true, attached);
        setUrlForOrder(idOrder, FileManager.getRelativePath(fileModel));

        FileManager fileHelper = new FileManager();
        String result = fileHelper.createFolder(fileModel, true, true, attached);

        boolean isSuccesTransfer = true;
        if (result != null && !result.equals("")) {
            if (getTypeDocumentByRule(orderService.getOrderRuleConst()) == (FileModel.SubTypeDocument.SOCIAL)) {
                List<StudentWithReference> listStudents = this.getStudentsWithReferenceFromSocialOrder(idOrder);

                for (StudentWithReference student : listStudents) {
                    if (student.getIdReference() == null) {
                        continue;
                    }

                    boolean bResult = fileHelper.transferFilesFromReferenceToAttached(fileModel,
                                                                                      student.getFamily() + " " + student.getName() + " " +
                                                                                      student.getPatronymic(), student.getUrl()
                    );

                    if (isSuccesTransfer && !bResult) {
                        isSuccesTransfer = false;
                    }
                }
            }

            if (getTypeDocumentByRule(orderService.getOrderRuleConst()) == (FileModel.SubTypeDocument.SOCIAL_INCREASE)) {
                List<StudentWithReference> listStudents = getStudentsWithReferenceFromSocialIncreasedOrder(idOrder);

                for (StudentWithReference student : listStudents) {
                    if (student.getIdReference() == null) {
                        continue;
                    }

                    boolean bResult = fileHelper.transferFilesFromReferenceToAttached(fileModel,
                                                                                      student.getFamily() + " " + student.getName() + " " +
                                                                                      student.getPatronymic(), student.getUrl()
                    );

                    if (isSuccesTransfer && !bResult) {
                        isSuccesTransfer = false;
                    }
                }
            }

            if (!isSuccesTransfer) {
                PopupUtil.showError("Возникли проблемы с переносом файлов справок. Обратитесь к администратору");
            }
        }
    }

    private void setUrlForOrder(Long idOrder, String url) {
        managerESO.setUrlForOrder(idOrder, url);
    }

    private FileModel.SubTypeDocument getTypeDocumentByRule(OrderRuleConst orderRuleConst) {
        switch (orderRuleConst) {
            case PROLONGATION_ELIMINATION_WINTER:
            case TRANSFER:
            case TRANSFER_AFTER_TRANSFER_CONDITIONALLY_NOT_RESPECTFUL:
            case TRANSFER_AFTER_TRANSFER_CONDITIONALLY_RESPECTFUL:
            case TRANSFER_CONDITIONALLY:
            case TRANSFER_PROLONGATION:
            case TRANSFER_CONDITIONALLY_RESPECTFUL:
                return FileModel.SubTypeDocument.TRANSFER;
            case DEDUCTION_INITIATIVE:
            case DEDUCTION_BY_COMMISSION_RESULT:
                return FileModel.SubTypeDocument.DEDUCTION;
            case CANCEL_SCHOLARSHIP_AFTER_PRACTICE:
            case ACADEMIC_IN_SESSION:
            case ACADEMIC_NOT_IN_SESSION:
            case CANCEL_ACADEMICAL_SCHOLARSHIP_IN_SESSION:
            case ACADEMIC_INDIVIDUAL:
                return FileModel.SubTypeDocument.ACADEMIC;
            case SOCIAL_IN_SESSION:
            case SOCIAL_NEW_REFERENCE:
                return FileModel.SubTypeDocument.SOCIAL;
            case SOCIAL_INCREASED_IN_SESSION:
            case SOCIAL_INCREASED_NEW_REFERENCE:
            case CANCEL_SOCIAL_INCREASED_BY_PRACTICE:
            case CANCEL_SOCIAL_INCREASED_IN_SESSION:
                return FileModel.SubTypeDocument.SOCIAL_INCREASE;
            case ACADEMIC_INCREASED:
                return FileModel.SubTypeDocument.ACADEMIC_INCREASED;
            case MATERIAL_SUPPORT:
                return FileModel.SubTypeDocument.MATERIAL_SUPPORT;
            case SET_ELIMINATION_RESPECTFUL:
            case SET_ELIMINATION_NOT_RESPECTFUL:
            case SET_ELIMINATION_AFTER_TRANSFER_RESPECTFUL:
            case SET_ELIMINATION_AFTER_PRACTICE:
            case SET_ELIMINATION_AFTER_TRANSFER_NOT_RESPECTFUL:
            case SET_FIRST_ELIMINATION:
            case SET_SECOND_ELIMINATION:
                return FileModel.SubTypeDocument.SET_ELIMINATION;
        }

        return null;
    }

    public Long createEmptyOrder(Long idSemester, String description) {
        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        return managerESO.createEmptyOrder(orderService.getOrderRuleConst().getId(), new Date(), idSemester, idHumanface, description);
    }

    public Long createEmptyOrder(Long idSemester, String description, ScholarshipModel scholarshipInfo){
        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        return managerESO.createEmptyOrder(orderService.getOrderRuleConst().getId(), new Date(), idSemester, idHumanface, description, scholarshipInfo);
    }

    public Long createEmptySection(Long idOrder, Long idOS, String foundation, Date firstDate, Date secondDate) {
        return managerESO.createLinkOrderSection(idOrder, idOS, foundation, firstDate, secondDate);
    }

    public void createStudentInSection(Long idLOS, Long idSSS, Date firstDate, Date secondDate, String groupname, String additional) {
        managerESO.createLinkOrderStudentStatus(idLOS, idSSS, firstDate, secondDate, groupname, null, additional);
    }

    public void createStudentInSection(Long idLOS, Long idSSS, Date firstDate, Date secondDate, String groupname, Integer curSemester,
                                       String additional) {
        managerESO.createLinkOrderStudentStatus(idLOS, idSSS, firstDate, secondDate, groupname, curSemester,
                                                (additional == null || additional.equals("")) ? "{}" : additional
        );
    }

    public List<OrderCreateParamModel> getOrderParams() {
        return orderService.getOrderParams();
    }

    private List<StudentWithReference> getStudentsWithReferenceFromSocialOrder(Long idOrder) {
        return managerESO.getStudentsWithReferenceFromSocialOrder(idOrder);
    }

    private List<StudentWithReference> getStudentsWithReferenceFromSocialIncreasedOrder(Long idOrder) {
        return managerESO.getStudentsWithReferenceFromSocialIncreasedOrder(idOrder);
    }

    public List<OrderCreateStudentModel> getStudentsForFilterDeductionByComission(Long idOrderHead) {

        return managerESO.getStudentsForFilterDeductionByComission(idOrderHead);
    }

    public Boolean deleteStudentByFilter(Long idLoss) {
        if (managerESO.deleteStudentByFilter(idLoss)) {

            long idOrder = new EditOrderManagerESO().getIdOrder(idLoss);
            orderLineService.updateOrderLines(idOrder, orderReportFillService.getLines(idOrder));
            return true;
        }

        return false;
    }

    public void updateOrderAttr(Long idOrder, List<String> attrList){
        managerESO.updateOrderAttr(idOrder, attrList);
    }

}
