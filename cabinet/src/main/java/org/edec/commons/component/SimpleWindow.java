package org.edec.commons.component;

import org.zkoss.zul.Window;


public class SimpleWindow extends Window {
    public SimpleWindow (String title, Integer heightPercent, Integer widthPercent) {
        super(title, "true", true);
        setHeight(heightPercent + "%");
        setWidth(widthPercent + "%");
    }

    public SimpleWindow (String title) {
        this(title, 40, 50);
    }
}
