package org.edec.successful.ctrl.renderer;

import org.edec.successful.model.*;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;

public class MeasureRenderer implements ListitemRenderer<Measure> {

    @Override
    public void render(Listitem li, Measure data, int index) throws Exception {
        li.setDraggable("true");
        li.setDroppable("true");
        li.setValue(data);

        Listcell lc = new Listcell();
        Checkbox cb = new Checkbox(data.getName());
        cb.setChecked(true);
        cb.setParent(lc);
        cb.setClass("measurecb");
        lc.setParent(li);
    }
}