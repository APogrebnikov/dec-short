package org.edec.newOrder.service.orderCreator.concept;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.newOrder.manager.CreateOrderManagerESO;
import org.edec.newOrder.manager.StudentsGetterForCreationManager;
import org.edec.newOrder.model.ScholarshipModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.report.OrderLineService;
import org.edec.newOrder.report.OrderReportMineDAO;
import org.edec.newOrder.report.model.OrderLineModel;
import org.edec.newOrder.report.service.OrderReportFillService;
import org.edec.newOrder.service.esoImpl.CreateOrderServiceESO;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.constants.QualificationConst;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.fileManager.FileModel;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AcademicFirstCourseMultiCreator {

    List<Object> valueOrderParams;

    OrderService orderService;
    StudentsGetterForCreationManager studentsOrderManager = new StudentsGetterForCreationManager();
    CreateOrderManagerESO managerESO = new CreateOrderManagerESO();
    OrderReportMineDAO orderReportMineDAO = new OrderReportMineDAO();

    FileManager fileHelper = new FileManager();
    OrderLineService orderLineService = new OrderLineService();
    OrderReportFillService orderReportFillService = new OrderReportFillService();

    public AcademicFirstCourseMultiCreator(List<Object> valueOrderParams, OrderService orderService) {
        this.valueOrderParams = valueOrderParams;
        this.orderService = orderService;
    }

    public boolean createMultipleAcademicOrders() {
        CreateOrderServiceESO orderServiceESO = new CreateOrderServiceESO();

        List<OrderCreateStudentModel> students;

        try {
            students = orderServiceESO
                    .parseForAcademicalFirstCourseIncreaseOrder(((Date) valueOrderParams.get(0)),
                                                                ((Date) valueOrderParams.get(1)),
                                                                (Media) valueOrderParams.get(2));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        //Получаем обратно список стипендий, чтобы по ним формировать приказы
        List<ScholarshipModel> scholarshipModels = new ArrayList<>();

        ScholarshipModel scholarship = null;

        for (OrderCreateStudentModel student : students) {
            if (scholarship == null) {
                scholarship = student.getScholarshipInfo();
            }
            if (!scholarship.getParagraph().equals(student.getScholarshipInfo().getParagraph())) {
                scholarshipModels.add(scholarship);

                scholarship = student.getScholarshipInfo();
            }
        }

        scholarshipModels.add(scholarship);

        if(checkWrongScholarshipNames(scholarshipModels)){
            return false;
        }

        //получаем информацию по студентам из файла
        StringBuilder subQuery = new StringBuilder();

        for (OrderCreateStudentModel student : students) {
            subQuery.append(student.getFio() + "|");
        }

        List<OrderCreateStudentModel> studentsFromSystemList = studentsOrderManager
                .getStudentsInfoForAcademicFirstCourse(subQuery.toString().substring(0, subQuery.toString().length() - 1));

        checkNonexistentStudents(students, studentsFromSystemList);

        students = students.stream().filter(studentModel -> studentModel.getId() != null).collect(Collectors.toList());

        //Создаем приказы для повышенной стипендии

        for (ScholarshipModel scholarshipModel : scholarshipModels) {

            List<Object> orderParams = new ArrayList<>(valueOrderParams);
            orderParams.add(3, scholarshipModel);
            orderParams.set(orderParams.size() - 1, scholarshipModel.getText());

            List<OrderCreateStudentModel> studentForOrder = students.stream()
                                                                    .filter(studentModel -> studentModel.getScholarshipInfo().getParagraph()
                                                                                                        .equals(scholarshipModel
                                                                                                                        .getParagraph()))
                                                                    .collect(Collectors.toList());

            createOrder(studentForOrder, orderParams);
        }

        //получаем обычных студентов
        StringBuilder subQueryIds = new StringBuilder();

        for (OrderCreateStudentModel student : students) {
            subQueryIds.append(student.getId() + ",");
        }

        //Те студенты, которые не попадают в приказ на повышенную должны попасть в обычный

        List<OrderCreateStudentModel> studentsForNotIncreasedOrders = studentsOrderManager
                .getStudentsForAcademicFirstCourseExceptingIncrease(
                        subQueryIds.toString().substring(0, subQueryIds.toString().length() - 1));

        //бакалавры
        List<OrderCreateStudentModel> bachelorStudentList = studentsForNotIncreasedOrders.stream().filter(studentModel -> studentModel
                .getQualification().equals(QualificationConst.BACHELOR.getValue())).collect(Collectors.toList());

        valueOrderParams.set(valueOrderParams.size() - 1, "Бакалавры");

        createOrder(bachelorStudentList, valueOrderParams);

        //магистры
        List<OrderCreateStudentModel> masterStudentList = studentsForNotIncreasedOrders.stream().filter(studentModel -> studentModel
                .getQualification().equals(QualificationConst.MASTER.getValue())).collect(Collectors.toList());

        valueOrderParams.set(valueOrderParams.size() - 1, "Магистры");

        createOrder(masterStudentList, valueOrderParams);

        return true;
    }

    private void createFolderForOrder(Long idInstitute, Long idSemester, Long idOrder) {
        FileModel fileModel = new FileModel(FileModel.Inst.getInstById(idInstitute), FileModel.TypeDocument.ORDER,
                                            FileModel.SubTypeDocument.ACADEMIC, idSemester, idOrder.toString()
        );

        fileHelper.createFolder(fileModel, true, true, null);
        fileHelper.setPathFile();

        setUrlForOrder(idOrder, FileManager.getRelativePath(fileModel));
    }

    private void setUrlForOrder(Long idOrder, String url) {
        managerESO.setUrlForOrder(idOrder, url);
    }

    public Long createEmptyOrder(Long idSemester, String description) {
        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        return managerESO.createEmptyOrder(orderService.getOrderRuleConst().getId(), new Date(), idSemester, idHumanface, description);
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

    private void createOrder(List<OrderCreateStudentModel> students, List<Object> orderParams) {

        Long idOrder = orderService.createOrderInDatabase(orderParams, students);

        createFolderForOrder(orderService.getParamsGetter().getIdInstFromParams(valueOrderParams),
                             orderService.getParamsGetter().getIdSemesterFromParams(valueOrderParams), idOrder);

        List<OrderLineModel> lines = orderReportFillService.getLines(idOrder);

        if (!lines.isEmpty()) {
            orderLineService.saveOrderLines(idOrder, orderReportFillService.getLines(idOrder));
        }
    }

    //обработка ошибок

    //неправильные названия у стипендий в файле
    private boolean checkWrongScholarshipNames(List<ScholarshipModel> scholarshipList){
        //получаем информацию по студентам из файла
        boolean hasError = false;

        StringBuilder errorMessageBuilder = new StringBuilder();

        for(ScholarshipModel scholarshipModel : scholarshipList){
            if(!orderReportMineDAO.checkScholarship(scholarshipModel.getName())){
                errorMessageBuilder.append(scholarshipModel.getName() + "\n");
                hasError = true;
            }
        }

        if (hasError) {
            DialogUtil.info(errorMessageBuilder.toString(), "Названия данных стипендий не соответствуют действительности");
        }

        return hasError;
    }

    //несуществующие в системе студенты
    private boolean checkNonexistentStudents(List<OrderCreateStudentModel> listFromFile, List<OrderCreateStudentModel> listFromSystem){
        Set<String> fioFileList = listFromFile.stream().map(OrderCreateStudentModel::getFio).collect(Collectors.toSet());

        Set<String> fioSystemList = listFromSystem.stream().map(OrderCreateStudentModel::getFio).collect(Collectors.toSet());

        fioFileList.removeAll(fioSystemList);

        if(fioFileList.isEmpty()){
            StringBuilder errorMessageBuilder = new StringBuilder();

            for(String fio : fioSystemList) {
                errorMessageBuilder.append(fio + "\n");
            }

            DialogUtil.info(errorMessageBuilder.toString(), "Данные студенты отсутствуют в АСУ ИКИТ");

            return true;
        }else{
            return false;
        }
    }

    //проверка на полных тезок
    private boolean checkFullNameSakes(){
        //TODO:Написать метод
        return true;
    }

}
