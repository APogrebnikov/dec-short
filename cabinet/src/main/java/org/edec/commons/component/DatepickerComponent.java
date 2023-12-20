package org.edec.commons.component;

import org.edec.utility.converter.DateConverter;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;

import java.util.Date;

/**
 * Общий компонент для выбора даты, принимающий форму надписи в неактивном состоянии
 */
public class DatepickerComponent extends Listcell {
    private Label lbComponent;
    private Datebox dbComponent;

    public DatepickerComponent(Date value){
        this.lbComponent = new Label();
        this.dbComponent = new Datebox();

        onRefresh(value);

        dbComponent.setVisible(false);

        dbComponent.setWidth("90%");
        dbComponent.addEventListener(Events.ON_CHANGE, event -> onChange());
        dbComponent.addEventListener(Events.ON_BLUR, event -> showDate(false));

        this.appendChild(lbComponent);
        this.appendChild(dbComponent);

        this.addEventListener(Events.ON_DOUBLE_CLICK, event -> onDoubleClick());
        this.addEventListener("onRefresh", event-> onRefresh((Date) event.getData()));
    }

    public void onRefresh(Date value){
        this.setValue(value);
        if (value != null) {
            lbComponent.setValue(DateConverter.convertDateToString(value));
        } else {
            lbComponent.setValue("");
        }
    }

    private void onChange(){
        this.setValue(dbComponent.getValue());
        Events.echoEvent("onUpdate", this, null);
        showDate(false);
    }

    private void onDoubleClick(){
        showDate(true);

        dbComponent.setValue(this.getValue());
        dbComponent.focus();
        dbComponent.open();
    }

    private void showDate(Boolean isShow){
        lbComponent.setVisible(!isShow);
        dbComponent.setVisible(isShow);
    }
}
