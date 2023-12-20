package org.edec.mine.ctrl.student.component;

import org.edec.cloud.sync.mine.api.student.data.StudentCurrentSemesterDto;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

public class ListboxStudentSearch
        extends Listbox
        implements ListitemRenderer<StudentCurrentSemesterDto> {

    private Auxhead auxheadFilter;
    private Listhead listhead;
    public Textbox tbFamily, tbName, tbPatronymic, tbGroupname;

    private LinkStudentsListener linkStudentsListener;
    private SearchStudentListener searchStudentListener;


    public ListboxStudentSearch() {

        setDefaultSettings();
        createAuxHead();
        createListHead();
        addTextboxEventListeners();
        setItemRenderer(this);
    }

    private void setDefaultSettings() {

        setHflex("1");
        setVflex("1");
    }

    private void createAuxHead() {

        auxheadFilter = new Auxhead();
        auxheadFilter.setParent(this);

        tbGroupname = createAuxHeaderWithTextBox("Группа");
        tbFamily = createAuxHeaderWithTextBox("Фамилия");
        tbName = createAuxHeaderWithTextBox("Имя");
        tbPatronymic = createAuxHeaderWithTextBox("Отчество");
        createAuxHeaderWithTextBox(null);
    }

    private Textbox createAuxHeaderWithTextBox(String placeholder) {

        Auxheader auxheader = new Auxheader();
        auxheader.setParent(auxheadFilter);

        Textbox textbox = new Textbox();
        if (placeholder != null) {
            textbox.setParent(auxheader);
        }

        textbox.setHflex("1");
        textbox.setPlaceholder(placeholder);

        return textbox;
    }

    private void createListHead() {

        listhead = new Listhead();
        listhead.setParent(this);

        createListheader("Группа", "90px");
        createListheader("Фамилия");
        createListheader("Имя");
        createListheader("Отчество");
        createListheader("");
    }

    public void createListheader(String labelName) {
        createListheader(labelName, null);
    }

    public void createListheader(String labelName, String width) {

        Listheader listheader = new Listheader(labelName);
        listheader.setParent(listhead);
        if (width != null) {
            listheader.setWidth(width);
        } else {
            listheader.setHflex("1");
        }
    }

    public void clearListHeadChildren() {

        listhead.getChildren().clear();
    }

    private void addTextboxEventListeners() {

        tbGroupname.addEventListener(Events.ON_OK, event -> refreshData());
        tbFamily.addEventListener(Events.ON_OK, event -> refreshData());
        tbName.addEventListener(Events.ON_OK, event -> refreshData());
        tbPatronymic.addEventListener(Events.ON_OK, event -> refreshData());
    }

    private void refreshData() {

        if (searchStudentListener == null) {
            PopupUtil.showWarning("Действие для поиска не установлено");
        } else {
            searchStudentListener.refreshData();
        }
    }

    private void linkStudents(StudentCurrentSemesterDto selectedStudent) {

        if (linkStudentsListener == null) {
            PopupUtil.showWarning("Действия для сопоставления не задано");
        } else {
            linkStudentsListener.linkStudents(selectedStudent);
        }
    }

    public void setSearchStudentListener(SearchStudentListener searchStudentListener) {
        this.searchStudentListener = searchStudentListener;
    }

    public void setLinkStudentsListener(LinkStudentsListener linkStudentsListener) {
        this.linkStudentsListener = linkStudentsListener;
    }

    @Override
    public void render(Listitem li, StudentCurrentSemesterDto data, int index) throws Exception {

        li.setValue(data);

        new Listcell(data.getGroupname()).setParent(li);
        new Listcell(data.getFamily()).setParent(li);
        new Listcell(data.getName()).setParent(li);
        new Listcell(data.getPatronymic()).setParent(li);

        Listcell lcLinkStudents = new Listcell();
        lcLinkStudents.setParent(li);

        Button btnLinkStudents = new Button("Соединить");
        btnLinkStudents.setParent(lcLinkStudents);
        btnLinkStudents.addEventListener(Events.ON_CLICK, event -> linkStudents(data));
    }

    public interface SearchStudentListener {
        void refreshData();
    }

    public interface LinkStudentsListener {
        void linkStudents(StudentCurrentSemesterDto selectedStudent);
    }
}
