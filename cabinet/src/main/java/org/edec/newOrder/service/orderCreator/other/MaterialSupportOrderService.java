package org.edec.newOrder.service.orderCreator.other;


import org.apache.poi.ss.usermodel.Cell;
import org.edec.newOrder.manager.OrderMainManagerESO;
import org.edec.newOrder.model.addStudent.LinkOrderSectionEditModel;
import org.edec.newOrder.model.addStudent.SearchStudentModel;
import org.edec.newOrder.model.createOrder.OrderCreateDocumentModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.DocumentEnum;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.model.enums.ParamEnum;
import org.edec.newOrder.model.report.ProtocolComissionerModel;
import org.edec.newOrder.service.esoImpl.CreateOrderServiceESO;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zul.Messagebox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MaterialSupportOrderService extends OrderService {

    @Override
    protected void generateParamModel () {
        groupingInEditEnum = GroupingInEditEnum.BY_SECTION;

        orderParams.add(new OrderCreateParamModel("Назначить с", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Назначить по", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Список студентов", ComponentEnum.FILE, true));

        isFilesNeeded = true;
    }

    @Override
    protected void generateDocumentModel () {
        orderDocuments = new ArrayList<>();

        OrderCreateDocumentModel documentModel = new OrderCreateDocumentModel("Протокол", DocumentEnum.MATERIAL_SUPPORT);
        documentModel.getListDocumentParam().add(new OrderCreateParamModel("Список комиссии", ComponentEnum.FILE, true));
        documentModel.getListDocumentParam().add(new OrderCreateParamModel("Номер протокола", ComponentEnum.TEXTBOX, true));
        documentModel.getListDocumentParam().add(new OrderCreateParamModel("Дата заседания", ComponentEnum.DATEBOX, true));

        orderDocuments.add(documentModel);
    }

    @Override
    public Long createOrderInDatabase (List<Object> orderParams, List<OrderCreateStudentModel> students) {

        Long idOrder = getOrderCreateService().createEmptyOrder(getParamsGetter().getIdSemesterFromParams(orderParams), getParamsGetter().getDescFromParams(orderParams));

        Long idSem = getParamsGetter().getIdSemesterFromParams(orderParams);
        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());
        CreateOrderServiceESO orderServiceESO = new CreateOrderServiceESO();

        // Лист для отклоненных
        List<OrderCreateStudentModel> refusal = new ArrayList<>();

        try {
            List<OrderCreateStudentModel> tempStudents = orderServiceESO.parseForMaterialSupportOrder(((Media) orderParams.get(2)), ((Date) orderParams.get(0)), ((Date) orderParams.get(1)), idSem, null);

            for (OrderCreateStudentModel student : tempStudents) {
                // Если студент одобрен к мат. помощи
                if (student.getApprovedMaterial() == 1) {
                    students.add(student);
                } else {
                    // Если не одобрен - то в отдельный список для дальнейшей генерации
                    refusal.add(student);
                }
            }
            // Костыль для проброса списка отклоненных
            orderParams.add(refusal);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        StringBuilder warningAboutUnknownStudents = new StringBuilder("Следующие студенты не найдены:\n");
        boolean checkWarningAboutUnknownStudents = false;
        List<OrderCreateStudentModel> listVerifiedStudents = new ArrayList<>();

        for (int i = 0; i < students.size(); i++) {
            OrderCreateStudentModel student = students.get(i);
            if(student.getId().equals(-1L)) {
                checkWarningAboutUnknownStudents = true;
                warningAboutUnknownStudents.append(student.getFio()).append(", группа ").append(student.getGroupname()).append("\n");
            }
            else {
                listVerifiedStudents.add(student);
            }
        }

        if (checkWarningAboutUnknownStudents) {
            Messagebox.show(warningAboutUnknownStudents.toString());
        }

        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = getOrderCreateService().createEmptySection(idOrder, section.getId(), section.getFoundation(), (Date) orderParams.get(0), (Date) orderParams.get(1));

            for (OrderCreateStudentModel student : listVerifiedStudents) {
                getOrderCreateService().createStudentInSection(idLOS, student.getId(), (Date) orderParams.get(0), (Date) orderParams.get(1), student.getGroupname(),
                        "{\"scholarship\":\""+ student.getScholarship() +"\", \"typeScholarship\":\"" + student.getTypeScholarship() + "\"}");
            }
        }

        return idOrder;
    }

    @Override
    public boolean createAndAttachOrderDocuments (List<Object> documentParams, List<Object> orderParams, OrderEditModel order) {
        if (documentParams.get(0) != null) {
            HashMap<ParamEnum, Object> params = new HashMap<>();

            CreateOrderServiceESO orderServiceESO = new CreateOrderServiceESO();

            List<ProtocolComissionerModel> protocolComissionerModelList;

            try {
                protocolComissionerModelList = orderServiceESO.parseForMaterialSupportProtocol((Media)documentParams.get(0));
            }
            catch (Exception e) {
                e.printStackTrace();
                DialogUtil.error("Не удалось создать протокол, обратитесь к администратору!");
                return false;
            }

            params.put(ParamEnum.DOCUMENT_NAME, "Протокол.pdf");
            params.put(ParamEnum.LIST_STUDENT, protocolComissionerModelList);
            params.put(ParamEnum.NUMBER_PROTOCOL, documentParams.get(1));
            params.put(ParamEnum.DATE_FROM, documentParams.get(2));
            params.put(ParamEnum.DATE_BEGIN, orderParams.get(0));
            params.put(ParamEnum.DATE_END, orderParams.get(1));

            // params.put(ParamEnum.DATE_BEGIN, new Date());
            // params.put(ParamEnum.DATE_END, new Date());

            // Костыль для проброса отклоненных
            params.put(ParamEnum.LIST_REFUSAL, orderParams.get(orderParams.size()-1));

            documentService.generateDocument(DocumentEnum.MATERIAL_SUPPORT, order, params);
        }

        return false;
    }

    public void manualGenerate() {

        HashMap<ParamEnum, Object> params = new HashMap<>();

        List<ProtocolComissionerModel> protocolCommissionerModelList = new ArrayList<>();
        ProtocolComissionerModel protocolComissionerModel = new ProtocolComissionerModel();
        protocolComissionerModel.setFio("Перегудова И.П.");
        protocolComissionerModel.setShortFio("Перегудова И.П.");
        protocolComissionerModel.setGroupname("зам. директора по ВР");
        protocolComissionerModel.setRole("");
        protocolCommissionerModelList.add(protocolComissionerModel);

        protocolComissionerModel = new ProtocolComissionerModel();
        protocolComissionerModel.setFio("Комаров М.А.");
        protocolComissionerModel.setShortFio("Комаров М.А.");
        protocolComissionerModel.setGroupname("КИ17-01, глава МЦ ИКИТ, председатель совета обучающихся СФУ");
        protocolComissionerModel.setRole("Совет обучающихся");
        protocolCommissionerModelList.add(protocolComissionerModel);

        protocolComissionerModel = new ProtocolComissionerModel();
        protocolComissionerModel.setFio("Моисеева Е.О.");
        protocolComissionerModel.setShortFio("Моисеева Е.О.");
        protocolComissionerModel.setGroupname("КИ21-02/1Б");
        protocolComissionerModel.setRole("");
        protocolCommissionerModelList.add(protocolComissionerModel);

        protocolComissionerModel = new ProtocolComissionerModel();
        protocolComissionerModel.setFio("Брехов С.А.");
        protocolComissionerModel.setShortFio("Брехов С.А.");
        protocolComissionerModel.setGroupname("КИ19-02/1Б");
        protocolComissionerModel.setRole("");
        protocolCommissionerModelList.add(protocolComissionerModel);

        protocolComissionerModel = new ProtocolComissionerModel();
        protocolComissionerModel.setFio("Круглова П.Н.");
        protocolComissionerModel.setShortFio("Круглова П.Н.");
        protocolComissionerModel.setGroupname("КИ18-12Б, председатель профбюро ИКИТ");
        protocolComissionerModel.setRole("Профсоюзная организация");
        protocolCommissionerModelList.add(protocolComissionerModel);

        protocolComissionerModel = new ProtocolComissionerModel();
        protocolComissionerModel.setFio("Мардоян Г.А.");
        protocolComissionerModel.setShortFio("Мардоян Г.А.");
        protocolComissionerModel.setGroupname("КИ19-21Б");
        protocolComissionerModel.setRole("");
        protocolCommissionerModelList.add(protocolComissionerModel);

        protocolComissionerModel = new ProtocolComissionerModel();
        protocolComissionerModel.setFio("Пусенкова Д.В.");
        protocolComissionerModel.setShortFio("Пусенкова Д.В.");
        protocolComissionerModel.setGroupname("КИ19-21Б");
        protocolComissionerModel.setRole("");
        protocolCommissionerModelList.add(protocolComissionerModel);

        protocolComissionerModel = new ProtocolComissionerModel();
        protocolComissionerModel.setFio("Никонов В.А.");
        protocolComissionerModel.setShortFio("Никонов В.А.");
        protocolComissionerModel.setGroupname("КИ19-20Б");
        protocolComissionerModel.setRole("");
        protocolCommissionerModelList.add(protocolComissionerModel);

        protocolComissionerModel = new ProtocolComissionerModel();
        protocolComissionerModel.setFio("Онищенко А.А.");
        protocolComissionerModel.setShortFio("Онищенко А.А.");
        protocolComissionerModel.setGroupname("КИ19-21Б");
        protocolComissionerModel.setRole("");
        protocolCommissionerModelList.add(protocolComissionerModel);

        protocolComissionerModel = new ProtocolComissionerModel();
        protocolComissionerModel.setFio("Алексеенко А.Ю.");
        protocolComissionerModel.setShortFio("Алексеенко А.Ю.");
        protocolComissionerModel.setGroupname("КИ20-05Б");
        protocolComissionerModel.setRole("");
        protocolCommissionerModelList.add(protocolComissionerModel);

        protocolComissionerModel = new ProtocolComissionerModel();
        protocolComissionerModel.setFio("Вайнштейн Ю. В.");
        protocolComissionerModel.setShortFio("Вайнштейн Ю. В.");
        protocolComissionerModel.setGroupname("");
        protocolComissionerModel.setRole("Председатель комиссии");
        protocolCommissionerModelList.add(protocolComissionerModel);

        params.put(ParamEnum.DOCUMENT_NAME, "Протокол.pdf");
        params.put(ParamEnum.LIST_STUDENT, protocolCommissionerModelList);
        params.put(ParamEnum.NUMBER_PROTOCOL, "32");
        params.put(ParamEnum.DATE_FROM, java.sql.Date.valueOf(LocalDate.parse("2021-12-22")));
        params.put(ParamEnum.DATE_BEGIN,  java.sql.Date.valueOf(LocalDate.parse("2021-12-01")));
        params.put(ParamEnum.DATE_END,  java.sql.Date.valueOf(LocalDate.parse("2021-12-31")));

        // Костыль для проброса отклоненных
        //params.put(ParamEnum.LIST_REFUSAL, orderParams.get(orderParams.size()-1));


        OrderMainManagerESO orderMainManagerESO = new OrderMainManagerESO();
        OrderEditModel order = orderMainManagerESO.getOrderById(2108L);

        documentService.generateDocument(DocumentEnum.MATERIAL_SUPPORT, order, params);
    }

    @Override
    public void removeStudentFromOrder (Long idLoss, OrderEditModel order) {

    }

    @Override
    public void setParamForStudent (Integer i, Object value, StudentModel studentModel, Long idOS) {

    }

    @Override
    public Object getParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        return null;
    }

    @Override
    public String getStringParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        return null;
    }
}
