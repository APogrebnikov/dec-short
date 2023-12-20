package org.edec.order.ctrl;

import org.edec.order.model.SearchStudentModel;
import org.edec.order.model.SemesterModel;
import org.edec.order.model.StudentToAddModel;
import org.edec.order.service.impl.CreateOrderServiceESO;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.OrderTypeConst;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.edec.order.ctrl.WinCreateOrderCtrl.FORM_OF_STUDY;
import static org.edec.order.ctrl.WinCreateOrderCtrl.INSTITUTE_MODEL;
import static org.edec.order.ctrl.WinCreateOrderCtrl.ORDER_TYPE;


public class WinAddStudentManualyCtrl extends SelectorComposer<Component> {
    @Wire
    Textbox family;

    @Wire
    Button search;

    @Wire
    Listbox searchResults;

    @Wire
    Window winAddStudentManualy;

    @Wire
    Button btnAdd;

    @Wire
    Combobox cmbListSem;


    List<StudentToAddModel> students;

    Set<SearchStudentModel> listToAdd = new HashSet<>();

    private FormOfStudy formOfStudy;
    private InstituteModel selectedInst;
    private OrderTypeConst orderTypeConst;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        students = (List<StudentToAddModel>) Executions.getCurrent().getArg().get("students");
        formOfStudy = (FormOfStudy) Executions.getCurrent().getArg().get(FORM_OF_STUDY);
        selectedInst = (InstituteModel) Executions.getCurrent().getArg().get(INSTITUTE_MODEL);
        orderTypeConst = (OrderTypeConst) Executions.getCurrent().getArg().get(ORDER_TYPE);
        final Listbox listbox = (Listbox)Executions.getCurrent().getArg().get("listbox");


        ListModelList<SemesterModel> lmlSem = new ListModelList<>(new CreateOrderServiceESO().getSemesterByInstitute(selectedInst.getIdInst(), formOfStudy.getType()));
        cmbListSem.setItemRenderer(new ComboitemRenderer<SemesterModel>() {
            @Override
            public void render(Comboitem comboitem, SemesterModel semesterModel, int i) throws Exception {
                int yearBegin = semesterModel.getDateOfBeginYear().getYear() + 1900;
                int yearEnd = semesterModel.getDateOfEndYear().getYear() + 1900;

                String seasonStr = semesterModel.getSeason() == 0 ? "осенний" : "весенний";

                comboitem.setLabel(yearBegin + " - " + yearEnd + " / " + seasonStr);
                comboitem.setValue(semesterModel);
            }
        });


        cmbListSem.setModel(lmlSem);

        btnAdd.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                outer:
                for(final SearchStudentModel sss : listToAdd) {
                    for(StudentToAddModel sSs : students) {
                        if(sSs == null || sSs.getId() == null) {
                            continue;
                        }

                        if(sSs.getId() == sss.getId()) {
                            continue outer;
                        }
                    }

                    final StudentToAddModel student = new StudentToAddModel();

                    final Listitem item = new Listitem();

                    final Image delete = new Image("/imgs/crossCLR.png");
                    delete.addEventListener("onClick", new EventListener<Event>() {
                        @Override
                        public void onEvent(Event event) throws Exception {
                            listbox.removeChild(item);
                            students.remove(student);
                        }
                    });

                    Listcell deleteCell = new Listcell();
                    deleteCell.appendChild(delete);

                    final Label fio = new Label(sss.getSurname() + " " + sss.getName() + " " + sss.getPatronymic());
                    Listcell fioCell = new Listcell();
                    fioCell.appendChild(fio);

                    final Datebox dateDeduction = new Datebox(new Date());
                    dateDeduction.addEventListener("onChange", new EventListener<Event>() {
                        @Override
                        public void onEvent(Event event) throws Exception {
                            student.setFirstDate(dateDeduction.getValue());
                        }
                    });

                    Listcell dateDeductionCell = new Listcell();
                    dateDeductionCell.appendChild(dateDeduction);

                    final Datebox dateEndPayment = new Datebox(new Date());
                    dateEndPayment.addEventListener("onChange", new EventListener<Event>() {
                        @Override
                        public void onEvent(Event event) throws Exception {
                            student.setSecondDate(dateEndPayment.getValue());
                        }
                    });
                    Listcell dateEndPaymentCell = new Listcell();
                    dateEndPaymentCell.appendChild(dateEndPayment);

                    students.add(student);
                    student.setFirstDate(new Date());
                    student.setId(sss.getId());
                    student.setGovernmentFinanced(sss.getGovernmentFinanced());
                    student.setGroup(sss.getGroupname());

                    item.appendChild(fioCell);
                    item.appendChild(dateDeductionCell);
                    if(orderTypeConst.equals(OrderTypeConst.DEDUCTION)) {
                        if(formOfStudy.equals(FormOfStudy.FULL_TIME)) {
                            item.appendChild(dateEndPaymentCell);
                            student.setSecondDate(new Date());
                        }
                    }
                    item.appendChild(deleteCell);

                    listbox.appendChild(item);

                    searchResults.getItems().clear();
                }
            }
        });
    }



    @Listen("onAfterRender = #cmbListSem")
    public void afterRenderCmbSem()
    {
        for(int i = 0; i < cmbListSem.getItemCount(); i++) {
            if(((SemesterModel)cmbListSem.getItems().get(i).getValue()).getCurrentSemester()) {
                cmbListSem.setSelectedIndex(i);
            }
        }
    }

    @Listen("onClick = #returnToOrder")
    public void onBtnReturn() {
        winAddStudentManualy.detach();
    }

    @Listen("onClick = #search")
    public void onSearchClick() {
        if(family.getText() == "") {
            PopupUtil.showInfo("Введите фамилию");
            return;
        }

        searchResults.getItems().clear();

        List<SearchStudentModel> listSSS = new CreateOrderServiceESO()
                        .getStudentsBySurnameAndSemester(family.getText(), ((SemesterModel) cmbListSem.getSelectedItem().getValue()).getId().longValue());

        for(final SearchStudentModel sss : listSSS) {
            Listitem item= new Listitem();
            Listcell cellFio = new Listcell(sss.getSurname() + " " + sss.getName() + " " + sss.getPatronymic());
            Listcell cellGroup = new Listcell(sss.getGroupname());
            item.appendChild(cellFio);
            item.setValue(sss);
            item.appendChild(cellGroup);
            item.addEventListener("onClick", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {

                    listToAdd.clear();
                    for (Listitem item : searchResults.getSelectedItems()) {
                        listToAdd.add((SearchStudentModel) item.getValue());
                    }
                }
            });

            searchResults.appendChild(item);
        }
    }
}
