package org.edec.chat.ctrl;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Hbox;

import java.util.function.Consumer;

/**
 * Created by lunin on 09.10.2017.
 */
public class WindowFilesCtrl extends SelectorComposer<Component> {

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);

        Runnable runnable = (Runnable) Executions.getCurrent().getArg().get("event");

        Hbox hbox = new Hbox();
        hbox.addEventListener(Events.ON_CLICK, event -> {
            Executions.getCurrent().getSession().setAttribute("some_param", 1);
        });
    }
}
