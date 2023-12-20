package org.edec.order.ctrl;

import org.edec.order.ctrl.renderer.DefineProlongationRenderer;
import org.edec.order.model.GroupModel;
import org.edec.order.model.OrderModel;
import org.edec.order.model.SectionModel;
import org.edec.order.model.StudentModel;
import org.edec.order.service.CreateOrderService;
import org.edec.order.service.EditOrderService;
import org.edec.order.service.impl.CreateOrderServiceESO;
import org.edec.order.service.impl.EditOrderServiceESO;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.fileManager.FileModel;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WinDefineProlongationCtrl extends SelectorComposer<Component> {
    public static final String ORDER = "order";
    public static final String ORDER_MODEL = "order_model";
    public static final String INSTITUTE = "institute";
    public static final String SEMESTER = "semester";

    private CreateOrderService service = new CreateOrderServiceESO();
    private EditOrderService editOrderService = new EditOrderServiceESO();
    private OrderModel order;
    private List<Media> listMedia = new ArrayList<>();
    private List<StudentModel> students;
    private InstituteModel institute;
    private Long sem;

    @Wire
    private Label countDocuments;
    @Wire
    private Button addDocuments, search;
    @Wire
    private Listbox searchResults;
    @Wire
    private Textbox family;
    @Wire
    private Window winDefineProlongation;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        order = (OrderModel) Executions.getCurrent().getArg().get(ORDER);
        institute = (InstituteModel) Executions.getCurrent().getArg().get(INSTITUTE);
        sem = (Long) Executions.getCurrent().getArg().get(SEMESTER);

        editOrderService.fillOrderModel(order);

        students = new ArrayList<>();

        for (SectionModel section : order.getSections()) {
            for (GroupModel groupModel : section.getGroups()) {
                students.addAll(groupModel.getStudentModels());
            }
        }

        for (StudentModel studentModel : students) {
            studentModel.setProlongatedManualy(false);
        }

        searchResults.setModel(new ListModelList<Object>(students));
        searchResults.setItemRenderer(new DefineProlongationRenderer());
        searchResults.renderAll();

        addDocuments.addEventListener("onClick", event -> {
            Map arg = new HashMap();
            arg.put(AddFilesToOrderAttachCtrl.LABEL_COUNT_DOCUMENT, countDocuments);
            arg.put(AddFilesToOrderAttachCtrl.MEDIAS, listMedia);

            ComponentHelper.createWindow("winAddFilesToOrderAttach.zul", "winAttachFiles", arg).doModal();
        });

        family.addEventListener(Events.ON_OK, event -> filter());
    }

    @Listen("onClick = #search")
    public void filter() {
        String surname = family.getText();

        List<StudentModel> filteredList = new ArrayList<>();

        for (StudentModel student : students) {
            if (student.getFio().toLowerCase().startsWith(surname.toLowerCase())) {
                filteredList.add(student);
            }
        }

        searchResults.setModel(new ListModelList<Object>(filteredList));
        searchResults.renderAll();
    }

    @Listen("onClick = #showOrder")
    public void create() {
        for (StudentModel student : students) {
            if (student.getProlongatedManualy() && student.getDateProlongation() == null) {
                PopupUtil.showWarning("Заполните дату у студента: " + student.getFio());
                return;
            }
        }

        for (StudentModel student : students) {
            if (student.getProlongatedManualy()) {
                service.changeSectionForStudentInTransfer(student.getId(), student.getDateProlongation());
            }
        }

        String pathCreatedFile = service
                .createOrderByParams(institute, FileModel.SubTypeDocument.TRANSFER, sem, String.valueOf(order.getIdOrder()), listMedia);

        if (pathCreatedFile == null || pathCreatedFile.equals("")) {
            PopupUtil.showError("Не удалось создать папку для приказа");
            return;
        }

        order.setUrl(pathCreatedFile);
        order = service.updateOrder(order);

        order = service.getCreatedOrderById(order.getIdOrder());

        Map arg = new HashMap();
        arg.put(ORDER_MODEL, order);
        ComponentHelper.createWindow("winEditOrder.zul", "winEditOrder", arg).doModal();

        winDefineProlongation.detach();
    }
}
