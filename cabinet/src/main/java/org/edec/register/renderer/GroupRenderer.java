package org.edec.register.renderer;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class GroupRenderer implements ListitemRenderer<String> {
    private Runnable update;

    public GroupRenderer (Runnable update) {
        this.update = update;
    }

    @Override
    public void render (Listitem listitem, String group, int i) throws Exception {
        listitem.setValue(group);
        listitem.appendChild(new Listcell(group));
    }
}
