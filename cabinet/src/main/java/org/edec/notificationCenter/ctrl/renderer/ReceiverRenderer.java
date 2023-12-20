package org.edec.notificationCenter.ctrl.renderer;

import org.edec.notificationCenter.model.ReceiverModel;
import org.edec.notificationCenter.model.receiverTypes.CourseModel;
import org.edec.notificationCenter.model.receiverTypes.EmployeeModel;
import org.edec.notificationCenter.model.receiverTypes.GroupModel;
import org.edec.notificationCenter.model.receiverTypes.StudentModel;
import org.edec.utility.constants.ReceiverStatusConst;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.function.Consumer;

/**
 * Данный класс-рендерер используется в окне для поиска получателей, а также в окне для просмотра получателей
 */
public class ReceiverRenderer implements ListitemRenderer<ReceiverModel> {

    public static final boolean IS_SEARCH_ELEMENT = true;

    public static final boolean IS_EDITOR_ELEMENT = false;

    private Consumer<ReceiverModel> action;
    private boolean isSearchElement;

    public ReceiverRenderer(Consumer<ReceiverModel> action, boolean isSearchElement) {
        this.action = action;
        this.isSearchElement = isSearchElement;
    }

    @Override
    public void render(Listitem item, ReceiverModel data, int index) throws Exception {
        item.setValue(data);

        switch (data.getReceiverType()) {
            case STUDENTS:
            case LEADERS:
                new Listcell(((StudentModel) data).getFio()).setParent(item);
                new Listcell(((StudentModel) data).getGroupName()).setParent(item);
                break;
            case GROUPS:
                new Listcell(((GroupModel) data).getGroupName()).setParent(item);
                break;
            case COURSES:
                new Listcell(((CourseModel) data).getCourseNumber().toString()).setParent(item);
                break;
            case EMPLOYEES:
                new Listcell(((EmployeeModel) data).getFio()).setParent(item);
                break;
        }

        Listcell lcActionReceiver = new Listcell();
        lcActionReceiver.setParent(item);

        Button btnAction = new Button(isSearchElement ? "Добавить" : "Удалить");

        btnAction.addEventListener(Events.ON_CLICK, event -> {
            if(action != null) action.accept(data);
            item.detach();
        });

        btnAction.setParent(lcActionReceiver);

        //если получатель уже получил уведомление, удалить его из рассылки нельзя
        if(!isSearchElement){
            if(data.getStatus().equals(ReceiverStatusConst.ADDED)) { //Если получатель был добавлен - подсвечиваем голубым
                item.setStyle("background: #38D6E8;");
            } else if(data.getStatus().equals(ReceiverStatusConst.SAVED)){ //Если получатель был сохраненен - подсвечиваем зеленым
                item.setStyle("background: #99ff99;");
            }
            else{
                btnAction.setVisible(false);
            }
        }
    }
}
